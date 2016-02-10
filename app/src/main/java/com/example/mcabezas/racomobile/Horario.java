package com.example.mcabezas.racomobile;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mcabezas on 10/02/16.
 */
public class Horario extends Fragment{


    public Horario() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.horario, container, false);

        return rootView;
    }
}
