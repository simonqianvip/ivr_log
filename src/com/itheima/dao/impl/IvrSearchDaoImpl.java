package com.itheima.dao.impl;

import java.util.Map;

import com.itheima.dao.IvrSearchDao;
import com.itheima.domain.Ivr;
import com.itheima.util.RedisClient;

public class IvrSearchDaoImpl implements IvrSearchDao {
	private RedisClient redis = new RedisClient();;

	@Override
	public Ivr findInfo(String caller) {
		Ivr ivr = null;
		try {
			@SuppressWarnings("rawtypes")
			Map map = this.redis.show(caller);
			ivr = new Ivr();
			ivr.setCaller(String.valueOf(map.get("caller")));
			ivr.setIp(String.valueOf(map.get("ip")));
			ivr.setSession(String.valueOf(map.get("session")));
			ivr.setFilename(String.valueOf(map.get("name")));
			ivr.setSpid(String.valueOf(map.get("spid")));
			ivr.setRbusNo(String.valueOf(map.get("rbusNo")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ivr;
	}

	@Override
	public boolean isExist(String caller) {
		boolean exist = this.redis.isExist(caller);
		return exist;
	}

//	public static void main(String[] args) {
//		new IvrSearchDaoImpl().findInfo("15125775960");
//	}

}
