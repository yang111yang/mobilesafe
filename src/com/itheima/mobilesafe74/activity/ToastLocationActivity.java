package com.itheima.mobilesafe74.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.SpUtil;

public class ToastLocationActivity extends Activity {

	private ImageView iv_drag;

	private Button btn_top;

	private Button btn_bottom;

	private WindowManager mWM;

	private int mScreenWidth;

	private int mScreenHeight;

	private long[] mHits = new long[2];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toast_location);

		initUI();

	}

	/**
	 * 初始化UI的方法
	 */
	private void initUI() {
		// 1.关联控件
		iv_drag = (ImageView) findViewById(R.id.iv_drag);
		btn_top = (Button) findViewById(R.id.btn_top);
		btn_bottom = (Button) findViewById(R.id.btn_bottom);

		// 获取WindowManager对象
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		// 获取屏幕的宽和高
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		mScreenHeight = mWM.getDefaultDisplay().getHeight();

		// 点击设置归属地提示框条目后，跳转到新的Activity回显ImageView的位置
		int locationX = SpUtil.getInt(getApplicationContext(),
				ConstantValue.LOCATION_X, 0);
		int locationY = SpUtil.getInt(getApplicationContext(),
				ConstantValue.LOCATION_Y, 0);

		// 因为ImageView控件的父控件是RelativeLayout，所以设置规则必须是RelativeLayout的
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		// 将左上角的坐标作用在iv_drag对应规则参数上
		layoutParams.leftMargin = locationX;
		layoutParams.topMargin = locationY;
		// 将以上规则作用在iv_drag上
		iv_drag.setLayoutParams(layoutParams);

		if (locationY > mScreenHeight / 2) {
			btn_top.setVisibility(View.VISIBLE);
			btn_bottom.setVisibility(View.INVISIBLE);
		} else {
			btn_top.setVisibility(View.INVISIBLE);
			btn_bottom.setVisibility(View.VISIBLE);
		}

		iv_drag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[mHits.length - 1] - mHits[0] < 500) {
					// 满足双击事件后调用的代码
					int left = mScreenWidth / 2 - iv_drag.getWidth() / 2;
					int right = mScreenWidth / 2 + iv_drag.getWidth() / 2;
					int top = mScreenHeight / 2 - iv_drag.getHeight() / 2;
					int bottom = mScreenHeight / 2 + iv_drag.getHeight() / 2;
					
					iv_drag.layout(left, top, right, bottom);
					//存储iv_drag的位置
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_X, iv_drag.getLeft());
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_Y, iv_drag.getTop());
				}
			}
		});

		// 2.给ImageView控件设置触摸事件
		iv_drag.setOnTouchListener(new OnTouchListener() {

			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 控件起始位置的坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					// 控件移动位置的坐标
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();

					// 控件的偏移量
					int diyX = moveX - startX;
					int diyY = moveY - startY;

					// 1.当前控件所在屏幕上的位置
					int left = iv_drag.getLeft() + diyX;
					int top = iv_drag.getTop() + diyY;
					int right = iv_drag.getRight() + diyX;
					int bottom = iv_drag.getBottom() + diyY;

					// 容错处理
					// 边缘不能超出屏幕可显示范围
					if (left < 0 || right > mScreenWidth || top < 0
							|| bottom > mScreenHeight - 22) {
						return true;
					}

					if (bottom > mScreenHeight / 2) {
						btn_top.setVisibility(View.VISIBLE);
						btn_bottom.setVisibility(View.INVISIBLE);
					} else {
						btn_top.setVisibility(View.INVISIBLE);
						btn_bottom.setVisibility(View.VISIBLE);
					}

					// 2.告知当前控件，按给定的坐标去做提示
					iv_drag.layout(left, top, right, bottom);

					// 3.重置一次控件坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:
					// 记录控件移动之后的位置
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_X, iv_drag.getLeft());
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_Y, iv_drag.getTop());
					break;

				}
				//如果既要响应点击事件，又要响应拖拽事件，就要把返回值结果改为false
				return false;
			}
		});
	}

}
