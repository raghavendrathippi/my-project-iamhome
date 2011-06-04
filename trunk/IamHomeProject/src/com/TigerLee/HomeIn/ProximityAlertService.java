package com.TigerLee.HomeIn;

import java.util.Locale;

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

	private static String TREASURE_PROXIMITY_ALERT = "com.TigerLee.HomeIn.ProximityAlertService";


	// To obtain notifications as frequently as possible, set both parameters to 0. 
	private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1000; // in Meters
	
	// In particular, values under 60000ms are not recommended. 
	private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000 * 60 * 7; // in Milliseconds
	 
	
	private static final long POINT_RADIUS = 10; // in Meters
	private static final long PROX_ALERT_EXPIRATION = -1; // -1 == Do not expire

	private static final int TIME_FOR_VIBRATOR = 500;

	private static final int NOTIFICATION_ID = 1000;
	private static final int TIME_FOR_LED = 1500;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		// Get LocationMager & Current Location.
		LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		String mProvider = GPSInformation.getProviderGPS(this);
		
		mLocation = mLocationManager.getLastKnownLocation(mProvider);
		
		// Do not support GPS.
		if(!mLocationManager.isProviderEnabled(mProvider)){
			//showDisabledGPSPage();
			Log.e(TAG, "Not support GPS - kill service!");
			stopSelf();
		}
		
		if(mLocation == null){
			Toast.makeText(this, getString(R.string.UnsupportGPS),
					Toast.LENGTH_SHORT).show();
			stopSelf();
		}	
		
		//Register a listener for location manager. 
		mLocationManager.requestLocationUpdates(
				mProvider, 
				MINIMUM_TIME_BETWEEN_UPDATE, 
				MINIMUM_DISTANCECHANGE_FOR_UPDATE,
				new MyLocationListener()
		);
		
		mAddress = new Address(Locale.getDefault());

		registerIntentFilter();

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
		// TODO Auto-generated method stub

		SharedPreference mSharedPreference = new SharedPreference(this);
		mAddress = mSharedPreference.getPreferenceAddress();
		setProximityAlert(mLocationManager);

		return Service.START_NOT_STICKY;
	}
	
	private void setProximityAlert(LocationManager locationManager) {
		// Test latitude, longitude
		// Coordinates of Pantech Building : 37.519793,126.924609
		// 37.519295,126.924534

		Intent intent = new Intent(TREASURE_PROXIMITY_ALERT);
		PendingIntent proximityIntent = PendingIntent.getBroadcast(
				this, 0, intent, 0);

		locationManager.addProximityAlert(
				mAddress.getLatitude(), 
				mAddress.getLongitude(), 
				POINT_RADIUS, 
				PROX_ALERT_EXPIRATION,
				proximityIntent);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mProximityAlertReceiver);
		Log.v(TAG, "Unregister Receiver");
	}

	private void registerIntentFilter() {
		IntentFilter mIntentfilter = new IntentFilter(TREASURE_PROXIMITY_ALERT);
		registerReceiver(mProximityAlertReceiver, mIntentfilter);
		Log.v(TAG, "Register Receiver");
	}
	
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

		Log.v(TAG, "Create Notification successfully");

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
		Log.v(TAG, "Notify - Name: "+ name + " Message" +message);
	}

	

	// BroadcastReceiver for ProximityAlertReceiver
	private final BroadcastReceiver mProximityAlertReceiver = new BroadcastReceiver() {

		private static final String TAG = "ProximityAlertReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			
			// TODO Auto-generated method stub
			String mKey = LocationManager.KEY_PROXIMITY_ENTERING;
			Boolean mIsEntering = intent.getBooleanExtra(mKey, false);
			if (mIsEntering) {
				Log.v(TAG, "Reaching Home!");
				// Stop ProximityAlertService
				stopSelf();
				setNotification(getString(R.string.AlertNotificationName), 
						getString(R.string.AlertNotificationMsg));
				
				// Start an Activity for sending a message.
				Intent sendingMsgIntent = new Intent(context,
						SendTextMessage.class);
				sendingMsgIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(sendingMsgIntent);
			} else {
				//TODO something for an opposite action.
				Log.v(TAG, "Exiting Home! " + mIsEntering);
			}
			

			/*
			 * Generating a notification message when reaching home. 1. Get a
			 * notification instance & a pending intent 2. Call
			 * createNotification() - set custom notification. 3. set a message
			 * then notify.
			 */
			

		}
	};

	private class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Send a message after 5 times alerts.
			
			//Notify a user by using a vibrator.
			Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibe.vibrate(TIME_FOR_VIBRATOR);
			setNotification(getString(R.string.VibratorNotificationName),
					getString(R.string.VibratorNotificationMsg));

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			if(status == LocationProvider.OUT_OF_SERVICE){
				setNotification(getString(R.string.VibratorNotificationName),
						getString(R.string.VibratorNotificationMsg));
			}
			if(status == LocationProvider.TEMPORARILY_UNAVAILABLE){
				//TODO
			}
		}

	}
}
