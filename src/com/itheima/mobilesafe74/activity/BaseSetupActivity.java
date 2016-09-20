package com.itheima.mobilesafe74.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {

	private GestureDetector gestureDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 3.创建手势识别器的对象
		gestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// e1 起始点
				// e2 抬起点
				if (e1.getRawX() - e2.getRawX() > 100) {
					// 下一页，从右向左滑动
					showNextPage();
				}
				if (e2.getRawX() - e1.getRawX() > 100) {
					// 上一页，从左向右滑动
					showPrePage();
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}

	// 1.监听当前Activity上的触摸事件(按下(1次)、滑动(多次)、抬起(1次))
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 2.通过手势识别器去识别不同的事件类型，做逻辑
		gestureDetector.onTouchEvent(event);// 将Activity中的手机移动操作交由手势识别器处理
		return super.onTouchEvent(event);
	}
	
	//抽象方法，定义跳转到下一页的方法
	public abstract void showNextPage();
	
	//抽象方法，定义跳转到上一页的方法
	public abstract void showPrePage();
	
	//统一处理每个页面中的上一页按钮
	public void nextPage(View view){
		//将跳转到下一页的代码逻辑，交由子类处理
		showNextPage();
	}
	//统一处理每个页面中的上一页按钮
	public void prePage(View view){
		//将跳转到上一页的代码逻辑，交由子类处理
		showPrePage();
	}
	
}
