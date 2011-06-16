package com.TigerLee.HomeIn.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.TigerLee.HomeIn.R;
import com.TigerLee.HomeIn.Geocoder.NativeGeocoder;
import com.TigerLee.HomeIn.util.Constants;
import com.TigerLee.HomeIn.util.GPSInformation;
import com.TigerLee.HomeIn.util.SharedPreference;


public class HomeIn01 extends Activity {
    /** Called when the activity is first created. */
	
	private Location mLocation;
	public Address mGeocodedAddress = null;
	public int mDownX = 0;
	
	
	public Cursor mCursor;
	
	public TextView mGeocodedLoacation;
	
	private Context mContext = this;
	private static final int PROGRESS_DIALOG = 1;
	private static final int MAP_REQUEST = 0;
	
	private static final String TAG = "HomeIn01";
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){	
				case Constants.DESTROY_ACTIVITY:
					//Remove Progress dialog
					removeDialog(PROGRESS_DIALOG);
					break;
				default:
					break;
			}
			
			super.handleMessage(msg);
		}
		
	};
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.homein01);
        
        // Get LocationMager & Current Location.
		LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);		
		String mProvider = GPSInformation.getProviderGPS(this);		
		mLocation = mLocationManager.getLastKnownLocation(mProvider);
		
		// Write your current position to a textView.
        getCurrentLocation(mLocation);
        
        
        //Transform Address to Coordinates
        final EditText mDestinationAddress = (EditText) findViewById(R.id.DestinationAddress);
        mGeocodedLoacation = (TextView) findViewById(R.id.GeocodedLoacation);
        
        final Button mGeocodingButton = (Button)findViewById(R.id.GeocodingButton);
        mGeocodingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Create Progress Dialog
				showDialog(PROGRESS_DIALOG);
				
				mGeocodedAddress = NativeGeocoder.getGeocodedAddress(mDestinationAddress.getText().toString());
				//List<Address> mListAddress = getCoordinatesFromAddress(mDestinationAddress.getText().toString());
				
				
				if(mGeocodedAddress != null){					
					String mTitle = getString(R.string.DialogTitle);
					//mGeocodedAddress = mListAddress.get(0);				
					String mMessage = getString(R.string.Latitude) 
					+ mGeocodedAddress.getLatitude()
					+ "\n"
					+ getString(R.string.Longitude) 
					+ mGeocodedAddress.getLongitude()
					+ "\n"
					+ getString(R.string.FoundAddress) 
					+ mGeocodedAddress.getAddressLine(1);
					String asd  = null;
					for(int i = 0; i < mGeocodedAddress.getMaxAddressLineIndex(); i++){
						asd = asd + mGeocodedAddress.getAddressLine(i);
					}
					if(Constants.D) Log.v(TAG, "Lat: " + mGeocodedAddress.getLatitude() +
							"Lng :" + mGeocodedAddress.getLongitude() + 
							" Addr :"+ asd);
					mHandler.sendMessage(mHandler.obtainMessage(Constants.DESTROY_ACTIVITY));
					GoogleMapPage();
					//createAlertDialog(mTitle, mMessage);
				}else{
					//Not available to geocode an address.
					mHandler.sendMessage(mHandler.obtainMessage(Constants.DESTROY_ACTIVITY));
					Toast.makeText(HomeIn01.this, getString(R.string.NotValidAddress), 
							Toast.LENGTH_LONG).show();
				}
				
			}
		});
        Button mMapButton = (Button) findViewById(R.id.MapButton);
        mMapButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(mGeocodedAddress == null){
					return;
				}else{
					//GoogleMapPage();
				}
			}
		});
        
        Button mNextPageButton = (Button) findViewById(R.id.NextPageto02);
        mNextPageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//Save share data in a preference.
				SharedPreference mSharedPreference = new SharedPreference(mContext);
				if(mGeocodedAddress!=null)
					mSharedPreference.setPreferenceAddress(mGeocodedAddress);
				NextPage();
			}
		});
	}

	/*
	 * THIS IS NOT AVAILABLE CURRENTLY.
	public Cursor getAddressList(String[] columnName, String selectionCondition){
		DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(this);
		mDatabaseAdapter = mDatabaseAdapter.open();
		return mDatabaseAdapter.selectDistinctQuery(columnName, selectionCondition);
	}
	public Cursor getRAWAddressList(String sql, String[] selectionCondition){
		DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter(this);
		mDatabaseAdapter = mDatabaseAdapter.open();
		return mDatabaseAdapter.rawQuery(sql, selectionCondition);
	}
   */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case PROGRESS_DIALOG:
				ProgressDialog dialog = new ProgressDialog(this);
	            dialog.setMessage(getString(R.string.LoadingMsg));
	            dialog.setIndeterminate(true);
	            dialog.setCancelable(false);
	            return dialog;
		}
		return null;
	}
	
	
	private void getCurrentLocation(Location location) {
		String mLocationString = getString(R.string.NoLocation);
		
		TextView mCurrentLocation = (TextView)findViewById(R.id.CurrentLoacation);

		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			mLocationString = getString(R.string.Latitude) 
			+ lat
			+ "\n"
			+ getString(R.string.Longitude) 
			+ lng;/*
			+ "\n"
			+ getAddressFromCoordaniates(location).get(MAX_RESULT).getAddressLine(1);*/
		}
		mCurrentLocation.setText(getString(R.string.CurrentLocation)+ "\n" + mLocationString);
		
		
		/*
		List<Address> addresses = null;
	   	addresses = reverseGeocoding(location);
	   	StringBuilder stringBuilder = new StringBuilder();
	   	
	   	//2. Reverse Geocoding.
		if(addresses.size()>0){
		   	Address address = addresses.get(0);
		   	for(int i = 0 ; i < address.getMaxAddressLineIndex(); i++){
		   		stringBuilder.append(address.getAddressLine(i)).append("\n");
		   		stringBuilder.append(address.getLocality()).append("\n");
		   		stringBuilder.append(address.getPostalCode()).append("\n");
		   		stringBuilder.append(address.getCountryName());
		   	}
		   	mDefaultAddress = stringBuilder.toString();
		}
		mTestText.append("Your Address is:\n" + mDefaultAddress);
		*/
	}
	
    
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
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {// Moving a page with touching.
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			mDownX = (int) event.getX();
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			if(mDownX - (int) event.getX() > 10){
				NextPage();								
			}
		}
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN){
			if(keyCode == KeyEvent.KEYCODE_ENTER){
				//Remove Enter Key because the key cause an error for an address.
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
    public void NextPage(){
		Intent intent = new Intent();
		intent.setClass(this, HomeIn02.class);
		startActivity(intent);
		overridePendingTransition(R.anim.hold, R.anim.fade);
	}	
	
	public void GoogleMapPage(){
		Intent intent = new Intent();
		intent.setClass(this, GoogleMapPicker.class);
		intent.putExtra("LONGITUDE", mGeocodedAddress.getLongitude());
		intent.putExtra("LATITUDE", mGeocodedAddress.getLatitude());
		intent.putExtra("ADDRESS", mGeocodedAddress.getAddressLine(1));
		startActivityForResult(intent, MAP_REQUEST);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	if(resultCode == RESULT_OK){
    		if(requestCode == MAP_REQUEST){
    			Intent intent = getIntent();
    			intent.getDoubleExtra("LONGITUDE", mGeocodedAddress.getLongitude());
    			intent.getDoubleExtra("LATITUDE", mGeocodedAddress.getLatitude());
    		}
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }	
    
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}	
}