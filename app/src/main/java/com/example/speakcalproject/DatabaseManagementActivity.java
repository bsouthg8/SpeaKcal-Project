package com.example.speakcalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManagementActivity extends AppCompatActivity {
    private Button addToDataBase;
    private Button getFromDataBase;
    private TextView textview;
    private FirebaseFirestore ff;
    private EditText edittext;
    private String foodName;
    private Button sendDataBack;
    private Button showDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Food Database Management");

        FirebaseApp.initializeApp(getApplicationContext());
        ff = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_database_management);
        addToDataBase = (Button) findViewById(R.id.addToDataBase);
        getFromDataBase = (Button) findViewById(R.id.getFromDataBase);
        textview = (TextView) findViewById(R.id.textView);
        edittext = (EditText) findViewById(R.id.editText);
        sendDataBack = (Button) findViewById(R.id.sendDataBack);
        showDatabase = (Button) findViewById(R.id.showDataBase);

        showDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ff.collection("Food").get().addOnCompleteListener(task -> {

                    if(task.isSuccessful()) {
                        StringBuilder sb = new StringBuilder();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = "Name: "+document.getString("Name")+"\n";
                            String amount ="Amount: "+document.getString("Amount")+"\n";
                            sb.append(name+amount);
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        View dialogView = getLayoutInflater().inflate(R.layout.show_data_content_layout,null);
                        builder.setView(dialogView);
                        TextView textview = dialogView.findViewById(R.id.textview);
                        textview.setText(sb);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else {
                        displayDataInToast("Food Database is empty");
                    }
                });
            }
        });

        sendDataBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = textview.getText().toString();
                if (!input.isEmpty()){
                    String[] inputs = input.split(" ");
                    String amount = inputs[inputs.length-1];
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",amount);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                } else {
                    displayDataInToast("Input is null, press get from database first");
                }
            }
        });

        addToDataBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ff.collection("Food").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();

                            if(querySnapshot.isEmpty()){
                                ReadCsv rc = new ReadCsv();
                                rc.readCSV(getApplicationContext(),"food_database.csv");
                                for(FoodData fd : rc.returnList()) {
                                    writeFoodDataToDataBase(fd);
                                }
                            } else {
                                String input = edittext.getText().toString();
                                if(!input.isEmpty()){
                                    getFoodDataFromDataBase(input);
                                } else {
                                    displayDataInToast("Invalid input");
                                }
                            }
                        } else {

                        }
                    }
                });
            }
        });

        getFromDataBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodName="";
                foodName = edittext.getText().toString().toLowerCase();
                if(!foodName.isEmpty()) {
                    getFoodDataFromDataBase(foodName);
                } else {
                    displayDataInToast("Input is null");
                }
            }

        });

        //receive data from photo recognition
        Intent intent = getIntent();
        if(intent != null) {
            edittext.setText(intent.getStringExtra("foodName"));
            if(intent.getBooleanExtra("resultRequest",false)){
                addToDataBase.setEnabled(false);
                sendDataBack.setEnabled(false);
                textview.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        boolean isTextViewEmpty = s.toString().isEmpty();
                        sendDataBack.setEnabled(!isTextViewEmpty);
                    }
                    @Override
                    public void afterTextChanged(Editable s) {}
                });
            } else {
                sendDataBack.setEnabled(false);
            }
        }

    }


    public FoodData getFoodDataFromDataBase(String foodName)
    {
        FoodData fd = new FoodData();
        fd.setName(foodName);
        ff.collection("Food").whereEqualTo("Name",foodName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        fd.setAmount(document.getString("Amount"));
                        displayDataInToast(foodName+" is in the database\n"+"Successfully retrieve data "+fd.getName()+" from database");
                        textview.setText(fd.getName()+" "+fd.getAmount());
                    }

                    if(task.getResult().isEmpty()){
                        displayDataInToast("No matching data in database");

                        AlertDialog.Builder dialoagBuilder = new AlertDialog.Builder(DatabaseManagementActivity.this);
                        LayoutInflater inflater =DatabaseManagementActivity.this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialog_weight_name_calories,null);
                        dialoagBuilder.setView(dialogView);

                        final EditText nameEditText = dialogView.findViewById(R.id.editTextName);
                        final EditText weightText = dialogView.findViewById(R.id.editTextWeight);
                        final EditText caloriesText = dialogView.findViewById(R.id.editTextKcalAmount);
                        nameEditText.setVisibility(View.GONE);
                        weightText.setVisibility(View.GONE);

                        dialoagBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String amount = caloriesText.getText().toString();
                                if(TextUtils.isEmpty(amount)) {
                                    Toast.makeText(getApplicationContext(), "Invalid input", Toast.LENGTH_SHORT).show();
                                }else{
                                    fd.setAmount(amount);
                                    writeFoodDataToDataBase(fd);
                                }
                            }
                        });

                        dialoagBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        dialoagBuilder.show();


                    }
                } else {
                    displayDataInToast("Error");
                }
            }
        });


        return fd;
    }

    public void displayDataInToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }


    public void writeFoodDataToDataBase(FoodData fd) {

        Map<String, Object> food = new HashMap<>();
        food.put("Amount",fd.getAmount());
        food.put("Name",fd.getName());
        ff.collection("Food").document().set(food).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    displayDataInToast("Data "+fd.getName()+" write to database successfully");
                } else {
                    displayDataInToast("Error");
                }
            }
        });
    }
}