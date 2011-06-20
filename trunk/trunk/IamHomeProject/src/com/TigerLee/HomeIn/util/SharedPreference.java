package com.TigerLee.HomeIn.util;

import java.util.Locale;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.util.Log;

public class SharedPreference{

	public class PrefrenceData{
		String address = null;
		Double latitude = null;
		Double longitude = null;
		Double minFrequency = null;
		Double minDistance = null;
		boolean isRunning = false; 
		boolean result = false;
	}
	private static String NAME_PREFERENCE[] = { 
		"ADDRESS", 
		"LATITUDE",
		"LONGITUDE",
		"MINIMUN_FREQUENCY",
		"MINIMUN_DISTANCE",
		"RUNNING",
		"RESULT"
	};
	
	private Context mContext;
	public SharedPreferences mPreferences;
	public PrefrenceData mPrefrenceData;
	
	public static String PREFERENCE_NAME = "preferencedata";
	public static int PREFERENCE_MODE = Activity.MODE_PRIVATE;
	
	public static String TAG = "Shared_Preference";
	
	public SharedPreference(Context context) {
		mContext= context;
		mPrefrenceData = new PrefrenceData();
	}
	public PrefrenceData getPreferenceAddress(){
		// get SharedData to save in preference xml
		SharedPreferences mPreferences = mContext.getSharedPreferences(
				PREFERENCE_NAME, PREFERENCE_MODE);
		String address = mPreferences.getString(NAME_PREFERENCE[0],	null);
		mPrefrenceData.address = address;
		Double latitude = Double.parseDouble(mPreferences.getString(NAME_PREFERENCE[1], null));
		mPrefrenceData.latitude = latitude;
		Double longitude = Double.parseDouble(mPreferences.getString(NAME_PREFERENCE[2], null));
		mPrefrenceData.longitude = longitude;
		Double minFrequency = Double.parseDouble(mPreferences.getString(NAME_PREFERENCE[3], null));
		mPrefrenceData.minFrequency = minFrequency;
		Double minDistance = Double.parseDouble(mPreferences.getString(NAME_PREFERENCE[4], null));
		mPrefrenceData.minDistance = minDistance;
		boolean isRunning = Boolean.getBoolean(mPreferences.getString(NAME_PREFERENCE[5], null));
		mPrefrenceData.isRunning = isRunning;
		boolean result = Boolean.getBoolean(mPreferences.getString(NAME_PREFERENCE[6], null));
		mPrefrenceData.result = result;
		
		//if(Constants.D) Log.v(TAG, "getPreferenceAddress() - " + mAddress.getPhone()+";");
		return mPrefrenceData;
	}
	public void setPreferenceAddress(PrefrenceData prefrenceData){
		
		//get SharedData to save in xml 
		SharedPreferences mPreferences = mContext.getSharedPreferences(
				PREFERENCE_NAME, PREFERENCE_MODE);
		SharedPreferences.Editor mEditor = mPreferences.edit();
        
        mEditor.putString(NAME_PREFERENCE[0], prefrenceData.address);
        mEditor.putString(NAME_PREFERENCE[1], prefrenceData.latitude.toString());
        mEditor.putString(NAME_PREFERENCE[2], prefrenceData.longitude.toString());        
        mEditor.putString(NAME_PREFERENCE[5], prefrenceData.isRunning+"");
        mEditor.putString(NAME_PREFERENCE[6], prefrenceData.result+"");        
        mEditor.commit();
	}
}
