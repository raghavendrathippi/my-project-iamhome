package com.TigerLee.HomeIn;

import java.io.IOException;


import java.io.InputStream;
import java.net.URLEncoder;
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
	
	//Static String for Google, Naver, Daum Maps API.
	public static String GoogleMapAPI = "http://maps.google.com/maps/api/geocode/json?address=";
	
	public static String DaumMapKey = "1176f6b78db03f01382ee44afe2f82b98b8b899d";
	public static String DaumMapAPI = "http://apis.daum.net/maps/addr2coord?apikey=" + DaumMapKey;
	
	// EXAMPLE : http://map.naver.com/api/geocode.php?key=8c36c72309be8ab6abd5d527cb472e0f&encoding=utf-8&coord=tm128&query=blahblah
	public static String NaverMapKey = "key=8c36c72309be8ab6abd5d527cb472e0f";
	public static String NaverEcodingType = "encoding=utf-8";
	public static String NaverCoordType = "coord=tm128";
	public static String NaverMapAPI = "http://map.naver.com/api/geocode.php" 
		+ "?" + NaverMapKey 
		+ "&" + NaverEcodingType
		+ "&" + NaverCoordType;
	
	public static String getLocationInfo(String address, int whichAPI) {
		
		//All space in address should be removed to create URI.
		address = address.replace(" ", "+");
		
		HttpGet mHttpGet = null;
		
		switch (whichAPI) {
		case Constants.GOOGLE_API:
			mHttpGet = new HttpGet(GoogleMapAPI + address + "ka&sensor=false");
			break;
		case Constants.DAUM_API:
			mHttpGet = new HttpGet(DaumMapAPI + "&q="+address+ "&output=json");
			break;
		case Constants.NAVER_API:
			mHttpGet = new HttpGet(NaverMapAPI + "&query="+address);
			break;
		}		
		
		HttpClient mHttpClient = new DefaultHttpClient();
		HttpResponse mHttpResponse;
		StringBuilder mStringBuilder = new StringBuilder();

		try {
			mHttpResponse = mHttpClient.execute(mHttpGet);
			HttpEntity mHttpEntity = mHttpResponse.getEntity();
			InputStream mInputStream = mHttpEntity.getContent();
			int mReadByte;
			while ((mReadByte = mInputStream.read()) != -1) {
				mStringBuilder.append((char) mReadByte);
			}
		} catch (ClientProtocolException e) {
			
		} catch (IOException e) {
			
		}
		return mStringBuilder.toString();		
	}
	
	public static JSONObject getJsonObject(String mAddressBuilder){
		JSONObject mJsonObject = new JSONObject();
		try {
			mJsonObject = new JSONObject(mAddressBuilder);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return mJsonObject;		
	}
	
	
	//public static Address getGeoPoint(JSONObject jsonObject) {
	public static Address getAddress(String addressBuilder) {
		JSONObject jsonObject = getJsonObject(addressBuilder);
		
		Address mAddress = new Address(Locale.getDefault());
		
		Double mLongitude = new Double(0);
		Double mLatitude = new Double(0);
		
		Double mDaumLong = new Double(0);
		Double mDaumLat = new Double(0);
		
		String mFormattedAddress = null;
				
		try {
			mFormattedAddress = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
			.getString("formatted_address");
	
			//mDaumLong = ((JSONArray)jsonObject.get("item")).getJSONObject(0).getDouble("lng");
		
			mLongitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
				.getJSONObject("geometry").getJSONObject("location")
				.getDouble("lng");

			//mDaumLat = ((JSONArray)jsonObject.get("channel")).getJSONObject(0)
			//.getJSONObject("item").getDouble("lat");
			
			mLatitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
				.getJSONObject("geometry").getJSONObject("location")
				.getDouble("lat");
			

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(ClassCastException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mAddress.setLongitude(mLongitude);
		mAddress.setLatitude(mLatitude);
		mAddress.setAddressLine(1, mFormattedAddress);
		
		
		return mAddress;
	}	

}
