package com.itheima.web.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.SheetSettings;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.itheima.domain.Log;
import com.itheima.service.BatchQueryService;

public class BatchQueryServlet extends HttpServlet {
	private BatchQueryService bqs = new BatchQueryService();
	private Map<String, List<Log>> mapList = new HashMap<String, List<Log>>();
	private Log log;
	private Map map = new HashMap<String, List>();;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String operation = request.getParameter("operation");
		if (operation.equals("QueryBatchFunc")) {
			try {
				showLogInfo(request, response);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("对不起，服务器出现问题！" + e.getMessage());
			}
		} else if (operation.equals("exportexcle")) {
			exportexcle(request, response);
		}
	}

	public String parseFile(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		StringBuffer stringBuffer = new StringBuffer();
		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				DiskFileItemFactory dff = new DiskFileItemFactory();// 创建该对象
				dff.setSizeThreshold(1024000);// 指定在内存中缓存数据大小,单位为byte
				ServletFileUpload sfu = new ServletFileUpload(dff);// 创建该对象
				sfu.setFileSizeMax(1048576);// 指定单个上传文件的最大尺寸
				sfu.setSizeMax(1024000);// 指定一次上传多个文件的 总尺寸
				FileItemIterator fii = sfu.getItemIterator(request);// 解析request
																	// 请求,并返回FileItemIterator集合
				while (fii.hasNext()) {
					FileItemStream fis = fii.next();// 从集合中获得一个文件流
					if (!fis.isFormField() && fis.getName().length() > 0) {// 过滤掉表单中非文件
						BufferedInputStream in = new BufferedInputStream(
								fis.openStream());// 获得文件输入流
						BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(in));
						String lineTxt = null;
						while ((lineTxt = bufferedReader.readLine()) != null) {
							stringBuffer.append(lineTxt + "\n");
						}
					}
				}
				// response.getWriter().println(stringBuffer.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("对不起，服务器出现问题！" + e.getMessage());
		} finally {
			String str = stringBuffer.toString();
			return str;
		}
	}

	private void showLogInfo(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			Exception {
		// 解析文本
		String zj_str = parseFile(request, response);
		String pagenum = request.getParameter("pagenum");
		List<Log> list = new ArrayList<Log>();
		String str = request.getParameter("zj");
		StringBuffer sb = new StringBuffer();
		if (str.trim() == null || str.trim().equals("")) {
			sb.append(zj_str.trim());
		} else {
			sb.append(str.trim()).append("\n").append(zj_str.trim());
		}
		String sbStr = sb.toString();
		if (sbStr.contains("\n")) {
			String[] split = sbStr.split("\n");

			ArrayList<String> result = new ArrayList<String>();

			for (String s : split) {
				if (Collections.frequency(result, s) < 1) {
					result.add(s);
				}
			}
			for (int i = 0; i < result.size(); i++) {
				log = new Log();
				log.setCALLING_NBR(result.get(i));
				log.setSTART_DATETIME(request.getParameter("btime"));
				log.setEND_DATETIME(request.getParameter("etime"));
				list.add(log);
			}
		} else {
			log = new Log();
			log.setCALLING_NBR(request.getParameter("zj"));
			log.setSTART_DATETIME(request.getParameter("btime"));
			log.setEND_DATETIME(request.getParameter("etime"));
			list.add(log);
		}
		// 分页方法
		// Page page = bqs.findAllLogs(pagenum, list);
		// page.setUrl("/client/BatchQueryServlet?operation=QueryBatchFunc&zj="
		// + request.getParameter("zj") + "&btime="
		// + request.getParameter("btime") + "&etime="
		// + request.getParameter("etime"));

		UUID uuid = UUID.randomUUID();
		List<Log> logList = bqs.findAll(list);
		String uid = String.valueOf(uuid);
		mapList.put(uid, logList);
		request.setAttribute("uuid", uid);

		for (Log logInfo : logList) {
			List mapList = new ArrayList<>();
			for (Log loginfo : logList) {
				if ((logInfo.getUuid()).equals(loginfo.getUuid())) {
					mapList.add(loginfo);
				}
			}
			this.map.put(logInfo.getUuid(), mapList);
		}
		if (logList.size() == 0) {
			request.setAttribute("error_msg", "暂无数据,请稍后再查！！");
		} 
		request.setAttribute("logList", logList);
		request.setAttribute("logInfo", this.map);
		request.getRequestDispatcher("/batchQuery_reslut.jsp").forward(request,
				response);
	}

	public int exportToExcel(HttpServletResponse response, List<Log> objData,
			String sheetName, List<String> columns) {
		int flag = 0;
		// 声明工作簿jxl.write.WritableWorkbook
		WritableWorkbook wwb;
		try {
			// 根据传进来的file对象创建可写入的Excel工作薄
			OutputStream os = response.getOutputStream();
			wwb = Workbook.createWorkbook(os);
			/*
			 * 创建一个工作表、sheetName为工作表的名称、"0"为第一个工作表
			 * 打开Excel的时候会看到左下角默认有3个sheet、"sheet1、sheet2、sheet3"这样
			 * 代码中的"0"就是sheet1、其它的一一对应。 createSheet(sheetName,
			 * 0)一个是工作表的名称，另一个是工作表在工作薄中的位置
			 */
			WritableSheet ws = wwb.createSheet(sheetName, 0);
			SheetSettings ss = ws.getSettings();
			ss.setVerticalFreeze(1);// 冻结表头
			WritableFont font1 = new WritableFont(
					WritableFont.createFont("微软雅黑"), 10, WritableFont.BOLD);
			WritableFont font2 = new WritableFont(
					WritableFont.createFont("微软雅黑"), 9, WritableFont.NO_BOLD);
			WritableCellFormat wcf = new WritableCellFormat(font1);
			WritableCellFormat wcf2 = new WritableCellFormat(font2);
			WritableCellFormat wcf3 = new WritableCellFormat(font2);// 设置样式，字体
			// 创建单元格样式
			// WritableCellFormat wcf = new WritableCellFormat();
			// 背景颜色
			wcf.setBackground(jxl.format.Colour.YELLOW);
			wcf.setAlignment(Alignment.CENTRE); // 平行居中
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直居中
			wcf3.setAlignment(Alignment.CENTRE); // 平行居中
			wcf3.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直居中
			wcf3.setBackground(Colour.LIGHT_ORANGE);
			wcf2.setAlignment(Alignment.CENTRE); // 平行居中
			wcf2.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直居中
			wcf2.setWrap(true);
			wcf3.setWrap(true);
			for (int i = 0; i <= 14; i++) {
				ws.setColumnView(i, 25);
			}

			/*
			 * 这个是单元格内容居中显示 还有很多很多样式
			 */
			wcf.setAlignment(Alignment.CENTRE);
			// 判断一下表头数组是否有数据
			if (columns != null && columns.size() > 0) {
				// 循环写入表头
				for (int i = 0; i < columns.size(); i++) {
					/*
					 * 添加单元格(Cell)内容addCell() 添加Label对象Label()
					 * 数据的类型有很多种、在这里你需要什么类型就导入什么类型 如：jxl.write.DateTime
					 * 、jxl.write.Number、jxl.write.Label Label(i, 0, columns[i],
					 * wcf) 其中i为列、0为行、columns[i]为数据、wcf为样式
					 * 合起来就是说将columns[i]添加到第一行(行、列下标都是从0开始)第i列、样式为什么"色"内容居中
					 */
					ws.addCell(new Label(i, 0, columns.get(i), wcf));
				}
				// 判断表中是否有数据
				if (objData != null && objData.size() > 0) {
					// 循环写入表中数据
					int j = 0;
					int i = 0;

					for (Log log : objData) {
						String serviceId = log.getSERVICE_ID();
						if ((log.getRow_number().equals("1"))) {
							i++;
							ws.addCell(new Label(0, i, log.getSP_ID()));
							ws.addCell(new Label(1, i, log.getSERVICE_ID()));
							ws.addCell(new Label(2, i, log.getCALLING_NBR()));
							ws.addCell(new Label(3, i, log.getCALLED_NBR()));
							ws.addCell(new Label(4, i, String.valueOf(log
									.getTICKET_DURATION())));
							ws.addCell(new Label(5, i, String.valueOf(log
									.getBILLING_CHARGE())));
							ws.addCell(new Label(6, i, log.getSTART_DATETIME()));
							ws.addCell(new Label(7, i, log.getEND_DATETIME()));
							if (log.getDIGITS() != null) {
								ws.addCell(new Label(8, i, "按键"
										+ log.getDIGITS() + "[" + log.getTIME()
										+ "]"));
							} else {
								ws.addCell(new Label(8, i, "无按键信息"));
							}
							ws.addCell(new Label(9, i, (log
									.getEnterprise_code())));
							ws.addCell(new Label(10, i, log.getService_name()));
							ws.addCell(new Label(11, i, log.getCn_simple_name()));
							ws.addCell(new Label(12, i, log.getLINKMAN_TEL()));
							ws.addCell(new Label(13, i, log.getClass_name()));
							StringBuffer sb = new StringBuffer();

							if (log.getDIGITS() != null) {
								if (this.map != null) {
									Set set = this.map.keySet();
									for (Iterator<Object> ite = set.iterator(); ite
											.hasNext();) {
										String key = String.valueOf(ite.next());
										if (key.equals(log.getUuid())) {
											List value = (List) this.map
													.get(key);
											for (int k = 0; k < value.size(); k++) {
												Log obj = (Log) value.get(k);
												if (k <= 30) {
													sb.append("按键"
															+ obj.getDIGITS()
															+ "["
															+ obj.getTIME()
															+ "]" + "\n");
												} else if (k == 31) {
													sb.append("该用户按键数量过多，超过阀值，没有完全展示");
												}
											}
										}
									}
									ws.addCell(new Label(14, i, sb.toString()));
								}
							} else {
								ws.addCell(new Label(14, i, "无按键信息"));
							}
						}
					}
				} else {
					flag = -1;
				}
				// 写入Exel工作表
				wwb.write();
				// 关闭Excel工作薄对象
				wwb.close();
				// 关闭流
				os.flush();
				os.close();
				os = null;
			}
		} catch (IllegalStateException e) {
			System.err.println(e.getMessage());
			throw new RuntimeException("对不起，服务器出现问题" + e.getMessage());
		} catch (Exception ex) {
			flag = 0;
			ex.printStackTrace();
			throw new RuntimeException("对不起，服务器出现问题" + ex.getMessage());
		}
		return flag;
	}

	/**
	 * 下载excel
	 * 
	 * @author
	 * @param response
	 * @param filename
	 *            文件名 ,如:20110808.xls
	 * @param listData
	 *            数据源
	 * @param sheetName
	 *            表头名称
	 * @param columns
	 *            列名称集合,如：{物品名称，数量，单价}
	 */
	public void exportexcle(HttpServletRequest request,
			HttpServletResponse response) {
		String uid = request.getParameter("UUID");
		List<Log> list = this.mapList.get(uid);

		// 调用上面的方法、生成Excel文件
		response.setContentType("application/vnd.ms-excel");
		// response.setHeader("Content-Disposition",
		// "attachment;filename="+filename);
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// 设置日期格式
		// System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
		System.out.println("调用导出功能");
		String ctime = df.format(new Date());
		List columnsList = new ArrayList();

		columnsList.add("SP名称");
		columnsList.add("业务代码");
		columnsList.add("主叫");
		columnsList.add("被叫");
		columnsList.add("通话时长(单位:分钟)");
		columnsList.add("计费费用(单位:人民币元)");
		columnsList.add("通话开始时间");
		columnsList.add("通话结束时间");
		columnsList.add("按键/按键时间");
		columnsList.add("企业代码");
		columnsList.add("业务名称");
		columnsList.add("公司名称");
		columnsList.add("客服热线");
		columnsList.add("计费类型");
		columnsList.add("按键详情");
		try {
			response.setHeader("Content-Disposition", "attachment;filename="
					+ new String(ctime.getBytes("gb2312"), "ISO8859-1")
					+ ".xls");
			exportToExcel(response, list, "批量日志报表", columnsList);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("对不起，服务器出现问题" + e.getMessage());
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
