package com.project.technion.appark;

public class Offer {

    //    private TimeSlot timeSlot;
    public String parkingSpotId;
    public String userId;
    public long startCalenderInMillis;
    public long endCalenderInMillis;


    public Offer(String parkingSpotId, String userId
            , long startCalenderInMillis, long endCalenderInMillis) {
        this.parkingSpotId = parkingSpotId;
        this.userId = userId;
        this.startCalenderInMillis = startCalenderInMillis;
        this.endCalenderInMillis = endCalenderInMillis;
    }

}
