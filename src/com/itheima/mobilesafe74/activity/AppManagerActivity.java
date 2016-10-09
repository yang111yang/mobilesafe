package com.itheima.mobilesafe74.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.domain.AppInfo;
import com.itheima.mobilesafe74.engine.AppInfoProvider;

public class AppManagerActivity extends Activity implements OnClickListener {

	private ListView lv_app_list;

	private List<AppInfo> mAppInfoList;

	private List<AppInfo> mCustomerList;

	private List<AppInfo> mSystemList;

	private TextView tv_des;

	protected AppInfo mAppInfo;

	private PopupWindow mPopupWindow;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			MyAdapter mAdapter = new MyAdapter();
			lv_app_list.setAdapter(mAdapter);
			tv_des.setText("用户应用(" + mCustomerList.size() + ")");
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
		lv_app_list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				/*
				 * 滚动过程中调用的方法 AbsListView中的view就是listview对象
				 * firstVisibleItem第一个可见的条目 visibleItemCount当前一个屏幕的可见的条目总数
				 * totalItemCount总条目的总数
				 */
				if (mCustomerList != null && mSystemList != null) {
					if (firstVisibleItem >= mCustomerList.size() + 1) {
						tv_des.setText("系统应用(" + mSystemList.size() + ")");
					} else {
						tv_des.setText("用户应用(" + mCustomerList.size() + ")");
					}
				}
			}
		});

		lv_app_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0 || position == mCustomerList.size() + 1) {
					return;
				} else {
					if (position < mCustomerList.size() + 1) {
						mAppInfo = mCustomerList.get(position - 1);
					} else {
						mAppInfo = mSystemList.get(position
								- mCustomerList.size() - 2);
					}
					showPopupWindow(view);
				}
			}
		});

	}

	protected void showPopupWindow(View view) {
		View popupView = View.inflate(this, R.layout.popupwindow_layout, null);
		TextView tv_uninstall = (TextView) popupView
				.findViewById(R.id.tv_uninstall);
		TextView tv_start = (TextView) popupView.findViewById(R.id.tv_start);
		TextView tv_share = (TextView) popupView.findViewById(R.id.tv_share);

		tv_uninstall.setOnClickListener(this);
		tv_start.setOnClickListener(this);
		tv_share.setOnClickListener(this);

		// 透明动画(由透明变为不透明)
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(1000);
		alphaAnimation.setFillAfter(true);
		// 缩放动画
		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleAnimation.setDuration(1000);
		scaleAnimation.setFillAfter(true);
		//动画集合
		AnimationSet animationSet = new AnimationSet(true);
		//添加两个动画
		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(scaleAnimation);
		

		// 1.创建窗体对象，指定宽高
		
		mPopupWindow = new PopupWindow(popupView,
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, true);
		// 2.设置一个透明的背景
		mPopupWindow.setBackgroundDrawable(new ColorDrawable());
		// 3.指定窗体的位置
		mPopupWindow.showAsDropDown(view, 50, -view.getHeight());
		//执行动画
		popupView.startAnimation(animationSet);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_uninstall:
			//系统应用不能被卸载
			if (mAppInfo.isSystem) {
				Toast.makeText(this, "此应用不能被卸载", 0).show();
			}else{
				Intent intent = new Intent("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:"+mAppInfo.getPackageName()));
				startActivity(intent);
			}
			break;
		case R.id.tv_start:
			//通过桌面去启动指定报名的应用
			PackageManager pm = getPackageManager();
			//通过Launch开启指定包名的意图，去开启应用
			Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mAppInfo.getPackageName());
			if (launchIntentForPackage != null) {
				startActivity(launchIntentForPackage);
			}else{
				Toast.makeText(this, "此应用不能被开启", 0).show();
			}
			break;
		case R.id.tv_share:
			//通过短信，向外发送
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT, "分享一个应用，应用名称为:"+mAppInfo.getName());
			intent.setType("text/plain");
			startActivity(intent);
			break;
		}
		//点击了窗体，窗体小时
		if (mPopupWindow != null) {
			mPopupWindow.dismiss();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//重新获取数据
		getData();
	}

	private void getData() {
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

		
	}

}
