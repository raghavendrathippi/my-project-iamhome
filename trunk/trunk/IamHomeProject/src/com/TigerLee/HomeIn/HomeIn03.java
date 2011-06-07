package com.TigerLee.HomeIn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class HomeIn03 extends Activity {

	public int mDownX = 0;
	private boolean mStart = false;
	
	private static final String TAG = "HomeIn03";
	private Button mStartService;
	private Button mPreviousPage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homein03);
		
		
		mStartService = (Button) findViewById(R.id.StartService);
		mStartService.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				EditText mTextMessageEditText = (EditText) findViewById(R.id.TextMessageEditText);
				String mTextMessage = mTextMessageEditText.getText().toString();
				
				if(mTextMessageEditText.length()>0){
					// Save a message.
					// TODO enable multiple sending & saving.
					saveTextMessage(mTextMessage);
			        // Start a proximity service.
			        startProximityService();
				}
		        
			}
		}); 
		
		mPreviousPage = (Button) findViewById(R.id.PreviousPageto02);
		mPreviousPage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PreviousPage();
			}
		});
	}
	public void saveTextMessage(String textMessage){
		SharedPreference mSharedPreference = new SharedPreference(this);
		mSharedPreference.setPreferenceMessage(textMessage);
	}
	private void startProximityService(){
		Toast.makeText(this, getString(R.string.ToastStart), Toast.LENGTH_LONG).show();
		
		//Invisible all button
		mStartService.setVisibility(View.INVISIBLE);
		mPreviousPage.setVisibility(View.INVISIBLE);
		
		Intent mServiceIntent = new Intent(this,ProximityAlertService.class);
        startService(mServiceIntent);
        if(Constants.D) Log.v(TAG,"Start Proximity Service");
        mStart = true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			mDownX = (int) event.getX();
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			if((int) event.getX() - mDownX > 10){
				PreviousPage();
			}
			
		}
		return super.onTouchEvent(event);
	}
	public void PreviousPage(){
		onBackPressed();
	}	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(mStart == true){
			if(keyCode == KeyEvent.KEYCODE_BACK)
				return false;			
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	
	


}
