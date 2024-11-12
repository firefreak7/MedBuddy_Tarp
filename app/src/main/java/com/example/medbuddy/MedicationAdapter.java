package com.example.medbuddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.ViewHolder> {

    private List<MedicineData> medicineList;
    private static SharedPreferences sharedPreferences;

    public MedicationAdapter(List<MedicineData> medicineList, Context context) {
        this.medicineList = medicineList;
        this.sharedPreferences = context.getSharedPreferences("MedicationPrefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the card layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medication_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedicineData medicine = medicineList.get(position);

        // Set the medicine name and quantity
        holder.medicineName.setText(medicine.getMedicineName());
        holder.quantity.setText("Quantity: " + medicine.getQuantity());

        // Create a list of time slots
        List<TimeSlot> timeSlots = new ArrayList<>();
        String mtime = medicine.getMorningTime();
        if (mtime != null) {
            timeSlots.add(new TimeSlot("Morning Time", mtime, getCheckedState(medicine.getMedicineName(), "morning")));
        }

        String atime = medicine.getAfternoonTime();
        if (atime != null) {
            timeSlots.add(new TimeSlot("Afternoon Time", atime, getCheckedState(medicine.getMedicineName(), "afternoon")));
        }

        String etime = medicine.getEveningTime();
        if (etime != null) {
            timeSlots.add(new TimeSlot("Evening Time", etime, getCheckedState(medicine.getMedicineName(), "evening")));
        }

        // Bind the time slots to the ViewHolder
        holder.bindTimeSlots(timeSlots);
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    private boolean getCheckedState(String medicineName, String timeSlot) {
        return sharedPreferences.getBoolean(medicineName + "_" + timeSlot, false);
    }

    private static void saveCheckedState(String medicineName, String timeSlot, boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(medicineName + "_" + timeSlot, isChecked);
        editor.apply();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView medicineName, quantity;
        ViewGroup timeContainer;
        List<View> timeSlotViews;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineName = itemView.findViewById(R.id.medicineName);
            quantity = itemView.findViewById(R.id.quantity);
            timeContainer = itemView.findViewById(R.id.timeContainer);
            timeSlotViews = new ArrayList<>();
        }

        public void bindTimeSlots(List<TimeSlot> timeSlots) {
            timeContainer.removeAllViews();
            timeSlotViews.clear();

            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
            for (TimeSlot timeSlot : timeSlots) {
                View timeSlotView = inflater.inflate(R.layout.time_slot_item, timeContainer, false);

                TextView timeSlotText = timeSlotView.findViewById(R.id.timeSlotText);
                TextView timeText = timeSlotView.findViewById(R.id.time);
                checkBox = timeSlotView.findViewById(R.id.checkBox);

                timeSlotText.setText(timeSlot.getTimeSlot());
                timeText.setText(timeSlot.getTime());
                checkBox.setChecked(timeSlot.isChecked());

                // Handle checkbox state change
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    String medicineName = ((TextView) itemView.findViewById(R.id.medicineName)).getText().toString();
                    String timeSlotName = timeSlot.getTimeSlot().toLowerCase().replace(" time", "");
                    // Save the checked state to SharedPreferences
                    saveCheckedState(medicineName, timeSlotName, isChecked);
                });

                timeContainer.addView(timeSlotView);
                timeSlotViews.add(timeSlotView);
            }
        }
    }

    private static class TimeSlot {
        private String timeSlot;
        private String time;
        private boolean isChecked;

        public TimeSlot(String timeSlot, String time, boolean isChecked) {
            this.timeSlot = timeSlot;
            this.time = time;
            this.isChecked = isChecked;
        }

        public String getTimeSlot() {
            return timeSlot;
        }

        public String getTime() {
            return time;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }
}
