package com.example.speakcalproject;

public class FoodInfo {
    private String foodName;
    private float foodWeight;
    private float calories;

    public FoodInfo(){
        this.foodName = "";
        this.foodWeight = 0;
        this.calories = 0;
    }

    public FoodInfo(String foodName, float foodWeight, float calories) {
        this.calories = calories;
        this.foodName = foodName;
        this.foodWeight = foodWeight;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public float getFoodWeight() {
        return foodWeight;
    }

    public void setFoodWeight(float foodWeight) {
        this.foodWeight = foodWeight;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }
}

