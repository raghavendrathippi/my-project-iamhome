package com.tigerlee.homein.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.UriMatcher;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tigerlee.homein.R;
import com.tigerlee.homein.util.SharedPreference;
import com.tigerlee.homein.geocoder.NativeGeocoder;

public class UserProfileActivity extends DashboardActivity implements OnClickListener{
	
	public ImageView mUserImage;
	public TextView mTextName;
	public TextView mTextAddress;
	public TextView mTextPhoneNum;
	
	private static final int IMAGE_REQUEST = 0;
	private static final int DIALOG_ADDRESS = 1;
	private static final int DIALOG_NAME = 2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userprofile);
		
		setAboutMsg(getString(R.string.about_profile));
		
		mUserImage = (ImageView) findViewById(R.id.profile_image);		
		mTextName = (TextView) findViewById(R.id.profile_name);
		mTextAddress = (TextView) findViewById(R.id.profile_address);
		mTextPhoneNum = (TextView) findViewById(R.id.profile_phonenum);	
		
		//setupImage();
		//setupText();
		mUserImage.setOnClickListener(this);
	}/*
	public void setupImage(){
		Uri mUri = getImageUri();
		if(mUri != null){
			mUserImage.setImageURI(mUri);			
		}else{
			mUserImage.setImageResource(R.drawable.default_userimage);
		}
	}

	public Uri getImageUri(){
		SharedPreference mSharedPreference = new SharedPreference(this);
		String uri = mSharedPreference.getUserImage();
		if(uri!=null){
			return Uri.parse(uri);
		}else{
			return null;
		}
		
	}

	public void setupText(){		
		TelephonyManager telephony = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		if(telephony != null){
			mTextPhoneNum.setText(getString(R.string.profile_phoneNum) 
					+ telephony.getLine1Number()); 
		}
		SharedPreference mSharedPreference = new SharedPreference(this);
		String name = mSharedPreference.getUserName();
		String address = mSharedPreference.getAddress();
		if(name != null){
			mTextName.setText(getString(R.string.profile_name)+name);
		}else{
			mTextName.setText(getString(R.string.profile_name)
					+ getString(R.string.profile_default_name));
		}
		if(address != null){
			mTextAddress.setText(getString(R.string.profile_address)+address);
		}else{
			mTextAddress.setText(getString(R.string.profile_address)
					+ getString(R.string.profile_default_address));
		}
	}
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.profile_image){
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
			intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.
	        startActivityForResult(intent, IMAGE_REQUEST);
		}else if(v.getId() == R.id.profile_address){
			showDialog(DIALOG_ADDRESS);
		}else if(v.getId() == R.id.profile_name){
			showDialog(DIALOG_NAME);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			if(requestCode == IMAGE_REQUEST){
				Uri mUri = data.getData();
				if(mUri != null){
					mUserImage.setImageURI(mUri);
					setImageUri(mUri);					
				}				
			}
		}		
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void setImageUri(Uri uri){		
		SharedPreference mSharedPreference = new SharedPreference(this);
		if(uri!=null){
			mSharedPreference.setUserImage(uri.toString());
		}
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == DIALOG_ADDRESS){
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
			View dialogView = getLayoutInflater().inflate(R.layout.dialog_edittext, 
					(ViewGroup) findViewById(R.id.dialog_layout));
			mBuilder.setView(dialogView);
			final TextView mTextView = (TextView) dialogView.findViewById(R.id.dialog_title);
			mTextView.setText(getString(R.string.profile_default_address));
			final EditText mEditText = (EditText) dialogView.findViewById(R.id.dialog_edittext);
			//mBuilder.setMessage(getString(R.string.profile_default_address));
			mBuilder.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {					
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String text = mEditText.getText().toString();
					if(text!=null){
						setName(text);
					}
					return;
				}
			}).setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {					
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			}).show();
		}else if(id == DIALOG_NAME){
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
			View dialogView = getLayoutInflater().inflate(R.layout.dialog_edittext, 
					(ViewGroup) findViewById(R.id.dialog_layout));
			mBuilder.setView(dialogView);
			final TextView mTextView = (TextView) dialogView.findViewById(R.id.dialog_title);
			mTextView.setText(getString(R.string.profile_default_name));
			final EditText mEditText = (EditText) dialogView.findViewById(R.id.dialog_edittext);
			mBuilder.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {					
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String text = mEditText.getText().toString();
					if(text!=null){
						setAddress(text);
					}
					return;
				}
			}).setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {					
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			}).show();
		}
		return super.onCreateDialog(id);
	}
	public void setName(String name){		
		SharedPreference mSharedPreference = new SharedPreference(this);
		if(name!=null){
			mSharedPreference.setUserName(name);
		}
	}
	public void setAddress(String address){		
		SharedPreference mSharedPreference = new SharedPreference(this);
		if(address!=null){
			mSharedPreference.setAddress(address);
			Address mAddress = NativeGeocoder.getGeocodedAddress(address);
			if(mAddress!=null){
				mSharedPreference.setLatitude(mAddress.getLatitude());
				mSharedPreference.setLatitude(mAddress.getLongitude());
			}
		}
	}*/
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
