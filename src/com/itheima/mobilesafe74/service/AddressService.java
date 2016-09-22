package com.itheima.mobilesafe74.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.engine.AddressDao;
import com.itheima.mobilesafe74.utils.ConstantValue;
import com.itheima.mobilesafe74.utils.SpUtil;

public class AddressService extends Service {

	private TelephonyManager mTM;
	private MyPhoneStateListener mPhoneStateListener;
	public static final String tag = "AddressService";
	private WindowManager mWM;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private View mToastView;
	private String mAddress;
	private TextView tv_toast;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			tv_toast.setText(mAddress);
		};
	};
	private int[] mDrawableIds;

	@Override
	public void onCreate() {
		super.onCreate();
		// 第一次开启服务以后，就需要去管理吐司的显示
		// 电话状态的监听(服务开启时监听，关闭时不需要监听)
		// 1.获取电话的管理者对象
		mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		// 2.监听电话状态
		mPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

		// 获取窗体对象
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
	}

	class MyPhoneStateListener extends PhoneStateListener {

		// 3.手动重写，电话状态发生改变时触发的方法
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				// 空闲状态，没有任何活动
				Log.i(tag, "挂断电话，空闲了。。。。。");
				// 挂断电话，一处吐司
				if (mWM != null && mToastView != null) {
					mWM.removeView(mToastView);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				// 摘机状态，至少有个电话活动
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				// 响铃状态(展示吐司)
				Log.i(tag, "响铃了。。。。。");
				showToast(incomingNumber);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void showToast(String incomingNumber) {

		final WindowManager.LayoutParams params = mParams;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.setTitle("Toast");

		params.gravity = Gravity.LEFT + Gravity.TOP;

		// 吐司的显示效果，将吐司挂载到WindowManager上

		mToastView = View.inflate(getApplicationContext(), R.layout.toast_view,
				null);
		tv_toast = (TextView) mToastView.findViewById(R.id.tv_toast);
		
		//从sp中获取色值文字的索引，匹配图片，用做展示
		mDrawableIds = new int[]{
				R.drawable.call_locate_white,
				R.drawable.call_locate_orange,
				R.drawable.call_locate_blue,
				R.drawable.call_locate_gray,
				R.drawable.call_locate_green};
		int toastStyleIndex = SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
		tv_toast.setBackgroundResource(mDrawableIds[toastStyleIndex]);
		// 在窗体上挂载一个View(权限)
		mWM.addView(mToastView, mParams);

		// 获取到来电号码后，需要做归属地查询
		query(incomingNumber);
	}

	/**
	 * 对来电号码进行查询
	 * 
	 * @param incomingNumber
	 *            来电号码
	 */
	private void query(final String incomingNumber) {
		new Thread() {

			public void run() {
				mAddress = AddressDao.getAddress(incomingNumber);
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 取消对电话状态的监听
		if (mTM != null && mPhoneStateListener != null) {
			mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
	}

}
