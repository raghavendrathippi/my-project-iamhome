package com.tigerlee.homein.geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.tigerlee.homein.util.Constants;
import com.google.android.maps.GeoPoint;

/*
 * If you are not going to use a emulator, you can use this Class.
 * There are bugs to use Geocoder class for the emulator, therefore it is only available in actual device.
 *
 */
public class GoogleGeocoder {
	private static final String TAG = "GoogleGeocoder";
	
	public static List<Address> getCoordinatesFromAddress(Context context, String address){
		
		Geocoder mGeocoder = new Geocoder(context, Locale.getDefault());
		if(Constants.D) Log.v(TAG, "Locale : " + Locale.getDefault());
		try {
			return mGeocoder.getFromLocationName(address, Constants.MAX_RESULT_GEOCODING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(Constants.D) Log.v(TAG, "Exception to get coordinates from an address: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	}

    public static List<Address> getAddressFromCoordaniates(Context context, GeoPoint mGeoPoint){
    	double mLatitude = mGeoPoint.getLatitudeE6() / 1E6;
		double mLongitude = mGeoPoint.getLongitudeE6() / 1E6;
    	Geocoder mGeocoder = new Geocoder(context, Locale.getDefault());
    	try{
    		if(Constants.D) Log.v(TAG, "Locale : " + Locale.getDefault());
    		if(Constants.D) Log.v(TAG, "Lat : " + mLatitude);
    		if(Constants.D) Log.v(TAG, "Lng : " + mLongitude);
    		return mGeocoder.getFromLocation(
    				mLatitude, 
    				mLongitude, 
    				Constants.MAX_RESULT_GEOCODING);
    		
    	}catch(IOException e){
    		e.printStackTrace();
    		if(Constants.D) Log.v(TAG, "Exception to get an address from Geopoint, lat: " + mLatitude +"lng: " + mLongitude);
    	}
    	return null;
    }

}
