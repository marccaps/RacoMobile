package com.example.mcabezas.racomobile.Connect;


public class Constants {

	public static final String CONSUMER_KEY 	= "2af15883-d54f-4610-9f08-6a541cf3daff";
	public static final String CONSUMER_SECRET 	= "f2652e24-2dea-4423-8bf7-e65ec7001b08";

	public static final String REQUEST_URL 		= "https://raco.fib.upc.edu/oauth/request_token";
	public static final String ACCESS_URL 		= "https://raco.fib.upc.edu/oauth/access_token";
	public static final String AUTHORIZE_URL 	= "https://raco.fib.upc.edu/oauth/protected/authorize";
	
	public static final String	OAUTH_CALLBACK_SCHEME	= "raco";
	public static final String	OAUTH_CALLBACK_HOST		= "raco";
	public static final String	OAUTH_CALLBACK_URL		= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

}
