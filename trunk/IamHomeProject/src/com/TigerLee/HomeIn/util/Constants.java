package com.tigerlee.homein.util;

import android.util.Log;

public class Constants {
	public static String EXTRA_PHONENUM = null;
	public static String EXTRA_TEXT_MSG = null;
	
	public static Double USER_CURRENT_LAT = null;
	public static Double USER_CURRENT_LNG = null;
	public static String USER_CURRENT_ADDRESS = null;
	
	public static Double USER_DESTINATION_LAT = null;
	public static Double USER_DESTINATION_LNG = null;
	public static String USER_DESTINATION_ADDRESS = null;
	
	public static boolean isRunningHomeIn = false;
	public static boolean isRunningHomeOut = false;
	public static boolean isEnd = false;
	public static boolean isShowDialog = true;
	
	//Default GeoPoint of US => Center of US, LAT. 39��50' LONG. 98��35'
	public static final Double DEFAULT_LAT_US = 39.50;
	public static final Double DEFAULT_LNG_US = -98.35;
	
	//Default GeoPoint of Korea => Seoul  
	public static final Double DEFAULT_LAT_KR = 37.566535;
	public static final Double DEFAULT_LNG_KR = 126.9779692;
	
	public static final String DAUM_LNG_TAG = "lng";
	public static final String DAUM_LAT_TAG = "lat";
	public static final String DAUM_ADDRESS_TAG = "";
	
	public static final String NAVER_LNG_TAG = "x";
	public static final String NAVER_LAT_TAG = "y";
	public static final String NAVER_ADDRESS_TAG = "address";
	
	public static final String GOOGLE_LNG_TAG = "lng";
	public static final String GOOGLE_LAT_TAG = "lat";
	public static final String GOOGLE_ADDRESS_TAG = "formatted_address";
	
	public static String ABOUT_ACTIVITY_STRING = null;
	
	public static double MIN_DISTANCE = 25; // in Meters
	public static double GPS_ERROR_DISTANCE = 20; // in Meters
	public static int MIN_FREQUENCY = 10000; // in milliseconds 
	
	public static final int DESTROY_ACTIVITY = 0;
	public static int FIVE_SECOND = 1000 * 5;

	public static final int MAX_RESULT_GEOCODING = 5;

	public static final String IS_RUNNING = "RUNNING";
	public static final String NOT_RUNNING = "END";
	
	public static boolean D = true;
	
	public static void init(){
		Log.i("Constants", "init()");
		
		EXTRA_PHONENUM = null;
		EXTRA_TEXT_MSG = null;
		
		USER_CURRENT_LAT = null;
		USER_CURRENT_LNG = null;
		USER_CURRENT_ADDRESS = null;
		
		USER_DESTINATION_LAT = null;
		USER_DESTINATION_LNG = null;
		USER_DESTINATION_ADDRESS = null;
	}

	public static void setDefaultVaues() {
		Log.i("Constants", "setDefaultVaues()");
		
	}
}
