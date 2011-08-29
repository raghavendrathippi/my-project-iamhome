package com.tigerlee.homein.activity;

import com.tigerlee.homein.R;
import com.tigerlee.homein.util.Constants;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setAboutMsg(getString(R.string.about_settings));
		
		addPreferencesFromResource(R.xml.settings);
		if (Constants.isRunningHomeIn || Constants.isRunningHomeOut) {
			Toast.makeText(this, getString(R.string.toast_pref_isRunning), Toast.LENGTH_LONG).show();
		}
	}
	private void setAboutMsg(String string) {
		Constants.ABOUT_ACTIVITY_STRING = string;
	}

}
