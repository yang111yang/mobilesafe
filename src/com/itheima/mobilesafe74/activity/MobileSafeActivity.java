package com.itheima.mobilesafe74.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.SpUtil;

public class MobileSafeActivity extends Activity {
	
	private TextView tv_safe_number;
	private TextView tv_reset_setup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean setup_over = SpUtil.getBoolean(this, ConstantValue.SETUP_OVER, false);
		if (setup_over) {
			//密码输入成功，并且是个导航界面设置完成--------》停留在设置完成的功能列表界面
			setContentView(R.layout.activity_mobile_safe);
			initUI();
		}else{
			//密码输入成功，并且是个导航界面设置没有完成--------》停留在导航界面的第一个界面
			Intent intent = new Intent(this, Setup1Activity.class);
			startActivity(intent);
			finish();
		}
	}

	private void initUI() {
		//从sp中获取联系人电话，显示到TextView中
		tv_safe_number = (TextView) findViewById(R.id.tv_safe_number);
		String phone = SpUtil.getString(this, ConstantValue.CONTACT_PHONE, "");
		tv_safe_number.setText(phone);
		
		//让textview具有可点击的效果,设置一个点击事件
		tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);
		tv_reset_setup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
	
	
}
