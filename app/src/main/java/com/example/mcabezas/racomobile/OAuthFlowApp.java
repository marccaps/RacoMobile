package com.example.mcabezas.racomobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import shem.com.materiallogin.MaterialLoginView;
import shem.com.materiallogin.MaterialLoginViewListener;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.mcabezas.racomobile.Connect.GestorCertificats;
import com.example.mcabezas.racomobile.Model.PreferenciesUsuari;
import com.example.mcabezas.racomobile.OauthDance.PrepareRequestTokenActivity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


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
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		prefs = getSharedPreferences(
				PreferenciesUsuari.getPreferenciesUsuari(), Context.MODE_PRIVATE);
		String username = prefs.getString(AndroidUtils.USERNAME, "");
		String password = prefs.getString(AndroidUtils.PASSWORD, "");
		if(!username.equals("")) {
			if(check_user(username,password)) {
				Intent i = new Intent(this, MainActivity.class);
				startActivity(i);
			}
		}

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
//				startActivity(new Intent().setClass(getApplicationContext(), PrepareRequestTokenActivity.class));
//				Intent i = new Intent(getApplicationContext(),MainActivity.class);
//				startActivity(i);
				boolean isAllOk = check_user(loginUser.getEditText().getText().toString(),loginPass.getEditText().getText().toString());
				if(isAllOk) {
					SharedPreferences sp = getSharedPreferences(
							PreferenciesUsuari.getPreferenciesUsuari(),
							MODE_PRIVATE);
					SharedPreferences.Editor editor = sp.edit();
					Intent i = new Intent(getApplicationContext(),MainActivity.class);
					startActivity(i);
				}
				else Log.d(TAG,"Error login");


			}
		});

//        performApiCall();
    }

//	private void performApiCall() {
//
//		String jsonOutput = "";
//        try {
//        	jsonOutput = doGet(Constants.API_REQUEST,getConsumer(this.prefs));
//        	System.out.println("jsonOutput : " + jsonOutput);
//        	Log.i(TAG, jsonOutput);
//			Intent i = new Intent(this,MainActivity.class);
//			startActivity(i);
//		} catch (Exception e) {
//			Log.e(TAG, "Error executing request",e);
//		}
//	}
	
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

	private boolean check_user(String username, String password) {

		GestorCertificats.allowAllSSL();
		AndroidUtils au = AndroidUtils.getInstance();
		/** open connection */

		//Així tanquem les connexions segur
		System.setProperty("http.keepAlive", "false");

		try {
			InputStream is = null;
			HttpGet request = new HttpGet(au.URL_LOGIN + "username="
					+ username + "&password=" + password);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(request);
			final int statusCode = response.getStatusLine()
					.getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Header[] headers = response.getHeaders("Location");
				if (headers != null && headers.length != 0) {
					String newUrl = headers[headers.length - 1]
							.getValue();
					request = new HttpGet(newUrl);
					client.execute(request);
				}
			}

			/** Get Keys */
			is = response.getEntity().getContent();
			ObjectMapper m = new ObjectMapper();
			JsonNode rootNode = m.readValue(is, JsonNode.class);

			is.close();
			client.getConnectionManager().closeExpiredConnections();

			if (rootNode.isNull()) {
				return false;
			} else {

				// GenerarUrl();
				/** calendari ics */
				String KEYportadaCal = rootNode
						.path("/ical/portada.ics").textValue();

				/** calendari rss */
				String KEYportadaRss = rootNode
						.path("/ical/portada.rss").textValue();

				/** Avisos */
				String KEYavisos = rootNode
						.path("/extern/rss_avisos.jsp").textValue();

				/** Assigraco */
				String KEYAssigRaco = rootNode.path("/api/assigList")
						.textValue();

				/** Horari */
				String KEYIcalHorari = rootNode
						.path("/ical/horari.ics").textValue();

				/**Notificacions */
				String KEYRegistrar = rootNode
						.path("/api/subscribeNotificationSystem").textValue();

				String KEYDesregistrar = rootNode
						.path("/api/unsubscribeNotificationSystem").textValue();

				SharedPreferences sp = getSharedPreferences(
						PreferenciesUsuari.getPreferenciesUsuari(),
						MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();

				/** Save Username and Password */
				editor.putString(AndroidUtils.USERNAME, username);
				editor.putString(AndroidUtils.PASSWORD, password);

				/** Save Keys */
				editor.putString(au.KEY_AGENDA_RACO_XML, KEYportadaRss);
				editor.putString(au.KEY_AGENDA_RACO_CAL, KEYportadaCal);
				editor.putString(au.KEY_AVISOS, KEYavisos);
				editor.putString(au.KEY_ASSIG_FIB, "public");
				editor.putString(au.KEY_ASSIGS_RACO, KEYAssigRaco);
				editor.putString(au.KEY_HORARI_RACO, KEYIcalHorari);
				editor.putString(au.KEY_NOTIFICACIONS_REGISTRAR, KEYRegistrar);
				editor.putString(au.KEY_NOTIFICACIONS_DESREGISTRAR, KEYDesregistrar);

				/** Save changes */
				editor.commit();
			}
			return true;

		} catch (IOException e) {
			Toast.makeText(getApplicationContext(),
					"Fallo en el login , sorry bruh", Toast.LENGTH_LONG).show();
			return false;
		}

	}


//    private void clearCredentials() {
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//		final Editor edit = prefs.edit();
//		edit.remove(OAuth.OAUTH_TOKEN);
//		edit.remove(OAuth.OAUTH_TOKEN_SECRET);
//		edit.commit();
//	}

	
	private OAuthConsumer getConsumer(SharedPreferences prefs) {
		String token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String secret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
		consumer.setTokenWithSecret(token, secret);
		return consumer;
	}

//	private String doGet(String url,OAuthConsumer consumer) throws Exception {
//
//		DefaultHttpClient httpclient = new DefaultHttpClient();
//		HttpGet request = new HttpGet(url);
//		Log.i(TAG,"Requesting URL : " + url);
//		consumer.sign(request);
//		HttpResponse response = httpclient.execute(request);
//		Log.i(TAG,"Statusline : " + response.getStatusLine());
//		InputStream data = response.getEntity().getContent();
//		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(data));
//		String responeLine;
//		StringBuilder responseBuilder = new StringBuilder();
//		while ((responeLine = bufferedReader.readLine()) != null) {
//			responseBuilder.append(responeLine);
//		}
//		Log.i(TAG,"Response : " + responseBuilder.toString());
//		return responseBuilder.toString();
//	}
}