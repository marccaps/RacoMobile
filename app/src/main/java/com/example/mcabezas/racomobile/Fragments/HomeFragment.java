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
import com.example.mcabezas.racomobile.Model.ItemGeneric;
import com.example.mcabezas.racomobile.Model.Noticia;
import com.example.mcabezas.racomobile.Model.PreferenciesUsuari;
import com.example.mcabezas.racomobile.R;


public class HomeFragment extends GestioActualitzaLlistesActivity {

    // Variable que fem servir per saber cada quan s'ha de refrescar, ara per
    // ara, seran 5 minuts (android utils hi ha la variable)
    private static int updateTime = -1;

    private final String mTAG = "VistaInici";
    private String mUsername;
    private String mPassword;
    private static SharedPreferences sPrefs;
    private int mTotalAvisos;
    private static boolean sEsLogin;
    AndroidUtils au = AndroidUtils.getInstance();
    private static boolean errorAlCarregar;
    // Llistes que tindran les dades
    private ArrayList<ItemGeneric> sListItems = new ArrayList<ItemGeneric>();
    private ArrayList<String> sListImatges = new ArrayList<String>();

    // Llista de la vista
    public ListView sLlistaVista;
    public static ItemGeneric itemSeleccionat = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        sLlistaVista = (ListView) rootView.findViewById(R.id.avisos);
        mRLayout = (RelativeLayout) rootView.findViewById(R.id.relative_avisos);

        // Base de dades
        mBdm = new BaseDadesManager(getActivity());

        // Per si hi ha error i no té usuari no es mostri l'opció username i
        // password
        errorAlCarregar = false;

        sPrefs = getActivity().getSharedPreferences(
                PreferenciesUsuari.getPreferenciesUsuari(), Context.MODE_PRIVATE);
        // Inicialitzem les preferències
        mUsername = sPrefs.getString("username", "");
        mPassword = sPrefs.getString("password", "");

        // Preferencies a zero
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putInt(au.NOTIFICATION_COUNTER, 0);
        editor.commit();

        mostrarLlistes();

        // per la primera vegada posem el updateTime = -1 i per la resta juguem
        // amb la funcio que ens dirà si toca o no
        if (updateTime == -1 || enableActualitzarVista()) {
            // Obtenir de la web
            if (!hihaInternet()) {
                Toast.makeText(getActivity(), "Hi ha internet",
                        Toast.LENGTH_LONG).show();
                if (sListItems.isEmpty()) {
                    mostrarVistaNoInformacio(mLLayout);
                }
            } else {
                if (sListItems.isEmpty()) {
                }
                obtenirDadesWeb();
                mostrarLlistes();
            }

            // Activem la variable
        }
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

