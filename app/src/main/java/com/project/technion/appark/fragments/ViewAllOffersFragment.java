package com.project.technion.appark.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.project.technion.appark.DataBase;
import com.project.technion.appark.DummyDB;
import com.project.technion.appark.ParkingSpot;
import com.project.technion.appark.ParkingSpotsAdapter;
import com.project.technion.appark.R;
import com.project.technion.appark.activities.SearchParkingsActivity;
import java.util.ArrayList;

public class ViewAllOffersFragment extends Fragment {

    private static final String  TAG = "ViewAllOffersActivity";
    private DataBase db;
    private ListView mListView;
    private ParkingSpotsAdapter mAdapter;
    public ViewAllOffersFragment() {
    }

    public static ViewAllOffersFragment newInstance() {
        ViewAllOffersFragment fragment = new ViewAllOffersFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_all_offers, container, false);
        setup(rootView);

        return rootView;
    }

    public void setup(View rootView){
        mListView = rootView.findViewById(R.id.list_view);

        db = DummyDB.getInstance();
        ArrayList<ParkingSpot> spots = new ArrayList<>(db.getAllParkingSpot());
        mAdapter = new ParkingSpotsAdapter(getContext(), spots);
        mListView.setAdapter(mAdapter);
        TextView noOffers = rootView.findViewById(R.id.textView_no_offers);
        if(spots.size() == 0){
            noOffers.setVisibility(View.VISIBLE);
        }
        else{
            noOffers.setVisibility(View.INVISIBLE);
        }
    }
}
