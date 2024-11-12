package com.example.medbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText signupName, signupAge, signupEmail, signupPassword, signupWeight, signupHeight;
    CheckBox maleCheckbox, femaleCheckbox, otherCheckbox;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        signupName = findViewById(R.id.signup_name);
        signupAge = findViewById(R.id.signup_age);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupWeight = findViewById(R.id.signup_weight);
        signupHeight = findViewById(R.id.signup_height);
        maleCheckbox = findViewById(R.id.male_checkbox);
        femaleCheckbox = findViewById(R.id.female_checkbox);
        otherCheckbox = findViewById(R.id.other_checkbox);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);
        // Initialize Firebase Auth and Database Reference
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = signupName.getText().toString();
                String age = signupAge.getText().toString();
                String email = signupEmail.getText().toString();
                String password = signupPassword.getText().toString();
                String weight = signupWeight.getText().toString();
                String height = signupHeight.getText().toString();
                String sex = getSelectedSex();

                // Validate input fields
                if (name.isEmpty() || age.isEmpty() || email.isEmpty() || password.isEmpty() || sex.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Register user with Firebase Auth
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Use name as the path for the node
                            DatabaseReference userRef = reference.child(name); // Use name as the child node
                            HelperClass helperClass = new HelperClass(name, age, sex, weight, height);

                            // Write to the database and listen for success/failure
                            userRef.setValue(helperClass)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this, "Sign-up successful!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                        } else {
                                            Toast.makeText(SignUpActivity.this, "Failed to update database!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(SignUpActivity.this, "Database error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Sign-up failed! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });




        // Redirect to Login Activity
        loginRedirectText.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        });
    }

    private String getSelectedSex() {
        if (maleCheckbox.isChecked()) return "Male";
        if (femaleCheckbox.isChecked()) return "Female";
        if (otherCheckbox.isChecked()) return "Other";
        return "";
    }
}
