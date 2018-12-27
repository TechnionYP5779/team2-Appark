package com.project.technion.appark;

public class Reservation {
    public String id;
    public String sellerId;
    public String buyerId;
    public String parkingSpotId;
    public long startCalenderInMillis;
    public long endCalenderInMillis;

    public Reservation(){

    }

    public Reservation(String id, String sellerId, String buyerId, String parkingSpotId,
                       long startCalenderInMillis, long endCalenderInMillis) {
        this.id = id;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.parkingSpotId = parkingSpotId;
        this.startCalenderInMillis = startCalenderInMillis;
        this.endCalenderInMillis = endCalenderInMillis;
    }
}
