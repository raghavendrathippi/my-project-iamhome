package com.TigerLee.HomeIn;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class GPSInformation {

	public static LocationManager mLocationManager;
	
	private static final Boolean V = true;
	private static final String TAG = "GPSInformation";

	
	// Determine turned off GPS
	public static boolean IsTurnOnGPS(Context context){

		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		String mProvider = LocationManager.GPS_PROVIDER;

		if(!mLocationManager.isProviderEnabled(mProvider)){
			if(V) Log.v(TAG, "GPS Provider disabled");
			return false;
		}
		return true;
	}
	
	
	// Determine how to get a location information.
	public static String getProviderGPS(Context context){
		
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		//1. checking available GPS
		Criteria mCriteria = new Criteria();
		mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		mCriteria.setPowerRequirement(Criteria.POWER_HIGH);
		mCriteria.setAltitudeRequired(true);
	    mCriteria.setSpeedRequired(true);
		mCriteria.setCostAllowed(true);
		
		String mProvider = mLocationManager.getBestProvider(mCriteria, true);
		Location mLocation = mLocationManager.getLastKnownLocation(mProvider);
		
		
		// Cannot catch a signal from GPS.
		if (mLocation == null) {
			
			if(V) Log.v(TAG, "Cannot get a signal from GPS.");
			mCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
			mCriteria.setPowerRequirement(Criteria.POWER_HIGH);
			mCriteria.setAltitudeRequired(false);
		    mCriteria.setSpeedRequired(false);
			
			//2. checking 
			mProvider = mLocationManager.getBestProvider(mCriteria, true);
			mLocation = mLocationManager.getLastKnownLocation(mProvider);
		}
		if(V) Log.v(TAG, "GPS Provider - " + mProvider);
		return mProvider;
	}
	public static boolean IsLocationAvailable(Context context){
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		String mProvider = getProviderGPS(context);
		Location mLocation = mLocationManager.getLastKnownLocation(mProvider);
		if(mLocation != null){
			if(V) Log.v(TAG, "Locaiton is available.");
			return true;
		}
		if(V) Log.v(TAG, "Locaiton is not available.");
		return false;
	}
}
