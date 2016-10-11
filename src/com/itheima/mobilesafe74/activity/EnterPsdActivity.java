package com.itheima.mobilesafe74.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.utils.ToastUtil;

public class EnterPsdActivity extends Activity {
	private String packageName;
	private TextView tv_app_name;
	private ImageView iv_app_icon;
	private EditText et_psd;
	private Button btn_app_submit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_psd);
		//获取包名
		packageName = getIntent().getStringExtra("packagename");
		
		initUI();
		initData();
	}

	private void initData() {
		//1.获取PackageManager对象
		PackageManager pm = getPackageManager();
		try {
			//2.获取包含应用信息的对象
			ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
			//3.获取应用名称，并设置到tv_app_name上
			tv_app_name.setText(applicationInfo.loadLabel(pm));
			//4.获取应用图标，并设置到iv_app_icon上
			iv_app_icon.setBackgroundDrawable(applicationInfo.loadIcon(pm));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		//设置按钮的点击事件
		btn_app_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String psd = et_psd.getText().toString();
				if (!TextUtils.isEmpty(psd)) {
					if (psd.equals("123")) {
						//解锁，进入应用,告知看门狗不要再去监听已经解锁的应用，发送广播
						Intent intent = new Intent("android.intent.action.SKIP");
						intent.putExtra("packagename", packageName);
						sendBroadcast(intent);
						finish();
					}else{
						//给提示
						ToastUtil.show(getApplicationContext(), "密码错误");
					}
				}else{
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}
			}
		});
	}

	private void initUI() {
		tv_app_name = (TextView) findViewById(R.id.tv_app_name);
		iv_app_icon = (ImageView) findViewById(R.id.iv_app_icon);
		et_psd = (EditText) findViewById(R.id.et_psd);
		btn_app_submit = (Button) findViewById(R.id.btn_app_submit);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//跳转到桌面
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}
	
}
