package com.itheima.mobilesafe74.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.db.dao.BlackNumberDao;
import com.itheima.mobilesafe74.db.domain.BlackNumberInfo;

public class BlackNumberActivity extends Activity {

	private Button btn_add;
	
	private ListView lv_blacknumber;
	
	private BlackNumberDao mBlackNumberDao;

	private List<BlackNumberInfo> mBlackNumberList;
	
	private Handler mHandler = new Handler(){
		private MyAdapter myAdapter;

		public void handleMessage(android.os.Message msg) {
			//4.告知ListVIew，可以去设置数据适配器了
			myAdapter = new MyAdapter();
			lv_blacknumber.setAdapter(myAdapter);
		};
	};
	
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mBlackNumberList.size();
		}

		@Override
		public BlackNumberInfo getItem(int position) {
			return mBlackNumberList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.list_blacknumber_item, null);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			TextView tv_mode = (TextView) view.findViewById(R.id.tv_mode);
			ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
			
			tv_phone.setText(mBlackNumberList.get(position).phone);
//			tv_mode.setText(mBlackNumberList.get(position).mode);
			int mode = Integer.parseInt(mBlackNumberList.get(position).mode);
			switch (mode) {
			case 1:
				tv_mode.setText("拦截短信");
				break;
			case 2:
				tv_mode.setText("拦截电话");
				break;
			case 3:
				tv_mode.setText("拦截所有");
				break;
			}
			return view;
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber);

		initUI();
		initData();

	}

	/**
	 * 初始化数据的方法
	 */
	private void initData() {
		// 获取数据库中所有的电话号码
		new Thread() {


			public void run() {
				//1.获取操作黑名单数据库的对象
				mBlackNumberDao = BlackNumberDao.getInstance(getApplicationContext());
				//2.查询所有数据
				mBlackNumberList = mBlackNumberDao.findAll();
				//3.消息机制
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	/**
	 * 初始化UI的方法
	 */
	private void initUI() {
		btn_add = (Button) findViewById(R.id.btn_add);
		lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
	}

}
