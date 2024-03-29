package com.tigerlee.homein.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;

import com.tigerlee.homein.R;
import com.tigerlee.homein.util.Constants;
import com.tigerlee.homein.util.SharedPreference;

public class IamHomeBroadcastReceiver extends BroadcastReceiver{
	
	private static final int TIME_FOR_VIBRATOR = 500;
	private static final int NOTIFICATION_ID = 1000;
	private static final int TIME_FOR_LED = 1500;
	
    public static final String DISABLE_GPS_INTENT = "com.tigerlee.homein.intent.action.DISABLE_GPS_INTENT";
    public static final String DISABLE_NETWORK_LOCATION_INTENT = "com.tigerlee.homein.intent.action.DISABLE_NETWORK_LOCATION_INTENT";
    public static final String FORCE_CLOSED_INTENT = "com.tigerlee.homein.intent.action.FORCE_CLOSED_INTENT";
    public static final String SUCCESS_INTENT = "com.tigerlee.homein.intent.action.SUCCESS_INTENT";

	public boolean mIsSend = false;
	public static String TAG = "IamHomeBroadcastReciver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
			if(Constants.D) Log.v(TAG, "ACTION_BOOT_COMPLETED");
			try { Thread.sleep(100000); } catch (InterruptedException e) { }
			handleForceClosed(context);
		}else if(action.equals(Intent.ACTION_BATTERY_LOW)){
			if(Constants.D) Log.v(TAG, "ACTION_BATTERY_LOW");
		}else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){
			if(Constants.D) Log.v(TAG, "ACTION_PACKAGE_REMOVED");
			
			handleForceClosed(context);
		}else if(action.equals(Intent.ACTION_PACKAGE_RESTARTED)){
			if(Constants.D) Log.v(TAG, "ACTION_PACKAGE_RESTARTED");
			
			handleForceClosed(context);
		}else if(action.equals(DISABLE_GPS_INTENT)){
			if(Constants.D) Log.v(TAG, "DISABLE_GPS_INTENT");
			createVibration(context);
			setNotification(context,
					context.getString(R.string.noti_disablegps_name),
					context.getString(R.string.noti_disablegps_msg));
		}else if(action.equals(DISABLE_NETWORK_LOCATION_INTENT)){
			if(Constants.D) Log.v(TAG, "DISABLE_NETWORK_LOCATION_INTENT");
		}else if(action.equals(SUCCESS_INTENT)){
			if(Constants.D) Log.v(TAG, "SUCCESS_INTENT");
			Constants.isEnd = true;
			setNotification(context, 
					context.getString(R.string.noti_success_name), 
					context.getString(R.string.noti_success_msg));
			sendSMS();
		}else if(action.equals(FORCE_CLOSED_INTENT)){
			if(Constants.D) Log.v(TAG, "FORCE_CLOSED_INTENT");
			handleForceClosed(context);
		}
	}
	private void createVibration(Context context){
		//Notify a user by using a vibrator.
		Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(TIME_FOR_VIBRATOR);
	}
	/*
	 * Generating a notification message when reaching home. 1. Get a
	 * notification instance & a pending intent 2. Call
	 * createNotification() - set custom notification. 3. set a message
	 * then notify.
	 */
	private Notification createNotification() {
		Notification notification = new Notification();
		notification.icon = R.drawable.ic_in;
		notification.when = System.currentTimeMillis();

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;

		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;

		notification.ledARGB = Color.WHITE;
		notification.ledOnMS = TIME_FOR_LED;
		notification.ledOffMS = TIME_FOR_LED;

		if(Constants.D) Log.v(TAG, "Create Notification successfully");
		return notification;
	}
	private void setNotification(Context context, String name, String message){
		NotificationManager mNotificationManager = (NotificationManager) 
		context.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, null, 0);

		Notification mNotification = createNotification();
		mNotification.setLatestEventInfo(
				context,
				name,
				message, 
				pendingIntent);

		mNotificationManager.notify(NOTIFICATION_ID, mNotification);
		if(Constants.D) Log.v(TAG, "Notify - Name: "+ name + " Message" +message);
	}
	private void handleForceClosed(Context context){
		Constants.isRunningHomeIn = false;
		SharedPreference mSharedPreference = new SharedPreference(context);
		//Application is not running anymore
        mSharedPreference.setIsRunning(false);
		setNotification(context, 
				context.getString(R.string.noti_forceclose_name), 
				context.getString(R.string.noti_forceclose_msg));
		Constants.EXTRA_TEXT_MSG = context.getString(R.string.noti_forceclose_msg);
		sendSMS();
	}
	public void sendSMS(){
		if(!mIsSend){
			SmsManager mSmsManager = SmsManager.getDefault();
			mSmsManager.sendTextMessage(Constants.EXTRA_PHONENUM, null, 
						Constants.EXTRA_TEXT_MSG, null,	null);
			mIsSend = true;
		}			
	}
}
