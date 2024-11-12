package com.example.medbuddy;
public class HelperClass {
    String name, age, sex, weight, height;

    // Default constructor
    public HelperClass() {
        // Default constructor required for calls to DataSnapshot.getValue(HelperClass.class)
    }

    // Constructor with all fields
    public HelperClass(String name, String age, String sex, String weight, String height) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.weight = weight;
        this.height = height;
    }

    // Setters for each field
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    // Getters for each field (you already have them implicitly)
    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getSex() {
        return sex;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeight() {
        return height;
    }
}
