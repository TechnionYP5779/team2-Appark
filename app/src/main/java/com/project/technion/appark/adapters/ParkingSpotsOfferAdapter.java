package com.project.technion.appark.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.project.technion.appark.DummyParkingSpot;
import com.project.technion.appark.Offer;
import com.project.technion.appark.R;

import java.util.ArrayList;


public class ParkingSpotsOfferAdapter extends ArrayAdapter<Offer> {
    public ParkingSpotsOfferAdapter(Context context, ArrayList<Offer> offers){
        super(context,0, offers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Offer offer = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.parking_spots_offers_list_item, parent, false);
        }
        TextView textViewStartTime = convertView.findViewById(R.id.start_time_text_view);
        TextView textViewEndTime = convertView.findViewById(R.id.end_time_text_view);
        textViewStartTime.setText(offer.getStartTime().toString());
        textViewEndTime.setText(offer.getEndTime().toString());
        return convertView;
    }
}