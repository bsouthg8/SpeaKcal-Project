package com.example.speakcalproject;//package com.example.speakcalproject;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
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


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;


public class Journal_entry extends AppCompatActivity {

    Button button;
    //    TextView textView;
    ListView listView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailylog);

        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("Food");

        Ref.child("Name").setValue("milk");
        Ref.child("Amount").setValue(23);

        button = findViewById(R.id.button_ID);
//        textView = findViewById(R.id.textView_ID);
        listView = findViewById(R.id.list_ID);
        editText = findViewById(R.id.editText_ID);

        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference().child("Food");
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LinkedList<Pair<String, Long>> foodList = new LinkedList<>();
                String userInput = editText.getText().toString();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("Name").getValue(String.class);
                    Long amount = snapshot.child("Amount").getValue(Long.class);
                    if (name != null && name.equals(userInput)) {
                        foodList.add(new Pair<>(name, amount));
                    }
                }

                ArrayAdapter<Pair<String, Long>> adapter = new ArrayAdapter<Pair<String, Long>>(Journal_entry.this, android.R.layout.simple_list_item_2, android.R.id.text1, foodList) {
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
                // Log a message
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodRef.addListenerForSingleValueEvent(valueEventListener);
            }
        });
    }
}