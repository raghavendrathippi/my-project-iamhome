package com.TigerLee.HomeIn.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.TigerLee.HomeIn.receiver.IamHomeBroadcastReceiver;
import com.TigerLee.HomeIn.util.Constants;
import com.TigerLee.HomeIn.util.GPSInformation;

public class ProximityAlertService extends Service {
	
	private LocationManager mLocationManager;
	
	private LocationListener mGPSLocationListener;
	
	private Location mGPSCurrentLocation;
	
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
		return Service.START_STICKY;
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
				mGPSCurrentLocation = location;
				if(Constants.isRunningHomeIn){
					Constants.USER_CURRENT_LAT = mGPSCurrentLocation.getLatitude();
					Constants.USER_CURRENT_LNG = mGPSCurrentLocation.getLongitude();
				}
				Double mDistance = GPSInformation.distance(
						mGPSCurrentLocation.getLatitude(), 
						mGPSCurrentLocation.getLongitude(), 
						Constants.USER_DESTINATION_LAT, 
						Constants.USER_DESTINATION_LNG);
				mDistance = Math.abs(mDistance);
				if(Constants.D) Log.v(TAG, "Distance to destination: " + mDistance);
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
		//mLocationManager.removeProximityAlert(PendingIntent.getBroadcast(
		//		this, 0, new Intent(Constants.TREASURE_PROXIMITY_ALERT), 0));
		//unregisterReceiver(mProximityAlertReceiver);
		mLocationManager.removeUpdates(mGPSLocationListener);
		Log.v(TAG, "onDestory");
		//Force closed by the user
		if(Constants.isRunningHomeIn){
			Intent mBroadCastIntent = new Intent();
			mBroadCastIntent.setAction(IamHomeBroadcastReceiver.FORCE_CLOSED_INTENT);
			sendBroadcast(mBroadCastIntent);			
		}
		super.onDestroy();
	}	
	
	public void closeService(){
		if(Constants.D) Log.v(TAG, "IamHome!");
		Constants.isRunningHomeIn = false;
		// Stop ProximityAlertService
		Intent mBroadCastIntent = new Intent();
		mBroadCastIntent.setAction(IamHomeBroadcastReceiver.SUCCESS_INTENT);
		sendBroadcast(mBroadCastIntent);
		stopSelf();	
	}
}
