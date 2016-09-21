package com.itheima.mobilesafe74.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.engine.AddressDao;

public class QueryAddressActivity extends Activity {
	
	
	private EditText et_phone;
	private Button btn_query;
	private TextView tv_query_result;
	private String mAddress;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			tv_query_result.setText(mAddress);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_address);
		
		initUI();
		
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		//关联空间
		et_phone = (EditText) findViewById(R.id.et_phone);
		btn_query = (Button) findViewById(R.id.btn_query);
		tv_query_result = (TextView) findViewById(R.id.tv_query_result);
		
		//1.点击查询功能
		btn_query.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String phone = et_phone.getText().toString().trim();
				if (!TextUtils.isEmpty(phone)) {
					//2.查询操作是耗时操作，需要在子线程中进行
					query(phone);
				}else{
					//抖动
					//interpolator插补器，数字函数
					Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
					et_phone.startAnimation(shake);
					
					//手机震动效果（vibrator震动）
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					//震动的毫秒值
					vibrator.vibrate(2000);
				}
			}
		});
		
		//5.实时查询(监听输入框文本的变化)
		et_phone.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当文本发生改变时调用的方法
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				//当文本发生改变之前调用的方法
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				//当文本发生改变之后调用的方法
				String phone = et_phone.getText().toString().trim();
				query(phone);
			}
		});
		
	}
	
	/**
	 * 查询号码归属地
	 * 耗时操作
	 * @param phone 电话号码
	 */
	protected void query(final String phone) {
		new Thread(){
			
			public void run() {
				mAddress = AddressDao.getAddress(phone);
				
				//3.消息机制，告知主线程查询结束，可以使用查询结果了
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}
}
