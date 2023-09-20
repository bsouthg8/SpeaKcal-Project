package com.example.speakcalproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
            textView.setText(recognizedText);

            // Determine the category based on recognized text
            String category = "unknown";  // Default category
            if (recognizedText.toLowerCase().contains("breakfast")) {
                category = "breakfast";
            } else if (recognizedText.toLowerCase().contains("lunch")) {
                category = "lunch";
            } else if (recognizedText.toLowerCase().contains("dinner")) {
                category = "dinner";
            }

            // Save recognizedText to Firestore
            Map<String, Object> textEntry = new HashMap<>();
            textEntry.put("category", category);
            textEntry.put("content", recognizedText);

            String finalCategory = category;
            firestore.collection("SpeechToTextTests").add(textEntry)
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
        }
    }

}
