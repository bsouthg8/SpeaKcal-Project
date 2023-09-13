package com.example.journalentryspeckal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.speakcalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Journal_entry extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseFirestore firestore;
    private ListView breakfastOutput;
    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;
    private EditText enterBreakfast;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize ListView and ArrayAdapter
        breakfastOutput = findViewById(R.id.Output_Breakfast);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.dailylogs, list);
        breakfastOutput.setAdapter(adapter);

        enterBreakfast = findViewById(R.id.Input_Breakfast);
        add = findViewById(R.id.Input_Food);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String food = enterBreakfast.getText().toString();
                retrieveFoodFromDatabase(food);
            }
        });
    }

    private void retrieveFoodFromDatabase(String food) {
        // Retrieve the typed food from the Firestore database
        firestore.collection("Food")
                .whereEqualTo("foodName", food)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                                    list.clear(); // Clear the current list
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String foodName = document.getString("foodName");
                                list.add(document.getString("foodName")); // Add the retrieved food to the list
                            }
                            adapter.notifyDataSetChanged(); // Notify the adapter about the data change
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}

