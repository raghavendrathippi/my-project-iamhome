package com.tigerlee.homein.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.tigerlee.homein.receiver.IamHomeBroadcastReceiver;
import com.tigerlee.homein.util.Constants;
import com.tigerlee.homein.util.GPSInformation;
import com.tigerlee.homein.util.SharedPreference;

public class ProximityAlertService extends Service {
	
	private LocationManager mLocationManager;
	private LocationListener mGPSLocationListener;
	
	private static final String TAG = "ProximityAlertService";

	@Override
	public void onCreate() {
		super.onCreate();
		if(Constants.D) Log.v(TAG, "Destination Latitude: " + Constants.USER_DESTINATION_LAT);
		if(Constants.D) Log.v(TAG, "Destination Longitude: " + Constants.USER_DESTINATION_LNG);	
		// Get LocationMager & Current Location.
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		startLocationListener();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//if this service's process is killed while it is started
		//it will be called with a null intent object
		if(intent == null){
			startLocationListener();//restart
		}
		return Service.START_NOT_STICKY;
	}
	private void startLocationListener(){
		mGPSLocationListener = new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
			@Override
			public void onProviderEnabled(String provider) {
			}
			@Override
			public void onProviderDisabled(String provider) {
				Intent intent = new Intent();
				intent.setAction(IamHomeBroadcastReceiver.DISABLE_GPS_INTENT);
				sendBroadcast(intent);
			}
			@Override
			public void onLocationChanged(Location location) {
				if(Constants.isRunningHomeIn){
					Constants.USER_CURRENT_LAT = location.getLatitude();
					Constants.USER_CURRENT_LNG = location.getLongitude();
				}
				Double mDistance = GPSInformation.distance(
						location.getLatitude(), 
						location.getLongitude(), 
						Constants.USER_DESTINATION_LAT, 
						Constants.USER_DESTINATION_LNG);
				mDistance = Math.abs(mDistance);
				if(Constants.D) Log.i(TAG, "Distance to destination: " + mDistance);
				if(mDistance < Constants.MIN_DISTANCE + Constants.GPS_ERROR_DISTANCE){
					closeService();
				}
			}
		};
		//Register a listener for location manager. 		
		mLocationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 0 , 0, mGPSLocationListener);
	}
	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestory");
		if(Constants.isRunningHomeIn){//Force closed by the user
			Intent mBroadCastIntent = new Intent();
			mBroadCastIntent.setAction(IamHomeBroadcastReceiver.FORCE_CLOSED_INTENT);
			sendBroadcast(mBroadCastIntent);			
		}
		super.onDestroy();
	}	
	
	public void closeService(){
		if(Constants.D) Log.v(TAG, "IamHome!");
		
		//Application is not running anymore 
		Constants.isRunningHomeIn = false;
		SharedPreference mSharedPreference = new SharedPreference(this);
        mSharedPreference.setIsRunning(false);
		
        // Stop ProximityAlertService
		Intent mBroadCastIntent = new Intent();
		mBroadCastIntent.setAction(IamHomeBroadcastReceiver.SUCCESS_INTENT);
		sendBroadcast(mBroadCastIntent);
		mLocationManager.removeUpdates(mGPSLocationListener);
		stopSelf();	
		
	}
}
