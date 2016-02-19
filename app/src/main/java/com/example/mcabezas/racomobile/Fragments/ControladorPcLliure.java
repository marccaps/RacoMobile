package com.example.mcabezas.racomobile.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;



import com.example.mcabezas.racomobile.Connect.GestioActualitzaLlistesActivity;
;
import com.example.mcabezas.racomobile.LlistesItems;

import com.example.mcabezas.racomobile.Model.PreferenciesUsuari;
import com.example.mcabezas.racomobile.R;
import com.squareup.picasso.Picasso;


/**
 * Created by mcabezas on 18/02/16.
 */
public class ControladorPcLliure extends GestioActualitzaLlistesActivity {

    private final String mTAG = "OcupacioRaco";
    private SharedPreferences sPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.pc_lliure, container, false);

        final ImageView mAulaImage = (ImageView) rootView.findViewById(R.id.aula_image);

        Button mAulaA5 = (Button) rootView.findViewById(R.id.aula_a5);
        Button mAulaB5 = (Button) rootView.findViewById(R.id.aula_b5);
        Button mAulaC6 = (Button) rootView.findViewById(R.id.aula_c6);

        mAulaA5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.with(getActivity())
                        .load("https://raco.fib.upc.edu/mapa_ocupades.php?mod=a5")
                        .into(mAulaImage);
            }
        });
        mAulaB5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.with(getActivity())
                        .load("https://raco.fib.upc.edu/mapa_ocupades.php?mod=b5")
                        .into(mAulaImage);
            }
        });
        mAulaC6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.with(getActivity())
                        .load("https://raco.fib.upc.edu/mapa_ocupades.php?mod=c6")
                        .into(mAulaImage);
            }
        });

        sPrefs = getActivity().getSharedPreferences(
                PreferenciesUsuari.getPreferenciesUsuari(), Context.MODE_PRIVATE);



        return rootView;
    }

    @Override
    protected void actualitzarLlistaBaseDades(LlistesItems lli) {
        //Tinc que recogir les dades constantment. No val la pena guardar-ho a la BD.
    }

    @Override
    protected void mostrarLlistes() {
        //Tinc que recogir les dades constantment. No val la pena guardar-ho a la BD.

    }

    @Override
    protected void obtenirDadesWeb() {
        //Tinc que recogir les dades constantment. No val la pena guardar-ho a la BD.

    }

    @Override
    protected void obtenirDadesBd() {
        //Tinc que recogir les dades constantment. No val la pena guardar-ho a la BD.

    }
}
