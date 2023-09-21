package com.example.speakcalproject;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private EditText editTextName;
    private ImageView imageViewProfilePic;
    private static final int PICK_IMAGE_REQUEST = 1;
    private int mCurrentSelectedItemId = R.id.navigation_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI elements
        editTextName = findViewById(R.id.editTextName);
        imageViewProfilePic = findViewById(R.id.imageViewProfilePic);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonSelectImage = findViewById(R.id.buttonSelectImage);

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
    }

    private void setupBottomNav() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(mCurrentSelectedItemId);
        navView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == mCurrentSelectedItemId) return false;

            Intent intent = null;
            int enterAnim = R.anim.slide_in_left;
            int exitAnim = R.anim.slide_out_right;
            if (itemId == R.id.navigation_home) {
                intent = new Intent(this, MainActivity.class);
            } else if (itemId == R.id.navigation_journal) {
                intent = new Intent(this, TestJournal.class);
            } else if (itemId == R.id.navigation_photo) {
                intent = new Intent(this, PhotoRecognition.class);
            } else if (itemId == R.id.navigation_profile) {
                return false;
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(enterAnim, exitAnim);
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
}

