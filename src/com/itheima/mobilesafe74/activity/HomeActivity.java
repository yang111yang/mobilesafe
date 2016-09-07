package com.itheima.mobilesafe74.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;

public class HomeActivity extends Activity {

	private GridView gv_home;
	private String[] mTitleStrs;
	private int[] mDrawableIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		// 初始化UI
		initUI();
		// 初始化数据
		initData();

	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		mTitleStrs = new String[] { "手机防盗", "通信卫士", "软件管理", "进程管理",
				"流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心" };
		mDrawableIds = new int[] { R.drawable.home_safe,
				R.drawable.home_callmsgsafe, R.drawable.home_apps,
				R.drawable.home_taskmanager, R.drawable.home_netmanager,
				R.drawable.home_trojan, R.drawable.home_sysoptimize,
				R.drawable.home_tools, R.drawable.home_settings };

		//九宫格控件设置数据适配器
		gv_home.setAdapter(new MyAdapter());
		//注册九宫格单个条目的点击事件
		gv_home.setOnItemClickListener(new OnItemClickListener() {
			
			//position点中列表条目的索引
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					
					break;
				case 1:
					
					break;
				case 2:
					
					break;
				case 3:
					
					break;
				case 4:
					
					break;
				case 5:
					
					break;
				case 6:
					
					break;
				case 7:
					
					break;
				case 8://设置中心
					Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}
				
				
			}
		});	
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		gv_home = (GridView) findViewById(R.id.gv_home);

	}
	
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mTitleStrs.length;
		}

		@Override
		public Object getItem(int position) {
			return mTitleStrs[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
			ImageView ic_icon = (ImageView) view.findViewById(R.id.iv_icon);
			TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
			ic_icon.setImageResource(mDrawableIds[position]);
			tv_title.setText(mTitleStrs[position]);
			return view;
		}
		
	}

}
