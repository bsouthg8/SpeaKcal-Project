package com.example.speakcalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.speakcalproject.ui.dashboard.DashboardFragment;
import com.example.speakcalproject.ui.home.HomeFragment;
import com.example.speakcalproject.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.speakcalproject.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Button goToPhotoRecognition;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Set up navigation for bottom navigation view
        navView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                // HomeFragment should be an Activity if you're using startActivity
                startActivity(new Intent(this, MainActivity.class));
                return true;  // Return true to indicate you've handled this item click
            } else if (itemId == R.id.navigation_journal) {
                startActivity(new Intent(this, DashboardFragment.class));
                return true;
            } else if (itemId == R.id.navigation_photo) {
                startActivity(new Intent(this, PhotoRecognition.class));
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(this, NotificationsFragment.class));
                return true;
            }
            return false; // Return false to indicate you haven't handled this item click
        });

        FirebaseApp.initializeApp(this);


        // The following code is test code to see if adding data to the database is working properly
        /* firestore = FirebaseFirestore.getInstance();

        Map<String, Object> users = new HashMap<>();
        users.put("firstName", "Bradley");
        users.put("lastName", "Southgate");
        users.put("description", "Hello");

        firestore.collection("users").add(users).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_LONG).show();
            }
        }); // database test ends here */

    }

}