package com.example.mcabezas.racomobile.Fragments;

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
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.example.mcabezas.racomobile.Connect.AndroidUtils;
import com.example.mcabezas.racomobile.Connect.GestioActualitzaLlistesActivity;
import com.example.mcabezas.racomobile.Connect.IcalParser;
import com.example.mcabezas.racomobile.LlistesItems;
import com.example.mcabezas.racomobile.Model.BaseDadesManager;
import com.example.mcabezas.racomobile.Model.EventHorari;
import com.example.mcabezas.racomobile.Model.PreferenciesUsuari;
import com.example.mcabezas.racomobile.R;

/**
 * Created by mcabezas on 15/02/16.
 */
public class ControladorHorario extends GestioActualitzaLlistesActivity
        implements Runnable ,WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {

    private final String mTAG = "ControladorHorari";
    private static ArrayList<EventHorari> mHorari = new ArrayList<EventHorari>();
    private Calendar mDiaActual;
    // volem carregar declarat a AndroidUtils
    private List<Integer> colors;
    private List<String> mAssignatures;

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;
    private SharedPreferences sPrefs;


    //Fragment heredado

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.horario, container, false);

        sPrefs = getActivity().getSharedPreferences(
                PreferenciesUsuari.getPreferenciesUsuari(), Context.MODE_PRIVATE);

        colors = new ArrayList<>();
        mAssignatures = new ArrayList<>();

        colors.add(getResources().getColor(R.color.event_color_01));
        colors.add(getResources().getColor(R.color.event_color_02));
        colors.add(getResources().getColor(R.color.event_color_03));
        colors.add(getResources().getColor(R.color.event_color_04));

        // Gestionar Base de dades
        mBdm = new BaseDadesManager(getActivity());

        // Creem la data Actual que apareix en la vista
        mDiaActual = Calendar.getInstance();

        int mes = mDiaActual.get(Calendar.MONTH);
        mes++;
        String dia;
        if (mDiaActual.get(Calendar.DAY_OF_MONTH) < 10) {
            dia = "0" + String.valueOf(mDiaActual.get(Calendar.DAY_OF_MONTH));
        } else {
            dia = String.valueOf(mDiaActual.get(Calendar.DAY_OF_MONTH));
        }


        mBdm.open();
        int res = mBdm.getAllHorariSize();
        mHorari = mBdm.getAllHorari();
        mBdm.close();

        mWeekView = (WeekView) rootView.findViewById(R.id.weekView);

        mWeekView.goToHour(8);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);

        if(mHorari.size() == 0) obtenirDadesWeb();
        ordenarPerData();
        actualitzarTaula();
        setHasOptionsMenu(true);

        return rootView;
    }


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
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
    }


    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        WeekViewEvent event = null;
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        if(newYear == mHorari.get(0).getmAny() && newYear == mHorari.get(mHorari.size()-1).getmAny()) {
            for (int i = 0; (i < mHorari.size()); ++i) {
                if(mHorari.get(i).getmMes() == newMonth) {
                    if(dosHoras(mHorari.get(i).getmHoraInici(),mHorari.get(i).getmHoraFi())) {
                        startTime = Calendar.getInstance();
                        startTime.set(Calendar.DAY_OF_MONTH, mHorari.get(i).getmDia());
                        startTime.set(Calendar.HOUR_OF_DAY, getHora(mHorari.get(i).getmHoraInici()));
                        startTime.set(Calendar.MINUTE, 0);
                        startTime.set(Calendar.MONTH, newMonth);
                        startTime.set(Calendar.YEAR, newYear);
                        endTime = (Calendar) startTime.clone();
                        endTime.add(Calendar.HOUR_OF_DAY, 2);
                        event = new WeekViewEvent(4, mHorari.get(i).getmAssignatura() +"\n" +mHorari.get(i).getmAula(), startTime, endTime);
                        for(int j = 0; j < mAssignatures.size();++j) {
                            if(mHorari.get(i).getmAssignatura().substring(0,mHorari.get(i).getmAssignatura().length()-2).equals(mAssignatures.get(j))) {
                                if(j > (colors.size()+1)) {
                                    int resto = j % colors.size()+1;
                                    event.setColor(colors.get(resto));
                                }
                                else event.setColor(colors.get(j));
                            }
                        }
                        events.add(event);
                    }
                    else {
                        startTime = Calendar.getInstance();
                        startTime.set(Calendar.DAY_OF_MONTH, mHorari.get(i).getmDia());
                        startTime.set(Calendar.HOUR_OF_DAY, getHora(mHorari.get(i).getmHoraInici()));
                        startTime.set(Calendar.MINUTE, 0);
                        startTime.set(Calendar.MONTH, newMonth);
                        startTime.set(Calendar.YEAR, newYear);
                        endTime = (Calendar) startTime.clone();
                        endTime.add(Calendar.HOUR_OF_DAY, 1);
                        event = new WeekViewEvent(4, mHorari.get(i).getmAssignatura() +"\n" +mHorari.get(i).getmAula(), startTime, endTime);
                        for(int j = 0; j < mAssignatures.size();++j) {
                            if (mHorari.get(i).getmAssignatura().equals(mAssignatures.get(j))) {
                                if (j >= colors.size()) {
                                    int resto = colors.size() % j;
                                    event.setColor(colors.get(resto));
                                } else event.setColor(colors.get(j));
                            }
                            events.add(event);
                        }
                    }
                }
            }
         }
        return events;
    }

    private boolean dosHoras(String s2, String s1) {
        if(s1.length() == 5) {
            s1 = s1.substring(0,2);
        }
        else s1 = s1.substring(0,1);
        if(s2.length() == 5) {
            s2 = s2.substring(0,2);
        }
        else s2 = s2.substring(0,1);
        if((Integer.parseInt(s1))-(Integer.parseInt(s2)) == 2) {
            return true;
        }
        else return false;
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
                Toast.makeText(getActivity(), "Error al cargar horario",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {

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

    }

    @Override
    protected void obtenirDadesWeb() {
        AndroidUtils au = AndroidUtils.getInstance();
        // Preparem les URL i les connexions per obtenir les dades
        try {
            String keyURL = sPrefs.getString(au.KEY_HORARI_RACO, "");
            URL not;

            not = au.crearURL(au.URL_HORARI_RACO + keyURL);
            IcalParser ip = IcalParser.getInstance();
            mHorari = ip.parserHorariComplet(not);
            mBdm.open();
            for(int i = 0; i < mHorari.size();++i) {
                mBdm.insertItemHorari(mHorari.get(i).getmHoraInici(),mHorari.get(i).getmHoraFi(),mHorari.get(i).getmAssignatura(),
                        mHorari.get(i).getmAula(),mHorari.get(i).getmDia(),mHorari.get(i).getmMes(),mHorari.get(i).getmAny());
            }
            mBdm.close();


        } catch (MalformedURLException e) {
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
    }


    private void ordenarPerData() {
        int minIndex;
        EventHorari rig, rag;
        int n = mHorari.size();
        String[] horaEventIniciRig;
        String[] horaEventIniciRag;
        for (int i = 0; i < n - 1; i++) {
            if(mAssignatures.isEmpty()) {
                mAssignatures.add(mHorari.get(i).getmAssignatura().substring(0,mHorari.get(i).getmAssignatura().length()-2));
            }
            else {
                if(!mAssignatures.contains(mHorari.get(i).getmAssignatura().substring(0,mHorari.get(i).getmAssignatura().length()-2))) {
                    mAssignatures.add(mHorari.get(i).getmAssignatura().substring(0,mHorari.get(i).getmAssignatura().length()-2));
                }
            }
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


    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        inflater.inflate(R.menu.main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.goToHour(8);

                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.goToHour(8);

                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(5);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.goToHour(8);

                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}