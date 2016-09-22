package com.itheima.mobilesafe74.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {
	
	private static ActivityManager mAM;

	/**
	 * @param ctx			上下文环境
	 * @param serviceName	判断是否正在运行的服务
	 * @return				true 运行		false 没有运行
	 */
	public static boolean isRunning(Context ctx, String serviceName){
		
		//1.获取activityManager对象，获取当前手机正在运行的服务
		mAM = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		//2.获取手机中正在运行的服务集合
		List<RunningServiceInfo> runningServices = mAM.getRunningServices(100);
		//3.遍历服务集合
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			//4.获取每一个真正运行服务的名称
			if (serviceName.equals(runningServiceInfo.service.getClassName())) {
				return true;
			}
		}
		
		return false;
	}
	
}
