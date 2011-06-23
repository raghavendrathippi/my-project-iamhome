package com.TigerLee.HomeIn.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class IamHomeBroadcastReciver extends BroadcastReceiver{
	public static String TAG = "IamHomeBroadcastReciver";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
			Log.v(TAG, "ACTION_BOOT_COMPLETED");
		}else if(action.equals(Intent.ACTION_BATTERY_LOW)){
			Log.v(TAG, "ACTION_BATTERY_LOW");
		}else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){
			Log.v(TAG, "ACTION_PACKAGE_REMOVED");
		}else if(action.equals(Intent.ACTION_PACKAGE_RESTARTED)){
			Log.v(TAG, "ACTION_PACKAGE_RESTARTED");
		}
	}

}
