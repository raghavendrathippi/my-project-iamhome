package com.TigerLee.HomeIn.util;

import java.util.Locale;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.util.Log;

public class SharedPreference{
	
	private static String NAME_PREFERENCE[] = { 
		"ADDRESS", 
		"LATITUDE",
		"LONGITUDE",
		"USER_NAME",
		"USER_IMAGE_URI",
		"MINIMUN_FREQUENCY",
		"MINIMUN_DISTANCE",
		"RUNNING",
		"RESULT"
	};
	
	private Context mContext;
	public SharedPreferences mPreferences;
	
	public static String PREFERENCE_NAME = "preferencedata";
	public static int PREFERENCE_MODE = Activity.MODE_PRIVATE;
	
	public static String TAG = "Shared_Preference";
	
	public SharedPreference(Context context) {
		mContext= context;
	}
	public SharedPreferences getSharedPreferences(){
		//get SharedData to save in xml 
		SharedPreferences mPreferences = mContext.getSharedPreferences(
				PREFERENCE_NAME, PREFERENCE_MODE);
		return mPreferences;
	}
	public Editor getSharedPreferencesEditor(){
		//get SharedData to save in xml 
		SharedPreferences mPreferences = mContext.getSharedPreferences(
				PREFERENCE_NAME, PREFERENCE_MODE);
		SharedPreferences.Editor mEditor = mPreferences.edit();
		return mEditor;
	}
	
	//GETTER
	public String getAddress(){
		SharedPreferences mPreferences =  getSharedPreferences();
		return mPreferences.getString(NAME_PREFERENCE[0], null);
	}
	public Double getLatitude(){
		SharedPreferences mPreferences =  getSharedPreferences();
		return Double.parseDouble(mPreferences.getString(NAME_PREFERENCE[1], null));
	}
	public Double getLongitude(){
		SharedPreferences mPreferences =  getSharedPreferences();
		return Double.parseDouble(mPreferences.getString(NAME_PREFERENCE[2], null));
	}
	public String getUserName(){
		SharedPreferences mPreferences =  getSharedPreferences();
		return mPreferences.getString(NAME_PREFERENCE[3], null);
	}
	public String getUserImage(){
		SharedPreferences mPreferences =  getSharedPreferences();
		return mPreferences.getString(NAME_PREFERENCE[4], null);
	}
	public String getRunning(){
		SharedPreferences mPreferences =  getSharedPreferences();
		return mPreferences.getString(NAME_PREFERENCE[7], null);
	}
	public String getResult(){
		SharedPreferences mPreferences =  getSharedPreferences();
		return mPreferences.getString(NAME_PREFERENCE[8], null);
	}
	
	//SETTER
	public void setAddress(String address){
		SharedPreferences.Editor mEditor =  getSharedPreferencesEditor();
		mEditor.putString(NAME_PREFERENCE[0], address);
        mEditor.commit();
	}
	public void setLatitude(Double latitude){
		SharedPreferences.Editor mEditor =  getSharedPreferencesEditor();
		mEditor.putString(NAME_PREFERENCE[1], latitude.toString());
        mEditor.commit();
	}

	public void setLongitude(Double longitude){
		SharedPreferences.Editor mEditor =  getSharedPreferencesEditor();
		mEditor.putString(NAME_PREFERENCE[2], longitude.toString());
        mEditor.commit();
	}
	public void setUserName(String name){
		SharedPreferences.Editor mEditor =  getSharedPreferencesEditor();
		mEditor.putString(NAME_PREFERENCE[0], name);
        mEditor.commit();
	}
	public void setUserImage(String uri){
		SharedPreferences.Editor mEditor =  getSharedPreferencesEditor();
		mEditor.putString(NAME_PREFERENCE[0], uri);
        mEditor.commit();
	}

	public void setIsRunning(String isRunning){
		SharedPreferences.Editor mEditor =  getSharedPreferencesEditor();
		mEditor.putString(NAME_PREFERENCE[7], isRunning+"");
        mEditor.commit();
	}

	public void setResult(String result){
		SharedPreferences.Editor mEditor =  getSharedPreferencesEditor();
		mEditor.putString(NAME_PREFERENCE[8], result+"");
        mEditor.commit();
	}
}
