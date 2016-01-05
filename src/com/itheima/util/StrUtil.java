package com.itheima.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.*;

import com.jcraft.jsch.ChannelSftp;

public class StrUtil {
	private static Log log = LogFactory.getLog(StrUtil.class);
	
	private long lastTimeFileSize = 0; // 
	/**
	 * 格式化linux里的文件名称
	 */
	public static String formatData(String name){
		String filename = null;
		if(!name.equals(" ") || !name.equals(null)){
			String[] splits = name.split(" ");
			filename = splits[splits.length-1];
		}
		return filename;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List traverse(Vector vector,String condition1,String stime) throws Exception{
		ArrayList list = new ArrayList();
		ArrayList<String> sortList = new ArrayList<String>();
		for(int i = 0;i < vector.size();i++){
			String file = String.valueOf(vector.get(i));
			if(file.contains(condition1)){
//				System.out.println("file==="+file);
				String file_date = "";
				if(file.contains(".gz")){
					file_date = file.substring(file.length()-17, file.length()-3);
//					System.out.println("file_date==="+file_date);
				}else{
					file_date = file.substring(file.length()-14, file.length());
//					System.out.println("file_date==="+file_date);
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
				long file_million_time = sdf.parse(file_date).getTime();
				long start_time = TimerUtil.getMillionSecondsTime(stime);
				//打开文件的时间大于开始时间
				if(file_million_time > start_time){
					String realFileName = getRealFileName(file);
					list.add(realFileName);
				}
			}
		} 
		//升序排序
		for(Object line:list){
			String str = String.valueOf(line);
			String[] split = str.split(".log_");
			sortList.add(split[split.length-1]);
		}
		Collections.sort(sortList);
		ArrayList<Object> arrayList2 = new ArrayList<>();
		for(Object obj:sortList){
			String str = String.valueOf(obj);
			for(int i =0;i<list.size();i++){
				if(((String) list.get(i)).contains(str)){
					arrayList2.add(list.get(i));
				}
			}
		}
		return arrayList2;
	}
	/**
	 * 根据条件找到匹配的文件
	 * @param vector
	 * @param condition
	 * @return
	 */
	public static String traverse(@SuppressWarnings("rawtypes") Vector vector,String condition){
		String result = "";
		@SuppressWarnings("rawtypes")
		ArrayList list = new ArrayList();
		for(int i = 0;i < vector.size();i++){
			String file = String.valueOf(vector.get(i));
			if(file.contains(condition)){
				list.add(file);
				if(list.size()>1){
					//取出最新的文件
					for(Object str:list){
						log.info("路径下的所有文件========="+String.valueOf(str));
					}
					String splitWord = sort(list);
					for(Object obj:list){
						String str = String.valueOf(obj);
						if(str.contains(splitWord)){
							result = str;
						}
					}
				}else{
					result = file;
				}
			}
		} 
		return result;
	}
	/**
	 * 根据条件返回满足条件的最新句子
	 * @param vector
	 * @param condition1
	 * @param condition2
	 * @return
	 */
	public static String traverseFileName(Vector vector,String condition1,String condition2){
		String result = "";
		ArrayList list = new ArrayList();
		for(int i = 0;i < vector.size();i++){
			String file = String.valueOf(vector.get(i));
			if(file.contains(condition1) && file.contains(condition2)){
				list.add(file);
				if(list.size()>1){
					//取出最新的文件
					for(Object str:list){
						System.out.println("路径下的所有文件========="+String.valueOf(str));
					}
					String sentence = sort(list);
					for(Object obj:list){
						String str = String.valueOf(obj);
						if(str.contains(sentence)){
							result = str;
						}
					}
				}else{
					result = file;
				}
			}
		} 
		return result;
	}
	
	/**
	 * 根据规则升序排序，返回集合
	 * @param vector
	 * @param condition1
	 * @param condition2
	 * @return
	 */
	public static List traverseFileName1(Vector vector,String condition1,String condition2){
		List result = new ArrayList<>();
		ArrayList list = new ArrayList();
		for(int i = 0;i < vector.size();i++){
			String file = String.valueOf(vector.get(i));
			if(file.contains(condition1) && file.contains(condition2)){
				list.add(file);
				if(list.size()>1){
					List newList = listSort(list);
					for(Object obj:list){
						String str = String.valueOf(obj);
						for(Object object:newList){
							if(str.contains(String.valueOf(object))){
								result.add(str);
							}
						}
					}
				}else{
					result = list;
				}
			}
		} 
		log.info("排序后的列表："+result);
		return result;
	}
	
	/**
	 * 对集合进行升序排列，返回最大值
	 * @param arrayList
	 * @return
	 */
	public static String sort(List arrayList){
		ArrayList<String> list = new ArrayList<String>();
		for(Object line:arrayList){
			String str = String.valueOf(line);
			String[] split = str.split(".log_");
			list.add(split[split.length-1]);
		}
		Collections.sort(list);
		
		System.out.println(list.get(list.size()-1));
		return list.get(list.size()-1);
	}
	
	
	/**
	 * 对集合进行升序排列，返回集合
	 * @param arrayList
	 * @return
	 */
	public static List listSort(List list){
		ArrayList<String> arrayList = new ArrayList<String>();
		for(Object line:list){
			String str = String.valueOf(line);
			String[] split = str.split(".log_");
			arrayList.add(split[split.length-1]);
		}
		Collections.sort(arrayList);
		log.info(arrayList);
		return arrayList;
	}
	
	
	public void realtimeShowLog(String filename) throws Exception {
		ChannelSftp sftp = LinuxUtil.connect("172.16.10.199");
		String stream2String = inputStream2String(sftp.get(filename));
		final RandomAccessFile randomFile = new RandomAccessFile(stream2String,
				"rw");
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
		exec.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				try {
					randomFile.seek(lastTimeFileSize);
					String tmp = "";
					while ((tmp = randomFile.readLine()) != null) {
						System.out.println(new String(tmp.getBytes("ISO8859-1")));
					}
					lastTimeFileSize = randomFile.length();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}
	
	
	/**
	 * 
	 * @param logFile
	 * @throws Exception
	 */

	public String inputStream2String(InputStream is) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line);
		}
		return buffer.toString();
	}
	
	/**
	 * 获取linux里文件名
	 * @param -rw-r--r--    1 yewu     yewu      3756331 Sep 20 21:59 AppSCU0011-01IVRModule.log_2015-09-20 21-4[001].log.20150920215959
	 * @return AppSCU0011-01IVRModule.log_2015-09-20\\ 21-4\\[001\\].log.20150920215959
	 * @param args
	 */
	public static String getRealFileName(String fileName){
		if(!fileName.equals(" ") || !fileName.equals(null)){
			String[] split = fileName.split(":");
			String str = split[split.length - 1];
			String filename = str.substring(3, str.length());
			
			String replace = filename.replace(" ", "\\ ");
			System.out.println(replace);
			
			String replace2 = replace.replace("[", "\\[");
			String replace3 = replace2.replace("]", "\\]");
			return replace3;
		}else{
			return fileName;
		}
	}
	
	public static void main(String[] args) {
		String name = "scu0067-02";
		String name1 = "ABDscu";
		String upperCase = name.toUpperCase();
		String lowerCase = name1.toLowerCase();
		System.out.println(upperCase);
		System.out.println(lowerCase);
	}
	

}
