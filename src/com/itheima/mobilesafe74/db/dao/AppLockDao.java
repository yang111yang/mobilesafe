package com.itheima.mobilesafe74.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.itheima.mobilesafe74.db.AppLockOpenHelper;
/**
 * 数据库的增删改查
 * @author 刘建阳
 * @date 2016-9-28 上午10:39:13
 */
public class AppLockDao {
	
	private AppLockOpenHelper appLockOpenHelper;
	private Context context;

	//BlackNumberDao 单例模式
	
	//1.私有化构造方法
	private AppLockDao(Context context){
		//创建数据库及其表结构
		appLockOpenHelper = new AppLockOpenHelper(context);
		this.context = context;
	}
	
	//2.声明一个当前类的对象
	private static AppLockDao appLockDao = null;
	
	//3.提供一个方法，如果当前类的对象为空，创建一个新的
	public static AppLockDao getInstance(Context context){
		if (appLockDao == null) {
			appLockDao = new AppLockDao(context);
		}
		return appLockDao;
	}
	
	/**
	 * 插入一条数据
	 * @param packageName 所要插入的包名
	 */
	public void insert(String packageName){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("packagename", packageName);
		db.insert("applock", null, contentValues);
		db.close();
		context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
	}
	
	/**
	 * 删除一条数据
	 * @param packageName 所要删除的包名
	 */
	public void delete(String packageName){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		db.delete("applock", "packagename = ?", new String[]{packageName});
		db.close();
		context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
	}
	
	/**
	 * 查询所有的数据
	 * @return	所有包名所在的集合
	 */
	public List<String> findAll(){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		Cursor cursor = db.query("applock", new String[]{"packagename"}, null, null, null, null, null);
		List<String> lockPackageNameList = new ArrayList<String>();
		while (cursor.moveToNext()) {
			lockPackageNameList.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return lockPackageNameList;
	}
	
}

