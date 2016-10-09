package com.itheima.mobilesafe74.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.domain.AppInfo;
import com.itheima.mobilesafe74.engine.AppInfoProvider;

public class AppManagerActivity extends Activity {

	private ListView lv_app_list;

	private List<AppInfo> mAppInfoList;

	private List<AppInfo> mCustomerList;

	private List<AppInfo> mSystemList;

	private TextView tv_des;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			MyAdapter mAdapter = new MyAdapter();
			lv_app_list.setAdapter(mAdapter);
			tv_des.setText("用户应用(" + mCustomerList.size()
							+ ")");
		};
	};


	class MyAdapter extends BaseAdapter {

		// 获取适配器中条目类型的整数，修改成两种(纯文本,图片+文字)
		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;
		}

		// 指定索引指向的条目类型，条目类型状态码指定(0(复用系统),1)
		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == mCustomerList.size() + 1) {
				// 返回0,代表纯文本条目状态码
				return 0;
			} else {
				// 返回1,代表图片+文本状态码
				return 1;
			}
		}

		// ListView中添加两个描述性的条目
		@Override
		public int getCount() {
			return mCustomerList.size() + mSystemList.size() + 2;
		}

		@Override
		public AppInfo getItem(int position) {
			if (position == 0 || position == mCustomerList.size() + 1) {
				return null;
			} else {
				if (position < mCustomerList.size() + 1) {
					return mCustomerList.get(position - 1);
				} else {
					return mSystemList.get(position - mCustomerList.size() - 2);
				}
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			if (type == 0) {
				// 展示灰色条目
				ViewTitleHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.list_app_title_item, null);
					holder = new ViewTitleHolder();
					holder.tv_title = (TextView) convertView
							.findViewById(R.id.tv_title);
					convertView.setTag(holder);
				} else {
					holder = (ViewTitleHolder) convertView.getTag();
				}
				if (position == 0) {
					holder.tv_title.setText("用户应用(" + mCustomerList.size()
							+ ")");
				} else {
					holder.tv_title.setText("系统应用(" + mSystemList.size() + ")");
				}
			} else {
				// 展示图片+文字条目
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.list_app_item, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_icon);
					holder.tv_name = (TextView) convertView
							.findViewById(R.id.tv_name);
					holder.tv_path = (TextView) convertView
							.findViewById(R.id.tv_path);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
				holder.tv_name.setText(getItem(position).name);
				if (getItem(position).isSdCard) {
					holder.tv_path.setText("sd卡应用");
				} else {
					holder.tv_path.setText("手机应用");
				}
			}
			return convertView;
		}

	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_path;

	}

	static class ViewTitleHolder {
		TextView tv_title;

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
		tv_des = (TextView) findViewById(R.id.tv_des);
		new Thread() {

			public void run() {
				mAppInfoList = AppInfoProvider
						.getAppInfoList(getApplicationContext());
				mCustomerList = new ArrayList<AppInfo>();
				mSystemList = new ArrayList<AppInfo>();
				for (AppInfo appInfo : mAppInfoList) {
					if (appInfo.isSystem) {
						// 系统应用
						mSystemList.add(appInfo);
					} else {
						// 非系统应用
						mCustomerList.add(appInfo);
					}
				}
				mHandler.sendEmptyMessage(0);
			};
		}.start();
		
		lv_app_list.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				/*滚动过程中调用的方法
				AbsListView中的view就是listview对象
				firstVisibleItem第一个可见的条目
				visibleItemCount当前一个屏幕的可见的条目总数
				totalItemCount总条目的总数*/
				if (mCustomerList != null && mSystemList != null) {
					if (firstVisibleItem >= mCustomerList.size() + 1) {
						tv_des.setText("系统应用(" + mSystemList.size()
								+ ")");
					}else{
						tv_des.setText("用户应用(" + mCustomerList.size()
								+ ")");
					}
				}
			}
		});
		
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
