package com.itheima.mobilesafe74.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.utils.StreamUtils;

public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity";
	private TextView tv_version_name;
	private int mLocalVersionCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除掉当前Activity的头
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		// 初始化UI
		initUI();
		// 初始化数据
		initData();
	}

	/**
	 * 初始化数据的方法
	 */
	private void initData() {
		// 1.获取应用版本名称
		tv_version_name.setText("版本名称：" + getVersionName());
		// 检测(本地版本号和服务器版本号比对)是否有更新，如果有更新，提示用户下载(member)
		// 2.获取本地版本号
		mLocalVersionCode = getVersionCode();
		// 3.获取服务端版本号(客户端发请求，服务端给相应(json,xml))，返回200代表请求成功，以流的方式读取出来
		/*
		 * json中包含的内容： 更新版本的版本名称 新版本的描述 服务器的版本号 新版本apk的下载地址
		 */
		checkedVersion();
	}

	/**
	 * 检测版本号
	 */
	private void checkedVersion() {
		/*
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() {
		 * 
		 * } });
		 */
		new Thread() {
			public void run() {
				//发送请求获取数据，参数为请求json的链接地址
				//http://192.168.1.102:8080/update.json	测试阶段不是最优
				//仅限于模拟器访问电脑tomcat
				try {
					//1.封装url地址
					URL url = new URL("http://10.0.2.2:8080/update.json");
					//2.开启一个链接
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					//3.设置常见的请求参数(请求头)
					//请求超时
					connection.setConnectTimeout(2000);
					//读取超时
					connection.setReadTimeout(2000);
					//默认就是get请求方式
//					connection.setRequestMethod("");
					//4.获取响应码
					int responseCode = connection.getResponseCode();
					if (responseCode==200) {
						//5.以流的型式，把数据读取下来
						InputStream is = connection.getInputStream();
						//6.将流转换成字符串
						String json = StreamUtils.stream2String(is);
						Log.i(TAG, json);
					}
					
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	/**
	 * 获取应用版本号
	 * 
	 * @return 非0代表获取成功
	 */
	private int getVersionCode() {
		// 1.包管理者对象PackageManager
		PackageManager pm = getPackageManager();
		// 2.从包管理者对象中获取指定包名的基本信息(版本名称，版本号)，传0代表获取基本信息
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			// 3.获取版本名称
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取应用版本名称：从清单文件中获取
	 * 
	 * @return 应用版本名称 返回null代表异常
	 */
	private String getVersionName() {
		// 1.包管理者对象PackageManager
		PackageManager pm = getPackageManager();
		// 2.从包管理者对象中获取指定包名的基本信息(版本名称，版本号)，传0代表获取基本信息
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			// 3.获取版本名称
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 初始化UI的方法 注释的快捷键：alt+shift+j
	 */
	private void initUI() {
		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
	}

}
