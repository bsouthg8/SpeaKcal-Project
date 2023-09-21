package com.example.speakcalproject;

import static android.provider.MediaStore.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.graphics.Bitmap;
import android.widget.Toast;
import com.example.speakcalproject.R;

import com.example.speakcalproject.ml.LiteModelAiyVisionClassifierFoodV11;
import com.example.speakcalproject.ui.dashboard.DashboardFragment;
import com.example.speakcalproject.ui.home.HomeFragment;
import com.example.speakcalproject.ui.home.HomeViewModel;
import com.example.speakcalproject.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;
import java.util.List;

public class PhotoRecognition extends AppCompatActivity {

    private Button camera,gallery,modify;
    private ImageView imageView;
    private TextView result;
    public FoodInfo foodInfo;
    private float foodWeight;
    private float foodCalories;
    private Button backToMainPage;
    private String foodName;
    private FirebaseFirestore ff;

    private int mCurrentSelectedItemId = R.id.navigation_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Photo Recognition");
        setContentView(R.layout.activity_photo_recognition);

        // backToMainPage = findViewById((R.id.button4));
        camera = findViewById(R.id.button);
        gallery = findViewById(R.id.button2);
        modify = findViewById(R.id.button3);
        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);
        FirebaseApp.initializeApp(getApplicationContext());
        ff = FirebaseFirestore.getInstance();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_photo);
        navView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Check if the item is already selected
            if (itemId == mCurrentSelectedItemId) {
                return false;
            }

            if (itemId == R.id.navigation_photo) {
                return false;  // Stay on the same screen
            } else if (itemId == R.id.navigation_home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.navigation_journal) {
                Intent intent = new Intent(this, TestJournal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            return false;
        });


        //waiting for main page
/*        backToMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }); */

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent cameraIntent = new Intent(Intent.ACTION_PICK, Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               inputCorrectAnswerDialog(1, null);
            }
        });
    }

    //requestCode == 1 gallery
    //requestCode == 3 camera
    //requestCode == 2 retrieve calories data from database management
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK && requestCode != 2){
            if(requestCode == 3){
                Bitmap image= (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getHeight(),image.getWidth());
                image = ThumbnailUtils.extractThumbnail(image,dimension,dimension);
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image,224,224,false);
                classifyImage(image);
            }else if (requestCode == 1){
                Uri dat = data.getData();
                Bitmap image = null;
                try{
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(),dat);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image,224,224,false);
                classifyImage(image);
            }
        } else if (resultCode == RESULT_OK && requestCode == 2) {
            if(data != null) {
                foodCalories = Float.parseFloat(data.getStringExtra("result"));
                foodInfo.setCalories(foodInfo.getFoodWeight()*foodCalories/100.0f);
                result.setText("Item: "+foodInfo.getFoodName()+"\nWeight: "+foodInfo.getFoodWeight()+"\nCalories: "+foodInfo.getCalories());
                UserDatabaseManagement.addCalorieToUser(getApplicationContext(),foodInfo.getFoodName(),foodInfo.getCalories());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void classifyImage(Bitmap bitmap)
    {
        try {
            LiteModelAiyVisionClassifierFoodV11 model = LiteModelAiyVisionClassifierFoodV11.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorImage image = TensorImage.fromBitmap(bitmap);

            // Runs model inference and gets result.
            LiteModelAiyVisionClassifierFoodV11.Outputs outputs = model.process(image);
            List<Category> probability = outputs.getProbabilityAsCategoryList();

            float[] numbers = new float[probability.size()];
            int maxPos = 0;
            float maxScore = 0;

            for(int i = 0; i < probability.size(); i++)
            {
                if(probability.get(i).getScore() > maxScore)
                {
                    maxScore = probability.get(i).getScore();
                    maxPos = i;
                }
            }

            String output = "";
            if(maxPos == 0) {
               output = "no match";
                foodName = null;
            } else {
                output += "Item: "+probability.get(maxPos).getLabel();
                foodName = probability.get(maxPos).getLabel();
            }

            foodWeight = 0;
            result.setText(output);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Is the result " + foodName+ " correct?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    inputCorrectAnswerDialog(2, foodName);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    inputCorrectAnswerDialog(1, foodName);
                }
            });
            builder.show();

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }

    // status 1 == need to correct answer and weight
    // status 2 == just need to input weight
    public void inputCorrectAnswerDialog(int status, String foodName){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(status == 1){
            builder.setTitle("Enter correct name and weight");
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_weight_name_calories,null);
            builder.setView(dialogView);

            final EditText nameEditText = dialogView.findViewById(R.id.editTextName);
            final EditText weightText = dialogView.findViewById(R.id.editTextWeight);
            final EditText caloriesText = dialogView.findViewById(R.id.editTextKcalAmount);
            caloriesText.setVisibility(View.GONE);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = nameEditText.getText().toString();
                    String weight = weightText.getText().toString();

                    if(TextUtils.isEmpty(name) || TextUtils.isEmpty(weight)) {
                        Toast.makeText(getApplicationContext(), "Invalid input", Toast.LENGTH_SHORT).show();
                    }else{
                        float weight_number = Float.parseFloat(weight);
                        handleNameAndWeight(name,weight_number);
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
        else if(status == 2){
            builder.setTitle("Enter correct weight");
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_weight_name_calories,null);
            builder.setView(dialogView);

            final EditText nameEditText = dialogView.findViewById(R.id.editTextName);
            final EditText weightText = dialogView.findViewById(R.id.editTextWeight);
            final EditText caloriesText = dialogView.findViewById(R.id.editTextKcalAmount);
            nameEditText.setVisibility(View.GONE);
            caloriesText.setVisibility(View.GONE);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String weight = weightText.getText().toString();
                    if(TextUtils.isEmpty(weight)) {
                        Toast.makeText(getApplicationContext(), "Invalid input", Toast.LENGTH_SHORT).show();
                    }else{
                        float weight_number = Float.parseFloat(weight);
                        handleNameAndWeight(foodName,weight_number);
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

    //need more work
    //after getting calories data, need to transfer to user database
    public void handleNameAndWeight(String foodName, float foodWeight)
    {
        foodInfo = new FoodInfo();
        foodInfo.setFoodWeight(foodWeight);
        foodInfo.setFoodName(foodName);
        this.foodWeight = foodWeight;
        this.foodName = foodName;

        Intent intent = new Intent(this, DatabaseManagementActivity.class);
        intent.putExtra("foodName",foodInfo.getFoodName());
        intent.putExtra("resultRequest",true);
        startActivityForResult(intent,2);
    }


}