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
				throw new RuntimeException("�Բ��𣬷������������⣡" + e.getMessage());
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
				DiskFileItemFactory dff = new DiskFileItemFactory();// �����ö���
				dff.setSizeThreshold(1024000);// ָ�����ڴ��л������ݴ�С,��λΪbyte
				ServletFileUpload sfu = new ServletFileUpload(dff);// �����ö���
				sfu.setFileSizeMax(1048576);// ָ�������ϴ��ļ������ߴ�
				sfu.setSizeMax(1024000);// ָ��һ���ϴ�����ļ��� �ܳߴ�
				FileItemIterator fii = sfu.getItemIterator(request);// ����request
																	// ����,������FileItemIterator����
				while (fii.hasNext()) {
					FileItemStream fis = fii.next();// �Ӽ����л��һ���ļ���
					if (!fis.isFormField() && fis.getName().length() > 0) {// ���˵����з��ļ�
						BufferedInputStream in = new BufferedInputStream(
								fis.openStream());// ����ļ�������
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
			throw new RuntimeException("�Բ��𣬷������������⣡" + e.getMessage());
		} finally {
			String str = stringBuffer.toString();
			return str;
		}
	}

	private void showLogInfo(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			Exception {
		// �����ı�
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
		// ��ҳ����
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
			request.setAttribute("error_msg", "��������,���Ժ��ٲ飡��");
		} 
		request.setAttribute("logList", logList);
		request.setAttribute("logInfo", this.map);
		request.getRequestDispatcher("/batchQuery_reslut.jsp").forward(request,
				response);
	}

	public int exportToExcel(HttpServletResponse response, List<Log> objData,
			String sheetName, List<String> columns) {
		int flag = 0;
		// ����������jxl.write.WritableWorkbook
		WritableWorkbook wwb;
		try {
			// ���ݴ�������file���󴴽���д���Excel������
			OutputStream os = response.getOutputStream();
			wwb = Workbook.createWorkbook(os);
			/*
			 * ����һ��������sheetNameΪ����������ơ�"0"Ϊ��һ��������
			 * ��Excel��ʱ��ῴ�����½�Ĭ����3��sheet��"sheet1��sheet2��sheet3"����
			 * �����е�"0"����sheet1��������һһ��Ӧ�� createSheet(sheetName,
			 * 0)һ���ǹ���������ƣ���һ���ǹ������ڹ������е�λ��
			 */
			WritableSheet ws = wwb.createSheet(sheetName, 0);
			SheetSettings ss = ws.getSettings();
			ss.setVerticalFreeze(1);// �����ͷ
			WritableFont font1 = new WritableFont(
					WritableFont.createFont("΢���ź�"), 10, WritableFont.BOLD);
			WritableFont font2 = new WritableFont(
					WritableFont.createFont("΢���ź�"), 9, WritableFont.NO_BOLD);
			WritableCellFormat wcf = new WritableCellFormat(font1);
			WritableCellFormat wcf2 = new WritableCellFormat(font2);
			WritableCellFormat wcf3 = new WritableCellFormat(font2);// ������ʽ������
			// ������Ԫ����ʽ
			// WritableCellFormat wcf = new WritableCellFormat();
			// ������ɫ
			wcf.setBackground(jxl.format.Colour.YELLOW);
			wcf.setAlignment(Alignment.CENTRE); // ƽ�о���
			wcf.setVerticalAlignment(VerticalAlignment.CENTRE); // ��ֱ����
			wcf3.setAlignment(Alignment.CENTRE); // ƽ�о���
			wcf3.setVerticalAlignment(VerticalAlignment.CENTRE); // ��ֱ����
			wcf3.setBackground(Colour.LIGHT_ORANGE);
			wcf2.setAlignment(Alignment.CENTRE); // ƽ�о���
			wcf2.setVerticalAlignment(VerticalAlignment.CENTRE); // ��ֱ����
			wcf2.setWrap(true);
			wcf3.setWrap(true);
			for (int i = 0; i <= 14; i++) {
				ws.setColumnView(i, 25);
			}

			/*
			 * ����ǵ�Ԫ�����ݾ�����ʾ ���кܶ�ܶ���ʽ
			 */
			wcf.setAlignment(Alignment.CENTRE);
			// �ж�һ�±�ͷ�����Ƿ�������
			if (columns != null && columns.size() > 0) {
				// ѭ��д���ͷ
				for (int i = 0; i < columns.size(); i++) {
					/*
					 * ��ӵ�Ԫ��(Cell)����addCell() ���Label����Label()
					 * ���ݵ������кܶ��֡�����������Ҫʲô���;͵���ʲô���� �磺jxl.write.DateTime
					 * ��jxl.write.Number��jxl.write.Label Label(i, 0, columns[i],
					 * wcf) ����iΪ�С�0Ϊ�С�columns[i]Ϊ���ݡ�wcfΪ��ʽ
					 * ����������˵��columns[i]��ӵ���һ��(�С����±궼�Ǵ�0��ʼ)��i�С���ʽΪʲô"ɫ"���ݾ���
					 */
					ws.addCell(new Label(i, 0, columns.get(i), wcf));
				}
				// �жϱ����Ƿ�������
				if (objData != null && objData.size() > 0) {
					// ѭ��д���������
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
								ws.addCell(new Label(8, i, "����"
										+ log.getDIGITS() + "[" + log.getTIME()
										+ "]"));
							} else {
								ws.addCell(new Label(8, i, "�ް�����Ϣ"));
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
													sb.append("����"
															+ obj.getDIGITS()
															+ "["
															+ obj.getTIME()
															+ "]" + "\n");
												} else if (k == 31) {
													sb.append("���û������������࣬������ֵ��û����ȫչʾ");
												}
											}
										}
									}
									ws.addCell(new Label(14, i, sb.toString()));
								}
							} else {
								ws.addCell(new Label(14, i, "�ް�����Ϣ"));
							}
						}
					}
				} else {
					flag = -1;
				}
				// д��Exel������
				wwb.write();
				// �ر�Excel����������
				wwb.close();
				// �ر���
				os.flush();
				os.close();
				os = null;
			}
		} catch (IllegalStateException e) {
			System.err.println(e.getMessage());
			throw new RuntimeException("�Բ��𣬷�������������" + e.getMessage());
		} catch (Exception ex) {
			flag = 0;
			ex.printStackTrace();
			throw new RuntimeException("�Բ��𣬷�������������" + ex.getMessage());
		}
		return flag;
	}

	/**
	 * ����excel
	 * 
	 * @author
	 * @param response
	 * @param filename
	 *            �ļ��� ,��:20110808.xls
	 * @param listData
	 *            ����Դ
	 * @param sheetName
	 *            ��ͷ����
	 * @param columns
	 *            �����Ƽ���,�磺{��Ʒ���ƣ�����������}
	 */
	public void exportexcle(HttpServletRequest request,
			HttpServletResponse response) {
		String uid = request.getParameter("UUID");
		List<Log> list = this.mapList.get(uid);

		// ��������ķ���������Excel�ļ�
		response.setContentType("application/vnd.ms-excel");
		// response.setHeader("Content-Disposition",
		// "attachment;filename="+filename);
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");// �������ڸ�ʽ
		// System.out.println(df.format(new Date()));// new Date()Ϊ��ȡ��ǰϵͳʱ��
		System.out.println("���õ�������");
		String ctime = df.format(new Date());
		List columnsList = new ArrayList();

		columnsList.add("SP����");
		columnsList.add("ҵ�����");
		columnsList.add("����");
		columnsList.add("����");
		columnsList.add("ͨ��ʱ��(��λ:����)");
		columnsList.add("�Ʒѷ���(��λ:�����Ԫ)");
		columnsList.add("ͨ����ʼʱ��");
		columnsList.add("ͨ������ʱ��");
		columnsList.add("����/����ʱ��");
		columnsList.add("��ҵ����");
		columnsList.add("ҵ������");
		columnsList.add("��˾����");
		columnsList.add("�ͷ�����");
		columnsList.add("�Ʒ�����");
		columnsList.add("��������");
		try {
			response.setHeader("Content-Disposition", "attachment;filename="
					+ new String(ctime.getBytes("gb2312"), "ISO8859-1")
					+ ".xls");
			exportToExcel(response, list, "������־����", columnsList);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException("�Բ��𣬷�������������" + e.getMessage());
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
