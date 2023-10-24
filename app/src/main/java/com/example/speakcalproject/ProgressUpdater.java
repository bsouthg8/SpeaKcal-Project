package com.example.speakcalproject;

public class ProgressUpdater {
    public float calculateProgress(double totalCalories, double limitedCalories) {
        return (float) ((totalCalories / limitedCalories) * 100);
    }
}
