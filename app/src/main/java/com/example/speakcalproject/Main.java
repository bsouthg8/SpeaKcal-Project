package com.example.speakcalproject;

import android.os.Bundle;
import android.view.MenuItem;
import com.example.speakcalproject.HomeFragment;
import com.example.speakcalproject.CameraFragment;
import com.example.speakcalproject.UserFragment;
import com.example.speakcalproject.MoveFragment;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment=null;
            switch (item.getItemId())
            {
                case R.id.home:
                    selectedFragment=new HomeFragment();
                    break;

                case R.id.camera:
                    selectedFragment=new CameraFragment();
                    break;

                case R.id.user:
                    selectedFragment=new UserFragment();
                    break;

                case R.id.move:
                    selectedFragment=new MoveFragment();
                    break;

            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            return true;
        }
    };

}