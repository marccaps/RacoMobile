package com.example.mcabezas.racomobile.Model;

/**
 * Created by mcabezas on 11/02/16.
 */
import com.example.mcabezas.racomobile.Model.ItemGeneric;

import java.util.Date;

public class News extends ItemGeneric {

    public final static String mTAG="News";
    private String mLink;

    public News(String titol, String descripcio, String imatge, Date pubDate, int tipus, String link) {
        super(titol, descripcio, imatge, pubDate, tipus);
        this.mLink = link;
    }

    public String getmLink() {
        return mLink;
    }
}
