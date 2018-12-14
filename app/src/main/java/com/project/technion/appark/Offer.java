package com.project.technion.appark;

public class Offer {
    private ParkingSpot parkingSPot;
    private TimeSlot timeSlot;

    public Offer(ParkingSpot parkingSpot, TimeSlot timeSlot) {
        this.parkingSPot = parkingSpot;
        this.timeSlot = timeSlot;
    }

    public ParkingSpot getParkingSPot() {
        return parkingSPot;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }
}
