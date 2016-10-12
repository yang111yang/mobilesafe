package com.itheima.mobilesafe74.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.engine.VirusDao;
import com.itheima.mobilesafe74.utils.Md5Util;

public class AnitVirusActivity extends Activity {
	protected static final int SCANNING = 100;
	protected static final int SCAN_FINISH = 101;
	
	protected static final String tag = "AnitVirusActivity";
	
	private ImageView iv_scanning;
	
	private TextView tv_name;
	
	private ProgressBar pb_bar;
	
	private LinearLayout ll_add_text;
	
	private int index = 0;
	
	private RotateAnimation mRotateAnimation;
	
	private List<ScanInfo> mVirusScanInfoList;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANNING:// 正在扫描
				// 1.显示正在扫描应用的名称
				ScanInfo scanInfo = (ScanInfo) msg.obj;
				tv_name.setText(scanInfo.name);
				// 2.向线性布局中添加一个TextView
				TextView textView = new TextView(getApplicationContext());
				if (scanInfo.isVirus) {
					// 是病毒
					textView.setText("发现病毒：" + scanInfo.name);
					textView.setTextColor(Color.RED);
				} else {
					// 不是病毒
					textView.setText("扫描安全：" + scanInfo.name);
					textView.setTextColor(Color.BLACK);
				}
				ll_add_text.addView(textView, 0);
				break;
			case SCAN_FINISH:// 扫描完成
				tv_name.setText("扫描完成");
				// 关闭动画
				iv_scanning.clearAnimation();
				// 卸载病毒
				unInstallVirus();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anit_virus);

		initUI();
		initAnimation();
		checkVirus();
	}

	/**
	 * 病毒卸载
	 */
	protected void unInstallVirus() {
		for (ScanInfo scanInfo : mVirusScanInfoList) {
			String packageName = scanInfo.packageName;
			Intent intent = new Intent("android.intent.action.DELETE");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:" + packageName));
			startActivity(intent);
		}

	}

	@SuppressWarnings("deprecation")
	private void checkVirus() {
		new Thread() {

			public void run() {
				// 获取数据库中的所有病毒的签名文件的MD5码的集合
				List<String> virusList = VirusDao.getVirusList();
				// 获取手机中所有应用的签名的MD5码的
				// 1.获取包管理者对象
				PackageManager pm = getPackageManager();
				// 2.获取所有应用程序的签名文件
				List<PackageInfo> packageInfoList = pm
						.getInstalledPackages(PackageManager.GET_SIGNATURES
								+ PackageManager.GET_UNINSTALLED_PACKAGES);
				// 创建一个记录病毒的集合
				mVirusScanInfoList = new ArrayList<ScanInfo>();
				// 记录所有应用的集合
				List<ScanInfo> scanInfoList = new ArrayList<ScanInfo>();
				// 设置进度条的最大值
				pb_bar.setMax(packageInfoList.size());
				// 3.遍历签名文件所在的集合
				for (PackageInfo packageInfo : packageInfoList) {
					ScanInfo scanInfo = new ScanInfo();
					// 获取签名文件的数组
					Signature[] signatures = packageInfo.signatures;
					// 获取签名文件数组的第一位，然后进行MD5加密，然后与数据库中的MD5码进行对比
					String signature = signatures[0].toCharsString();
					String encoder = Md5Util.encoder(signature);
					scanInfo.packageName = packageInfo.packageName;
					// 4.对比
					if (virusList.contains(encoder)) {
						// 5.记录病毒
						scanInfo.isVirus = true;
						mVirusScanInfoList.add(scanInfo);
					} else {
						scanInfo.isVirus = false;

					}
					// 6.维护扫描应用的包名，以及应用名称
					scanInfo.name = packageInfo.applicationInfo.loadLabel(pm)
							.toString();
					scanInfoList.add(scanInfo);
					Log.i(tag, encoder + scanInfo.name);

					// 7.在扫描的过程中，更新进度条
					index++;
					pb_bar.setProgress(index);

					// 由于扫描过程非常迅速，所以要睡一下
					try {
						Thread.sleep(50 + new Random().nextInt(100));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// 8.消息机制，更新UI(1.顶部扫描应用的名称，2.扫描过程中向线性布局中添加TextView)
					Message msg = Message.obtain();
					msg.what = SCANNING;
					msg.obj = scanInfo;
					mHandler.sendMessage(msg);
				}
				Message msg = new Message();
				msg.what = SCAN_FINISH;
				mHandler.sendMessage(msg);

			};
		}.start();
	}

	class ScanInfo {
		public boolean isVirus;
		public String packageName;
		public String name;
	}

	private void initAnimation() {
		mRotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateAnimation.setDuration(1000);
		// 设置动画重复的模式，无限循环
		mRotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
		mRotateAnimation.setFillAfter(true);
		iv_scanning.startAnimation(mRotateAnimation);
	}

	private void initUI() {
		iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
		tv_name = (TextView) findViewById(R.id.tv_name);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
	}
}
