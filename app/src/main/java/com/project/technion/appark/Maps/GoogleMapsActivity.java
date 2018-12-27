package com.project.technion.appark.Maps;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import com.project.technion.appark.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GoogleMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabaseReference;
    private SupportMapFragment mapFrag = null;
    private final int zoomLevel = 16;
    private int currentBearing = 0;
    private long timeFromLastSwipe;

    private LocationManager locationManager;
    // GPSTracker class
    GPSTracker gps;

    private float x1,x2;
    private float y1;
    static final int MIN_DISTANCE = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fisheye_map);
        mapFrag.getMapAsync(this);
        /*((View)findViewById(R.id.fisheye_map)).setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //Toast.makeText(GoogleMapsActivity.this, scrollX - oldScrollX, Toast.LENGTH_SHORT).show();
            }
        });*/

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    101);
        }

        gps = new GPSTracker(GoogleMapsActivity.this);


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();

                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                if(java.lang.System.currentTimeMillis()-timeFromLastSwipe > 700 && (y1 < 960 || y1 > 1160)) {
                    timeFromLastSwipe = java.lang.System.currentTimeMillis();
                    float deltaX = x2 - x1;
                    int sign = y1 < 960 ? -1 : 1;

                    int normalized = (int) deltaX / 6;
                    normalized = normalized < -180 ? -179 : normalized;
                    normalized = normalized > 180 ? 179 : normalized;
                    normalized *= sign;
                    currentBearing = (currentBearing + normalized) % 360;
                    if (currentBearing < 0) {
                        currentBearing += 360;
                    }

                    CameraPosition cameraPosition = CameraPosition.builder().zoom(zoomLevel).target(mMap.getCameraPosition().target)
                            .tilt(90).bearing(currentBearing).build();
                    CameraUpdate yourLocation = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    mMap.animateCamera(yourLocation);
                }
        }
        //findViewById(R.id.map_layout).onTouchEvent(event);
        return super.dispatchTouchEvent(event);
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
            Offer offer = (Offer) marker.getTag();
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
                //List<Offer> offers = new ArrayList<>();
                GPSTracker gps = new GPSTracker(GoogleMapsActivity.this);
                double lat = gps.getLatitude();
                double lon = gps.getLongitude();

                Map<Integer,Offer> map = new TreeMap<>();
                for (DataSnapshot offer : dataSnapshot.getChildren()) {
                    long currentTimeMillis = System.currentTimeMillis();
                    Offer gotOffer = offer.getValue(Offer.class);
                    if(gotOffer.isShow() && gotOffer.startCalenderInMillis <= currentTimeMillis &&
                            gotOffer.endCalenderInMillis >= currentTimeMillis) {

                        double offerLat = gotOffer.getLat();
                        double offerLng = gotOffer.getLng();



                        double dist = (offerLat-lat)*(offerLat-lat) + (offerLng - lon)*(offerLng - lon);

                        double dist2 = gotOffer.price*gotOffer.price;

                        if(dist*100000 <= 100) {

                            map.put((int) (dist * 100000 + dist2), gotOffer);
                        }
                    }
                }







                    /*BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_red_dollar",130,130));
                    if(offer.price <= 10){
                        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_green_dollar",130,130));
                    }else if(offer.price <= 25){
                        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_yellow_dollar",130,130));
                    }

                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(offer.lat, offer.lng)).icon(bitmapDescriptor));
                    marker.showInfoWindow();
                    marker.setTag(offer);*/



                    /*IconGenerator icg = new IconGenerator(GoogleMapsActivity.this);
                    String dollar = "$";
                    if(offer.price > 10) {
                        dollar += "$";
                    }
                    if(offer.price > 25){
                        dollar += "$";
                    }


                    Bitmap bm = icg.makeIcon(currency);

                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(offer.lat, offer.lng)).icon(BitmapDescriptorFactory.fromBitmap(bm)));
                    marker.showInfoWindow();
                    marker.setTag(offer);*/



                int size = map.size();
                int greenBarier = size/3;
                int yellowBarrier = 2*greenBarier;

                List<Offer> l = new ArrayList<>(map.values());

                for(int i =0; i<map.size(); i ++){
                    Offer offer = l.get(i);
                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_red_dollar",130,130));
                    if(i<=greenBarier){
                        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_green_dollar",150,150));
                    }else if(i <= yellowBarrier){
                        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_yellow_dollar",140,140));
                    }

                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(offer.lat, offer.lng)).icon(bitmapDescriptor));
                    marker.showInfoWindow();
                    marker.setTag(offer);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void getMyLocation() {

        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    /*@Override
    public boolean onTouchEvent(MotionEvent eve) {
        Point touchPoint = new Point();  //first point on screen the user's finger touches
        final GoogleMap map;
        final int action = MotionEventCompat.getActionMasked(eve);
        int pointerIndex = MotionEventCompat.getActionIndex(eve);
        GestureDetector g = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener());
        g.onTouchEvent(eve);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                // get the point the user's finger touches
                touchPoint.x =(int) MotionEventCompat.getX(eve,pointerIndex);
                touchPoint.y =(int) MotionEventCompat.getY(eve,pointerIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                if(eve.getPointerCount()<2) {   // leave two finger gestures for other actions
                    final Point newTouchPoint = new Point();  // the new position of user's finger on screen after movement is detected
                    newTouchPoint.x = (int) MotionEventCompat.getX(eve, pointerIndex);
                    newTouchPoint.y = (int) MotionEventCompat.getY(eve, pointerIndex);
                    Point centerOfMap = getCenterOfMapAsPoint();   // center of map(as Point object) for calculation of angle
                    // now you need to calculate the angle betwwen 2 lines with centerOfMap as center:
                    //line 1: imaginary line between first touch detection on screen - and the center of the map
                    //line 2: imaginary line between last place finger moved on screen - and the center of the map
                    final float angle = angleBetweenLines(centerOfMap, touchPoint, centerOfMap, newTouchPoint);
                    final LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());  //set camera movement to that position
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            // move the camera (NOT animateCamera() ) to new position with "bearing" updated
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(latlng).tilt(67.5f).zoom(map.getCameraPosition().zoom).bearing(map.getCameraPosition().bearing - angle).build()));
                        }
                    });
                    touchPoint = newTouchPoint; // update touchPoint value
                    return true;
                }else{
                    break;
                }
        }
        return true;
    }

    // convert center of map from Latln object to Point object
    public Point getCurrentLocation(){
        Projection projection = mMap.getProjection();
        return projection.toScreenLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }*/
}
