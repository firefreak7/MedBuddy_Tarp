package com.example.medbuddy;
import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {

    private TextView hellotxt;
    private TextView name;
    private ImageButton profileButton;

    // Firebase variables
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firebase components
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize UI components
        hellotxt = view.findViewById(R.id.hellotxt);
        name = view.findViewById(R.id.name);
        profileButton = view.findViewById(R.id.rounded_image_button);

        // Fetch the username from Firebase
        if (currentUser != null) {
            fetchUsernameFromFirebase(currentUser.getEmail());
        }

        // Set up Profile Button to open ProfileActivity on click
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        // Initialize TabLayout and ViewPager2
        // Directly load the UpcomingFragment into the container
        getChildFragmentManager().beginTransaction()
                .replace(R.id.container, new UpcomingFragment()) // Replace with UpcomingFragment
                .commit();

        return view;
    }

    // Method to fetch the username from Firebase Realtime Database
    @SuppressLint("RestrictedApi")
    private void fetchUsernameFromFirebase(String userEmail) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // Get the email of the current user
            String ema = firebaseUser.getEmail();

            if (userEmail != null) {
                // Replace '.' with ',' to create the Firebase path for the user
                String userKey = userEmail.replace(".", ",");

                // Reference to the user's data in Firebase Realtime Database
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userKey);

                // Fetch the current user's name from the database
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Assuming the name is stored under the "name" key in the database
                        String userName = dataSnapshot.child("name").getValue(String.class);

                        // Log the user's name
                        name.setText(userName);
                        Log.d("TAG", "fetchUsernameFromFirebase: User Name: " + userName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle the error (e.g., if the user data cannot be fetched)
                        Log.e("TAG", "Error fetching user data", databaseError.toException());
                    }
                });
            } else {
                Log.e("TAG", "User is not authenticated.");
            }
            Log.d(TAG, "fetchUsernameFromFirebase: " + userEmail);
        }
    }
}
