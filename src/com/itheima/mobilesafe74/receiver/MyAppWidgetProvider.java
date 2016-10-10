package com.itheima.mobilesafe74.receiver;

import com.itheima.mobilesafe74.service.UpdateWidgetService;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MyAppWidgetProvider extends AppWidgetProvider {
	
	private static final String tag = "MyAppWidgetProvider";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(tag, "onReceive。。。。。。。。。。");
		super.onReceive(context, intent);
	}
	
	@Override
	public void onEnabled(Context context) {
		//创建第一个窗体小部件调用的方法
		Log.i(tag, "onEnabled 创建第一个窗体小部件调用的方法");
		super.onEnabled(context);
		//开启服务
		context.startService(new Intent(context,UpdateWidgetService.class));
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		//创建多一个窗体小部件时调用的方法
		Log.i(tag, "onUpdate 创建多一个窗体小部件时调用的方法");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		//开启服务
		context.startService(new Intent(context,UpdateWidgetService.class));
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		//当小部件的宽高发生改变时调用的方法，创建小部件时，也会调用此方法
		Log.i(tag, "onAppWidgetOptionsChanged 创建多一个窗体小部件时调用的方法");
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
		//开启服务
		context.startService(new Intent(context,UpdateWidgetService.class));
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		//删除一个窗体小部件时调用的方法
		Log.i(tag, "onDeleted 删除一个窗体小部件时调用的方法");
		super.onDeleted(context, appWidgetIds);
		//开启服务
		context.startService(new Intent(context,UpdateWidgetService.class));
	}
	
	@Override
	public void onDisabled(Context context) {
		//删除最后一个窗体小部件时调用的方法
		Log.i(tag, "onDisabled 删除最后一个窗体小部件时调用的方法");
		super.onDisabled(context);
		//关闭服务
		context.stopService(new Intent(context,UpdateWidgetService.class));
	}
}
