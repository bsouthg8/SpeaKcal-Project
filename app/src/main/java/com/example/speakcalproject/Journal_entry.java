package com.example.speakcalproject;



import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Journal_entry extends AppCompatActivity {

    Button buttonBreakfast, buttonLunch, buttonDinner;
    ListView listViewBreakfast, listViewLunch, listViewDinner;
    EditText editTextBreakfast, editTextLunch, editTextDinner;
    FirebaseFirestore db;
    CollectionReference foodRef;
    private List<FoodEntry> foodListBreakfast = new ArrayList<>();
    private List<FoodEntry> foodListLunch = new ArrayList<>();
    private List<FoodEntry> foodListDinner = new ArrayList<>();

    private String targetDate;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailylog);
        targetDate = dateFormat.format(new Date());

        // Bottom navigation
        setupBottomNav();

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
        buttonBreakfast.setOnClickListener(v -> handleButtonClick(editTextBreakfast, foodListBreakfast, listViewBreakfast, "Breakfast", targetDate));
        buttonLunch.setOnClickListener(v -> handleButtonClick(editTextLunch, foodListLunch, listViewLunch, "Lunch", targetDate));
        buttonDinner.setOnClickListener(v -> handleButtonClick(editTextDinner, foodListDinner, listViewDinner, "Dinner", targetDate));

        // Set List View onClick listeners
        listViewBreakfast.setOnItemClickListener((parent, view, position, id) -> {
            FoodEntry clickedFoodEntry = foodListBreakfast.get(position);
            showFoodDetailsDialog(clickedFoodEntry, foodListBreakfast, listViewBreakfast, "Breakfast", clickedFoodEntry.getDateTime());
        });
        listViewLunch.setOnItemClickListener((parent, view, position, id) -> {
            FoodEntry clickedFoodEntry = foodListLunch.get(position);
            showFoodDetailsDialog(clickedFoodEntry, foodListLunch, listViewLunch, "Lunch", clickedFoodEntry.getDateTime());
        });
        listViewDinner.setOnItemClickListener((parent, view, position, id) -> {
            FoodEntry clickedFoodEntry = foodListDinner.get(position);
            showFoodDetailsDialog(clickedFoodEntry, foodListDinner, listViewDinner, "Dinner", clickedFoodEntry.getDateTime());
        });


        // Load initial data
        loadSavedBreakfastData(targetDate);
        loadSavedLunchData(targetDate);
        loadSavedDinnerData(targetDate);

        TextView currentDateTextView = findViewById(R.id.currentDateTextView);
        currentDateTextView.setText(targetDate);
        Button prevButton = findViewById(R.id.prevButton);
        Button nextButton = findViewById(R.id.nextButton);

        prevButton.setOnClickListener(v -> {
            try {
                Date date = dateFormat.parse(targetDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DATE, -1);
                targetDate = dateFormat.format(cal.getTime());

                // Reload data
                loadSavedBreakfastData(targetDate);
                loadSavedLunchData(targetDate);
                loadSavedDinnerData(targetDate);
                currentDateTextView.setText(targetDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        nextButton.setOnClickListener(v -> {
            try {
                Date date = dateFormat.parse(targetDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DATE, 1);
                targetDate = dateFormat.format(cal.getTime());

                // Reload data
                loadSavedBreakfastData(targetDate);
                loadSavedLunchData(targetDate);
                loadSavedDinnerData(targetDate);
                currentDateTextView.setText(targetDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
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

    private void handleButtonClick(EditText editText, List<FoodEntry> foodList, ListView listView, String mealType, String targetDate) {
        String userInput = editText.getText().toString();

        if (!userInput.isEmpty()) {
            foodRef.whereEqualTo("Name", userInput).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String name = document.getString("Name");
                        String amount = document.getString("Amount");
                        if (name != null && amount != null) {
                            FoodEntry entry = new FoodEntry(name, Float.parseFloat(amount), targetDate, mealType);
                            foodList.add(entry);
                            // Save the food entry to Firestore for the user
                            UserDatabaseManagement.addCalorieToUser(getApplicationContext(), name, entry.getCalories(), mealType, targetDate);
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

    private void updateListView(List<FoodEntry> foodList, ListView listView) {
        ArrayAdapter<FoodEntry> adapter = new ArrayAdapter<FoodEntry>(this, R.layout.list_item_layout, foodList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view;
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.list_item_layout, null);
                } else {
                    view = convertView;
                }
                FoodEntry entry = (FoodEntry) getItem(position);
                TextView text1 = view.findViewById(R.id.text1);
                TextView text2 = view.findViewById(R.id.text2);

                if (entry != null) {
                    text1.setText("Food: " + entry.getFoodName());
                    text2.setText("Calories: " + entry.getCalories());
                }
                return view;
            }
        };
        listView.setAdapter(adapter);
    }

    private void showFoodDetailsDialog(FoodEntry clickedFood, List<FoodEntry> foodList, ListView listView, String mealType, String dateTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the dialog_edit_entry XML layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_entry, null);
        builder.setView(dialogView);

        // Populate the EditTexts with the clicked food's data
        EditText editFoodName = dialogView.findViewById(R.id.editFoodName);
        EditText editCalories = dialogView.findViewById(R.id.editCalories);
        Spinner spinnerMealType = dialogView.findViewById(R.id.spinnerMealType);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);

        editFoodName.setText(clickedFood.getFoodName());
        editCalories.setText(String.valueOf(clickedFood.getCalories()));

        // Setup spinner
        String[] foodCategories = getResources().getStringArray(R.array.food_categories);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, foodCategories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMealType.setAdapter(spinnerAdapter);
        spinnerMealType.setSelection(spinnerAdapter.getPosition(mealType));

        // Handle the Save button click
        btnSave.setOnClickListener(v -> {
            String newFoodName = editFoodName.getText().toString();
            String newCalories = editCalories.getText().toString();

            // Update the clickedFood details and update the database
            clickedFood.setFoodName(newFoodName);
            clickedFood.setCalories(Float.parseFloat(newCalories));
            UserDatabaseManagement.updateFoodEntry(this, clickedFood, mealType, dateTime);

            // Refresh the ListView
            ArrayAdapter<FoodEntry> adapter = (ArrayAdapter<FoodEntry>) listView.getAdapter();
            adapter.notifyDataSetChanged();

            Toast.makeText(this, "Entry updated", Toast.LENGTH_SHORT).show();
        });

        // Handle the Delete button click
        btnDelete.setOnClickListener(v -> {
            // Remove the clickedFood from the list and the database
            foodList.remove(clickedFood);
            UserDatabaseManagement.removeFoodEntry(this, clickedFood, mealType, dateTime);

            // Refresh the ListView
            ArrayAdapter<FoodEntry> adapter = (ArrayAdapter<FoodEntry>) listView.getAdapter();
            adapter.notifyDataSetChanged();

            Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show();
        });

        // Display the dialog
        builder.create().show();
    }

    private void loadSavedDataByMealType(String mealType, List<FoodEntry> foodList, ListView listView, String targetDate) {
        // Clear the list first
        foodList.clear();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();
            db.collection("users").document(userID).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> allFoodData = (Map<String, Object>) document.get("Food");
                        if (allFoodData != null) {
                            for (Map.Entry<String, Object> entry : allFoodData.entrySet()) {
                                Object value = entry.getValue();
                                if (value instanceof Map) {
                                    Map<String, Object> foodData = (Map<String, Object>) value;
                                    if (mealType.equals(foodData.get("mealType"))) {
                                        String entryDateTime = (String) foodData.get("dateTime");
                                        String entryDate = entryDateTime.split(" ")[0];
                                        if (entryDate.equals(targetDate)) {
                                            String name = (String) foodData.get("foodName");
                                            float amount = ((Number) foodData.get("calories")).floatValue();
                                            foodList.add(new FoodEntry(name, amount, entryDateTime, mealType));
                                        }
                                    }
                                } else if (value instanceof String) {
                                    // Handle the old format (String)
                                    String[] parts = ((String) value).split(", ");
                                    if (parts.length == 3 && mealType.equals(parts[2])) {
                                        // Use targetDate as the date since the old format doesn't contain the exact dateTime
                                        foodList.add(new FoodEntry(parts[0], Float.parseFloat(parts[1]), targetDate, mealType));
                                    }
                                }
                            }
                        }
                        updateListView(foodList, listView);
                    }
                }
            });
        }
    }

    private void loadSavedBreakfastData(String targetDate) {
        loadSavedDataByMealType("Breakfast", foodListBreakfast, listViewBreakfast, targetDate);
    }

    private void loadSavedLunchData(String targetDate) {
        loadSavedDataByMealType("Lunch", foodListLunch, listViewLunch, targetDate);
    }

    private void loadSavedDinnerData(String targetDate) {
        loadSavedDataByMealType("Dinner", foodListDinner, listViewDinner, targetDate);
    }

}


// James code that I have commented out for now (This was in the onCreate method)
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
