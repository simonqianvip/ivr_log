package com.itheima.domain;

public class Scu {
	private String caller;
	private String called;
	private String stime;
	private String etime;
	private String session;
	/**
	 * suc站点名
	 */
	private String scuname;
	private String spid;
	private String ip;	
	private String callstate;
	
	public String getCaller() {
		return caller;
	}
	public void setCaller(String caller) {
		this.caller = caller;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
//		String regString = "[0-9]+";
//		if(!session.equals(" ") || !session.equals(null)){
//			if (Pattern.matches(regString, session)){
//		    	String hexString = Long.toHexString(Long.parseLong(session));
//		    	String upperCase = hexString.toUpperCase();
//		    	this.session = upperCase;
//		    }else{
//		    	this.session = session;
//		    }
//		}else{
//			this.session = session;
//		}
		this.session = session;
	}
	public String getSpid() {
		return spid;
	}
	public void setSpid(String spid) {
		this.spid = spid;
	}
	public String getCalled() {
		return called;
	}
	public void setCalled(String called) {
		this.called = called;
	}
	public String getStime() {
		return stime;
	}
	public void setStime(String stime) {
		this.stime = stime;
	}
	public String getEtime() {
		return etime;
	}
	public void setEtime(String etime) {
		this.etime = etime;
	}
	public String getScuname() {
		return scuname;
	}
	public void setScuname(String scuname) {
		String lowerCase = scuname.toLowerCase();
		this.scuname = lowerCase;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getCallstate() {
		return callstate;
	}
	public void setCallstate(String callstate) {
		this.callstate = callstate;
	}
	@Override
	public String toString() {
		return "Scu [caller=" + caller + ", called=" + called + ", stime="
				+ stime + ", etime=" + etime + ", session=" + session
				+ ", scuname=" + scuname + ", spid=" + spid + ", ip=" + ip
				+ ", callstate=" + callstate + "]";
	}
	
	

}
