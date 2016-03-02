package com.example.mcabezas.racomobile.Fragments;

/**
 * Created by mcabezas on 9/02/16.
 */
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.mcabezas.racomobile.Adapter.AdaptadorAssignaturesRaco;
import com.example.mcabezas.racomobile.Connect.AndroidUtils;
import com.example.mcabezas.racomobile.Connect.GestioActualitzaLlistesActivity;
import com.example.mcabezas.racomobile.Connect.GestioConnexions;
import com.example.mcabezas.racomobile.Connect.ParserAndUrl;
import com.example.mcabezas.racomobile.LlistesItems;
import com.example.mcabezas.racomobile.Model.BaseDadesManager;
import com.example.mcabezas.racomobile.Model.Correu;
import com.example.mcabezas.racomobile.Model.CuerpoAvisos;
import com.example.mcabezas.racomobile.Model.ItemGeneric;
import com.example.mcabezas.racomobile.Model.Noticia;
import com.example.mcabezas.racomobile.Model.PreferenciesUsuari;
import com.example.mcabezas.racomobile.R;


public class HomeFragment extends GestioActualitzaLlistesActivity {

    // Variable que fem servir per saber cada quan s'ha de refrescar, ara per
    // ara, seran 5 minuts (android utils hi ha la variable)

    private final String mTAG = "VistaInici";
    private String mUsername;
    private String mPassword;
    private static SharedPreferences sPrefs;
    AndroidUtils au = AndroidUtils.getInstance();
    // Llistes que tindran les dades
    private ArrayList<ItemGeneric> sListItems = new ArrayList<ItemGeneric>();
    private ArrayList<String> sListImatges = new ArrayList<String>();

    // Llista de la vista
    public ListView sLlistaVista;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        sLlistaVista = (ListView) rootView.findViewById(R.id.avisos);


        // Base de dades
        mBdm = new BaseDadesManager(getActivity());

        // Per si hi ha error i no té usuari no es mostri l'opció username i
        // password

        sPrefs = getActivity().getSharedPreferences(
                PreferenciesUsuari.getPreferenciesUsuari(), Context.MODE_PRIVATE);
        // Inicialitzem les preferències
        mUsername = sPrefs.getString("username", "");
        mPassword = sPrefs.getString("password", "");

        // Preferencies a zero
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putInt(au.NOTIFICATION_COUNTER, 0);
        editor.commit();

        // per la primera vegada posem el updateTime = -1 i per la resta juguem
        // amb la funcio que ens dirà si toca o no

        // Obtenir de la web
        obtenirDadesWeb();
        sListItems.clear();
        while(sListItems.isEmpty()) {

            obtenirDadesBd();
        }
        sListItems.clear();
        mostrarLlistes();

