package com.itheima.mobilesafe74.activity;

import com.itheima.mobilesafe74.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost.TabSpec;

public class BaseCacheClearActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_clear_cache);
		
		//1.生成选项卡1
		TabSpec tab1 = getTabHost().newTabSpec("clear_cache").setIndicator("缓存清理");
		//2.生成选项卡2
		TabSpec tab2 = getTabHost().newTabSpec("sd_clear_cache").setIndicator("sd卡清理");
		//3.告知选项卡得后续操作
		tab1.setContent(new Intent(this,CacheClearActivity.class));
		tab2.setContent(new Intent(this,SdCacheClearActivity.class));
		
		//4.将此两个选项卡维护到host(选项卡宿主)中去
		getTabHost().addTab(tab1);
		getTabHost().addTab(tab2);
		
	}
	
	
}
