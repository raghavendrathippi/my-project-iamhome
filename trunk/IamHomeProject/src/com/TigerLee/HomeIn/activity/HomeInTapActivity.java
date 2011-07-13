package com.TigerLee.HomeIn.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

import com.TigerLee.HomeIn.R;
import com.TigerLee.HomeIn.util.Constants;

public class HomeInTapActivity extends DashboardTapActivity {
	 private TabHost mTabHost;

	 public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(Constants.INTENT_MOVE_SECOND_TAP)){
				mTabHost.setCurrentTab(1);
			}
			
		}
	};
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.tabmenu);
	        mTabHost = getTabHost();
	        mTabHost.getTabWidget().setDividerDrawable(R.drawable.popup_line_olivegreen);
	        if(Constants.isRunningHomeIn){
	        	setupTab(new TextView(this), "", R.drawable.ic_map, new Intent(this, GoogleMapPicker.class));
	        }else{
	        	setupTab(new TextView(this), "", R.drawable.ic_map, new Intent(this, GoogleMapPicker.class));
	        	setupTab(new TextView(this), "", R.drawable.ic_contact, new Intent(this, test.class));
	        	IntentFilter mIntentFilter = new IntentFilter(Constants.INTENT_MOVE_SECOND_TAP);
	        	registerReceiver(mBroadcastReceiver, mIntentFilter);
	        }
		}
		private void setupTab(final View view, final String tag, int resID, Intent intent) {
			View tabview = createTabImageView(mTabHost.getContext(), resID);
	        TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);
	        mTabHost.addTab(setContent);
		}
		private static View createTabImageView(final Context context, int resID) {
			View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
			ImageView iv = (ImageView) view.findViewById(R.id.tabsImage);
			iv.setImageResource(resID);
			return view;
		}
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
			if(!Constants.isRunningHomeIn){
				unregisterReceiver(mBroadcastReceiver);
			}
		}
	}