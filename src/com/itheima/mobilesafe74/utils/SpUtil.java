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
	
	/**
	 * @param ctx	上下文环境
	 * @param key	存储节点的名称
	 * @param value 存储节点的值String
	 */
	public static void putString(Context ctx, String key, String value){
		if (sp==null) {
			sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}
	
	/**
	 * 从sp中读取String标识的值
	 * @param ctx		上下文环境
	 * @param key		存储节点的名称
	 * @param defValue  没有此节点默认值
	 * @return			默认值或者此节点读取到的结果
	 */
	public static String getString(Context ctx, String key, String defValue){
		if (sp==null) {
			sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getString(key, defValue);
		
	}
	
	/**
	 * @param ctx	上下文环境
	 * @param key	存储节点的名称
	 * @param value 存储节点的值int
	 */
	public static void putInt(Context ctx, String key, int value){
		if (sp==null) {
			sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putInt(key, value).commit();
	}
	
	/**
	 * 从sp中读取int标识的值
	 * @param ctx		上下文环境
	 * @param key		存储节点的名称
	 * @param defValue  没有此节点默认值
	 * @return			默认值或者此节点读取到的结果
	 */
	public static int getInt(Context ctx, String key, int defValue){
		if (sp==null) {
			sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getInt(key, defValue);
		
	}
	
	/**
	 * 从sp中移除指定节点
	 * @param ctx 上下文环境
	 * @param key sim卡的序列卡号
	 */
	public static void remove(Context ctx, String key) {
		if (sp==null) {
			sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().remove(key).commit();
	}
	
	
	
	
}
