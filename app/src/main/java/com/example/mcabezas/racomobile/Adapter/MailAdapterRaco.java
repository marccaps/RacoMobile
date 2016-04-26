package com.example.mcabezas.racomobile.Adapter;

/**
 * Created by mcabezas on 17/02/16.
 */
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.mcabezas.racomobile.Model.Mail;
import com.example.mcabezas.racomobile.R;

import java.util.ArrayList;


/**
 * Common base class of common implementation for an Adapter that can be used in
 * both ListView (by implementing the specialized ListAdapter interface} and
 * Spinner (by implementing the specialized SpinnerAdapter interface
 */

public class MailAdapterRaco extends BaseAdapter {

    private Activity mActivity;
    private LayoutInflater mInflater;
    private ArrayList<Mail> mLitems;

    public MailAdapterRaco(Activity a, ArrayList<Mail> it) {
        mActivity = a;
        mLitems = it;
        mInflater = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class VistaH {
        public ImageView item_image;
        public TextView item_titol;
        public TextView item_nombre;
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

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color1 = generator.getRandomColor();
        vi = mInflater.inflate(R.layout.mail_list, null);
        vh = new VistaH();
        vh.item_image = (ImageView) vi.findViewById(R.id.item_correo_imagen);
        vh.item_titol = (TextView) vi.findViewById(R.id.item_correo_titulo);
        vh.item_nombre =(TextView) vi.findViewById(R.id.item_correo_nombre);
        TextDrawable textDrawable;
        if(mLitems.get(position).getDescripcio() != null) {
            textDrawable = (TextDrawable) TextDrawable.builder().
                    beginConfig().width(60).height(60).endConfig().buildRoundRect(Html.fromHtml(mLitems.get(position).getDescripcio()).subSequence(1,2).toString().toUpperCase(), color1, 10);
        }
        else {
            textDrawable = (TextDrawable) TextDrawable.builder().
                    beginConfig().width(60).height(60).endConfig().buildRoundRect("R", color1, 10);
        }
        vh.item_image.setImageDrawable(textDrawable);

        if(mLitems.get(position).getTitol().equals(" ")) {
            vh.item_titol.setText("Sense assumpte");
        }
        else {
            vh.item_titol.setText(Html.fromHtml(mLitems.get(position).getTitol()));
        }
        vh.item_titol.setTextColor(Color.GRAY);
        vh.item_nombre.setText(Html.fromHtml(mLitems.get(position).getDescripcio()));
        vi.setTag(vh);
        vi.setBackgroundColor(Color.WHITE);

        return vi;
    }

    @Override
    public int getCount() {
        return mLitems.size();
    }


}
