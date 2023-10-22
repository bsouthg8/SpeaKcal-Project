package com.example.speakcalproject;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateSetTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getLastWeekDateZero() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        date.setYear(2023 - 1900); // Years in Date are 1900-based
        date.setMonth(0); // Month is zero-based (0 for January)
        date.setDate(1);
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        // Check if it's the first week of the year
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

        if(day == 1){
            --weekOfYear;
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        }

        if (weekOfYear == 1 || weekOfYear == 0) {
            calendar.add(Calendar.YEAR, -1);
            // Set the week to the last week of the previous year
            calendar.set(Calendar.WEEK_OF_YEAR, calendar.getActualMaximum(Calendar.WEEK_OF_YEAR));
            weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

        } else {
            // If it's not the first week, simply subtract one week
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        }

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
        String[] actual = new String[]{"2022-12-19","2022-12-20","2022-12-21","2022-12-22","2022-12-23","2022-12-24","2022-12-25"};


        assertEquals(dateArray,actual);



    }

    @Test
    public void getLastWeekDateOne() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        if(day == 1){
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        }

        calendar.add(Calendar.WEEK_OF_YEAR, -1);

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
        String[] actual = new String[]{"2023-10-09","2023-10-10","2023-10-11","2023-10-12","2023-10-13","2023-10-14","2023-10-15"};


        assertEquals(dateArray,actual);



    }
    @Test
    public void getLastWeekDateTwo() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        date.setYear(2023 - 1900); // Years in Date are 1900-based
        date.setMonth(0); // Month is zero-based (0 for January)
        date.setDate(1);
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        if(day == 1){
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        }

        calendar.add(Calendar.WEEK_OF_YEAR, -1);

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
        String[] actual = new String[]{"2022-12-19","2022-12-20","2022-12-21","2022-12-22","2022-12-23","2022-12-24","2022-12-25"};


        assertEquals(dateArray,actual);



    }

    @Test
    public void getLastWeekDateThree() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        date.setYear(2023 - 1900); // Years in Date are 1900-based
        date.setMonth(0); // Month is zero-based (0 for January)
        date.setDate(2);
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        if(day == 1){
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        }

        calendar.add(Calendar.WEEK_OF_YEAR, -1);

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
        String[] actual = new String[]{"2022-12-26","2022-12-27","2022-12-28","2022-12-29","2022-12-30","2022-12-31","2023-01-01"};


        assertEquals(dateArray,actual);
    }

    @Test
    public void getLastWeekDateFive() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        date.setYear(2023 - 1900); // Years in Date are 1900-based
        date.setMonth(9); // Month is zero-based (0 for January)
        date.setDate(23);
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        if(day == 1){
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        }

        calendar.add(Calendar.WEEK_OF_YEAR, -1);

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
        String[] actual = new String[]{"2023-10-16","2023-10-17","2023-10-18","2023-10-19","2023-10-20","2023-10-21","2023-10-22"};


        assertEquals(dateArray,actual);
    }

    @Test
    public void getLastWeekDateSix() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        date.setYear(2024 - 1900); // Years in Date are 1900-based
        date.setMonth(0); // Month is zero-based (0 for January)
        date.setDate(1);
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        if(day == 1){
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        }

        calendar.add(Calendar.WEEK_OF_YEAR, -1);

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
        String[] actual = new String[]{"2023-12-25","2023-12-26","2023-12-27","2023-12-28","2023-12-29","2023-12-30","2023-12-31"};


        assertEquals(dateArray,actual);
    }

    @Test
    public void getLastWeekDateSeven() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        date.setYear(2023 - 1900); // Years in Date are 1900-based
        date.setMonth(9); // Month is zero-based (0 for January)
        date.setDate(25);
        calendar.setTime(date);

        int subtract = 0;
        if(calendar.get(Calendar.DAY_OF_WEEK) == 1){
            subtract = 2;
        } else {
            subtract = 1;
        }
        calendar.add(Calendar.WEEK_OF_YEAR, -subtract);

        int lastWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR) ;
        int year = calendar.get(Calendar.YEAR);


    }


}