package com.itheima.mobilesafe74.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.SpUtil;

public class Setup4Activity extends BaseSetupActivity {

	private CheckBox cb_box;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);

		initUI();
	}

	private void initUI() {
		cb_box = (CheckBox) findViewById(R.id.cb_box);
		// 1.是否选中状态的回显
		boolean open_security = SpUtil.getBoolean(getApplicationContext(),
				ConstantValue.OPEN_SECURITY, false);
		// 2.根据状态，修改checkbox后续的文字显示
		cb_box.setChecked(open_security);
		if (open_security) {
			cb_box.setText("安全设置已开启");
		} else {
			cb_box.setText("安全设置已关闭");
		}
		// 3.点击过程中，监听选中状态发生改变的过程
		// cb_box.setChecked(!cb_box.isChecked());
		cb_box.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// 4.存储点击后的状态
				SpUtil.putBoolean(getApplicationContext(),
						ConstantValue.OPEN_SECURITY, isChecked);
				// 5.根据开关的状态，去修改checkbox的文字
				if (isChecked) {
					cb_box.setText("安全设置已开启");
				} else {
					cb_box.setText("安全设置已关闭");
				}
			}
		});

	}

	@Override
	public void showNextPage() {
		boolean open_security = SpUtil.getBoolean(getApplicationContext(),
				ConstantValue.OPEN_SECURITY, false);
		if (open_security) {
			Intent intent = new Intent(this, MobileSafeActivity.class);
			startActivity(intent);

			// 设置完成的标识存入Sp中
			SpUtil.putBoolean(this, ConstantValue.SETUP_OVER, true);

			finish();
			// 开启屏移动画
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		} else {
			Toast.makeText(getApplicationContext(), "请开启防盗保护",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void showPrePage() {
		Intent intent = new Intent(this, Setup3Activity.class);
		startActivity(intent);
		finish();
		// 开启屏移动画
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}
}
