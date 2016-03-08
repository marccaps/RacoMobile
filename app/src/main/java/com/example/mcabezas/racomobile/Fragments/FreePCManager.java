package com.example.mcabezas.racomobile.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


import com.example.mcabezas.racomobile.Connect.AndroidUtils;
import com.example.mcabezas.racomobile.Connect.RefreshListActivity;
;
import com.example.mcabezas.racomobile.ItemList;

import com.example.mcabezas.racomobile.Model.UserPreferences;
import com.example.mcabezas.racomobile.R;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Picasso;


/**
 * Created by mcabezas on 18/02/16.
 */
public class FreePCManager extends RefreshListActivity {

    private final String mTAG = "OcupacioRaco";
    private SharedPreferences sPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.free_pc, container, false);

        final ImageView mAulaImage = (ImageView) rootView.findViewById(R.id.aula_image);

        Button mAulaA5 = (Button) rootView.findViewById(R.id.aula_a5);
        Button mAulaB5 = (Button) rootView.findViewById(R.id.aula_b5);
        Button mAulaC6 = (Button) rootView.findViewById(R.id.aula_c6);

        final ProgressWheel wheel = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);
        wheel.setBarColor(Color.rgb(252,207,116));
        wheel.setVisibility(View.GONE);

        mAulaA5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheel.setVisibility(View.VISIBLE);
                Picasso.with(getActivity())
                        .load(AndroidUtils.getInstance().URL_AULA_A5)
                        .into(mAulaImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                wheel.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }
        });
        mAulaB5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheel.setVisibility(View.VISIBLE);
                Picasso.with(getActivity())
                        .load(AndroidUtils.getInstance().URL_AULA_B5)
                        .into(mAulaImage,new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        wheel.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
            });
            }
        });
        mAulaC6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheel.setVisibility(View.VISIBLE);
                Picasso.with(getActivity())
                        .load(AndroidUtils.getInstance().URL_AULA_C6)
                        .into(mAulaImage, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                wheel.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }
        });

        sPrefs = getActivity().getSharedPreferences(
                UserPreferences.getPreferenciesUsuari(), Context.MODE_PRIVATE);


        return rootView;
    }

    @Override
    protected void actualitzarLlistaBaseDades(ItemList lli) {
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
