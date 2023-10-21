package com.example.speakcalproject;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SplashScreenActivityTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getCurrentDaysCalories() {
        Calendar calendar = Calendar.getInstance();

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

        }



    }
}