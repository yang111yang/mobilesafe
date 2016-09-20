package com.itheima.mobilesafe74.receiver;

import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.SpUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	private static final String tag = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		//一旦监听到开机广播，就需要发送短信给指定号码
		Log.i(tag, "开关机广播的触发");
		
		//1.获取本地存储的sim卡
		String spSimNmuber = SpUtil.getString(context, ConstantValue.SIM_NUMBER, "");
		//2.获取当前插入手机的sim卡
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String simSerialNumber = tm.getSimSerialNumber()+"xxx";
		//3.连个sim卡序列号比对
		if (!spSimNmuber.equals(simSerialNumber)) {
			//4.如果序列号不一致，则给指定的联系人发送短信
			SmsManager sm = SmsManager.getDefault();
			//4.1获取指定联系人的电话号码
			String phone = SpUtil.getString(context, ConstantValue.CONTACT_PHONE, "");
			//4.2发送短信
			sm.sendTextMessage(phone, null, "sim change", null, null);
		}
		
	}

}
