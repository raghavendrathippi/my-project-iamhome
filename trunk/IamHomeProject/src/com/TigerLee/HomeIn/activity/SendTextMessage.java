package com.TigerLee.HomeIn.activity;

import com.TigerLee.HomeIn.R;
import com.TigerLee.HomeIn.util.Constants;
import com.TigerLee.HomeIn.util.SharedPreference;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

public class SendTextMessage extends Activity {

	
	public String mPhoneNumber;
	public String mTextMessage;
	public Boolean IsSended = false;
	
	public static String TAG = "SendMessage";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms);
		getSharedPreference();
		if(IsSended == false){
			sendSMS();
		}
	}
	private void sendSMS(){
		PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
		
		SmsManager mSmsManager = SmsManager.getDefault();
		
		// Get a Message from EditText.
		if(mPhoneNumber != null){
			if(Constants.D) Log.v(TAG, "PhoneNum: " +mPhoneNumber +"TextMSG: "+mTextMessage);
			mSmsManager.sendTextMessage(mPhoneNumber, 
					null, 
					mTextMessage, 
					mPendingIntent, 
					null);
		}
		setResult(true);
		
	}
	public void getSharedPreference(){
		//Get SharedData 
        SharedPreference mSharedPreference = new SharedPreference(this);
        
        mPhoneNumber = mSharedPreference.getPreferenceAddress().getPhone();
        mTextMessage = mSharedPreference.getPreferenceMessage();
        //IsSended = mSharedPreference.getPreferenceResult();
        if(Constants.D) Log.v(TAG, "PhoneNum : " + mPhoneNumber 
        		+ " TextMessage : " + mTextMessage 
        		+" Sended" + IsSended);
	}

	private void setResult(boolean result) {
		SharedPreference mSharedPreference = new SharedPreference(this);
		mSharedPreference.setPreferenceResult(result);
	}
	
	

}
