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

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Signup extends AppCompatActivity {

    private EditText signupUsernameEditText, signupEmailEditText, signupPasswordEditText, reenterPasswordEditText;
    private Button signupButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupUsernameEditText = findViewById(R.id.signupUsernameEditText);
        signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        reenterPasswordEditText = findViewById(R.id.reenterPasswordEditText);
        signupButton = findViewById(R.id.signupButton);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = signupUsernameEditText.getText().toString();
                String password = signupPasswordEditText.getText().toString();
                String reenterPassword = reenterPasswordEditText.getText().toString();

                // Perform validation for username, email, and passwords
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(reenterPassword)) {
                    Toast.makeText(Signup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(reenterPassword)) {
                    Toast.makeText(Signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                registerUser(username,password);

            }
        });
    }

    public void registerUser(String userName, String passWord){
        mAuth.createUserWithEmailAndPassword(userName+"@example.com",passWord).addOnCompleteListener(this,task -> {
            if(task.isSuccessful()) {
                String userID = mAuth.getCurrentUser().getUid();
                HashMap<String, Object> userData = new HashMap<>();
                userData.put("username",userName);

                db.collection("users").document(userID).set(userData).addOnSuccessListener(aVoid -> {
                    Toast.makeText(Signup.this,"Registered successfully",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Signup.this,Login.class);
                    startActivity(intent);
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(Signup.this,"Database error",Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(Signup.this,"Registration failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
