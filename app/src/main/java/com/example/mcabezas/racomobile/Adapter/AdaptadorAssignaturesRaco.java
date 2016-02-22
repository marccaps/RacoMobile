package com.example.mcabezas.racomobile.Adapter;

/**
 * Created by mcabezas on 19/02/16.
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mcabezas.racomobile.Connect.AndroidUtils;
import com.example.mcabezas.racomobile.Model.AssignaturesAvisos;
import com.example.mcabezas.racomobile.Model.ItemGeneric;
import com.example.mcabezas.racomobile.R;


/**
 * Common base class of common implementation for an Adapter that can be used in
 * both ListView (by implementing the specialized ListAdapter interface} and
 * Spinner (by implementing the specialized SpinnerAdapter interface
 */

public class AdaptadorAssignaturesRaco extends BaseAdapter {

    private Activity mActivity;
    private LayoutInflater mInflater;
    private ArrayList<ItemGeneric> mLitems;

    public AdaptadorAssignaturesRaco(Activity a,
                                     ArrayList<ItemGeneric> it) {
        mActivity = a;
        mLitems = it;
        mInflater = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class VistaH {
        public TextView titol;
        public TextView data;
    }


    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            View vi = convertView;
            VistaH vh;
            vi = mInflater.inflate(R.layout.llista_assignatures_raco, null);
            vh = new VistaH();
            vh.titol = (TextView) vi.findViewById(R.id.nomAssigRaco);
            if(mLitems.get(position).getDescripcio().equals("NomAssignatura")) {
                vh.titol.setText(mLitems.get(position).getTitol());
                vh.titol.setTextColor(parent.getResources().getColor(R.color.list_background_pressed));
                vi.setBackgroundColor(parent.getResources().getColor(R.color.list_background));
            }
            else vh.titol.setText(mLitems.get(position).getTitol());
            vi.setTag(vh);
            return vi;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getCount() {
        return mLitems.size();
    }

}
