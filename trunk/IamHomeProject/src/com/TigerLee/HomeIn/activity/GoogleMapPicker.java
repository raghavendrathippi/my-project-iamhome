package com.tigerlee.homein.activity;

import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tigerlee.homein.R;
import com.tigerlee.homein.util.Constants;
import com.tigerlee.homein.util.GPSInformation;
import com.tigerlee.homein.util.SharedPreference;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.tigerlee.homein.geocoder.NativeGeocoder;

public class GoogleMapPicker extends MapActivity implements OnClickListener,
		android.view.GestureDetector.OnGestureListener {

	public EditText mEditTextDestination;
	public ImageButton mImageButtonSearch;
	
	public MapView mMapView;
	public MapController mMapController;

	private GeoPoint mDestinationGeoPoint = null;
	private GeoPoint mCurrentGeoPoint = null;
	private boolean isMoveFirst = false;

	private GestureDetector mGestureDetector;
	private LocationListener mNetworkLocationListener;
	private LocationListener mGPSLocationListener;
	private LocationManager mLocationManager;
	
	private Address mGeocodeAddress = null;
	private Location currentBestLocation = null;
	
	private static final int DEFAULT_ZOOM = 15;
	private static final int NO_POSITION_ZOOM_KR = 8;
	private static final int NO_POSITION_ZOOM_US = 4;
	
	private static final int PROGRESS_DIALOG = 99;
	private static final int REQUEST_CUSTOM_DIALOG = 99;

	private static final long ONE_MINUTES = 1000;// milliseconds

	protected static final int ERROR_POOR_NETWORK = 11;
	protected static final int ERROR_NOT_FOUND_DESTINATION = 12;
	protected static final int ERROR_NOT_FOUND_CURRENT = 13;
	protected static final int ERROR_NO_TEXT = 14;
	protected static final int NO_ERROR_FIND_GEOPOINT = 15;
	protected static final int NO_ERROR_GET_DETINATION_ADDRESS = 16;
	protected static final int NO_ERROR_GET_CURRENT_ADDRESS = 17;
	
	private static final String TAG = "MapActivity";
	

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		if(Constants.isRunningHomeIn){
			setContentView(R.layout.running_mappicker);
		}else{
			setContentView(R.layout.mappicker);
			//Not to detect gesture actions when the service is running.
			this.mGestureDetector = new GestureDetector(this);
		}
		setAboutMsg(getString(R.string.about_map));
		this.mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//1. Setup MapView
						setupMapView();
					}
				});
			}
		}).start();
		
		//2. Show Dialog or finish activity
		if(Constants.isShowDialog){
			showCustomDialog();
			//3. Set Destination Position
			createDestinationGeopoint();
			//4. Find Current position 
			startLocationListener();
		}else{
			//3. Set Destination Position
			createDestinationGeopoint();
			//4. Find Current position 
			startLocationListener();
			findCurrentPosition();
		}
	}

	// Not to extend Dashboard Activity
	private void setAboutMsg(String string) {
		Constants.ABOUT_ACTIVITY_STRING = string;
	}
	public void setupMapView() {
		//Setup the view to get searching data.
		if(!Constants.isRunningHomeIn){
			this.mEditTextDestination = (EditText) findViewById(R.id.et_destination);
			this.mImageButtonSearch = (ImageButton) findViewById(R.id.bt_search);
			this.mEditTextDestination.setOnClickListener(this);
			this.mImageButtonSearch.setOnClickListener(this);
		}
		// Setup your Mapview & Controller
		this.mMapView = (MapView) findViewById(R.id.mapview);
		this.mMapView.setBuiltInZoomControls(true);
		this.mMapView.setClickable(true);
		this.mMapView.setLongClickable(true);
		
		//Set Zoom.
		this.mMapController = this.mMapView.getController();
		this.mMapController.setZoom(DEFAULT_ZOOM);
	}
	
	public void createDestinationGeopoint() {
		Double mLatitude;
		Double mLongitude;
		//Set Destination if not null
		if(Constants.USER_DESTINATION_LAT!=null && Constants.USER_DESTINATION_LNG!=null){
			mLatitude = Constants.USER_DESTINATION_LAT;
			mLongitude = Constants.USER_DESTINATION_LNG;
			if (Constants.D){
				Log.v(TAG, "Received Destination(Double):" + mLatitude + mLongitude);
			}	
			mLatitude *= 1E6;
			mLongitude *= 1E6;
			this.mDestinationGeoPoint = new GeoPoint(mLatitude.intValue(), mLongitude.intValue());
		}
	}
	public void showCustomDialog() {
		Intent intent = new Intent();
		intent.setClass(this, CustomDialogActivity.class);
		if(!Constants.isRunningHomeIn) {
			intent.putExtra(Intent.EXTRA_TEXT,
					getString(R.string.text_notrunning_custom_activiy));
		}else{
			intent.putExtra(Intent.EXTRA_TEXT,
					getString(R.string.text_running_custom_activiy));
		}
		startActivityForResult(intent, REQUEST_CUSTOM_DIALOG);
	}
	public void findCurrentPosition(){
		if(!this.isMoveFirst){//Not found current location by Listener.
			createCurrentGeopoint();
			toast(getString(R.string.toast_notfound_current_position));
			createCurrentAddressByNetwork();
		}else{
			this.mMapController.setZoom(DEFAULT_ZOOM);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CUSTOM_DIALOG){
			if(resultCode == RESULT_OK){
				findCurrentPosition();
			}else{
				toast(getString(R.string.toast_crash));
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void createCurrentGeopoint() {
		Log.v(TAG, "Locale: " + Locale.getDefault().getCountry());
		
		Double mLatitude;
		Double mLongitude;
		
		if(Locale.getDefault().getCountry().equals("US")){
			mLatitude = Constants.DEFAULT_LAT_US;
			mLongitude = Constants.DEFAULT_LNG_US;
			mLatitude *= 1E6;
			mLongitude *= 1E6;
			this.mCurrentGeoPoint = new GeoPoint(mLatitude.intValue(), mLongitude.intValue());
			this.mMapController.setZoom(NO_POSITION_ZOOM_US);
		}else{
			mLatitude = Constants.DEFAULT_LAT_KR;
			mLongitude = Constants.DEFAULT_LNG_KR;
			mLatitude *= 1E6;
			mLongitude *= 1E6;
			this.mCurrentGeoPoint = new GeoPoint(mLatitude.intValue(), mLongitude.intValue());
			this.mMapController.setZoom(NO_POSITION_ZOOM_KR);
		}
		
		/*	
		//Set CurrentPosition if not null
		if(mCurrentGeoPoint==null){
			mLatitude = Constants.DEFAULT_LAT;
			mLongitude = Constants.DEFAULT_LNG;
			mLatitude *= 1E6;
			mLongitude *= 1E6;
			this.mCurrentGeoPoint = new GeoPoint(mLatitude.intValue(), mLongitude.intValue());
			//this.mMapController.animateTo(mDestinationGeoPoint);
			if(this.mDestinationGeoPoint!=null){
				this.mMapController.animateTo(this.mDestinationGeoPoint);
			}
			this.mMapController.setZoom(NO_POSITION_ZOOM);
		}
		if(Constants.USER_CURRENT_LAT!=null && Constants.USER_CURRENT_LNG!=null){
			//Not to move by location listener.
			this.isMoveFirst = true;	
			mLatitude = Constants.USER_CURRENT_LAT;
			mLongitude = Constants.USER_CURRENT_LNG;
			mLatitude *= 1E6;
			mLongitude *= 1E6;
			this.mCurrentGeoPoint = new GeoPoint(mLatitude.intValue(), mLongitude.intValue());
			this.mMapController.animateTo(this.mCurrentGeoPoint);
		}else{//if null
			mLatitude = Constants.DEFAULT_LAT;
			mLongitude = Constants.DEFAULT_LNG;
			mLatitude *= 1E6;
			mLongitude *= 1E6;
			this.mCurrentGeoPoint = new GeoPoint(mLatitude.intValue(), mLongitude.intValue());
			//this.mMapController.animateTo(mDestinationGeoPoint);
			if(this.mDestinationGeoPoint!=null){
				this.mMapController.animateTo(this.mDestinationGeoPoint);
			}
			this.mMapController.setZoom(NO_POSITION_ZOOM);
		}*/
	}
	
	public void drawMapOverlay(){
		Log.i(TAG, "drawMapOverlay");
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//Get Map overlays
				List<Overlay> mMapOverlays = mMapView.getOverlays();
				//Clear Map ovelays
				mMapOverlays.clear();
				
				if(mCurrentGeoPoint!=null){
					Drawable mCurrentDrawable = getResources().getDrawable(
							R.drawable.ic_maps_indicator_current_position_anim);
					MapItemizedOverlay mCurrentMapOverlay = new MapItemizedOverlay(
							mCurrentDrawable, mMapView);
					
					mCurrentMapOverlay.addOverlay(new OverlayItem(mCurrentGeoPoint,
							getString(R.string.overlay_current_title), 
							Constants.USER_CURRENT_ADDRESS));
					mMapOverlays.add(mCurrentMapOverlay);
				}
				if(mDestinationGeoPoint!=null){
					Drawable mDestinationDrawable = getResources().getDrawable(
							R.drawable.marker);
					MapItemizedOverlay mDestinationMapOverlay = new MapItemizedOverlay(
							mDestinationDrawable, mMapView);
					
					mDestinationMapOverlay.addOverlay(new OverlayItem(mDestinationGeoPoint,
							getString(R.string.overlay_destination_title), 
							Constants.USER_DESTINATION_ADDRESS));
					mMapOverlays.add(mDestinationMapOverlay);
				}
			}
		}).start();
		
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = event.getAction();
		//GestureDetector will be invoked only press down.
		if(!(action > 4)){
			if (Constants.D){Log.v(TAG, "dispatchTouchEvent()- TouchDown");}
			if (!Constants.isRunningHomeIn){this.mGestureDetector.onTouchEvent(event);}
		}
		return super.dispatchTouchEvent(event);
	}
	
	/*
	 * public void createAlertDialog(String title, final String message){
	 * AlertDialog mAlertDialog = new AlertDialog.Builder(this) .setTitle(title)
	 * .setMessage(message) .setPositiveButton(getString(R.string.Yes), new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { //
	 * TODO Auto-generated method stub mGeocodedLoacation.setText(message);
	 * return; } }).setNegativeButton(getString(R.string.No), new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { //
	 * TODO Clear all text in Edittext, TextView return; } }).show(); }
	 */
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onDestroy() {
		if (this.mNetworkLocationListener != null) {
			this.mLocationManager.removeUpdates(mNetworkLocationListener);
		}
		if (this.mGPSLocationListener != null) {
			this.mLocationManager.removeUpdates(mGPSLocationListener);
		}
		super.onDestroy();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		if (Constants.D){Log.v(TAG, "onDown()");}
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// Detect Long press event by Gesture detector
		if (Constants.D){Log.v(TAG, "onLongPress()");}
		if (!Constants.isRunningHomeIn) {
			this.mDestinationGeoPoint = this.mMapView.getProjection().fromPixels(
					(int) e.getX(), (int) e.getY());
			Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibe.vibrate(80);
			createDestinationAddressByNetwork();
		}
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	//
	private void startLocationListener() {
		this.mNetworkLocationListener = new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				if (status == LocationProvider.OUT_OF_SERVICE) {
				}
				if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
				}
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {

			}
			@Override
			public void onLocationChanged(Location location) {
				if (isBetterLocation(location)) {
					currentBestLocation = location;
					
					Constants.USER_CURRENT_LAT = location.getLatitude();
					Constants.USER_CURRENT_LNG = location.getLongitude();
					
					Double lat;
					Double lng;
					lat = location.getLatitude() * 1E6;
					lng = location.getLongitude() * 1E6;
					
					mCurrentGeoPoint = new GeoPoint(lat.intValue(),
							lng.intValue());
					createCurrentAddressByNetwork();
				}
			}
		};
		mGPSLocationListener = new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			@Override
			public void onProviderEnabled(String provider) {
			}

			@Override
			public void onProviderDisabled(String provider) {
			}

			@Override
			public void onLocationChanged(Location location) {
				if (isBetterLocation(location)) {
					currentBestLocation = location;
					Constants.USER_CURRENT_LAT = location.getLatitude();
					Constants.USER_CURRENT_LNG = location.getLongitude();
					
					Double lat;
					Double lng;
					lat = location.getLatitude() * 1E6;
					lng = location.getLongitude() * 1E6;
					mCurrentGeoPoint = new GeoPoint(lat.intValue(),
							lng.intValue());
					createCurrentAddressByNetwork();
				}
			}
		};
		// Register a listener for location manager.
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0,
				mNetworkLocationListener);
		
		mLocationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				0, 0, mGPSLocationListener);
	}

	public boolean isBetterLocation(Location location) {
		if (this.currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}
		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - this.currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > ONE_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -ONE_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - 
				this.currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				this.currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	public String calDistance(Location current) {
		Double mDistance;
		if (current != null) {
			mDistance = GPSInformation.distance(current.getLatitude(),
					current.getLongitude(), Constants.USER_DESTINATION_LAT,
					Constants.USER_DESTINATION_LNG);
		}else{
			mDistance = GPSInformation.distance(Constants.USER_CURRENT_LAT,
					Constants.USER_CURRENT_LNG, Constants.USER_DESTINATION_LAT,
					Constants.USER_DESTINATION_LNG);
		}
		mDistance = Math.abs(mDistance);
		return mDistance.intValue() + "m";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(Constants.isRunningHomeIn){
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.option_menu, menu);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.distance:
			if(this.currentBestLocation!=null){
				toast(getString(R.string.toast_distance)
						+ calDistance(this.currentBestLocation));				
			}else{
				toast(getString(R.string.toast_notfound_current_position));
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_search:
				//1. Create Progress Dialog
				showDialog(PROGRESS_DIALOG);
				new Thread(new Runnable() {
					@Override
					public void run() {
						//2. Search destination geopoint with Google API
						mNetworkHandler.sendMessage(
								mNetworkHandler.obtainMessage(
										createDestinationGeopointByNetwork(
												mEditTextDestination.getText().toString())));
					}
				}).start();
				break;
		}
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROGRESS_DIALOG:
			ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.dialog_loading));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            return dialog;
		default:
			break;
		}
		return super.onCreateDialog(id);
	}
	public int createDestinationGeopointByNetwork(String address){
		if(address == null){
			return ERROR_NO_TEXT;
		}else{
			if(address.length() == 0){
				return ERROR_NO_TEXT;
			}else{
				try{
					//Initializing destination address.
					this.mGeocodeAddress = null;
					this.mGeocodeAddress = NativeGeocoder.getGeocodedAddress(address);
					if(this.mGeocodeAddress == null){
						return ERROR_NOT_FOUND_DESTINATION;
					}else{
						//Create Destination GeoPoint 
						Double mLatitude = this.mGeocodeAddress.getLatitude();
						Double mLongitude = this.mGeocodeAddress.getLongitude();
						mLatitude *= 1E6;
						mLongitude *= 1E6;
						this.mDestinationGeoPoint = new GeoPoint(mLatitude.intValue(), mLongitude.intValue());
						
						return NO_ERROR_FIND_GEOPOINT;
					}
				}catch(Exception e){
					e.printStackTrace();
					return ERROR_POOR_NETWORK;
				}
			}
		}
	}
	public void createDestinationAddressByNetwork(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Constants.USER_DESTINATION_ADDRESS = NativeGeocoder.getAddress(
							mDestinationGeoPoint.getLatitudeE6() / 1E6,
							mDestinationGeoPoint.getLongitudeE6() / 1E6);
					if(Constants.USER_DESTINATION_ADDRESS != null){
						//Set successfully destination with geoPoint.
						Constants.USER_DESTINATION_LAT = mDestinationGeoPoint.getLatitudeE6() / 1E6;
						Constants.USER_DESTINATION_LNG = mDestinationGeoPoint.getLongitudeE6() / 1E6;
						
						mNetworkHandler.sendMessage(
								mNetworkHandler.obtainMessage(NO_ERROR_GET_DETINATION_ADDRESS));
					}else{
						// Not able to find destination with coordinates.
						mNetworkHandler.sendMessage(
								mNetworkHandler.obtainMessage(ERROR_NOT_FOUND_DESTINATION));
					}
				}catch(Exception e){
					// The Exception caused poor network condition.
					e.printStackTrace();
					mNetworkHandler.sendMessage(
							mNetworkHandler.obtainMessage(ERROR_POOR_NETWORK));
				}
			}
		}).start();	
	}


	public void createCurrentAddressByNetwork(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Constants.USER_CURRENT_ADDRESS = NativeGeocoder.getAddress(
							mCurrentGeoPoint.getLatitudeE6() / 1E6,
							mCurrentGeoPoint.getLongitudeE6() / 1E6);
					if(Constants.USER_CURRENT_ADDRESS != null){
						//Set successfully destination with geoPoint.
						Constants.USER_CURRENT_LAT = mCurrentGeoPoint.getLatitudeE6() / 1E6;
						Constants.USER_CURRENT_LNG = mCurrentGeoPoint.getLongitudeE6() / 1E6;
						
						mNetworkHandler.sendMessage(
								mNetworkHandler.obtainMessage(NO_ERROR_GET_CURRENT_ADDRESS));
					}else{
						// Not able to find destination with coordinates.
						mNetworkHandler.sendMessage(
								mNetworkHandler.obtainMessage(ERROR_NOT_FOUND_CURRENT));
					}
				}catch(Exception e){
					// The Exception caused poor network condition.
					e.printStackTrace();
					mNetworkHandler.sendMessage(
							mNetworkHandler.obtainMessage(ERROR_POOR_NETWORK));
				}
			}
		}).start();	
	}
	
	private Handler mNetworkHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case ERROR_POOR_NETWORK:
				removeDialog(PROGRESS_DIALOG);
				toast(getString(R.string.toast_poor_network));
				break;
			case ERROR_NOT_FOUND_DESTINATION:
				//TODO: Reset EditText
				removeDialog(PROGRESS_DIALOG);
				toast(getString(R.string.toast_notfound_destination_position));
				break;
			case ERROR_NOT_FOUND_CURRENT:
				toast(getString(R.string.toast_notfound_current_position));
				break;
			case ERROR_NO_TEXT:
				//TODO: Reset EditText
				removeDialog(PROGRESS_DIALOG);
				toast(getString(R.string.toast_reset_destination));
				break;
			case NO_ERROR_FIND_GEOPOINT:
				//TODO: Set destination address.
				createDestinationAddressByNetwork();
				break;
			case NO_ERROR_GET_DETINATION_ADDRESS:
				//Set address to EditText
				mEditTextDestination.setText(Constants.USER_DESTINATION_ADDRESS);
				//Draw destination mark on map
				drawMapOverlay();
				//Move to destination position.
				mMapController.animateTo(mDestinationGeoPoint);
				removeDialog(PROGRESS_DIALOG);
				toast(getString(R.string.toast_address)
						+ Constants.USER_DESTINATION_ADDRESS + "\n" 
						+ getString(R.string.toast_setDestination));
				Log.i(TAG, "Successfully set Destination address");
				break;
			case NO_ERROR_GET_CURRENT_ADDRESS:
				if(!isMoveFirst){
					isMoveFirst=true;
					mMapController.animateTo(mCurrentGeoPoint);
				}
				//Draw current position on map
				drawMapOverlay();
				//Move to current position.
				
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	public void toast (String msg){
	    Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_LONG).show ();
	} // end toast
