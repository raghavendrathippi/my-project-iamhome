/*
 * Copyright (C) 2007 The Android Open Source Project
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


import com.TigerLee.HomeIn.R;
import com.TigerLee.HomeIn.service.ProximityAlertService;
import com.TigerLee.HomeIn.util.Constants;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Contacts.Phones;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

 /**
 * A list view example where the 
 * data comes from a cursor, and a
 * SimpleCursorListAdapter is used to map each item to a two-line
 * display.
 */
public class test extends ListActivity implements OnClickListener{
	
	private static final String TAG = "test";
	public EditText mEditTextMessage;
	public ImageButton mImageButtonStart;
	public ListAdapter mListAdapter;
	
	private static final String[] PHONE_PROJECTION = new String[] {
		Phone.DISPLAY_NAME,
        Phone._ID,
        Phone.TYPE,
        Phone.LABEL,
        Phone.NUMBER
    };
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test);
        
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageButtonStart = (ImageButton) findViewById(R.id.bt_start);
        mImageButtonStart.setOnClickListener(this);
        
        // Get a cursor with all phones
        Cursor c = getContentResolver().query(Phone.CONTENT_URI,
                PHONE_PROJECTION, null, null, null);
        startManagingCursor(c);
        
        // Map Cursor columns to views defined in simple_list_item_2.xml
        mListAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_multiple_choice, c, 
                new String[] {Phone.DISPLAY_NAME, Phone.NUMBER},
                new int[] { android.R.id.text1, android.R.id.text2 });
        setListAdapter(mListAdapter);
        final ListView listView = getListView();
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_start:
				//String mPhoneNumber = mReceiverPhoneNumber.getText().toString();
				String mTextMessage = mEditTextMessage.getText().toString();			
				//Constants.EXTRA_PHONENUM = mPhoneNumber;
				Constants.EXTRA_TEXT_MSG = mTextMessage;		
				if(isAllsetRequiedInfomation()){
					if(!Constants.isRunningHomeIn){
						startProximityService();
					}
				}
				break;
		}
	}
	public boolean isAllsetRequiedInfomation(){
		return (Constants.EXTRA_PHONENUM != null 
				&& Constants.EXTRA_TEXT_MSG != null
				&& Constants.USER_DESTINATION_LAT != null
				&& Constants.USER_DESTINATION_LNG != null);
	}
	private void startProximityService(){
		Intent intent = new Intent(this,ProximityAlertService.class);
		startService(intent);
		Constants.isRunningHomeIn = true;		
		toast(getString(R.string.toast_startservice));
		finish();
        if(Constants.D) Log.v(TAG,"Start Proximity Service");
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Constants.EXTRA_PHONENUM="01034561626";
		Toast.makeText(this, "01034561626", Toast.LENGTH_SHORT).show();
	}
	public void toast (String msg){
	    Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
	} // end toast
  
}
