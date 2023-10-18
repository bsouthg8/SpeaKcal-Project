package com.example.speakcalproject;

import android.app.Application;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        PeriodicWorkRequest weeklyWorkRequest = new PeriodicWorkRequest.Builder(
                WeeklyCheckDailyCaloriesWorker.class,
                7, // Repeat interval in days (1 week)
                TimeUnit.DAYS
        ).build();

        WorkManager.getInstance(this).enqueue(weeklyWorkRequest);
    }
}
