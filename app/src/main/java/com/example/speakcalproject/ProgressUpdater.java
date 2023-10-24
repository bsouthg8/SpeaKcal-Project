package com.example.speakcalproject;

public class ProgressUpdater {
    public float calculateProgress(double totalCalories, double limitedCalories) {
        return (float) ((totalCalories / limitedCalories) * 100);
    }

    public float calculateWeeklyProgress(double totalCalories, double limitedCalories) {
        Double pastDayLimit = limitedCalories * 7;
        double totalPercentage1 = ((pastDayLimit - totalCalories) / pastDayLimit) * 100;
        return (float) ((totalCalories/pastDayLimit) * 100);
    }
}
