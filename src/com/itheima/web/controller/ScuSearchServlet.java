package com.itheima.web.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itheima.domain.Ivr;
import com.itheima.service.ScuSearchService;
import com.itheima.util.LinuxUtil;
import com.itheima.util.StrUtil;
import com.jcraft.jsch.ChannelSftp;

public class ScuSearchServlet extends HttpServlet {
	private static final long serialVersionUID = -3565801523496697508L;
	private Log log = LogFactory.getLog(ScuSearchServlet.class);
	private ScuSearchService service = new ScuSearchService();
	private static final String APP = "app";
	private static final String LOG = "log";
	/**
	 * 斜杠
	 */
	private static final String SPRIT = "/";
	

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String caller = request.getParameter("caller");
		String called = request.getParameter("called");
		String stime = request.getParameter("stime");
		String etime = request.getParameter("etime");
		String meeting = request.getParameter("meeting");
		
		try {
			boolean flag = this.service.isExist(caller,stime,etime);
			if (flag) {
				List scuList = service.findAll(caller,called,stime,etime,meeting);
				request.setAttribute("scuList", scuList);
				request.getRequestDispatcher("/scu_search.jsp").forward(
						request, response);
			} else {
				request.setAttribute("msg", "对不起，"+caller +"号码库里不存在！");
				request.getRequestDispatcher("/scu_search.jsp").forward(
						request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
}
