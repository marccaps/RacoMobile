package com.example.mcabezas.racomobile.Model;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.example.mcabezas.racomobile.R;
import com.thefinestartist.finestwebview.FinestWebView;

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

        String descripcion_aviso = getIntent().getStringExtra("DESCRIPCION_AVISO").toString().replace("<p>", "").replace("</p>", "\n")
                .replace("&", "").replace("nbsp;", "").replace("acute;", "").replace("grave;", "");

        descripcion_aviso = replace_this('<','>',descripcion_aviso);

        descripcion_aviso.replace(">","");

        mDescripcionAviso.setText(descripcion_aviso);
        mTituloAviso.setText(getIntent().getStringExtra("TITULO_AVISO").toString());

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
}
