package com.example.mcabezas.racomobile.Adapter;

/**
 * Created by mcabezas on 11/02/16.
 */
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mcabezas.racomobile.AndroidUtils;
import com.example.mcabezas.racomobile.GestorImatges;
import com.example.mcabezas.racomobile.Model.ItemGeneric;
import com.example.mcabezas.racomobile.R;

import java.util.ArrayList;

public class PostItemAdapter extends BaseAdapter {
//    private Activity myContext;
//    private PostData[] datas;
//
//    public PostItemAdapter(Context context, int textViewResourceId,
//                           PostData[] objects) {
//        super(context, textViewResourceId, objects);
//        // TODO Auto-generated constructor stub
//        myContext = (Activity) context;
//        datas = objects;
//    }
//
//
//
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View rowView;
//
//        if(convertView == null) {
//            LayoutInflater inflater = myContext.getLayoutInflater();
//            convertView = inflater.inflate(R.layout.post_item, null);
//        }
//
//        rowView = convertView;
//        ImageView thumbImageView = (ImageView) rowView
//                .findViewById(R.id.postThumb);
//        //check if the datas[position].postThumbUrl is null
//        if (datas[position].postThumbUrl == null) {
//            thumbImageView.setImageResource(R.mipmap.ic_launcher);
//        }
//
//        TextView postTitleView = (TextView) rowView
//                .findViewById(R.id.postTitleLabel);
//        postTitleView.setText(datas[position].postTitle);
//
//        TextView postDateView = (TextView) rowView
//                .findViewById(R.id.postDateLabel);
//        postDateView.setText(datas[position].postDate);
//
//        return rowView;
//    }

    private Activity mActivityAct;
    private String[] mLUrlImatges;
    private LayoutInflater mInflater;
    public GestorImatges gestorIm;
    private ArrayList<ItemGeneric> mLItems;

    public PostItemAdapter(Activity a, String[] im, ArrayList<ItemGeneric> it) {

        // Preparem la classe per gestionar la llistes
        mActivityAct = a;
        mLUrlImatges = im;
        mLItems = it;
        mInflater = (LayoutInflater) mActivityAct
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gestorIm = new GestorImatges(mActivityAct.getApplicationContext());
    }

    public static class VistaH {
        public TextView titol;
        public ImageView image;
        public ImageView barra;
        public ImageView fletxa;
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
            vi.setTag(vh);
            vh.data.setText(data);
        }
//        // Correu
//        if (mLItems.get(position).getTipus() == 1) {
//            vi = mInflater.inflate(R.layout.llista_events_resum, null);
//            vh = new VistaH();
//            vh.titol = (TextView) vi.findViewById(R.id.titol);
//            vh.image = (ImageView) vi.findViewById(R.id.image);
//            vh.barra = (ImageView) vi.findViewById(R.id.barra);
//            vh.barra.setImageResource(R.drawable.barra_lila);
//            vh.data = (TextView) vi.findViewById(R.id.dataIni);
//            vh.fletxa = (ImageView) vi.findViewById(R.id.fletxa);
//            vh.fletxa.setImageResource(R.drawable.fletxa_celda);
//            vi.setTag(vh);
//            vh.data.setText(data);
//        }
//        // Avisos
//        if (mLItems.get(position).getTipus() == 2) {
//            vi = mInflater.inflate(R.layout.llista_events_resum, null);
//            vh = new VistaH();
//            vh.titol = (TextView) vi.findViewById(R.id.titol);
//            vh.image = (ImageView) vi.findViewById(R.id.image);
//            vh.barra = (ImageView) vi.findViewById(R.id.barra);
//            vh.barra.setImageResource(R.drawable.barra_vermella);
//            vh.data = (TextView) vi.findViewById(R.id.dataIni);
//            vh.fletxa = (ImageView) vi.findViewById(R.id.fletxa);
//            vh.fletxa.setImageResource(R.drawable.fletxa_celda);
//            vi.setTag(vh);
//            vh.data.setText(data);
//        }
//        // Configuracio
//        if (mLItems.get(position).getTipus() == 3) {
//            vi = mInflater.inflate(R.layout.llista_events_resum, null);
//            vh = new VistaH();
//            vh.titol = (TextView) vi.findViewById(R.id.titol);
//            vh.image = (ImageView) vi.findViewById(R.id.image);
//            vi.setTag(vh);
//        }

        vh.titol.setText(mLItems.get(position).getTitol().replaceAll("\"", ""));
        vh.image.setTag(mLUrlImatges[position]);
        gestorIm.MostrarImatge(mLUrlImatges[position], vh.image);
        return vi;
    }
}


