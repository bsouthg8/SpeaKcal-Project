package com.example.speakcalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.speakcalproject.databinding.ActivityMainBinding;
import com.google.android.material.slider.LabelFormatter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private ImageView imageView;
    private String userName;
    private Map<String, Object> userInfo;
    private PieChart pieChart;
    private PieChart pieChart1;
    private double limitedCalories;


    private int mCurrentSelectedItemId = R.id.navigation_home; // default item

    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        // Bottom navigation
        setupBottomNav();

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        pieChart = findViewById(R.id.pieChart1);
        pieChart1 = findViewById(R.id.pieChart2);

        //waiting for further modification
        limitedCalories = 2500;

        UserDatabaseManagement.getUserData(getApplicationContext(), new UserDatabaseManagement.OnUserDataCallback() {
            @Override
            public void onUserDataReceived(Map<String, Object> userData) {
                userInfo = userData;
                userName = (String) userData.get("username");
                textView.setText("Welcome back\n"+userName);

                try {
                    Double totalCalories = getCurrentDaysCalories();
                    List<PieEntry> entries = new ArrayList<>();
                    if(totalCalories >= limitedCalories){
                        totalCalories = limitedCalories;
                    }

                    double totalPercentage = ((limitedCalories - totalCalories) / limitedCalories) * 100;
                    entries.add(new PieEntry(new Float(totalCalories/limitedCalories) * 100,"Taken"));
                    //just testing now
                    entries.add(new PieEntry(new Float(totalPercentage),"Left"));

                    PieDataSet dataSet = new PieDataSet(entries,"Total Calories");
                    dataSet.setValueTextSize(20f);
                    ArrayList<Integer> colors = new ArrayList<>();
                    colors.add(getResources().getColor(R.color.colorYellow));
                    colors.add(getResources().getColor(R.color.colorGrey));
                    dataSet.setColors(colors);

                    PieData data = new PieData(dataSet);
                    pieChart.setData(data);

                    pieChart.setDrawHoleEnabled(true);
                    pieChart.setHoleColor(android.R.color.transparent);
                    pieChart.setDrawEntryLabels(false);
                    pieChart.getDescription().setEnabled(false);

                    Calendar calendar = Calendar.getInstance();
                    Date currentDate = calendar.getTime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedCurrentDate = dateFormat.format(currentDate);
                    String centerText = "Today \n %";
                    pieChart.setCenterText(centerText);
                    pieChart.setCenterTextSize(20f);
                    pieChart.setCenterTextColor(R.color.black);

                    pieChart.invalidate();

                    //Past 7 days Calculation
                    Double pastTotalCalories = getTotalCaloriesForLast7Days();
                    List<PieEntry> pastEntries = new ArrayList<>();
                    Double pastDayLimit = limitedCalories * 7;

                    if(pastTotalCalories >= pastDayLimit){
                        pastTotalCalories = pastDayLimit;
                    }
                    double totalPercentage1 = ((pastDayLimit - pastTotalCalories) / pastDayLimit) * 100;
                    pastEntries.add(new PieEntry(new Float(pastTotalCalories/pastDayLimit) * 100,"Taken"));
                    //just testing now
                    pastEntries.add(new PieEntry(new Float(totalPercentage1) ,"Left"));

                    PieDataSet pastDataSet = new PieDataSet(pastEntries,"Total Calories");
                    pastDataSet.setValueTextSize(20f);
                    ArrayList<Integer> pastColors = new ArrayList<>();
                    pastColors.add(getResources().getColor(R.color.colorAccent));
                    pastColors.add(getResources().getColor(R.color.colorGrey));
                    pastDataSet.setColors(pastColors);

                    PieData pastData = new PieData(pastDataSet);
                    pieChart1.setData(pastData);

                    pieChart1.setDrawHoleEnabled(true);
                    pieChart1.setHoleColor(android.R.color.transparent);
                    pieChart1.setDrawEntryLabels(false);
                    pieChart1.getDescription().setEnabled(false);


                    String pastCenterText = "Past \n 7 \n days %";
                    pieChart1.setCenterText(pastCenterText);
                    pieChart1.setCenterTextSize(20f);
                    pieChart1.setCenterTextColor(R.color.black);

                    pieChart1.invalidate();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
        });

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

    public Double getCurrentDaysCalories() throws ParseException{
        Double totalCalories = 0.0;
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedCurrentDate = dateFormat.format(currentDate);


        if (userInfo.get("Food") != null) {
            Map<String, Object> userFoodInfo = (Map<String, Object>) userInfo.get("Food");
            totalCalories = UserDatabaseManagement.calculateCaloriesForDate(userFoodInfo,formattedCurrentDate);
        }

        return totalCalories;
    }
    public Double getTotalCaloriesForLast7Days() throws ParseException {
        Double totalCalories = 0.0;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            Date currentDate = calendar.getTime();
            String formattedCurrentDate = dateFormat.format(currentDate);

            if (userInfo.get("Food") != null) {
                Map<String, Object> userFoodInfo = (Map<String, Object>) userInfo.get("Food");
                totalCalories += UserDatabaseManagement.calculateCaloriesForDate(userFoodInfo, formattedCurrentDate);
            }

            // Move the calendar back by one day
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }

        return totalCalories;
    }

}