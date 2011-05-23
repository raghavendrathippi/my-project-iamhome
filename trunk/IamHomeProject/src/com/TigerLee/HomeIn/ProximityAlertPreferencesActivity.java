package com.TigerLee.HomeIn;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ProximityAlertPreferencesActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

	}

}
