package com.example.medbuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ResultFragment extends Fragment {

    private static final String ARG_EXTRACTED_TEXT = "extracted_text";

    private String extractedText;

    public ResultFragment() {
        // Required empty public constructor
    }

    // Factory method to create a new instance of this fragment
    public static ResultFragment newInstance(String extractedText) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EXTRACTED_TEXT, extractedText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            extractedText = getArguments().getString(ARG_EXTRACTED_TEXT);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        // Set the extracted text to a TextView
        TextView resultTextView = view.findViewById(R.id.result_text_view);
        resultTextView.setText(extractedText);

        // Handle 'Repredict' button click
        MaterialButton btnRepredict = view.findViewById(R.id.btn_repredict);
        btnRepredict.setOnClickListener(v -> {
            // Navigate back to the ScanFragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ScanFragment())  // Replace with ScanFragment
                    .addToBackStack(null)  // Add to back stack to allow back navigation
                    .commit();
        });

        // Handle 'Save Result' button click
        MaterialButton btnSaveResult = view.findViewById(R.id.btn_save_result);
        btnSaveResult.setOnClickListener(v -> saveResultToFirebase(extractedText));

        // Fetch and display prediction history
        fetchPredictionHistory(view);

        return view;
    }
    private void fetchPredictionHistory(View view) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail().replace(".", "_");

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("predictions").child(userEmail);

            databaseReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Map<String, Object> predictions = (Map<String, Object>) task.getResult().getValue();

                    if (predictions != null) {
                        LinearLayout historyLayout = view.findViewById(R.id.history_layout);
                        historyLayout.removeAllViews(); // Clear previous content

                        for (Map.Entry<String, Object> entry : predictions.entrySet()) {
                            Map<String, String> predictionData = (Map<String, String>) entry.getValue();
                            String result = predictionData.get("result");

                            // Create a new TextView for each prediction result
                            TextView predictionTextView = new TextView(getContext());
                            predictionTextView.setText(result);
                            predictionTextView.setTextSize(16);
                            predictionTextView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                            predictionTextView.setPadding(16, 16, 16, 16);
                            predictionTextView.setBackgroundResource(android.R.drawable.list_selector_background);

                            // Set click listener for expanding/collapsing
                            predictionTextView.setOnClickListener(v -> {
                                if (predictionTextView.getMaxLines() == Integer.MAX_VALUE) {
                                    predictionTextView.setMaxLines(3); // Collapse
                                } else {
                                    predictionTextView.setMaxLines(Integer.MAX_VALUE); // Expand
                                }
                            });

                            // Add the TextView to the LinearLayout
                            historyLayout.addView(predictionTextView);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load history: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveResultToFirebase(String result) {
        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Get the email of the current user and sanitize it (replace '.' with '_')
            String userEmail = currentUser.getEmail().replace(".", "_");

            // Create a reference to the 'predictions' node using the user's email
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("predictions").child(userEmail);

            // Create a unique ID for each prediction
            String predictionId = databaseReference.push().getKey();

            // Create a map of the result data (no date)
            Map<String, String> resultData = new HashMap<>();
            resultData.put("result", result);  // Only store the result

            // Save the data under the user's email and prediction ID
            if (predictionId != null) {
                databaseReference.child(predictionId).setValue(resultData)
                        .addOnSuccessListener(aVoid -> {
                            // Data saved successfully
                            Toast.makeText(getContext(), "Result saved successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // Failed to save data
                            Toast.makeText(getContext(), "Failed to save result: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        } else {
            Toast.makeText(getContext(), "No user signed in", Toast.LENGTH_SHORT).show();
        }
    }



    // Helper class to represent the Prediction data
    public static class Prediction {
        public String userName;
        public String date;
        public String result;

        public Prediction(String userName, String date, String result) {
            this.userName = userName;
            this.date = date;
            this.result = result;
        }

        // Default constructor for Firebase
        public Prediction() {}
    }
}
