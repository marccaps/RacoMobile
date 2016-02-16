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
    protected ProgressBar mPd;
    protected RelativeLayout mRLayout;
    protected LinearLayout mLLayout;
    protected static String[] sHores = { "8:00-9:00", "9:00-10:00",
            "10:00-11:00", "11:00-12:00", "12:00-13:00", "13:00-14:00",
            "14:00-15:00", "15:00-16:00", "16:00-17:00", "17:00-18:00",
            "18:00-19:00", "19:00-20:00", "20:00-21:00" };

    protected String[] mTitols = new String[3];
    protected int[] mContingut = new int[3];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = null;
//        View rootView = inflater.inflate(R.layout.noticies_fib, container, false);
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
//            ControladorTabIniApp.carregant.setVisibility(ProgressBar.VISIBLE);
    }

    protected void amagarProgressBarBanner(){
//            ControladorTabIniApp.carregant.setVisibility(ProgressBar.GONE);
    }

    protected void mostrarProgressBarPantalla(ProgressBar mPd, RelativeLayout mRLayout) {
//        mPd.setVisibility(View.VISIBLE);
//        mRLayout.setVisibility(View.VISIBLE);
    }

    protected void amagarProgressBarPantalla(ProgressBar mPd, RelativeLayout mRLayout) {
//        mPd.setVisibility(View.GONE);
//        mRLayout.setVisibility(View.GONE);
    }

    protected void mostrarVistaNoInformacio(LinearLayout mLLayout) {
    }

    protected void amagarMostrarVistaNoInformacio(LinearLayout mLLayout) {
//        mLLayout.setBackgroundDrawable(null);
    }

    protected void amagarMostrarVistaNoInformacioRL(RelativeLayout mLLayout) {
//        mLLayout.setBackgroundDrawable(null);
    }


    protected boolean hihaInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
