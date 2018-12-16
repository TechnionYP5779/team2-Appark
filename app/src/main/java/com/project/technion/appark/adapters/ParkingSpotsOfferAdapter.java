package com.project.technion.appark.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.project.technion.appark.Offer;
import com.project.technion.appark.R;

import java.text.SimpleDateFormat;
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
        TextView textViewStartTime = convertView.findViewById(R.id.start_time);

        TextView textViewEndTime = convertView.findViewById(R.id.end_time);
        SimpleDateFormat start_format = new SimpleDateFormat("MMMM d, yyyy 'from' h:mm a");
        SimpleDateFormat end_format = new SimpleDateFormat("MMMM d, yyyy 'until' h:mm a");

        textViewStartTime.setText(start_format.format(offer.startTime().getTime()));
        textViewEndTime.setText(end_format.format(offer.endTime().getTime()));
        return convertView;
    }
}