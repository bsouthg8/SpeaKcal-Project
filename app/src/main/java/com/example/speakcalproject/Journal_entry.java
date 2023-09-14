package com.example.speakcalproject;//package com.example.speakcalproject;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;



public class Journal_entry extends AppCompatActivity {

    private static final String TAG = "Journal_entry";


    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;
    private EditText enterBreakfast;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Food");

        enterBreakfast = findViewById(R.id.Input_Breakfast);

        ListView breakfastOutput = findViewById(R.id.Output_Breakfast);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        breakfastOutput.setAdapter(adapter);

        Button add = findViewById(R.id.GetFromDatabase);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foodName = enterBreakfast.getText().toString();// Gets user input
                Query query = ref.orderByChild("Name").equalTo(foodName);// Query the database

                query.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list.clear();
                        //Retrievve data from the database
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String name = childSnapshot.child("Name").getValue(String.class);
                            int amount = childSnapshot.child("Amount").getValue(Integer.class);
                            String entry = "Name: " + name + "\nAmount: " + amount;
                            list.add(entry);
                            Log.d(TAG, "Data: " + entry); // Log the data
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Log.e(TAG, "Fail to retrive data", error.toException());
                    }
                });
            }
        });


    }
}

