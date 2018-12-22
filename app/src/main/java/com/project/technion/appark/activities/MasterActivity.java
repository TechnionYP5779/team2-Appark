package com.project.technion.appark.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.technion.appark.R;
import com.project.technion.appark.SortingBy;
import com.project.technion.appark.fragments.ViewAllOffersFragment;
import com.project.technion.appark.fragments.ViewMyParkingSpotsFragment;
import com.project.technion.appark.fragments.ViewMyReservationFragment;

import java.util.Arrays;

import static android.support.v4.app.ActivityCompat.requestPermissions;

public class MasterActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public FloatingActionButton mFab, searchFab;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;

    AlertDialog.Builder sortDialog;
    MenuItem sortItem;
    private ViewAllOffersFragment viewAllOffersFragment;
    public int tabPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        permissionHandler();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // greet the user after logging in
        mUser = mAuth.getCurrentUser();
        Intent forIntent = getIntent();
        String previousActivity = forIntent.getStringExtra("FROM");
        if (previousActivity.equals("LOGIN") || previousActivity.equals("REGISTER")) {
            mDatabaseReference.child("Users").child(mUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child("name").getValue().toString();
                            Toast.makeText(getApplicationContext(), "Hello " + name + "!",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    if (sortItem != null)
                        sortItem.setVisible(true);
                    searchFab.show();
                } else {
                    if (sortItem != null)
                        sortItem.setVisible(false);
                    searchFab.hide();
                }
                if (i == 2)
                    mFab.show();
                else
                    mFab.hide();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(view -> startActivity(new Intent(MasterActivity.this, AddParkingSpotActivity.class)));

        mFab.hide();
        searchFab = findViewById(R.id.searchFab);
        searchFab.setOnClickListener(v -> startActivity(new Intent(MasterActivity.this, SearchParkingsActivity.class)));


        String[] sortMethodsEnum = Arrays.stream(SortingBy.values()).map(SortingBy::toString).toArray(String[]::new);

        sortDialog = new AlertDialog.Builder(this);
        sortDialog.setTitle("Pick a sorting method");
        sortDialog.setItems(sortMethodsEnum, (dialog, sortingMethodIndex) -> {
            if (viewAllOffersFragment != null) {
                SortingBy sortingMethod = SortingBy.values()[sortingMethodIndex];
                viewAllOffersFragment.setup(sortingMethod);
            }
        });
    }

    private void permissionHandler() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_master, menu);
        sortItem = menu.findItem(R.id.action_sort);

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
        if (id == R.id.action_sort) {
            sortDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                tabPosition = 0;
                viewAllOffersFragment = ViewAllOffersFragment.newInstance(MasterActivity.this);
                return viewAllOffersFragment;
            } else if (position == 1) {
                tabPosition = 1;
                return ViewMyReservationFragment.newInstance();
            } else {
                tabPosition = 2;
                return ViewMyParkingSpotsFragment.newInstance(MasterActivity.this);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
