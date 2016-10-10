package com.itheima.mobilesafe74.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.itheima.mobilesafe74.R;
import com.itheima.mobilesafe74.engine.CommonNumberDao;
import com.itheima.mobilesafe74.engine.CommonNumberDao.Child;
import com.itheima.mobilesafe74.engine.CommonNumberDao.Group;

public class CommonNumberQueryActivity extends Activity {
	private ExpandableListView elv_common_number;
	private List<Group> mGroup;
	private MyAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);
		
		initUI();
		initData();
	}

	/**
	 * 给可扩展ListView准备数据，并填充
	 */
	private void initData() {
		CommonNumberDao commonNumberDao = new CommonNumberDao();
		mGroup = commonNumberDao.getGroup();
		mAdapter = new MyAdapter();
		elv_common_number.setAdapter(mAdapter);
		elv_common_number.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				startCall(mAdapter.getChild(groupPosition, childPosition).number);
				return false;
			}
		});
	}
	
	protected void startCall(String number) {
		//开启系统的打电话界面
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:"+number));
		startActivity(intent);
	}

	/**
	 * 初始化UI的方法
	 */
	private void initUI() {
		elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
	}
	
	class MyAdapter extends BaseExpandableListAdapter{

		@Override
		public int getGroupCount() {
			return mGroup.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mGroup.get(groupPosition).childList.size();
		}

		@Override
		public Group getGroup(int groupPosition) {
			return mGroup.get(groupPosition);
		}

		@Override
		public Child getChild(int groupPosition, int childPosition) {
			return mGroup.get(groupPosition).childList.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setText("			 " + mGroup.get(groupPosition).name);
			textView.setTextColor(Color.RED);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			return textView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.elv_child_item, null);
			TextView tv_child_name = (TextView) view.findViewById(R.id.tv_child_name);
			TextView tv_child_number = (TextView) view.findViewById(R.id.tv_child_number);
			tv_child_name.setText(getChild(groupPosition, childPosition).name);
			tv_child_number.setText(getChild(groupPosition, childPosition).number);
			return view;
		}

		//孩子节点是否响应事件
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
	
}
