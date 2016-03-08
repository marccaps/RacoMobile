package com.example.mcabezas.racomobile.Model;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.example.mcabezas.racomobile.R;

/**
 * Created by mcabezas on 10/02/16.
 */
public class UserPreferences extends Activity {

    private String sTAG = "UserPreferences";
    private static final String RACO_PREFS = "PREF_USUARI_ANDROID_RACO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static String getPreferenciesUsuari() {
        return RACO_PREFS;
    }
}
