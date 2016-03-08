package com.example.mcabezas.racomobile.Model;

/**
 * Created by mcabezas on 11/02/16.
 */
import com.example.mcabezas.racomobile.Model.ItemGeneric;

import java.util.Date;

public class Notice extends ItemGeneric {

    private String mNomAssig;

    public Notice(String titol, String descripcio, String imatge, Date pubDate, int tipus, String nomAssig) {
        super(titol, descripcio, imatge, pubDate, tipus);
        this.mNomAssig = nomAssig;
    }

    public Notice(String titol, String descripcio, String imatge, Date pubDate, int tipus, String nomAssig, String url) {
        super(titol , descripcio,imatge,pubDate,tipus,url);
        this.mNomAssig = nomAssig;

    }

    public String getNomAssig() {
        return mNomAssig;
    }

}