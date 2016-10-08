package com.itheima.mobilesafe74.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.db.dao.BlackNumberDao;
import com.itheima.mobilesafe74.domain.BlackNumberInfo;

/**
 * 针对ListView做出一系列的优化 1.复用convertView 2.使用ViewHolder服用findViewById
 * 3.使用分页查询来避免加载过多的数据
 * 
 * @author 刘建阳
 * @date 2016-9-28 下午3:05:18
 */
public class BlackNumberActivity extends Activity {

	private Button btn_add;

	private ListView lv_blacknumber;

	private BlackNumberDao mBlackNumberDao;

	private List<BlackNumberInfo> mBlackNumberList;

	private int mode = 1;

	private boolean mIsLoad = false;
	
	private int mCount;

	private MyAdapter myAdapter;

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (myAdapter == null) {
				// 4.告知ListVIew，可以去设置数据适配器了
				myAdapter = new MyAdapter();
				lv_blacknumber.setAdapter(myAdapter);
			}else{
				myAdapter.notifyDataSetChanged();
			}
		};
	};

	class MyAdapter extends BaseAdapter {

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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.list_blacknumber_item, null);
				holder = new ViewHolder();
				holder.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_phone);
				holder.tv_mode = (TextView) convertView
						.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) convertView
						.findViewById(R.id.iv_delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 1.数据库删除
					mBlackNumberDao.delete(mBlackNumberList.get(position).phone);
					// 2.集合中的删除，通知适配器刷新
					mBlackNumberList.remove(position);
					if (myAdapter != null) {
						myAdapter.notifyDataSetChanged();
					}
				}
			});

			holder.tv_phone.setText(mBlackNumberList.get(position).phone);
			// tv_mode.setText(mBlackNumberList.get(position).mode);
			int mode = Integer.parseInt(mBlackNumberList.get(position).mode);
			switch (mode) {
			case 1:
				holder.tv_mode.setText("拦截短信");
				break;
			case 2:
				holder.tv_mode.setText("拦截电话");
				break;
			case 3:
				holder.tv_mode.setText("拦截所有");
				break;
			}
			return convertView;
		}

	}

	static class ViewHolder {
		public TextView tv_phone;
		public TextView tv_mode;
		public ImageView iv_delete;
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
				// 1.获取操作黑名单数据库的对象
				mBlackNumberDao = BlackNumberDao
						.getInstance(getApplicationContext());
				// 2.查询部分数据
				mBlackNumberList = mBlackNumberDao.find(0);
				mCount = mBlackNumberDao.getCount();
				// 3.消息机制
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

		btn_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showDialog();
			}
		});

		// 监听listview的监听状态
		lv_blacknumber.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 当滚动过程中状态发生改变调用的方法
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& lv_blacknumber.getLastVisiblePosition() >= mBlackNumberList
								.size() - 1 && !mIsLoad) {
					// 加载下一页数据
					// mIsLoad防止重复加载的变量
					if (mCount>mBlackNumberList.size()) {
						new Thread() {
							public void run() {
								// 1.获取操作黑名单数据库的对象
								mBlackNumberDao = BlackNumberDao
										.getInstance(getApplicationContext());
								// 2.查询部分数据
								List<BlackNumberInfo> moreData = mBlackNumberDao
										.find(mBlackNumberList.size());
								// 3.添加下一页数据的过程
								mBlackNumberList.addAll(moreData);
								//4.通知适配器刷新数据
								mHandler.sendEmptyMessage(0);
							};
						}.start();
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 滚动过程中调用的方法
			}
		});
	}

	protected void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dialog_add_blacknumber, null);
		dialog.setView(view, 0, 0, 0, 0);
		final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
		Button btn_submit = (Button) view.findViewById(R.id.btn_submit);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		// 监听其选中条目的切换过程
		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_sms:// 拦截短信
					mode = 1;
					break;
				case R.id.rb_phone:// 拦截电话
					mode = 2;
					break;
				case R.id.rb_all:// 拦截所有
					mode = 3;
					break;
				}
			}
		});

		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 1.获取输入框中的内容
				String phone = et_phone.getText().toString();
				if (!TextUtils.isEmpty(phone)) {
					// 2.数据库插入当前输入的电话号码
					mBlackNumberDao.insert(phone, mode + "");
					// 3.让数据库和集合保持同步
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
					blackNumberInfo.phone = phone;
					blackNumberInfo.mode = mode + "";
					// 4.将对象插入到集合的顶部
					mBlackNumberList.add(0, blackNumberInfo);
					// 5.通知数据适配器刷新
					if (myAdapter != null) {
						myAdapter.notifyDataSetChanged();
					}
					dialog.dismiss();

				} else {
					Toast.makeText(getApplicationContext(), "请输入电话号码",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();

	}

}
