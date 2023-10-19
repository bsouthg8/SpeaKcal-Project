package com.example.speakcalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private ImageView imageView;
    private String userName;
    private Map<String, Object> userInfo;
    private CircularProgressBar progressBar;
    private double limitedCalories;
    private double totalCalories;
    private int mCurrentSelectedItemId = R.id.navigation_home; // default item
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        int count = getIntent().getIntExtra("count",0);

        //show achievement
        if(count != 0){
            LayoutInflater inflater = getLayoutInflater();
            View customToastView = inflater.inflate(R.layout.custom_goldmedal_layout,null);
            ImageView medalImageView = customToastView.findViewById(R.id.medal);
            Toast customToast = new Toast(getApplicationContext());
            customToast.setDuration(Toast.LENGTH_SHORT);
            customToast.setView(customToastView);
            customToast.show();
        }

        // Bottom navigation
        setupBottomNav();

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progressBar);

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedCurrentDate = dateFormat.format(currentDate);

        UserDatabaseManagement.getUserData(getApplicationContext(), new UserDatabaseManagement.OnUserDataCallback() {
            @Override
            public void onUserDataReceived(Map<String, Object> userData) {
                userInfo = userData;
                userName = (String) userData.get("username");
                textView.setText("Welcome back\n" + userName);
                limitedCalories = (Double) userData.get("calories limitation");

                try {
                    totalCalories = getCurrentDaysCalories(formattedCurrentDate);
                    updateProgress(getCurrentFocus());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
        },2);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedCurrentDate = dateFormat.format(currentDate);

        UserDatabaseManagement.getUserData(getApplicationContext(), new UserDatabaseManagement.OnUserDataCallback() {
            @Override
            public void onUserDataReceived(Map<String, Object> userData) {
                userInfo = userData;
                userName = (String) userData.get("username");
                textView.setText("Welcome back\n"+userName);

                try {
                    totalCalories = getCurrentDaysCalories(formattedCurrentDate);
                    updateProgress(getCurrentFocus());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
        },1);
    }

    private void setupBottomNav() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_home);
        navView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) return false;

            Intent intent = null;
            if (itemId == R.id.navigation_journal) {
                intent = new Intent(this, Journal_entry.class);
                finishAfterTransition();
            } else if (itemId == R.id.navigation_photo) {
                intent = new Intent(this, PhotoRecognition.class);
                finishAfterTransition();
            } else if (itemId == R.id.navigation_profile) {
                intent = new Intent(this, ProfileActivity.class);
                finishAfterTransition();
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            return true;
        });
    }

    public Double getCurrentDaysCalories(String targetDate) throws ParseException{
        Double totalCalories = 0.0;

        if (userInfo.get("Food") != null) {
            Map<String, Object> userFoodInfo = (Map<String, Object>) userInfo.get("Food");
            totalCalories = UserDatabaseManagement.calculateCaloriesForDate(userFoodInfo,targetDate);
        }

        return totalCalories;
    }

    public void updateProgress(View view){
        float progress = (float) ((totalCalories/limitedCalories)*100);
        progressBar.setProgress(progress,String.valueOf(totalCalories),limitedCalories);
    }
}