package com.itheima.mobilesafe74.activity;

import net.youmi.android.normal.banner.BannerManager;
import net.youmi.android.normal.banner.BannerViewListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.Md5Util;
import com.itheima.mobilesafe74.utils.SpUtil;
import com.itheima.mobilesafe74.utils.ToastUtil;

public class HomeActivity extends Activity {

	private GridView gv_home;
	private String[] mTitleStrs;
	private int[] mDrawableIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		// 获取广告条
		View bannerView = BannerManager.getInstance(this)
		    .getBannerView(new BannerViewListener() {
				
				@Override
				public void onSwitchBanner() {
					
				}
				
				@Override
				public void onRequestSuccess() {
					
				}
				
				@Override
				public void onRequestFailed() {
					
				}
			});

		// 获取要嵌入广告条的布局
		LinearLayout bannerLayout = (LinearLayout) findViewById(R.id.ll_banner);

		// 将广告条加入到布局中
		bannerLayout.addView(bannerView);
		
		// 初始化UI
		initUI();
		// 初始化数据
		initData();

	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		mTitleStrs = new String[] { "手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计",
				"手机杀毒", "缓存清理", "高级工具", "设置中心" };
		mDrawableIds = new int[] { R.drawable.home_safe,
				R.drawable.home_callmsgsafe, R.drawable.home_apps,
				R.drawable.home_taskmanager, R.drawable.home_netmanager,
				R.drawable.home_trojan, R.drawable.home_sysoptimize,
				R.drawable.home_tools, R.drawable.home_settings };

		// 九宫格控件设置数据适配器
		gv_home.setAdapter(new MyAdapter());
		// 注册九宫格单个条目的点击事件
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			// position点中列表条目的索引
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0://手机防盗
					// 开启对话框
					showDialog();
					break;
				case 1://通信卫士
					startActivity(new Intent(getApplicationContext(),
							BlackNumberActivity.class));
					break;
				case 2://软件管理
					startActivity(new Intent(getApplicationContext(),
							AppManagerActivity.class));
					break;
				case 3://进程管理
					startActivity(new Intent(getApplicationContext(),
							ProgressManagerActivity.class));
					break;
				case 4:
					startActivity(new Intent(getApplicationContext(),
							TrafficActivity.class));
					break;
				case 5://手机杀毒
					startActivity(new Intent(getApplicationContext(),
							AnitVirusActivity.class));
					break;
				case 6://缓存清理
					startActivity(new Intent(getApplicationContext(),
							BaseCacheClearActivity.class));
					break;
				case 7://高级工具
					startActivity(new Intent(getApplicationContext(),
							AToolActivity.class));
					break;
				case 8:// 设置中心
					startActivity(new Intent(getApplicationContext(),
							SettingActivity.class));
					break;

				default:
					break;
				}

			}
		});
	}

	/**
	 * 点击手机防盗，弹出对话框的方法
	 */
	protected void showDialog() {
		// 判断本地是否存储密码
		String psd = SpUtil.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");
		if (TextUtils.isEmpty(psd)) {
			// 1.初始设置密码对话框
			showSetPsdDialog();
		} else {
			// 2.确认密码对话框
			showConfirmPsdDialog();
		}
	}

	/**
	 * 确认密码对话框
	 */
	private void showConfirmPsdDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		
		final View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
		//让对话框显示一个自定义的界面
//		dialog.setView(view);
		
		//为了兼容低版本，给对话框设置布局的时候，让其没有内边距(android系统默认提供出来的)
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
		Button btn_submit = (Button) view.findViewById(R.id.btn_submit);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		
		btn_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//关联弹出对话框中的输入框
				EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
				//获取输入框的值
				String confrimPsd = et_confirm_psd.getText().toString().trim();
				
				
				//对获取的密码进行非空检查，并判断是否一致
				if (!TextUtils.isEmpty(confrimPsd)) {
					//从sp中获取上次存储的密码
					String psd = SpUtil.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, "");
					if (psd.equals(Md5Util.encoder(confrimPsd))) {
						//进入手机防盗模块的界面,开启一个新的activity
//						Intent intent = new Intent(getApplicationContext(),TestActivity.class);
						Intent intent = new Intent(getApplicationContext(),MobileSafeActivity.class);
						
						startActivity(intent);
						//隐藏对话框
						dialog.dismiss();
						
					}else{
						//提示用户密码不一致
						ToastUtil.show(getApplicationContext(), "确认密码错误");
					}
				}else{
					//提示用户密码不能为空
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}
			}
		});
		
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 设置密码对话框
	 */
	private void showSetPsdDialog() {
		//因为需要自定义对话框的展示样式，所以需要dialog.setView(view);
		//view是由自己定义的xml文件转换成view xml---》view
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		
		final View view = View.inflate(this, R.layout.dialog_set_psd, null);
		//让对话框显示一个自定义的界面
//		dialog.setView(view);
		dialog.setView(view, 0, 0, 0, 0);
		
		dialog.show();
		Button btn_submit = (Button) view.findViewById(R.id.btn_submit);
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		
		btn_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//关联弹出对话框中的输入框
				EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
				EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
				//获取输入框的值
				String psd = et_set_psd.getText().toString().trim();
				String confrimPsd = et_confirm_psd.getText().toString().trim();
				//对获取的密码进行非空检查，并判断是否一致
				if (!TextUtils.isEmpty(psd)&&!TextUtils.isEmpty(confrimPsd)) {
					if (psd.equals(confrimPsd)) {
						//进入手机防盗模块的界面,开启一个新的activity
						Intent intent = new Intent(getApplicationContext(),MobileSafeActivity.class);
						startActivity(intent);
						//隐藏对话框
						dialog.dismiss();
						String mPsd = Md5Util.encoder(psd);
						SpUtil.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, mPsd);
					}else{
						//提示用户密码不一致
						ToastUtil.show(getApplicationContext(), "密码不一致");
					}
				}else{
					//提示用户密码不能为空
					ToastUtil.show(getApplicationContext(), "请输入密码");
				}
			}
		});
		
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
	
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		gv_home = (GridView) findViewById(R.id.gv_home);

	}

	class MyAdapter extends BaseAdapter {

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
			View view = View.inflate(getApplicationContext(),
					R.layout.gridview_item, null);
			ImageView ic_icon = (ImageView) view.findViewById(R.id.iv_icon);
			TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
			ic_icon.setImageResource(mDrawableIds[position]);
			tv_title.setText(mTitleStrs[position]);
			return view;
		}

	}

}
