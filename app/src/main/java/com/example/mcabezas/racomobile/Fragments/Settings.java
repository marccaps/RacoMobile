package com.example.mcabezas.racomobile.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mcabezas.racomobile.R;

/**
 * Created by mcabezas on 3/03/16.
 */
public class Settings extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.settings, container, false);

        return rootView;
    }

}
