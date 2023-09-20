package com.example.speakcalproject;//package com.example.speakcalproject;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailylog);

        // Initialize the button
        button = findViewById(R.id.button_ID);
        button2 = findViewById(R.id.button2_ID);
        button3 = findViewById(R.id.button3_ID);

        // Initialize the  ListView
        listView = findViewById(R.id.list_ID);
        listView2 = findViewById(R.id.list2_ID);
        listView3 = findViewById(R.id.list3_ID);

        // Initialize the EditText
        editText = findViewById(R.id.editText_ID);
        editText2 = findViewById(R.id.editText2_ID);
        editText3 = findViewById(R.id.editText3_ID);

        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference().child("Food");
        ValueEventListener valueEventListener = new ValueEventListener() {

            // onDataChane looks for any change in the data base and activates when the data in the database changes
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //arrayList stores the data from the data base that is equil to what is in the Text view
                ArrayList<Pair<String, Long>> foodList = new ArrayList<>();
                String userInput = editText.getText().toString();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("Name").getValue(String.class);
                    Long amount = snapshot.child("Amount").getValue(Long.class);
                    if (name != null && name.equals(userInput)) {
                        foodList.add(new Pair<>(name, amount));
                    }
                }

                ArrayAdapter<Pair<String, Long>> adapter = new ArrayAdapter<Pair<String, Long>>(Journal_entry.this, android.R.layout.simple_list_item_2, android.R.id.text1, foodList) {

                    //getView is responsible for returning a view
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        Pair<String, Long> pair = (Pair<String, Long>) getItem(position);
                        TextView text1 = view.findViewById(android.R.id.text1);
                        TextView text2 = view.findViewById(android.R.id.text2);
                        if (pair != null) {
                            text1.setText(pair.first);
                            text2.setText(String.valueOf(pair.second));
                        }
                        return view;
                    }
                };
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log or show the error message
                Log.e("DatabaseError", databaseError.getMessage());
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = editText.getText().toString();
                if (!userInput.isEmpty()) {
                    foodRef.addListenerForSingleValueEvent(valueEventListener);
                } else {
//                    Toast.makeText(getApplicationContext(), "Input cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput2 = editText2.getText().toString();
                if (!userInput2.isEmpty()) {
                    foodRef.addListenerForSingleValueEvent(valueEventListener);
                } else {
//                    Toast.makeText(getApplicationContext(), "Input cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput2 = editText2.getText().toString();
                if (!userInput2.isEmpty()) {
                    foodRef.addListenerForSingleValueEvent(valueEventListener);
                } else {
//                    Toast.makeText(getApplicationContext(), "Input cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
