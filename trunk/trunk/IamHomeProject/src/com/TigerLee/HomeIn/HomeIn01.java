package com.TigerLee.HomeIn;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class HomeIn01 extends Activity {
    /** Called when the activity is first created. */
	
	private Location mLocation;
	public Address mGeocodedAddress;
	private Context mContext = this;
	public int mDownX = 0;
	
	public Cursor mCursor;
	
	public TextView mGeocodedLoacation;
	
	//Maximum result of addresses after transferring geocoding
	private static int MAX_RESULT = 1;

	private static final String TAG = "HomeIn01";
	private boolean V = true;
	
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
        new AlertDialog.Builder(this)
        .setTitle("Alerting")
        .setMessage("")
        .setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode == KeyEvent.KEYCODE_SEARCH)
					return false;
				return false;
			}
		})
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            
        }
        })
        .setCancelable(false)
        .show();
        /*
         * Database which has stored an address data in Korea is not available currently.
         * 
        
        String[] mColumnArray = {"sido"};
        
        mCursor = getRAWAddressList("select distinct sido from korea;", null);
        for(int i = 0; i < mCursor.getColumnCount(); i++){
        	Log.v(TAG,mCursor.getColumnName(i));
        }
         */
        
        //Transform Address to Coordinates
        final EditText mDestinationAddress = (EditText) findViewById(R.id.DestinationAddress);
        mGeocodedLoacation = (TextView) findViewById(R.id.GeocodedLoacation);
        
        final Button mGeocodingButton = (Button)findViewById(R.id.GeocodingButton);
        mGeocodingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mGeocodedAddress = getGeocodedAddress(mDestinationAddress.getText().toString());
				//List<Address> mListAddress = getCoordinatesFromAddress(mDestinationAddress.getText().toString());
				
				if(mGeocodedAddress != null){
				//if(!mListAddress.isEmpty()){
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
					Log.v(TAG, "Lat: " + mGeocodedAddress.getLatitude() +
							"Lng :" + mGeocodedAddress.getLongitude() + 
							" Addr :"+ asd);
					
					
					createAlertDialog(mTitle, mMessage);
				}else{
					//Not available to geocode an address.
					Toast.makeText(HomeIn01.this, 
							getString(R.string.NotValidAddress), 
							Toast.LENGTH_LONG).show();
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
	
    public List<Address> getAddressFromCoordaniates(Location location){
    	
    	Geocoder mGeocoder = new Geocoder(this, Locale.getDefault());
    	
    	try{
    		return mGeocoder.getFromLocation(
    				mLocation.getLatitude(), 
    				mLocation.getLongitude(), 
    				MAX_RESULT);
    		
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    	return null;
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	
		
	
	/*
     * If you are not going to use a emulator, you can use these methods.
     * There are bugs to use Geocoder class for the emulator, therefore it is only available in actual device.
     *
     */
	public List<Address> getCoordinatesFromAddress(String address){
		
		Geocoder mGeocoder = new Geocoder(this, Locale.getDefault());
		if(V) Log.v(TAG, "Locale : " + Locale.getDefault());
		try {
			return mGeocoder.getFromLocationName(address, MAX_RESULT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(V) Log.v(TAG, "Exception from getting coordinates from an address: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	
	/*
     * If you are testing wih a emulator, you can use these methods.
     * Please enable NativeGeocoder Class.
     *
     */
     
	public Address getGeocodedAddress(String address){
		return NativeGeocoder.getAddress(NativeGeocoder.getLocationInfo(address, Constants.NAVER_API));
   }
	 
	
	
	
}