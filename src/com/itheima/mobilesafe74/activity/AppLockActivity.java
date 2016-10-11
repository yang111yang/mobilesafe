package com.itheima.mobilesafe74.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.db.dao.AppLockDao;
import com.itheima.mobilesafe74.domain.AppInfo;
import com.itheima.mobilesafe74.engine.AppInfoProvider;

public class AppLockActivity extends Activity {

	private Button btn_unlock, btn_lock;
	private LinearLayout ll_unlock, ll_lock;
	private TextView tv_unlock, tv_lock;
	private ListView lv_unlock, lv_lock;
	private List<AppInfo> mAppInfoList;
	private ArrayList<AppInfo> mLockList;
	private ArrayList<AppInfo> mUnlockList;
	private AppLockDao mDao;
	private List<String> mAppLockList;
	private MyAdapter mLockAdapter;
	private MyAdapter mUnLockAdapter;
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			//6.为已加锁和未加锁的listview填充数据
			mLockAdapter = new MyAdapter(true);
			mUnLockAdapter = new MyAdapter(false);
			lv_lock.setAdapter(mLockAdapter);
			lv_unlock.setAdapter(mUnLockAdapter);
			
		};
	};
	private TranslateAnimation mTranslateAnimation;
	
	class MyAdapter extends BaseAdapter{
		
		private boolean isLock;

		/**
		 * @param isLock 应用是否加锁的标识	true 表示已加锁		false 表示未加锁
		 */
		public MyAdapter(boolean isLock) {
			this.isLock = isLock;
		}

		@Override
		public int getCount() {
			if (isLock) {
				tv_lock.setText("已加锁应用："+mLockList.size());
				return mLockList.size();
			}else{
				tv_unlock.setText("未加锁应用："+mUnlockList.size());
				return mUnlockList.size();
			}
		}

		@Override
		public AppInfo getItem(int position) {
			if (isLock) {
				return mLockList.get(position);
			}else{
				return mUnlockList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.list_islock_item, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.iv_lock = (ImageView) convertView.findViewById(R.id.iv_lock);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			final AppInfo appInfo = getItem(position);
			holder.iv_icon.setBackgroundDrawable(appInfo.icon);
			holder.tv_name.setText(appInfo.name);
			if (isLock) {
				holder.iv_lock.setBackgroundResource(R.drawable.lock);
			}else{
				holder.iv_lock.setBackgroundResource(R.drawable.unlock);
			}
			
			final View animationView = convertView;
			
			holder.iv_lock.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//添加动画效果
					animationView.startAnimation(mTranslateAnimation);
					mTranslateAnimation.setAnimationListener(new AnimationListener() {
						
						@Override
						public void onAnimationStart(Animation animation) {
							
						}
						
						@Override
						public void onAnimationRepeat(Animation animation) {
							
						}
						
						@Override
						public void onAnimationEnd(Animation animation) {
							if (isLock) {
								//1.已加锁集合中删除一个，未加锁集合中添加一个
								mLockList.remove(appInfo);
								mUnlockList.add(appInfo);
								//2.从已加锁数据库中删除一条数据
								mDao.delete(appInfo.packageName);
								//3.刷新数据适配器
								mLockAdapter.notifyDataSetChanged();
							}else{
								//1.未加锁集合中删除一个，已加锁集合中添加一个
								mLockList.add(appInfo);
								mUnlockList.remove(appInfo);
								//2.往已加锁数据库中添加一条数据
								mDao.insert(appInfo.packageName);
								//3.刷新数据适配器
								mUnLockAdapter.notifyDataSetChanged();
							}
						}
					});
				}
			});
			
			return convertView;
		}
		
	}
	
	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		ImageView iv_lock;
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);

		initUI();
		initData();
		initAnimation();

	}

	/**
	 * 加载一个平移动画
	 */
	private void initAnimation() {
		mTranslateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 1, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0);
		mTranslateAnimation.setDuration(500);
	}

	private void initData() {
		new Thread() {
			public void run() {
				// 1.获取手机中的所有应用
				mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
				//2.创建已加锁和未加锁应用的集合
				mLockList = new ArrayList<AppInfo>();
				mUnlockList = new ArrayList<AppInfo>();
				//3.获取数据库中所有已加锁的应用的包名的集合
				mDao = AppLockDao.getInstance(getApplicationContext());
				mAppLockList = mDao.findAll();
				for (AppInfo appInfo : mAppInfoList) {
					//4.如果数据库中的数据集合包含当前遍历的应用的包名，则该应用已加锁
					if (mAppLockList.contains(appInfo.packageName)) {
						mLockList.add(appInfo);
					}else{
						mUnlockList.add(appInfo);
					}
				}
				//5.消息机制
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initUI() {
		btn_unlock = (Button) findViewById(R.id.btn_unlock);
		btn_lock = (Button) findViewById(R.id.btn_lock);
		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
		ll_lock = (LinearLayout) findViewById(R.id.ll_lock);
		tv_unlock = (TextView) findViewById(R.id.tv_unlock);
		tv_lock = (TextView) findViewById(R.id.tv_lock);
		lv_unlock = (ListView) findViewById(R.id.lv_unlock);
		lv_lock = (ListView) findViewById(R.id.lv_lock);
		
		btn_lock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//1.已加锁列表隐藏，未加锁列表显示
				ll_unlock.setVisibility(View.GONE);
				ll_lock.setVisibility(View.VISIBLE);
				//2.已加锁按钮背景变深，未加锁按钮背景变浅
				btn_unlock.setBackgroundResource(R.drawable.tab_left_default);
				btn_lock.setBackgroundResource(R.drawable.tab_right_pressed);
				
			}
		});
		btn_unlock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//1.未加锁列表隐藏，已加锁列表显示
				ll_unlock.setVisibility(View.VISIBLE);
				ll_lock.setVisibility(View.GONE);
				//2.未加锁按钮背景变深，已加锁按钮背景变浅
				btn_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
				btn_lock.setBackgroundResource(R.drawable.tab_right_default);
			}
		});
	}

}
