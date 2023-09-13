package com.example.speakcalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Map;

public class UserDatabaseExample extends AppCompatActivity {
    private Button add,delete,get,addCalorie;
    private EditText nameInput, passwordInput,foodNameInput,calorieInput;
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

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nameInput.getText().toString().isEmpty() && !passwordInput.getText().toString().isEmpty()) {
                UserDatabaseManagement.addUser(UserDatabaseExample.this,nameInput.getText().toString());
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nameInput.getText().toString().isEmpty()) {
                    UserDatabaseManagement.deleteUser(UserDatabaseExample.this, nameInput.getText().toString());
                }
            }
        });

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nameInput.getText().toString().isEmpty()) {
                    UserDatabaseManagement.getUser(nameInput.getText().toString(), new OnSuccessListener<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> userData) {
                            String userName = (String) userData.get("username");
                            Map<String, Object> food = (Map<String, Object>) userData.get("Food");
                            infoOutput.setText(userName+"\n"+food);
                        }
                    }, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"User "+nameInput.getText().toString()+" is not in the database",Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

        addCalorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nameInput.getText().toString().isEmpty() && !foodNameInput.getText().toString().isEmpty() && !calorieInput.getText().toString().isEmpty()) {
                    UserDatabaseManagement.addCalorieToUser(getApplicationContext(), nameInput.getText().toString(), foodNameInput.getText().toString(), Float.parseFloat(calorieInput.getText().toString()));
                }
            }
        });
    }
}