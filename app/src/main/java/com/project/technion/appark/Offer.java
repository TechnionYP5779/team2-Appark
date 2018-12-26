package com.project.technion.appark;

import java.util.Calendar;

public class Offer {

    //    private TimeSlot timeSlot;
    public String id;
    public String parkingSpotId;
    public String userId;
    public long startCalenderInMillis;
    public long endCalenderInMillis;
    public double lat;
    public double lng;
    public double price;
    public boolean show;


    public Offer(){
        this.show = true;
    }

    public Offer(String id,String parkingSpotId, String userId
            , long startCalenderInMillis, long endCalenderInMillis, double lat, double lng, double price) {
        this.id = id;
        this.parkingSpotId = parkingSpotId;
        this.userId = userId;
        this.startCalenderInMillis = startCalenderInMillis;
        this.endCalenderInMillis = endCalenderInMillis;
        this.lat = lat;
        this.lng = lng;
        this.price = price;
        this.show = true;
    }

    public Offer(String id,String parkingSpotId, String userId
            , long startCalenderInMillis, long endCalenderInMillis, double lat, double lng,double price,
                 boolean show) {
        this.id = id;
        this.parkingSpotId = parkingSpotId;
        this.userId = userId;
        this.startCalenderInMillis = startCalenderInMillis;
        this.endCalenderInMillis = endCalenderInMillis;
        this.lat = lat;
        this.lng = lng;
        this.price = price;
        this.show = true;
        this.show = show;
    }

    public Calendar startTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startCalenderInMillis);
        return cal;
    }

    public Calendar endTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(endCalenderInMillis);
        return cal;
    }

    public String getId() {
        return id;
    }

    public String getParkingSpotId() {
        return parkingSpotId;
    }

    public String getUserId() {
        return userId;
    }

    public long getStartCalenderInMillis() {
        return startCalenderInMillis;
    }

    public long getEndCalenderInMillis() {
        return endCalenderInMillis;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public double getPrice() {
        return price;
    }

    public boolean isShow() {
        return show;
    }
}
