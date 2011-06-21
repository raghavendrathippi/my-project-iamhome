package com.TigerLee.HomeIn.activity;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import com.TigerLee.HomeIn.R;

public class UserProfileActivity extends DashboardActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userprofile);
		TelephonyManager telephony = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		TextView mTextView1 = (TextView) findViewById(R.id.profile_text1);
		if(telephony != null){
			mTextView1.setText("전화번호: " + telephony.getLine1Number()); 
		}
		
	}

}
