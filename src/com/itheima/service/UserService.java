package com.itheima.service;

import com.itheima.dao.impl.UserDaoImpl;
import com.itheima.domain.User;

public class UserService {
	private UserDaoImpl dao = new UserDaoImpl();
	/**
	 * ע���û��ķ���
	 */
//	public void registUser(User user){
//		dao.addUser(user);
//	}
	/**
	 * ����û����Ƿ��Ѿ���ʹ�õķ���
	 */
//	public boolean hasName(String username){
//		return dao.hasName(username);
//	}
	
	/**
	 * �����û��������Ƿ���ȷ�ķ���
	 */
	public boolean isUser(User user){
		return dao.isUser(user);
	}
}
