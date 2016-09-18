package com.itheima.mobilesafe74.activity;

import java.util.HashMap;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.itheima.mobilesafe74.R;

public class ContactListActivity extends Activity {

	protected static final String tag = "ContactListActivity";
	private ListView lv_contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);

		initUI();
		initData();
	}

	/**
	 * 获取系统联系人数据的方法
	 */
	private void initData() {
		// 因为读取联系人，可能是一个耗时操作，所以要使用子线程
		new Thread() {
			public void run() {
				// 1.获取内容解析器对象
				ContentResolver contentResolver = getContentResolver();
				// 2.做查询系统联系人数据库的过程
				Cursor cursor = contentResolver.query(Uri
						.parse("content://com.android.contacts/raw_contacts"),
						new String[] { "contact_id" }, null, null, null);
				// 3.循环游标，直到没有数据位置
				while (cursor.moveToNext()) {
					String id = cursor.getString(0);
					 Log.i(tag, "id="+id);
					// 4.根据用户唯一性id的值，查询data表和mimetypes表生成的视图，获取data以及mimetype字段
					Cursor indexCursor = contentResolver.query(
							Uri.parse("content://com.android.contacts/data"),
							new String[] { "data1", "mimetype" },
							"raw_contact_id=?", new String[] { id }, null);
					//5.循环获取每一个联系人的电话号码以及姓名，数据类型
					HashMap<String, String> hashMap = new HashMap<String, String>();
					while (indexCursor.moveToNext()) {
						String data = indexCursor.getString(0);
						String type = indexCursor.getString(1);
//						if (type.equals(other)) {
//							
//						}
					}
					indexCursor.close();	
				}
				// 关闭游标
				cursor.close();
			};
		}.start();

	}

	private void initUI() {
		lv_contact = (ListView) findViewById(R.id.lv_contact);
	}

}
