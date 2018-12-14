package com.project.technion.appark.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.technion.appark.R;
import com.project.technion.appark.activities.MasterActivity;


public class ViewAllOffersFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public ViewAllOffersFragment() {
    }

    public static ViewAllOffersFragment newInstance(int sectionNumber) {
        ViewAllOffersFragment fragment = new ViewAllOffersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_master, container, false);
        TextView textView = rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }
}
