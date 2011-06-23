package com.TigerLee.HomeIn.activity;

import com.TigerLee.HomeIn.R;
import com.TigerLee.HomeIn.util.Constants;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		if (Constants.isRunningHomeIn || Constants.isRunningHomeOut) {
			Toast.makeText(this, "", Toast.LENGTH_LONG).show();
		}
	}

}
