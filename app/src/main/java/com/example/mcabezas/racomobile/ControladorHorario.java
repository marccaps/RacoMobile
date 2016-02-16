package com.example.mcabezas.racomobile;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.example.mcabezas.racomobile.Connect.GestioActualitzaLlistesActivity;
import com.example.mcabezas.racomobile.Connect.IcalParser;
import com.example.mcabezas.racomobile.Model.BaseDadesManager;
import com.example.mcabezas.racomobile.Model.EventHorari;
import com.example.mcabezas.racomobile.Model.PreferenciesUsuari;

/**
 * Created by mcabezas on 15/02/16.
 */
public class ControladorHorario extends GestioActualitzaLlistesActivity
        implements Runnable ,WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {

    private int contador = 0;
    private final String mTAG = "ControladorHorari";
    private static ArrayList<EventHorari> mHorari = new ArrayList<EventHorari>();
    private Calendar mDiaActual;
    private int mDiesConsultats; // per saber si hem superat o no els dies que
    // volem carregar declarat a AndroidUtils

    private List<HashMap<String, String>> mInformacio = new ArrayList<HashMap<String, String>>();

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;
    private SharedPreferences sPrefs;

    private int entrada = 0;

    //Fragment heredado

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.horario, container, false);


        sPrefs = getActivity().getSharedPreferences(
                PreferenciesUsuari.getPreferenciesUsuari(), Context.MODE_PRIVATE);

        // Preparem la vista
//        mListHorari = (ListView) findViewById(R.id.list_horari);
//        mListHorari.setClickable(false);
//        mTdiaSetmana = (TextView) findViewById(R.id.diaSetmana);
//        mHora = (TextView) findViewById(R.id.hora);
//        mAula = (TextView) findViewById(R.id.classe_horari);
//        mAssig = (TextView) findViewById(R.id.assignatura_horari);
//
//        crearBotonsAbansDespres();

        // Posem el titol a la taula
        mTitols[0] = "Hora";
        mTitols[1] = "Assignatura";
        mTitols[2] = "Aula";
        // Assignem els mId's per llavors poder assignar els valors
//        mContingut[0] = R.id.hora;
//        mContingut[1] = R.id.assignatura_horari;
//        mContingut[2] = R.id.classe_horari;

        mDiesConsultats = 0;
        // Gestionar Base de dades
        mBdm = new BaseDadesManager(getActivity());

        // Creem la data Actual que apareix en la vista
        mDiaActual = Calendar.getInstance();
        String diaSet = buscarNomDia(mDiaActual.get(Calendar.DAY_OF_WEEK));

        int mes = mDiaActual.get(Calendar.MONTH);
        mes++;
        String dia;
        if (mDiaActual.get(Calendar.DAY_OF_MONTH) < 10) {
            dia = "0" + String.valueOf(mDiaActual.get(Calendar.DAY_OF_MONTH));
        } else {
            dia = String.valueOf(mDiaActual.get(Calendar.DAY_OF_MONTH));
        }

//        mTdiaSetmana.setText(diaSet + " | " + dia + "/" + mes);

        mBdm.open();
        //int res = mBdm.getAllHorari().getCount();
        int res = mBdm.getAllHorariSize();
        mBdm.close();
//        if (res == 0) {
//            if (hihaInternet()) {
//                ControladorTabIniApp.mPd.show();
//                Thread thread = new Thread(this);
//                thread.start();
//            } else {
//                Toast.makeText(getApplicationContext(), R.string.hiha_internet,
//                        Toast.LENGTH_LONG).show();
//                ControladorTabIniApp.carregant.setVisibility(ProgressBar.GONE);
//            }
//        } else {
//            mostrarLlistes();
//        }

        mWeekView = (WeekView) rootView.findViewById(R.id.weekView);

        mWeekView.setNumberOfVisibleDays(1);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);

        if(mHorari.size() == 0) obtenirDadesWeb();
        ordenarPerData();
//        crearHorari();
//        addHorari();


        return rootView;
    }

