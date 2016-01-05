package com.itheima.service;

import com.itheima.dao.IvrSearchDao;
import com.itheima.dao.impl.IvrSearchDaoImpl;
import com.itheima.domain.Ivr;



public class IvrSearchService {
	private IvrSearchDao ivr = new IvrSearchDaoImpl();
	
	public Ivr findAll(String caller){
		return ivr.findInfo(caller);
	}
	
	public boolean isExist(String caller){
		return ivr.isExist(caller);
	}
	
}
