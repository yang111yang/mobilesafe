package com.itheima.mobilesafe74.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;

/**
 * 
 * @author 刘建阳
 * @date 2016-9-7 下午4:27:42
 */
public class SettingItemView extends RelativeLayout {

	/*
	 * 把前两个构造方法的super换成this,并将其里面的参数和下一个保持一致，无论调哪一个构造方法，最终都会走第三个构造方法
	 * 第一个调第二个，第二个调第三个
	 */
	public SettingItemView(Context context) {
		this(context, null);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// xml->view 将设置界面的一个条目转换成view对象
		// this:将View.inflate(上下文,)的到的view放到SettingItemView中
		View.inflate(context, R.layout.setting_item_view, this);
		/*
		 * View view = View.inflate(context, R.layout.setting_item_view, null);
		 * this.addView(view);
		 */

		// 关联控件
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		TextView tv_des = (TextView) findViewById(R.id.tv_des);
		CheckBox cb_box = (CheckBox) findViewById(R.id.cb_box);
	}

}
