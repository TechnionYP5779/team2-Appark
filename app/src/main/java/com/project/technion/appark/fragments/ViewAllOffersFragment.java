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
import com.project.technion.appark.Offer;
import com.project.technion.appark.activities.MasterActivity;
import com.project.technion.appark.adapters.OffersAdapter;
import com.project.technion.appark.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViewAllOffersFragment extends Fragment {

    private static final String  TAG = "ViewAllOffersActivity";
    private ListView mListView;
    private OffersAdapter mAdapter;

    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private View mRootView;
    private static int lastSortIndex = -1;
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
        mRootView = rootView;
        setup(lastSortIndex);

        return rootView;
    }

    public void setup(int index){
        lastSortIndex = index;
        final View rootView = mRootView;
        mListView = rootView.findViewById(R.id.list_view);
        mDatabaseReference.child("Offers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Offer> offers = new ArrayList<>();
                for(DataSnapshot offer : dataSnapshot.getChildren()){
                    offers.add(offer.getValue(Offer.class));
                }
                Log.d("beebo", String.valueOf(index));

                if(index == 1){
                    Log.d("beebo", "toast");
                    offers = offers.stream().sorted((offer1, offer2) -> (int)(offer1.price - offer2.price)).collect(Collectors.toList());
                }

                if(getContext() != null) {
                    mAdapter = new OffersAdapter(getContext(), new ArrayList<>(offers));
                    mListView.setAdapter(mAdapter);
                    TextView noOffers = rootView.findViewById(R.id.textView_no_offers);
                    if (offers.size() == 0) {
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
