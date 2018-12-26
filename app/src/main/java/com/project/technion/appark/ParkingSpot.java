package com.project.technion.appark;

import java.util.ArrayList;

public class ParkingSpot {
    public String userId;
    public double price; //TODO: change this to pph - price per hour
    public String address;
    public double lat;
    public double lng;
    public String id;
    public ArrayList<String> offers;
    private boolean show;

    public ParkingSpot(){
        offers = new ArrayList<>();
        this.show = true;
    }

    public ParkingSpot(String userId, double price, String address, double lat, double lng, String id) {
        this.userId = userId;
        this.price = price;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.id=id;
        offers = new ArrayList<>();
        this.show = true;

    }

    public ParkingSpot(String userId, double price, String address, double lat, double lng, String id, boolean show) {
        this.userId = userId;
        this.price = price;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.id=id;
        offers = new ArrayList<>();
        this.show = show;

    }

    public String getUserId() {
        return userId;
    }

    public double getPrice() {
        return price;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getOffers() {
        return offers;
    }

    public boolean isShow() {
        return show;
    }
}
