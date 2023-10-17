package com.example.speakcalproject;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class WeeklyCheckDailyCaloriesWorker extends Worker {
    private HashMap<String,Object> data = new HashMap<>();
    private double dailyLimitation;
    final CountDownLatch latch = new CountDownLatch(1);
    private HashMap<String,Object> reward = new HashMap<>();
    private Object lock;

    public WeeklyCheckDailyCaloriesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams){
        super(context, workerParams);
    }
    @NonNull
    @Override
    public Result doWork() {
        // Calculate the date range for the past week (previous Monday to current Sunday)
        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_WEEK, -currentDayOfWeek -7 + Calendar.MONDAY); // Set to previous Monday
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_WEEK, 6); // Set to next Monday (current Sunday)
        Date endDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        List<String> dateList = new ArrayList<>();
        calendar.setTime(startDate);
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        int year = calendar.get(Calendar.YEAR);

        while (calendar.getTime().before(endDate) || calendar.getTime().equals(endDate)) {
            dateList.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_WEEK, 1); // Move to the next day
        }

        String[] dateArray = dateList.toArray(new String[0]);

        UserDatabaseManagement.getUserData(getApplicationContext(), new UserDatabaseManagement.OnUserDataCallback() {
            @Override
            public void onUserDataReceived(Map<String, Object> userData) {
                data = (HashMap<String, Object>) userData;
                dailyLimitation = (double) userData.get("calories limitation");

                latch.countDown();
            }
        });

        try {
            latch.await(); // Wait until the latch counts down to 0
        } catch (InterruptedException e) {
            // Handle the interruption
        }


        HashMap<String,Object> foodData = (HashMap<String, Object>) data.get("Food");

        if (foodData != null){

            for (String currentDate : dateArray) {
                try {
                    double calories = UserDatabaseManagement.calculateCaloriesForDate(foodData, currentDate);

                    if (calories > 0 && calories < dailyLimitation) {
                        reward.put(currentDate, calories);
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        //test
        if(!reward.isEmpty()){

            for (Map.Entry<String, Object> entry : reward.entrySet()) {
                UserDatabaseManagement.addRewardToUser(getApplicationContext(), entry.getKey() + " eat less than daily limitation");

                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            UserDatabaseManagement.addRewardToUser(getApplicationContext(), "Week " + weekOfYear + " of year " + year + " " + reward.size() + " days eat less than daily limitation");

            try{
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }

        return Result.success();
    }
}
