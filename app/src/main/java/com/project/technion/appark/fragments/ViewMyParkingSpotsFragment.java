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
import com.project.technion.appark.adapters.ParkingSpotsAdapter;
import com.project.technion.appark.R;
import com.project.technion.appark.User;

import java.util.ArrayList;

public class ViewMyParkingSpotsFragment extends Fragment {

    private static final String  TAG = "ViewMyParkingSpotsFragment";
    private ListView mListView;
    private ParkingSpotsAdapter mAdapter;

    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;


    public ViewMyParkingSpotsFragment() {
    }

    public static ViewMyParkingSpotsFragment newInstance() {
        ViewMyParkingSpotsFragment fragment = new ViewMyParkingSpotsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        View rootView = inflater.inflate(R.layout.fragment_view_my_parking_spots, container, false);

        setup(rootView);

        return rootView;
    }

    public void setup(final View rootView){
        mListView = rootView.findViewById(R.id.list_view);
        mDatabaseReference.child("Users").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                if(getContext() != null) {
                    mAdapter = new ParkingSpotsAdapter(getContext(), new ArrayList<>(u.parkingSpots));
                    Log.d(TAG, u.parkingSpots.toString());
                    mListView.setAdapter(mAdapter);
                    TextView noOffers = rootView.findViewById(R.id.textView_no_offers);
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
