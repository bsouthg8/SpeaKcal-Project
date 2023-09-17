package com.example.speakcalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        ImageView logo = (ImageView) findViewById(R.id.imageView);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent dbmanager = new Intent(MainActivity.this,AndroidDatabaseManager.class);
                //startActivity(dbmanager);


                startActivity(new Intent(MainActivity.this, UserDatabaseExample.class));
            }
        });


        ImageButton meals = (ImageButton) findViewById(R.id.meals);
        meals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MealsActivity.class));
            }
        });

        ImageButton recipes = (ImageButton) findViewById(R.id.recipes);
        recipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RecipeActivity.class));
            }
        });

        ImageButton groceries = (ImageButton) findViewById(R.id.groceries);
        groceries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PhotoRecognition.class));
            }
        });

        ImageButton settings = (ImageButton) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Profile.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
