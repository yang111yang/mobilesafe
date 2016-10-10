package com.itheima.mobilesafe74.activity;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.service.LockScreenService;
import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.ServiceUtil;
import com.itheima.mobilesafe74.utils.SpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProcessSettingActivity extends Activity {

	private CheckBox cb_show_system;
	private CheckBox cb_lock_clear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);
		
		initSystemShow();
		
		initLockClear();
		
	}

	/**
	 * 锁屏清理
	 */
	private void initLockClear() {
		cb_lock_clear = (CheckBox) findViewById(R.id.cb_lock_clear);
		
		//根据锁屏清理服务是否开启来回显checkbox的状态
		boolean isRunning = ServiceUtil.isRunning(getApplicationContext(), "com.itheima.mobilesafe74.service.LockScreenService");
		if (isRunning) {
			cb_lock_clear.setText("锁屏清理已开启");
		} else {
			cb_lock_clear.setText("锁屏清理已关闭");
		}
		//cb_lock_clear选中状态的维护
		cb_lock_clear.setChecked(isRunning);
		
		//对选中状态进行监听
		cb_lock_clear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_lock_clear.setText("锁屏清理已开启");
					//开启服务
					startService(new Intent(getApplicationContext(),LockScreenService.class));
				}else{
					cb_lock_clear.setText("锁屏清理已关闭");
					//关闭服务
					stopService(new Intent(getApplicationContext(),LockScreenService.class));
				}
			}
		});
	}

	/**
	 * 隐藏系统进程
	 */
	private void initSystemShow() {
		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		//对之前存储过的状态进行回显
		boolean showSystem = SpUtil.getBoolean(ProcessSettingActivity.this, ConstantValue.SHOW_SYSTEM, false);
		if (showSystem) {
			cb_show_system.setText("显示系统进程");
		}else{
			cb_show_system.setText("隐藏系统进程");
		}
		
		//单选框状态的维护
		cb_show_system.setChecked(showSystem);
		
		//对选中状态进行监听
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					cb_show_system.setText("显示系统进程");
				}else{
					cb_show_system.setText("隐藏系统进程");
				}
				SpUtil.putBoolean(ProcessSettingActivity.this, ConstantValue.SHOW_SYSTEM, isChecked);
			}
		});
	}
	
}
