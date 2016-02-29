package com.example.mcabezas.racomobile.Connect;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by mcabezas on 11/02/16.
 */
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.mcabezas.racomobile.LlistesItems;
import com.example.mcabezas.racomobile.Model.BaseDadesManager;
import com.example.mcabezas.racomobile.R;


public abstract class GestioActualitzaLlistesActivity extends Fragment {

    protected static Context context;
    protected BaseDadesManager mBdm;
    protected RelativeLayout mRLayout;
    protected LinearLayout mLLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = null;
        GestioActualitzaLlistesActivity.context = getActivity();

        return rootView;
    }

    /**Funció que implementa cada classe que necessita actualitzar la llista i la Base de dades*/
    protected abstract void actualitzarLlistaBaseDades(LlistesItems lli);

    /**Funció que implementa cada classe que necessita actualitzar la llista i la Base de dades*/
    protected abstract void mostrarLlistes();

    /**Funció per obtenir les dades de la Web, es connecta a la FIB*/
    protected abstract void obtenirDadesWeb();

    /**Funció per recuperar les dades que hi ha a la Base de dades*/
    protected abstract void obtenirDadesBd();


    protected void mostrarProgressBarBanner(){
    }

    protected void mostrarVistaNoInformacio(LinearLayout mLLayout) {
    }

    protected boolean hihaInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
