package com.example.speakcalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SharedMemory;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button login_button;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.emailEditText); // Change to usernameEditText
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
                firebaseAuth.signInWithEmailAndPassword(username+"@example.com", password)
                        .addOnCompleteListener(Login.this, task -> {
                            if (task.isSuccessful()) {
                                // Login successful
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (user != null) {
                                    // Redirect to the main activity
                                    String uid = user.getUid();

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentReference userRef = db.collection("users").document(uid);

                                    userRef.get().addOnSuccessListener(documentSnapshot -> {

                                        if (documentSnapshot.exists()) {
                                            String userName = documentSnapshot.getString("username");
                                            SharedPreferences preferences = getSharedPreferences("userName",MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.putString("userName",userName);
                                            editor.apply();
                                        }
                                    }).addOnFailureListener(e -> {

                                    });

                                    Intent intent = new Intent(Login.this, PhotoRecognition.class);
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
}
