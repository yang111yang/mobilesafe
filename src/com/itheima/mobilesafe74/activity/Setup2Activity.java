package com.itheima.mobilesafe74.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.SpUtil;
import com.itheima.mobilesafe74.view.SettingItemView;

public class Setup2Activity extends Activity {
	
	private SettingItemView siv_sim_bound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		
		initUI();
	}
	
	private void initUI() {
		siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);
		//回显(读取已有的绑定状态用作显示)
		String sim_number = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");
		//判断序列卡号是否为空
		if (TextUtils.isEmpty(sim_number)) {
			siv_sim_bound.setCheck(false);
		}else{
			siv_sim_bound.setCheck(true);
		}
		
		siv_sim_bound.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//获取原有的状态
				boolean isChecked = siv_sim_bound.isChecked();
				//将原有的状态去反
				siv_sim_bound.setCheck(!isChecked);
				if (!isChecked) {
					TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					//获取sim卡的序列卡号
					String simSerialNumber = manager.getSimSerialNumber();
					//存储
					SpUtil.putString(getApplicationContext(), ConstantValue.SIM_NUMBER, simSerialNumber);
				}else{
					//不存储（将存储的sim卡的序列号删掉）
					SpUtil.remove(getApplicationContext(), ConstantValue.SIM_NUMBER);
				}
			}
		});
	}

	//点击按钮，跳转到上一页
	public void prePage(View view){
		Intent intent = new Intent(this,Setup1Activity.class);
		startActivity(intent);
		finish();
		
	}
	
	//点击按钮，跳转到下一页
	public void nextPage(View view){
		String sim_number = SpUtil.getString(getApplicationContext(), ConstantValue.SIM_NUMBER, "");
		if (!TextUtils.isEmpty(sim_number)) {
			
			Intent intent = new Intent(this,Setup3Activity.class);
			startActivity(intent);
			finish();
		}else{
			Toast.makeText(this, "请绑定sim卡", Toast.LENGTH_SHORT).show();
		}
	}
}
