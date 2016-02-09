package com.example.mcabezas.racomobile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import shem.com.materiallogin.MaterialLoginView;
import shem.com.materiallogin.MaterialLoginViewListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Contacts.People;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


/**
 * Entry point in the application.
 * Launches the OAuth flow by starting the PrepareRequestTokenActivity
 *
 */
public class OAuthFlowApp extends Activity {

	private static final int PICK_CONTACT = 0;
	final String TAG = getClass().getName();
	private SharedPreferences prefs;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.user_login);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

		FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.register_fab);
		floatingActionButton.setVisibility(View.GONE);

		final MaterialLoginView login = (MaterialLoginView) findViewById(R.id.login);
		login.setListener(new MaterialLoginViewListener() {
			@Override
			public void onRegister(TextInputLayout registerUser, TextInputLayout registerPass, TextInputLayout registerPassRep) {
				//Handle register
			}

			@Override
			public void onLogin(TextInputLayout loginUser, TextInputLayout loginPass) {
				//Handle login
				startActivity(new Intent().setClass(getApplicationContext(), PrepareRequestTokenActivity.class));
			}
		});

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

        performApiCall();
    }

	private void performApiCall() {

		String jsonOutput = "";
        try {
        	jsonOutput = doGet(Constants.API_REQUEST,getConsumer(this.prefs));
        	System.out.println("jsonOutput : " + jsonOutput);
        	Log.i(TAG,jsonOutput);
		} catch (Exception e) {
			Log.e(TAG, "Error executing request",e);
		}
	}
	
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		  super.onActivityResult(reqCode, resultCode, data);

		  switch (reqCode) {
		    case (PICK_CONTACT) :
		      if (resultCode == Activity.RESULT_OK) {
		        Uri contactData = data.getData();
		        Cursor c =  managedQuery(contactData, null, null, null, null);
		        if (c.moveToFirst()) {
		          String name = c.getString(c.getColumnIndexOrThrow(People.NAME));
		          Log.i(TAG,"Response : " + "Selected contact : " + name);
		        }
		      }
		      break;
		  }
		}

    private void clearCredentials() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Editor edit = prefs.edit();
		edit.remove(OAuth.OAUTH_TOKEN);
		edit.remove(OAuth.OAUTH_TOKEN_SECRET);
		edit.commit();
	}

	
	private OAuthConsumer getConsumer(SharedPreferences prefs) {
		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
		consumer.setTokenWithSecret(token, secret);
		return consumer;
	}

	private String doGet(String url,OAuthConsumer consumer) throws Exception {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		Log.i(TAG,"Requesting URL : " + url);
		consumer.sign(request);
		HttpResponse response = httpclient.execute(request);
		Log.i(TAG,"Statusline : " + response.getStatusLine());
		InputStream data = response.getEntity().getContent();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data));
		String responeLine;
		StringBuilder responseBuilder = new StringBuilder();
		while ((responeLine = bufferedReader.readLine()) != null) {
			responseBuilder.append(responeLine);
		}
		Log.i(TAG,"Response : " + responseBuilder.toString());
		return responseBuilder.toString();
	}
}