        // si vinc del login també hem d'actualitzar forçadament
        if (sEsLogin || enableActualitzarVista()) {
            // Obtenir de la web
            if (!hihaInternet()) {
                Toast.makeText(getActivity(), "Hi ha internet",
                        Toast.LENGTH_LONG).show();
                mostrarProgressBarBanner();
                mostrarLlistes();
            } else {
                if (sListItems.isEmpty()) {
                } else {
                }
                obtenirDadesWeb();
            }
            sEsLogin = false;
            // Activem la variable
            updateTime = Calendar.getInstance().getTime().getMinutes();
        }
    }

    public ArrayList<ItemGeneric> getLlistaItems() {
        return sListItems;
    }

    public void setLlistaItems(ArrayList<ItemGeneric> llistaItems) {
        this.sListItems = llistaItems;
    }

    public ArrayList<String> getLlistaUrlImatges() {
        return sListImatges;
    }

    public void setLlistaUrlImatges(ArrayList<String> llistaUrlImatges) {
        this.sListImatges = llistaUrlImatges;
    }

    @Override
    public void actualitzarLlistaBaseDades(LlistesItems lli) {
        try {

            if (lli.getLitemsGenerics().size() > 0) {
                sListItems = (ArrayList<ItemGeneric>) lli.getLitemsGenerics();
                sListImatges = (ArrayList<String>) lli.getLimatges();
                /** Actualitzem la Base de dades */
                actualitzarTaula();
//                amagarProgressBarBanner();
//                amagarProgressBarPantalla(mPd, mRLayout);
            } else {
//                amagarProgressBarBanner();
//                amagarProgressBarPantalla(mPd, mRLayout);
                mostrarVistaNoInformacio(mLLayout);
            }
        } catch (Exception e) {
//            amagarProgressBarBanner();
//            amagarProgressBarPantalla(mPd, mRLayout);
            mostrarVistaNoInformacio(mLLayout);
            errorAlCarregar = true;
            Toast.makeText(getActivity(), "Error",
                    Toast.LENGTH_LONG).show();
        }
    }

    protected void ResetLlistes() {
        sListItems.clear();
        sListImatges.clear();
    }

    @Override
    protected void mostrarLlistes() {
        sListItems.clear();
        obtenirDadesBd();
        AssignaturesAnalyzer();
        AdaptadorAssignaturesRaco adaptadorAssignaturesRaco = new AdaptadorAssignaturesRaco(getActivity(),sListItems);
        sLlistaVista.setAdapter(adaptadorAssignaturesRaco);
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
        mBdm.deleteTableNoticies();
        mBdm.deleteTableCorreus();
        mBdm.deleteTableAvisos();

        // comptadors per saber els elements a mostrar amb les preferencies
        mTotalAvisos = 0;
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
                        ig.getTipus(), idAssigFinal);
                mTotalAvisos++;
            }
        }
        mBdm.close();
    }

    @Override
    protected void obtenirDadesBd() {
        obtenirAvisos();
        if (sListItems.isEmpty()) {
            // Toast.makeText(getApplicationContext(), R.string.noBaseDades,
            // Toast.LENGTH_LONG).show();
        }
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

                pauA = pauA.ParserAndUrl(avisos, AndroidUtils.TIPUS_AVISOS, null,
                        null);

                gc.execute(pauA);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    protected void tancarApp() {
        super.onDestroy();
        System.runFinalizersOnExit(true);
        System.exit(0);
    }


    private void obtenirAvisos() {
        mBdm.open();
        ArrayList<ItemGeneric> list = mBdm.getAllAvisos();
        for (ItemGeneric a : list) {
            sListItems.add(a);
            sListImatges.add(a.getImatge());
        }
        mTotalAvisos = list.size();
        mBdm.close();
    }

    private void ordenarPerData() {
        try {
            int minIndex;
            ItemGeneric rig, rag;
            int n = sListItems.size();
            for (int i = 0; i < n - 1; i++) {
                minIndex = i;
                for (int j = i + 1; j < n; j++) {
                    rig = sListItems.get(j);
                    rag = sListItems.get(minIndex);
                    if (rig.getDataPub().after(rag.getDataPub())) {
                        minIndex = j;
                    }
                }
                if (minIndex != i) {
                    Collections.swap(sListImatges, i, minIndex);
                    Collections.swap(sListItems, i, minIndex);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] tractarImatges() {
        String link[] = null;
        try {
            link = (String[]) sListImatges.toArray(new String[sListImatges
                    .size()]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return link;
    }

    private void tractarPreferencies() {
        String ressaltat = sPrefs.getString("ListPreferenceElement", "0");
        Boolean checkboxSeleccionat = sPrefs.getBoolean(
                "Active_box_preference", false);

        if (checkboxSeleccionat) {
            if (ressaltat.equals("2")) { // vol correus
                eliminarNoticiesAvisos();
            }
        }
    }

    private void eliminarCorreuAvisos() {
        ItemGeneric ig;
        for (int i = sListItems.size() - 1; i > 0; i--) {
            ig = sListItems.get(i);
            if (ig.getTipus() == 2) {
                if (mTotalAvisos <= 2) {
                    // no fem res
                } else {
                    sListItems.remove(i);
                    sListImatges.remove(i);
                    i = sListItems.size();
                    mTotalAvisos--;
                }
            }
        }
    }

    private void eliminarNoticiesAvisos() {
        ItemGeneric ig;
        for (int i = sListItems.size() - 1; i > 0; i--) {
            ig = sListItems.get(i);
            if (ig.getTipus() == 2) {
                if (mTotalAvisos <= 2) {
                    // no fem res
                } else {
                    sListItems.remove(i);
                    sListImatges.remove(i);
                    i = sListItems.size();
                    mTotalAvisos--;
                }
            }
        }
    }


    private boolean enableActualitzarVista() {
        if (updateTime != -1) {
            if (Math.abs(updateTime
                    - Calendar.getInstance().getTime().getMinutes()) >= au.TEMPS_REFRESC) {
                return true;
            }
        }
        return false;
    }

//    /** Gestió del Menú */
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.resum_events, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.actualitzaResumEvents:
//                if (hihaInternet()) {
//                    mostrarProgressBarPantalla(mPd, mRLayout);
//                    obtenirDadesWeb(au);
//                } else {
//                    Toast.makeText(getApplicationContext(), R.string.hiha_internet,
//                            Toast.LENGTH_LONG).show();
//                }
//                break;
//            case R.id.about:
//                Intent intentAbout = new Intent(this, ControladorVistaAbout.class);
//                startActivity(intentAbout);
//                break;
//            case R.id.preferencies:
//                if (!"".equals(mUsername)) {
//                    Intent intentPreferencies = new Intent(this,
//                            PreferenciesUsuari.class);
//                    intentPreferencies.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intentPreferencies.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intentPreferencies);
//                } else {
//                    Toast.makeText(getApplicationContext(),
//                            R.string.necessita_username, Toast.LENGTH_LONG).show();
//                }
//
//                break;
//            case R.id.sortir:
//                tancarApp();
//                break;
//        }
//        return true;
//    }


    public void onBackPressed() {
        getActivity().finish();
    }
}