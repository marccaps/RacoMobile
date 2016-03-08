package com.example.mcabezas.racomobile.Model;

import java.util.Date;

/**
 * Created by mcabezas on 11/02/16.
 */
public class ClassRoom {
    private String mNom;
    private String mPlaces;
    private Date mActualitzacio;
    private Date mDataInici;
    private Date mDataFi;
    private String mNomAssig;
    private String mHihaClasse;


    public ClassRoom(String nom, String places, Date actualitzacio, Date dataInici, Date dataFi, String nomAssig, String hiha){
        this.mNom = nom;
        this.mPlaces = places;
        this.mActualitzacio = actualitzacio;
        this.mDataInici = dataInici;
        this.mDataFi = dataFi;
        this.mNomAssig = nomAssig;
        this.mHihaClasse = hiha;
    }

    public String getmNom() {
        return mNom;
    }


    public Date getmDataInici() {
        return mDataInici;
    }

    public Date getmDataFi() {
        return mDataFi;
    }

    public void setmNomAssig(String mNomAssig) {
        this.mNomAssig = mNomAssig;
    }
}
