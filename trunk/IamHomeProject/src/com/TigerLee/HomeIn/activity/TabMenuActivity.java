package com.TigerLee.HomeIn.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.TigerLee.HomeIn.R;

public class TabMenuActivity extends DashboardTapActivity {
	 private TabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabmenu);
        
        mTabHost = getTabHost();
        mTabHost.getTabWidget().setDividerDrawable(R.drawable.popup_line_olivegreen);
		setupTab(new TextView(this), "", R.drawable.tap_search, new Intent(this, HomeIn01.class));
		setupTab(new TextView(this), "", R.drawable.tap_map, new Intent(this, GoogleMapPicker.class));
		
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
}
