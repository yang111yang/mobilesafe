package com.itheima.mobilesafe74.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.utils.ConstantValue;

/**
 * 自定义控件SettingItemView，把设置中心的一个条目封装到SettingItemView中
 * @author 刘建阳
 * @date 2016-9-7 下午4:27:42
 */
public class SettingClickView extends RelativeLayout {

	
	private static final String tag = "SettingItemView";
	private TextView tv_des;
	private TextView tv_title;


	/*
	 * 把前两个构造方法的super换成this,并将其里面的参数和下一个保持一致，无论调哪一个构造方法，最终都会走第三个构造方法
	 * 第一个调第二个，第二个调第三个
	 */
	public SettingClickView(Context context) {
		this(context, null);
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// xml->view 将设置界面的一个条目转换成view对象
		// this:将View.inflate(上下文,)的到的view放到SettingItemView中
		View.inflate(context, R.layout.setting_click_view, this);

		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
		
	}
	
	/**
	 * @param title 设置标题的内容
	 */
	public void setTitle(String title){
		tv_title.setText(title);
	}

	/**
	 * @param des 设置的描述
	 */
	public void setDes(String des){
		tv_des.setText(des);
	}
	
	
}
