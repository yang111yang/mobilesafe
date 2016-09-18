package com.itheima.mobilesafe74.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.itheima.mobilesafe74.R;

public class Setup1Activity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
		
	}
	
	//点击按钮，跳转到下一页
	public void nextPage(View v) {
		Intent intent = new Intent(this,Setup2Activity.class);
		startActivity(intent);
		//销毁当前页面
		finish();
	}
}
