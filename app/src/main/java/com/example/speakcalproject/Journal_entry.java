package com.example.speakcalproject;//package com.example.speakcalproject;



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
import java.util.Objects;

public class Journal_entry extends AppCompatActivity {

    Button button;
    Button button2;
    Button button3;
    ListView listView;
    ListView listView2;
    ListView listView3;
    EditText editText;
    EditText editText2;
    EditText editText3;

    FirebaseFirestore db;
    CollectionReference foodRef;
    ListenerRegistration listener1;
    ListenerRegistration listener2;
    ListenerRegistration listener3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailylog);

        // Bottom navigation
        setupBottomNav();

        // Initialize the Firestore database
        db = FirebaseFirestore.getInstance();
        foodRef = db.collection("Food");

        // Initialize the button
        button = findViewById(R.id.button_ID);
        button2 = findViewById(R.id.button2_ID);
        button3 = findViewById(R.id.button3_ID);

        // Initialize the ListView
        listView = findViewById(R.id.list_ID);
        listView2 = findViewById(R.id.list2_ID);
        listView3 = findViewById(R.id.list3_ID);

        // Initialize the EditText
        editText = findViewById(R.id.editText_ID);
        editText2 = findViewById(R.id.editText2_ID);
        editText3 = findViewById(R.id.editText3_ID);

        // For button1
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

        listView.setAdapter(adapter);
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
            } else if (itemId == R.id.navigation_photo) {
                intent = new Intent(this, PhotoRecognition.class);
            } else if (itemId == R.id.navigation_profile) {
                intent = new Intent(this, ProfileActivity.class);
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            return true;
        });
    }

}