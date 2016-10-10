package com.itheima.mobilesafe74.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.domain.ProcessInfo;
import com.itheima.mobilesafe74.engine.ProcessInfoProvider;
import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.SpUtil;
import com.itheima.mobilesafe74.utils.ToastUtil;

public class ProgressManagerActivity extends Activity implements
		OnClickListener {

	private TextView tv_process_count, tv_memory_info, tv_des;

	private Button btn_select_all, btn_select_reverse, btn_clear, btn_setting;

	private ListView lv_process;

	private List<ProcessInfo> mProcessInfoList;

	private ArrayList<ProcessInfo> mCustomerList;

	private ArrayList<ProcessInfo> mSystemList;

	private MyAdapter mAdapter;

	private ProcessInfo mProcessInfo;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			mAdapter = new MyAdapter();
			lv_process.setAdapter(mAdapter);
			tv_des.setText("用户进程(" + mCustomerList.size() + ")");
		};
	};

	private int mProcessCount;

	private long mAvailSpace;

	private long mTotalSpace;

	private String mStrTotalSpace;

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
			if (SpUtil.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, false)) {
				return mCustomerList.size() + mSystemList.size() + 2;
			} else {
				return mCustomerList.size() + 1;
			}
		}

		@Override
		public ProcessInfo getItem(int position) {
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
					holder.tv_title.setText("用户进程(" + mCustomerList.size()
							+ ")");
				} else {
					holder.tv_title.setText("系统进程(" + mSystemList.size() + ")");
				}
			} else {
				// 展示图片+文字条目
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.list_process_item, null);
					holder = new ViewHolder();
					holder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_icon);
					holder.tv_name = (TextView) convertView
							.findViewById(R.id.tv_name);
					holder.tv_memory = (TextView) convertView
							.findViewById(R.id.tv_memory);
					holder.cb_box = (CheckBox) convertView
							.findViewById(R.id.cb_box);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
				holder.tv_name.setText(getItem(position).name);
				holder.tv_memory.setText(Formatter.formatFileSize(
						getApplicationContext(), getItem(position).memSize));
				// 本进程不能被选中，所以先隐藏
				if (getItem(position).packageName.equals(getPackageName())) {
					// 选中的是本进程
					holder.cb_box.setVisibility(View.GONE);
				} else {
					holder.cb_box.setVisibility(View.VISIBLE);
				}

				holder.cb_box.setChecked(getItem(position).isCheck);
			}
			return convertView;
		}

	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_memory;
		CheckBox cb_box;
	}

	static class ViewTitleHolder {
		TextView tv_title;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress_manager);

		initUI();
		initTitleData();
		initListData();

	}

	private void initListData() {
		getData();
	}

	private void getData() {
		new Thread() {
			public void run() {
				mProcessInfoList = ProcessInfoProvider
						.getProcessInfo(getApplicationContext());
				mCustomerList = new ArrayList<ProcessInfo>();
				mSystemList = new ArrayList<ProcessInfo>();
				for (ProcessInfo processInfo : mProcessInfoList) {
					if (processInfo.isSystem) {
						// 系统进程
						mSystemList.add(processInfo);
					} else {
						// 非系统进程
						mCustomerList.add(processInfo);
					}
				}
				mHandler.sendEmptyMessage(0);
			};
		}.start();

	}

	private void initTitleData() {
		// 获取进程的个数
		mProcessCount = ProcessInfoProvider.getProcessCount(this);
		// 将进程总数显示
		tv_process_count.setText("进程总数：" + mProcessCount);
		// 获取可用内存的大小,并且格式化
		mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
		String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);
		// 获取总内存的大小,并且格式化
		mTotalSpace = ProcessInfoProvider.getTotalSpace(this);
		mStrTotalSpace = Formatter.formatFileSize(this, mTotalSpace);
		// 将内存和可用内存显示
		tv_memory_info.setText("剩余/总共：" + strAvailSpace + "/" + mStrTotalSpace);
	}

	private void initUI() {
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);
		tv_des = (TextView) findViewById(R.id.tv_des);
		lv_process = (ListView) findViewById(R.id.lv_process);
		btn_select_all = (Button) findViewById(R.id.btn_select_all);
		btn_select_reverse = (Button) findViewById(R.id.btn_select_reverse);
		btn_clear = (Button) findViewById(R.id.btn_clear);
		btn_setting = (Button) findViewById(R.id.btn_setting);

		btn_select_all.setOnClickListener(this);
		btn_select_reverse.setOnClickListener(this);
		btn_clear.setOnClickListener(this);
		btn_setting.setOnClickListener(this);

		lv_process.setOnScrollListener(new OnScrollListener() {

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
						tv_des.setText("系统进程(" + mSystemList.size() + ")");
					} else {
						tv_des.setText("用户进程(" + mCustomerList.size() + ")");
					}
				}
			}
		});
		lv_process.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0 || position == mCustomerList.size() + 1) {
					return;
				} else {
					if (position < mCustomerList.size() + 1) {
						mProcessInfo = mCustomerList.get(position - 1);
					} else {
						mProcessInfo = mSystemList.get(position
								- mCustomerList.size() - 2);
					}
					if (!mProcessInfo.packageName.equals(getPackageName())) {
						// 选中的非当前应用的进程，才需要状态取反，和设置单选框状态
						mProcessInfo.isCheck = !mProcessInfo.isCheck;
						// checkbox状态显示切换
						// 通过选中条目的view对象，findViewById()找到此条目的cb_box，然后切换其状态
						CheckBox cb_box = (CheckBox) view
								.findViewById(R.id.cb_box);
						cb_box.setChecked(mProcessInfo.isCheck);

					}
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_select_all:
			selectAll();
			break;
		case R.id.btn_select_reverse:
			selectReverse();
			break;
		case R.id.btn_clear:
			clearAll();
			break;
		case R.id.btn_setting:
			setting();
			break;
		}
	}

	/**
	 * 设置
	 */
	private void setting() {
		Intent intent = new Intent(this,ProcessSettingActivity.class);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//通知数据适配器刷新
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 一键清理
	 */
	private void clearAll() {
		//1.获取选中的进程
		//2.创建一个记录，用来记录选中的进程
		List<ProcessInfo> killProcessList = new ArrayList<ProcessInfo>();
		for (ProcessInfo processInfo : mCustomerList) {
			if (processInfo.packageName.equals(getPackageName())) {
				continue;
			}
			if (processInfo.isCheck) {
				//3.将选中的用户进程添加到记录集合中去
				killProcessList.add(processInfo);
			}
		}
		for (ProcessInfo processInfo : mSystemList) {
			if (processInfo.isCheck) {
				//4.将选中的系统进程添加到记录集合中去
				killProcessList.add(processInfo);
			}
		}
		
		//5.遍历记录选中进程的集合，删除选中的进程
		long totalRelaseSpace = 0;
		for (ProcessInfo processInfo : killProcessList) {
			if (mCustomerList.contains(processInfo)) {
				mCustomerList.remove(processInfo);
			}
			if (mSystemList.contains(processInfo)) {
				mSystemList.remove(processInfo);
			}
			totalRelaseSpace += processInfo.getMemSize();
			//6.杀死进程
			ProcessInfoProvider.killProcess(this,processInfo);
		}
		
		//7.通知数据适配器刷新
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		
		//8.更新进程总数
		mProcessCount -= killProcessList.size();
		tv_process_count.setText("进程总数：" + mProcessCount);
		//9.更新剩余内存
		mAvailSpace += totalRelaseSpace;
		tv_memory_info.setText("剩余/总共：" + Formatter.formatFileSize(this, mAvailSpace) + "/" + mStrTotalSpace);
		//10.通过吐司，告知杀死了几个进程，释放了多少空间
		String totalRelase = Formatter.formatFileSize(this, totalRelaseSpace);
//		ToastUtil.show(this, "杀死了"+killProcessList.size()+"个进程，释放了"+totalRelase+"内存");
		ToastUtil.show(this, String.format("杀死了%d个进程，释放了%s内存", killProcessList.size(),totalRelase));
	}

	/**
	 * 反选
	 */
	private void selectReverse() {
		// 遍历所有集合中的对象上的isCheck取反
		for (ProcessInfo processInfo : mCustomerList) {
			if (processInfo.packageName.equals(getPackageName())) {
				continue;
			}
			processInfo.isCheck = !processInfo.isCheck;
		}
		for (ProcessInfo processInfo : mSystemList) {
			processInfo.isCheck = !processInfo.isCheck;
		}
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 全选
	 */
	private void selectAll() {
		// 遍历所有集合中的对象，将其的状态设置为true，代表全选，排除当前应用
		for (ProcessInfo processInfo : mCustomerList) {
			if (processInfo.packageName.equals(getPackageName())) {
				continue;
			}
			processInfo.isCheck = true;
		}
		for (ProcessInfo processInfo : mSystemList) {
			processInfo.isCheck = true;
		}
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

}
