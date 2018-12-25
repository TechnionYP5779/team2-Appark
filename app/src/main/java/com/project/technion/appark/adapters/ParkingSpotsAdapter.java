package com.project.technion.appark.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.technion.appark.Offer;
import com.project.technion.appark.ParkingSpot;
import com.project.technion.appark.R;
import com.project.technion.appark.User;
import com.project.technion.appark.activities.EditParkingSpotActivity;
import com.project.technion.appark.activities.MasterActivity;
import com.project.technion.appark.activities.ParkingSpotActivity;
import com.project.technion.appark.activities.SearchParkingsActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ParkingSpotsAdapter extends ArrayAdapter<ParkingSpot> {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDB;
    private StorageReference mStorageRef;


    public ParkingSpotsAdapter(Context context, ArrayList<ParkingSpot> parkingSpots){
        super(context,0, parkingSpots);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ParkingSpot parkingSpot = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_parkings_list_item, parent, false);
        }
        TextView textViewLocation = convertView.findViewById(R.id.textView_location);
        TextView textViewPrice = convertView.findViewById(R.id.textView_price);
        textViewLocation.setText(parkingSpot.address);
        textViewPrice.setText(parkingSpot.price+" $");

        Button button = convertView.findViewById(R.id.button_make_offer);
        button.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), ParkingSpotActivity.class);
            i.putExtra("parking_spot_index", position);
            getContext().startActivity(i);
        });

        Button edit_button = convertView.findViewById(R.id.button_edit_parking_spot);
        edit_button.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), EditParkingSpotActivity.class);
            i.putExtra("parking_spot_index", position);
            getContext().startActivity(i);
        });

        final ImageView imageView = convertView.findViewById(R.id.imageView);

        StorageReference storageRef = mStorageRef.child("Images").child(mUser.getUid()).child(parkingSpot.id);
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//            Picasso.with(getContext()).load(uri.toString()).into(imageView);

            Picasso.with(getContext()).load(uri.toString())
                    .resize(100,100)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            imageView.setImageDrawable(imageDrawable);
                        }
                        @Override
                        public void onError() {
                            imageView.setImageResource(R.mipmap.ic_launcher);
                        }
                    });


            Log.d("tag",uri.toString());

        }).addOnFailureListener(exception -> {
            Log.d("tag","error "+exception.getMessage());
        });

        return convertView;
    }
}