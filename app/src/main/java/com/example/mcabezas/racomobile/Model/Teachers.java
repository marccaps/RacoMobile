package com.example.mcabezas.racomobile.Model;

/**
 * Created by mcabezas on 11/02/16.
 */
public class Teachers {

    private String mNom;
    private String mCorreu;
    private int mIdAssig;

    public Teachers(String nom, String correu, int id){
        this.mNom = nom;
        this.mCorreu = correu;
        this.mIdAssig = id;
    }

    public String getNom() {
        return mNom;
    }

    public void setNom(String nom) {
        this.mNom = nom;
    }

    public String getCorreu() {
        return mCorreu;
    }

    public void setCorreu(String correu) {
        this.mCorreu = correu;
    }

    public void setIdAssig(int idAssig) {
        this.mIdAssig = idAssig;
    }

    public int getIdAssig() {
        return mIdAssig;
    }
}
