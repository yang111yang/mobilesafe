package com.itheima.mobilesafe74.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.itheima.mobilesafe74.R;

public class Setup3Activity extends Activity {

	private EditText et_phone_number;
	private Button btn_select_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		
		initUI();
	}

	private void initUI() {
		//显示电话号码的输入框
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		//点击选择联系人的对话框
		btn_select_number = (Button) findViewById(R.id.btn_select_number);
		btn_select_number.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),ContactListActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//返回到当前界面当中要接收的结果的方法
		super.onActivityResult(requestCode, resultCode, data);
	}


	//点击按钮，跳转到上一页
	public void prePage(View view){
		Intent intent = new Intent(this,Setup2Activity.class);
		startActivity(intent);
		finish();
		
	}
	
	//点击按钮，跳转到下一页
	public void nextPage(View view){
		Intent intent = new Intent(this,Setup4Activity.class);
		startActivity(intent);
		finish();
	}
}
