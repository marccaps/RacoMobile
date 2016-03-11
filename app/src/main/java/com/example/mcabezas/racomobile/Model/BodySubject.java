package com.example.mcabezas.racomobile.Model;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.mcabezas.racomobile.R;
import com.thefinestartist.finestwebview.FinestWebView;

import java.io.UnsupportedEncodingException;

import me.grantland.widget.AutofitHelper;

/**
 * Created by mcabezas on 25/02/16.
 */

public class BodySubject extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.body_subject);

        TextView mTituloAviso = (TextView) findViewById(R.id.titulo_aviso);
        TextView mDescripcionAviso = (TextView) findViewById(R.id.descripcion_aviso);
        com.melnykov.fab.FloatingActionButton button = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.web_avisos);

        mTituloAviso.setMovementMethod(new ScrollingMovementMethod());
        mDescripcionAviso.setMovementMethod(new ScrollingMovementMethod());

        String descripcion_aviso = null;
        try {
            descripcion_aviso = new String(getIntent().getStringExtra("DESCRIPCION_AVISO").toString().getBytes("ISO-8859-1"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mDescripcionAviso.setText(Html.fromHtml(descripcion_aviso));
        mTituloAviso.setText(getIntent().getStringExtra("TITULO_AVISO").toString());

        AutofitHelper.create(mTituloAviso);


        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();


        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.back);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FinestWebView.Builder(BodySubject.this).show(getIntent().getStringExtra("URL_AVISOS"));
            }
        });

    }

    private String replace_this(char first, char last,String text) {

        boolean inside = false;
        String content = "";
        for(int i = 0; i < text.length();++i) {
            if(text.charAt(i) == first) {
                inside = true;
            }
            if(text.charAt(i) == last) {
                inside = false;
            }
            if(!inside) {
                content = content+text.charAt(i);
            }
        }
        return content;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // One of the group items (using the onClick attribute) was clicked
        // The item parameter passed here indicates which item it is
        // All other menu item clicks are handled by onOptionsItemSelected()
        int id = item.getItemId();
        switch (id) {
            default:
                // Handle action bar actions click
                onBackPressed();
                return true;

        }
    }
}
