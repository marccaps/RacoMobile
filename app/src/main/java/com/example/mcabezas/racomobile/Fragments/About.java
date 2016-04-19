package com.example.mcabezas.racomobile.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mcabezas.racomobile.Connect.AndroidUtils;
import com.example.mcabezas.racomobile.R;
import com.squareup.picasso.Picasso;

/**
 * Created by mcabezas on 22/02/16.
 */
public class About extends Fragment {

    private static final String TAG = "About";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.about, container, false);

        return rootView;

    }
}
