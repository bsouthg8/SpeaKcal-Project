package com.example.speakcalproject;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Map;

public class UserDatabaseExample extends AppCompatActivity {
    private Button add,delete,get,addCalorie,addReward;
    private EditText nameInput, passwordInput,foodNameInput,calorieInput,rewardInput;
    private TextView infoOutput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_database_example);
        add = findViewById(R.id.addUser);
        delete = findViewById(R.id.deleteUser);
        get = findViewById(R.id.getUser);
        addCalorie = findViewById(R.id.addCalorieToDatabase);
        nameInput = findViewById(R.id.editUserName);
        passwordInput = findViewById(R.id.editUserPassword);
        foodNameInput = findViewById(R.id.editFoodName);
        calorieInput = findViewById(R.id.editFoodCalorie);
        infoOutput = findViewById(R.id.userInfoTextView);
        addReward = findViewById(R.id.addRewardToDatabase);
        rewardInput = findViewById(R.id.editReward);
        String mealTypeTest = "Breakfast";

        addReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reward = rewardInput.getText().toString();
                UserDatabaseManagement.addRewardToUser(UserDatabaseExample.this,reward,1);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    UserDatabaseManagement.getUserData(getApplicationContext(), new UserDatabaseManagement.OnUserDataCallback() {
                        @Override
                        public void onUserDataReceived(Map<String, Object> userData) {
                            StringBuilder userDataText = new StringBuilder();
                            for(Map.Entry<String, Object> entry: userData.entrySet() ){
                                String key = entry.getKey();
                                Object value = entry.getValue();

                                userDataText.append(key).append(": ").append(value).append("\n");
                            }

                            String userDataString = userDataText.toString();
                            infoOutput.setText(userDataString);
                        }
                    },1);

            }
        });

        addCalorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nameInput.getText().toString().isEmpty() && !foodNameInput.getText().toString().isEmpty() && !calorieInput.getText().toString().isEmpty()) {
                    UserDatabaseManagement.addCalorieToUser(getApplicationContext(), foodNameInput.getText().toString(), Float.parseFloat(calorieInput.getText().toString()), mealTypeTest);
                }
            }
        });
    }
}