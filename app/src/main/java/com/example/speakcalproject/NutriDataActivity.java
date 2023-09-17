package com.example.speakcalproject;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NutriDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nutri_data);
        final TextView name = findViewById(R.id.textviewwww);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("User");
        String userId = FirebaseAuth.getInstance().getUid();

        mDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Users user = dataSnapshot.getValue(Users.class);
                name.setText(user.getName());
                name.setText(user.getEmail());
                //      Log.d(TAG, "User name: " + user.getName() + ", email " + user.getEmail());
                Toast.makeText(NutriDataActivity.this, "Connection Successful " ,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // Log.w(TAG, "Failed to read value.", error.toException());
                Toast.makeText(NutriDataActivity.this, "Failed to read value." ,Toast.LENGTH_SHORT).show();

            }
        });
    }
}