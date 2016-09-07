package com.itheima.mobilesafe74.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharePreference的工具类
 * @author 刘建阳
 * @date 2016-9-7下午10:09:10
 */
public class SpUtil {
	
	private static SharedPreferences sp;

	/**
	 * 写入boolean变量至sp中
	 * @param ctx	上下文环境
	 * @param key	存储节点的名称
	 * @param value 存储节点的值boolean
	 */
	public static void putBoolean(Context ctx, String key, boolean value){
		if (sp==null) {
			sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).commit();
	}
	//读
	/**
	 * 从sp中读取boolean标识的值
	 * @param ctx		上下文环境
	 * @param key		存储节点的名称
	 * @param defValue  没有此节点默认值
	 * @return			默认值或者此节点读取到的结果
	 */
	public static boolean getBoolean(Context ctx, String key, boolean defValue){
		if (sp==null) {
			sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getBoolean(key, defValue);
		
	}
	
}
