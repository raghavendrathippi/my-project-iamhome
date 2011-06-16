package com.TigerLee.HomeIn.activity;

import com.TigerLee.HomeIn.R;
import com.TigerLee.HomeIn.util.SharedPreference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class HomeIn02 extends Activity {

	public int mDownX = 0;
	
	public Context mContext;
	
	private static final String TAG = "HomeIn02";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homein02);
		
		Button mNextPageButton = (Button) findViewById(R.id.NextPageto03);
		mNextPageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText mReceiverPhoneNumber = (EditText) findViewById(R.id.ReceiverPhoneNumber);
				String mPhoneNumber = mReceiverPhoneNumber.getText().toString();
				if(mPhoneNumber != null){
					savePhoneNumber(mPhoneNumber);
					NextPage();
				}
				
			}
		});
		Button mPreviousButton = (Button) findViewById(R.id.PreviousPageto01);
		mPreviousButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PreviousPage();
			}
		});
	}

	public void savePhoneNumber(String phoneNumber){
		SharedPreference mSharedPreference = new SharedPreference(this);
		Address mAddress = mSharedPreference.getPreferenceAddress();
		mAddress.setPhone(phoneNumber);
		mSharedPreference.setPreferenceAddress(mAddress);
		Log.v(TAG, "savePhoneNumber() - " + mAddress.getPhone()+";");
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			mDownX = (int) event.getX();
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			if(mDownX - (int) event.getX() > 10){
				NextPage();								
			}
			if((int) event.getX() - mDownX > 10){
				PreviousPage();
			}
			
		}
		return super.onTouchEvent(event);
	}
	public void NextPage(){
		Intent intent = new Intent();
		intent.setClass(this, HomeIn03.class);
		startActivity(intent);
		overridePendingTransition(R.anim.hold, R.anim.fade);
	}
	
	public void PreviousPage(){
		onBackPressed();
	}	
	

}
