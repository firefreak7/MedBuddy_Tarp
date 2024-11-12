package com.example.medbuddy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private List<MedicineData> medicines;

    public MedicineAdapter(List<MedicineData> medicines) {
        this.medicines = medicines;
    }

    @Override
    public MedicineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medicine_layout, parent, false);
        return new MedicineViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MedicineViewHolder holder, int position) {
        MedicineData medicine = medicines.get(position);
        holder.textMedicineName.setText(medicine.getMedicineName());
        holder.textQuantity.setText("Quantity: " + medicine.getQuantity());

        // Handle Edit button click
        holder.buttonEditQuantity.setOnClickListener(v -> {
            // Show the input field and Save button, hide Edit/Delete buttons
            holder.layoutEditQuantity.setVisibility(View.VISIBLE);
            holder.buttonEditQuantity.setVisibility(View.GONE);
            holder.buttonDelete.setVisibility(View.GONE);
        });

        // Handle Save button click
        holder.buttonSaveQuantity.setOnClickListener(v -> {
            // Get new quantity from input field
            String newQuantityStr = holder.editTextNewQuantity.getText().toString();
            if (!newQuantityStr.isEmpty()) {
                int newQuantity = Integer.parseInt(newQuantityStr);
                updateQuantityInFirebase(medicine, newQuantity, holder);
            } else {
                Toast.makeText(v.getContext(), "Please enter a quantity", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Delete button click (implement delete logic)
        holder.buttonDelete.setOnClickListener(v -> {
            // Handle delete functionality here (remove from list and update Firebase)
        });
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    public static class MedicineViewHolder extends RecyclerView.ViewHolder {

        public TextView textMedicineName, textQuantity;
        public MaterialButton buttonEditQuantity, buttonDelete, buttonSaveQuantity;
        public LinearLayout layoutEditQuantity;  // Change this to LinearLayout
        public TextInputEditText editTextNewQuantity;

        public MedicineViewHolder(View itemView) {
            super(itemView);
            textMedicineName = itemView.findViewById(R.id.textMedicineName);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            buttonEditQuantity = itemView.findViewById(R.id.buttonEditQuantity);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            layoutEditQuantity = itemView.findViewById(R.id.layoutEditQuantity);  // Should reference LinearLayout, not TextInputLayout
            buttonSaveQuantity = itemView.findViewById(R.id.buttonSaveQuantity);
            editTextNewQuantity = itemView.findViewById(R.id.editTextNewQuantity);
        }


    }

    private void updateQuantityInFirebase(MedicineData medicine, int newQuantity, MedicineViewHolder holder) {
        // Get the user's email to reference in the Firebase path
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (userEmail == null) {
            Toast.makeText(holder.itemView.getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Replace "." with "," to match Firebase database structure
        String userKey = userEmail.replace(".", ",");
        String mname = medicine.getMedicineName();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MedicineData").child(userKey);

        // Fetch the medicine's current data (find it by medicine name or id)
        databaseReference.child(medicine.getMedicineName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the existing quantity
                int existingQuantity = medicine.getQuantity();

                // Calculate the new quantity
                int updatedQuantity = existingQuantity + newQuantity;

                // Update the database with the new quantity
                dataSnapshot.getRef().child("quantity").setValue(updatedQuantity);
                dataSnapshot.getRef().child("timestamp").setValue(timestamp).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        // Update UI or notify that the update was successful
                        holder.textQuantity.setText("Quantity: " + updatedQuantity);
                        Toast.makeText(holder.itemView.getContext(), "Quantity updated successfully", Toast.LENGTH_SHORT).show();
                        holder.layoutEditQuantity.setVisibility(View.GONE);  // Hide the edit quantity UI
                    } else {
                        Toast.makeText(holder.itemView.getContext(), "Failed to update quantity", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(holder.itemView.getContext(), "Error updating quantity", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
