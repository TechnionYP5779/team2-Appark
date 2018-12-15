package com.project.technion.appark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class OffersAdapter extends ArrayAdapter<DummyParkingSpot> {
    public OffersAdapter(Context context, ArrayList<DummyParkingSpot> dummyParkingSpots){
        super(context,0, dummyParkingSpots);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DummyParkingSpot dummyParkingSpot = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.all_offers_list_item, parent, false);
        }
        TextView textViewLocation = convertView.findViewById(R.id.textView_location);
        TextView textViewPrice = convertView.findViewById(R.id.textView_price);
        textViewLocation.setText(dummyParkingSpot.getLocation().toString());
        textViewPrice.setText(dummyParkingSpot.getPrice()+" $");
        return convertView;
    }
}