/*
	public void searchLocation(){
		try{//Transform Address to Coordinates
			Address mGeocodedAddress = null;
			mGeocodedAddress = NativeGeocoder.getGeocodedAddress(this.mEditTextDestination.getText().toString());
			if(mGeocodedAddress != null){
				//Create Destination GeoPoint 
				Double mLatitude = mGeocodedAddress.getLatitude();
				Double mLongitude = mGeocodedAddress.getLongitude();
				mLatitude *= 1E6;
				mLongitude *= 1E6;
				this.mDestinationGeoPoint = new GeoPoint(mLatitude.intValue(), mLongitude.intValue());
				//Set Destination.
				if(!setDestination(this.mDestinationGeoPoint, mGeocodedAddress.getAddressLine(0))){
					toastThread(getString(R.string.toast_reset_destination));
				}
				this.mMapController.setZoom(DEFAULT_ZOOM);
			}else{
				//Not available to geocode with the address.
				toast(getString(R.string.toast_reset_destination));
			}
		}catch(Exception e){
			toastThread(getString(R.string.toast_poor_network));
			finish();
		}
	}

	public boolean setDestination(GeoPoint geoPoint, String address){
		if(geoPoint == null){
			return false;
		}
		//Set Destination with geoPoint.
		double mLatitude = geoPoint.getLatitudeE6() / 1E6;
		double mLongitude = geoPoint.getLongitudeE6() / 1E6;
		Constants.USER_DESTINATION_LAT = mLatitude;
		Constants.USER_DESTINATION_LNG = mLongitude;
		if (Constants.D){Log.v(TAG, "Location - " + mLatitude + mLongitude);}
		
		if(address == null){//No searched an address so far.
			try {
				address = NativeGeocoder.getAddress(mLatitude, mLongitude);
				if(address != null){			
					Constants.USER_DESTINATION_ADDRESS = address;
				}else{
					// Not able to find destination with coordinates.
					toastThread(getString(R.string.toast_reset_destination));
					return false;
				}
			} catch (Exception e) {
				// The Exception caused poor network condition.
				e.printStackTrace();
				toastThread(getString(R.string.toast_poor_network));
				finish();
				return false;
			}
		}else{// Set destination with searched address.
			Constants.USER_DESTINATION_ADDRESS = address;
		}
		//Set address to EditText
		this.mEditTextDestination.setText(Constants.USER_DESTINATION_ADDRESS);
		//Draw destination mark on map
		drawMapOverlay(this.mDestinationGeoPoint, this.mCurrentGeoPoint);
		//Move to destination point.
		this.mMapController.animateTo(geoPoint);
		toastThread(getString(R.string.toast_address)
				+ Constants.USER_DESTINATION_ADDRESS + "\n" 
				+ getString(R.string.toast_setDestination));
		Log.i(TAG, "Successfully set Destination address");
		return true;
	}
	
	public boolean setAddressObjectFromNetwork(){
		try{
			this.mGeocodeAddress = NativeGeocoder.getGeocodedAddress(this.mEditTextDestination.getText().toString());
		}catch(Exception e){
			toastThread(getString(R.string.toast_poor_network));
			return false;
		}if(mGeocodeAddress==null){
			toast(getString(R.string.toast_reset_destination));
			return false;
		}else{
			return true;
		}
	}
	public boolean setAddressStringFromNetwork(Double mLatitude, Double mLongitude){
		//Create Progress Dialog
		showDialog(PROGRESS_DIALOG);
		try{
			mStringAddress = NativeGeocoder.getAddress(mLatitude, mLongitude);
		}catch(Exception e){
			toastThread(getString(R.string.toast_poor_network));
			return false;
		}if(mGeocodeAddress==null){
			toast(getString(R.string.toast_reset_destination));
			return false;
		}else{
			return true;
		}
	}*/
}
