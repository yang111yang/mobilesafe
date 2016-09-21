package com.itheima.mobilesafe74.service;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class LocationService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		//获取手机的经纬度坐标
		//1.获取位置管理者对象
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		//2.以最优的方式获取经纬度坐标
		Criteria criteria = new Criteria();
		//允许花费
		criteria.setCostAllowed(true);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//指定获取经纬度的精确度
		String bestProvider = lm.getBestProvider(criteria, true);
		//3.在一定的时间间隔，移动一定的距离后获取经纬度的坐标
		MyLocationListener myLocationListener = new MyLocationListener();
		lm.requestLocationUpdates(bestProvider, 0, 0, myLocationListener);
		
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	class MyLocationListener implements LocationListener{
		
		@Override
		public void onLocationChanged(Location location) {
			double longitude = location.getLongitude();//经度
			double latitude = location.getLatitude();//纬度
			//4.发送短信(添加权限)
			SmsManager sm = SmsManager.getDefault();
			sm.sendTextMessage("18200366709", null, "longitude = "+longitude+","+"latitude = "+latitude, null, null);
		}
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
	}
	
}