            // Activem la variable
        return rootView;
    }

    /**
     * Quan tornen a la classe només carreguem al informació que hi havia a la
     * BD
     */
    @Override
    public void onResume() {
        super.onResume();

        // Preferencies a zero
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putInt(au.NOTIFICATION_COUNTER, 0);
        editor.commit();

        obtenirDadesWeb();
    }

    @Override
    public void actualitzarLlistaBaseDades(LlistesItems lli) {
        try {

            if (lli.getLitemsGenerics().size() > 0) {
                sListItems = (ArrayList<ItemGeneric>) lli.getLitemsGenerics();
                sListImatges = (ArrayList<String>) lli.getLimatges();
                /** Actualitzem la Base de dades */
                actualitzarTaula();
            }
        } catch (Exception e) {

            mostrarVistaNoInformacio(mLLayout);
            Toast.makeText(getActivity(), "Error",
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void mostrarLlistes() {
        obtenirDadesBd();
        AssignaturesAnalyzer();
        AdaptadorAssignaturesRaco adaptadorAssignaturesRaco = new AdaptadorAssignaturesRaco(getActivity(),sListItems);
        sLlistaVista.setAdapter(adaptadorAssignaturesRaco);

        sLlistaVista.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), CuerpoAvisos.class);
                i.putExtra("DESCRIPCION_AVISO",sListItems.get(position).getDescripcio());
                i.putExtra("TITULO_AVISO",sListItems.get(position).getTitol());
                i.putExtra("URL_AVISOS",sListItems.get(position).getUrl());
                startActivity(i);
            }
        });
    }

    private void AssignaturesAnalyzer() {
        String ultima_assig = "";
        for(int i = 0; i < sListItems.size();++i) {
            if(i == 0) {
                String mNomAssig = sListItems.get(i).getTitol().replace("GRAU-", "");
                mNomAssig = agafaAssig(mNomAssig);
                sListItems.get(i).setTitol(sListItems.get(i).getTitol().replace("GRAU-"+mNomAssig+"-",""));
                ultima_assig = mNomAssig;
                ItemGeneric itemGeneric = new ItemGeneric(sListItems.get(i).getTitol(),sListItems.get(i).getDescripcio(),sListItems.get(i).getImatge(),sListItems.get(i).getDataPub(),sListItems.get(i).getTipus());
                itemGeneric.setTitol(mNomAssig);
                itemGeneric.setDescripcio("NomAssignatura");
                sListItems.add(i, itemGeneric);
                ++i;
            }
            else {
                String mNomAssig = sListItems.get(i).getTitol().replace("GRAU-", "");
                mNomAssig = agafaAssig(mNomAssig);
                if(!ultima_assig.equals(mNomAssig)) {
                    ultima_assig = mNomAssig;
                    ItemGeneric itemGeneric = new ItemGeneric(sListItems.get(i).getTitol(),sListItems.get(i).getDescripcio(),sListItems.get(i).getImatge(),sListItems.get(i).getDataPub(),sListItems.get(i).getTipus());
                    itemGeneric.setTitol(mNomAssig);
                    itemGeneric.setDescripcio("NomAssignatura");
                    sListItems.add(i, itemGeneric);
                    ++i;
                }
                sListItems.get(i).setTitol(sListItems.get(i).getTitol().replace("GRAU-"+mNomAssig+"-",""));
            }
        }
    }

    private String agafaAssig(String mNomAssig) {
        boolean trobat = false;
        String assig = "";
        for(int i = 0; i < mNomAssig.length() && !trobat;++i) {
            if(mNomAssig.charAt(i) != '-') {
                assig = assig+ mNomAssig.charAt(i);
            }
            else trobat = true;
        }
        return assig;
    }

    // Actualitzem al base de dades
    protected void actualitzarTaula() {
        ItemGeneric ig;

        mBdm.open();
        mBdm.deleteTableAvisos();

        // comptadors per saber els elements a mostrar amb les preferencies
        String[] idAssig;
        for (int i = 0; i < sListItems.size(); i++) {
            ig = sListItems.get(i);
            if (ig.getTipus() == 2) {
                String idAssigFinal = "";
                idAssig = ig.getTitol().split("-");
                if (idAssig.length == 3) {
                    idAssigFinal = idAssig[0].trim() + "-" + idAssig[1].trim();
                } else {
                    idAssigFinal = idAssig[0].trim();
                }
                mBdm.insertItemAvis(ig.getTitol(), ig.getDescripcio(),
                        sListImatges.get(i), ig.getDataPub().toString(),
                        ig.getTipus(), idAssigFinal,ig.getUrl());
            }
        }
        mBdm.close();
    }

    @Override
    protected void obtenirDadesBd() {
        obtenirAvisos();
    }

    @Override
    protected void obtenirDadesWeb() {

        GestioConnexions gc = new GestioConnexions(getActivity());
        SharedPreferences prefs = getActivity().getSharedPreferences(
                PreferenciesUsuari.getPreferenciesUsuari(), Context.MODE_PRIVATE);

        /** Preparem les URL i les connexions per obtenir les dades */
        try {

            // Si ja té username i password mostrem assignatures
            if (mUsername != null && mUsername != "" && mPassword != ""
                    && mPassword != null) {

                // Afegim a la URLAssig la KEY de l'alumne
                String keyURL = prefs.getString(au.KEY_AVISOS, "");
                URL avisos = au.crearURL(au.URL_AVISOS + "" + keyURL);
                ParserAndUrl pauA = new ParserAndUrl();

                pauA = pauA.ParserAndUrl(avisos, AndroidUtils.TIPUS_AVISOS, mUsername,
                        mPassword);

                gc.execute(pauA);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    private void obtenirAvisos() {
        try {
            mBdm.open();
            ArrayList<ItemGeneric> list = mBdm.getAllAvisos();
            for (ItemGeneric a : list) {
                sListItems.add(a);
                sListImatges.add(a.getImatge());
            }
            mBdm.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBdm.open();
        mBdm.deleteTableAssigFib();
        mBdm.deleteTableAvisos();
        mBdm.deleteTableAssigRaco();
        mBdm.close();
    }

    public void onBackPressed() {
        getActivity().finish();
    }
}