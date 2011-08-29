package com.tigerlee.homein.util;

import java.util.Locale;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.preference.PreferenceManager;
import android.util.Log;

public class SharedPreference{
	
	private static String NAME_PREFERENCE[] = { 
		"ADDRESS", 
		"LATITUDE",
		"LONGITUDE",
		"TEXT_MSG",
		"PHONE_NUM",
		"MINIMUN_FREQUENCY",
		"MINIMUN_DISTANCE",
		"RUNNING",
		"SHOW_DIALOG"
	};
	
	private Context mContext;
	public SharedPreferences mPreferences;
	
	public static String PREFERENCE_NAME = "preferencedata";
	public static int PREFERENCE_MODE = Activity.MODE_PRIVATE;
	
	public static String TAG = "Shared_Preference";
	
	public SharedPreference(Context context) {
		mContext= context;
	}
	public SharedPreferences getCustomSharedPreferences(){
		//get SharedData to save in xml 
		SharedPreferences mPreferences = mContext.getSharedPreferences(
				PREFERENCE_NAME, PREFERENCE_MODE);
		return mPreferences;
	}
	public SharedPreferences getDefaultSharedPreferences(){
		//get SharedData to save in xml with preference activity
		SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		return mPreferences;
	}
	
	public Editor getCustomSharedPreferencesEditor(){
		//get SharedData to save in xml 
		SharedPreferences mPreferences = mContext.getSharedPreferences(
				PREFERENCE_NAME, PREFERENCE_MODE);
		SharedPreferences.Editor mEditor = mPreferences.edit();
		return mEditor;
	}	
	//GETTER
	public String getAddress(){
		SharedPreferences mPreferences =  getCustomSharedPreferences();
		return mPreferences.getString(NAME_PREFERENCE[0], null);
	}
	public Double getLatitude(){
		SharedPreferences mPreferences =  getCustomSharedPreferences();
		String temp = mPreferences.getString(NAME_PREFERENCE[1], null);
		if(temp!=null){
			return Double.parseDouble(temp);
		}else{
			return null;
		}
	}
	public Double getLongitude(){
		SharedPreferences mPreferences =  getCustomSharedPreferences();
		String temp = mPreferences.getString(NAME_PREFERENCE[2], null);
		if(temp!=null){
			return Double.parseDouble(temp);
		}else{
			return null;
		}
	}
	public String getTextMsg(){
		SharedPreferences mPreferences =  getCustomSharedPreferences();
		return mPreferences.getString(NAME_PREFERENCE[3], null);
	}
	public String getPhoneNum(){
		SharedPreferences mPreferences =  getCustomSharedPreferences();
		return mPreferences.getString(NAME_PREFERENCE[4], null);
	}
	public String getMinimumFrequency(){
		SharedPreferences mPreferences =  getDefaultSharedPreferences();
		return mPreferences.getString(NAME_PREFERENCE[5], null);
	}
	public String getMinimumDistance(){
		SharedPreferences mPreferences =  getDefaultSharedPreferences();
		return mPreferences.getString(NAME_PREFERENCE[6], null);
	}
	public boolean getRunning(){
		SharedPreferences mPreferences =  getCustomSharedPreferences();
		return mPreferences.getBoolean(NAME_PREFERENCE[7], false);
	}
	public boolean getShowDialog(){
		SharedPreferences mPreferences =  getDefaultSharedPreferences();
		return mPreferences.getBoolean(NAME_PREFERENCE[8], true);
	}
	
	//SETTER
	public void setAddress(String address){
		SharedPreferences.Editor mEditor =  getCustomSharedPreferencesEditor();
		mEditor.putString(NAME_PREFERENCE[0], address);
        mEditor.commit();
	}
	public void setLatitude(Double latitude){
		SharedPreferences.Editor mEditor =  getCustomSharedPreferencesEditor();
		mEditor.putString(NAME_PREFERENCE[1], latitude.toString());
        mEditor.commit();
	}

	public void setLongitude(Double longitude){
		SharedPreferences.Editor mEditor =  getCustomSharedPreferencesEditor();
		mEditor.putString(NAME_PREFERENCE[2], longitude.toString());
        mEditor.commit();
	}
	public void setTextMsg(String name){
		SharedPreferences.Editor mEditor =  getCustomSharedPreferencesEditor();
		mEditor.putString(NAME_PREFERENCE[3], name);
        mEditor.commit();
	}
	public void setPhoneNum(String result){
		SharedPreferences.Editor mEditor =  getCustomSharedPreferencesEditor();
		mEditor.putString(NAME_PREFERENCE[4], result);
        mEditor.commit();
	}

	public void setIsRunning(boolean isRunning){
		SharedPreferences.Editor mEditor =  getCustomSharedPreferencesEditor();
		mEditor.putBoolean(NAME_PREFERENCE[7], isRunning);
        mEditor.commit();
	}
	public void setShowDialog(boolean isSetDialog){
		SharedPreferences.Editor mEditor =  getCustomSharedPreferencesEditor();
		mEditor.putBoolean(NAME_PREFERENCE[8], isSetDialog);
        mEditor.commit();
	}

}
