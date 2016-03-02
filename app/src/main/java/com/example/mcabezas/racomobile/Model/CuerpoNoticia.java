package com.example.mcabezas.racomobile.Model;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mcabezas.racomobile.R;
import com.thefinestartist.finestwebview.FinestWebView;

/**
 * Created by mcabezas on 23/02/16.
 */
public class CuerpoNoticia extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuerpo_noticia);

        TextView titulo = (TextView) findViewById(R.id.titulo_noticia);
        TextView descripcion = (TextView) findViewById(R.id.descripcion_noticia);

        com.melnykov.fab.FloatingActionButton button = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.web_noticia);

        titulo.setMovementMethod(new ScrollingMovementMethod());
        descripcion.setMovementMethod(new ScrollingMovementMethod());


        descripcion.setText(getIntent().getStringExtra("DESCRIPCION").toString());
        titulo.setText(getIntent().getStringExtra("TITULO").toString());


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new FinestWebView.Builder(CuerpoNoticia.this).show(getIntent().getStringExtra("URL_NOTICIA").toString());

            }
        });

    }

}
