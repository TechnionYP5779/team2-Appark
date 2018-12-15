package com.project.technion.appark;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String contactInfo;

    public List<ParkingSpot> parkingSpots;

    public User() {
        parkingSpots = new ArrayList<>();
    }

    public User( String name, String contactInfo) {
        this.name = name;
        this.contactInfo = contactInfo;
        parkingSpots = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void update(User updatedUser) {
        this.name = updatedUser.getName();
        this.contactInfo = updatedUser.getContactInfo();
    }

    public String toString() {
        return "UserImplementation [ name=" + name + ", contactInfo=" + contactInfo + "]";
    }
}
