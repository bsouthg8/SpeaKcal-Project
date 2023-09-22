package com.example.speakcalproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.speakcalproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SpeechToTextFragment extends Fragment {

    private TextView textView;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_speech_to_text, container, false);

        firestore = FirebaseFirestore.getInstance();

        textView = rootView.findViewById(R.id.textView);
        Button speakButton = rootView.findViewById(R.id.speakButton);
        speakButton.setOnClickListener(v -> {
            // Create an AlertDialog to show the instructions
            new AlertDialog.Builder(getContext())
                    .setTitle("Instructions")
                    .setMessage("Please format your speech as: 'breakfast: ...', 'lunch: ...' or 'dinner: ...'.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        // Start the speech recognition after pressing OK
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start speaking now");
                        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                    })
                    .show();
        });


        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == getActivity().RESULT_OK) {
            String recognizedText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);

            // Remove instances of "breakfast", "lunch", and "dinner" from the recognizedText
            recognizedText = recognizedText.replace("breakfast", "").replace("lunch", "").replace("dinner", "").trim();

            textView.setText(recognizedText);

            // Determine the category based on the original recognized text
            String category = "unknown";  // Default category
            if (data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0).toLowerCase().contains("breakfast")) {
                category = "breakfast";
            } else if (data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0).toLowerCase().contains("lunch")) {
                category = "lunch";
            } else if (data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0).toLowerCase().contains("dinner")) {
                category = "dinner";
            }

            // Setup and show the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Confirm Entry");
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_speech_entry, null);
            builder.setView(dialogView);

            Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.food_categories, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);
            // Set default selected value based on recognized text
            int defaultPosition = adapter.getPosition(category);
            spinnerCategory.setSelection(defaultPosition);

            EditText editRecognizedContent = dialogView.findViewById(R.id.editRecognizedContent);
            editRecognizedContent.setText(recognizedText);

            EditText editCalories = dialogView.findViewById(R.id.editCalories);

            builder.setPositiveButton("Save", (dialog, which) -> {
                // Save the data to Firestore
                String finalCategory = spinnerCategory.getSelectedItem().toString();
                String finalContent = editRecognizedContent.getText().toString();
                String caloriesInput = editCalories.getText().toString();

                // Save recognizedText to Firestore
                Map<String, Object> textEntry = new HashMap<>();
                textEntry.put("category", finalCategory);
                textEntry.put("content", finalContent);
                textEntry.put("calories", caloriesInput);

                firestore.collection("Food").add(textEntry)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(getContext(), "Saved to database under " + finalCategory, Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to save", Toast.LENGTH_LONG).show();
                            }
                        });

            });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        }
    }

}
