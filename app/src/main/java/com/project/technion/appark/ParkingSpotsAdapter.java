package com.project.technion.appark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ParkingSpotsAdapter extends ArrayAdapter<ParkingSpot> {
    public ParkingSpotsAdapter(Context context, ArrayList<ParkingSpot> parkingSpots){
        super(context,0,parkingSpots);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ParkingSpot parkingSpot = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.parking_spots_list_item, parent, false);
        }
        TextView textViewLocation = convertView.findViewById(R.id.textView_location);
        TextView textViewPrice = convertView.findViewById(R.id.textView_price);
        textViewLocation.setText(parkingSpot.getLocation().toString());
        textViewPrice.setText(parkingSpot.getPrice()+" $");
        return convertView;
    }
}