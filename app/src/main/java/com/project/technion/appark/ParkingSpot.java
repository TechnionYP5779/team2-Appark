package com.project.technion.appark;

public class ParkingSpot {
    private String userId;
    private double price;

    public ParkingSpot(){

    }

    public String getUserId() {
        return userId;
    }

    public double getPrice() {
        return price;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ParkingSpot(String userId, double price) {
        this.userId = userId;
        this.price = price;
    }
}
