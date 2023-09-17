package com.example.speakcalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    private EditText signupUsernameEditText, signupPasswordEditText, reenterPasswordEditText;
    private Button signupButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupUsernameEditText = findViewById(R.id.signupUsernameEditText);
        signupPasswordEditText = findViewById(R.id.signupPasswordEditText);
        reenterPasswordEditText = findViewById(R.id.reenterPasswordEditText);
        signupButton = findViewById(R.id.signupButton);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

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

                // Use the username as an email (add a domain like example.com)
                String email = username + "@example.com";
                registerUser(email, password, username);
            }
        });
    }

    private void registerUser(String email, String password, String username) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveUserDataToFirestore(username);
                        } else {
                            Toast.makeText(Signup.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void saveUserDataToFirestore(String username) {
        String userId = firebaseAuth.getCurrentUser().getUid();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", username);

        firestore.collection("user").document(userId).set(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Redirect to the login activity
                            Intent intent = new Intent(Signup.this, Login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Signup.this, "Error saving user data: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

