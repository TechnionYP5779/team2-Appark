package com.project.technion.appark;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeSlot {
    private Calendar start;
    private Calendar end;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm",Locale.getDefault());

    private String DateToString(Calendar cal){
        return sdf.format(cal.getTime());
    }

    public TimeSlot(Calendar start, Calendar end) {
        this.start = start;
        this.end = end;
    }

    @NonNull
    public String toString() {
        return "Slot [start = " + DateToString(start) + ", end = " + DateToString(end) + "]";
    }

    public Calendar getStart() {
        return start;
    }

    public Calendar getEnd() {
        return end;
    }
}
