package com.itheima.mobilesafe74.service;

import com.itheima.mobilesafe74.db.dao.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;

public class BlackNumberService extends Service {

	private InnerSmsReceiver mInnerSmsReceiver;

	private BlackNumberDao mDao;

	@Override
	public void onCreate() {
		super.onCreate();
		
		IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(1000);
		mInnerSmsReceiver = new InnerSmsReceiver();
		registerReceiver(mInnerSmsReceiver, intentFilter);
		
	}
	
	class InnerSmsReceiver extends BroadcastReceiver{


		@Override
		public void onReceive(Context context, Intent intent) {
			//1.获取短信的内容
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			//2.循环遍历短信
			for (Object object : objects) {
				//3.获取短信对象
				SmsMessage sms = SmsMessage.createFromPdu((byte[])object);
				//4.获取短信对象的基本信息
				String originatingAddress = sms.getOriginatingAddress();
				
				mDao = BlackNumberDao.getInstance(getApplicationContext());
				int mode = mDao.getMode(originatingAddress);
				if (mode == 1 || mode == 3) {
					//拦截短信
					abortBroadcast();
				}
			}
		}
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mInnerSmsReceiver != null) {
			unregisterReceiver(mInnerSmsReceiver);
		}
	}
	
}
