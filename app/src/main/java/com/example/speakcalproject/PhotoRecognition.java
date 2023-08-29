package com.example.speakcalproject;

import static android.provider.MediaStore.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
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

    Button camera,gallery;
    ImageView imageView;
    TextView result;
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
        Bitmap floatImage = Bitmap.createBitmap(224,224,Bitmap.Config.ARGB_8888);

        for(int x = 0; x < 224; x++){
            for (int y = 0; y < 224; y++){
                int pixel = bitmap.getPixel(x,y);
                int alpha = Color.alpha(pixel);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                floatImage.setPixel(x,y,Color.argb(alpha,(int) red,(int) green, (int) blue));
            }
        }

        try {
            LiteModelAiyVisionClassifierFoodV11 model = LiteModelAiyVisionClassifierFoodV11.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorImage image = TensorImage.fromBitmap(floatImage);

            // Runs model inference and gets result.
            LiteModelAiyVisionClassifierFoodV11.Outputs outputs = model.process(image);
            List<Category> probability = outputs.getProbabilityAsCategoryList();

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }
}