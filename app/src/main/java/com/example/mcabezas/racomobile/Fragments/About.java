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

        ImageView mPictureCaps = (ImageView) rootView.findViewById(R.id.view);

        Picasso.with(getActivity())
                .load("https://media.licdn.com/mpr/mpr/shrinknp_200_200/AAEAAQAAAAAAAALQAAAAJGEyZjUyNDAzLTZmMzMtNDI2Mi1hZWRkLTQyMzFlZjU1NGE0OQ.jpg")
                .into(mPictureCaps, new com.squareup.picasso.Callback() {

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Log.d(TAG, "Error");
                    }
                });

        return rootView;

    }
}
