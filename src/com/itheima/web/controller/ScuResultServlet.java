package com.itheima.web.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

import com.itheima.domain.Scu;
import com.itheima.service.ScuSearchService;
import com.itheima.util.LinuxUtil;
import com.itheima.util.StrUtil;
import com.itheima.util.TimerUtil;
import com.jcraft.jsch.ChannelSftp;

public class ScuResultServlet extends HttpServlet {
	private static final long serialVersionUID = 8750962071917671879L;
	private Log log = LogFactory.getLog(ScuResultServlet.class);
	
	private ScuSearchService service = new ScuSearchService();
	private static final String APP = "app";
	private static final String LOG = "log";
	private static final String SCU = "SCU";
	private static final String BACKUP_IP = "172.16.0.132";
	
	private StringBuilder stringBuilder;
	/**
	 * 斜杠
	 */
	private static final String SPRIT = "/";
	

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String operation = request.getParameter("operation");
		
		String stime = request.getParameter("startTime");
		if("showLog".equals(operation)){
			stringBuilder = new StringBuilder();
			Scu scu = new Scu();
			scu.setSpid(request.getParameter("spid"));
			scu.setScuname(request.getParameter("scuname"));
			scu.setSession(request.getParameter("session"));
			scu.setIp(request.getParameter("ip"));
			scu.setStime(request.getParameter("startTime"));
			scu.setCaller(request.getParameter("caller"));
			scu.setCalled(request.getParameter("called"));
			log.info("显示查询的日志");
			showLog(request, response, stime, scu);
		}else if("exportexcle".equals(operation)){
			String caller = request.getParameter("caller");
			String called = request.getParameter("called");
			String startTime = request.getParameter("startTime");
			log.info("导出到excel");
			downLoad2TXT(response,caller,called,startTime);
		}
	}
	/**
	 * show log
	 */
	public void showLog(HttpServletRequest request,
			HttpServletResponse response, String stime, Scu scu) {
		long startTime = 0;
		try {
			startTime = TimerUtil.getMillionSecondsTime(stime);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		long currentTimeMillis = System.currentTimeMillis();
		//如果开始时间大于70分钟，直接到备份机子去查日志，否则，备份机子和日志机子都查
		if((currentTimeMillis-startTime)>= 70*60*1000){
			// TODO 查备份机子
			try {
				getLogInfo(scu);
				if(stringBuilder.length()==0){
					request.setAttribute("scu_msg", " Don't find the file path");
				}
				setAttribute(request, response, scu);
				
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		}else{
			request.setAttribute("scu_msg", " Don't find the file path");
			try {
				request.getRequestDispatcher("/scu_result.jsp").forward(
						request, response);
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//TODO 代码已编写完，待测试
//			try {
//				//TODO 查产生日志的机子
//				@SuppressWarnings("rawtypes")
//				List fd = findPath(scu);
//				for(Object str:fd){
//					String s = String.valueOf(str);
//					log.info(s);
//					String cat_cmd = "cat "+s+" | grep -i "+scu.getSession();
//					boolean flag = remoteRunCmd(scu.getIp(), "",cat_cmd,"", scu.getSession());
//					if(flag == true){
//						break;
//					}
//				}
//				// TODO 如果客户端机子没有发现日志结尾则到备份服务器上去查
//				String sb = stringBuilder.toString();
//				if(!sb.contains("SessionTime") || !sb.contains("ServiceTime")){
//					getLogInfo(scu);
//				}
//				setAttribute(request, response, scu);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}
	}
	/**
	 * 在备份服务器上获取日志信息
	 * @param scu
	 * @throws Exception
	 */
	
	private void getLogInfo(Scu scu) throws Exception {
		@SuppressWarnings("rawtypes")
		List pathList = findBackupPath(scu);
//		log.info(pathList);
		String gunzip_cmd ="";
		String del_cmd = "";
		String cat_cmd = "";
		if(pathList!=null){
			for(int i =0;i<pathList.size();i++){
				String fileName = (String) pathList.get(i);
				log.info(fileName);
				//如果是压缩文件先解压
				if(fileName.contains(".gz")){
					gunzip_cmd = "gunzip -c "+fileName+" > "+fileName.substring(0, fileName.length()-3);
					del_cmd = "rm -rf "+fileName.substring(0, fileName.length()-3);
					cat_cmd = "cat "+fileName.substring(0, fileName.length()-3)+" | grep -i "+scu.getSession();
				}else{
					cat_cmd = "cat "+fileName+" | grep -i "+scu.getSession();
				}
				boolean flag = remoteRunCmd(BACKUP_IP, gunzip_cmd,cat_cmd,del_cmd, scu.getSession());
				if(flag == true){
					break;
				}
			}
		}
	}
	/**
	 * 设置值给结果页面
	 * @param request
	 * @param response
	 * @param scu
	 * @throws ServletException
	 * @throws IOException
	 */
	private void setAttribute(HttpServletRequest request,
			HttpServletResponse response, Scu scu) throws ServletException,
			IOException {
		request.setAttribute("scuLog", stringBuilder);
		request.setAttribute("caller", scu.getCaller());
		request.setAttribute("called", scu.getCalled());
		request.setAttribute("stime", scu.getStime());
		request.getRequestDispatcher("/scu_result.jsp").forward(
				request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	/*
	 * 登陆linux机子，执行命令
	 */
	public boolean remoteRunCmd(String hostname, String tar_cmd,String cat_cmd,String del_cmd, String session) {
		boolean flag = false;
		String username = "yewu";
		String password = "Gaoyangsray";
		Connection conn = new Connection(hostname);
		Session sess = null;
		try {
			conn.connect();
			log.info("*********************conn close*********************");
			boolean isAuthenticated = conn.authenticateWithPassword(username,
					password);
			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");
			//解压
			if(tar_cmd.trim().length()!=0){
				sess = conn.openSession();
				log.info("******************sess open******************");
				sess.requestPTY("vt100", 80, 24, 640, 480, null);
				sess.execCommand(tar_cmd);
				log.info(tar_cmd);
				//线程睡眠2秒钟
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				sess.close();
				log.info("******************sess close******************");
			}
			
			//获取需要的数据
			sess = conn.openSession();
			log.info("******************sess open******************");
			sess.requestPTY("vt100", 80, 24, 640, 480, null);
			sess.execCommand(cat_cmd);
			log.info(cat_cmd);
			InputStream stdout = sess.getStdout();

			BufferedReader br = new BufferedReader(
					new InputStreamReader(stdout),1024);
			
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				String lowerCase = line.toLowerCase();
				String se = session.toLowerCase();
				if (lowerCase.contains(se)) {
					stringBuilder.append(line + "$");
//					log.info("line ========== "+ line);
					if (line.contains("SessionTime")
							|| line.contains("ServiceTime")) {
						flag = true;
						break;
					}
				}
			}
			sess.close();
			log.info("******************sess close******************");
			
			//删除
			if(del_cmd.trim().length()!=0){
				sess = conn.openSession();
				log.info("******************sess open******************");
				sess.requestPTY("vt100", 80, 24, 640, 480, null);
				sess.execCommand(del_cmd);
				log.info(del_cmd);
				sess.close();
				log.info("******************sess close******************");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			conn.close();
			log.info("*********************conn close*********************");
		}
		return flag;
	}
	
	/**
	 * 查找备份机子上的日志
	 * @param scu
	 * @return
	 * @throws Exception 
	 */
	public List findBackupPath(Scu scu) throws Exception{
		try {
			List list = new ArrayList<>();
			ChannelSftp sftp = LinuxUtil.connect(BACKUP_IP);
			String stime = scu.getStime();
			String format_stime = stime.substring(0, 10);
			String first_path = SPRIT+APP+SPRIT+LOG+SPRIT+SCU+SPRIT+scu.getSpid()+SPRIT+scu.getScuname()+SPRIT+format_stime;
			sftp.cd(first_path);
			Vector vector = sftp.ls(first_path);
			List file_name_list = StrUtil.traverse(vector, "IVRModule", scu.getStime());
			for(int i=0;i<file_name_list.size();i++){
				String fileName = (String) file_name_list.get(i);
				list.add(first_path+SPRIT+fileName);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 查找号码对应的日志文件
	 * @param ivr
	 * @return
	 * @throws Exception
	 * @author simon
	 */
	public List findPath(Scu scu) throws Exception {
		List<String> list = new ArrayList<String>();
		String result = null;
		ChannelSftp sftp = LinuxUtil.connect(scu.getIp());
		//一级目录
		sftp.cd(SPRIT+APP);
		@SuppressWarnings("rawtypes")
		Vector vector = sftp.ls(SPRIT+APP);
		String traverse = StrUtil.traverse(vector, scu.getSpid());
		String formatData = StrUtil.formatData(traverse);
		String lowerCase = scu.getScuname().toLowerCase();
		//二、三、四级目录
		sftp.cd(formatData + SPRIT + lowerCase + SPRIT+LOG);

		String pwd = sftp.pwd();
		@SuppressWarnings("rawtypes")
		Vector vector2 = sftp.ls(pwd);
		String traverse2 = StrUtil.traverse(vector2, "logfiles");
		String formatData2 = StrUtil.formatData(traverse2);
		//五级目录
		sftp.cd(formatData2);

		String month = new SimpleDateFormat("MM").format(Calendar.getInstance()
				.getTime());
		//六级目录
		sftp.cd(month);
		String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar
				.getInstance().getTime());
		//七级目录
		sftp.cd(date);

		String pwd3 = sftp.pwd();
		@SuppressWarnings("rawtypes")
		Vector vector3 = sftp.ls(pwd3);
		String condit = scu.getScuname();
		String upperCase = condit.toUpperCase();
//		String traverse3 = StrUtil.traverseFileName(vector3, "App" + upperCase,"IVRModule");
		List sortList = StrUtil.traverseFileName1(vector3, "App" + upperCase,"IVRModule");
		
		String pwd2 = sftp.pwd();
		LinuxUtil.disconnect();
		
		if(sortList.size()!=0){
			for(Object obj:sortList){
				String fileName = String.valueOf(obj);
				String[] split = fileName.split(":");
				String str = split[split.length - 1];
				String filename = str.substring(3, str.length());
				
				String replace = filename.replace(" ", "\\ ");
				log.info(replace);
				
				String replace2 = replace.replace("[", "\\[");
				String replace3 = replace2.replace("]", "\\]");
				result = pwd2 + SPRIT + replace3;
				list.add(result);
			}
			return list;
		}else{
			return list;
		}
	}
	
	/**
	 * 下载txt文件
	 * @param response
	 * @param caller
	 * @param called
	 * @param date
	 */
	public void downLoad2TXT(HttpServletResponse response, String caller,String called,String date) {  
		  
        response.setContentType("text/plain");
        response.addHeader("Content-Disposition",  
                "attachment;filename="+caller+"_"+called+"_"+date+".txt");// filename指定默认的名字  
        BufferedOutputStream buff = null;  
        StringBuffer buffer = new StringBuffer();   
        String enter = "\r\n";  
         
        ServletOutputStream outSTr = null;  
        try {  
            outSTr = response.getOutputStream();// 建立  
            buff = new BufferedOutputStream(outSTr);  
            String string = stringBuilder.toString();
            
            if(string != null && string.trim().length()!=0){
            	//$美元符号需要转义
            	String[] splits = string.split("\\u0024");
            	log.info(splits.length);
            	for(int i =0;i<splits.length;i++){
            		buffer.append(splits[i]+enter);
            	}
            }
            buff.write(buffer.toString().getBytes("UTF-8"));  
            buff.flush();  
            buff.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                buff.close();  
                outSTr.close();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}
