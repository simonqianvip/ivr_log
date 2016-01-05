package com.itheima.dao;

import java.util.List;

import com.itheima.domain.Scu;


public interface ScuSearchDao {
	public List<Scu> findInfo(String caller,String called,String stime,String etime,String meeting);

	public boolean isExist(String caller,String stime,String etime);
}
