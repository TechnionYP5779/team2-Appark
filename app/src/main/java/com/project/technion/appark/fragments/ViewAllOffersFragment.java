package com.project.technion.appark.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.technion.appark.Offer;
import com.project.technion.appark.SortingBy;
import com.project.technion.appark.activities.MasterActivity;
import com.project.technion.appark.adapters.OffersAdapter;
import com.project.technion.appark.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ViewAllOffersFragment extends Fragment {

    private static final String TAG = "ViewAllOffersFragment";
    private ListView mListView;
    private OffersAdapter mAdapter;

    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private View mRootView;
    private static SortingBy lastSortMethod = SortingBy.DISTANCE_LOWEST; // default sorting method
    private static MasterActivity masterActivity;


    public ViewAllOffersFragment() {
    }


    public static ViewAllOffersFragment newInstance(MasterActivity masterActivity) {
        ViewAllOffersFragment fragment = new ViewAllOffersFragment();
        ViewAllOffersFragment.masterActivity = masterActivity;
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
        mRootView = rootView;
        setup(lastSortMethod);

        return rootView;
    }

    public void setup(SortingBy sortingMethod) {
        lastSortMethod = sortingMethod;
        final View rootView = mRootView;
        mListView = rootView.findViewById(R.id.list_view);
//        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//                if(masterActivity.tabPosition == 0) {
//                    int lastItem = firstVisibleItem + visibleItemCount;
//                    if (lastItem == totalItemCount) {
//
//                        masterActivity.searchFab.hide();
//                    } else {
//                        masterActivity.searchFab.show();
//                    }
//                }
//                else{
//                    masterActivity.searchFab.hide();
//                }
//            }
//        });
        mDatabaseReference.child("Offers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Offer> offers = new ArrayList<>();
                for (DataSnapshot offer : dataSnapshot.getChildren()) {
                    offers.add(offer.getValue(Offer.class));
                }


                offers = sortOffers(sortingMethod,offers);

                if (getContext() != null) {
                    mAdapter = new OffersAdapter(getContext(), new ArrayList<>(offers));
                    mListView.setAdapter(mAdapter);
                    TextView noOffers = rootView.findViewById(R.id.textView_no_offers);
                    if (offers.size() == 0) {
                        noOffers.setVisibility(View.VISIBLE);
                    } else {
                        noOffers.setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private List<Offer> sortOffers(SortingBy sortingMethod, List<Offer> offers){
        LocationManager locationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return offers;
        }
        Location locationCurrent = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        switch (sortingMethod) {
            case DISTANCE_LOWEST:
                return offers = offers.stream().sorted((offer1, offer2) -> {
                    Location locationOffer1 = new Location("");
                    locationOffer1.setLatitude(offer1.lat);
                    locationOffer1.setLongitude(offer1.lng);
                    float dist1 = locationCurrent.distanceTo(locationOffer1) / 1000;
                    Location locationOffer2 = new Location("");
                    locationOffer2.setLatitude(offer2.lat);
                    locationOffer2.setLongitude(offer2.lng);
                    float dist2 = locationCurrent.distanceTo(locationOffer2) / 1000;
                    return (int) (dist1 - dist2);
                }).collect(Collectors.toList());

            case DISTANCE_HIGHEST:
                return offers = offers.stream().sorted((offer1, offer2) -> {
                    Location locationOffer1 = new Location("");
                    locationOffer1.setLatitude(offer1.lat);
                    locationOffer1.setLongitude(offer1.lng);
                    float dist1 = locationCurrent.distanceTo(locationOffer1) / 1000;
                    Location locationOffer2 = new Location("");
                    locationOffer2.setLatitude(offer2.lat);
                    locationOffer2.setLongitude(offer2.lng);
                    float dist2 = locationCurrent.distanceTo(locationOffer2) / 1000;
                    return (int) (dist2 - dist1);
                }).collect(Collectors.toList());
            case PRICE_LOWEST:
                return offers = offers.stream().sorted((offer1, offer2) -> (int) (offer1.price - offer2.price)).collect(Collectors.toList());
            case PRICE_HiGHEST:
                return offers = offers.stream().sorted((offer1, offer2) -> (int) (offer2.price - offer1.price)).collect(Collectors.toList());
        }
        return offers;
    }
}
