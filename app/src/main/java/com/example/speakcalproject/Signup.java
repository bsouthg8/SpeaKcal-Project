package com.example.speakcalproject;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    private EditText signupUsernameEditText, signupPasswordEditText, reenterPasswordEditText;
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

        mAuth= FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = signupUsernameEditText.getText().toString();
                String password = signupPasswordEditText.getText().toString();
                String reenterPassword = reenterPasswordEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty() || reenterPassword.isEmpty()) {
                    Toast.makeText(Signup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(reenterPassword)) {
                    Toast.makeText(Signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                registerUser(username, password);
            }
        });
    }

    private void registerUser(String username, String password) {
        mAuth.createUserWithEmailAndPassword(username+"@example.com",password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()) {
                String userId = mAuth.getCurrentUser().getUid();
                Map<String, Object> user = new HashMap<>();
                user.put("username",username);

                db.collection("users").document(userId).set(user).addOnSuccessListener(aVoid -> {
                    Toast.makeText(Signup.this,"User: "+username+" registered successfully",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Signup.this, Login.class);
                    startActivity(intent);
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(Signup.this,"Database error",Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(Signup.this,"Registration failed or User already exist",Toast.LENGTH_SHORT).show();
            }
        });
    }


}

