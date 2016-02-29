package com.example.mcabezas.racomobile.Model;

import java.util.Date;

/**
 * Created by mcabezas on 10/02/16.
 */
public class ItemGeneric {

    protected String mId; // Aquest es crea a la Base de dades quan s'incerten
    // els avisos, però no es fan servir mai, és la clau
    // primària de la BD, és a qui per coherència
    protected String mTitol;
    protected String mDescripcio;
    protected Date mDataPub;
    protected String mImatge;
    protected int mTipus;
    protected String url;

    public ItemGeneric(String titol, String descripcio, String imatge,
                       Date pubDate, int tipus) {
        this.setTitol(titol);
        this.setDescripcio(descripcio);
        this.setImatge(imatge);
        this.setDataPub(pubDate);
        this.setTipus(tipus);
    }

    public ItemGeneric(String titol, String descripcio, String imatge,
                       Date pubDate, int tipus,String url) {
        this.setTitol(titol);
        this.setDescripcio(descripcio);
        this.setImatge(imatge);
        this.setDataPub(pubDate);
        this.setTipus(tipus);
        this.setUrl(url);
    }


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getImatge() {
        return mImatge;
    }

    public void setImatge(String imatge) {
        this.mImatge = imatge;
    }

    public String getTitol() {
        return mTitol;
    }

    public void setTitol(String titol) {
        this.mTitol = titol;
    }

    public String getDescripcio() {
        return mDescripcio;
    }

    public void setDescripcio(String descripcio) {
        this.mDescripcio = descripcio;
    }

    public Date getDataPub() {
        return mDataPub;
    }

    public void setDataPub(Date dataPub) {
        this.mDataPub = dataPub;
    }

    public int getTipus() {
        return mTipus;
    }

    public void setTipus(int tipus) {
        this.mTipus = tipus;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
