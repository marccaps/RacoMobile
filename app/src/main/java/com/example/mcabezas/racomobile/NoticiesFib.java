package com.example.mcabezas.racomobile;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mcabezas.racomobile.Adapter.PostItemAdapter;
import com.example.mcabezas.racomobile.Connect.GestioActualitzaLlistesActivity;
import com.example.mcabezas.racomobile.Model.ItemGeneric;
import com.example.mcabezas.racomobile.Model.PostData;

import java.util.ArrayList;

/**
 * Created by mcabezas on 10/02/16.
 */
public class NoticiesFib extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.noticies_fib, container, false);

//        this.generateDummyData();


        return rootView;
    }


//    private void generateDummyData() {
//        PostData data = null;
//        listData = new PostData[10];
//        for (int i = 0; i < 10; i++) { //please ignore this comment :>
//            data = new PostData();
//            data.postDate = "May 20, 2013";
//            data.postTitle = "Post " + (i + 1) + " Title: This is the Post Title from RSS Feed";
//            data.postThumbUrl = null;
//            listData[i] = data;
//        }
//    }

    public NoticiesFib() {

    }
}

