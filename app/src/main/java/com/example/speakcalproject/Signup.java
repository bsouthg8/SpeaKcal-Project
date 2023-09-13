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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    private EditText signupUsernameEditText, signupEmailEditText, signupPasswordEditText, reenterPasswordEditText;
    private Button signupButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

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
        firestore = FirebaseFirestore.getInstance();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = signupUsernameEditText.getText().toString();
                String email = signupEmailEditText.getText().toString();
                String password = signupPasswordEditText.getText().toString();
                String reenterPassword = reenterPasswordEditText.getText().toString();

                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || reenterPassword.isEmpty()) {
                    Toast.makeText(Signup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(reenterPassword)) {
                    Toast.makeText(Signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new user with email and password using Firebase Authentication
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Registration successful
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if (user != null) {
                                        // Save user data to Firestore
                                        saveUserDataToFirestore(username, email, user.getUid());

                                        // Redirect to the main activity
                                        Intent intent = new Intent(Signup.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    // Registration failed
                                    Toast.makeText(Signup.this, "Error:" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    private void saveUserDataToFirestore(String username, String email, String userId) {
        // Create a new user document in Firestore
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("email", email);

        DocumentReference userRef = firestore.collection("users").document(userId);
        userRef.set(userMap, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // User data saved to Firestore
                        } else {
                            // Error saving user data
                            Toast.makeText(Signup.this, "Error saving user data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
