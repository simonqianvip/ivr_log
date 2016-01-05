package com.itheima.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.itheima.dao.ScuSearchDao;
import com.itheima.domain.Scu;
import com.itheima.util.OracleUtil;

public class ScuSearchDaoImpl implements ScuSearchDao {

	@Override
	public List<Scu> findInfo(String caller,String called,String stime,String etime,String meeting) {
		Connection con = null;
		ResultSet set = null;
		Statement statement = null;
		List<Scu> list = new ArrayList<Scu>();
		Scu scu = null;
		try {
			con = OracleUtil.getConnection();
			statement = con.createStatement();
			String sql = " select caller,called,begintime,endtime,callstate,b.ip,b.name,b.spid,b.sessionid "
					+ "FROM PARTICULARS.IVRLOG_CALLDETAIL_hour a, PARTICULARS.IVRLOG_SCUINFO b "
					+ "WHERE a.uuid = B.UUID(+) and caller = '"+caller+"' ";
			if(called != ""){
				sql += "and called = '"+called+"' ";
			}
			if(etime != ""){
				sql +="and BEGINTIME >= to_date('"+stime+"','yyyy-mm-dd hh24:mi:ss') and BEGINTIME <= to_date('"+etime+"','yyyy-mm-dd hh24:mi:ss')+1 ";
			}else{
				sql +="and BEGINTIME >= to_date('"+stime+"','yyyy-mm-dd hh24:mi:ss') and BEGINTIME <= to_date('"+stime+"','yyyy-mm-dd hh24:mi:ss')+1 ";
			}
			sql +="order by begintime asc";
			set = statement.executeQuery(sql);
			System.out.println(sql);
			while(set.next()){
				scu = new Scu();
				scu.setCaller(set.getString("caller"));
				scu.setCalled(set.getString("called"));
				scu.setIp(set.getString("ip"));
				scu.setSession(set.getString("sessionid"));
				scu.setScuname(set.getString("name"));
				scu.setSpid(meeting.trim().length()==0?set.getString("spid"):meeting);
				scu.setCallstate(set.getString("callstate"));
				scu.setStime(set.getString("begintime"));
				scu.setEtime(set.getString("endtime"));
				scu.toString();
				list.add(scu);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			OracleUtil.relase(set, statement, con);
		}
		return list;
	}

	@Override
	public boolean isExist(String caller,String stime,String etime) {
		Connection con = null;
		ResultSet set = null;
		Statement statement = null;
		int count = 0;
		boolean flag = false;
		try {
			con = OracleUtil.getConnection();
			statement = con.createStatement();
			String sql = "select count(*) from PARTICULARS.IVRLOG_CALLDETAIL_HOUR where caller = '"+caller+"' ";
			if(etime != ""){
				sql +="and BEGINTIME >= to_date('"+stime+"','yyyy-mm-dd hh24:mi:ss') and BEGINTIME <= to_date('"+etime+"','yyyy-mm-dd hh24:mi:ss')+1";
			}else{
				sql +="and BEGINTIME >= to_date('"+stime+"','yyyy-mm-dd hh24:mi:ss') and BEGINTIME <= to_date('"+stime+"','yyyy-mm-dd hh24:mi:ss')+1";
			}
			set = statement.executeQuery(sql);
			System.out.println(sql);
			while(set.next()){
				count = set.getInt(1);
			}
			if(count != 0){
				flag = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			OracleUtil.relase(set, statement, con);
		}
		return flag;
	}

//	public static void main(String[] args) {
//		new ScuSearchDaoImpl().isExist("18228163613", "2015-09-23", "");
//	}

}
