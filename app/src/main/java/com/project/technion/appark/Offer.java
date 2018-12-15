package com.project.technion.appark;

import java.util.Calendar;

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

    public Calendar getStartTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startCalenderInMillis);
        return cal;
    }

    public Calendar getEndTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(endCalenderInMillis);
        return cal;
    }

}
