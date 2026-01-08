package com.example.recylerequestapp.model;

import java.io.Serializable;

public class Request implements Serializable {

    private int request_id;
    private int item_id;
    private int user_id;
    private String address;
    private String status;
    private Double weight;
    private Double total_price;
    private String request_date;
    private String notes;
    private String item_name;
    private Item item;

    public Request() {
        // Required for Retrofit
    }

    // ✅ New constructor matching your addNewRequest() call
    public Request(int item_id, int user_id, Double weight, String address, String notes, String request_date, Double total_price) {
        this.item_id = item_id;
        this.user_id = user_id;
        this.weight = weight;
        this.address = address;
        this.notes = notes;
        this.status = "Pending"; // default status
        this.request_date = request_date;
        this.total_price = total_price;
    }

    // (Optional) Keep the old constructor if you need it elsewhere
    public Request(int item_id, int user_id, Double weight, String address, String notes, String request_date, Double total_price, int points) {
        this.item_id = item_id;
        this.user_id = user_id;
        this.weight = weight;
        this.address = address;
        this.notes = notes;
        this.status = "Pending";
        this.request_date = request_date;
        this.total_price = total_price;
    }

    public int getRequest_id() {
        return request_id;
    }

    public void setRequest_id(int request_id) {
        this.request_id = request_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(Double total_price) {
        this.total_price = total_price;
    }

    public String getRequest_date() {
        return request_date;
    }

    public void setRequest_date(String request_date) {
        this.request_date = request_date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
