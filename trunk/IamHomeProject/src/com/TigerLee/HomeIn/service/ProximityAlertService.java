package com.TigerLee.HomeIn.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.TigerLee.HomeIn.receiver.IamHomeBroadcastReceiver;
import com.TigerLee.HomeIn.util.Constants;
import com.TigerLee.HomeIn.util.GPSInformation;

public class ProximityAlertService extends Service {
	
	private LocationManager mLocationManager;
	
	private LocationListener mNetworkLocationListener;
	private LocationListener mGPSLocationListener;
	
	private Location mCurrentBestLocation;
	
	private Location mGPSCurrentLocation;
	private Location mGPSPreviousLocation;
	
	private Location mNetworkCurrentLocation;
	private Location mNetworkPreviousLocation;
	
	private String mPreviousProvider;

	
	private static final String TAG = "ProximityAlertService";

	@Override
	public void onCreate() {
		super.onCreate();

		// Get LocationMager & Current Location.
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		if(Constants.D) Log.v(TAG, "Destination Latitude: " + Constants.USER_DESTINATION_LAT);
		if(Constants.D) Log.v(TAG, "Destination Longitude: " + Constants.USER_DESTINATION_LNG);
		//
		startLocationListener();
	}	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}
	private void startLocationListener(){		
		mNetworkLocationListener = new LocationListener() {			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				if(status == LocationProvider.OUT_OF_SERVICE){
				}
				if(status == LocationProvider.TEMPORARILY_UNAVAILABLE){
				}
			}			
			@Override
			public void onProviderEnabled(String provider) {
			}			
			@Override
			public void onProviderDisabled(String provider) {
				
			}			
			@Override
			public void onLocationChanged(Location location) {
				mNetworkCurrentLocation = location;
				if(Constants.isRunningHomeIn){
					Constants.USER_CURRENT_LAT = mNetworkCurrentLocation.getLatitude();
					Constants.USER_CURRENT_LNG = mNetworkCurrentLocation.getLongitude();
				}
				Double mDistance = GPSInformation.distance(
						mNetworkCurrentLocation.getLatitude(), 
						mNetworkCurrentLocation.getLongitude(), 
						Constants.USER_DESTINATION_LAT, 
						Constants.USER_DESTINATION_LNG);
				mDistance = Math.abs(mDistance);
				if(Constants.D) Log.v(TAG, "Distance to destination: " + mDistance);
				if(mDistance < Constants.DISTANCE){
					closeService();
				}
			}
		};	
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
				if(mDistance < Constants.DISTANCE){
					closeService();
				}
			}
		};
		//Register a listener for location manager. 
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, mNetworkLocationListener);
		
		mLocationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 0, 0, mGPSLocationListener);
	}
	/*
	private void setProximityAlert(LocationManager locationManager) {
		// Test latitude, longitude
		// Coordinates of Pantech Building : 37.519793,126.924609
		// 37.519295,126.924534

		Intent intent = new Intent(Constants.TREASURE_PROXIMITY_ALERT);
		PendingIntent proximityIntent = PendingIntent.getBroadcast(
				this, 0, intent, 0);
		locationManager.addProximityAlert(
				mAddress.getLatitude(), 
				mAddress.getLongitude(), 
				Constants.POINT_RADIUS, 
				Constants.PROXY_ALERT_EXPIRATION,
				proximityIntent);
	}
	
	private void registerIntentFilter() {
		IntentFilter mIntentfilter = new IntentFilter(Constants.TREASURE_PROXIMITY_ALERT);
		registerReceiver(mProximityAlertReceiver, mIntentfilter);
		if(Constants.D) Log.v(TAG, "Register Receiver");
	}
	*/
	@Override
	public void onDestroy() {
		//mLocationManager.removeProximityAlert(PendingIntent.getBroadcast(
		//		this, 0, new Intent(Constants.TREASURE_PROXIMITY_ALERT), 0));
		//unregisterReceiver(mProximityAlertReceiver);
		mLocationManager.removeUpdates(mNetworkLocationListener);
		mLocationManager.removeUpdates(mGPSLocationListener);
		Log.v(TAG, "onDestory");
		//Force closed by the user
		if(Constants.isRunningHomeIn){
			Intent mBroadCastIntent = new Intent();
			mBroadCastIntent.setAction(IamHomeBroadcastReceiver.FORCE_CLOSED_INTENT);
			sendBroadcast(mBroadCastIntent);			
		}
		//if(Constants.D) Log.v(TAG, "Unregister Receiver");
		super.onDestroy();
	}	
	// BroadcastReceiver for ProximityAlertReceiver
	/*
	private final BroadcastReceiver mProximityAlertReceiver = new BroadcastReceiver() {
		private static final String TAG = "ProximityAlertReceiver";
		@Override
		public void onReceive(Context context, Intent intent) {
			
			// TODO Auto-generated method stub
			String mKey = LocationManager.KEY_PROXIMITY_ENTERING;
			Boolean mIsEntering = intent.getBooleanExtra(mKey, false);
			if (mIsEntering) {
				
			} else {
				//TODO something for an opposite action.
				if(Constants.D) Log.v(TAG, "Exiting Home! " + mIsEntering);
			}			
			

		}
	};
	*/
	
	
	
	public synchronized Location findBetterLocation(Location location){
		if (mCurrentBestLocation == null) {
			mCurrentBestLocation = location;
			return location;
		}
		if(mPreviousProvider.equals(LocationManager.GPS_PROVIDER)){
			
		}		
		mGPSPreviousLocation = location;
		mPreviousProvider = LocationManager.GPS_PROVIDER;
		if(mPreviousProvider.equals(LocationManager.GPS_PROVIDER)){
			
		}
		mNetworkPreviousLocation = location;
		mPreviousProvider = LocationManager.NETWORK_PROVIDER;
		return location;
	}
	public void closeService(){
		if(Constants.D) Log.v(TAG, "IamHome!");
		// Stop ProximityAlertService
		Intent mBroadCastIntent = new Intent();
		mBroadCastIntent.setAction(IamHomeBroadcastReceiver.SUCCESS_INTENT);
		sendBroadcast(mBroadCastIntent);
		stopSelf();		
	}
}
