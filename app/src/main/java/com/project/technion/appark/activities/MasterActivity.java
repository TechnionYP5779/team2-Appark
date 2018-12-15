package com.project.technion.appark.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.technion.appark.ParkingSpot;
import com.project.technion.appark.R;
import com.project.technion.appark.User;
import com.project.technion.appark.fragments.ViewAllOffersFragment;

import java.io.IOException;
import java.util.List;

public class MasterActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private FloatingActionButton mFab;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        mAuth = FirebaseAuth.getInstance();

        //TODO: this prevents unauthorized users reach the master page:
        if(mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        mUser = mAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) { }

            @Override
            public void onPageSelected(int i) {
                if(i==2)
                    mFab.show();
                else
                    mFab.hide();
            }

            @Override
            public void onPageScrollStateChanged(int i) { }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseReference.child("Users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);


                        Geocoder gc = new Geocoder(getApplicationContext());
                        if(gc.isPresent()) {
                            List<Address> list = null;
                            try {
                                list = gc.getFromLocationName("155 Park Theater, Palo Alto, CA", 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Address address = list.get(0);
                            double lat = address.getLatitude();
                            double lng = address.getLongitude();

                            u.parkingSpots.add(new ParkingSpot(mUser.getUid(),10,lat,lng));

                            mDatabaseReference.child("Users").child(mUser.getUid()).setValue(u);
                        }



                        Toast.makeText(MasterActivity.this, u.getName() +" "+u.getContactInfo(), Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        mFab.hide();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_master, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            //meanwhile use this as logout
            mAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0)
                return ViewAllOffersFragment.newInstance();
            else if (position == 1)
                return ViewAllOffersFragment.newInstance();
            else
                return ViewAllOffersFragment.newInstance();
        }
        @Override
        public int getCount() {
            return 3;
        }
    }
}
