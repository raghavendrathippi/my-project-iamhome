package com.TigerLee.HomeIn;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class GoogleMapPicker extends MapActivity {
	
	public MapView mMapView;
	public MapController mMapController;
	
	private GeoPoint mLastGeoPoint;
	
	private long mEndTouchTime;
	private long mStartTouchTime;
	
	private static final int DEFAULT_ZOOM = 16;
	private static final int DURATION_LONGCLICK = 1500;
	
	private static final String TAG = "MapActivity";
	private boolean V = true;
	
	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		
		Log.v(TAG, "onCreate()");
		
		setContentView(R.layout.mappicker);
		
		// Setup your Mapview * Controller
		mMapView  = (MapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setClickable(true);
		mMapView.setLongClickable(true);
		
		mMapController = mMapView.getController();
		mMapController.setZoom(DEFAULT_ZOOM);
		
		
		// Generate a Geopoint from intent
		Intent intent = getIntent();
		
		Double mLatitude = intent.getDoubleExtra("LATITUDE", 0.0) * 1E6;
		Double mLongitude = intent.getDoubleExtra("LONGITUDE", 0.0) * 1E6;
		
		if(Constants.D) Log.v(TAG, "Received Point(Double):" + mLatitude + mLongitude);
		
		GeoPoint mGeopoint = new GeoPoint(mLatitude.intValue(), mLongitude.intValue());
		
		//Animate geopoint / marker with touchEvent
		mapAnimateTo(mGeopoint);
		
		
	}

	public void mapAnimateTo(GeoPoint geopoint){
		mMapController.animateTo(geopoint);
		List<Overlay>  mMapOverlays = mMapView.getOverlays(); 
		Drawable mMarkerDrawable = getResources().getDrawable(R.drawable.marker); 
		MapItemizedOverlay mMapItemizedOverlay = new MapItemizedOverlay(mMarkerDrawable);
        
        OverlayItem overlayitem = new OverlayItem(geopoint, "", "");
        
        mMapItemizedOverlay.addOverlay(overlayitem);
        
        mMapOverlays.add(mMapItemizedOverlay);
        
        mLastGeoPoint = geopoint;		
	}
	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		if(Constants.D) Log.v(TAG, "onTouchEvent()- " + action);
		
		switch (action) {
			case (MotionEvent.ACTION_MOVE) : // Contact has moved across screen
				break; 
			case (MotionEvent.ACTION_CANCEL) : // Touch event canceled
				break;
			case (MotionEvent.ACTION_DOWN) : // Touch screen pressed
				// Record the start time
		         mStartTouchTime = event.getEventTime();
				break; 
		    case (MotionEvent.ACTION_UP) : // Touch screen touch ended
		    	mEndTouchTime = event.getEventTime();
			    if(mEndTouchTime - mStartTouchTime > DURATION_LONGCLICK){
			        // Propagate your own event
			    	GeoPoint mGeoPoint = mMapView.getProjection().fromPixels((int) event.getX(), (int)event.getY());
			    	dispatchLongClickEvent(mGeoPoint);
			        return true; 			    }
		    
			break; 
		    
		}
		return super.dispatchTouchEvent(event);
	}
	public void dispatchLongClickEvent(GeoPoint mGeoPoint){
		
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(500);
		
		//createAlertDialog("Confirm change destination", "Use this address?");
		
		mapAnimateTo(mGeoPoint);
		
	    if(Constants.D) Log.v(TAG, "Location - " + mGeoPoint.getLatitudeE6() + mGeoPoint.getLongitudeE6());
		
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
		mLastGeoPoint = mGeoPoint;
	}/*
	public void createAlertDialog(String title, final String message){
		AlertDialog mAlertDialog = new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mGeocodedLoacation.setText(message);
				return;
			}
		}).setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Clear all text in Edittext, TextView
				return;
			}
		}).show();
	}*/
	@Override
	public void onBackPressed() {
		//TODO: USE LAST GEOPOINT 
		finish();
		super.onBackPressed();
	}
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	

}
