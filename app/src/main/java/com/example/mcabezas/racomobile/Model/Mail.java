package com.example.mcabezas.racomobile.Model;

import java.util.Date;

/**
 * Created by mcabezas on 11/02/16.
 */

public class Mail extends ItemGeneric {

    private String mTo;
    private String mFrom;
    private int mLlegits;
    private int mNoLlegits;

    public String getDireccio() {
        return direccio;
    }

    public void setDireccio(String direccio) {
        this.direccio = direccio;
    }

    private String direccio;

    public int getLlegits() {
        return mLlegits;
    }

    public void setLlegits(int llegits) {
        this.mLlegits = llegits;
    }

    public int getNo_llegits() {
        return mNoLlegits;
    }

    public void setNo_llegits(int no_llegits) {
        this.mNoLlegits = no_llegits;
    }

    public String getTo() {
        return mTo;
    }

    public void setTo(String to) {
        this.mTo = to;
    }

    public String getFrom() {
        return mFrom;
    }

    public void setFrom(String from) {
        this.mFrom = from;
    }

    public Mail(String titol, String descripcio, String Direccio,String imatge, Date pubDate, int tipus, int llegits, int no_llegits) {
        super(titol, descripcio, imatge, pubDate, tipus,Direccio);
        this.setNo_llegits(no_llegits);
        this.setLlegits(llegits);
    }
}
