package com.example.speakcalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TestJournal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_journal);

        // Bottom navigation
        setupBottomNav();

    }

    private void setupBottomNav() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_journal);
        navView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_journal) return false;

            Intent intent = null;
            int enterAnim = R.anim.slide_in_right;
            int exitAnim = R.anim.slide_out_left;
            if (itemId == R.id.navigation_home) {
                intent = new Intent(this, MainActivity.class);
                enterAnim = R.anim.slide_in_left;
                exitAnim = R.anim.slide_out_right;
            } else if (itemId == R.id.navigation_photo) {
                intent = new Intent(this, PhotoRecognition.class);
            } else if (itemId == R.id.navigation_profile) {
                intent = new Intent(this, ProfileActivity.class);
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(enterAnim, exitAnim);
            }
            return true;
        });
    }
}