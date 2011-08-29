package com.tigerlee.homein.util;


import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class GPSInformation {

	public static LocationManager mLocationManager;
	
	private static final String TAG = "GPSInformation";

	
	// Determine turned off GPS
	public static boolean IsTurnOnGPS(Context context){

		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		String mProvider = LocationManager.GPS_PROVIDER;

		if(!mLocationManager.isProviderEnabled(mProvider)){
			if(Constants.D)  Log.v(TAG, "GPS Provider disabled");
			return false;
		}
		return true;
	}
	
	
	// Determine how to get a location information.
	public static String getProviderGPS(Context context){
		
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		//1. checking available GPS
		Criteria mCriteria = new Criteria();
		mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		mCriteria.setPowerRequirement(Criteria.POWER_HIGH);
	    mCriteria.setSpeedRequired(true);
		mCriteria.setCostAllowed(true);
		
		String mProvider = mLocationManager.getBestProvider(mCriteria, true);
		Location mLocation = mLocationManager.getLastKnownLocation(mProvider);
		
		
		// Cannot catch a signal from GPS.
		if (mLocation == null) {			
			if(Constants.D)  Log.v(TAG, "Cannot get a signal from GPS.");
			mCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
			mCriteria.setPowerRequirement(Criteria.POWER_HIGH);
		    mCriteria.setSpeedRequired(false);
			
			//2. checking 
			mProvider = mLocationManager.getBestProvider(mCriteria, true);
			mLocation = mLocationManager.getLastKnownLocation(mProvider);
		}
		if(Constants.D)  Log.v(TAG, "GPS Provider - " + mProvider);
		return mProvider;
	}
	public static boolean IsLocationAvailable(Context context){
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		String mProvider = getProviderGPS(context);
		Location mLocation = mLocationManager.getLastKnownLocation(mProvider);
		if(mLocation != null){
			if(Constants.D)  Log.v(TAG, "Locaiton is available.");
			return true;
		}
		if(Constants.D)  Log.v(TAG, "Locaiton is NOT available.");
		return false;
	}
	
    public static double distance(double P1_latitude/*C10*/, double P1_longitude/*C11*/, double P2_latitude/*C12*/, double P2_longitude/*C13*/)
    {
        if ((P1_latitude == P2_latitude)&&(P1_longitude == P2_longitude))
        {
            return 0;
        }

        double e10 = P1_latitude * Math.PI / 180;
        double e11 = P1_longitude * Math.PI / 180;
        double e12 = P2_latitude * Math.PI / 180;
        double e13 = P2_longitude * Math.PI / 180;

        /* Ÿ��ü GRS80 */
        double c16 = 6356752.314140910;
        double c15 = 6378137.000000000;
        double c17 = 0.0033528107;
        

        double f15 = c17 + c17 * c17;
        double f16 = f15 / 2;
        double f17 = c17 * c17 / 2;
        double f18 = c17 * c17 / 8;
        double f19 = c17 * c17 / 16;

        double c18 = e13 - e11;
        double c20 = (1 - c17) * Math.tan(e10);
        double c21 = Math.atan(c20);
        double c22 = Math.sin(c21);
        double c23 = Math.cos(c21);
        double c24 = (1 - c17) * Math.tan(e12); 
        double c25 = Math.atan(c24);
        double c26 = Math.sin(c25);
        double c27 = Math.cos(c25);

        double c29 = c18;
        double c31 = (c27 * Math.sin(c29) * c27 * Math.sin(c29)) + (c23 * c26 - c22 * c27 * Math.cos(c29)) * (c23 * c26 - c22 * c27 * Math.cos(c29));
        double c33 = (c22 * c26) + (c23 * c27 * Math.cos(c29));
        double c35 = Math.sqrt(c31) / c33;
        double c36 = Math.atan(c35);
        double c38 = 0;
        if (c31==0)
        {
            c38 = 0;
        }else{
            c38 = c23 * c27 * Math.sin(c29) / Math.sqrt(c31);
        }

        double c40 = 0;
        if ((Math.cos(Math.asin(c38)) * Math.cos(Math.asin(c38))) == 0)
        {
            c40 = 0;
        }else{
            c40 = c33 - 2 * c22 * c26 / (Math.cos(Math.asin(c38)) * Math.cos(Math.asin(c38)));
        }

        double c41 = Math.cos(Math.asin(c38)) * Math.cos(Math.asin(c38)) * (c15 * c15 - c16 * c16) / (c16 * c16);
        double c43 = 1 + c41 / 16384 * (4096 + c41 * (-768 + c41 * (320 - 175 * c41)));
        double c45 = c41 / 1024 * (256 + c41 * (-128 + c41 * (74 - 47 * c41)));
        double c47 = c45 * Math.sqrt(c31) * (c40 + c45 / 4 * (c33 * (-1 + 2 * c40 * c40) - c45 / 6 * c40 * (-3 + 4 * c31) * (-3 + 4 * c40 * c40)));
        double c50 = c17 / 16 * Math.cos(Math.asin(c38)) * Math.cos(Math.asin(c38)) * (4 + c17 * (4 - 3 * Math.cos(Math.asin(c38)) * Math.cos(Math.asin(c38))));
        double c52 = c18 + (1 - c50) * c17 * c38 * (Math.acos(c33) + c50 * Math.sin(Math.acos(c33)) * (c40 + c50 * c33 * (-1 + 2 * c40 * c40)));

        double c54 = c16 * c43 * (Math.atan(c35) - c47);
        
        // return distance in meter
        return c54;
    }


    public static short bearingP1toP2(double P1_latitude, double P1_longitude, double P2_latitude, double P2_longitude)
    {
        // ���� ��ġ : ������ �浵�� ���� �߽��� ������� �ϴ� �����̱� ������ ���� ������ ��ȯ�Ѵ�.
        double Cur_Lat_radian = P1_latitude * (3.141592 / 180);
        double Cur_Lon_radian = P1_longitude * (3.141592 / 180);


        // ��ǥ ��ġ : ������ �浵�� ���� �߽��� ������� �ϴ� �����̱� ������ ���� ������ ��ȯ�Ѵ�.
        double Dest_Lat_radian = P2_latitude * (3.141592 / 180);
        double Dest_Lon_radian = P2_longitude * (3.141592 / 180);

        // radian distance
        double radian_distance = 0;
        radian_distance = Math.acos(Math.sin(Cur_Lat_radian) * Math.sin(Dest_Lat_radian) + Math.cos(Cur_Lat_radian) * Math.cos(Dest_Lat_radian) * Math.cos(Cur_Lon_radian - Dest_Lon_radian));

        // ������ �̵� ������ ���Ѵ�.(���� ��ǥ���� ���� ��ǥ�� �̵��ϱ� ���ؼ��� ������ �����ؾ� �Ѵ�. ���Ȱ��̴�.
        double radian_bearing = Math.acos((Math.sin(Dest_Lat_radian) - Math.sin(Cur_Lat_radian) * Math.cos(radian_distance)) / (Math.cos(Cur_Lat_radian) * Math.sin(radian_distance)));		// acos�� �μ��� �־����� x�� 360�й��� ������ �ƴ� radian(ȣ��)���̴�.		

        double true_bearing = 0;
        if (Math.sin(Dest_Lon_radian - Cur_Lon_radian) < 0)
        {
            true_bearing = radian_bearing * (180 / 3.141592);
            true_bearing = 360 - true_bearing;
        }
        else
        {
            true_bearing = radian_bearing * (180 / 3.141592);
        }

        return (short)true_bearing;
    }

}
