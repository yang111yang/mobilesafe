package com.itheima.mobilesafe74.test;

import java.util.List;
import java.util.Random;

import com.itheima.mobilesafe74.db.dao.BlackNumberDao;
import com.itheima.mobilesafe74.domain.BlackNumberInfo;

import android.test.AndroidTestCase;

@SuppressWarnings("deprecation")
public class DaoTest extends AndroidTestCase {
	
	public void insert() {
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		dao.insert("110", "1");
		for (int i = 0; i < 100; i++) {
			if (i<10) {
				dao.insert("1396660000"+i, 1+new Random().nextInt(3)+"");
			}else{
				dao.insert("139666000"+i, 1+new Random().nextInt(3)+"");
			}
		}

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
