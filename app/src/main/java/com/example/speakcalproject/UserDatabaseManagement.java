package com.example.speakcalproject;

import android.content.Context;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UserDatabaseManagement {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final CollectionReference userCollection = db.collection("users");


    //add user database "User"
    //User structure:
    //username
    //Food -> Map<Date+Time, FoodName+Calories>>
    //reward

    //done
    public static void addCalorieToUser(Context context, String foodName, float calories) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            String userID = currentUser.getUid();

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            String dateTime = String.format("%04d-%02d-%02d %02d-%02d-%02d", year, month, day, hour,minute,second);

            db.collection("users").document(userID).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        if(document.get("Food") != null) {
                            Map<String, Object> existingFoodData = (Map<String, Object>) document.get("Food");
                            existingFoodData.put(dateTime,foodName+", "+calories);

                            db.collection("users").document(userID).update("Food", existingFoodData).addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Added successfully", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(context, "Error add food to database", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Map<String, Object> existingFoodData = new HashMap<>();
                            existingFoodData.put(dateTime,foodName+", "+calories);

                            db.collection("users").document(userID).update("Food", existingFoodData).addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Added successfully", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(context, "Error add food to database", Toast.LENGTH_SHORT).show();
                            });

                        }

                    }
                } else {

                }
            });


        }

    }

    //When need to retrieve user data
    //user method like below:
    //UserDatabaseManagement.getUserData(getApplicationContext, new UserDatabaseManagement.OnUserDataCallback() {
    // @Override
    // public void onUserDataReceived(Map<String, Object> userData) {
    //String userName = (String) userData.get("username");
    //}
    //});
    //done
    public static void getUserData(Context context, OnUserDataCallback callback){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();

        userCollection.document(userId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document.exists()) {
                    Map<String, Object> userData = (Map<String, Object>) document.getData();
                    callback.onUserDataReceived(userData);
                    Toast.makeText(context,"User data retrieved successfully",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,"User data does not exist",Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context,"Error",Toast.LENGTH_SHORT).show();
            }
        });
    }
    //done
    public interface  OnUserDataCallback {
        void onUserDataReceived(Map<String, Object> userData);
    }
    //done
    public static void addRewardToUser(Context context, String reward){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            String userID = currentUser.getUid();

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            String dateTime = String.format("%04d-%02d-%02d %02d-%02d-%02d", year, month, day, hour,minute,second);

            db.collection("users").document(userID).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        if(document.get("reward") != null) {
                            Map<String, Object> existingRewardData = (Map<String, Object>) document.get("reward");
                            existingRewardData.put(dateTime, reward);

                            db.collection("users").document(userID).update("reward", existingRewardData).addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Reward added successfully", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(context, "Error add reward to database", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Map<String, Object> existingRewardData = new HashMap<>();
                            existingRewardData.put(dateTime, reward);

                            db.collection("users").document(userID).update("reward", existingRewardData).addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Reward added successfully", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(context, "Error add reward to database", Toast.LENGTH_SHORT).show();
                            });

                        }

                    }
                } else {

                }
            });


        }

    }

}
