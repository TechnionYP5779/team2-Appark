package com.project.technion.appark;

public class ParkingSpot {
    public String userId;
    public double price; //TODO: change this to pph - price per hour
    public String address;
    public double lat;
    public double lng;

    public ParkingSpot(){

    }

    public ParkingSpot(String userId, double price,String address,double lat,double lng) {
        this.userId = userId;
        this.price = price;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

}
