package com.example.mcabezas.racomobile.Model;

import java.util.ArrayList;

/**
 * Created by mcabezas on 11/02/16.
 */
public class Subject {

    private String mIdAssig; //GRAU-XXXX
    private String mNomAssig; //NOM COMPLET
    private int mCodi;
    private int mCredits;
    private static ArrayList<Teachers> sListProfessors = new ArrayList<Teachers>();
    private static ArrayList<String> sListObjectius = new ArrayList<String>();

    public Subject(int codi, String nomA, String idAssig, int cred, ArrayList<Teachers> p, ArrayList<String> obj) {
        this.mCodi = codi;
        this.mIdAssig = idAssig;
        this.mNomAssig = nomA;
        this.mCredits = cred;
        sListProfessors = p;
        sListObjectius = obj;
    }

    public Subject() {
        // TODO Auto-generated constructor stub
    }
}
