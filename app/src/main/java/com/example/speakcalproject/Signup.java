package com.example.speakcalproject;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.AuthResult;
import android.text.TextUtils;

public class Signup extends AppCompatActivity {

    private EditText signupUsernameEditText, signupEmailEditText, signupPasswordEditText, reenterPasswordEditText;
    private Button signupButton;
    private FirebaseAuth firebaseAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupUsernameEditText = findViewById(R.id.signupUsernameEditText);
        signupEmailEditText = findViewById(R.id.signupEmailEditText);
        signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        reenterPasswordEditText = findViewById(R.id.reenterPasswordEditText);
        signupButton = findViewById(R.id.signupButton);

        firebaseAuth = FirebaseAuth.getInstance();

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

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Signup successful
                                    // You can redirect to the login screen or perform other actions
                                } else {
                                    // Signup failed
                                    Toast.makeText(Signup.this, "Signup failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}

