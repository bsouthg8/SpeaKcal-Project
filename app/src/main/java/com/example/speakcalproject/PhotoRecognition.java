package com.example.speakcalproject;

import static android.provider.MediaStore.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.graphics.Bitmap;

import com.example.speakcalproject.ml.LiteModelAiyVisionClassifierFoodV11;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class PhotoRecognition extends AppCompatActivity {

    public Button camera,gallery;
    public ImageView imageView;
    public TextView result;

    private float foodWeight;
    private String foodName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_recognition);

        camera = findViewById(R.id.button);
        gallery = findViewById(R.id.button2);
        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 3){
                Bitmap image= (Bitmap) data.getExtras().get("data");
                int dimension = Math.min(image.getHeight(),image.getWidth());
                image = ThumbnailUtils.extractThumbnail(image,dimension,dimension);
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image,224,224,false);
                classifyImage(image);
            }else{
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
            } else {
                output += "Item: "+probability.get(maxPos).getLabel();
            }
            foodName = probability.get(maxPos).getLabel();
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
            View dialogView = inflater.inflate(R.layout.dialog_weight_name,null);
            builder.setView(dialogView);

            final EditText nameEditText = dialogView.findViewById(R.id.editTextName);
            final EditText weightText = dialogView.findViewById(R.id.editTextWeight);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String name = nameEditText.getText().toString();
                    String weight = weightText.getText().toString();
                    float weight_number = Float.parseFloat(weight);

                    handleNameandWeight(name,weight_number);
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
            View dialogView = inflater.inflate(R.layout.dialog_weight_name,null);
            builder.setView(dialogView);

            final EditText nameEditText = dialogView.findViewById(R.id.editTextName);
            final EditText weightText = dialogView.findViewById(R.id.editTextWeight);
            nameEditText.setVisibility(View.GONE);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String weight = weightText.getText().toString();
                    float weight_number = Float.parseFloat(weight);

                    handleNameandWeight(foodName,weight_number);
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

    public void handleNameandWeight(String foodName, float foodWeight)
    {
       this.foodWeight = foodWeight;
       this.foodName = foodName;
       result.setText("Item: "+this.foodName+"\nWeight: "+this.foodWeight+" g");
    }
}