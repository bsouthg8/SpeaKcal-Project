package com.example.speakcalproject;

import android.app.Application;

import java.util.HashMap;

public class MyApplication extends Application {
    private HashMap<String, Object> globalData;

    public HashMap<String, Object> getGlobalData(){
        return globalData;
    }

    public void setGlobalData(HashMap<String, Object> data){
        globalData = data;
    }


}
