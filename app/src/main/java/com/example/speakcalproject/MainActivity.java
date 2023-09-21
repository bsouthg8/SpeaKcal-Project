package com.example.speakcalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progressBar);
        //waiting for further modification
        limitedCalories = 2500;

        UserDatabaseManagement.getUserData(getApplicationContext(), new UserDatabaseManagement.OnUserDataCallback() {
            @Override
            public void onUserDataReceived(Map<String, Object> userData) {
                userInfo = userData;
                userName = (String) userData.get("username");
                textView.setText("Welcome back\n"+userName);

                try {
                    totalCalories = getCurrentDaysCalories();
                    updateProgress(getCurrentFocus());




                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
        });




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

    public void updateProgress(View view) {
        int progress =(int) ((totalCalories / limitedCalories) * 100);
        progressBar.setProgress(progress, String.valueOf(totalCalories));
    }



}