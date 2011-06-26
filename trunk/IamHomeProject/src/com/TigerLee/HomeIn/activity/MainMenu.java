package com.TigerLee.HomeIn.activity;

import com.TigerLee.HomeIn.R;
import com.TigerLee.HomeIn.util.Constants;
import com.TigerLee.HomeIn.util.GPSInformation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainMenu extends Activity {

	public Button mHomeButton;
	public Button mSchoolButton;
	
	private static final int HOME = 0;
	private static final int SCHOOL = 1;
	
	private static final String TAG = "MainMenu";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.mainmenu);
		
		mHomeButton = (Button) findViewById(R.id.HomeButton);
		mSchoolButton = (Button) findViewById(R.id.SchoolButton);
		
		
		mHomeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Constants.D)  Log.v(TAG, "Click - Next Page Home");
				NextPage(HOME);
			}
		});/*
		mHomeButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					mHomeButton.setBackgroundColor(R.color.after_touch);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					mHomeButton.setBackgroundColor(R.color.before_touch);
				}
				return false;
			}
		});*/
		mSchoolButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(Constants.D)  Log.v(TAG, "Click - Next Page School");
				NextPage(SCHOOL);
			}
		});/*
		mSchoolButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){
					mSchoolButton.setBackgroundColor(R.color.after_touch);
				}else if(event.getAction() == MotionEvent.ACTION_UP){
					mSchoolButton.setBackgroundColor(R.color.before_touch);
				}
				return false;
			}
		});*/
	}
	public void NextPage(int which){
		if(!GPSInformation.IsLocationAvailable(this)){			
			return;
		}
		Intent intent = new Intent();
		switch(which){
		case HOME:
			intent.setClass(this, HomeIn01.class);
			startActivity(intent);
			return;
		case SCHOOL:
			intent.setClass(this, TabMenuActivity.class);
			startActivity(intent);
			return;
		}
	}

}
