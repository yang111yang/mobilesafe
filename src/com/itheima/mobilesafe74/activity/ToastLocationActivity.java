package com.itheima.mobilesafe74.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;

import com.itheima.mobilesafe74.R;

public class ToastLocationActivity extends Activity {

	private ImageView iv_drag;
	private Button btn_top;
	private Button btn_bottom;

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
		//1.关联控件
	    iv_drag = (ImageView) findViewById(R.id.iv_drag);
	    btn_top = (Button) findViewById(R.id.btn_top);
	    btn_bottom = (Button) findViewById(R.id.btn_bottom);
	    
	    //2.给ImageView控件设置触摸事件
	    iv_drag.setOnTouchListener(new OnTouchListener() {
			
			private int startX;
			private int startY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					//控件起始位置的坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					//控件移动位置的坐标
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();
					
					//控件的偏移量
					int diyX = moveX - startX;
					int diyY = moveY - startY;
					
					//1.当前控件所在屏幕上的位置
					int left = iv_drag.getLeft() + diyX;
					int top = iv_drag.getTop() + diyY;
					int right = iv_drag.getRight() + diyX;
					int bottom = iv_drag.getBottom() + diyY;
					//2.告知当前控件，按给定的坐标去做提示
					iv_drag.layout(left, top, right, bottom);
					
					//3.重置一次控件坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					//4.容错处理
					
					
					break;
				case MotionEvent.ACTION_UP:
					
					break;

				}
				return true;
			}
		});
	}
	
}
