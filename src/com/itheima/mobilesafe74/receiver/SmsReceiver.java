package com.itheima.mobilesafe74.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.service.LocationService;
import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.SpUtil;

public class SmsReceiver extends BroadcastReceiver {

	private DevicePolicyManager mDPM;
	private ComponentName mComponentName;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		mComponentName = new ComponentName(context, DeviceAdmin.class);
		//1.判断是否开启了防盗保护
		boolean open_security = SpUtil.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
		if (open_security) {
			//2.获取短信的内容
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			//3.循环遍历短信
			for (Object object : objects) {
				//4.获取短信对象
				SmsMessage sms = SmsMessage.createFromPdu((byte[])object);
				//5.获取短信对象的基本信息
				String originatingAddress = sms.getOriginatingAddress();
				String messageBody = sms.getMessageBody();
				//6.判断短信内容中是否包含关键字
				if (messageBody.contains("#*alarm*#")) {
					//7.播放报警音乐
					MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
					mediaPlayer.setLooping(true);
					mediaPlayer.start();
				}
				
				if (messageBody.contains("#*location*#")) {
					//8.开启 获取位置服务
					context.startService(new Intent(context,LocationService.class));
				}
				//9.一键锁屏
				if (messageBody.contains("#*lockscreen*#")) {
					//判断设备管理器是否开启
					if (mDPM.isAdminActive(mComponentName)) {
						mDPM.lockNow();
					}else{
						Toast.makeText(context, "请先激活设备管理器", Toast.LENGTH_SHORT).show();
					}
				}
				//10.一键清除数据
				if (messageBody.contains("#*wipedata*#")) {
					//判断设备管理器是否开启
					if (mDPM.isAdminActive(mComponentName)) {
						mDPM.wipeData(0);
					}else{
						Toast.makeText(context, "请先激活设备管理器", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}

}
