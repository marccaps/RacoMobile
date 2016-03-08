package com.example.mcabezas.racomobile.Model;

/**
 * Created by mcabezas on 11/02/16.
 */
import java.util.Date;

/**Aquesta classe serveix per poder mostrar de manera comuna la llista d'assignatures + avisos en el Racó*/

public class SubjectNotice {

    private String mNomAssig;
    private String mTitolAvis;
    private String mDescripcioAvis;
    private Date mData;
    private int mCodiAssig;
    private String mIdAssig;
    private String mImatge;

    public SubjectNotice(String nomAssig, int codiAssig, String idAssig, String titolAvis, String descripcioAvis, Date data, String imatge) {
        this.mNomAssig = nomAssig;
        this.mDescripcioAvis = descripcioAvis;
        this.mData = data;
        this.mTitolAvis = titolAvis;
        this.mCodiAssig = codiAssig;
        this.mIdAssig = idAssig;
        this.mImatge = imatge;
    }
}