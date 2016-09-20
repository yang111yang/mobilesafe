package com.itheima.mobilesafe74.activity;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.SpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SetupOverActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean setup_over = SpUtil.getBoolean(this, ConstantValue.SETUP_OVER, false);
		if (setup_over) {
			//密码输入成功，并且是个导航界面设置完成--------》停留在设置完成的功能列表界面
			setContentView(R.layout.activity_mobile_safe);
		}else{
			//密码输入成功，并且是个导航界面设置没有完成--------》停留在导航界面的第一个界面
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			finish();
		}
	}
	
	
	
}
