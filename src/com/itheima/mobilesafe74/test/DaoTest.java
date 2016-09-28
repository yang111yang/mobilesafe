package com.itheima.mobilesafe74.test;

import java.util.List;

import com.itheima.mobilesafe74.db.dao.BlackNumberDao;
import com.itheima.mobilesafe74.db.domain.BlackNumberInfo;

import android.test.AndroidTestCase;

@SuppressWarnings("deprecation")
public class DaoTest extends AndroidTestCase {
	
	public void insert() {
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		dao.insert("110", "1");

	}
	public void delete() {
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		dao.delete("110");
		
	}
	public void update() {
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		dao.update("110","2");
		
	}
	public void findAll() {
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		List<BlackNumberInfo> blackNumberList = dao.findAll();
		
	}
	
}
