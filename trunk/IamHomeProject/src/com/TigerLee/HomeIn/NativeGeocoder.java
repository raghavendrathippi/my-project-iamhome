package com.TigerLee.HomeIn;

import java.io.IOException;


import java.io.InputStream;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Address;
/*
 * This class is for Geocoding with Google. 
 * Emulator does not work Geocode class. (Actual device works very well)  
 * See Issue 8816:	service not available. - http://code.google.com/p/android/issues/detail?id=8816#makechanges
 * Therefore, we need to create this NativeGeocoder class for geocoding.   
 */

public class NativeGeocoder {
	
	//Static String for Google Maps API.
	public static String GoogleMapAPI = "http://maps.google.com/maps/api/geocode/json?address=";
	
	public static int END = -1;
	
	public static JSONObject getLocationInfo(String address) {

		//All space in address should be removed to create URI.
		address = address.replace(" ", "+");
		
		HttpGet mHttpGet = new HttpGet(GoogleMapAPI + address + "ka&sensor=false");
		HttpClient mHttpClient = new DefaultHttpClient();
		HttpResponse mHttpResponse;
		StringBuilder mStringBuilder = new StringBuilder();

		try {
			mHttpResponse = mHttpClient.execute(mHttpGet);
			HttpEntity mHttpEntity = mHttpResponse.getEntity();
			InputStream mInputStream = mHttpEntity.getContent();
			int mReadByte;
			while ((mReadByte = mInputStream.read()) != END) {
				mStringBuilder.append((char) mReadByte);
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		JSONObject mJsonObject = new JSONObject();
		try {
			mJsonObject = new JSONObject(mStringBuilder.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mJsonObject;
	}
	
	//public static Address getGeoPoint(JSONObject jsonObject) {
	public static Address getAddress(JSONObject jsonObject) {
		Address mAddress = new Address(Locale.getDefault());
		
		Double mLongitude = new Double(0);
		Double mLatitude = new Double(0);
		String mFormattedAddress = null;
				
		try {

			mFormattedAddress = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
			.getString("formatted_address");
			
			mLongitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
				.getJSONObject("geometry").getJSONObject("location")
				.getDouble("lng");

			mLatitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
				.getJSONObject("geometry").getJSONObject("location")
				.getDouble("lat");
			

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mAddress.setLongitude(mLongitude);
		mAddress.setLatitude(mLatitude);
		mAddress.setAddressLine(1, mFormattedAddress);
		
		
		return mAddress;
	}	

}
