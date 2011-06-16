package com.TigerLee.HomeIn.util;

import java.util.Locale;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.util.Log;

public class SharedPreference{

	private static String NAME_PREFERENCE[] = { 
		"Address", 
		"Latitude",
		"Longitude", 
		"FormattedAddress", 
		"PhoneNumber",
		"TextMessage",
		"Result",
		"DefualtAPI"
	};
	
	private Context mContext;
	public SharedPreferences mPreferences;
	
	public static String PREFERENCE_NAME = "preferencedata";
	public static int PREFERENCE_MODE = Activity.MODE_PRIVATE;
	
	public static String TAG = "Shared_Preference";
	
	public SharedPreference(Context context) {
		mContext= context;
	}
	
	

	public Address getPreferenceAddress(){
		Address mAddress = new Address(Locale.getDefault());
		// get SharedData to save in preference xml
		SharedPreferences mPreferences = mContext.getSharedPreferences(
				PREFERENCE_NAME, PREFERENCE_MODE);
		
		/* Set a address from the preference data
		 * 1. Address 
		 * 2. Latitude
		 * 3. Longitude
		 * 4. FormattedAddress
		 * 5. PhoneNumber
		 */
		
 		mAddress.setAddressLine(1, mPreferences.getString(
				NAME_PREFERENCE[0],	null));
		mAddress.setLatitude(Double.parseDouble(mPreferences.getString(
				NAME_PREFERENCE[1], null)));
		mAddress.setLongitude(Double.parseDouble(mPreferences.getString(
				NAME_PREFERENCE[2], null)));
		mAddress.setAddressLine(1, mPreferences.getString(
				NAME_PREFERENCE[3], null));
		mAddress.setPhone(mPreferences.getString(
				NAME_PREFERENCE[4], null));
		
		if(Constants.D) Log.v(TAG, "getPreferenceAddress() - " + mAddress.getPhone()+";");
		return mAddress;
	}
	public void setPreferenceAddress(Address address){
		
		//get SharedData to save in xml 
		SharedPreferences mPreferences = mContext.getSharedPreferences(
				PREFERENCE_NAME, PREFERENCE_MODE);
		SharedPreferences.Editor mEditor = mPreferences.edit();
        
        mEditor.putString(NAME_PREFERENCE[0], address.getAddressLine(1));
        mEditor.putString(NAME_PREFERENCE[1], address.getLatitude() + "");
        mEditor.putString(NAME_PREFERENCE[2], address.getLongitude() + "");
        mEditor.putString(NAME_PREFERENCE[3], address.getAddressLine(1));
        mEditor.putString(NAME_PREFERENCE[4], address.getPhone());

        if(Constants.D) Log.v(TAG, "setPreferenceAddress() - " + address.getPhone()+";");
        mEditor.commit();
	}

	public String getPreferenceMessage() {
		// get SharedData to save in preference xml
		SharedPreferences mPreferences = mContext.getSharedPreferences(
				PREFERENCE_NAME, PREFERENCE_MODE);
		return mPreferences.getString(NAME_PREFERENCE[5], null);
		
	}

	public void setPreferenceMessage(String message) {
		//get SharedData to save in xml 
		SharedPreferences mPreferences = mContext.getSharedPreferences(
				PREFERENCE_NAME, PREFERENCE_MODE);
		SharedPreferences.Editor mEditor = mPreferences.edit();
		mEditor.putString(NAME_PREFERENCE[5], message);
        mEditor.commit();
	}
	
	public boolean getPreferenceResult() {
		// get SharedData to save in preference xml
		SharedPreferences mPreferences = mContext.getSharedPreferences(
				PREFERENCE_NAME, PREFERENCE_MODE);
		return mPreferences.getBoolean(NAME_PREFERENCE[6], false);
	}

	public void setPreferenceResult(boolean result) {
		//get SharedData to save in xml 
		SharedPreferences mPreferences = mContext.getSharedPreferences(
				PREFERENCE_NAME, PREFERENCE_MODE);
		SharedPreferences.Editor mEditor = mPreferences.edit();
		mEditor.putBoolean(NAME_PREFERENCE[6], result);
        mEditor.commit();
	}
	
	public boolean getPreferenceDefualtAPI() {
		// get SharedData to save in preference xml
		SharedPreferences mPreferences = mContext.getSharedPreferences(
				PREFERENCE_NAME, PREFERENCE_MODE);
		return mPreferences.getBoolean(NAME_PREFERENCE[7], false);
	}
	
}
