package com.TigerLee.HomeIn.activity;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.TigerLee.HomeIn.R;
import com.TigerLee.HomeIn.Geocoder.NativeGeocoder;
import com.TigerLee.HomeIn.util.Constants;
import com.TigerLee.HomeIn.util.GPSInformation;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class GoogleMapPicker extends MapActivity implements
		android.view.GestureDetector.OnGestureListener {

	public MapView mMapView;
	public MapController mMapController;

	private GeoPoint mDestinationGeoPoint = null;
	private GeoPoint mCurrentGeoPoint = null;

	private GestureDetector mGestureDetector;
	private String mChangedAddress = null;

	private static final int DEFAULT_ZOOM = 15;
	private static final int CONFIRM_DIALOG = 1;

	private LocationListener mNetworkLocationListener;
	private LocationListener mGPSLocationListener;
	private LocationManager mLocationManager;
	private Location currentBestLocation = null;
	private static final long ONE_MINUTES = 1000;// milliseconds

	private boolean mIsChangedAddress = false;
	private static final String TAG = "MapActivity";

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.mappicker);
		setAboutMsg(getString(R.string.about_map));
	}

	// Not to extend Dashboard Activity
	private void setAboutMsg(String string) {
		Constants.ABOUT_ACTIVITY_STRING = string;
	}

	@Override
	protected void onStart() {
		setupMapView();
		createGeopoint();

		if (Constants.isRunningHomeIn && mCurrentGeoPoint == null) {
			Toast.makeText(this, getString(R.string.NoLocation),
					Toast.LENGTH_LONG).show();
			finish();
		} else {
			showCustomDialog();
		}
		mapAnimateandOverlay(mDestinationGeoPoint, mCurrentGeoPoint);
		mGestureDetector = new GestureDetector(this);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		startLocationListener();
		super.onStart();
	}

	public void setupMapView() {
		// Setup your Mapview * Controller
		mMapView = (MapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setClickable(true);
		mMapView.setLongClickable(true);
	}

	public void createGeopoint() {
		Double mLatitude;
		Double mLongitude;
		mLatitude = Constants.USER_DESTINATION_LAT;
		mLongitude = Constants.USER_DESTINATION_LNG;
		if (mLatitude == null && mLongitude == null) {
			mLatitude = Constants.DEFAULT_LAT;
			mLongitude = Constants.DEFAULT_LNG;
		}
		if (Constants.D)
			Log.v(TAG, "Received DestnationGeoPoint(Double):" + mLatitude
					+ mLongitude);
		mLatitude *= 1E6;
		mLongitude *= 1E6;
		// Animate geopoint / marker with touchEvent
		mDestinationGeoPoint = new GeoPoint(mLatitude.intValue(),
				mLongitude.intValue());

		if (Constants.isRunningHomeIn) {
			mLatitude = Constants.USER_CURRENT_LAT;
			mLongitude = Constants.USER_CURRENT_LNG;
			if (Constants.D)
				Log.v(TAG, "Received CurrentGeoPoint(Double):" + mLatitude
						+ mLongitude);
			if (mLatitude != null && mLongitude != null) {
				mLatitude *= 1E6;
				mLongitude *= 1E6;
				mCurrentGeoPoint = new GeoPoint(mLatitude.intValue(),
						mLongitude.intValue());
			}
		}
	}

	public void showCustomDialog() {
		Intent intent = new Intent();
		intent.setClass(this, CustomDialogActivity.class);
		if (!Constants.isRunningHomeIn) {
			intent.putExtra(Intent.EXTRA_TEXT,
					getString(R.string.text_notrunning_custom_activiy));
		} else {
			intent.putExtra(Intent.EXTRA_TEXT,
					getString(R.string.text_running_custom_activiy));
		}
		startActivity(intent);
	}

	public void mapAnimateandOverlay(GeoPoint destinationGeopoint,
			GeoPoint currentGeopoint) {
		double mLatitude;
		double mLongitude;
		String address = null;
		
		//Set Zoom.
		mMapController = mMapView.getController();
		mMapController.setZoom(DEFAULT_ZOOM);

		//Move to Current & Destination postion.
		if (Constants.isRunningHomeIn && currentGeopoint != null) {
			mMapController.animateTo(currentGeopoint);
		} else {
			mMapController.animateTo(destinationGeopoint);
		}
		
		//Get Map overlays
		List<Overlay> mMapOverlays = mMapView.getOverlays();
		//Clear Map ovelays
		mMapOverlays.clear();

		//set Destination position on map
		Drawable mDestinationDrawable = getResources().getDrawable(
				R.drawable.marker);
		MapItemizedOverlay mDestinationMapOverlay = new MapItemizedOverlay(
				mDestinationDrawable, mMapView);
		mLatitude = destinationGeopoint.getLatitudeE6() / 1E6;
		mLongitude = destinationGeopoint.getLongitudeE6() / 1E6;
		address = NativeGeocoder.getAddress(mLatitude, mLongitude);
		mDestinationMapOverlay.addOverlay(new OverlayItem(destinationGeopoint,
				getString(R.string.overlay_destination_title), address));
		mMapOverlays.add(mDestinationMapOverlay);

		//set current position on map
		if (currentGeopoint != null) {
			Drawable mCurrentDrawable = getResources().getDrawable(
					R.drawable.marker_rounded_green);
			MapItemizedOverlay mCurrentnMapOverlay = new MapItemizedOverlay(
					mCurrentDrawable, mMapView);

			mLatitude = currentGeopoint.getLatitudeE6() / 1E6;
			mLongitude = currentGeopoint.getLongitudeE6() / 1E6;
			address = NativeGeocoder.getAddress(mLatitude, mLongitude);

			mCurrentnMapOverlay.addOverlay(new OverlayItem(currentGeopoint,
					getString(R.string.overlay_current_title), address));
			mMapOverlays.add(mCurrentnMapOverlay);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (Constants.D){Log.v(TAG, "dispatchTouchEvent()- " + action);}
		//set GestureDetector as a touch event.
		mGestureDetector.onTouchEvent(event);
		return super.dispatchTouchEvent(event);
	}

	public void dispatchLongClickEvent(GeoPoint mGeoPoint) {
		if (Constants.D){Log.v(TAG, "dispatchLongClickEvent()");}
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(80);
		double mLatitude = mGeoPoint.getLatitudeE6() / 1E6;
		double mLongitude = mGeoPoint.getLongitudeE6() / 1E6;
		if (Constants.D)
			Log.v(TAG, "Location - " + mLatitude + mLongitude);
		try {
			mChangedAddress = NativeGeocoder.getAddress(mLatitude, mLongitude);
			mapAnimateandOverlay(mDestinationGeoPoint, mCurrentGeoPoint);
			showDialog(CONFIRM_DIALOG);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case CONFIRM_DIALOG:
			AlertDialog mAlertDialog = new AlertDialog.Builder(this)
					.setMessage(mChangedAddress + "\n" + getString(R.string.ConfirmMsg))
					.setPositiveButton(getString(R.string.Yes),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mIsChangedAddress = true;
									if (!Constants.isRunningHomeIn) {
										Log.v(TAG, "mIsChangedAddress");
										Constants.USER_DESTINATION_ADDRESS = mChangedAddress;
										Constants.USER_DESTINATION_LAT = mDestinationGeoPoint
												.getLatitudeE6() / 1E6;
										Constants.USER_DESTINATION_LNG = mDestinationGeoPoint
												.getLongitudeE6() / 1E6;
										setResult(RESULT_OK);
									}
									finish();
									return;
								}
							})
					.setNegativeButton(getString(R.string.No),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Toast.makeText(
											getApplicationContext(),
											getString(R.string.toast_reset_destination),
											Toast.LENGTH_SHORT).show();
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
		if (mNetworkLocationListener != null) {
			mLocationManager.removeUpdates(mNetworkLocationListener);
		}
		if (mGPSLocationListener != null) {
			mLocationManager.removeUpdates(mGPSLocationListener);
		}

		if (!mIsChangedAddress) {
			setResult(RESULT_CANCELED);
		} else {

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
			mDestinationGeoPoint = mMapView.getProjection().fromPixels(
					(int) e.getX(), (int) e.getY());
			dispatchLongClickEvent(mDestinationGeoPoint);
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
		mNetworkLocationListener = new LocationListener() {
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
					Double lat;
					Double lng;
					lat = location.getLatitude() * 1E6;
					lng = location.getLongitude() * 1E6;
					GeoPoint geopoint = new GeoPoint(lat.intValue(),
							lng.intValue());
					mapAnimateandOverlay(mDestinationGeoPoint, geopoint);
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
					Double lat;
					Double lng;
					lat = location.getLatitude() * 1E6;
					lng = location.getLongitude() * 1E6;
					GeoPoint geopoint = new GeoPoint(lat.intValue(),
							lng.intValue());
					mapAnimateandOverlay(mDestinationGeoPoint, geopoint);
				}
			}
		};
		// Register a listener for location manager.
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0,
				mNetworkLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				0, 0, mGPSLocationListener);
	}

	public boolean isBetterLocation(Location location) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}
		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
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
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

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
		} else {
			mDistance = GPSInformation.distance(Constants.USER_CURRENT_LAT,
					Constants.USER_CURRENT_LNG, Constants.USER_DESTINATION_LAT,
					Constants.USER_DESTINATION_LNG);
		}
		mDistance = Math.abs(mDistance);
		return mDistance.intValue() + "m";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.distance:
			Toast.makeText(
					this,
					getString(R.string.toast_distance)
							+ calDistance(currentBestLocation),
					Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

}
