package com.itheima.mobilesafe74.activity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;

public class CacheClearActivity extends Activity {

	protected static final int UPDATE_CACHE_APP = 100;
	protected static final int CHECK_CACGE_APP = 101;
	protected static final int CHECK_FINISH = 102;
	protected static final int CLEAR_CACHE = 103;
	private Button btn_clear_now;
	private ProgressBar pb_bar;
	private TextView tv_app_name;
	private LinearLayout ll_add_text;
	private PackageManager mPm;
	private int index = 0;
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_CACHE_APP:
				//8.在线性布局中添加view
				View view = View.inflate(getApplicationContext(), R.layout.linearlayout_cache_item, null);
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
				TextView tv_memory = (TextView) view.findViewById(R.id.tv_memory);
				ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				
				final CacheInfo cacheInfo = (CacheInfo) msg.obj;
				iv_icon.setBackgroundDrawable(cacheInfo.icon);
				tv_name.setText(cacheInfo.name);
				tv_memory.setText(Formatter.formatFileSize(getApplicationContext(), cacheInfo.cacheSize));
				
				iv_delete.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.setData(Uri.parse("package:"+cacheInfo.packageName));
						startActivity(intent);
					}
				});
				
				ll_add_text.addView(view, 0);
				break;
			case CHECK_CACGE_APP:
				tv_app_name.setText((String) msg.obj);
				break;
			case CHECK_FINISH:
				tv_app_name.setText("扫描完成");
				break;
			case CLEAR_CACHE:
				ll_add_text.removeAllViews();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_clear);
		
		initUI();
		initData();
	}

	private void initData() {
		//获取应用的缓存大小
		
		new Thread(){

			public void run() {
				//1.获取包管理者对象
				mPm = getPackageManager();
				//2.获取手机上已安装的应用
				List<PackageInfo> installedPackages = mPm.getInstalledPackages(0);
				//3.给进度条设置最大值
				pb_bar.setMax(installedPackages.size());
				//4.遍历集合,获取有缓存的应用的相关信息(应用的图标，应用的名称，应用的缓存大小，应用的包名)
				for (PackageInfo packageInfo : installedPackages) {
					//包名作为查询应用缓存大小的条件
					String packageName = packageInfo.packageName;
					getPackageCache(packageName);
					
					try {
						Thread.sleep(100+new Random().nextInt(50));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					//进度条状态更新
					index++;
					pb_bar.setProgress(index);
					//扫描应用的名称显示,(消息机制)
					Message msg = Message.obtain();
                	msg.what = CHECK_CACGE_APP;
                	try {
						msg.obj = mPm.getApplicationInfo(packageName, 0).loadLabel(mPm).toString();
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
                	mHandler.sendMessage(msg);
				}
				Message msg = Message.obtain();
				msg.what = CHECK_FINISH;
				mHandler.sendMessage(msg);
			};
		}.start();
	}

	/**
	 * 通过包名获取此应用的缓存大小
	 * @param packageName 应用程序的包名
	 */
	protected void getPackageCache(String packageName) {
		//创建了一个IPackageStatsObserver.Stub子类的对象,并且实现了onGetStatsCompleted方法
	     IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
	        	
	            public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
	            	//4.获取指定包名的缓存大小
	                long cacheSize = stats.cacheSize;
	                //5.判断缓存大小是否大于0
	                if (cacheSize>0) {
						//6.告知主线程更新UI
	                	Message msg = Message.obtain();
	                	msg.what = UPDATE_CACHE_APP;
	                	//7.维护有缓存应用的javabean
	                	CacheInfo cacheInfo = null;
	                	try {
							cacheInfo = new CacheInfo();
							cacheInfo.cacheSize = cacheSize;
							cacheInfo.packageName = stats.packageName;
							cacheInfo.name = mPm.getApplicationInfo(stats.packageName, 0).loadLabel(mPm).toString();
							cacheInfo.icon = mPm.getApplicationInfo(stats.packageName, 0).loadIcon(mPm);
						} catch (NameNotFoundException e) {
							e.printStackTrace();
						}
	                	msg.obj = cacheInfo;
	                	mHandler.sendMessage(msg);
					}
	            }
	     };
	     
	     try {
			//1.获取指定类的字节码文件
			 Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			 //2.获取调用方法对象
			 Method method = clazz.getMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
			 //3.获取对象调用方法
			 method.invoke(mPm, packageName,mStatsObserver);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	     
	}
	
	class CacheInfo{
		public String name;
		public Drawable icon;
		public String packageName;
		public long cacheSize;
	}

	private void initUI() {
		btn_clear_now = (Button) findViewById(R.id.btn_clear_now);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		tv_app_name = (TextView) findViewById(R.id.tv_app_name);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
		
		btn_clear_now.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					//1.获取指定类的字节码文件
					 Class<?> clazz = Class.forName("android.content.pm.PackageManager");
					 //2.获取调用方法对象
					 Method method = clazz.getMethod("freeStorageAndNotify", long.class,IPackageDataObserver.class);
					 //3.获取对象调用方法
					 method.invoke(mPm, Long.MAX_VALUE,new IPackageDataObserver.Stub() {
						
						@Override
						public void onRemoveCompleted(String packageName, boolean succeeded)
								throws RemoteException {
							Message msg = Message.obtain();
							msg.what = CLEAR_CACHE;
							mHandler.sendMessage(msg);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		});
	}
	
	
}
