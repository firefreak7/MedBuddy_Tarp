package com.example.medbuddy;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            // Handle Home navigation
            // For example, replace the fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            return true;
        } else if (item.getItemId() == R.id.nav_scan) {
            // Handle Scan navigation
            // For example, replace the fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ScanFragment())
                    .commit();
            return true;
        } else if (item.getItemId() == R.id.nav_chatbot) {
            // Handle Chatbot navigation
            // For example, replace the fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ChatbotFragment())
                    .commit();
            return true;
        } else if (item.getItemId() == R.id.nav_inventory) {
            // Handle Inventory navigation
            // For example, replace the fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new YourMedicine())
                    .commit();
            return true;
        } else {
            return false;
        }
    }
}
