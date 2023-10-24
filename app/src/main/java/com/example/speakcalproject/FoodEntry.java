package com.example.speakcalproject;

public class FoodEntry {
    private String foodName;
    private float calories;
    private String dateTime;
    private String mealType;

    // Constructor, getters, and setters
    public FoodEntry(String foodName, float calories, String dateTime, String mealType) {
        this.foodName = foodName;
        this.calories = calories;
        this.dateTime = dateTime;
        this.mealType = mealType;
    }

    public FoodEntry(){
        foodName = "";
        calories = 0;
        dateTime = "";
        mealType = "";
    }

    public FoodEntry(String foodName, float calories, String dateTime){
        this.foodName = foodName;
        this.calories = calories;
        this.dateTime = dateTime;
        mealType = "";
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
}
