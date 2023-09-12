package com.example.speakcalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class Signup extends AppCompatActivity {

    private EditText signupUsernameEditText, signupEmailEditText, signupPasswordEditText, reenterPasswordEditText;
    private Button signupButton;
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupUsernameEditText = findViewById(R.id.signupUsernameEditText);
        signupEmailEditText = findViewById(R.id.signupEmailEditText);
        signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        reenterPasswordEditText = findViewById(R.id.reenterPasswordEditText);
        signupButton = findViewById(R.id.signupButton);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = signupUsernameEditText.getText().toString();
                String email = signupEmailEditText.getText().toString();
                String password = signupPasswordEditText.getText().toString();
                String reenterPassword = reenterPasswordEditText.getText().toString();

                // Perform validation for username, email, and passwords
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(reenterPassword)) {
                    Toast.makeText(Signup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(reenterPassword)) {
                    Toast.makeText(Signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new user and add them to the user list (in-memory storage)
                @SuppressLint("RestrictedApi") User newUser = new User(username);
                userList.add(newUser);

                // Optionally, you can store the user data in a database or send it to a backend server
                // for actual user registration and data storage.

                // Notify the user that registration was successful
                Toast.makeText(Signup.this, "Registration successful", Toast.LENGTH_SHORT).show();

                // You can redirect to the login screen or perform other actions
                // For example, redirect to LoginActivity
                Intent intent = new Intent(Signup.this, Login.class);
                startActivity(intent);
            }
        });
    }
}
