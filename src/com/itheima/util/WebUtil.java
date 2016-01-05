package com.itheima.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;

public class WebUtil {
//	public static Object findFormData(Class clazz,HttpServletRequest request){
//		try {
//			Object obj = clazz.newInstance();//JavaBean�Ķ���
//			BeanUtils.populate(obj, request.getParameterMap());//�?���ֶ���Ҫ��JavaBean������һ��
//			return obj;
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//	}
	public static <T>T findFormData(Class<T> clazz,HttpServletRequest request){
		try {
			T obj = clazz.newInstance();//JavaBean�Ķ���
			BeanUtils.populate(obj, request.getParameterMap());//�?���ֶ���Ҫ��JavaBean������һ��
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
