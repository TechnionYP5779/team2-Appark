package com.project.technion.appark;

public class Offer {

    private DummyParkingSpot dummyParkingSPot;
    private TimeSlot timeSlot;

    public Offer(DummyParkingSpot dummyParkingSpot, TimeSlot timeSlot) {
        this.dummyParkingSPot = dummyParkingSpot;
        this.timeSlot = timeSlot;
    }

    public DummyParkingSpot getDummyParkingSPot() {
        return dummyParkingSPot;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }
}
