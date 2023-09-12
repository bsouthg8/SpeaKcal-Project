package com.example.speakcalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;

    // Replace this list with a database or backend service.
    private List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Replace this with actual user authentication logic.
                User user = authenticateUser(email, password);

                if (user != null) {
                    // Login successful
                    // You can store user data in SharedPreferences or perform other actions.
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Login failed
                    Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Replace this with actual authentication logic.
    @SuppressLint("RestrictedApi")
    private User authenticateUser(String email, String password) {
        for (User user : userList) {
            if (user.getUid().equals(email) && user.getClass().equals(password)) {
                return user;
            }
        }
        return null; // Authentication failed
    }

    public void openSignupActivity(View view) {
        Intent intent = new Intent(this, Signup.class);
        startActivity(intent);
    }
}
