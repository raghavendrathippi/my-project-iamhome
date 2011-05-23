package com.TigerLee.HomeIn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class IntroPage extends Activity {

	private boolean V = true;
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
				if(V) Log.v(TAG, "Timeout - finish intro activity");
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
		/*
		if(mCurrentState == DISABLED_GPS){
			Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS ); 
			startActivity(myIntent);
			finish();
			/*
			ImageButton mGPSSettingButton = (ImageButton) findViewById(R.id.GPSSettingButton);
			mGPSSettingButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
						
				}
			});
		}else if(mCurrentState == DISABLED_CONNECTIVITY || mCurrentState == DISABLED_WIFI){
			Intent myIntent = new Intent( Settings.ACTION_WIRELESS_SETTINGS ); 
			startActivity(myIntent);
			finish();
		}*/
		
	}
	
	
	public void setCurrentState(){
		if(!GPSInformation.IsTurnOnGPS(this)){//Not turned on GPS
			Log.e(TAG, getString(R.string.UnsupportGPS));
			mCurrentState = DISABLED_GPS;
			if(!ConnectivityInformation.IsWifiAvailable(this)){//Not support Wifi
				Log.e(TAG, getString(R.string.UnsupportWifi));
				mCurrentState = DISABLED_WIFI;
			}else if(!ConnectivityInformation.IsDataNetworkAvailable(this) && 
					!ConnectivityInformation.IsWifiAvailable(this)){//Not support data network
				Log.e(TAG, getString(R.string.UnsupportConnectivity));
				mCurrentState = DISABLED_CONNECTIVITY;			
			}else{
				mCurrentState = NOT_DISABLED;
			}
		}else{//fine
			mCurrentState = NOT_DISABLED;
		}
	}
	
	
	public void setLayout(){
		switch(mCurrentState){
		case DISABLED_GPS:
			Intent mLocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS ); 
			startActivity(mLocationIntent);
			finish();
			//setContentView(R.layout.disabled_gps);
			break;
		case DISABLED_CONNECTIVITY:
			Intent mWirelessnIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS ); 
			startActivity(mWirelessnIntent);
			finish();
			//setContentView(R.layout.disabled_connectivity);
			break;
		default:
			setContentView(R.layout.intro);
			mTimeHandler.sendMessageDelayed(mTimeHandler.obtainMessage(TIMEOUT), HOLDING_TIME);
		}
	}
	
	public void NextPage(){
		
		// Call main activity
		Intent intent = new Intent();
		intent.setClass(this, MainMenu.class);
		startActivity(intent);

		// show a warning message that it could be charing lots costs.
		if(mCurrentState == DISABLED_WIFI){
			Toast.makeText(this, getString(R.string.UnsupportWifi), Toast.LENGTH_LONG).show();
		}
		
		// Terminate intro activity
		if(V) Log.v(TAG, "Terminate intro activity");
		finish();
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// skip back key when showing intro activity
		if(mCurrentState == DISABLED_WIFI || mCurrentState == NOT_DISABLED){
			if(keyCode == KeyEvent.KEYCODE_BACK)
				return false;			
		}
		return super.onKeyDown(keyCode, event);
	}
}
