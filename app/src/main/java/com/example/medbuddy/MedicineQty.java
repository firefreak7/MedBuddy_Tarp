package com.example.medbuddy;

public class MedicineQty {
    private String name;
    private int quantity;

    // Default constructor required for Firebase
    public MedicineQty() {}

    public MedicineQty(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
