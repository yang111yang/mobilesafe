package com.itheima.mobilesafe74.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.SpUtil;

public class Setup3Activity extends BaseSetupActivity {

	private EditText et_phone_number;
	private Button btn_select_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);

		initUI();
	}

	private void initUI() {
		// 显示电话号码的输入框
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		// 获取联系人电话号码回显过程
		String contact_phone = SpUtil.getString(getApplicationContext(),
				ConstantValue.CONTACT_PHONE, "");
		et_phone_number.setText(contact_phone);
		// 点击选择联系人的对话框
		btn_select_number = (Button) findViewById(R.id.btn_select_number);
		btn_select_number.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						ContactListActivity.class);
				startActivityForResult(intent, 0);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			// 1.返回到当前界面当中要接收的结果的方法
			String phone = data.getStringExtra("phone");
			// 2.将特殊字符(-,空格)进行过滤
			phone = phone.replace("-", "").replace(" ", "").trim();
			et_phone_number.setText(phone);
			super.onActivityResult(requestCode, resultCode, data);
			// 3.存储联系人
			SpUtil.putString(getApplicationContext(),
					ConstantValue.CONTACT_PHONE, phone);
		}
	}

	@Override
	public void showNextPage() {
		String phone = et_phone_number.getText().toString().trim();
		// 在sp中存储了相关联系人以后才可以跳转到下一页
		// String contact_phone = SpUtil.getString(getApplicationContext(),
		// ConstantValue.CONTACT_PHONE, "");
		if (!TextUtils.isEmpty(phone)) {
			Intent intent = new Intent(this, Setup4Activity.class);
			startActivity(intent);
			finish();

			// 如果是输入的电话号码，则需要保存
			SpUtil.putString(getApplicationContext(),
					ConstantValue.CONTACT_PHONE, phone);
			// 开启屏移动画
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else {
			Toast.makeText(getApplicationContext(), "请输入电话号码",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void showPrePage() {
		Intent intent = new Intent(this, Setup2Activity.class);
		startActivity(intent);
		finish();
		// 开启屏移动画
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}
}
