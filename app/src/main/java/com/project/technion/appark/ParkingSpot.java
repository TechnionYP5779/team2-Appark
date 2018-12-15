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

    public ParkingSpot(){
        offers = new ArrayList<>();
    }

    public ParkingSpot(String userId, double price,String address,double lat,double lng,String id) {
        this.userId = userId;
        this.price = price;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.id=id;
        offers = new ArrayList<>();
    }

}
