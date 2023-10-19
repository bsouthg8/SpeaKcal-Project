package com.example.speakcalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.speakcalproject.UserDatabaseManagement.OnUserDataCallback;

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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SplashScreenActivity extends AppCompatActivity {
    HashMap<String,Object> reward = new HashMap<>();
    private double limitedCalories;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        UserDatabaseManagement.getUserData(getApplicationContext(), new UserDatabaseManagement.OnUserDataCallback() {
            @Override
            public void onUserDataReceived(Map<String, Object> userData) {
                MyApplication myApp = (MyApplication) getApplication();
                myApp.setGlobalData((HashMap<String, Object>) userData);

                if(userData.get("calories limitation") != null){
                    limitedCalories = (Double) userData.get("calories limitation");
                } else {
                    limitedCalories = 2500;
                    UserDatabaseManagement.updateLimitation(getApplicationContext(),limitedCalories,1);
                }


            }
        },1);



        new Handler().postDelayed(() -> {
            MyApplication myApp = (MyApplication) getApplication();
            HashMap<String, Object> userInfo = myApp.getGlobalData();
            Calendar calendar = Calendar.getInstance();
            int lastWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR) - 1;
            int year = calendar.get(Calendar.YEAR);

            if(userInfo.get("weekly reward") != null){
                HashMap<String, Boolean> rewardStatus = (HashMap<String, Boolean>) userInfo.get("weekly reward");
                if(!rewardStatus.containsKey(lastWeekOfYear+" "+year)){
                    try {
                        reward = checkAndSetRewardForLastWeek();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
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
                for (Map.Entry<String, Object> entry : reward.entrySet()) {
                    String date = entry.getKey();
                    String rewardText = date + " eats less than limitation";
                    taskQueue.add(rewardText);
                    ++count;
                }
                String summaryText = count + " days eat less than limitation at week " + lastWeekOfYear + " of year " + year;
                executeDatabaseUpdateTasksSequentially(taskQueue,summaryText);

            }

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("count",count);
            startActivity(intent);
            finish();

        },3000);

    }

    public HashMap<String,Object> checkAndSetRewardForLastWeek() throws ParseException {
        MyApplication myApp = (MyApplication) getApplication();
        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_WEEK, -currentDayOfWeek -7 + Calendar.MONDAY); // Set to previous Monday
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

    private void executeDatabaseUpdateTasksSequentially(Queue<String> taskQueue,String input) {
        if (!taskQueue.isEmpty()) {
            String rewardText = taskQueue.poll();

            DatabaseUpdateReward task = new DatabaseUpdateReward(new DatabaseUpdateReward.OnPostExecuteListener() {
                @Override
                public void onPostExecute() {
                    // Task has completed, execute the next task
                    executeDatabaseUpdateTasksSequentially(taskQueue,input);
                }
            });
            task.execute(rewardText);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            // All tasks are done, you can execute the final task if needed
            executeFinalTask(input);
        }
    }

    private void executeFinalTask(String input) {
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