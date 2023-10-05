package com.example.speakcalproject;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Journal_entry extends AppCompatActivity {

    Button buttonBreakfast, buttonLunch, buttonDinner;
    ListView listViewBreakfast, listViewLunch, listViewDinner;
    EditText editTextBreakfast, editTextLunch, editTextDinner;

    FirebaseFirestore db;
    CollectionReference foodRef;

    ArrayList<Pair<String, String>> foodListBreakfast = new ArrayList<>();
    ArrayList<Pair<String, String>> foodListLunch = new ArrayList<>();
    ArrayList<Pair<String, String>> foodListDinner = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailylog);

        // Bottom navigation
        setupBottomNav();

        // Currently it is saving the data to the wrong collection, but we can fix that next sprint.

        // Initialize the Firestore database
        db = FirebaseFirestore.getInstance();
        foodRef = db.collection("Food");

        // Initialize Buttons
        buttonBreakfast = findViewById(R.id.button_ID);
        buttonLunch = findViewById(R.id.button2_ID);
        buttonDinner = findViewById(R.id.button3_ID);

        // Initialize ListViews
        listViewBreakfast = findViewById(R.id.list_ID);
        listViewLunch = findViewById(R.id.list2_ID);
        listViewDinner = findViewById(R.id.list3_ID);

        // Initialize EditTexts
        editTextBreakfast = findViewById(R.id.editText_ID);
        editTextLunch = findViewById(R.id.editText2_ID);
        editTextDinner = findViewById(R.id.editText3_ID);

        // Set button onClick listeners
        buttonBreakfast.setOnClickListener(v -> handleButtonClick(editTextBreakfast, foodListBreakfast, listViewBreakfast, "Breakfast"));
        buttonLunch.setOnClickListener(v -> handleButtonClick(editTextLunch, foodListLunch, listViewLunch, "Lunch"));
        buttonDinner.setOnClickListener(v -> handleButtonClick(editTextDinner, foodListDinner, listViewDinner, "Dinner"));

        // Load the saved data for each meal category when the activity is created
        loadSavedBreakfastData();
        loadSavedLunchData();
        loadSavedDinnerData();


        // James code that I have commented out for now
/*        // For button1
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = editText.getText().toString();
                if (!userInput.isEmpty()) {
                    if (listener1 != null) {
                        listener1.remove();
                    }
                    listener1 = foodRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                            handleEvent(querySnapshot, e, userInput, listView);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Input cannot be empty", Toast.LENGTH_SHORT).show();
                }

                // Reset the EditText field
                editText.setText("");
            }
        });

// For button2
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = editText2.getText().toString();
                if (!userInput.isEmpty()) {
                    if (listener2 != null) {
                        listener2.remove();
                    }
                    listener2 = foodRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                            handleEvent(querySnapshot, e, userInput, listView2);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Input cannot be empty", Toast.LENGTH_SHORT).show();
                }

                // Reset the EditText field
                editText2.setText("");
            }
        });

// For button3
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = editText3.getText().toString();
                if (!userInput.isEmpty()) {
                    if (listener3 != null) {
                        listener3.remove();
                    }
                    listener3 = foodRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                            handleEvent(querySnapshot, e, userInput, listView3);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Input cannot be empty", Toast.LENGTH_SHORT).show();
                }

                // Reset the EditText field
                editText3.setText("");
            }
        });

    }


    private void handleEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e, String userInput, ListView listView) {
        if (e != null) {
            Log.e("Fire store Error", Objects.requireNonNull(e.getMessage()));
            return;
        }

        ArrayList<Pair<String, String>> foodList = new ArrayList<>();

//        assert querySnapshot != null;
//        foodList.clear(); // Clear the list before populating with new items

        for (QueryDocumentSnapshot document : querySnapshot) {
            String name = document.getString("Name");
            String amount = document.getString("Amount");

            if (name != null && name.equals(userInput)) {
                foodList.add(new Pair<>("Food: " + name, "Calories: "+amount));
            }
        }

        ArrayAdapter<Pair<String, String>> adapter = new ArrayAdapter<Pair<String, String>>(Journal_entry.this, android.R.layout.simple_list_item_2, android.R.id.text1, foodList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Pair<String, String> pair = getItem(position);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                if (pair != null) {
                    text1.setText(pair.first);
                    text2.setText(pair.second);
                }

                return view;
            }
        };

        listView.setAdapter(adapter);*/
    }

    private void setupBottomNav() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_journal);
        navView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_journal) return false;

            Intent intent = null;
            if (itemId == R.id.navigation_home) {
                intent = new Intent(this, MainActivity.class);
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

    private void handleButtonClick(EditText editText, ArrayList<Pair<String, String>> foodList, ListView listView, String mealType) {
        String userInput = editText.getText().toString();

        if (!userInput.isEmpty()) {
            foodRef.whereEqualTo("Name", userInput).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String name = document.getString("Name");
                        String amount = document.getString("Amount");
                        if (name != null && amount != null) {
                            foodList.add(new Pair<>("Food: " + name, "Calories: " + amount));

                            // Save the food entry to Firestore for the user
                            Map<String, Object> data = new HashMap<>();
                            data.put("Name", name);
                            data.put("Amount", amount);
                            data.put("MealType", mealType);
                            foodRef.add(data);
                        }
                    }
                    updateListView(foodList, listView);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Input cannot be empty", Toast.LENGTH_SHORT).show();
        }
        editText.setText("");
    }

    private void updateListView(ArrayList<Pair<String, String>> foodList, ListView listView) {
        ArrayAdapter<Pair<String, String>> adapter = new ArrayAdapter<Pair<String, String>>(this, android.R.layout.simple_list_item_2, android.R.id.text1, foodList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Pair<String, String> pair = (Pair<String, String>) getItem(position);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                if (pair != null) {
                    text1.setText(pair.first);
                    text2.setText(pair.second);
                }
                return view;
            }
        };
        listView.setAdapter(adapter);
    }

    private void loadSavedDataByMealType(String mealType, ArrayList<Pair<String, String>> foodList, ListView listView) {
        foodRef.whereEqualTo("MealType", mealType).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("Name");
                    String amount = document.getString("Amount");
                    if (name != null && amount != null) {
                        foodList.add(new Pair<>("Food: " + name, "Calories: " + amount));
                    }
                }
                updateListView(foodList, listView);
            }
        });
    }

    private void loadSavedBreakfastData() {
        loadSavedDataByMealType("Breakfast", foodListBreakfast, listViewBreakfast);
    }

    private void loadSavedLunchData() {
        loadSavedDataByMealType("Lunch", foodListLunch, listViewLunch);
    }

    private void loadSavedDinnerData() {
        loadSavedDataByMealType("Dinner", foodListDinner, listViewDinner);
    }

}

