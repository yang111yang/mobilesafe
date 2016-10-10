package com.itheima.mobilesafe74.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UpdateWidgetService extends Service {

	@Override
	public void onCreate() {
		//管理进程总数和可用内存数更新(定时器)
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
