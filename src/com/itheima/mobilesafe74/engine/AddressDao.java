package com.itheima.mobilesafe74.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AddressDao {
	private static final String tag = "AddressDao";
	// 1.指定访问数据库的路径
	public static String path = "data/data/com.itheima.mobilesafe74/files/address.db";
	private static String mAddress = "未知号码";

	/**
	 * 传递一个电话号码，开启数据库的链接，进行访问，返回一个归属地
	 * 
	 * @param phone
	 *            查询的电话号码
	 */
	public static String getAddress(String phone) {

		mAddress = "未知号码";
		
		// 2.开启数据库的链接(只读的型式打开)
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);

		// 使用正则表达式，匹配手机号码
		String regex = "^1[3-8]\\d{9}";
		if (phone.matches(regex)) {

			phone = phone.substring(0, 7);

			// 3.数据库查询
			Cursor cursor = db.query("data1", new String[] { "outkey" },
					"id = ?", new String[] { phone }, null, null, null);
			// 4.查询到即可，不用遍历循环
			if (cursor.moveToNext()) {
				String outkey = cursor.getString(0);
				Log.i(tag, "outkey = " + outkey);
				// 5.通过data1查询到的数据作为外键查询data2
				Cursor indexCursor = db.query("data2",
						new String[] { "location" }, "id = ?",
						new String[] { outkey }, null, null, null);
				if (indexCursor.moveToNext()) {
					mAddress = indexCursor.getString(0);
					Log.i(tag, "location = " + mAddress);
				}
			} else {
				mAddress = "未知号码";
			}
		} else {
			switch (phone.length()) {
			case 3:
				mAddress = "报警电话";
				break;
			case 4:
				mAddress = "模拟器";
				break;
			case 5:
				mAddress = "服务电话";
				break;
			case 7:
				mAddress = "本地电话";
				break;
			case 8:
				mAddress = "本地电话";
				break;
			case 11:
				// (3+8)区号+座机号码(外地)，查询表data2
				String area = phone.substring(1, 3);
				Cursor cursor = db.query("data2",
						new String[] { "location" }, "area = ?",
						new String[] { area }, null, null, null);
				if (cursor.moveToNext()) {
					mAddress = cursor.getString(0);
					Log.i(tag, "location = " + mAddress);
				}else{
					mAddress = "未知号码";
				}
				break;
			case 12:
				// (3+8)区号+座机号码(外地)
				String area1 = phone.substring(1, 4);
				Cursor cursor1 = db.query("data2",
						new String[] { "location" }, "area = ?",
						new String[] { area1 }, null, null, null);
				if (cursor1.moveToNext()) {
					mAddress = cursor1.getString(0);
					Log.i(tag, "location = " + mAddress);
				}else{
					mAddress = "未知号码";
				}
				break;
			}
		}
		
		return mAddress;
		
	}
}
