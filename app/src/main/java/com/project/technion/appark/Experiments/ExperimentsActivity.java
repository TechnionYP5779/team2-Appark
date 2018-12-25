package com.project.technion.appark.Experiments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;
import com.project.technion.appark.Offer;
import com.project.technion.appark.R;
import com.project.technion.appark.activities.MapsActivity;
import com.project.technion.appark.activities.OfferActivity;

import java.util.ArrayList;
import java.util.List;

public class ExperimentsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //private DatabaseReference mDatabaseReference;
    private static Location lastLocation = null;
    private Marker mCurrLocationMarker = null;
    private SupportMapFragment mapFrag = null;
    private final int zoomLevel = 18;

    private LocationManager locationManager;
    Button btnShowLocation;

    // GPSTracker class
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expriments);
        //mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fisheye_map);
        mapFrag.getMapAsync(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    101);
        }

        // Show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Create class object
                gps = new GPSTracker(ExperimentsActivity.this);

                // Check if GPS enabled
                if (gps.canGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoomLevel);
                    mMap.animateCamera(yourLocation);

                    // \n is for new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                } else {
                    // Can't get location.
                    // GPS or network is not enabled.
                    // Ask user to enable GPS/network in settings.
                    gps.showSettingsAlert();
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(31.771959, 35.217018)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(8.0f));

        if (lastLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(8.0f));
        }
        getMyLocation();

        /*mMap.setOnMarkerClickListener(marker -> {
            Offer offer = (Offer)marker.getTag();
            Intent i = new Intent(MapsActivity.this, OfferActivity.class);
            i.putExtra("lat", offer.lat);
            i.putExtra("lng", offer.lng);
            i.putExtra("price", offer.price);
            i.putExtra("userId", offer.userId);
            i.putExtra("offerId", offer.id);
            i.putExtra("PSID", offer.parkingSpotId);
            i.putExtra("startMillis", offer.startCalenderInMillis);
            i.putExtra("endMillis", offer.endCalenderInMillis);
            startActivity(i);
            return false;
        });*/

        /*mDatabaseReference.child("Offers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Offer> offers = new ArrayList<>();
                for (DataSnapshot offer : dataSnapshot.getChildren()) {
                    offers.add(offer.getValue(Offer.class));
                }

                for(Offer offer : offers){

                    IconGenerator icg = new IconGenerator(ExperimentsActivity.this);
                    Bitmap bm = icg.makeIcon(offer.price+" $");

                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(offer.lat, offer.lng)).icon(BitmapDescriptorFactory.fromBitmap(bm)));
                    marker.showInfoWindow();
                    marker.setTag(offer);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer("LUUcUMjlN6r8Fg7BnCu", "LUPEyDQhrKhgAgYFy2M", "uwOb4dkhhDXV00am0gTj4RHbzKB2", 1545645193337L, 1545645193337L, 32.7767783, 35.023127099999996, 100));
        for (Offer offer : offers) {

            IconGenerator icg = new IconGenerator(ExperimentsActivity.this);
            Bitmap bm = icg.makeIcon(offer.price + " $");

            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(offer.lat, offer.lng)).icon(BitmapDescriptorFactory.fromBitmap(bm)));
            marker.showInfoWindow();
            marker.setTag(offer);


        }
    }
    /*@Override
    public void onLocationChanged(Location location) {
        double lattitude = location.getLatitude();
        double longitude = location.getLongitude();

        //Place current location marker
        LatLng latLng = new LatLng(lattitude, longitude);


        if(mCurrLocationMarker!=null){
            mCurrLocationMarker.setPosition(latLng);
        }else{
            mCurrLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title("I am here"));
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        //stop location updates
    }*/


    private void getMyLocation() {

        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                lastLocation = location;

                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoomLevel);
                mMap.animateCamera(yourLocation);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("OMER","no permission");
            return;
        }
        //locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1L,(float)0.001,locationListener);

    }
}
