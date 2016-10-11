package com.itheima.mobilesafe74.service;

import java.util.Timer;
import java.util.TimerTask;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.engine.ProcessInfoProvider;
import com.itheima.mobilesafe74.receiver.MyAppWidgetProvider;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.DocumentsContract.Root;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * 窗体小部件
 * @author 刘建阳
 * @date 2016-10-11 上午8:34:58
 */
public class UpdateWidgetService extends Service {

	protected static final String tag = "UpdateWidgetService";
	private Timer mTimer;
	private InnerReceiver mInnerReceiver;

	@Override
	public void onCreate() {
		//管理进程总数和可用内存数更新(定时器)
		super.onCreate();
		startTimer();
		
		//注册广播接收者，监听屏幕的解锁和锁屏
		IntentFilter intentFilter = new IntentFilter();
		//屏幕解锁
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		//屏幕锁屏
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		
		mInnerReceiver = new InnerReceiver();
		registerReceiver(mInnerReceiver, intentFilter);
		
	}
	
	class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
				//屏幕解锁
				startTimer();
			}else{
				//屏幕锁屏
				//取消定时更新任务
				cancelTimerTask();
			}
		}
		
	}
	
	/**
	 * 开启一个定时器
	 */
	private void startTimer() {
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				updateAppWidget();
				Log.i(tag, "每5秒一次的定时更新任务正在进行中......");
			}
		}, 0, 5000);
	}

	/**
	 * 取消定时更新任务
	 */
	public void cancelTimerTask() {
		if (mTimer != null) {
			mTimer.cancel();
		}
	}

	/**
	 * 更新窗体小部件
	 */
	protected void updateAppWidget() {
		//1.获取AppWidgetManager对象
		AppWidgetManager aWM = AppWidgetManager.getInstance(this);
		//2.获取窗体小部件的布局转换成View对象(当前应用的包名,当前应用中的那个布局的ID)
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
		//3.给窗体小部件的内部控件赋值
		remoteViews.setTextViewText(R.id.tv_process_count, "进程总数：" + ProcessInfoProvider.getProcessCount(this));
		String strAvailSpace = Formatter.formatFileSize(this, ProcessInfoProvider.getAvailSpace(this));
		remoteViews.setTextViewText(R.id.tv_process_memory, "可用内存：" + strAvailSpace);
		
		//点击窗体小部件，进入应用
		Intent intent = new Intent("android.intent.action.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);

		//通过延期意图，发送广播，在广播中杀死进程
		Intent broadcastIntent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
		PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.btn_clear, broadcast);
		
		//(上下文环境,窗体小部件对应的广播接收者对应的字节码文件)
		ComponentName componentName = new ComponentName(this, MyAppWidgetProvider.class);
		//更新窗体小部件
		aWM.updateAppWidget(componentName, remoteViews);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//注销广播接收者
		if (mInnerReceiver != null) {
			unregisterReceiver(mInnerReceiver);
		}
		//取消定时更新任务
		cancelTimerTask();
	}
	
}
