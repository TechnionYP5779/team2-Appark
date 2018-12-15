package com.project.technion.appark;

public class ParkingSpot {
    private String userId;
    private double price;
    private double lat;
    private double lng;

    public ParkingSpot(){

    }

    public ParkingSpot(String userId, double price,double lat,double lng) {
        this.userId = userId;
        this.price = price;
        this.lat = lat;
        this.lng = lng;

    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
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


}
