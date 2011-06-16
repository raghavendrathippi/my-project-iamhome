package com.TigerLee.HomeIn.activity;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
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

import com.TigerLee.HomeIn.R;
import com.TigerLee.HomeIn.Geocoder.GoogleGeocoder;
import com.TigerLee.HomeIn.util.Constants;
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
	private static final int DURATION_LONGCLICK = 700;
	private static final int CONFIRM_DIALOG = 1;
	
	private static final String TAG = "MapActivity";
	private boolean mIsPressed = false;
	private boolean mIsChangedAddress = false;
	
	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		Intent mDialogIntent = new Intent();
		mDialogIntent.setClass(this, CustomDialogActivity.class);
		startActivity(mDialogIntent);
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
		String mFormattedAddress = intent.getStringExtra("ADDRESS");
		
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
        
        mMapItemizedOverlay.addOverlay(new OverlayItem(geopoint, null, null));
        mMapOverlays.clear();
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
				if(!mIsPressed){
					mIsPressed = true;
					mStartTouchTime = event.getEventTime();					
				}else{					
					mEndTouchTime = event.getEventTime();
					if(mEndTouchTime - mStartTouchTime > DURATION_LONGCLICK){
						mIsPressed = false;
				        // Propagate your own event
				    	GeoPoint mGeoPoint = mMapView.getProjection().fromPixels((int) event.getX(), (int)event.getY());
				    	dispatchLongClickEvent(mGeoPoint);
					}
				}
				break; 
		    case (MotionEvent.ACTION_UP) : // Touch screen touch ended 
		    	break;
		}
		return super.dispatchTouchEvent(event);
	}
	public void dispatchLongClickEvent(GeoPoint mGeoPoint){
		if(Constants.D) Log.v(TAG, "dispatchLongClickEvent()");
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(500);
		double mLatitude = mGeoPoint.getLatitudeE6() / 1E6;
		double mLongitude = mGeoPoint.getLongitudeE6() / 1E6;
	    if(Constants.D) Log.v(TAG, "Location - " + mLatitude + mLongitude);
		
		Address mClickedAddress = null;		
		try {
			List<Address> mListAddress = GoogleGeocoder.getAddressFromCoordaniates(getBaseContext(), mGeoPoint); 
			mClickedAddress = mListAddress.get(0);		
			mapAnimateTo(mGeoPoint);
			showDialog(CONFIRM_DIALOG);
			Toast.makeText(getBaseContext(), mClickedAddress.getAddressLine(0), Toast.LENGTH_SHORT).show();			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		mLastGeoPoint = mGeoPoint;
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
			case CONFIRM_DIALOG:
				AlertDialog mAlertDialog = new AlertDialog.Builder(this)
				.setMessage(getString(R.string.ConfirmMsg))
				.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mIsChangedAddress = true;
						finish();
						return;
					}
				}).setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).show();				
				break;
			default:
				break;
		}
		// TODO Auto-generated method stub
		return super.onCreateDialog(id);
	}
	/*
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
	@Override
	protected void onDestroy() {
		if(mIsChangedAddress){
			Intent intent = getIntent();
			intent.putExtra("LATITUDE", mLastGeoPoint.getLatitudeE6() / 1E6);
			intent.putExtra("LONGITUDE", mLastGeoPoint.getLongitudeE6() / 1E6);
			
			setResult(RESULT_OK,intent);
		}else{
			setResult(RESULT_CANCELED);
		}
		super.onDestroy();
	}
}
