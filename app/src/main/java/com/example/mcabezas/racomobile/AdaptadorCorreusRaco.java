package com.example.mcabezas.racomobile;

/**
 * Created by mcabezas on 17/02/16.
 */
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mcabezas.racomobile.Model.Correu;

import java.util.ArrayList;


/**
 * Common base class of common implementation for an Adapter that can be used in
 * both ListView (by implementing the specialized ListAdapter interface} and
 * Spinner (by implementing the specialized SpinnerAdapter interface
 */

public class AdaptadorCorreusRaco extends BaseAdapter {

    private Activity mActivity;
    private LayoutInflater mInflater;
    private ArrayList<Correu> mLitems;

    public AdaptadorCorreusRaco(Activity a, ArrayList<Correu> it) {
        mActivity = a;
        mLitems = it;
        mInflater = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class VistaH {
        public TextView titol;
        public TextView descripcio;
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
        View vi = convertView;
        VistaH vh;
        vi = mInflater.inflate(R.layout.llista_correu, null);
        vh = new VistaH();
        vh.titol = (TextView) vi.findViewById(R.id.nom_event);
        vh.descripcio = (TextView) vi.findViewById(R.id.descripcio_event);
        vi.setTag(vh);

        if (position % 2 == 0) {
            vi.setBackgroundResource(R.color.list_background);
        }

        vh.titol.setText(mLitems.get(position).getTitol().replaceAll("\"", ""));
        String enviatPer ="Enviat per";
        vh.descripcio.setText(enviatPer + " "
                + mLitems.get(position).getDescripcio().replaceAll("\"", ""));

        return vi;
    }

    @Override
    public int getCount() {
        return mLitems.size();
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

}
