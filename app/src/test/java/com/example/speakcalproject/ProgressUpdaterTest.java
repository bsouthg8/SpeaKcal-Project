package com.example.speakcalproject;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

// Unit test for ProgressUpdater class
@RunWith(MockitoJUnitRunner.class)
public class ProgressUpdaterTest {
    @InjectMocks
    private ProgressUpdater progressUpdater;

    @Test
    public void testCalculateProgress() {
        double totalCalories = 500.0;
        double limitedCalories = 2000.0;

        float progress = progressUpdater.calculateProgress(totalCalories, limitedCalories);

        assertEquals(25.0f, progress, 0.01); // 25% progress
    }

    @Test
    public void testWeeklyCalculateProgress() {
        double totalCalories = 7000.0;
        double limitedCalories = 2000.0;

        float progress = progressUpdater.calculateWeeklyProgress(totalCalories, limitedCalories);

        assertEquals(50.0f, progress, 0.01); // 25% progress
    }

//    @Test
//    public void testCalculateProgressFailure() {
//        double totalCalories = 500.0;
//        double limitedCalories = 2000.0;
//
//        float progress = progressUpdater.calculateProgress(totalCalories, limitedCalories);
//
//        assertEquals(75.0f, progress, 0.01); // 25% progress
//    }
}