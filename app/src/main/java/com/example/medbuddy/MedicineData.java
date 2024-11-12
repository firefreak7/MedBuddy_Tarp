package com.example.medbuddy;

public class MedicineData {
    public String medicineName;
    public int quantity;
    public double dosage;
    public String morningTime;
    public String afternoonTime;
    public String eveningTime;
    public String timestamp; // New field for storing date and time

    public MedicineData() {
        // Default constructor required for calls to DataSnapshot.getValue(MedicineData.class)
    }

    public MedicineData(String medicineName, int quantity, double dosage, String morningTime, String afternoonTime, String eveningTime, String timestamp) {
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.dosage = dosage;
        this.morningTime = morningTime;
        this.afternoonTime = afternoonTime;
        this.eveningTime = eveningTime;
        this.timestamp = timestamp;

    }
}
