package com.tigerlee.homein.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.tigerlee.homein.R;
import com.tigerlee.homein.util.Constants;

public class HomeInTapActivity extends DashboardTapActivity {
	 private TabHost mTabHost;

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
	        	setupTab(new TextView(this), "", R.drawable.ic_contact, new Intent(this, SendMessageActivity.class));
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
		}
	}