package com.hcop.otn.common.db.oms;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class BaseDBConnection implements Connection {
	private static String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";

	private Connection _conn;
	private ConnectionPoolImpl _pool;
	
	BaseDBConnection(Connection conn, ConnectionPoolImpl pool) {
		_conn = conn;
		_pool = pool;
	}
	
	
	BaseDBConnection(String host, int port, String sid, String user, String passwd, ConnectionPoolImpl pool) throws SQLException, ClassNotFoundException {
		Class.forName(ORACLE_DRIVER);
		String url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;
		_conn = DriverManager.getConnection(url, user, passwd);
		_pool = pool;
	}
	
	
	void purge() throws SQLException {
		_conn.close();
		_conn = null;
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return _conn.isWrapperFor(arg0);
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return _conn.unwrap(arg0);
	}

	@Override
	public void clearWarnings() throws SQLException {
		_conn.clearWarnings();
	}

	@Override
	public void close() throws SQLException {
		if (_pool != null) {
			_pool.recycleConnection(this);
		}
	}

	@Override
	public void commit() throws SQLException {
		_conn.commit();
	}

	@Override
	public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
		return _conn.createArrayOf(arg0, arg1);
	}

	@Override
	public Blob createBlob() throws SQLException {
		return _conn.createBlob();
	}

	@Override
	public Clob createClob() throws SQLException {
		return _conn.createClob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return _conn.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return _conn.createSQLXML();
	}

	@Override
	public Statement createStatement() throws SQLException {
		return _conn.createStatement();
	}

	@Override
	public Statement createStatement(int arg0, int arg1) throws SQLException {
		return _conn.createStatement(arg0, arg1);
	}

	@Override
	public Statement createStatement(int arg0, int arg1, int arg2) throws SQLException {
		return _conn.createStatement(arg0, arg1, arg2);
	}

	@Override
	public Struct createStruct(String arg0, Object[] arg1) throws SQLException {
		return _conn.createStruct(arg0, arg1);
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return _conn.getAutoCommit();
	}

	@Override
	public String getCatalog() throws SQLException {
		return _conn.getCatalog();
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return _conn.getClientInfo();
	}

	@Override
	public String getClientInfo(String arg0) throws SQLException {
		return _conn.getClientInfo(arg0);
	}

	@Override
	public int getHoldability() throws SQLException {
		return _conn.getHoldability();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return _conn.getMetaData();
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return _conn.getTransactionIsolation();
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return _conn.getTypeMap();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return _conn.getWarnings();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return _conn.isClosed();
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return _conn.isReadOnly();
	}

	@Override
	public boolean isValid(int arg0) throws SQLException {
		return _conn.isValid(arg0);
	}

	@Override
	public String nativeSQL(String arg0) throws SQLException {
		return _conn.nativeSQL(arg0);
	}

	@Override
	public CallableStatement prepareCall(String arg0) throws SQLException {
		return _conn.prepareCall(arg0);
	}

	@Override
	public CallableStatement prepareCall(String arg0, int arg1, int arg2) throws SQLException {
		return _conn.prepareCall(arg0, arg1, arg2);
	}

	@Override
	public CallableStatement prepareCall(String arg0, int arg1, int arg2, int arg3) throws SQLException {
		return _conn.prepareCall(arg0, arg1, arg2, arg3);
	}

	@Override
	public PreparedStatement prepareStatement(String arg0) throws SQLException {
		return _conn.prepareStatement(arg0);
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1) throws SQLException {
		return _conn.prepareStatement(arg0, arg1);
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int[] arg1) throws SQLException {
		return _conn.prepareStatement(arg0, arg1);
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, String[] arg1) throws SQLException {
		return _conn.prepareStatement(arg0, arg1);
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2) throws SQLException {
		return _conn.prepareStatement(arg0, arg1, arg2);
	}

	@Override
	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2, int arg3) throws SQLException {
		return _conn.prepareStatement(arg0, arg1, arg2, arg3);
	}

	@Override
	public void releaseSavepoint(Savepoint arg0) throws SQLException {
		_conn.releaseSavepoint(arg0);
	}

	@Override
	public void rollback() throws SQLException {
		_conn.rollback();
	}

	@Override
	public void rollback(Savepoint arg0) throws SQLException {
		_conn.rollback(arg0);
	}

	@Override
	public void setAutoCommit(boolean arg0) throws SQLException {
		_conn.setAutoCommit(arg0);
	}

	@Override
	public void setCatalog(String arg0) throws SQLException {
		_conn.setCatalog(arg0);
	}

	@Override
	public void setClientInfo(Properties arg0) throws SQLClientInfoException {
		_conn.setClientInfo(arg0);
	}

	@Override
	public void setClientInfo(String arg0, String arg1) throws SQLClientInfoException {
		_conn.setClientInfo(arg0, arg1);
	}

	@Override
	public void setHoldability(int arg0) throws SQLException {
		_conn.setHoldability(arg0);
	}

	@Override
	public void setReadOnly(boolean arg0) throws SQLException {
		_conn.setReadOnly(arg0);
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return _conn.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String arg0) throws SQLException {
		return _conn.setSavepoint(arg0);
	}

	@Override
	public void setTransactionIsolation(int arg0) throws SQLException {
		_conn.setTransactionIsolation(arg0);
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
		_conn.setTypeMap(arg0);
	}

	public void setSchema(String schema) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public String getSchema() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void abort(Executor executor) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNetworkTimeout(Executor executor, int milliseconds)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public int getNetworkTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

}
