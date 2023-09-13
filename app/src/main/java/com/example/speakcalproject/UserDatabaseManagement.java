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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Calendar;
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
    //Reward
    public static void addUser(Context context, String userName, String passWord) {
        Map<String, Object> user = new HashMap<>();
        user.put("Username",userName);
        user.put("Password",passWord);

        userCollection.add(user).addOnSuccessListener(documentReference -> {
            Toast.makeText(context,"User added successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(context,"Error adding user", Toast.LENGTH_SHORT).show();
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

    //still working on it. most function works
    public static void addCalorieToUser(Context context, String userName, String foodName, float calories) {

        Query query = userCollection.whereEqualTo("Username", userName);

        query.get().addOnSuccessListener(querySnapshot -> {
            for (QueryDocumentSnapshot document : querySnapshot) {
                String documentIdToUpdate = document.getId();
                DocumentReference userDocRef = userCollection.document(documentIdToUpdate);

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                String dateTime = String.format("%04d-%02d-%02d %02d-%02d-%02d", year, month, day, hour,minute,second);

                userCollection.document(documentIdToUpdate).get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> userData = documentSnapshot.getData();
                        if (userData.containsKey("Food") && userData.get("Food") instanceof Map) {
                            Map<String, Object> foodData = (Map<String, Object>) userData.get("Food");

                            foodData.put(dateTime, foodName + "," + Float.toString(calories));

                            userDocRef.update("Food", foodData).addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Food added successfully", Toast.LENGTH_SHORT).show();
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

}
