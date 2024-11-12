package com.example.medbuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.medbuddy.R;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        // Set the extracted text to a TextView
        TextView resultTextView = view.findViewById(R.id.result_text_view);
        resultTextView.setText(extractedText);

        return view;
    }
}
