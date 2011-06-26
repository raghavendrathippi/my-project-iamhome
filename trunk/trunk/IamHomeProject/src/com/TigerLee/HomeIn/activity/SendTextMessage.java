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
		if(Constants.D) Log.v(TAG, "PhoneNum : " + Constants.EXTRA_PHONENUM 
        		+ " TextMessage : " + Constants.EXTRA_TEXT_MSG 
        		+" Sended : " + IsSended);
		
		if(IsSended == false){
			sendSMS();
		}
	}
	private void sendSMS(){
		PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
		SmsManager mSmsManager = SmsManager.getDefault();
		if(mPhoneNumber != null){			
			mSmsManager.sendTextMessage(
					Constants.EXTRA_PHONENUM, 
					null, 
					Constants.EXTRA_TEXT_MSG, 
					mPendingIntent, 
					null);
		}
		IsSended = true;		
	}
}
