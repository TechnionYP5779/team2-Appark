package com.project.technion.appark.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.project.technion.appark.activities.ParkingSpotActivity;
import com.project.technion.appark.utils.Constants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ParkingSpotsAdapter extends ArrayAdapter<ParkingSpot> {

    private FirebaseUser mUser;
    private DatabaseReference mDB;
    private StorageReference mStorageRef;


    public ParkingSpotsAdapter(Context context, ArrayList<ParkingSpot> parkingSpots) {
        super(context, 0, parkingSpots);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference();
        mUser = mAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final ParkingSpot parkingSpot = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_parkings_list_item, parent, false);
        }
        TextView textViewLocation = convertView.findViewById(R.id.textView_location);
        TextView textViewPrice = convertView.findViewById(R.id.textView_price);
        textViewLocation.setText(Objects.requireNonNull(parkingSpot).address);
        textViewPrice.setText(parkingSpot.price + " " + Constants.CURRENCY);

        Button button = convertView.findViewById(R.id.button_make_offer);
        button.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), ParkingSpotActivity.class);
            i.putExtra("parking_spot_id", parkingSpot.getId());
            getContext().startActivity(i);
        });

        Button edit_button = convertView.findViewById(R.id.button_edit_parking_spot);
        edit_button.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
            builder.setTitle("Edit or Delete?");
            builder.setMessage("Do you want to EDIT or DELETE this parking spot?");
            builder.setPositiveButton("EDIT", (dialog, which) -> mDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Intent i = new Intent(getContext(), EditParkingSpotActivity.class);
                    i.putExtra("parking_spot_id", parkingSpot.getId());
                    getContext().startActivity(i);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            }));
            builder.setNegativeButton("DELETE", (dialog, which) -> mDB.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                            builder.setTitle("Delete?");
                            builder.setMessage("Are you sure you want to DELETE this parking spot?");
                            builder.setPositiveButton("YES", (dialog, which) -> mDB.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User u = dataSnapshot.child("Users").child(mUser.getUid()).getValue(User.class);
                                    List<ParkingSpot> parkingSpotsList = Objects.requireNonNull(u).parkingSpots;
                                    Integer index = 0;
                                    ParkingSpot psToDelete = null;
                                    for (ParkingSpot ps : parkingSpotsList) {
                                        if (ps.getId().equals(parkingSpot.getId())) {
                                            psToDelete = ps;
                                            break;
                                        }
                                        index++;
                                    }

                                    if (psToDelete == null) {
                                        Toast.makeText(getContext(), "PROBLEM!", Toast.LENGTH_SHORT).show();
                                    }

                                    ParkingSpot newParkingSpot = new ParkingSpot(mUser.getUid(), Objects.requireNonNull(psToDelete).getPrice(),
                                            psToDelete.getAddress(), psToDelete.getLat(), psToDelete.getLng(),
                                            psToDelete.getId(), false);
                                    u.parkingSpots.set(index, newParkingSpot);
                                    markOffersAsDeleted(psToDelete.getId());
                                    mDB.child("Users").child(mUser.getUid()).setValue(u);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            }));
                            builder.setNegativeButton("NO", null);
                            builder.show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    })
            );
            builder.show();
        });

        final ImageView imageView = convertView.findViewById(R.id.imageView);

        StorageReference storageRef = mStorageRef.child("Images").child(mUser.getUid()).child(parkingSpot.id);
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//            Picasso.with(getContext()).load(uri.toString()).into(imageView);

            Picasso.with(getContext()).load(uri.toString())
                    .resize(100, 100)
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


            Log.d("tag", uri.toString());

        }).addOnFailureListener(exception -> Log.d("tag", "error " + exception.getMessage()));

        return convertView;
    }


    private void markOffersAsDeleted(String psID) {
        mDB.child("Offers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot offer : dataSnapshot.getChildren()) {
                    Offer offerItem = offer.getValue(Offer.class);
                    if (Objects.requireNonNull(offerItem).getParkingSpotId().equals(psID)) {
                        Offer o = new Offer(offerItem.getId(), offerItem.getParkingSpotId(), offerItem.getUserId(),
                                offerItem.getStartCalenderInMillis(), offerItem.getEndCalenderInMillis(),
                                offerItem.getLat(), offerItem.getLng(), offerItem.getPrice(), false);

                        mDB.child("Offers").child(Objects.requireNonNull(offer.getKey())).setValue(o);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

