package com.itheima.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OracleUtil {
	private static Connection con;
	private static final String USER ="PARTICULARS";
	private static final String PASSWORD ="jsnjivrparticulars";
	//集群方式连接
	private static final String URL="jdbc:oracle:thin:@(DESCRIPTION="
			+ "(LOAD_BALANCE=on)"
			+ "(ADDRESS=(PROTOCOL=TCP) (HOST=172.16.64.45)(PORT=1521))"
			+ "(CONNECT_DATA=(SERVICE_NAME=IVRLOG)) )";
	
	
	public static Connection getConnection(){
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			con= DriverManager.getConnection(URL,USER,PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return con;
	}
	
//	public static void main(String[] args) {
//		Connection connection = OracleUtil.getConnection();
//	}
	
	public static void relase(ResultSet rs,Statement stmt,Connection conn){
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			rs = null;
		}
		if(stmt!=null){
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			stmt = null;
		}
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			conn = null;
		}
	}

}
