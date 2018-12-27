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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.technion.appark.Maps.GPSTracker;
import com.project.technion.appark.Offer;
import com.project.technion.appark.SortingBy;
import com.project.technion.appark.activities.MasterActivity;
import com.project.technion.appark.adapters.OffersAdapter;
import com.project.technion.appark.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ViewAllOffersFragment extends Fragment {

    private static final String TAG = "ViewAllOffersFragment";
    private ListView mListView;

    private DatabaseReference mDatabaseReference;
    private View mRootView;
    private static SortingBy lastSortMethod = SortingBy.DISTANCE_LOWEST; // default sorting method
    private static MasterActivity masterActivity;
    // Declare Context variable at class level in Fragment
    private Context mContext;

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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        View rootView = inflater.inflate(R.layout.fragment_view_all_offers, container, false);
        mRootView = rootView;
        setup(lastSortMethod);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public void setup(SortingBy sortingMethod) {
        lastSortMethod = sortingMethod;
        final View rootView = mRootView;
        mListView = rootView.findViewById(R.id.list_view);

        final ProgressBar progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        TextView noOffers = rootView.findViewById(R.id.textView_no_offers);
        noOffers.setVisibility(View.INVISIBLE);


        mDatabaseReference.child("Offers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Offer> offers = new ArrayList<>();
                for (DataSnapshot offer : dataSnapshot.getChildren()) {
                    Offer offerItem = offer.getValue(Offer.class);
                    if (Objects.requireNonNull(offerItem).isShow() && Calendar.getInstance().getTimeInMillis() < offerItem.startCalenderInMillis) {
                        offers.add(offerItem);
                    }
                }

                offers = sortOffers(sortingMethod, offers);

                progressBar.setVisibility(View.INVISIBLE);
                if (getContext() != null) {
                    if (offers.size() == 0) {
                        rootView.findViewById(R.id.textView_no_offers).setVisibility(View.VISIBLE);
                    } else {
                        OffersAdapter mAdapter = new OffersAdapter(getContext(), new ArrayList<>(offers));
                        mListView.setAdapter(mAdapter);
                        rootView.findViewById(R.id.textView_no_offers).setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private List<Offer> sortOffers(SortingBy sortingMethod, List<Offer> offers) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return offers;
        }

        switch (sortingMethod) {
            case DISTANCE_LOWEST:
                return offers.stream().sorted((offer1, offer2) -> {
                    int distDiff = calcDistDiffOffers(offer1, offer2);
                    if (distDiff != 0) return distDiff;
                    return (int) (offer1.price - offer2.price);
                }).collect(Collectors.toList());

            case DISTANCE_HIGHEST:
                return offers.stream().sorted((offer1, offer2) -> {
                    int distDiff = calcDistDiffOffers(offer2, offer1);
                    if (distDiff != 0) return distDiff;
                    return (int) (offer2.price - offer1.price);
                }).collect(Collectors.toList());
            case PRICE_LOWEST:
                return offers.stream().sorted((offer1, offer2) -> {
                    int priceDiff = (int) (offer1.price - offer2.price);
                    if (priceDiff != 0) return priceDiff;
                    return calcDistDiffOffers(offer1, offer2);
                }).collect(Collectors.toList());
            case PRICE_HiGHEST:
                return offers.stream().sorted((offer1, offer2) -> {
                    int priceDiff = (int) (offer2.price - offer1.price);
                    if (priceDiff != 0) return priceDiff;
                    return calcDistDiffOffers(offer2, offer1);
                }).collect(Collectors.toList());
        }
        return offers;
    }

    private int calcDistDiffOffers(Offer offer1, Offer offer2) {
        Location currentLocation = new GPSTracker(mContext).getLocation();
        Location locationOffer1 = new Location("");
        locationOffer1.setLatitude(offer1.lat);
        locationOffer1.setLongitude(offer1.lng);
        float dist1 = currentLocation.distanceTo(locationOffer1) / 1000;
        Location locationOffer2 = new Location("");
        locationOffer2.setLatitude(offer2.lat);
        locationOffer2.setLongitude(offer2.lng);
        float dist2 = currentLocation.distanceTo(locationOffer2) / 1000;
        return (int) (dist1 - dist2);
    }

}
