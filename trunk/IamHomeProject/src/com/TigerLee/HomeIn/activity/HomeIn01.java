package com.TigerLee.HomeIn.activity;

import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.TigerLee.HomeIn.R;
import com.TigerLee.HomeIn.Geocoder.GoogleGeocoder;
import com.TigerLee.HomeIn.Geocoder.NativeGeocoder;
import com.TigerLee.HomeIn.service.ProximityAlertService;
import com.TigerLee.HomeIn.util.Constants;
import com.TigerLee.HomeIn.util.GPSInformation;
import com.google.android.maps.GeoPoint;


public class HomeIn01 extends DashboardActivity implements OnClickListener{
	
	private Location mLocation;
	public Address mGeocodedAddress = null;
	public Boolean mIsStartServce = false;
	public int mDownX = 0;
		
	public EditText mDestinationAddress;
	public EditText mReceiverPhoneNumber;
	public EditText mTextMessageEditText;
	
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
					//Remove Progress dialog which is created for loading page.
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
        
        setAboutMsg(getString(R.string.about_homein));
        
        // Get LocationMager & Current Location.
		LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);		
		String mProvider = GPSInformation.getProviderGPS(this);		
		mLocation = mLocationManager.getLastKnownLocation(mProvider);
		
		// Write your current position to a textView.
        getCurrentLocation(mLocation);        
		
        mDestinationAddress = (EditText) findViewById(R.id.DestinationAddress);
        if(Constants.USER_DESTINATION_ADDRESS != null){
        	mDestinationAddress.setText(Constants.USER_DESTINATION_ADDRESS); 
        }
        mReceiverPhoneNumber = (EditText) findViewById(R.id.ReceiverPhoneNumber);
    	mTextMessageEditText = (EditText) findViewById(R.id.TextMessageEditText);
    	
    	mButton_Geocoding = (Button)findViewById(R.id.bt_GeocodingButton);
        mButton_StartService = (Button)findViewById(R.id.bt_StartService);
        mButton_PickAddress = (Button)findViewById(R.id.bt_pickAddress);        
		
    	if(Constants.isRunningHomeIn){
    		disableAllButton();
    	}else{
    		enableAllButton();
    	}
	}

	private void getCurrentLocation(Location location) {
		String mLocationString = getString(R.string.NoLocation);
		if (location != null) {
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			if(!Constants.isRunningHomeIn){
				Constants.USER_CURRENT_LAT = lat;
				Constants.USER_CURRENT_LNG = lng;
			}
			mLocationString = getString(R.string.Latitude) 
			+ lat
			+ "\n"
			+ getString(R.string.Longitude) 
			+ lng;
		}
		TextView mCurrentLocation = (TextView)findViewById(R.id.CurrentLoacation);
		mCurrentLocation.setText(getString(R.string.CurrentLocation)+ "\n" + mLocationString);
	}

	private void enableAllButton(){
		mButton_Geocoding.setOnClickListener(this);
		mButton_PickAddress.setOnClickListener(this);
        mButton_StartService.setOnClickListener(this);   
	}
	private void disableAllButton(){
		mButton_Geocoding.setVisibility(View.INVISIBLE);
		mButton_PickAddress.setVisibility(View.INVISIBLE);
		mButton_StartService.setVisibility(View.INVISIBLE);
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
			if(Constants.isRunningHomeIn){
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
    			if(Constants.D) Log.v(TAG, "ResultOK-MapRequest");
    			mDestinationAddress.setText(Constants.USER_DESTINATION_ADDRESS);
    		}
    		if(requestCode == ADDRESS_REQUEST){
    			String mDefaultContactUri = data.getData().toString();
                if (mDefaultContactUri != null) {
                	Cursor cursor = null;
                	try {
                		cursor = getContentResolver().query(Uri.parse(mDefaultContactUri),
                			new String[] {
                			ContactsContract.CommonDataKinds.Phone.NUMBER},
                			null, null,null);
                		if (cursor.moveToFirst()) {
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
    		}
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }	
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	if(Constants.D) Log.v(TAG, "onResume");
    	if(Constants.USER_DESTINATION_ADDRESS != null){
        	mDestinationAddress.setText(Constants.USER_DESTINATION_ADDRESS); 
        }
    	super.onResume();
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
	        mGeocodedLoacation = (TextView) findViewById(R.id.GeocodedLoacation);	        
			mGeocodedAddress = NativeGeocoder.getGeocodedAddress(mDestinationAddress.getText().toString());
			if(mGeocodedAddress != null){
				if(!Constants.isRunningHomeIn){//not allow to change address when is running.
					Constants.USER_DESTINATION_LAT = mGeocodedAddress.getLatitude();
					Constants.USER_DESTINATION_LNG = mGeocodedAddress.getLongitude();
				}
				try{//Get well Formatted Address from lat, lng
					List<Address> mListaddress = GoogleGeocoder.getAddressFromCoordaniates(this, 
							new GeoPoint(Constants.USER_CURRENT_LAT.intValue(), 
									Constants.USER_CURRENT_LNG.intValue()));
					if(mListaddress != null){
						Constants.USER_DESTINATION_ADDRESS = mListaddress.get(0).getAddressLine(0);
						mDestinationAddress.setText(Constants.USER_DESTINATION_ADDRESS);
					}					
				}catch(Exception e){
					
				}
				if(Constants.D){
					Log.i(TAG, "Lat: " + Constants.USER_DESTINATION_LAT +
						"Lng :" + Constants.USER_DESTINATION_LNG + 
						" Addr :"+ Constants.USER_DESTINATION_ADDRESS);
				}
				mHandler.sendMessage(mHandler.obtainMessage(Constants.DESTROY_ACTIVITY));
				GoogleMapPage();
			}else{
				//Not available to geocode with the address.
				mHandler.sendMessage(mHandler.obtainMessage(Constants.DESTROY_ACTIVITY));
				toast(getString(R.string.NotValidAddress));
			}
			break;
		case R.id.bt_StartService:
			String mPhoneNumber = mReceiverPhoneNumber.getText().toString();
			String mTextMessage = mTextMessageEditText.getText().toString();			
			Constants.EXTRA_PHONENUM = mPhoneNumber;
			Constants.EXTRA_TEXT_MSG = mTextMessage;		
			if(isAllsetRequiedInfomation()){
				if(!Constants.isRunningHomeIn){
					startProximityService();
				}
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
		startActivityForResult(intent, MAP_REQUEST);
	}
	
	private void startProximityService(){
		Intent intent = new Intent(this,ProximityAlertService.class);
		startService(intent);
		Constants.isRunningHomeIn = true;
		toast(getString(R.string.toast_startservice));
		//Invisible start Service button
		disableAllButton();
		finish();
        if(Constants.D) Log.v(TAG,"Start Proximity Service");
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
	public boolean isAllsetRequiedInfomation(){
		return (Constants.EXTRA_PHONENUM != null 
				&& Constants.EXTRA_TEXT_MSG != null
				&& Constants.USER_DESTINATION_LAT != null
				&& Constants.USER_DESTINATION_LNG != null);
	}
}