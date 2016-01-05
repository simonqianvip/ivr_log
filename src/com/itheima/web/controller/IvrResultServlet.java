package com.itheima.web.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.startup.Tomcat.ExistingStandardWrapper;

import com.itheima.util.TimerUtil;

import nl.justobjects.pushlet.core.Event;
import nl.justobjects.pushlet.core.EventPullSource;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class IvrResultServlet extends HttpServlet {
	private static final long serialVersionUID = 5399192875308222882L;
	private IvrResultServlet ivrResultServlet = null;
	private static StringBuilder stringBuilder = new StringBuilder();
	private static final long finalTime = 10*60 * 1000;
	private String  charset = Charset.defaultCharset().toString();


	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.ivrResultServlet = new IvrResultServlet();

		@SuppressWarnings("unused")
		String caller = request.getParameter("caller");
		String findPath = request.getParameter("findPath");
		System.out.println("findPath==========" + findPath);

		String replace = findPath.replace(" ", "\\ ");
		String replace2 = replace.replace("[", "\\[");
		String parsePath = replace2.replace("]", "\\]");

		System.out.println("parsePath==========" + parsePath);

		String ip = request.getParameter("ip");
		String session = request.getParameter("session");
		System.out.println("session==========" + session);

		try {
			// tail -f pp.log|awk {'print $0 a'} a="$$"
			String cmd = "tail -f -n 2000 " + parsePath;
//					+ "| awk {\'print $0 a \'} a=\"pid=$$\" ";
			this.ivrResultServlet.remoteRunCmd(ip, cmd, session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 在指定机子上执行命令tail命令
	 * 
	 * @param hostname
	 * @param cmd
	 * @param session
	 * @return
	 */
	public void remoteRunCmd(String hostname, String cmd, String session) {
		String username = "yewu";
		String password = "Gaoyangsray";
		Connection conn = new Connection(hostname);
		Session sess = null;
		try {
			conn.connect();
			boolean isAuthenticated = conn.authenticateWithPassword(username,
					password);
			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");
			sess = conn.openSession();
			sess.requestPTY("vt100", 80, 24, 640, 480, null);
			sess.execCommand(cmd);
			System.out.println(cmd);
			long startTime = TimerUtil.getStartTime();
			InputStream stdout = sess.getStdout();

			BufferedReader br = new BufferedReader(
					new InputStreamReader(stdout),1024);
			
			while (true) {
				String line = br.readLine();
//				System.out.println("source========="+line);
				if (line == null) {
					break;
				}
//				System.out.println("有session"+session);
				
				String lowerCase = line.toLowerCase();
				String se = session.toLowerCase();
				
				if (lowerCase.contains(se)) {
					stringBuilder.append(line + "$");
					System.out.println("line ========== "+ line);
					if (line.contains("SessionTime")
							|| line.contains("ServiceTime")) {
						break;
					}
				}
				//记时，超过10分钟，也杀掉此命令
				long endTime = TimerUtil.getEndTime();
				long consumeTimer = TimerUtil.getConsumeTimer(startTime,
						endTime);
				if (consumeTimer > finalTime) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			sess.close();
			System.out.println("sess close******************");
			conn.close();
			System.out.println("conn close*********************");
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	static public class TestPullSource extends EventPullSource {
		public long getSleepTime() {
			return 1000;
		}

		public Event pullEvent() {
			Event event = Event.createDataEvent("/system/test");
			event.setField("log", "" + stringBuilder.toString());
			// System.out.println("log========"+event.getField("log"));
			stringBuilder.setLength(0);
			return event;
		}
	}

}
