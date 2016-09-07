package com.itheima.mobilesafe74.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.SpUtil;
import com.itheima.mobilesafe74.view.SettingItemView;

public class SettingActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		initUpdate();
	}

	private void initUpdate() {
		final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
		
		//获取已有的开关状态，作为显示
		boolean open_update = SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
		//是否选中，根据上一次存储的结果去做决定
		siv_update.setCheck(open_update);
		
		siv_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//如果之前是选中，点击之后，变成未选中
				//如果之前是未选中，点击之后，变成选中
				
				//获取之前的选中状态
				boolean check = siv_update.isChecked();
				//将原有状态取反
				siv_update.setCheck(!check);
				//将取反后的结果存储到相应的sp中
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, !check);
				
			}
		});
	}
	
	
}
