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
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.mcabezas.racomobile.ItemList;
import com.example.mcabezas.racomobile.Model.BaseDadesManager;


public abstract class RefreshListActivity extends Fragment {

    protected static Context context;
    protected BaseDadesManager mBdm;
    protected RelativeLayout mRLayout;
    protected LinearLayout mLLayout;
    protected ProgressBar mPd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = null;
        RefreshListActivity.context = this.getActivity();

        return rootView;
    }

    /**Funció que implementa cada classe que necessita actualitzar la llista i la Base de dades*/
    protected abstract void actualitzarLlistaBaseDades(ItemList lli);

    /**Funció que implementa cada classe que necessita actualitzar la llista i la Base de dades*/
    protected abstract void mostrarLlistes();

    /**Funció per obtenir les dades de la Web, es connecta a la FIB*/
    protected abstract void obtenirDadesWeb();

    /**Funció per recuperar les dades que hi ha a la Base de dades*/
    protected abstract void obtenirDadesBd();


    protected void mostrarProgressBarBanner(){

    }

    protected void mostrarProgressBarPantalla(ProgressBar mPd, RelativeLayout mRLayout) {
        mPd.setVisibility(View.VISIBLE);
        mRLayout.setVisibility(View.VISIBLE);
    }

    protected void amagarProgressBarPantalla(ProgressBar mPd, RelativeLayout mRLayout) {
        mPd.setVisibility(View.GONE);
        mRLayout.setVisibility(View.GONE);
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
