package com.example.speakcalproject;

import android.os.AsyncTask;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUpdateReward extends AsyncTask<String, Void, Void> {
    private OnPostExecuteListener onPostExecuteListener;

    public interface OnPostExecuteListener {
        void onPostExecute();
    }

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final CollectionReference userCollection = db.collection("users");

    public DatabaseUpdateReward(OnPostExecuteListener listener) {
        this.onPostExecuteListener = listener;
    }
    @Override
    protected Void doInBackground(String... strings) {
        if (strings.length > 0) {
            String reward = strings[0];
            addRewardToUser(reward);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // Call the listener when the task is complete
        if (onPostExecuteListener != null) {
            onPostExecuteListener.onPostExecute();
        }
    }

    private void addRewardToUser(String reward) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            String dateTime = String.format("%04d-%02d-%02d %02d-%02d-%02d", year, month, day, hour, minute, second);

            db.collection("users").document(userID).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.get("reward") != null) {
                            Map<String, Object> existingRewardData = (Map<String, Object>) document.get("reward");
                            existingRewardData.put(dateTime, reward);

                            db.collection("users").document(userID).update("reward", existingRewardData).addOnSuccessListener(aVoid -> {

                            }).addOnFailureListener(e -> {

                            });
                        } else {
                            Map<String, Object> existingRewardData = new HashMap<>();
                            existingRewardData.put(dateTime, reward);

                            db.collection("users").document(userID).update("reward", existingRewardData).addOnSuccessListener(aVoid -> {

                            }).addOnFailureListener(e -> {

                            });

                        }

                    }
                } else {

                }
            });


        }

    }


}










