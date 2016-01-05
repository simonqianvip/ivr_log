package com.itheima.web.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import com.itheima.domain.User;
import com.itheima.exception.CheckDataException;
import com.itheima.service.UserService;

public class RegistServlet extends HttpServlet {
	private UserService service = new UserService();
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try{
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		//1.��֤��֤��
		HttpSession session = request.getSession(false);
		if(session==null || session.getAttribute("valiStr")==null || !session.getAttribute("valiStr").equals(request.getParameter("valiStr"))){
			request.setAttribute("msg", "��֤�벻��ȷ��");
			request.getRequestDispatcher("/regist.jsp").forward(request, response);
			return;
		}
		//2.��װ���ݵ�bean
		User user = new User();
		BeanUtils.populate(user, request.getParameterMap());
		
		//3.У������
		user.valiDate();
		
		//4.У���û����Ƿ��Ѿ�ʹ�ã�����serviceע���û�
//		if(service.hasName(user.getUsername())){
//			request.setAttribute("msg", "�û����Ѿ����ڣ�");
//			request.getRequestDispatcher("/regist.jsp").forward(request, response);
//			return;
//		}
//		service.registUser(user);
		//5.��session�б����¼״̬
		request.getSession().setAttribute("username", user.getUsername());
		//6.�ض�����ҳ
		response.sendRedirect(request.getContextPath()+"/homepage.jsp");
		
		}catch (CheckDataException e) {//У�������ص�ע��ҳ����ʾ����
			request.setAttribute("msg", e.getMessage());
			request.getRequestDispatcher("/regist.jsp").forward(request, response);
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
