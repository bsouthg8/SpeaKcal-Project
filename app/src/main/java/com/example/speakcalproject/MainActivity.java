package com.example.speakcalproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public FirebaseFirestore firestore;

    public ListView breakfast_output;
    public FirebaseDatabase database;

    private EditText enter_Breakfast;

    private ArrayList<String> list;
    private ArrayAdapter<String> adapter;

    private Button add;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();


        // Initialize ListView and ArrayAdapter
        breakfast_output = findViewById(R.id.Output_Breakfast);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.dailylogs, list);
        breakfast_output.setAdapter(adapter);


        enter_Breakfast = findViewById(R.id.Input_Breakfast);
        add = findViewById(R.id.Input_Food);


//        database = FirebaseDatabase.getInstance();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String food = enter_Breakfast.getText().toString();
              retrieveFoodFromDatabase(food);
            }
        });


//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        Object binding = null;
//        NavigationUI.setupWithNavController(binding.navView, navController);

    }

    private void retrieveFoodFromDatabase(String food){

        // Retreve the typed food from the Firestore database
        firestore.collection("Food")
                .whereEqualTo("foodName", food)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            list.clear(); // Clear the current list
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String foodName = document.getString("foodName");
                                list.add(foodName); // Add the retrieved food to the list
                            }
                            adapter.notifyDataSetChanged(); // Notify the adapter about the data change
                        } else {
                            Log.d(TAG, " Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}