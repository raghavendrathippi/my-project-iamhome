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
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
public class SendMessageActivity extends ListActivity implements OnClickListener{
	
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
	public static int COLUMNS_DISPLAY_NAME = 0;
	public static int COLUMNS_ID = 1;
	public static int COLUMNS_TYPE = 2;
	public static int COLUMNS_LABEL = 3;
	public static int COLUMNS_NUMBER = 4;
	
	private static final String TAG = "test";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.send_message);
        
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageButtonStart = (ImageButton) findViewById(R.id.bt_start);
        mImageButtonStart.setOnClickListener(this);
        
        // Get a cursor with all phones
        Cursor c = getContentResolver().query(Phone.CONTENT_URI,
                PHONE_PROJECTION, null, null, null);
        startManagingCursor(c);
        
        // Map Cursor columns to views defined in simple_list_item_2.xml
        mListAdapter = new SimpleCursorAdapter(this,
        		R.layout.customlist, c, 
        		new String[] { Phone.DISPLAY_NAME, Phone.NUMBER},
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
		if(Constants.D){
			Log.e(TAG,"LAT:" + Constants.USER_DESTINATION_LAT);
			Log.e(TAG,"LNG:" + Constants.USER_DESTINATION_LNG);
			Log.e(TAG,"MSG:" + Constants.EXTRA_TEXT_MSG);
			Log.e(TAG,"NUM:" + Constants.EXTRA_PHONENUM);
		}
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
		Cursor c = (Cursor)mListAdapter.getItem(position);
		String phoneNum = c.getString(COLUMNS_NUMBER);		
		Toast.makeText(this, phoneNum, Toast.LENGTH_SHORT).show();
		Constants.EXTRA_PHONENUM = phoneNum;
	}
	public void toast (String msg){
	    Toast.makeText (getApplicationContext(), msg, Toast.LENGTH_SHORT).show ();
	} // end toast
  
}
