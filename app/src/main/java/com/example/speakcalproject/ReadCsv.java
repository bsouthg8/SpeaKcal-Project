package com.example.speakcalproject;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//read food data from assets file
//foodID is food ID
//foodAmount is amount of Kcal per 100g
//foodName is food name
public class ReadCsv {

    private List<FoodData> fds;

    public List<FoodData> returnList(){
        return this.fds;
    }

    public ReadCsv() {
        this.fds = new ArrayList<>();
    }

    public void readCSV(Context context, String fileName){
        fds.clear();

        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String info;

            while((info = br.readLine()) != null)
            {
                String[] data = info.split(",");
                String foodName = "";
                for(int i = 1; i < data.length; i++)
                {
                    foodName += data[i];
                }
                String newFoodName = foodName.replace("\"","");
                FoodData fd = new FoodData();
                fd.setAmount(data[0]);
                fd.setName(newFoodName.toLowerCase(Locale.ROOT));
                fds.add(fd);
            }

            br.close();
            is.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }





}
