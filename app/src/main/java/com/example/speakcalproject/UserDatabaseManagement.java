package com.example.speakcalproject;
import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserDatabaseManagement {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final CollectionReference userCollection = db.collection("User");


    //add user database "User"
    //User structure:
    //Username
    //Password
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
            int day = calendar.get(Calendar.DAY_OF_MONTH)+1;
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


    //delete user according to user name
    public static void deleteUser(Context context, String userName) {
        Query query = userCollection.whereEqualTo("Username",userName);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    if(documentSnapshot.exists()) {
                        userCollection.document(documentSnapshot.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context,"User: "+userName+" deleted",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context,"User: "+userName+" failed to be deleted",Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(context,"User: "+userName+" failed to be deleted",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context,"User: "+userName+" is not in the database",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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
            int day = calendar.get(Calendar.DAY_OF_MONTH)+1;
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
                                Toast.makeText(context, "Error adding food", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Map<String, Object> foodData = new HashMap<>();
                            foodData.put(dateTime, foodName + "," + Float.toString(calories));

                            userDocRef.update("Food", foodData).addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Food added successfully", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(context, "Error adding food", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        Toast.makeText(context, "User document does not exist", Toast.LENGTH_SHORT).show();
                    }

                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Error retrieving user document", Toast.LENGTH_SHORT).show();
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show();
        });
    }

    public static void getUser(String userName, final OnSuccessListener<Map<String, Object>> successListener, final OnFailureListener failureListener){
        Query query = userCollection.whereEqualTo("Username",userName);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                    if (documentSnapshot.exists()) {
                        Map<String, Object> userData = new HashMap<>(documentSnapshot.getData());
                        successListener.onSuccess(userData);
                    } else {
                        failureListener.onFailure((new Exception("User document does not exist")));
                    }

                } else {
                    failureListener.onFailure((new Exception("No user found with the specified username")));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                failureListener.onFailure(e);
            }
        });
    }

    //still working on it.
    public static void addRewardToUser(Context context, String userName, String reward){

    }

    public static double calculateCaloriesForDate(Map<String,Object>data,String targetDate) throws ParseException {
        double totalCalories = 0.0;

        for(Map.Entry<String, Object> entry : data.entrySet()){
            String dateTimeStr = entry.getKey();
            String foodInfo = entry.getValue().toString();
            dateTimeStr = dateTimeStr.split(" ")[0];
            dateTimeStr.replace("\"","");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(dateTimeStr);
            String currentDate = dateFormat.format(date);

            if(currentDate.equals(targetDate)) {
                String[] parts = foodInfo.split(", ");
                if(parts.length == 2) {
                    try {
                        double calories = Double.parseDouble(parts[1]);
                        totalCalories += calories;
                    } catch (NumberFormatException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return totalCalories;
    }
}
