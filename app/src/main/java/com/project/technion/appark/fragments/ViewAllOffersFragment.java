package com.project.technion.appark.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.technion.appark.DataBase;
import com.project.technion.appark.DummyDB;
import com.project.technion.appark.DummyParkingSpot;
import com.project.technion.appark.Offer;
import com.project.technion.appark.User;
import com.project.technion.appark.adapters.OffersAdapter;
import com.project.technion.appark.R;
import com.project.technion.appark.adapters.ParkingSpotsAdapter;

import java.util.ArrayList;

public class ViewAllOffersFragment extends Fragment {

    private static final String  TAG = "ViewAllOffersActivity";
    private ListView mListView;
    private OffersAdapter mAdapter;

    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

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
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        View rootView = inflater.inflate(R.layout.fragment_view_all_offers, container, false);
        setup(rootView);

        return rootView;
    }

    public void setup(final View rootView){
        mListView = rootView.findViewById(R.id.list_view);

//        ArrayList<DummyParkingSpot> spots = new ArrayList<>(db.getAllParkingSpot());
//        mAdapter = new OffersAdapter(getContext(), spots);
//        mListView.setAdapter(mAdapter);
//        TextView noOffers = rootView.findViewById(R.id.textView_no_offers);
//        if(spots.size() == 0){
//            noOffers.setVisibility(View.VISIBLE);
//        }
//        else{
//            noOffers.setVisibility(View.INVISIBLE);
//        }

        mDatabaseReference.child("Offers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Offer> offers = new ArrayList<>();
                for(DataSnapshot offer : dataSnapshot.getChildren()){
                    offers.add(offer.getValue(Offer.class));
                }
                mAdapter = new OffersAdapter(getContext(), new ArrayList<>(offers));
                mListView.setAdapter(mAdapter);
                TextView noOffers = rootView.findViewById(R.id.textView_no_offers);
                if(offers.size() == 0){
                    noOffers.setVisibility(View.VISIBLE);
                }
                else{
                    noOffers.setVisibility(View.INVISIBLE);
                }

//                Toast.makeText(MasterActivity.this, u.getName() +" "+u.getContactInfo(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
