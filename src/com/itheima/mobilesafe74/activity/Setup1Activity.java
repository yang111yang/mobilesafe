package com.itheima.mobilesafe74.activity;

import android.content.Intent;
import android.os.Bundle;

import com.itheima.mobilesafe74.R;

public class Setup1Activity extends BaseSetupActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
		
	}

	@Override
	public void showNextPage() {
		Intent intent = new Intent(this,Setup2Activity.class);
		startActivity(intent);
		//销毁当前页面
		finish();
		//开启屏移动画
		overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		
	}

	@Override
	public void showPrePage() {
		
	}
	
}
