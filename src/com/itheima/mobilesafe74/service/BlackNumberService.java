package com.itheima.mobilesafe74.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BlackNumberService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
}
