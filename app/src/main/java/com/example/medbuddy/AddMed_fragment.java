package com.example.medbuddy;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddMed_fragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private EditText editTextMedicineName, editTextQuantity, editTextDosage;
    private Button buttonMorningTime, buttonAfternoonTime, buttonEveningTime, buttonSave;
    private LinearLayout layoutMorningTime, layoutAfternoonTime, layoutEveningTime;
    private CheckBox checkBoxMorning, checkBoxAfternoon, checkBoxEvening;

    private String morningTime = null, afternoonTime = null, eveningTime = null;

    public AddMed_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("MedicineData");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_med_fragment, container, false);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            Log.d("AddMed_fragment", "Current user email: " + email);
        } else {
            Log.d("AddMed_fragment", "No user is currently logged in.");
        }

        // Initialize views
        editTextMedicineName = view.findViewById(R.id.editTextMedicineName);
        editTextQuantity = view.findViewById(R.id.editTextQuantity);
        editTextDosage = view.findViewById(R.id.editTextDosage);

        buttonMorningTime = view.findViewById(R.id.buttonMorningTime);
        buttonAfternoonTime = view.findViewById(R.id.buttonAfternoonTime);
        buttonEveningTime = view.findViewById(R.id.buttonEveningTime);
        buttonSave = view.findViewById(R.id.buttonSave);

        layoutMorningTime = view.findViewById(R.id.layoutMorningTime);
        layoutAfternoonTime = view.findViewById(R.id.layoutAfternoonTime);
        layoutEveningTime = view.findViewById(R.id.layoutEveningTime);

        checkBoxMorning = view.findViewById(R.id.checkBoxMorning);
        checkBoxAfternoon = view.findViewById(R.id.checkBoxAfternoon);
        checkBoxEvening = view.findViewById(R.id.checkBoxEvening);

        // Set listeners for time selection
        buttonMorningTime.setOnClickListener(v -> showTimePickerDialog(buttonMorningTime, "morning"));
        buttonAfternoonTime.setOnClickListener(v -> showTimePickerDialog(buttonAfternoonTime, "afternoon"));
        buttonEveningTime.setOnClickListener(v -> showTimePickerDialog(buttonEveningTime, "evening"));

        // Toggle time layouts based on checkbox
        toggleTimeLayout(checkBoxMorning, layoutMorningTime);
        toggleTimeLayout(checkBoxAfternoon, layoutAfternoonTime);
        toggleTimeLayout(checkBoxEvening, layoutEveningTime);

        buttonSave.setOnClickListener(v -> saveMedicineData());

        return view;
    }

    private void showTimePickerDialog(Button button, String timeOfDay) {
        int hour = 12;
        int minute = 0;

        TimePickerDialog timePicker = new TimePickerDialog(
                getContext(),
                (view, selectedHour, selectedMinute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    button.setText(time);
                    switch (timeOfDay) {
                        case "morning":
                            morningTime = time;
                            break;
                        case "afternoon":
                            afternoonTime = time;
                            break;
                        case "evening":
                            eveningTime = time;
                            break;
                    }
                },
                hour, minute, true
        );
        timePicker.show();
    }

    private void toggleTimeLayout(CheckBox checkBox, LinearLayout layout) {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> layout.setVisibility(isChecked ? View.VISIBLE : View.GONE));
    }

    private void saveMedicineData() {
        String medicineName = editTextMedicineName.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();
        String dosageStr = editTextDosage.getText().toString().trim();

        if (medicineName.isEmpty() || quantityStr.isEmpty() || dosageStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkBoxMorning.isChecked() && morningTime == null ||
                checkBoxAfternoon.isChecked() && afternoonTime == null ||
                checkBoxEvening.isChecked() && eveningTime == null) {
            Toast.makeText(getContext(), "Please select times for all checked dosages", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        double dosage = Double.parseDouble(dosageStr);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userEmailPath = currentUser.getEmail().replace(".", ","); // Replace dots for Firebase key compatibility
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            // Create and store medicine data
            MedicineData medicineData = new MedicineData(medicineName, quantity, dosage, morningTime, afternoonTime, eveningTime,timestamp);
            databaseReference.child(userEmailPath).child(medicineName).setValue(medicineData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Medicine data saved successfully", Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to save data", Toast.LENGTH_LONG).show());
        }
    }
}
