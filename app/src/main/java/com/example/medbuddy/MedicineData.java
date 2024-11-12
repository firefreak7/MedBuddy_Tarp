package com.example.medbuddy;

public class MedicineData {
    public String medicineName;
    public int quantity;
    public double dosage;
    public String morningTime;
    public String afternoonTime;

    public String getAfternoonTime() {
        return afternoonTime;
    }

    public void setAfternoonTime(String afternoonTime) {
        this.afternoonTime = afternoonTime;
    }

    public double getDosage() {
        return dosage;
    }

    public void setDosage(double dosage) {
        this.dosage = dosage;
    }

    public String getEveningTime() {
        return eveningTime;
    }

    public void setEveningTime(String eveningTime) {
        this.eveningTime = eveningTime;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMorningTime() {
        return morningTime;
    }

    public void setMorningTime(String morningTime) {
        this.morningTime = morningTime;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

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
