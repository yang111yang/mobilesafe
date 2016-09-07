package com.itheima.mobilesafe74.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * 能够获取焦点的自定义TextView
 * @author 刘建阳
 * @date 2016-9-7 下午3:21:30
 */
public class FocusTextView extends TextView {

	//在Java代码中创建控件
	public FocusTextView(Context context) {
		super(context);
	}

	//由系统调用(带属性+上下文环境)
	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	//由系统调用(带属性+上下文环境+布局文件中定义的样式文件的构造方法)
	public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	//重写该方法获取焦点，由系统调用，调用的时候控件获取了焦点
	@Override
	public boolean isFocused() {
		return true;
	}

}
