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

    private int mCurrentSelectedItemId = R.id.navigation_home; // default item

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(mCurrentSelectedItemId);
        navView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Check if the item is already selected
            if (itemId == mCurrentSelectedItemId) {
                return false;
            }

            if (itemId == R.id.navigation_home) {
                return false;  // Stay on the same screen
            } else if (itemId == R.id.navigation_journal) {
                Intent intent = new Intent(this, TestJournal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.navigation_photo) {
                Intent intent = new Intent(this, PhotoRecognition.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Intent intent = new Intent(this, TestProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            return false;
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