package com.example.medbuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        // Fetch the username from Firebase
        if (currentUser != null) {
            fetchUsernameFromFirebase(currentUser.getEmail());
        }

        // Initialize TabLayout and ViewPager2
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager);

        // Set up the ViewPager with the adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Link the TabLayout with the ViewPager using TabLayoutMediator
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Upcoming");
                    break;
                case 1:
                    tab.setText("History");
                    break;
            }
        }).attach();

        return view;
    }

    // Method to fetch the username from Firebase Realtime Database
    private void fetchUsernameFromFirebase(String userEmail) {
        // Query the users node by iterating through all users to find a matching email
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean userFound = false;

                // Loop through all users
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    if (email != null && email.equals(userEmail)) {
                        // If email matches, fetch the username
                        String username = userSnapshot.child("username").getValue(String.class);

                        if (username != null) {
                            hellotxt.setText("Hello");
                            name.setText(username);
                        } else {
                            hellotxt.setText("Hello");
                            name.setText("User,");
                        }
                        userFound = true;
                        break;  // Exit loop after finding the correct user
                    }
                }

                // If no matching user is found
                if (!userFound) {
                    hellotxt.setText("Hello");
                    name.setText("User,");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible database errors
                hellotxt.setText("Hello");
                name.setText("Error fetching username.");
            }
        });
    }
}
