package com.example.mcabezas.racomobile.Adapter;

/**
 * Created by mcabezas on 11/02/16.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mcabezas.racomobile.Connect.AndroidUtils;
import com.example.mcabezas.racomobile.ImageManager;
import com.example.mcabezas.racomobile.Model.ItemGeneric;
import com.example.mcabezas.racomobile.R;

import java.util.ArrayList;

public class PostItemAdapter extends BaseAdapter {

    private Activity mActivityAct;
    private String[] mLUrlImatges;
    private LayoutInflater mInflater;
    public ImageManager gestorIm;
    private ArrayList<ItemGeneric> mLItems;

    public PostItemAdapter(Activity a, String[] im, ArrayList<ItemGeneric> it) {

        // Preparem la classe per gestionar la llistes
        mActivityAct = a;
        mLUrlImatges = im;
        mLItems = it;
        mInflater = (LayoutInflater) mActivityAct
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gestorIm = new ImageManager(mActivityAct.getApplicationContext());
    }

    public static class VistaH {
        public TextView titol;
        public ImageView image;
        public TextView data;
    }

    @Override
    public int getCount() {
        return mLUrlImatges.length;
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
        VistaH vh = null;
        String data = "";

        // Formategem la data perquè surti com volem
        if (mLItems.get(position).getDataPub() != null) {
            data = AndroidUtils.dateToStringVistaInici(mLItems.get(position).getDataPub());
        }

        // Noticies
        if (mLItems.get(position).getTipus() == 0) {
            vi = mInflater.inflate(R.layout.post_item, null);
            vh = new VistaH();
            vh.titol = (TextView) vi.findViewById(R.id.postTitleLabel);
            vh.image = (ImageView) vi.findViewById(R.id.postThumb);
            vh.data = (TextView) vi.findViewById(R.id.postDateLabel);
            vh.titol.setTextColor(Color.rgb(106,152,233));
            vi.setTag(vh);
            vi.setBackgroundColor(Color.WHITE);

            vh.data.setText(data);
        }


        vh.titol.setText(mLItems.get(position).getTitol().replaceAll("\"", ""));
        vh.image.setTag(mLUrlImatges[position]);
        gestorIm.MostrarImatge(mLUrlImatges[position], vh.image);
        return vi;
    }
}


