package com.example.mcabezas.racomobile;

/**
 * Created by mcabezas on 11/02/16.
 */
import com.example.mcabezas.racomobile.Model.Subject;
import com.example.mcabezas.racomobile.Model.ClassRoom;
import com.example.mcabezas.racomobile.Model.EventAgenda;
import com.example.mcabezas.racomobile.Model.ItemGeneric;

import java.util.ArrayList;



public class ItemList {

    private ArrayList<ItemGeneric> mLitems; // items de la llista ResumEvents i NoticiesFib
    private ArrayList<String> mLimatges;	// Imatges dels items de la llista
    private ArrayList<Subject> mLassig;
    private ArrayList<EventAgenda> mLeventAgenda;
    private ArrayList<ClassRoom> mLaula;

    public ItemList() {
        // TODO Es podria passar un identificador per saber quin new s'ha de fer
        mLitems = new ArrayList<ItemGeneric>();
        mLimatges = new ArrayList<String>();
        mLassig = new ArrayList<Subject>();
        mLeventAgenda = new ArrayList<EventAgenda>();
        mLaula = new ArrayList<ClassRoom>();
    }

    /**GET's*/
    public ArrayList<ItemGeneric> getLitemsGenerics() {
        return mLitems;
    }

    public ArrayList<String> getLimatges() {
        return mLimatges;
    }

    public ArrayList<Subject> getLassig() {
        return mLassig;
    }

    public ArrayList<EventAgenda> getLeventAgenda() {
        return mLeventAgenda;
    }

    public ArrayList<ClassRoom> getLaula() {
        return mLaula;
    }

    /**SET's*/
    public void setLassig(ArrayList<Subject> lassig) {
        this.mLassig = lassig;
    }

    public void setLeventAgenda(ArrayList<EventAgenda> leventAgenda) {
        this.mLeventAgenda = leventAgenda;
    }

    public void setLitemsGenerics(ArrayList<ItemGeneric> litems) {
        this.mLitems = litems;
    }

    public void setLimatges(ArrayList<String> limatges) {
        this.mLimatges = limatges;
    }

    public void setLaula(ArrayList<ClassRoom> laula) {
        this.mLaula = laula;
    }

    /**AFEGIR's*/
    public void afegirItemEventAgenda(EventAgenda it) {
        this.mLeventAgenda.add(it);
    }

    public void afegirItemGeneric(ItemGeneric it) {
        this.mLitems.add(it);
    }

    public void afegirImatge(String im) {
        this.mLimatges.add(im);
    }

    public void afegirItemAssig(Subject it) {
        this.mLassig.add(it);
    }

    public void afegirItemAula(ClassRoom it) {
        this.mLaula.add(it);
    }

}
