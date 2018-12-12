package com.project.technion.appark;

import android.location.Location;

public class ParkingSpot {
    private Integer id;
//    private Availability availability;
    private User seller;
    private double price;
    private Location location;



    public ParkingSpot(Integer id, User seller, double price,Location location) {
        this.id = id;
//        this.availability = availability;
        this.seller = seller;
        this.price = price;
        this.location = location;
    }

    public Integer getId() {
        return this.id;
    }

//    public Availability getAvailability() {
//        return this.availability;
//    }

//     public void subtractAvailability(Availability otherAvailability) {
//        this.availability = this.availability.subtraction(otherAvailability);
//    }

     public User getSeller() {
        return this.seller;
    }

     public double getPrice() {
        return this.price;
    }

     public String toString() {
        return "ParkingSpotImplementation [id=" + id +  ", seller=" + seller + ", price=" + price + "]";
    }

     public Location getLocation() {
        return this.location;
    }
}
