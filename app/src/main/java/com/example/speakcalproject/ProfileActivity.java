package com.example.speakcalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private EditText editTextName;
    private ImageView imageViewProfilePic;
    private static final int PICK_IMAGE_REQUEST = 1;
    private int mCurrentSelectedItemId = R.id.navigation_profile;
    private Button changeCaloriesLimitation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI elements
        editTextName = findViewById(R.id.editTextName);
        imageViewProfilePic = findViewById(R.id.imageViewProfilePic);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonSelectImage = findViewById(R.id.buttonSelectImage);
        changeCaloriesLimitation = findViewById(R.id.buttonChangeCaloriesLimitation);

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Set initial UI states
        String username = sharedPreferences.getString("username", "");
        editTextName.setText(username);

        // Bottom navigation
        setupBottomNav();

        // Event listeners
        buttonSave.setOnClickListener(v -> saveProfileData());
        buttonSelectImage.setOnClickListener(v -> openImageSelection());

        changeCaloriesLimitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeCaloriesLimitDialog();
            }
        });
    }

    private void setupBottomNav() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(mCurrentSelectedItemId);
        navView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == mCurrentSelectedItemId) return false;

            Intent intent = null;
            if (itemId == R.id.navigation_home) {
                intent = new Intent(this, MainActivity.class);
                finishAfterTransition();
            } else if (itemId == R.id.navigation_journal) {
                intent = new Intent(this, Journal_entry.class);
                finishAfterTransition();
            } else if (itemId == R.id.navigation_photo) {
                intent = new Intent(this, PhotoRecognition.class);
                finishAfterTransition();
            } else if (itemId == R.id.navigation_profile) {
                return false;
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            return true;
        });
    }

    private void saveProfileData() {
        String name = editTextName.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", name);
        editor.apply();
    }

    private void openImageSelection() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageViewProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showChangeCaloriesLimitDialog() {
        MyApplication myApp = (MyApplication) getApplication();
        HashMap<String,Object> userInfo = myApp.getGlobalData();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Calories Limit");
        builder.setMessage("Current calories limitation is "+userInfo.get("calories limitation"));

        // Create an EditText view to input the new calories limit
        final EditText input = new EditText(this);
        input.setHint("new calories limitation");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the input from the user
                String newCaloriesLimit = input.getText().toString();

                try {
                    int newIntCaloriesLimit = Integer.parseInt(newCaloriesLimit);
                    if (newIntCaloriesLimit > 0) {
                        UserDatabaseManagement.updateLimitation(getApplicationContext(),newIntCaloriesLimit,1);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        // Display an error message as the input is not greater than 0
                        Toast.makeText(getApplicationContext(), "Calories limit must be greater than 0", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    // Invalid input (not a valid integer)
                    Toast.makeText(getApplicationContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }



            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}

