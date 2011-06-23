package com.TigerLee.HomeIn.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

import com.TigerLee.HomeIn.R;
import com.TigerLee.HomeIn.util.Constants;

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
		
		mPhoneNumber = Constants.EXTRA_PHONENUM;
		mTextMessage = Constants.EXTRA_TEXTMSG;
		if(Constants.D) Log.v(TAG, "PhoneNum : " + mPhoneNumber 
        		+ " TextMessage : " + mTextMessage 
        		+" Sended" + IsSended);
		
		if(IsSended == false){
			sendSMS();
		}
	}
	private void sendSMS(){
		PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
		SmsManager mSmsManager = SmsManager.getDefault();
		if(mPhoneNumber != null){			
			mSmsManager.sendTextMessage(
					mPhoneNumber, 
					null, 
					mTextMessage, 
					mPendingIntent, 
					null);
		}
		IsSended = true;
		
	}
}
