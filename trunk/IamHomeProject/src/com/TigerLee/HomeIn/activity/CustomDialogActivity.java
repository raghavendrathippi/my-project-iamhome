/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.TigerLee.HomeIn.activity;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.TextView;

import com.TigerLee.HomeIn.R;
import com.TigerLee.HomeIn.util.Constants;

/**
 * <h3>Dialog Activity</h3>
 * 
 * <p>This demonstrates the how to write an activity that looks like 
 * a pop-up dialog with a custom theme using a different text color.</p>
 */
public class CustomDialogActivity extends Activity {
    
	/**
     * Initialization of the Activity after it is first created.  Must at least
     * call {@link android.app.Activity#setContentView setContentView()} to
     * describe what is to be displayed in the screen.
     */
	
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        // Be sure to call the super class.
        super.onCreate(savedInstanceState);
        
        // See assets/res/any/layout/dialog_activity.xml for this
        // view layout definition, which is being set here as
        // the content of our screen.
        setContentView(R.layout.custom_dialog_activity);
        /*
        Typeface mTypefaceTypeface = Typeface.createFromAsset(getAssets(), "fonts/NanumGothic.otf");
        TextView mTextView = (TextView) findViewById(R.id.custom_dialog_text);
        
        mTextView.setTypeface(mTypefaceTypeface);
        */
        getMessage();
        mHandler.sendMessageDelayed(mHandler.obtainMessage(Constants.DESTROY_ACTIVITY), Constants.FIVE_SECOND);
    }
    public void getMessage(){
    	TextView mTextView = (TextView) findViewById(R.id.custom_dialog_text);
    	Intent intent = getIntent();
    	String text = intent.getStringExtra(Intent.EXTRA_TEXT);
    	mTextView.setText(text);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Skip back key when showing intro activity
		if(keyCode == KeyEvent.KEYCODE_BACK)
			return false;
		return super.onKeyDown(keyCode, event);
    }
    private Handler mHandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case Constants.DESTROY_ACTIVITY:
				finish();
				break;
			default:
				break;
			}
    		
    	};
    
    };
}
