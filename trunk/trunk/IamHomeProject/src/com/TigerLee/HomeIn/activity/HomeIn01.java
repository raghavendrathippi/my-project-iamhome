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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
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
import com.TigerLee.HomeIn.service.ProximityAlertService;
import com.TigerLee.HomeIn.util.Constants;
import com.TigerLee.HomeIn.util.GPSInformation;


public class HomeIn01 extends DashboardActivity implements OnClickListener{
    /** Called when the activity is first created. */
	private Context mContext = this;
	
	private Location mLocation;
	public Address mGeocodedAddress = null;
	public Boolean mIsStartServce = false;
	public int mDownX = 0;
		
	public Button mButton_Geocoding;
	public Button mButton_StartService;
	public Button mButton_PickAddress;
	
	public TextView mGeocodedLoacation;
	
	private static final int PROGRESS_DIALOG = 1;
	private static final int MAP_REQUEST = 0;
	private static final int ADDRESS_REQUEST = 1;
	
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
        
        mButton_Geocoding = (Button)findViewById(R.id.bt_GeocodingButton);
        mButton_Geocoding.setOnClickListener(this);
        
        mButton_StartService = (Button)findViewById(R.id.bt_StartService);
        mButton_StartService.setOnClickListener(this);
        
        mButton_PickAddress = (Button)findViewById(R.id.bt_pickAddress);
        mButton_PickAddress.setOnClickListener(this);
        
	}

	private void getCurrentLocation(Location location) {
		String mLocationString = getString(R.string.NoLocation);
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			mLocationString = getString(R.string.Latitude) 
			+ lat
			+ "\n"
			+ getString(R.string.Longitude) 
			+ lng;
		}
		TextView mCurrentLocation = (TextView)findViewById(R.id.CurrentLoacation);
		mCurrentLocation.setText(getString(R.string.CurrentLocation)+ "\n" + mLocationString);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getAction() == KeyEvent.ACTION_DOWN){
			if(mIsStartServce){
				if(keyCode == KeyEvent.KEYCODE_BACK){
					return false;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(resultCode == RESULT_OK){
    		if(requestCode == MAP_REQUEST){
    			mGeocodedAddress.setLongitude(data.getDoubleExtra("LONGITUDE", mGeocodedAddress.getLongitude()));
    			mGeocodedAddress.setLatitude(data.getDoubleExtra("LATITUDE", mGeocodedAddress.getLatitude()));
    		}
    		if(requestCode == ADDRESS_REQUEST){
    			String mDefaultContactUri = data.getData().toString();
                // 20110425, @HoryunLee, _BLUETOOTH_, _ATT_, Get an user name from a ContactUri)
                if (mDefaultContactUri != null) {
                	Cursor cursor = null;
                	try {
                		cursor = getContentResolver().query(Uri.parse(mDefaultContactUri),
                			new String[] {
                			ContactsContract.CommonDataKinds.Phone.NUMBER},
                			null, null,null);
                		if (cursor.moveToFirst()) {
                			//Broadcom Code - it does not work because of contact
                			String displayName =data.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);
                			EditText mReceiverPhoneNumber = (EditText) findViewById(R.id.ReceiverPhoneNumber);
                			mReceiverPhoneNumber.setText(cursor.getString(0));                			
                		}
                	} catch (Throwable t) {
                		Log.e(TAG, "Unable to get default contact display name",t);
                	}
                	if (cursor != null) {
                		cursor.close();
                		cursor = null;
                	}
                }
                /*
    			Cursor cursor = getContentResolver().query(data.getData(), 
    					new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, 
    				ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
    			cursor.moveToFirst();
    			if(Constants.D){
    				Log.v(TAG, "NAME : " + cursor.getString(0));
        			Log.v(TAG, "PHONENUM : " + cursor.getString(1));    				
    			}
                cursor.close();
                */
    		}
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }	
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_GeocodingButton:
			//Create Progress Dialog
			showDialog(PROGRESS_DIALOG);
			
			//Transform Address to Coordinates
	        EditText mDestinationAddress = (EditText) findViewById(R.id.DestinationAddress);
	        mGeocodedLoacation = (TextView) findViewById(R.id.GeocodedLoacation);	        
			mGeocodedAddress = NativeGeocoder.getGeocodedAddress(mDestinationAddress.getText().toString());			
			
			if(mGeocodedAddress != null){					
				if(Constants.D){
					String address  = null;
					for(int i = 0; i < mGeocodedAddress.getMaxAddressLineIndex(); i++){
						address = address + mGeocodedAddress.getAddressLine(i);
					}
					 Log.i(TAG, "Lat: " + mGeocodedAddress.getLatitude() +
							"Lng :" + mGeocodedAddress.getLongitude() + 
							" Addr :"+ address);
				}
				mHandler.sendMessage(mHandler.obtainMessage(Constants.DESTROY_ACTIVITY));
				GoogleMapPage();
				//createAlertDialog(mTitle, mMessage);
			}else{
				//Not available to geocode with the address.
				mHandler.sendMessage(mHandler.obtainMessage(Constants.DESTROY_ACTIVITY));
				Toast.makeText(HomeIn01.this, getString(R.string.NotValidAddress), 
						Toast.LENGTH_LONG).show();
			}
			break;

		case R.id.bt_StartService:
			EditText mReceiverPhoneNumber = (EditText) findViewById(R.id.ReceiverPhoneNumber);
			String mPhoneNumber = mReceiverPhoneNumber.getText().toString();
			
			EditText mTextMessageEditText = (EditText) findViewById(R.id.TextMessageEditText);
			String mTextMessage = mTextMessageEditText.getText().toString();
			
			if(mGeocodedAddress!=null && mPhoneNumber!= null && mTextMessage != null){
				startProximityService(mPhoneNumber, mTextMessage);
				mIsStartServce =true;
			}
			break;
		case R.id.bt_pickAddress:
			// 20110425, @HoryunLee, _BLUETOOTH_, _ATT_, Get selecting user name
        	// ACTION_PICK -> ACTION_GET_CONTENT
        	Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        	// Contacts.CONTENT_TYPE -> "vnd.android.cursor.item/phone_v2"
            i.setType("vnd.android.cursor.item/phone_v2");
            startActivityForResult(i, ADDRESS_REQUEST);
		default:
			break;
		}		
	}
	
	//Show Progress Dialog
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
	
	public void GoogleMapPage(){
		Intent intent = new Intent();
		intent.setClass(this, GoogleMapPicker.class);
		intent.putExtra(Constants.EXTRA_LONGITUDE, mGeocodedAddress.getLongitude());
		intent.putExtra(Constants.EXTRA_LATITUDE, mGeocodedAddress.getLatitude());
		intent.putExtra(Constants.EXTRA_ADDRESS, mGeocodedAddress.getAddressLine(1));
		startActivityForResult(intent, MAP_REQUEST);
	}
	
	private void startProximityService(String phonenum, String textmsg){
				
		Intent intent = new Intent(this,ProximityAlertService.class);
		intent.putExtra(Constants.EXTRA_LONGITUDE, mGeocodedAddress.getLongitude());
		intent.putExtra(Constants.EXTRA_LATITUDE, mGeocodedAddress.getLatitude());
		intent.putExtra(Constants.EXTRA_ADDRESS, mGeocodedAddress.getAddressLine(1));
		intent.putExtra(Constants.EXTRA_PHONENUM, phonenum);
		intent.putExtra(Constants.EXTRA_TEXTMSG, textmsg);
        startService(intent);
        
        Toast.makeText(this, getString(R.string.ToastStart), Toast.LENGTH_LONG).show();
		//Invisible start Service button
		mButton_StartService.setVisibility(View.INVISIBLE);
		
        if(Constants.D) Log.v(TAG,"Start Proximity Service");
        mIsStartServce = true;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {// Moving a page with touching.
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			mDownX = (int) event.getX();
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			if(mDownX - (int) event.getX() > 10){
				//NextPage();								
			}
			if((int) event.getX() - mDownX > 10){
				//PreviousPage();
			}
			
		}
		return super.onTouchEvent(event);
	}
	
}