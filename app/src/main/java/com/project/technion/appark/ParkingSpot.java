package com.project.technion.appark;

import java.sql.Time;

public class ParkingSpot {
    private Integer id;
    private User user;
    private double price;
    private XYLocation location;
    private TimeSlot timeSlot;

    public ParkingSpot(Integer id, User user, double price, XYLocation location, TimeSlot timeSlot) {
        this.id = id;
        this.timeSlot = timeSlot;
        this.user = user;
        this.price = price;
        this.location = location;
    }

    public Integer getId() {
        return this.id;
    }

    public TimeSlot getTimeSlot() {
        return this.timeSlot;
    }

//     public void subtractTimeSlot(TimeSlot otherTimeSlot) {
//        this.timeSlot = this.timeSlot.subtraction(otherTimeSlot);
//    }

    public User getUser() {
        return this.user;
    }

    public double getPrice() {
        return this.price;
    }

    public String toString() {
        return "ParkingSpot [id: " + id + "\n user: " + user + "\n price: " + price + "\n location: " + location + "\n TimeSlot: " + timeSlot + "]\n";
    }

    public XYLocation getLocation() {
        return this.location;
    }
}
