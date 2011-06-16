package com.TigerLee.HomeIn.activity;

import com.TigerLee.HomeIn.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ProximityAlertPreferencesActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

}
