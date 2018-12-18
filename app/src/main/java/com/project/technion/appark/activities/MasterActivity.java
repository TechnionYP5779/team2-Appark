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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.technion.appark.R;
import com.project.technion.appark.fragments.ViewAllOffersFragment;
import com.project.technion.appark.fragments.ViewMyParkingSpotsFragment;
import com.project.technion.appark.fragments.ViewMyReservationFragment;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        permissionHandler();


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
                if(i==0) {
                    if(sortItem != null)
                        sortItem.setVisible(true);
                    searchFab.show();
                }
                else {
                    if(sortItem != null)
                        sortItem.setVisible(false);
                    searchFab.hide();
                }
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
        mFab.setOnClickListener(view -> startActivity(new Intent(MasterActivity.this, AddParkingSpotActivity.class)));

        mFab.hide();
        searchFab = findViewById(R.id.searchFab);
        searchFab.setOnClickListener(v -> startActivity(new Intent(MasterActivity.this, SearchParkingsActivity.class)));


        String[] sortMethods = {"by distance", "by price"};

        sortDialog = new AlertDialog.Builder(this);
        sortDialog.setTitle("Pick a sorting method");
        sortDialog.setItems(sortMethods, (dialog, index) -> {
            if(viewAllOffersFragment != null)
                viewAllOffersFragment.setup(index);
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
        if(id == R.id.action_sort){
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
            if(position==0) {
                viewAllOffersFragment = ViewAllOffersFragment.newInstance(MasterActivity.this);
                return viewAllOffersFragment;
            }
            else if (position == 1)
                return ViewMyReservationFragment.newInstance();
            else {
                return ViewMyParkingSpotsFragment.newInstance(MasterActivity.this);
            }
        }
        @Override
        public int getCount() {
            return 3;
        }
    }
}
