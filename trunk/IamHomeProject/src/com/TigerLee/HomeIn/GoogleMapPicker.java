package com.TigerLee.HomeIn;

import java.util.Iterator;
import java.util.Locale;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class GoogleMapPicker extends MapActivity {
	
	MapView mMapView;
	MapController mMapController;
	
	
	private static final String TAG = "MapActivity";
	private boolean V = true;
	
	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		
		Log.v(TAG, "onCreate()");
		Intent intent = getIntent();
		
		Double mLatitude = intent.getDoubleExtra("LATITUDE", 0.0);
		Double mLongitude = intent.getDoubleExtra("LONGITUDE", 0.0);
		
		Log.v(TAG, "Received Point:" + mLatitude + mLongitude);
		
		setContentView(R.layout.mappicker);
		mMapView  = (MapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setClickable(true);
		mMapController = mMapView.getController();
		
		GeoPoint mGeopoint = new GeoPoint(mLatitude.intValue(), mLongitude.intValue());
		mMapController.animateTo(mGeopoint);
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onTouchEvent()");
		if (event.getAction() == MotionEvent.ACTION_UP) {
			GeoPoint mGeoPoint = mMapView.getProjection().fromPixels((int) event.getX(), (int)event.getY());
			mMapView.getController().animateTo(mGeoPoint);
			
			Log.v(TAG, "Location - " + mGeoPoint.getLatitudeE6() + mGeoPoint.getLongitudeE6());
			
			Geocoder mGeocoder = new Geocoder(getBaseContext(), Locale.getDefault());
			String mClickedAddress = null;
			
			try {
				Iterator<Address> mAddressIterator = mGeocoder.getFromLocation(mGeoPoint.getLatitudeE6()/1E6, 
						mGeoPoint.getLongitudeE6()/1E6, 1).iterator();
				if(mAddressIterator != null){
					while(mAddressIterator.hasNext()){
						Address mAddress = mAddressIterator.next();
						
						int mAddressIndex = mAddress.getMaxAddressLineIndex();
						for(int i = 0; i < mAddressIndex; i++){
							String mAddressLine = mAddress.getAddressLine(i);
							mClickedAddress += String.format("\nAddress: %s", mAddressLine);
						}
					}
					double mLatitude = mGeoPoint.getLatitudeE6() / 1E6;
					double mLongitude = mGeoPoint.getLongitudeE6() / 1E6;
					Toast.makeText(getBaseContext(), mClickedAddress
							+ "\n" + mLatitude + ", " + mLongitude, Toast.LENGTH_SHORT).show();
					
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return super.onTouchEvent(event);
		
	}
	

}
