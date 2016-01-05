package com.itheima.dao;

import com.itheima.domain.Ivr;

public interface IvrSearchDao {
	public Ivr findInfo(String caller);

	public boolean isExist(String caller);
}
