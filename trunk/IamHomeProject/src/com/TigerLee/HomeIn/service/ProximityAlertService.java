package com.TigerLee.HomeIn.service;

import java.util.Locale;

import com.TigerLee.HomeIn.R;
import com.TigerLee.HomeIn.activity.SendTextMessage;
import com.TigerLee.HomeIn.util.Constants;
import com.TigerLee.HomeIn.util.GPSInformation;
import com.TigerLee.HomeIn.util.SharedPreference;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class ProximityAlertService extends Service {

	private static final String TAG = "ProximityAlertService";
	private Address mAddress;

	private Location mLocation;
	private LocationManager mLocationManager;
	
	private LocationListener mNetworkLocationListener;
	private LocationListener mGPSLocationListener;
	
	private Location mCurrentBestLocation;
	
	private Location mGPSCurrentLocation;
	private Location mGPSPreviousLocation;
	
	private Location mNetworkCurrentLocation;
	private Location mNetworkPreviousLocation;
	
	private String mPreviousProvider;

	private static final int TIME_FOR_VIBRATOR = 500;

	private static final int NOTIFICATION_ID = 1000;
	private static final int TIME_FOR_LED = 1500;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		// Get LocationMager & Current Location.
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		String mProvider = GPSInformation.getProviderGPS(this);
		
		mLocation = mLocationManager.getLastKnownLocation(mProvider);
		
		// Do not support GPS.
		if(!mLocationManager.isProviderEnabled(mProvider)){
			//showDisabledGPSPage();
			if(Constants.D) Log.e(TAG, "Not support GPS - kill service!");
			stopSelf();
		}
		
		if(mLocation == null){
			Toast.makeText(this, getString(R.string.UnsupportGPS),
					Toast.LENGTH_SHORT).show();
			stopSelf();
		}	
		
		handleNetworkLocationListener();

		
		
		mAddress = new Address(Locale.getDefault());
		SharedPreference mSharedPreference = new SharedPreference(this);
		mAddress = mSharedPreference.getPreferenceAddress();
		
		if(Constants.D) Log.v(TAG, "Destination Latitude: " + mAddress.getLatitude());
		if(Constants.D) Log.v(TAG, "Destination Longitude: " + mAddress.getLongitude());
		 
		
		//setProximityAlert(mLocationManager);
		//registerIntentFilter();
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
	private void handleNetworkLocationListener(){		
		mNetworkLocationListener = new LocationListener() {			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				if(status == LocationProvider.OUT_OF_SERVICE){
					//setNotification(getString(R.string.VibratorNotificationName),
					//		getString(R.string.VibratorNotificationMsg));
				}
				if(status == LocationProvider.TEMPORARILY_UNAVAILABLE){
					//TODO
				}
			}			
			@Override
			public void onProviderEnabled(String provider) {
			}			
			@Override
			public void onProviderDisabled(String provider) {
				//Notify a user by using a vibrator.
				Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibe.vibrate(TIME_FOR_VIBRATOR);
				setNotification(getString(R.string.VibratorNotificationName),
						getString(R.string.VibratorNotificationMsg));
			}			
			@Override
			public void onLocationChanged(Location location) {
				mNetworkCurrentLocation = location;
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
				//Notify a user by using a vibrator.
				Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				vibe.vibrate(TIME_FOR_VIBRATOR);
				setNotification(getString(R.string.VibratorNotificationName),
						getString(R.string.VibratorNotificationMsg));
			}
			@Override
			public void onLocationChanged(Location location) {
				mGPSCurrentLocation = location;
				Double mDistance = GPSInformation.distance(
						mGPSCurrentLocation.getLatitude(), 
						mGPSCurrentLocation.getLongitude(), 
						mAddress.getLatitude(), 
						mAddress.getLongitude());
				mDistance = Math.abs(mDistance);
				if(Constants.D) Log.v(TAG, "Distance to destination: " + mDistance);
						
				if(mDistance < Constants.DISTANCE){
					destroyService();
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
	/*
	 * Generating a notification message when reaching home. 1. Get a
	 * notification instance & a pending intent 2. Call
	 * createNotification() - set custom notification. 3. set a message
	 * then notify.
	 */
	private Notification createNotification() {

		Notification notification = new Notification();
		notification.icon = R.drawable.icon;
		notification.when = System.currentTimeMillis();

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;

		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;

		notification.ledARGB = Color.WHITE;
		notification.ledOnMS = TIME_FOR_LED;
		notification.ledOffMS = TIME_FOR_LED;

		if(Constants.D) Log.v(TAG, "Create Notification successfully");

		return notification;
	}	
	private void setNotification(String name, String message){
		NotificationManager mNotificationManager = 
			(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, null, 0);

		Notification mNotification = createNotification();

		mNotification.setLatestEventInfo(
				this,
				name,
				//getString(R.string.NotificationName),
				message,
				//getString(R.string.NotificationMsg), 
				pendingIntent);

		mNotificationManager.notify(NOTIFICATION_ID, mNotification);
		if(Constants.D) Log.v(TAG, "Notify - Name: "+ name + " Message" +message);
	}
	
	
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
	public void destroyService(){
		if(Constants.D) Log.v(TAG, "IamHome!");
		// Stop ProximityAlertService
		setNotification(getString(R.string.AlertNotificationName), 
				getString(R.string.AlertNotificationMsg));
		stopSelf();
		// Start an Activity for sending a message.
		Intent sendingMsgIntent = new Intent(this,
				SendTextMessage.class);
		sendingMsgIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(sendingMsgIntent);
	}
}
