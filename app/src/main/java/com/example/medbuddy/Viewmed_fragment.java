package com.example.medbuddy;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Viewmed_fragment extends Fragment {

    private RecyclerView recyclerView;
    private MedicineAdapter medicineAdapter;
    private List<MedicineData> medicineList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("MedicineData");

        // Initialize medicine list
        medicineList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_viewmed_fragment, container, false);

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewMedicines);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the adapter
        medicineAdapter = new MedicineAdapter(medicineList);
        recyclerView.setAdapter(medicineAdapter);

        // Fetch data from Firebase
        fetchMedicinesFromFirebase();

        return view;
    }

    private void fetchMedicinesFromFirebase() {
        // Get the current user's email (use it as a key in Firebase)
        String userEmail = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : null;

        if (!TextUtils.isEmpty(userEmail)) {
            String userKey = userEmail.replace(".", ",");  // Firebase keys can't contain periods

            // Log user key for debugging
            Log.d("Viewmed_fragment", "User key: " + userKey);

            // Get the medicines from the database under the current user's email
            databaseReference.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Clear existing list
                    medicineList.clear();

                    if (!dataSnapshot.exists()) {
                        // Log if there are no medicines in the database
                        Log.d("Viewmed_fragment", "No medicines found for this user.");
                    }

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Log each medicine entry key and data to check what is being fetched
                        Log.d("Viewmed_fragment", "Medicine entry: " + snapshot.getKey() + " => " + snapshot.getValue());

                        // Parse the data into Medicine objects
                        MedicineData medicine = snapshot.getValue(MedicineData.class);
                        if (medicine != null) {
                            medicineList.add(medicine);
                        }
                    }

                    // Log the size of the medicine list
                    Log.d("Viewmed_fragment", "Medicines list size: " + medicineList.size());

                    // Notify the adapter that the data has changed
                    medicineAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database read error
                    Log.e("Viewmed_fragment", "Error fetching data: " + databaseError.getMessage());
                    Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("Viewmed_fragment", "User email is null or empty.");
        }
    }
}
