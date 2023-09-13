package com.example.speakcalproject;

public class FoodData {
    private String amount;
    private String name;

    public FoodData(){
        this.amount = "";
        this.name = "";
    }

    public FoodData( String amount, String name){
        this.amount = amount;
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
