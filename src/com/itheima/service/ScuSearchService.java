package com.itheima.service;

import java.util.List;

import com.itheima.dao.ScuSearchDao;
import com.itheima.dao.impl.ScuSearchDaoImpl;
import com.itheima.domain.Scu;



public class ScuSearchService {
	private ScuSearchDao scu = new ScuSearchDaoImpl();
	
	public List<Scu> findAll(String caller,String called,String stime,String etime,String meeting){
		return scu.findInfo(caller,called,stime,etime,meeting);
	}
	
	public boolean isExist(String caller,String stime,String etime){
		return scu.isExist(caller,stime,etime);
	}
	
}
