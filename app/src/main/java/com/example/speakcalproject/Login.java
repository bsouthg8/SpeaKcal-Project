package com.example.speakcalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;

public class Login extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button login_button;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);

        usernameEditText = findViewById(R.id.usernameEditText); // Change to usernameEditText
        passwordEditText = findViewById(R.id.passwordEditText);
        login_button = findViewById(R.id.login_button);

        firebaseAuth = FirebaseAuth.getInstance();

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString(); // Get username
                String password = passwordEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Use Firebase Authentication to sign in the user
                // Replace "username" with the field you use to store usernames in Firebase
                firebaseAuth.signInWithEmailAndPassword(username + "@example.com", password)
                        .addOnCompleteListener(Login.this, task -> {
                            if (task.isSuccessful()) {
                                // Login successful
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (user != null) {
                                    // Redirect to the main activity
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                // Login failed
                                Toast.makeText(Login.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    public void openSignupActivity(View view) {
        Intent intent = new Intent(this, Signup.class);
        startActivity(intent);
    }


    //When MainActivity finished, change intent to MainActivity
    public void loginUser(String userName, String passWord){
        mAuth.signInWithEmailAndPassword(userName+"@example.com",passWord).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user =mAuth.getCurrentUser();

                    Intent intent = new Intent(Login.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(Login.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}
