package com.example.mcabezas.racomobile.Model;

import java.util.ArrayList;

/**
 * Created by mcabezas on 11/02/16.
 */
public class Assignatura {

    private String mIdAssig; //GRAU-XXXX
    private String mNomAssig; //NOM COMPLET
    private int mCodi;
    private int mCredits;
    private static ArrayList<Professors> sListProfessors = new ArrayList<Professors>();
    private static ArrayList<String> sListObjectius = new ArrayList<String>();

    public Assignatura(int codi, String nomA, String idAssig, int cred, ArrayList<Professors> p, ArrayList<String> obj) {
        this.mCodi = codi;
        this.mIdAssig = idAssig;
        this.mNomAssig = nomA;
        this.mCredits = cred;
        sListProfessors = p;
        sListObjectius = obj;
    }

    public Assignatura() {
        // TODO Auto-generated constructor stub
    }
}
