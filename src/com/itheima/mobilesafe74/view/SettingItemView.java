package com.itheima.mobilesafe74.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;

/**
 * 自定义控件SettingItemView，把设置中心的一个条目封装到SettingItemView中
 * @author 刘建阳
 * @date 2016-9-7 下午4:27:42
 */
public class SettingItemView extends RelativeLayout {

	private CheckBox cb_box;
	private TextView tv_des;


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
		tv_des = (TextView) findViewById(R.id.tv_des);
		cb_box = (CheckBox) findViewById(R.id.cb_box);
		
		//获取自定义及原生属性的操作，写在此处，AttributeSet attrs对象中获取
		initAttrs(attrs);
	}

	
	/**
	 * 返回属性集合中自定义属性的属性值
	 * @param attrs	构造方法中维护好的属性集合
	 */
	private void initAttrs(AttributeSet attrs) {
		
	}

	/**
	 * 判断是否开启的方法
	 * @return 返回当前SettingItemView是否选中状态	true开启(checkBox返回true)	false关闭(checkBox返回false)
	 */
	public boolean isChecked(){
		//由checkbox的选中结果来决定当前条目是否开启
		return cb_box.isChecked();
	}
	
	public void setCheck(boolean isChecked){
		//当前条目在选择的过程中，cb_box选中状态也随着(isChecked)变化
		cb_box.setChecked(isChecked);
		if (isChecked) {
			tv_des.setText("自动更新已开启");
		}else{
			tv_des.setText("自动更新已关闭");
			
		}
	}
	
}
