package com.example.recylerequestapp.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class Item implements Serializable {

    private int item_id;
    private String item_name;
    private double price_per_kg;
    private transient double weightKg = 0.0;



    public Item(int item_id, String item_name, Double price_per_kg) {
        this.item_id = item_id;
        this.item_name = item_name;
        this.price_per_kg = price_per_kg;

    }

    // Getters
    public int getItem_id() { return item_id; }
    public String getItem_name() { return item_name; }
    public double getPrice_per_kg() { return price_per_kg; }
    public double getWeightKg() { return weightKg; }

    // Setters
    public void setItem_id(int item_id) { this.item_id = item_id; }
    public void setItem_name(String item_name) { this.item_name = item_name; }
    public void setPrice_per_kg(double price_per_kg) { this.price_per_kg = price_per_kg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    // Computation methods
    public double getTotalPrice() {
        return this.getWeightKg() * this.getPrice_per_kg();
    }



    public Item(String item_name, double price_per_kg) {
        this.item_name = item_name;
        this.price_per_kg = price_per_kg;

    }

}

