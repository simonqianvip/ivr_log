package com.itheima.domain;

import com.itheima.exception.CheckDataException;

public class User {
	private String username;
	private String password;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void valiDate() throws CheckDataException{
		if(username == null || "".equals(username)){
			throw new CheckDataException("�û�������Ϊ�գ�����");
		}
		if(password == null || "".equals(password)){
			throw new CheckDataException("���벻��Ϊ�գ�����");
		}
	}

}
