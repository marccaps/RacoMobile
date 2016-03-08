package com.example.mcabezas.racomobile.Fragments;

/**
 * Created by mcabezas on 11/02/16.
 */
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.mcabezas.racomobile.Adapter.PostItemAdapter;
import com.example.mcabezas.racomobile.Connect.AndroidUtils;
import com.example.mcabezas.racomobile.Connect.RefreshListActivity;
import com.example.mcabezas.racomobile.Connect.ConnectionsManager;
import com.example.mcabezas.racomobile.Connect.ParserAndUrl;
import com.example.mcabezas.racomobile.Model.BodyNews;
import com.example.mcabezas.racomobile.ItemList;
import com.example.mcabezas.racomobile.Model.BaseDadesManager;
import com.example.mcabezas.racomobile.Model.ItemGeneric;
import com.example.mcabezas.racomobile.Model.News;
import com.example.mcabezas.racomobile.R;


public class NewsManager extends RefreshListActivity {

    private String mTAG = "NewsManager";

    // Llistes que tindran les dades
    public static ArrayList<ItemGeneric> sListNoticies = new ArrayList<ItemGeneric>();
    public static ArrayList<String> sListImatges = new ArrayList<String>();

    // Llista de la vista
    public ListView sLlistaVista;
    public PostItemAdapter sAdaptadorLlista;
    public static ItemGeneric sItemSeleccionat = null;
    BaseDadesManager mBdm = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fib_news, container, false);

        mBdm = new BaseDadesManager(getActivity());



        sLlistaVista = (ListView) rootView.findViewById(R.id.postListView);
        mLLayout = (LinearLayout) rootView.findViewById(R.id.linearNoticiesLayout);
        mRLayout = (RelativeLayout) rootView.findViewById(R.id.layout_progress_horari);
        mPd = (ProgressBar) rootView.findViewById(R.id.progressBar_horari);

        mostrarProgressBarPantalla(mPd,mRLayout);

        obtenirDadesWeb();
        resetLlistes();
        mostrarLlistes();
        amagarProgressBarPantalla(mPd,mRLayout);
        return rootView;

    }

    protected void resetLlistes() {
        sListNoticies.clear();
        sListImatges.clear();
    }

    @Override
    public void actualitzarLlistaBaseDades(ItemList lli) {

        try {

            if (lli.getLitemsGenerics().size() > 0) {
                sListNoticies.clear();
                sListNoticies = (ArrayList<ItemGeneric>) lli
                        .getLitemsGenerics();
                sListImatges = (ArrayList<String>) lli.getLimatges();

                // Actualitzem la Base de dades
                ActualitzarTaula();

            } else {
            }
        } catch (Exception e) {
            mostrarVistaNoInformacio(mLLayout);
            Toast.makeText(getActivity(), "Error noticies",
                    Toast.LENGTH_LONG).show();
        }
    }

    protected void obtenirDadesBd() {
        try {
            mBdm.open();
            sListNoticies = mBdm.getAllNoticies();
            for (ItemGeneric n: sListNoticies)
                sListImatges.add(n.getImatge());
            mBdm.close();
        } catch (SQLException e) {
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

    protected void obtenirDadesWeb() {
        ConnectionsManager gc = new ConnectionsManager(getActivity());
        AndroidUtils au = AndroidUtils.getInstance();

        // Preparem les URL i les connexions per obtenir les dades
        try {
            URL not = au.crearURL(au.URL_NOTICIES);
            ParserAndUrl pauN = new ParserAndUrl();
            pauN = pauN.ParserAndUrl(not, AndroidUtils.TIPUS_NOTICIA, null, null);

            gc.execute(pauN);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    protected void mostrarLlistes() {

        resetLlistes();
        obtenirDadesBd();

        if (sListNoticies.isEmpty()) {

        } else {
            // Gestionar les llistes
            sAdaptadorLlista = new PostItemAdapter(getActivity(),
                    tractarImatges(), sListNoticies);
            sLlistaVista.setAdapter(sAdaptadorLlista);

            // Per cada element que cliquin
            sLlistaVista.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> av, View view,
                                        int index, long arg3) {

                    sItemSeleccionat = sListNoticies.get(index);
                    //TODO:La noticia con todos los datos

                    Intent i = new Intent(getActivity() , BodyNews.class);
                    i.putExtra("DESCRIPCION",sItemSeleccionat.getDescripcio());
                    i.putExtra("TITULO",sItemSeleccionat.getTitol());
                    i.putExtra("URL_NOTICIA",((News)sItemSeleccionat).getmLink());
                    startActivity(i);
                }
            });

        }
    }

    protected void ActualitzarTaula() {
        News ig;

        mBdm.open();
        mBdm.deleteTableNoticies();

        for (int i = 0; i < sListNoticies.size(); i++) {
            ig = (News) sListNoticies.get(i);
            mBdm.insertItemNoticia(ig.getTitol(), ig.getDescripcio(),
                    sListImatges.get(i), ig.getDataPub().toString(),
                    ig.getTipus(), ig.getmLink());
        }
        mBdm.close();

    }
}