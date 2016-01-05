package com.itheima.web.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;





import com.itheima.domain.Ivr;
import com.itheima.service.IvrSearchService;
import com.itheima.util.LinuxUtil;
import com.itheima.util.StrUtil;
import com.jcraft.jsch.ChannelSftp;

public class IvrSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 8072117364840315411L;
	private IvrSearchService service = new IvrSearchService();
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
		String spid = request.getParameter("spid");
		
		try {
			//Determine whether the number is present
			boolean exist = this.service.isExist(caller);
			if (exist) {
				Ivr ivr = service.findAll(caller);
				if(!spid.equals("")){
					ivr.setSpid(spid);
				}
				String findPath = findPath(ivr);
				if(!findPath.equals(null) || !findPath.equals(" ")){
//					request.setAttribute("ip", ivr.getIp());
//					request.setAttribute("name", ivr.getFilename());
//					request.setAttribute("rbusNo", ivr.getRbusNo());
//					request.getRequestDispatcher("/ivr_result.jsp?caller="+caller+"&findPath="+findPath+"&ip="+ivr.getIp()+"&session="+ivr.getSession()).forward(
//							request, response);
					response.sendRedirect("/IVR_log/ivr_result.jsp?caller="+caller+"&findPath="+findPath+"&ip="+ivr.getIp()+"&session="+ivr.getSession()+"&name="+ivr.getFilename()+"&rbusNo="+ivr.getRbusNo());
				}else{
					request.setAttribute("ivr_msg", " 对不起，"+caller + "号码对应的日志文件不存在！");
					request.getRequestDispatcher("/ivr_search.jsp").forward(
							request, response);
				}

			} else {
				request.setAttribute("msg", " 对不起，"+caller + "号码库里不存在！");
				request.getRequestDispatcher("/ivr_search.jsp").forward(
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
	
	/**
	 * 查找号码对应的日志
	 * @param ivr
	 * @return
	 * @throws Exception
	 * @author simon
	 */
	public String findPath(Ivr ivr) throws Exception {
		String result = null;
		ChannelSftp sftp = LinuxUtil.connect(ivr.getIp());
		sftp.cd(SPRIT+APP);
		@SuppressWarnings("rawtypes")
		Vector vector = sftp.ls(SPRIT+APP);
		String traverse = StrUtil.traverse(vector, ivr.getSpid());
		String formatData = StrUtil.formatData(traverse);
		String lowerCase = ivr.getFilename().toLowerCase();
		sftp.cd(formatData + SPRIT + lowerCase + SPRIT+LOG);

		String pwd = sftp.pwd();
		@SuppressWarnings("rawtypes")
		Vector vector2 = sftp.ls(pwd);
		String traverse2 = StrUtil.traverse(vector2, "logfiles");
		String formatData2 = StrUtil.formatData(traverse2);
		sftp.cd(formatData2);

		String month = new SimpleDateFormat("MM").format(Calendar.getInstance()
				.getTime());
		sftp.cd(month);
		String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar
				.getInstance().getTime());
		sftp.cd(date);

		String pwd3 = sftp.pwd();
		@SuppressWarnings("rawtypes")
		Vector vector3 = sftp.ls(pwd3);
		String condit = ivr.getFilename();
		String upperCase = condit.toUpperCase();
		String traverse3 = StrUtil.traverseFileName(vector3, "App" + upperCase,"IVRModule");
		System.out.println("traverse3="+traverse3);
		
		String pwd2 = sftp.pwd();
		LinuxUtil.disconnect();
		
		if(!traverse3.equals(" ") || !traverse3.equals(null)){
			String[] split = traverse3.split(":");
			String str = split[split.length - 1];
			String filename = str.substring(3, str.length());
			
			String replace = filename.replace(" ", "\\ ");
			System.out.println(replace);
			
			String replace2 = replace.replace("[", "\\[");
			String replace3 = replace2.replace("]", "\\]");
			result = pwd2 + SPRIT + replace3;
			return result;
		}else{
			return result;
		}
	}

}
