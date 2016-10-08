package com.itheima.mobilesafe74.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.domain.AppInfo;
import com.itheima.mobilesafe74.engine.AppInfoProvider;

public class AppManagerActivity extends Activity {

	private ListView lv_app_list;

	private List<AppInfo> appInfoList;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			MyAdapter mAdapter = new MyAdapter();
			lv_app_list.setAdapter(mAdapter);
		};
	};
	
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return appInfoList.size();
		}

		@Override
		public Object getItem(int position) {
			return appInfoList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.list_app_item, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.tv_path = (TextView) convertView.findViewById(R.id.tv_path);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.iv_icon.setBackgroundDrawable(appInfoList.get(position).icon);
			holder.tv_name.setText(appInfoList.get(position).name);
			if (appInfoList.get(position).isSdCard) {
				holder.tv_path.setText("sd卡应用");
			}else{
				holder.tv_path.setText("手机应用");
			}
			return convertView;
		}
		
	}
	
	static class ViewHolder{
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_path;
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);

		initTitle();

		initList();
	}

	/**
	 * 展示应用列表
	 */
	private void initList() {
		lv_app_list = (ListView) findViewById(R.id.lv_app_list);
		new Thread() {
			public void run() {
				appInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	/**
	 * 获取内存和sd卡的可用大小
	 */
	private void initTitle() {
		// 1.获取磁盘（内存）可用大小，磁盘路径
		String path = Environment.getDataDirectory().getAbsolutePath();
		// 2.获取sd卡可用大小，sd卡的路径
		String sdPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		// 3.获取以上两个路径下文件夹的可用大小
		String memoryAvailSpace = Formatter.formatFileSize(this,
				getAvailSpace(path));
		String sdMemoryAvailSpace = Formatter.formatFileSize(this,
				getAvailSpace(sdPath));
		TextView tv_memory = (TextView) findViewById(R.id.tv_memory);
		TextView tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);

		tv_memory.setText("磁盘可用: " + memoryAvailSpace);
		tv_sd_memory.setText("sd卡可用: " + sdMemoryAvailSpace);
	}

	/**
	 * 获取磁盘的可用大小
	 * 
	 * @param path
	 * @return 返回值结果为byte = 8bit，最大结果为2147483647 bytes = 2GB
	 */
	private long getAvailSpace(String path) {
		// 获取可用磁盘大小的类
		StatFs statFs = new StatFs(path);
		// 获取可用区块的个数
		long count = statFs.getAvailableBlocks();
		// 获取可用区块的大小
		long size = statFs.getBlockSize();
		// 计算可用磁盘的大小,并返回
		return count * size;
	}

}
