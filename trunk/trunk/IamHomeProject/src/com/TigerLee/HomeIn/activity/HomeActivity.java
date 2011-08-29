/*
 * Copyright (C) 2011 Wglxy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tigerlee.homein.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tigerlee.homein.R;
import com.tigerlee.homein.service.ProximityAlertService;
import com.tigerlee.homein.util.Constants;
import com.tigerlee.homein.util.SharedPreference;

/**
 * This is a simple activity that demonstrates the dashboard user interface pattern.
 *
 */

public class HomeActivity extends DashboardActivity 
{

/**
 * onCreate - called when the activity is first created.
 * Called when the activity is first created. 
 * This is where you should do all of your normal static set up: create views, bind data to lists, etc. 
 * This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
 * 
 * Always followed by onStart().
 *
 */

protected void onCreate(Bundle savedInstanceState) 
{
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
}



/**
 * onDestroy
 * The final call you receive before your activity is destroyed. 
 * This can happen either because the activity is finishing (someone called finish() on it, 
 * or because the system is temporarily destroying this instance of the activity to save space. 
 * You can distinguish between these two scenarios with the isFinishing() method.
 *
 */

protected void onDestroy ()
{
   super.onDestroy ();
}

/**
 * onPause
 * Called when the system is about to start resuming a previous activity. 
 * This is typically used to commit unsaved changes to persistent data, stop animations 
 * and other things that may be consuming CPU, etc. 
 * Implementations of this method must be very quick because the next activity will not be resumed 
 * until this method returns.
 * Followed by either onResume() if the activity returns back to the front, 
 * or onStop() if it becomes invisible to the user.
 *
 */

protected void onPause ()
{
   super.onPause ();
}

/**
 * onRestart
 * Called after your activity has been stopped, prior to it being started again.
 * Always followed by onStart().
 *
 */

protected void onRestart ()
{
   super.onRestart ();
}

/**
 * onResume
 * Called when the activity will start interacting with the user. 
 * At this point your activity is at the top of the activity stack, with user input going to it.
 * Always followed by onPause().
 *
 */

protected void onResume ()
{
   super.onResume ();
}

/**
 * onStart
 * Called when the activity is becoming visible to the user.
 * Followed by onResume() if the activity comes to the foreground, or onStop() if it becomes hidden.
 *
 */

protected void onStart ()
{
	super.onStart ();
   if(!Constants.isRunningHomeIn && !Constants.isRunningHomeOut){
		Constants.init();
		setupConstants();
	}
}

/**
 * onStop
 * Called when the activity is no longer visible to the user
 * because another activity has been resumed and is covering this one. 
 * This may happen either because a new activity is being started, an existing one 
 * is being brought in front of this one, or this one is being destroyed.
 *
 * Followed by either onRestart() if this activity is coming back to interact with the user, 
 * or onDestroy() if this activity is going away.
 */

protected void onStop ()
{
   super.onStop ();
}

/**
 */
// Click Methods


/**
 */
// More Methods

private void setupConstants() {
	SharedPreference mSharedPreference = new SharedPreference(this);
	String address = mSharedPreference.getAddress();
	Log.v("HomeActivity", "Address: "+ address);
	Constants.USER_DESTINATION_ADDRESS = address;
	
	
	Double latitude = mSharedPreference.getLatitude();
	Log.v("HomeActivity", "Lat: "+ latitude);
	Constants.USER_DESTINATION_LAT = latitude;
	
	Double longitude = 	mSharedPreference.getLongitude();
	Log.v("HomeActivity", "Lng: "+ longitude);
	Constants.USER_DESTINATION_LNG = longitude;
	/*
	String userImage = mSharedPreference.getUserImage();
	Log.v("HomeActivity", "UserImage: "+ userImage);
	*/
	String msg = mSharedPreference.getTextMsg();
	Log.v("HomeActivity", "msg: "+ msg);
	Constants.EXTRA_TEXT_MSG = msg;
	
	String phoneNum = mSharedPreference.getPhoneNum();
	Log.v("HomeActivity", "phoneNum: "+ phoneNum);
	Constants.EXTRA_PHONENUM = phoneNum;
	
	String minFreq = mSharedPreference.getMinimumFrequency();
	Log.v("HomeActivity", "minFreq: "+ minFreq);	
	if(minFreq!=null){
		Constants.MIN_FREQUENCY = Integer.parseInt(minFreq);
	}
	
	String minDis = mSharedPreference.getMinimumDistance();	
	Log.v("HomeActivity", "minDis: "+ minDis);
	if(minDis!=null) {
		Constants.MIN_DISTANCE = Double.parseDouble(minDis);
	}
	/*
	String result = mSharedPreference.getResult();
	Log.v("HomeActivity", "Result: "+ result);
	*/
	Boolean IsRunning = mSharedPreference.getRunning();
	Log.v("HomeActivity", "Running: "+ IsRunning);
	if(IsRunning == true){
		//User has not been reached home properly.
		Constants.isRunningHomeIn = true;
		startProximityService();
	}else{
		Constants.isRunningHomeIn = false;
	}	
	Constants.isShowDialog = mSharedPreference.getShowDialog();
	Log.v("HomeActivity", "isShowDialog: "+ Constants.isShowDialog);	
	
}
private void startProximityService(){
	Intent intent = new Intent(this,ProximityAlertService.class);
	startService(intent);
	toast(getString(R.string.toast_restart));
}
} // end class
