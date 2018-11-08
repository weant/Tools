package com.otn.tool.common.db.oms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConnectionPoolImpl implements ConnectionPool {
	Log log = LogFactory.getLog(ConnectionPoolImpl.class);
	private LinkedList<UTPDBConnection> notUsedConnection = new LinkedList<UTPDBConnection>();
	private HashSet<UTPDBConnection> usedConnection = new HashSet<UTPDBConnection>();
	
	private String _host;
	private String _ip;
	private int _port;
	private String _sid;
	private String _user;
	private String _passwd;
	private String _url;
	
	public ConnectionPoolImpl(String host, String ip, int port, String sid, String user, String passwd) {
		_host = host;
		_ip = ip;
		_port = port;
		_sid = sid;
		_user = user;
		_passwd = passwd;
	}
	
	public ConnectionPoolImpl(String url, String user, String passwd) {
		_url = url;
		_user = user;
		_passwd = passwd;
	}
	
	/* sqlite */
	public ConnectionPoolImpl(String url){
		_url = url;
	}
	
	public synchronized Connection getConnection()
	{
		
		DriverManager.setLoginTimeout(5);
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			log.error("can't find class oracle.jdbc.OracleDriver", e);
			return null;
		}
		Connection conn = null;
		if (_host.length() != 0) {
			String url = "jdbc:oracle:thin:@" + _host + ":" + _port + ":" + _sid;
			try {
				conn = DriverManager.getConnection(url, _user, _passwd);
			} catch (Exception e) {
				log.debug("DriverManager.getConnection failed! with host "+_host);
				if (_ip.length() != 0) {
					url = "jdbc:oracle:thin:@" + _ip + ":" + _port + ":" + _sid;
					try {
						conn = DriverManager.getConnection(url, _user, _passwd);
						log.debug("DriverManager.getConnection failed! with ip "+_ip);
					} catch (Exception e1) {
						log.error("can't connect to DB : " + _host + ":" + _ip + ":" + _port + ":" + _sid, e1);
					}
				} else {
					log.error("can't connect to DB : " + _host + ":" + _port + ":" + _sid, e);
				}
			}
		}
		
		return conn;
	}
	
	synchronized Connection getEmlConnection()
	{

		if(_sid!=null){//it's oracle
			return getConnection();
		}
		
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			log.error("can't find class com.mysql.jdbc.Driver", e);
			return null;
		}
		Connection conn;
		try {
			conn = DriverManager.getConnection(_url, _user, _passwd);
			return conn;
		} catch (SQLException e) {
			log.error("can't connect mysql DB.", e);
			return null;
		}
	}
	
	
	
	
	synchronized void recycleConnection(UTPDBConnection conn) {
		boolean exist = usedConnection.remove(conn);
		if (exist) {
			notUsedConnection.addLast(conn);
		}
	}
}
