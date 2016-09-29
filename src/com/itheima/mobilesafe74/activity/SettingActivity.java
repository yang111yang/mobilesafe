package com.itheima.mobilesafe74.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.service.AddressService;
import com.itheima.mobilesafe74.service.BlackNumberService;
import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.ServiceUtil;
import com.itheima.mobilesafe74.utils.SpUtil;
import com.itheima.mobilesafe74.view.SettingClickView;
import com.itheima.mobilesafe74.view.SettingItemView;

public class SettingActivity extends Activity {

	private String[] mToastStyleDes;
	private SettingClickView scv_toast_style;
	private int mToastStyle;
	private SettingClickView scv_toast_location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		initUpdate();
		initAddress();
		initToastStyle();
		initToastLocation();
		initBlackNumber();
	}

	/**
	 * 黑名单管理的开启与关闭
	 */
	private void initBlackNumber() {
		final SettingItemView siv_blacknumber = (SettingItemView) findViewById(R.id.siv_blacknumber);
		boolean isRunning = ServiceUtil.isRunning(this, "com.itheima.mobilesafe74.service.BlackNumberService");
		siv_blacknumber.setCheck(isRunning);
		siv_blacknumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isChecked = siv_blacknumber.isChecked();
				siv_blacknumber.setCheck(!isChecked);
				if (!isChecked) {
					//开启服务
					startService(new Intent(getApplicationContext(),BlackNumberService.class));
				}else{
					//关闭服务
					stopService(new Intent(getApplicationContext(),BlackNumberService.class));
				}
			}
		});
	}

	/**
	 * 双击居中view所在屏幕位置的处理方法
	 */
	private void initToastLocation() {
		//关联控件
		scv_toast_location = (SettingClickView) findViewById(R.id.scv_toast_location);
		scv_toast_location.setTitle("归属地提示框的位置");
		scv_toast_location.setDes("设置归属地提示框的位置");
		scv_toast_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),ToastLocationActivity.class));
			}
		});
	}

	/**
	 * 吐司风格设置
	 */
	private void initToastStyle() {
		scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
		scv_toast_style.setTitle("设置归属地显示风格");
		// 创建描述文字所在的string数组
		mToastStyleDes = new String[] { "透明", "橙色", "蓝色", "灰色", "绿色" };
		// 通过sp，获取吐司显示样式的索引值，用于获取描述文字
		mToastStyle = SpUtil.getInt(this, ConstantValue.TOAST_STYLE, 0);
		// 3.通过获取字符串数组中的文字显示给描述内容的空间上
		scv_toast_style.setDes(mToastStyleDes[mToastStyle]);
		// 4.监听事件，弹出对话框
		scv_toast_style.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 5.显示吐司样式的对话框
				showToastStyleDialog();
			}
		});
	}

	/**
	 * 显示吐司样式的对话框
	 */
	protected void showToastStyleDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("请选择归属地样式");
		builder.setSingleChoiceItems(mToastStyleDes, mToastStyle,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
						dialog.dismiss();
						scv_toast_style.setDes(mToastStyleDes[which]);
					}
				});
		
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	/**
	 * 是否显示号码归属地的开关
	 */
	private void initAddress() {
		final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);

		// 对服务是否开启，作为显示
		boolean isRunning = ServiceUtil.isRunning(this,
				"com.itheima.mobilesafe74.service.AddressService");
		// 是否选中，根据上一次存储的结果去做决定
		siv_address.setCheck(isRunning);

		siv_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 如果之前是选中，点击之后，变成未选中
				// 如果之前是未选中，点击之后，变成选中

				// 获取之前的选中状态
				boolean check = siv_address.isChecked();
				// 将原有状态取反
				siv_address.setCheck(!check);

				if (!check) {
					// 开启服务，管理吐司
					startService(new Intent(getApplicationContext(),
							AddressService.class));
				} else {
					// 关闭服务
					stopService(new Intent(getApplicationContext(),
							AddressService.class));
				}

			}
		});
	}

	/**
	 * 版本更新开关
	 */
	private void initUpdate() {
		final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);

		// 获取已有的开关状态，作为显示
		boolean open_update = SpUtil.getBoolean(this,
				ConstantValue.OPEN_UPDATE, false);
		// 是否选中，根据上一次存储的结果去做决定
		siv_update.setCheck(open_update);

		siv_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 如果之前是选中，点击之后，变成未选中
				// 如果之前是未选中，点击之后，变成选中

				// 获取之前的选中状态
				boolean check = siv_update.isChecked();
				// 将原有状态取反
				siv_update.setCheck(!check);
				// 将取反后的结果存储到相应的sp中
				SpUtil.putBoolean(getApplicationContext(),
						ConstantValue.OPEN_UPDATE, !check);

			}
		});
	}

}
