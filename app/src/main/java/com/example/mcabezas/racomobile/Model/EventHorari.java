package com.example.mcabezas.racomobile.Model;

/**
 * Created by mcabezas on 11/02/16.
 */
public class EventHorari {

    private String mAula;
    private String mAssignatura;
    private String mHoraInici;
    private String mHoraFi;
    private int mDia;
    private int mMes;
    private int mAny;

    public EventHorari (String horaI, String horaF, String assig, String aula, int dia, int mes, int any){
        this.mAula = aula;
        this.mAssignatura = assig;
        this.mHoraInici = horaI;
        this.mHoraFi = horaF;
        this.mDia = dia;
        this.mMes = mes;
        this.mAny = any;
    }

    /**Per poder crear events quan no hi ha classe*/

    public String getmAula() {
        return mAula;
    }


    public String getmAssignatura() {
        return mAssignatura;
    }


    public String getmHoraInici() {
        return mHoraInici;
    }


    public String getmHoraFi() {
        return mHoraFi;
    }



    public int getmDia() {
        return mDia;
    }


    public int getmMes() {
        return mMes;
    }


    public int getmAny() {
        return mAny;
    }

}
