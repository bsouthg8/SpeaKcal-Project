package com.example.speakcalproject;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

public class SplashScreenActivity extends AppCompatActivity {
    HashMap<String,Object> reward = new HashMap<>();
    private double limitedCalories;
    HashMap<String, Object> userInfo = new HashMap<>();
    private CountDownLatch userInfoLatch = new CountDownLatch(1);
    Calendar calendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        HandlerThread handlerThread = new HandlerThread("UserInfoThread", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler userInfoHandler = new Handler(looper);

        userInfoHandler.post(() -> {
        UserDatabaseManagement.getUserData(getApplicationContext(), new UserDatabaseManagement.OnUserDataCallback() {
            @Override
            public void onUserDataReceived(Map<String, Object> userData) {
                MyApplication myApp = (MyApplication) getApplication();
                myApp.setGlobalData((HashMap<String, Object>) userData);
                userInfo = (HashMap<String, Object>) userData;

                if(userData.get("calories limitation") != null){
                    limitedCalories = (Double) userData.get("calories limitation");
                } else {
                    limitedCalories = 2500;
                    UserDatabaseManagement.updateLimitation(getApplicationContext(),limitedCalories,1);
                }

                userInfoLatch.countDown();
            }
        },1);
        });

        new Handler().postDelayed(() -> {
            try {
                userInfoLatch.await(); // Wait for userInfo to become available
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int subtract = 0;
            if(calendar.DAY_OF_WEEK == 7){
                subtract = 2;
            } else {
                subtract = 1;
            }

            int lastWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR) - subtract;
            int year = calendar.get(Calendar.YEAR);

            if(lastWeekOfYear == 0 || lastWeekOfYear == -1){
                calendar.add(Calendar.YEAR, -1);
                year = calendar.get(Calendar.YEAR);

                // Set the week to the last week of the previous year
                calendar.set(Calendar.WEEK_OF_YEAR, calendar.getActualMaximum(Calendar.WEEK_OF_YEAR));
                lastWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

            }

            if(userInfo.get("weekly reward") != null){
                HashMap<String, Boolean> rewardStatus = (HashMap<String, Boolean>) userInfo.get("weekly reward");
                if(!rewardStatus.containsKey(lastWeekOfYear+" "+year)){
                    try {
                        reward = checkAndSetRewardForLastWeek();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    UserDatabaseManagement.addWeeklyRewardStatus(getApplicationContext(), String.valueOf(lastWeekOfYear));
                }
            } else {
                UserDatabaseManagement.addWeeklyRewardStatus(getApplicationContext(), String.valueOf(lastWeekOfYear));
                try {
                    reward = checkAndSetRewardForLastWeek();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            Queue<String> taskQueue = new LinkedList<>();
            int count = 0;

            if(!reward.isEmpty()){
                count = reward.size();
                String summaryText = count + " days eat less than limitation at week " + lastWeekOfYear + " of year " + year;
                executeTask(summaryText);
            }


            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("count",count);
            startActivity(intent);
            finish();

        },1000);
    }

    public HashMap<String,Object> checkAndSetRewardForLastWeek() throws ParseException {
        MyApplication myApp = (MyApplication) getApplication();
        int currentDayOfWeek = Calendar.DAY_OF_WEEK;
        calendar.add(Calendar.DAY_OF_WEEK,  - 7 - (currentDayOfWeek - 1)); // Set to previous Monday


        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_WEEK, 6); // Set to next Monday (current Sunday)
        Date endDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        List<String> dateList = new ArrayList<>();
        calendar.setTime(startDate);

        while (calendar.getTime().before(endDate) || calendar.getTime().equals(endDate)) {
            dateList.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_WEEK, 1); // Move to the next day
        }

        String[] dateArray = dateList.toArray(new String[0]);

        HashMap<String, Object> reward = new HashMap<>();

        if(myApp.getGlobalData() != null){
            for(String targetDate : dateArray){
                double targetDateCalories = getCurrentDaysCalories(targetDate);
                if (targetDateCalories > 0 && targetDateCalories < limitedCalories) {
                    reward.put(targetDate, targetDateCalories);
                }
            }
        }

        return reward;
    }

    public Double getCurrentDaysCalories(String targetDate) throws ParseException{
        MyApplication myApp = (MyApplication) getApplication();
        Double totalCalories = 0.0;

        if (myApp.getGlobalData().get("Food") != null) {
            Map<String, Object> userFoodInfo = (Map<String, Object>) myApp.getGlobalData().get("Food");
            totalCalories = UserDatabaseManagement.calculateCaloriesForDate(userFoodInfo,targetDate);
        }

        return totalCalories;
    }


    private void executeTask(String input) {
        String summaryText = input; // Replace with your summary text
        DatabaseUpdateReward summaryTask = new DatabaseUpdateReward(new DatabaseUpdateReward.OnPostExecuteListener() {
            @Override
            public void onPostExecute() {
                // Handle the completion of the final task
            }
        });
        summaryTask.execute(summaryText);
    }



}