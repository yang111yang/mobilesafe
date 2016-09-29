package com.itheima.mobilesafe74.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;

public class AToolActivity extends Activity {
	private TextView tv_query_phone_address,tv_sms_backup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);
		
		//电话归属地查询的方法
		initPhoneAddress();
		//短信备份的方法
		initSmsBackUp();
		
	}

	/**
	 * 短信备份的方法
	 */
	private void initSmsBackUp() {
		tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
		tv_sms_backup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showSmsBackUpDialog();
			}
		});
	}
	
	/**
	 * 弹出进度条对话框
	 */
	protected void showSmsBackUpDialog() {
		//1.创建进度条对话框
		ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setTitle("短信备份");
		//2.指定进度条的样式为水平
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//3.展示进度条对话框
		progressDialog.show();
		//4.短信的获取
		
	}

	/**
	 * 电话归属地查询的方法
	 */
	private void initPhoneAddress() {
		tv_query_phone_address = (TextView) findViewById(R.id.tv_query_phone_address);
		tv_query_phone_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),QueryAddressActivity.class));
			}
		});
	}
}
