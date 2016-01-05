package com.itheima.util;

import java.text.SimpleDateFormat;

public class TimerUtil {
	/**
	 * start time
	 * @return
	 */
	public static long getStartTime(){
		long currentTimeMillis = System.currentTimeMillis();
		return currentTimeMillis;
	}
	/**
	 * end time
	 * @return
	 */
	public static long getEndTime(){
		long currentTimeMillis = System.currentTimeMillis();
		return currentTimeMillis;
	}
	/**
	 * process time
	 * @param start
	 * @param end
	 * @return
	 */
	public static long getConsumeTimer(long start,long end){
		long consumeTime= end-start;
		return consumeTime;
	}
	
	/**
	 * 时间转换成毫秒
	 * @param time
	 * @return
	 * @throws Exception
	 */
	public static long getMillionSecondsTime(String time) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		long millionSeconds = sdf.parse(time).getTime();//毫秒
		return millionSeconds;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		long time = getMillionSecondsTime("2015-09-12 16:12:13");
		System.out.println(time);
	}

}
