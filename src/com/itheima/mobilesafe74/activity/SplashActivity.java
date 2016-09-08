package com.itheima.mobilesafe74.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.SpUtil;
import com.itheima.mobilesafe74.utils.StreamUtil;
import com.itheima.mobilesafe74.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
/**
 * Splash界面
 * @author 刘建阳
 * @date 2016-9-8 下午3:55:06
 */
public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity";

	/**
	 * 更新新版本的状态码
	 */
	protected static final int UPDATE_VERSION = 100;

	/**
	 * 进入应用程序主界面的状态码
	 */
	protected static final int ENTER_HOME = 101;

	/**
	 * url地址出错的状态码
	 */
	protected static final int URL_ERROR = 102;

	/**
	 * io异常的状态码
	 */
	protected static final int IO_ERROR = 103;

	/**
	 * json解析异常的状态码
	 */
	protected static final int JSON_ERROR = 104;

	private TextView tv_version_name;

	private String mVersionDes;

	private int mLocalVersionCode;
	
	private String mDownloadUrl;
	
	private RelativeLayout rl_root;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_VERSION:
				// 弹出对话框，提示用户更新
				showUpdateDialog();
				break;
			case ENTER_HOME:
				// 进入应用程序主界面，Activity跳转过程
				enterHome();
				break;
			case URL_ERROR:
				ToastUtil.show(SplashActivity.this, "url异常");
				enterHome();
				break;
			case IO_ERROR:
				ToastUtil.show(SplashActivity.this, "读取异常");
				enterHome();
				break;
			case JSON_ERROR:
				ToastUtil.show(SplashActivity.this, "json解析异常");
				enterHome();
				break;

			}

		};
	};


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
		//初始化动画
		initAinmation();
	}

	private void initAinmation() {
		AlphaAnimation alpha = new AlphaAnimation(0, 1);
		alpha.setDuration(3000);
		rl_root.startAnimation(alpha);
		
	}

	/**
	 * 弹出对话框，提示用户更新
	 */
	protected void showUpdateDialog() {
		// 对话框是依赖于activity存在的，需要绑定界面，getApplicationContext()所对应的的activity是未知的，所以必须要用this
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// 设置左上角的图标
		builder.setIcon(R.drawable.ic_launcher);
		// 设置标题
		builder.setTitle("版本更新");
		// 设置描述内容
		builder.setMessage(mVersionDes);
		
		// 积极按钮，立即更新
		builder.setPositiveButton("立即更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载apk
				downloadApk();
				dialog.dismiss();
			}
		});
		
		// 消极按钮，稍后再说
		builder.setNegativeButton("稍后再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 取消对话框，进入主界面
				enterHome();
				dialog.dismiss();
			}
		});
		
		//点击取消事件监听
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// 即使用户点击取消，也要跳转到主界面
				enterHome();
				dialog.dismiss();
			}
		});
		
		builder.show();
	}

	/**
	 * 下载apk的方法
	 */
	protected void downloadApk() {
		// apk下载链接地址，放置apk的所在路径
		// 1.判断sdcard是否可用，是否挂载上
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// 2.获取sd卡的路径
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator + "mobilesafe74.apk";
			//3.发送请求，获取apk，并放置到指定路径
			HttpUtils httpUtils = new HttpUtils();
			//4.发送请求，传递参数(下载地址，下载应用放置的位置)
			httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
				
				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					//下载成功
					Log.i(TAG, "下载成功");
					File file = responseInfo.result;
					//提示用户安装
					installApk(file);
				}
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					//下载失败
					Log.i(TAG, "下载失败");
				}
				
				//刚刚开始下载的方法
				@Override
				public void onStart() {
					Log.i(TAG, "刚刚开始下载");
					super.onStart();
				}
				
				//下载过程中的方法(下载apk的总大小，当前下载的位置，是否正在下载)
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					Log.i(TAG, "下载中...");
					Log.i(TAG, "total:"+total);
					Log.i(TAG, "current:"+current);
					super.onLoading(total, current, isUploading);
				}
			});
		}

	}

	/**
	 * 安装对应apk的方法
	 * @param file	安装文件
	 */
	protected void installApk(File file) {
		//系统应用界面，系统的源码，安装入口
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		/*//文件作为数据源
		intent.setData(Uri.fromFile(file));
		//设置安装的类型
		intent.setType("application/vnd.android.package-archive");*/
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		startActivityForResult(intent, 0);
		/* 
		 * 升级
		 * 注意事项：
		 * 1.要将原有应用覆盖，包名要一致  
		 * 2.签名要一致
		 * 从Eclipse运行到手机上的应用，使用的是bin目录下的应用，使用debug.keystore签名应用
		 * 手机卫士版本1，右键运行至手机，所以使用的签名文件是debug.keystore
		 * 手机卫士版本2，是单独打包，生成签名文件，所以签名文件是heima74keystore
		 * 生成一个heima74keystore为签名文件的apk
		 */
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 进入应用程序主界面，Activity跳转过程的方法
	 */
	protected void enterHome() {

		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);

		// 在开启一个新的界面后，将导航界面关闭(导航界面只可见一次)
		finish();
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
		if (SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false)) {
			checkedVersion();
		}else{
			//直接进入应用程序主界面
//			enterHome();
			//消息机制
//			mHandler.sendMessageDelayed(msg, 4000);
			//在发送消息4秒后，去处理ENTER_HOME状态吗指定的消息
			mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
		}
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
				// 发送请求获取数据，参数为请求json的链接地址
				// http://192.168.1.102:8080/update.json 测试阶段不是最优
				// 仅限于模拟器访问电脑tomcat
				Message msg = Message.obtain();
				long startTime = System.currentTimeMillis();
				try {
					// 1.封装url地址
					URL url = new URL("http://10.0.2.2:8080/update.json");
					// 2.开启一个链接
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					// 3.设置常见的请求参数(请求头)
					// 请求超时
					connection.setConnectTimeout(2000);
					// 读取超时
					connection.setReadTimeout(2000);
					// 默认就是get请求方式
					// connection.setRequestMethod("");
					// 4.获取响应码
					int responseCode = connection.getResponseCode();
					if (responseCode == 200) {
						// 5.以流的型式，把数据读取下来
						InputStream is = connection.getInputStream();
						// 6.将流转换成字符串
						String json = StreamUtil.stream2String(is);

						Log.i(TAG, json);
						// 7.json的解析
						JSONObject jsonObject = new JSONObject(json);
						String versionName = jsonObject
								.getString("versionName");
						mVersionDes = jsonObject.getString("versionDes");
						String versionCode = jsonObject
								.getString("versionCode");
						mDownloadUrl = jsonObject
								.getString("downloadUrl");

						Log.i(TAG, versionName);
						Log.i(TAG, mVersionDes);
						Log.i(TAG, versionCode);
						Log.i(TAG, mDownloadUrl);

						// 8.比对版本号(服务器版本号》本地版本号，提示更新)
						if (mLocalVersionCode < Integer.parseInt(versionCode)) {
							// 提示用户更新
							msg.what = UPDATE_VERSION;
						} else {
							// 进入主界面
							msg.what = ENTER_HOME;
						}

					}

				} catch (MalformedURLException e) {
					e.printStackTrace();
					msg.what = URL_ERROR;
				} catch (IOException e) {
					e.printStackTrace();
					msg.what = IO_ERROR;
				} catch (JSONException e) {
					e.printStackTrace();
					msg.what = JSON_ERROR;
				} finally {
					// 指定睡眠，请求网络的时长超过4秒则不做处理
					// 请求网络的时长小于4秒，强制让其睡眠满4秒钟
					long endTime = System.currentTimeMillis();
					long time = endTime - startTime;
					if (time < 4000) {
						try {
							Thread.sleep(4000 - time);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					mHandler.sendMessage(msg);
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
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
	}

}
