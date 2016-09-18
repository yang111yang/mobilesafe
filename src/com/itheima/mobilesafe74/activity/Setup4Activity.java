package com.itheima.mobilesafe74.activity;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.SpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Setup4Activity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
	}
	
	//点击按钮，跳转到上一页
	public void prePage(View view){
		Intent intent = new Intent(this,Setup3Activity.class);
		startActivity(intent);
		finish();
		
	}
	
	//点击按钮，跳转到下一页
	public void nextPage(View view){
		Intent intent = new Intent(this,SetupOverActivity.class);
		startActivity(intent);
		
		//设置完成的标识存入Sp中
		SpUtil.putBoolean(this, ConstantValue.SETUP_OVER, true);
		
		finish();
	}
}
