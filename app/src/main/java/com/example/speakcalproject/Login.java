package com.example.speakcalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button login_button;

    // Replace this list with a database or backend service.
    private List<User> userList = new ArrayList<>();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        login_button = findViewById(R.id.login_button);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();

                            if(user != null) {
                                String uid = user.getUid();

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference userRef = db.collection("User").document(uid);

                                userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()) {
                                            String storedUsername = documentSnapshot.getString("Username");
                                            String storedPasswordHash = documentSnapshot.getString("Password");
                                            if(email.equals(storedUsername) && password.equals(storedPasswordHash)) {
                                                Intent intent = new Intent(Login.this,PhotoRecognition.class);
                                            } else {
                                                Toast.makeText(Login.this,"Authentication failed.", Toast.LENGTH_SHORT).show();
                                            }

                                        } else {

                                        }
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(Login.this,"Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
