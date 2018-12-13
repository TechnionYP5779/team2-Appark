package com.project.technion.appark;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeSlot {
    private boolean available;
    private Calendar start;
    private Calendar end;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm",Locale.getDefault());

    private String DateToString(Calendar cal){
        return sdf.format(cal.getTime());
    }

    public TimeSlot(Calendar start, Calendar end, boolean available) {
        this.start = start;
        this.end = end;
        this.available = available;
    }

    @NonNull
    public String toString() {
        return "Slot [start = " + DateToString(start) + ", end = " + DateToString(end) + "]";
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar newStart) {
        this.start = newStart;
    }

    public Calendar getEnd() {
        return end;
    }

    public void setEnd(Calendar newEnd) {
        this.end = newEnd;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

//    public TimeSlot subtraction(TimeSlot otherTimeSlot) {
//    }
}
