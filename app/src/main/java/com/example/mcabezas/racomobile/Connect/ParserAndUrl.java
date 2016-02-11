package com.example.mcabezas.racomobile.Connect;

import java.net.URL;

/**
 * Created by mcabezas on 11/02/16.
 */
public class ParserAndUrl {

    private URL mUrl;
    private int mTipus; // 0.- Noticia, 1.- Correu, 2.-Avis
    private String mUsername;
    private String mPassword;

    public ParserAndUrl ParserAndUrl(URL url, int tipus, String username,
                                 String password) {
        ParserAndUrl parserAndUrl = new ParserAndUrl();
        parserAndUrl.setTipus(tipus);
        parserAndUrl.setUrl(url);
        parserAndUrl.setUsername(username);
        parserAndUrl.setPassword(password);
        return parserAndUrl;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public URL getUrl() {
        return mUrl;
    }

    public void setUrl(URL url) {
        this.mUrl = url;
    }

    public int getTipus() {
        return mTipus;
    }

    public void setTipus(int tipus) {
        this.mTipus = tipus;
    }
}
