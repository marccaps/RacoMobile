package com.example.mcabezas.racomobile.Model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.mcabezas.racomobile.OAuthFlowApp;
import com.example.mcabezas.racomobile.R;
import com.thefinestartist.finestwebview.FinestWebView;

import me.grantland.widget.AutofitHelper;

/**
 * Created by mcabezas on 23/02/16.
 */
public class BodyNews extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.body_news);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        TextView titulo = (TextView) findViewById(R.id.titulo_noticia);
        TextView descripcion = (TextView) findViewById(R.id.descripcion_noticia);

        com.melnykov.fab.FloatingActionButton button = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.web_noticia);

        titulo.setMovementMethod(new ScrollingMovementMethod());
        descripcion.setMovementMethod(new ScrollingMovementMethod());

        descripcion.setText(getIntent().getStringExtra("DESCRIPCION").toString());
        titulo.setText(getIntent().getStringExtra("TITULO").toString());

        AutofitHelper.create(titulo);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();


        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.mipmap.back);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new FinestWebView.Builder(BodyNews.this).show(getIntent().getStringExtra("URL_NOTICIA").toString());

            }
        });

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
