package com.TigerLee.HomeIn.Geocoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.location.Address;
import android.util.Log;

import com.TigerLee.HomeIn.util.Constants;

public class NativeGeocoder {
	
	public static Address mAddress;
	
	//Static String for Google, Naver, Daum Maps API.
	public static String GoogleMapAPI = "http://maps.google.com/maps/api/geocode/xml?address=";
	
	public static String DaumMapKey = "1176f6b78db03f01382ee44afe2f82b98b8b899d";
	public static String DaumMapAPI = "http://apis.daum.net/maps/addr2coord?apikey=" + DaumMapKey;
	
	// EXAMPLE : http://map.naver.com/api/geocode.php?key=8c36c72309be8ab6abd5d527cb472e0f&encoding=utf-8&coord=tm128&query=blahblah
	public static String NaverMapKey = "key=8c36c72309be8ab6abd5d527cb472e0f";
	public static String NaverEcodingType = "encoding=utf-8";
	public static String NaverCoordType = "coord=latlng";
	public static String NaverMapAPI = "http://map.naver.com/api/geocode.php" 
		+ "?" + NaverMapKey 
		+ "&" + NaverEcodingType
		+ "&" + NaverCoordType;
	
	private static final String TAG = "NativeGeocoder";
	
	public static Address getGeocodedAddress(String address) {
		boolean isResultOK = false;
		String mURI;
		mAddress = new Address(Locale.getDefault());
		
		//All space & enter in address fields should be removed to create URI.
		address = address.replace(" ", "+");
		address = address.replace("\n", "+");
		
		HttpClient mHttpClient = new DefaultHttpClient();
		HttpResponse mHttpResponse;
		HttpGet mHttpGet = null;
		HttpEntity mHttpEntity;
		InputStream mInputStream;
		try{/*
			mURI = DaumMapAPI + "&q="+address+ "&output=xml";
			mHttpGet = new HttpGet(mURI);
			mHttpResponse = mHttpClient.execute(mHttpGet);
			mHttpEntity = mHttpResponse.getEntity();
			mInputStream = mHttpEntity.getContent();
			isResultOK = Geocoding(mInputStream, Constants.DAUM_LNG_TAG, Constants.DAUM_LAT_TAG, Constants.DAUM_ADDRESS_TAG);
			if(isResultOK){
				if(Constants.D) Log.v(TAG, "DAUM OK");
				return mAddress;
			}*/
			mURI = NaverMapAPI + "&query="+address;
			mHttpGet = new HttpGet(mURI);
			mHttpResponse = mHttpClient.execute(mHttpGet);
			mHttpEntity = mHttpResponse.getEntity();
			mInputStream = mHttpEntity.getContent();
			isResultOK = Geocoding(mInputStream, Constants.NAVER_LNG_TAG, Constants.NAVER_LAT_TAG, Constants.NAVER_ADDRESS_TAG);
			if(isResultOK){
				if(Constants.D) Log.v(TAG, "NAVER OK");
				if(Constants.D) Log.v(TAG, mAddress.getAddressLine(0));
				if(Constants.D) Log.v(TAG, ""+mAddress.getLatitude());
				if(Constants.D) Log.v(TAG, ""+mAddress.getLongitude());
				return mAddress;
			}
			mURI =GoogleMapAPI + address + "&ka&sensor=false";
			mHttpGet = new HttpGet(mURI);
			mHttpResponse = mHttpClient.execute(mHttpGet);
			mHttpEntity = mHttpResponse.getEntity();
			mInputStream = mHttpEntity.getContent();
			isResultOK = Geocoding(mInputStream, Constants.GOOGLE_LNG_TAG, Constants.GOOGLE_LAT_TAG, Constants.GOOGLE_ADDRESS_TAG);
			if(isResultOK){
				if(Constants.D) Log.v(TAG, "GOOGLE OK");
				if(Constants.D) Log.v(TAG, mAddress.getAddressLine(0));
				if(Constants.D) Log.v(TAG, ""+mAddress.getLatitude());
				if(Constants.D) Log.v(TAG, ""+mAddress.getLongitude());
				return mAddress;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return null;
		/*
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
		*/		
	}
	
	public static boolean Geocoding(InputStream inputStream, String LNG_TAG, String LAT_TAG, String ADDRESS_TAG){
		
		Double mLongitude = null;
		Double mLatitude = null;
		String mFormattedAddress = null;
		
		XmlPullParserFactory mXmlPullParserFactory;
		try {
			mXmlPullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser mXmlPullParser = mXmlPullParserFactory.newPullParser();
			mXmlPullParser.setInput(inputStream, null);
			int mEventType = mXmlPullParser.getEventType();
			int mIndexTotal = 0;
			String tag;
			boolean isTitleLng = false;   
			boolean isTitleLat = false;
			boolean isTitleAddress = false;
			while (mEventType != XmlPullParser.END_DOCUMENT ){
				switch(mEventType){
					case XmlPullParser.TEXT:
						if (isTitleAddress) {
							mFormattedAddress = mXmlPullParser.getText();
							mIndexTotal++;
						}
						if (isTitleLng) {
							mLongitude = Double.valueOf(mXmlPullParser.getText());
							mIndexTotal++;
						}
						if(isTitleLat){
							mLatitude = Double.valueOf(mXmlPullParser.getText());
							mIndexTotal++;
						}						
						break;
					case XmlPullParser.END_TAG:		
						tag = mXmlPullParser.getName();
						if (tag.compareTo(LNG_TAG) == 0 || tag.compareTo(LAT_TAG) == 0 || tag.compareTo(ADDRESS_TAG) == 0) {
							isTitleAddress = false;
							isTitleLng = false;
							isTitleLat = false;
							if(mIndexTotal == 3){
								mAddress.setLongitude(mLongitude);
								mAddress.setLatitude(mLatitude);
								mAddress.setAddressLine(0, mFormattedAddress);
								return true;
							}
						}
						break;
					case XmlPullParser.START_TAG:
						tag = mXmlPullParser.getName();
						if (tag.compareTo(ADDRESS_TAG) == 0){
							isTitleAddress = true;
						}
						if (tag.compareTo(LNG_TAG) == 0){
							isTitleLng = true;
						}
						if(tag.compareTo(LAT_TAG) == 0) {
							isTitleLat = true;
						}
						break;
				}
				mEventType = mXmlPullParser.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false;
	}
	/*
	public static Address getAddressWithGoogleAPI(String mAddressBuilder){
		JSONObject mJsonObject = new JSONObject();
		try {
			mJsonObject = new JSONObject(mAddressBuilder);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Double mLongitude = new Double(0);
		Double mLatitude = new Double(0);
		String mFormattedAddress = null;
				
		try {
			mFormattedAddress = ((JSONArray)mJsonObject.get("results")).getJSONObject(0)
			.getString("formatted_address");
	
			//mDaumLong = ((JSONArray)jsonObject.get("item")).getJSONObject(0).getDouble("lng");
		
			mLongitude = ((JSONArray)mJsonObject.get("results")).getJSONObject(0)
				.getJSONObject("geometry").getJSONObject("location")
				.getDouble("lng");

			//mDaumLat = ((JSONArray)jsonObject.get("channel")).getJSONObject(0)
			//.getJSONObject("item").getDouble("lat");
			
			mLatitude = ((JSONArray)mJsonObject.get("results")).getJSONObject(0)
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
	*/
	
	public static List<Address> getFromLocation(double lat, double lon, int maxResults) {
        String urlStr = "http://maps.google.com/maps/geo?q=" + lat + "," + lon + "&output=json&sensor=false";
                String response = "";
                List<Address> results = new ArrayList<Address>();
                HttpClient client = new DefaultHttpClient();
                
                Log.d("ReverseGeocode", urlStr);
                try {
                        HttpResponse hr = client.execute(new HttpGet(urlStr));
                        HttpEntity entity = hr.getEntity();

                        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));

                        String buff = null;
                        while ((buff = br.readLine()) != null)
                                response += buff;
                } catch (IOException e) {
                        e.printStackTrace();
                }

                JSONArray responseArray = null;
                try {
                        JSONObject jsonObject = new JSONObject(response);
                        responseArray = jsonObject.getJSONArray("Placemark");
                } catch (JSONException e) {
                        return results;
                }

                Log.d("ReverseGeocode", "" + responseArray.length() + " result(s)");
                
                for(int i = 0; i < responseArray.length() && i < maxResults-1; i++) {
                        Address addy = new Address(Locale.getDefault());

                        try {
                                JSONObject jsl = responseArray.getJSONObject(i);

                                String addressLine = jsl.getString("address");

                                if(addressLine.contains(","))
                                        addressLine = addressLine.split(",")[0];

                                addy.setAddressLine(0, addressLine);

                                jsl = jsl.getJSONObject("AddressDetails").getJSONObject("Country");
                                addy.setCountryName(jsl.getString("CountryName"));
                                addy.setCountryCode(jsl.getString("CountryNameCode"));
                                /*
                                jsl = jsl.getJSONObject("AdministrativeArea");
                                addy.setAdminArea(jsl.getString("AdministrativeAreaName"));

                                jsl = jsl.getJSONObject("SubAdministrativeArea");
                                addy.setSubAdminArea(jsl.getString("SubAdministrativeAreaName"));
                                
                                jsl = jsl.getJSONObject("Locality");
                                addy.setLocality(jsl.getString("LocalityName"));

                                addy.setPostalCode(jsl.getJSONObject("PostalCode").getString("PostalCodeNumber"));
                                addy.setThoroughfare(jsl.getJSONObject("Thoroughfare").getString("ThoroughfareName"));
                                */

                        } catch (JSONException e) {
                                e.printStackTrace();
                                continue;
                        }

                        results.add(addy);
                }

                return results;
        }
	
}
