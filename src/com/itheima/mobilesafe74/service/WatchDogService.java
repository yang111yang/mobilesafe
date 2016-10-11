package com.itheima.mobilesafe74.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.itheima.mobilesafe74.activity.EnterPsdActivity;
import com.itheima.mobilesafe74.db.dao.AppLockDao;

public class WatchDogService extends Service {

	private boolean isWatch;
	private AppLockDao mDao;
	private List<String> mPackageNameList;
	private IntentFilter mIntentFilter;
	private String mSkipPackageName;
	private InnerReceiver mInnerReceiver;
	private MyContentObserver mContentObserver;

	@Override
	public void onCreate() {
		super.onCreate();
		mDao = AppLockDao.getInstance(this);
		isWatch = true;
		watch();
		
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("android.intent.action.SKIP");
		mInnerReceiver = new InnerReceiver();
		registerReceiver(mInnerReceiver, mIntentFilter);
		
		//注册一个内容观察者，观察数据库的变化，一旦数据库有删除或者添加，则需要让mPackageNameList重新获取数据
		mContentObserver = new MyContentObserver(new Handler());
		getContentResolver().registerContentObserver(Uri.parse("content://applock/change"), true, mContentObserver);
		
	}
	
	class MyContentObserver extends ContentObserver{

		public MyContentObserver(Handler handler) {
			super(handler);
		}
		
		//一旦数据库发生改变时调用此方法，重新获取包名所在集合的数据
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			mPackageNameList = mDao.findAll();
		}
		
	}
	
	class InnerReceiver extends BroadcastReceiver{


		@Override
		public void onReceive(Context context, Intent intent) {
			//获取发送广播过程中传递过来的包名，跳过此包名的检测过程
			
			mSkipPackageName = intent.getStringExtra("packagename");
		}
		
	}
	
	/**
	 * 监测所点击的应用是否加了锁
	 */
	private void watch() {
		//1.开启子线程，在子线程中开启一个可控死循环
		new Thread(){

			public void run() {
				mPackageNameList = mDao.findAll();
				while (isWatch) {
					//2.监测现在正在开启的应用
					//3.获取ActivityManager对象
					ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					//4.获取正在开启应用的任务栈
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					RunningTaskInfo runningTaskInfo = runningTasks.get(0);
					//5.获取栈顶的activity，然后获取此activity所在应用得包名
					String packageName = runningTaskInfo.topActivity.getPackageName();
					//6.拿此包名和已加锁集合中的包名对比，如果含有此包名，则弹出拦截界面
					if (mPackageNameList.contains(packageName)) {
						//如果检测的程序已经解锁了，则不需要再弹出拦截界面了
						if (!packageName.equals(mSkipPackageName)) {
							//7.弹出拦截界面
							Intent intent = new Intent(getApplicationContext(),EnterPsdActivity.class);
							//开启一个新的任务栈
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packagename", packageName);
							startActivity(intent);
						}
					}
					//睡眠一下，时间片轮转
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//结束死循环
		isWatch = false;
		//注销广播接收者
		if (mInnerReceiver != null) {
			unregisterReceiver(mInnerReceiver);
		}
		//关闭内容观察者
		if (mContentObserver != null) {
			getContentResolver().unregisterContentObserver(mContentObserver);
		}
	}

}
