package com.project.technion.appark.Maps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
import com.project.technion.appark.activities.OfferActivity;

import java.util.ArrayList;
import java.util.List;

public class GoogleMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabaseReference;
    private SupportMapFragment mapFrag = null;
    private final int zoomLevel = 16;
    private int currentBearing = 0;

    private LocationManager locationManager;
    ImageButton leftBearingButton;
    ImageButton rightBearingButton;

    // GPSTracker class
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fisheye_map);
        mapFrag.getMapAsync(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    101);
        }

        gps = new GPSTracker(GoogleMapsActivity.this);

        leftBearingButton = (ImageButton) findViewById(R.id.LeftBearing);
        // Show location button click event
        leftBearingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


                currentBearing = (currentBearing - 30)%360;
                if(currentBearing < 0){
                    currentBearing += 360;
                }

                CameraPosition cameraPosition = CameraPosition.builder().zoom(zoomLevel).target(mMap.getCameraPosition().target)
                        .tilt(90).bearing(currentBearing).build();
                CameraUpdate yourLocation = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.animateCamera(yourLocation);
            }
        });

        rightBearingButton = (ImageButton) findViewById(R.id.RightBearing);
        rightBearingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                currentBearing = (currentBearing + 30)%360;

                CameraPosition cameraPosition = CameraPosition.builder().zoom(zoomLevel).target(mMap.getCameraPosition().target)
                        .tilt(90).bearing(currentBearing).build();
                CameraUpdate yourLocation = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.animateCamera(yourLocation);
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
        mMap.getUiSettings().setAllGesturesEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        getMyLocation();

        mMap.setOnMarkerClickListener(marker -> {
            Offer offer = (Offer)marker.getTag();
            Intent i = new Intent(GoogleMapsActivity.this, OfferActivity.class);
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
        });

        mDatabaseReference.child("Offers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Offer> offers = new ArrayList<>();
                for (DataSnapshot offer : dataSnapshot.getChildren()) {
                    long currentTimeMillis = System.currentTimeMillis();
                    Offer gotOffer = offer.getValue(Offer.class);
                    if(gotOffer.startCalenderInMillis <= currentTimeMillis &&
                            gotOffer.endCalenderInMillis >= currentTimeMillis) {
                        offers.add(offer.getValue(Offer.class));
                    }
                }

                for(Offer offer : offers){

                    /*BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_red_dollar",130,130));
                    if(offer.price <= 10){
                        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_green_dollar",130,130));
                    }else if(offer.price <= 25){
                        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_yellow_dollar",130,130));
                    }

                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(offer.lat, offer.lng)).icon(bitmapDescriptor));
                    marker.showInfoWindow();
                    marker.setTag(offer);*/


                    IconGenerator icg = new IconGenerator(GoogleMapsActivity.this);
                    String dollar = "$";
                    if(offer.price > 10) {
                        dollar += "$";
                    }
                    if(offer.price > 25){
                        dollar += "$";
                    }

                    Bitmap bm = icg.makeIcon(dollar);

                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(offer.lat, offer.lng)).icon(BitmapDescriptorFactory.fromBitmap(bm)));
                    marker.showInfoWindow();
                    marker.setTag(offer);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void getMyLocation() {

        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                CameraPosition cameraPosition = CameraPosition.builder().zoom(zoomLevel).target(latLng)
                        .tilt(90).bearing(currentBearing).build();
                CameraUpdate yourLocation = CameraUpdateFactory.newCameraPosition(cameraPosition);
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
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);

    }
}
