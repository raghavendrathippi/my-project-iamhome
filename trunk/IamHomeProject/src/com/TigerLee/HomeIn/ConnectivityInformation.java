package com.TigerLee.HomeIn;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*
 * This class is for checking Connectivity state like Wifi and data network.
 * Do not need to initiate this class.
 * Need to add a permission in AndroidMenifest.xml as follow.
 * <uses-permission android:name="android.permission.INTERNET"/>
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
 */

public class ConnectivityInformation {

	private static ConnectivityManager mConnectivityManager;
	
	public static String CONNECTIVITY_SERVICE = Context.CONNECTIVITY_SERVICE;

	public static int WIFI_SERVICE = ConnectivityManager.TYPE_WIFI;
	public static int DATANETWORK_SERVICE = ConnectivityManager.TYPE_MOBILE;

	
	private static final String TAG = "ConnectivityInfo";

	// Checking turned on and connected Wifi
	public static boolean IsWifiAvailable(Context context){
		mConnectivityManager = 
			(ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
		if(mConnectivityManager == null)
			return false;
		try{
			NetworkInfo mNetworkInfo 
			= mConnectivityManager.getNetworkInfo(WIFI_SERVICE);
			return (mNetworkInfo.isAvailable() && mNetworkInfo.isConnected());
						
		}catch(Exception e){
			return false;
		}
	}
	
	// Checking turned on and connected data network as 3G or 4G.
	public static boolean IsDataNetworkAvailable(Context context){
		mConnectivityManager = 
			(ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
		if(mConnectivityManager == null)
			return false;		
		try{
			NetworkInfo mNetworkInfo 
			= mConnectivityManager.getNetworkInfo(DATANETWORK_SERVICE);
			return (mNetworkInfo.isAvailable() && mNetworkInfo.isConnected());
		}catch(Exception e){
			return false;
		}
	}	
}
