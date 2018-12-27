package com.project.technion.appark.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.technion.appark.ParkingSpot;
import com.project.technion.appark.activities.MasterActivity;
import com.project.technion.appark.adapters.ParkingSpotsAdapter;
import com.project.technion.appark.R;
import com.project.technion.appark.User;

import java.util.ArrayList;

public class ViewMyParkingSpotsFragment extends Fragment {

    private static final String TAG = "ViewMyParkingSpotsFragment";
    private ListView mListView;
    private ParkingSpotsAdapter mAdapter;

    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    static MasterActivity masterActivity;


    public ViewMyParkingSpotsFragment() {
    }

    public static ViewMyParkingSpotsFragment newInstance(MasterActivity masterActivity) {
        ViewMyParkingSpotsFragment fragment = new ViewMyParkingSpotsFragment();
        Bundle args = new Bundle();
        ViewMyParkingSpotsFragment.masterActivity = masterActivity;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        View rootView = inflater.inflate(R.layout.fragment_view_my_parking_spots, container, false);

        setup(rootView);

        return rootView;
    }

    public void setup(final View rootView) {
        mListView = rootView.findViewById(R.id.list_view);

        final ProgressBar progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        TextView noOffers = rootView.findViewById(R.id.textView_no_offers);
        noOffers.setVisibility(View.INVISIBLE);

        mDatabaseReference.child("Users").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                ArrayList<ParkingSpot> psToShow = new ArrayList<>();
                for (ParkingSpot ps : u.parkingSpots) {
                    if (ps.isShow()) {
                        psToShow.add(ps);
                    }
                }
                if (getContext() != null) {
                    mAdapter = new ParkingSpotsAdapter(getContext(), psToShow);
                    Log.d(TAG, u.parkingSpots.toString());
                    mListView.setAdapter(mAdapter);
                    TextView noOffers = rootView.findViewById(R.id.textView_no_offers);
                    progressBar.setVisibility(View.INVISIBLE);

                    if (u.parkingSpots.size() == 0) {
                        noOffers.setVisibility(View.VISIBLE);
                    } else {
                        noOffers.setVisibility(View.INVISIBLE);
                    }
                }

//                Toast.makeText(MasterActivity.this, u.getName() +" "+u.getContactInfo(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
