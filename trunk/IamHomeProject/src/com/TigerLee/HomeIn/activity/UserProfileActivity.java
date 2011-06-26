package com.TigerLee.HomeIn.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.TigerLee.HomeIn.R;

public class UserProfileActivity extends DashboardActivity implements OnClickListener{
	
	public ImageView mUserImage;
	public TextView mTextName;
	public TextView mTextAddress;
	public TextView mTextPhoneNum;
	
	private static final int IMAGE_REQUEST = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userprofile);
		
		mUserImage = (ImageView) findViewById(R.id.profile_image);		
		mTextName = (TextView) findViewById(R.id.profile_name);
		mTextAddress = (TextView) findViewById(R.id.profile_address);
		mTextPhoneNum = (TextView) findViewById(R.id.profile_phonenum);	
		
		setupImage();
		setupText();
		mUserImage.setOnClickListener(this);
	}
	public void setupImage(){
		Uri mUri = getImageUri();
		if(mUri != null){
			mUserImage.setImageURI(mUri);			
		}else{
			mUserImage.setImageResource(R.drawable.default_userimage);
		}
	}
	public void setupText(){
		
		TelephonyManager telephony = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		if(telephony != null){
			mTextAddress.setText("전화번호: " + telephony.getLine1Number()); 
		}
	}	

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.profile_image){
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
			intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // images on the SD card.
	        startActivityForResult(intent, IMAGE_REQUEST);
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
		
	}
	public Uri getImageUri(){
		return null;
	}

}
