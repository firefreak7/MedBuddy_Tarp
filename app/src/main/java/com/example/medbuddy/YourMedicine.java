package com.example.medbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.card.MaterialCardView;

public class YourMedicine extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_yourmed, container, false);

        MaterialCardView cardAddMedicine = view.findViewById(R.id.cardAddMedicine);
        MaterialCardView cardViewMedicines = view.findViewById(R.id.cardViewMedicines);
        MaterialCardView cardScanPrescription = view.findViewById(R.id.cardScanPrescription);

        // Set an onClickListener for each card
        View.OnClickListener cardClickListener = v -> {
            Toast.makeText(getActivity(), "Navigating to Add Medicine", Toast.LENGTH_SHORT).show();
            openAddMedicineFragment();
        };
        View.OnClickListener viewmedlisten = v -> {
            Toast.makeText(getActivity(), "Navigating to Add Medicine", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getActivity(), AddMedicine.class);
//            startActivity(intent);
        };
        View.OnClickListener scanmedlisten = v -> {
            Toast.makeText(getActivity(), "Navigating to Add Medicine", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getActivity(), AddMedicine.class);
//            startActivity(intent);
        };

        cardAddMedicine.setOnClickListener(cardClickListener);
        cardViewMedicines.setOnClickListener(viewmedlisten);
        cardScanPrescription.setOnClickListener(scanmedlisten);

        return view;
    }
    private void openAddMedicineFragment() {
        // Begin a fragment transaction to replace this fragment with AddMedicineFragment
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new AddMed_fragment()); // Replace R.id.fragment_container with your container's ID
        transaction.addToBackStack(null); // Add to back stack to allow back navigation
        transaction.commit();
    }
}