//    private void addHorari() {
//        for(int i = 0; i < mHorari.size();++i) {
//            mWeekView.
//        }
//    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(getActivity(), "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(getActivity(), "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        Toast.makeText(getActivity(), "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
    }

    public WeekView getWeekView() {
        return mWeekView;
    }


    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        WeekViewEvent event = null;
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        boolean mfinal = false;
        if(newYear == mHorari.get(0).getmAny() || newYear == mHorari.get(mHorari.size()-1).getmAny()) {
            for (int i = 0; (i < mHorari.size()); ++i) {
                if(mHorari.get(i).getmMes() == newMonth) {
                    //TODO:Implementar una busqueda optima
//                    event = new WeekViewEvent(contador,mHorari.get(i).getmAssignatura(),newYear,newMonth-1,mHorari.get(i).getmDia()
//                            ,getHora(mHorari.get(i).getmHoraInici()),0,newYear,newMonth,mHorari.get(i).getmDia(),getHora(mHorari.get(i).getmHoraFi()),0);
//                    event.setColor(getResources().getColor(R.color.event_color_01));
//                    ++contador;
//                    events.add(event);
                    startTime = Calendar.getInstance();
                    startTime.set(Calendar.DAY_OF_MONTH, mHorari.get(i).getmDia());
                    startTime.set(Calendar.HOUR_OF_DAY, getHora(mHorari.get(i).getmHoraInici()));
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.MONTH, newMonth);
                    startTime.set(Calendar.YEAR, newYear);
                    endTime = (Calendar) startTime.clone();
                    endTime.add(Calendar.HOUR_OF_DAY, 2);
                    event = new WeekViewEvent(4, mHorari.get(i).getmAssignatura(), startTime, endTime);
                    event.setColor(getResources().getColor(R.color.event_color_04));
                    events.add(event);
                }
                else mfinal = true;
            }
         }
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 30);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.set(Calendar.HOUR_OF_DAY, 4);
//        endTime.set(Calendar.MINUTE, 30);
//        endTime.set(Calendar.MONTH, newMonth-1);
//        event = new WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_02));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 4);
//        startTime.set(Calendar.MINUTE, 20);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.set(Calendar.HOUR_OF_DAY, 5);
//        endTime.set(Calendar.MINUTE, 0);
//        event = new WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_03));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 5);
//        startTime.set(Calendar.MINUTE, 30);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 2);
//        endTime.set(Calendar.MONTH, newMonth-1);
//        event = new WeekViewEvent(2, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_02));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 5);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        startTime.add(Calendar.DATE, 2);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        endTime.set(Calendar.MONTH, newMonth - 1);
//        event = new WeekViewEvent(3, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_03));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, 15);
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        event = new WeekViewEvent(4, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_04));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, 1);
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_01));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, startTime.getActualMaximum(Calendar.DAY_OF_MONTH));
//        startTime.set(Calendar.HOUR_OF_DAY, 15);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_02));
//        events.add(event);

        return events;
    }

    private int getHora(String hora) {
        int mHora = 0;
        if(hora.length() == 5) mHora = Integer.parseInt(hora.substring(0,2));
        else mHora = Integer.parseInt(hora.substring(0,1));

        return mHora;
    }

    @Override
    public void actualitzarLlistaBaseDades(LlistesItems lli) {
        try {
            if (mHorari.size() > 0) {

                // Actualitzem la Base de dades
                actualitzarTaula();
            } else {
//                ControladorTabIniApp.mPd.dismiss();
//                ControladorTabIniApp.carregant.setVisibility(ProgressBar.GONE);
                Toast.makeText(getActivity(), "Error al cargar horario",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
//            ControladorTabIniApp.mPd.dismiss();
//            ControladorTabIniApp.carregant.setVisibility(ProgressBar.GONE);
            Toast.makeText(getActivity(), "Error al cargar horario",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void mostrarLlistes() {

        if (mHorari != null) {
            mHorari.clear();
        }
        obtenirDadesBd();

        omplirEspaisHorari();
//        crearHorari();

//        // tornem a mostrar els botons
//        mSeguent.setVisibility(AndroidUtils.VISIBLE);
//        mAnterior.setVisibility(AndroidUtils.VISIBLE);
//        mEscollirDia.setVisibility(AndroidUtils.VISIBLE);
//        mTdiaSetmana.setVisibility(AndroidUtils.VISIBLE);
//        mHora.setVisibility(AndroidUtils.VISIBLE);
//        mAula.setVisibility(AndroidUtils.VISIBLE);
//        mAssig.setVisibility(AndroidUtils.VISIBLE);

        // Gestionar les llistes
//        AdaptadorHorariTaula adapter = new AdaptadorHorariTaula(this,
//                mInformacio, R.layout.llista_horari, mTitols, mContingut);
//        mListHorari.setAdapter(adapter);
    }

    @Override
    protected void obtenirDadesWeb() {
        AndroidUtils au = AndroidUtils.getInstance();
        // Preparem les URL i les connexions per obtenir les dades
        try {
            String keyURL = sPrefs.getString(au.KEY_HORARI_RACO, "");
            URL not;
			/*if (sPrefs.getString(AndroidUtils.USERNAME, "")
					.equals("roger.sala")) {
				not = au.crearURL(au.URL_HORARI_RACO_ROGER);
			} else {
				not = au.crearURL(au.URL_HORARI_RACO + keyURL);
			}*/
            not = au.crearURL(au.URL_HORARI_RACO + keyURL);
            // URL not = au.crearURL(au.URL_HORARI_RACO);
            IcalParser ip = IcalParser.getInstance();
            mHorari = ip.parserHorariComplet(not);

        } catch (MalformedURLException e) {
//            ControladorTabIniApp.mPd.dismiss();
//            ControladorTabIniApp.carregant.setVisibility(ProgressBar.GONE);
            Toast.makeText(getActivity(), "Error al cargar horario",
                    Toast.LENGTH_LONG).show();
        }
    }

    protected void obtenirDadesBd() {
        try {
            mBdm.open();
            mHorari = mBdm.getDiaHorari(
                    Integer.toString(mDiaActual.get(Calendar.DAY_OF_MONTH)),
                    Integer.toString(mDiaActual.get(Calendar.MONTH)),
                    Integer.toString(mDiaActual.get(Calendar.YEAR)));

            mBdm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void actualitzarTaula() {
        EventHorari e;
        mBdm.open();
        mBdm.deleteTableHorari();

        for (int i = 0; i < mHorari.size(); i++) {
            e = mHorari.get(i);
            mBdm.insertItemHorari(e.getmHoraInici(), e.getmHoraFi(),
                    e.getmAssignatura(), e.getmAula(), e.getmDia(),
                    e.getmMes(), e.getmAny());
        }
        mBdm.close();
    }

    @Override
    public void run() {
        Looper.prepare();
        obtenirDadesWeb();
        actualitzarLlistaBaseDades(null);
//        handler.sendEmptyMessage(0);
    }

//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//
////            if (ControladorTabIniApp.mPd != null) {
////                ControladorTabIniApp.mPd.dismiss();
////                ControladorTabIniApp.carregant.setVisibility(ProgressBar.GONE);
////            }
////            mostrarLlistes();
//        }
//    };

    private String buscarNomDia(int dia) {
        switch (dia) {
            case 1:
                return getResources().getString(R.string.diumenge);
            case 2:
                return getResources().getString(R.string.dilluns);
            case 3:
                return getResources().getString(R.string.dimarts);
            case 4:
                return getResources().getString(R.string.dimecres);
            case 5:
                return getResources().getString(R.string.dijous);
            case 6:
                return getResources().getString(R.string.divendres);
            case 7:
                return getResources().getString(R.string.dissabte);
            default:
                return " ";
        }
    }

//    protected void crearHorari() {
//
//        // Per si ja hem consultat al principi no cal tornar-hi
//        if (mHorari.isEmpty()) {
//            obtenirDadesBd(); // Buscar dia actual a la BD
//        }
//
//        if (mHorari.isEmpty()) {
//            Toast.makeText(getActivity(), "No hi ha classe",
//                    Toast.LENGTH_LONG).show();
//        } else {
//            String[] horaAct; // SPLIT: el format de la taula es XX:00-ZZ:00 i
//            // aixi tindrem el XX:00 i el ZZ:00 per separat
//            String[] horaActInici;
//            String[] horaActFi;
//
//            mInformacio.clear();
//
//            // Assegurem que els elements del vector estan ordenats per
//            // horaInici
//            ordenarPerData();
//            int i = 0;
//            String assignatura, aula;
//            while (i < sHores.length) {
//
//                // Vector hores
//                horaAct = sHores[i].split("-");
//                horaActInici = horaAct[0].split(":");
//                horaActFi = horaAct[1].split(":");
//
//                ArrayList<EventHorari> classeEnUnaHora = obtenirElementsEnHora(
//                        Integer.parseInt(horaActInici[0]),
//                        Integer.parseInt(horaActFi[0]));
//
//                if (classeEnUnaHora.size() > 0) {
//                    int m = 0;
//                    assignatura = classeEnUnaHora.get(m).getmAssignatura();
//                    aula = "";
//                    // si no hi ha hores solapades
//                    if (classeEnUnaHora.size() == 1) {
//                        // mirem ara si hi ha m\E9s d'una classe on es fa aquella
//                        // assignatura
//                        String[] aulaV = classeEnUnaHora.get(m).getmAula()
//                                .split(",");
//                        aula = aulaV[0];
//                        if (aulaV.length > 1) {
//                            for (int k = 1; k < aulaV.length; k++) {
//                                aula = aula + "\n" + aulaV[k];
//                            }
//                        }
//                    } else {
//                        aula = classeEnUnaHora.get(m).getmAula();
//                        for (m = 1; m < classeEnUnaHora.size(); m++) {
//                            assignatura = assignatura + "\n"
//                                    + classeEnUnaHora.get(m).getmAssignatura();
//                            aula = aula + "\n"
//                                    + classeEnUnaHora.get(m).getmAula();
//                        }
//                    }
//                    HashMap<String, String> map = new HashMap<String, String>();
//                    map.put(mTitols[0], sHores[i]);
//                    map.put(mTitols[1], assignatura);
//                    map.put(mTitols[2], aula);
//                    mInformacio.add(i, map);
//                    i++;
//                } else {
//                    HashMap<String, String> map = new HashMap<String, String>();
//                    map.put(mTitols[0], sHores[i]);
//                    map.put(mTitols[1], "");
//                    map.put(mTitols[2], "");
//                    mInformacio.add(i, map);
//                    i++;
//                }
//            }
//        }
//    }

    private ArrayList<EventHorari> obtenirElementsEnHora(int inici, int fi) {
        ArrayList<EventHorari> eh = new ArrayList<EventHorari>();

        EventHorari event;
        String[] horaEventInici;
        String[] horaEventFi;

        for (int i = 0; i < mHorari.size(); i++) {
            event = mHorari.get(i);
            horaEventInici = event.getmHoraInici().split(":");
            horaEventFi = event.getmHoraFi().split(":");
            int horaIniciEvent = Integer.parseInt(horaEventInici[0]);
            int horaFiEvent = Integer.parseInt(horaEventFi[0]);

            if (inici >= horaIniciEvent && fi <= horaFiEvent) {
                EventHorari evAux = new EventHorari();
                evAux.setmAssignatura(mHorari.get(i).getmAssignatura());
                evAux.setmAny(mHorari.get(i).getmAny());
                evAux.setmAula(mHorari.get(i).getmAula());
                evAux.setmDia(mHorari.get(i).getmDia());
                evAux.setmHoraFi(mHorari.get(i).getmHoraFi());
                evAux.setmHoraInici(mHorari.get(i).getmHoraInici());
                evAux.setmMes(mHorari.get(i).getmMes());
                eh.add(evAux);
            }
        }
        return eh;
    }

    private void ordenarPerData() {
        int minIndex;
        EventHorari rig, rag;
        int n = mHorari.size();
        String[] horaEventIniciRig;
        String[] horaEventIniciRag;
        for (int i = 0; i < n - 1; i++) {
            minIndex = i;
            for (int j = i + 1; j < n; j++) {
                rig = mHorari.get(j);
                rag = mHorari.get(minIndex);
                if (rig.getmMes() < rag.getmMes()) {
                    minIndex = j;
                }
                else if(rig.getmMes() == rag.getmMes()) {
                    if(rig.getmDia() < rag.getmDia()) {
                        minIndex = j;
                    }
                }
            }
            if (minIndex != i) {
                Collections.swap(mHorari, i, minIndex);
            }
        }
    }

    private void omplirEspaisHorari() {
        int diferencia = 0;
        if (mHorari != null) {
            diferencia = Math.abs(mHorari.size() - sHores.length);
        } else {
            mHorari = new ArrayList<EventHorari>();
            diferencia = Math.abs(0 - sHores.length);
        }

        Calendar cDataNoValida = Calendar.getInstance();
        cDataNoValida.set(Calendar.YEAR, mDiaActual.get(Calendar.YEAR) + 1);
        cDataNoValida.set(Calendar.MONTH, mDiaActual.get(Calendar.MONTH));
        cDataNoValida.set(Calendar.DAY_OF_MONTH,
                mDiaActual.get(Calendar.DAY_OF_MONTH));
        Date dataNoValida = cDataNoValida.getTime();

        for (int i = 0; i < diferencia; i++) {
            EventHorari ehTemp = new EventHorari("23:00", "23:00", "", "",
                    dataNoValida.getDay(), dataNoValida.getMonth(),
                    dataNoValida.getYear());
            mHorari.add(ehTemp);
        }
    }

//    private void crearBotonsAbansDespres() {
//
//        mSeguent = (ImageButton) findViewById(R.id.seguent);
//        // No mostrem el bot\F3 perqu\E8 no hi ha dades
//        mSeguent.setVisibility(AndroidUtils.INVISIBLE);
//        mSeguent.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                Intent horariConcret = new Intent(getParent(),
//                        ControladorVistaHorariConcret.class);
//                horariConcret.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                horariConcret.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                horariConcret.putExtra("any", mDiaActual.get(Calendar.YEAR));
//                horariConcret.putExtra("mes", mDiaActual.get(Calendar.MONTH));
//                horariConcret.putExtra("dia",
//                        mDiaActual.get(Calendar.DAY_OF_MONTH));
//                horariConcret.putExtra("queEs", "despres");
//                horariConcret.putExtra("diesConsultats", mDiesConsultats + 1);
//                ControladorTabGroupHorari parentActivity = (ControladorTabGroupHorari) getParent();
//                parentActivity.startChildActivity(
//                        "ControladorVistaHorariConcret", horariConcret);
//            }
//        });
//
//        mAnterior = (ImageButton) findViewById(R.id.abans);
//        // No mostrem el bot\F3 perqu\E8 no hi ha dades
//        mAnterior.setVisibility(AndroidUtils.INVISIBLE);
//        mAnterior.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                Intent horariConcret = new Intent(getParent(),
//                        ControladorVistaHorariConcret.class);
//                horariConcret.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                horariConcret.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                horariConcret.putExtra("any", mDiaActual.get(Calendar.YEAR));
//                horariConcret.putExtra("mes", mDiaActual.get(Calendar.MONTH));
//                horariConcret.putExtra("dia",
//                        mDiaActual.get(Calendar.DAY_OF_MONTH));
//                horariConcret.putExtra("queEs", "abans");
//                horariConcret.putExtra("diesConsultats", mDiesConsultats - 1);
//                ControladorTabGroupHorari parentActivity = (ControladorTabGroupHorari) getParent();
//                parentActivity.startChildActivity(
//                        "ControladorVistaHorariConcret", horariConcret);
//            }
//        });
//
//        /** El tema del DatePicker */
//        mEscollirDia = (ImageButton) findViewById(R.id.escollirDia);
//        mEscollirDia.setVisibility(AndroidUtils.INVISIBLE);
//        mEscollirDia.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent datePicker = new Intent(getParent(),
//                        ControladorDatePickerHorari.class);
//                datePicker.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                datePicker.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                datePicker.putExtra("username",
//                        sPrefs.getString(AndroidUtils.USERNAME, ""));
//                datePicker.putExtra("queEs", "mostrarDatePicker");
//                startActivity(datePicker);
//            }
//        });
//
//    }

    /**
     * Gesti\F3 del Men\FA
     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.zona_raco, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onMenuItemSelected(int featureId, MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.actualitza:
//                if (hihaInternet()) {
//                    mBdm.open();
//                    mBdm.deleteTableHorari();
//                    mBdm.close();
//                    Toast.makeText(getParent().getBaseContext(),
//                            R.string.infoActualitzaHorari, Toast.LENGTH_LONG)
//                            .show();
//                } else {
//                    Toast.makeText(getApplicationContext(), R.string.hiha_internet,
//                            Toast.LENGTH_LONG).show();
//                }
//                break;
//            case R.id.logout:
//                sPrefs.edit().clear().commit();
//                Toast.makeText(getParent(), R.string.logout_correcte,
//                        Toast.LENGTH_SHORT).show();
//                mBdm.open();
//                mBdm.deleteTablesLogout();
//                mBdm.close();
//                startActivity(new Intent(getParent(), ControladorTabIniApp.class));
//                break;
//
//        }
//        return true;
//    }
//}
//        extends GestioActualitzaLlistesActivity
//        implements Runnable {
//
//    private final String mTAG = "ControladorHorari";
//    private static ArrayList<EventHorari> mHorari = new ArrayList<EventHorari>();
//    private Calendar mDiaActual;
//    private int mDiesConsultats; // per saber si hem superat o no els dies que
//    // volem carregar declarat a AndroidUtils
//
//    private List<HashMap<String, String>> mInformacio = new ArrayList<HashMap<String, String>>();
//
//    private ListView mListHorari;
//    private ImageButton mSeguent;
//    private ImageButton mAnterior;
//    private ImageButton mEscollirDia;
//    private TextView mTdiaSetmana;
//    private TextView mAssig;
//    private TextView mHora;
//    private TextView mAula;
//    private SharedPreferences sPrefs;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.vista_horari);
//
//        sPrefs = getApplicationContext().getSharedPreferences(
//                PreferenciesUsuari.getPreferenciesUsuari(), MODE_PRIVATE);
//
//        // Preparem la vista
//        mListHorari = (ListView) findViewById(R.id.list_horari);
//        mListHorari.setClickable(false);
//        mTdiaSetmana = (TextView) findViewById(R.id.diaSetmana);
//        mHora = (TextView) findViewById(R.id.hora);
//        mAula = (TextView) findViewById(R.id.classe_horari);
//        mAssig = (TextView) findViewById(R.id.assignatura_horari);
//
//        crearBotonsAbansDespres();
//
//        // Posem el titol a la taula
//        mTitols[0] = getString(R.string.horari_hora);
//        mTitols[1] = getString(R.string.horari_assignatura);
//        mTitols[2] = getString(R.string.horari_aula);
//        // Assignem els mId's per llavors poder assignar els valors
//        mContingut[0] = R.id.hora;
//        mContingut[1] = R.id.assignatura_horari;
//        mContingut[2] = R.id.classe_horari;
//
//        mDiesConsultats = 0;
//        // Gestionar Base de dades
//        mBdm = new BaseDadesManager(this);
//
//        // Creem la data Actual que apareix en la vista
//        mDiaActual = Calendar.getInstance();
//        String diaSet = buscarNomDia(mDiaActual.get(Calendar.DAY_OF_WEEK));
//
//        int mes = mDiaActual.get(Calendar.MONTH);
//        mes++;
//        String dia;
//        if (mDiaActual.get(Calendar.DAY_OF_MONTH) < 10) {
//            dia = "0" + String.valueOf(mDiaActual.get(Calendar.DAY_OF_MONTH));
//        } else {
//            dia = String.valueOf(mDiaActual.get(Calendar.DAY_OF_MONTH));
//        }
//
//        mTdiaSetmana.setText(diaSet + " | " + dia + "/" + mes);
//
//        mBdm.open();
//        //int res = mBdm.getAllHorari().getCount();
//        int res = mBdm.getAllHorariSize();
//        mBdm.close();
//        if (res == 0) {
//            if (hihaInternet()) {
//                ControladorTabIniApp.mPd.show();
//                Thread thread = new Thread(this);
//                thread.start();
//            } else {
//                Toast.makeText(getApplicationContext(), R.string.hiha_internet,
//                        Toast.LENGTH_LONG).show();
//                ControladorTabIniApp.carregant.setVisibility(ProgressBar.GONE);
//            }
//        } else {
//            mostrarLlistes();
//        }
//    }
//
//    @Override
//    public void actualitzarLlistaBaseDades(LlistesItems lli) {
//        try {
//            if (mHorari.size() > 0) {
//
//                // Actualitzem la Base de dades
//                actualitzarTaula();
//            } else {
//                ControladorTabIniApp.mPd.dismiss();
//                ControladorTabIniApp.carregant.setVisibility(ProgressBar.GONE);
//                Toast.makeText(getApplicationContext(), R.string.errorHorari,
//                        Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            ControladorTabIniApp.mPd.dismiss();
//            ControladorTabIniApp.carregant.setVisibility(ProgressBar.GONE);
//            Toast.makeText(getApplicationContext(), R.string.errorHorari,
//                    Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    protected void mostrarLlistes() {
//
//        if (mHorari != null) {
//            mHorari.clear();
//        }
//        obtenirDadesBd();
//
//        omplirEspaisHorari();
//        crearHorari();
//
//        // tornem a mostrar els botons
//        mSeguent.setVisibility(AndroidUtils.VISIBLE);
//        mAnterior.setVisibility(AndroidUtils.VISIBLE);
//        mEscollirDia.setVisibility(AndroidUtils.VISIBLE);
//        mTdiaSetmana.setVisibility(AndroidUtils.VISIBLE);
//        mHora.setVisibility(AndroidUtils.VISIBLE);
//        mAula.setVisibility(AndroidUtils.VISIBLE);
//        mAssig.setVisibility(AndroidUtils.VISIBLE);
//
//        // Gestionar les llistes
//        AdaptadorHorariTaula adapter = new AdaptadorHorariTaula(this,
//                mInformacio, R.layout.llista_horari, mTitols, mContingut);
//        mListHorari.setAdapter(adapter);
//    }
//
//    @Override
//    protected void obtenirDadesWeb() {
//        AndroidUtils au = AndroidUtils.getInstance();
//        // Preparem les URL i les connexions per obtenir les dades
//        try {
//            String keyURL = sPrefs.getString(au.KEY_HORARI_RACO, "");
//            URL not;
//			/*if (sPrefs.getString(AndroidUtils.USERNAME, "")
//					.equals("roger.sala")) {
//				not = au.crearURL(au.URL_HORARI_RACO_ROGER);
//			} else {
//				not = au.crearURL(au.URL_HORARI_RACO + keyURL);
//			}*/
//            not = au.crearURL(au.URL_HORARI_RACO + keyURL);
//            // URL not = au.crearURL(au.URL_HORARI_RACO);
//            IcalParser ip = IcalParser.getInstance();
//            mHorari = ip.parserHorariComplet(not);
//
//        } catch (MalformedURLException e) {
//            ControladorTabIniApp.mPd.dismiss();
//            ControladorTabIniApp.carregant.setVisibility(ProgressBar.GONE);
//            Toast.makeText(getApplicationContext(), R.string.errorHorari,
//                    Toast.LENGTH_LONG).show();
//        }
//    }
//
//    protected void obtenirDadesBd() {
//        try {
//            mBdm.open();
//            mHorari = mBdm.getDiaHorari(
//                    Integer.toString(mDiaActual.get(Calendar.DAY_OF_MONTH)),
//                    Integer.toString(mDiaActual.get(Calendar.MONTH)),
//                    Integer.toString(mDiaActual.get(Calendar.YEAR)));
//
//            mBdm.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    protected void actualitzarTaula() {
//        EventHorari e;
//        mBdm.open();
//        mBdm.deleteTableHorari();
//
//        for (int i = 0; i < mHorari.size(); i++) {
//            e = mHorari.get(i);
//            mBdm.insertItemHorari(e.getmHoraInici(), e.getmHoraFi(),
//                    e.getmAssignatura(), e.getmAula(), e.getmDia(),
//                    e.getmMes(), e.getmAny());
//        }
//        mBdm.close();
//    }
//
//    @Override
//    public void run() {
//        Looper.prepare();
//        obtenirDadesWeb();
//        actualitzarLlistaBaseDades(null);
//        handler.sendEmptyMessage(0);
//    }
//
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//
//            if (ControladorTabIniApp.mPd != null) {
//                ControladorTabIniApp.mPd.dismiss();
//                ControladorTabIniApp.carregant.setVisibility(ProgressBar.GONE);
//            }
//            mostrarLlistes();
//        }
//    };
//
//    private String buscarNomDia(int dia) {
//        switch (dia) {
//            case 1:
//                return getResources().getString(R.string.diumenge);
//            case 2:
//                return getResources().getString(R.string.dilluns);
//            case 3:
//                return getResources().getString(R.string.dimarts);
//            case 4:
//                return getResources().getString(R.string.dimecres);
//            case 5:
//                return getResources().getString(R.string.dijous);
//            case 6:
//                return getResources().getString(R.string.divendres);
//            case 7:
//                return getResources().getString(R.string.dissabte);
//            default:
//                return " ";
//        }
//    }
//
//    protected void crearHorari() {
//
//        // Per si ja hem consultat al principi no cal tornar-hi
//        if (mHorari.isEmpty()) {
//            obtenirDadesBd(); // Buscar dia actual a la BD
//        }
//
//        if (mHorari.isEmpty()) {
//            Toast.makeText(getApplicationContext(), R.string.no_hi_ha_classe,
//                    Toast.LENGTH_LONG).show();
//        } else {
//            String[] horaAct; // SPLIT: el format de la taula es XX:00-ZZ:00 i
//            // aixi tindrem el XX:00 i el ZZ:00 per separat
//            String[] horaActInici;
//            String[] horaActFi;
//
//            mInformacio.clear();
//
//            // Assegurem que els elements del vector estan ordenats per
//            // horaInici
//            ordenarPerData();
//            int i = 0;
//            String assignatura, aula;
//            while (i < sHores.length) {
//
//                // Vector hores
//                horaAct = sHores[i].split("-");
//                horaActInici = horaAct[0].split(":");
//                horaActFi = horaAct[1].split(":");
//
//                ArrayList<EventHorari> classeEnUnaHora = obtenirElementsEnHora(
//                        Integer.parseInt(horaActInici[0]),
//                        Integer.parseInt(horaActFi[0]));
//
//                if (classeEnUnaHora.size() > 0) {
//                    int m = 0;
//                    assignatura = classeEnUnaHora.get(m).getmAssignatura();
//                    aula = "";
//                    // si no hi ha hores solapades
//                    if (classeEnUnaHora.size() == 1) {
//                        // mirem ara si hi ha m\E9s d'una classe on es fa aquella
//                        // assignatura
//                        String[] aulaV = classeEnUnaHora.get(m).getmAula()
//                                .split(",");
//                        aula = aulaV[0];
//                        if (aulaV.length > 1) {
//                            for (int k = 1; k < aulaV.length; k++) {
//                                aula = aula + "\n" + aulaV[k];
//                            }
//                        }
//                    } else {
//                        aula = classeEnUnaHora.get(m).getmAula();
//                        for (m = 1; m < classeEnUnaHora.size(); m++) {
//                            assignatura = assignatura + "\n"
//                                    + classeEnUnaHora.get(m).getmAssignatura();
//                            aula = aula + "\n"
//                                    + classeEnUnaHora.get(m).getmAula();
//                        }
//                    }
//                    HashMap<String, String> map = new HashMap<String, String>();
//                    map.put(mTitols[0], sHores[i]);
//                    map.put(mTitols[1], assignatura);
//                    map.put(mTitols[2], aula);
//                    mInformacio.add(i, map);
//                    i++;
//                } else {
//                    HashMap<String, String> map = new HashMap<String, String>();
//                    map.put(mTitols[0], sHores[i]);
//                    map.put(mTitols[1], "");
//                    map.put(mTitols[2], "");
//                    mInformacio.add(i, map);
//                    i++;
//                }
//            }
//        }
//    }
//
//    private ArrayList<EventHorari> obtenirElementsEnHora(int inici, int fi) {
//        ArrayList<EventHorari> eh = new ArrayList<EventHorari>();
//
//        EventHorari event;
//        String[] horaEventInici;
//        String[] horaEventFi;
//
//        for (int i = 0; i < mHorari.size(); i++) {
//            event = mHorari.get(i);
//            horaEventInici = event.getmHoraInici().split(":");
//            horaEventFi = event.getmHoraFi().split(":");
//            int horaIniciEvent = Integer.parseInt(horaEventInici[0]);
//            int horaFiEvent = Integer.parseInt(horaEventFi[0]);
//
//            if (inici >= horaIniciEvent && fi <= horaFiEvent) {
//                EventHorari evAux = new EventHorari();
//                evAux.setmAssignatura(mHorari.get(i).getmAssignatura());
//                evAux.setmAny(mHorari.get(i).getmAny());
//                evAux.setmAula(mHorari.get(i).getmAula());
//                evAux.setmDia(mHorari.get(i).getmDia());
//                evAux.setmHoraFi(mHorari.get(i).getmHoraFi());
//                evAux.setmHoraInici(mHorari.get(i).getmHoraInici());
//                evAux.setmMes(mHorari.get(i).getmMes());
//                eh.add(evAux);
//            }
//        }
//        return eh;
//    }
//
//    private void ordenarPerData() {
//        int minIndex;
//        EventHorari rig, rag;
//        int n = mHorari.size();
//        String[] horaEventIniciRig;
//        String[] horaEventIniciRag;
//        for (int i = 0; i < n - 1; i++) {
//            minIndex = i;
//            for (int j = i + 1; j < n; j++) {
//                rig = mHorari.get(j);
//                rag = mHorari.get(minIndex);
//                horaEventIniciRig = rig.getmHoraInici().split(":");
//                horaEventIniciRag = rag.getmHoraInici().split(":");
//                if (Integer.parseInt(horaEventIniciRig[0]) < (Integer
//                        .parseInt(horaEventIniciRag[0]))) {
//                    minIndex = j;
//                }
//            }
//            if (minIndex != i) {
//                Collections.swap(mHorari, i, minIndex);
//            }
//        }
//    }
//
//    private void omplirEspaisHorari() {
//        int diferencia = 0;
//        if (mHorari != null) {
//            diferencia = Math.abs(mHorari.size() - sHores.length);
//        } else {
//            mHorari = new ArrayList<EventHorari>();
//            diferencia = Math.abs(0 - sHores.length);
//        }
//
//        Calendar cDataNoValida = Calendar.getInstance();
//        cDataNoValida.set(Calendar.YEAR, mDiaActual.get(Calendar.YEAR) + 1);
//        cDataNoValida.set(Calendar.MONTH, mDiaActual.get(Calendar.MONTH));
//        cDataNoValida.set(Calendar.DAY_OF_MONTH,
//                mDiaActual.get(Calendar.DAY_OF_MONTH));
//        Date dataNoValida = cDataNoValida.getTime();
//
//        for (int i = 0; i < diferencia; i++) {
//            EventHorari ehTemp = new EventHorari("23:00", "23:00", "", "",
//                    dataNoValida.getDay(), dataNoValida.getMonth(),
//                    dataNoValida.getYear());
//            mHorari.add(ehTemp);
//        }
//    }
//
//    private void crearBotonsAbansDespres() {
//
//        mSeguent = (ImageButton) findViewById(R.id.seguent);
//        // No mostrem el bot\F3 perqu\E8 no hi ha dades
//        mSeguent.setVisibility(AndroidUtils.INVISIBLE);
//        mSeguent.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                Intent horariConcret = new Intent(getParent(),
//                        ControladorVistaHorariConcret.class);
//                horariConcret.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                horariConcret.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                horariConcret.putExtra("any", mDiaActual.get(Calendar.YEAR));
//                horariConcret.putExtra("mes", mDiaActual.get(Calendar.MONTH));
//                horariConcret.putExtra("dia",
//                        mDiaActual.get(Calendar.DAY_OF_MONTH));
//                horariConcret.putExtra("queEs", "despres");
//                horariConcret.putExtra("diesConsultats", mDiesConsultats + 1);
//                ControladorTabGroupHorari parentActivity = (ControladorTabGroupHorari) getParent();
//                parentActivity.startChildActivity(
//                        "ControladorVistaHorariConcret", horariConcret);
//            }
//        });
//
//        mAnterior = (ImageButton) findViewById(R.id.abans);
//        // No mostrem el bot\F3 perqu\E8 no hi ha dades
//        mAnterior.setVisibility(AndroidUtils.INVISIBLE);
//        mAnterior.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                Intent horariConcret = new Intent(getParent(),
//                        ControladorVistaHorariConcret.class);
//                horariConcret.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                horariConcret.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                horariConcret.putExtra("any", mDiaActual.get(Calendar.YEAR));
//                horariConcret.putExtra("mes", mDiaActual.get(Calendar.MONTH));
//                horariConcret.putExtra("dia",
//                        mDiaActual.get(Calendar.DAY_OF_MONTH));
//                horariConcret.putExtra("queEs", "abans");
//                horariConcret.putExtra("diesConsultats", mDiesConsultats - 1);
//                ControladorTabGroupHorari parentActivity = (ControladorTabGroupHorari) getParent();
//                parentActivity.startChildActivity(
//                        "ControladorVistaHorariConcret", horariConcret);
//            }
//        });
//
//        /** El tema del DatePicker */
//        mEscollirDia = (ImageButton) findViewById(R.id.escollirDia);
//        mEscollirDia.setVisibility(AndroidUtils.INVISIBLE);
//        mEscollirDia.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent datePicker = new Intent(getParent(),
//                        ControladorDatePickerHorari.class);
//                datePicker.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                datePicker.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                datePicker.putExtra("username",
//                        sPrefs.getString(AndroidUtils.USERNAME, ""));
//                datePicker.putExtra("queEs", "mostrarDatePicker");
//                startActivity(datePicker);
//            }
//        });
//
//    }
//
//    /** Gesti\F3 del Men\FA */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.zona_raco, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onMenuItemSelected(int featureId, MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.actualitza:
//                if (hihaInternet()) {
//                    mBdm.open();
//                    mBdm.deleteTableHorari();
//                    mBdm.close();
//                    Toast.makeText(getParent().getBaseContext(),
//                            R.string.infoActualitzaHorari, Toast.LENGTH_LONG)
//                            .show();
//                } else {
//                    Toast.makeText(getApplicationContext(), R.string.hiha_internet,
//                            Toast.LENGTH_LONG).show();
//                }
//                break;
//            case R.id.logout:
//                sPrefs.edit().clear().commit();
//                Toast.makeText(getParent(), R.string.logout_correcte,
//                        Toast.LENGTH_SHORT).show();
//                mBdm.open();
//                mBdm.deleteTablesLogout();
//                mBdm.close();
//                startActivity(new Intent(getParent(), ControladorTabIniApp.class));
//                break;
//
//        }
//        return true;
//    }
//}
}