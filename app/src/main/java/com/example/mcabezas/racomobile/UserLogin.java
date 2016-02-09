package com.example.mcabezas.racomobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

/**
 * Created by mcabezas on 4/02/16.
 */
public class UserLogin extends Activity{

    private static final String TAG = "UserLogin";

    private SharedPreferences mSharedPreferences;
    private static final String REQ_TOKEN = "REQ_TOKEN";
    private static final String ACC_TOKEN = "ACC_TOKEN";

    OAuthProvider provider = new DefaultOAuthProvider(
            "https://raco.fib.upc.edu/oauth/request_token",
            "https://raco.fib.upc.edu/oauth/access_token",
            "https://raco.fib.upc.edu/oauth/protected/authorize");

    String callback = "raco://raco";
    String consumer_key = "2af15883-d54f-4610-9f08-6a541cf3daff";
    String consumer_secret = "f2652e24-2dea-4423-8bf7-e65ec7001b08";
    DefaultOAuthConsumer consumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);

        consumer = new DefaultOAuthConsumer(consumer_key,consumer_secret);
        new DemanaRequestTokenAsync().execute();

//        FloatingActionButton registerFab = (FloatingActionButton) findViewById(R.id.register_fab);
//        registerFab.setVisibility(View.GONE);

//        final MaterialLoginView login = (MaterialLoginView) findViewById(R.id.login);
//        login.setListener(new MaterialLoginViewListener() {
//            @Override
//            public void onRegister(TextInputLayout registerUser, TextInputLayout registerPass, TextInputLayout registerPassRep) {
//                //Handle register
//            }
//
//            @Override
//            public void onLogin(TextInputLayout loginUser, TextInputLayout loginPass) {
//                //Handle login
//                try {
//                    Log.d(TAG, " " + getNom());
//                } catch (JSONException e) {
//                    Log.d(TAG, e.getMessage());
//                }
//            }
//        });
    }

    private void guardaTokens() {

//        String requestToken = provider.getRequestTokenEndpointUrl();
//        String accessToken = provider.getAccessTokenEndpointUrl();
//        SharedPreferences.Editor e = mSharedPreferences.edit();
//        e.putString(REQ_TOKEN,requestToken);
//        e.putString(ACC_TOKEN,accessToken);
//        e.commit();

    }

    private String recuperaTokens(boolean who) {
//        if(!who) {
//            String tokens = null;
//            tokens = mSharedPreferences.getString(REQ_TOKEN, "null");
//            return tokens;
//        }
//        else {
//            String tokens = null;
//            tokens = mSharedPreferences.getString(ACC_TOKEN,"null");
//            return tokens;
//        }
        return "Hello";
    }

    @Override
    public void onResume() {
        super.onResume();
        Uri uri = this.getIntent().getData();
        recuperaTokens(true);

        // Aixo es el cas en que tornem amb token. Si no, no fem res
        if (uri != null && uri.toString().startsWith(callback)) {
            new DemanaAccessTokenAsync().execute();
        }
    }

    class DemanaAccessTokenAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                provider.retrieveAccessToken(consumer,null);
                recuperaTokens(true);
            } catch (Exception e) {
                Log.d("oauth","ha petat al access token"+ e);
            } return null;
        }
        // Una vegada tinc el access token i secret, ja puc fer feina...
        @Override
        protected void onPostExecute(Void result) {
            // Aqui el consumer s'ha actualitzat amb els 2 access tokens que ja li permeten fer les peticions
            // als nostres webservices. Aquests valor s'haurien de guardar i abans de tot fer un
            // consumer.setTokenWithSecret si existeixen.
        }
    }

    class DemanaRequestTokenAsync extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String authURL = null;
            try {
                authURL = provider.retrieveRequestToken(consumer, callback);
                guardaTokens();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return authURL;
        }

        // Una vegada tinc el token obro ja el navegador. D'event en event...
        // Important! Abans de continguar he de guardar els tokens

        @Override
        protected void onPostExecute(String authURL) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authURL)));
        }
    }

    public String getNom() throws JSONException {
        String json = demana("https://raco.fib.upc.edu/api-v1/info-personal.json");
        JSONObject jObject = new JSONObject(json);
        return jObject.getString("nom");
    }

    private String demana (String url) {
        StringBuffer aux=new StringBuffer();
        try {
            URL u = new URL(url);
            HttpURLConnection request = (HttpURLConnection) u.openConnection();
            consumer.sign(request);
            BufferedReader in = new BufferedReader(new InputStreamReader( request.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                aux.append(inputLine);
            }
        } catch (Exception e) {
            Log.i("oauth"," " +e.getMessage());
        }
        return aux.toString();
    }
}
