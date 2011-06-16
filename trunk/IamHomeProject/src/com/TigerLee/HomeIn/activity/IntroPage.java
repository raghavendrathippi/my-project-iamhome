package com.TigerLee.HomeIn.activity;

import com.TigerLee.HomeIn.R;
import com.TigerLee.HomeIn.util.ConnectivityInformation;
import com.TigerLee.HomeIn.util.Constants;
import com.TigerLee.HomeIn.util.GPSInformation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class IntroPage extends Activity {

	private static String TAG = "INTRO";
	
	public int mCurrentState;
	
	// Holding time for this intro page. (3000ms)
	private static int HOLDING_TIME = 3000;
	
	private static final int DISABLED_GPS = 0;
	private static final int DISABLED_CONNECTIVITY = 1;
	private static final int DISABLED_WIFI = 2;
	private static final int NOT_DISABLED = 3;
	
	// Handler message for timeout.
	private static final int TIMEOUT = 0;
	Handler mTimeHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
			case TIMEOUT:
				if(Constants.D)  Log.v(TAG, "Timeout - finish intro activity");
				NextPage();
				return;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Verify a current state of this device.
		setCurrentState();
		// Set content view according to the current state. 
		setLayout();
	}
	
	public void setCurrentState(){
		if(!GPSInformation.IsTurnOnGPS(this)){//Not turned on GPS
			makeToast(getString(R.string.UnsupportGPS));
			mCurrentState = DISABLED_GPS;
			return;
		}
		if(!ConnectivityInformation.IsWifiAvailable(this)){//Not support Wifi
			if(Constants.D) Log.e(TAG, getString(R.string.UnsupportWifi));
			mCurrentState = DISABLED_WIFI;
		}
		if(!ConnectivityInformation.IsDataNetworkAvailable(this) && 
				!ConnectivityInformation.IsWifiAvailable(this)){//Not support data network
			makeToast(getString(R.string.UnsupportConnectivity));
			mCurrentState = DISABLED_CONNECTIVITY;
			return;
		}
		mCurrentState = NOT_DISABLED;
	}
	
	public void setLayout(){
		switch(mCurrentState){
		case DISABLED_GPS:
			Intent mLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS ); 
			startActivity(mLocationIntent);
			finish();
			break;
		case DISABLED_CONNECTIVITY:
			Intent mWirelessnIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS ); 
			startActivity(mWirelessnIntent);
			finish();
			break;
		default:
			setContentView(R.layout.intro);
			mTimeHandler.sendMessageDelayed(mTimeHandler.obtainMessage(TIMEOUT), HOLDING_TIME);
		}
	}
	public void makeToast(String toastMessage){
		Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
	}
	
	public void NextPage(){
		// Call main activity
		Intent intent = new Intent();
		intent.setClass(this, MainMenu.class);
		startActivity(intent);
		// show a warning message that it could be charing lots costs.
		if(mCurrentState == DISABLED_WIFI){
			makeToast(getString(R.string.UnsupportWifi));
		}		
		
		finish();
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Skip back key when showing intro activity
		if(keyCode == KeyEvent.KEYCODE_BACK)
			return false;
		return super.onKeyDown(keyCode, event);
	}
}
