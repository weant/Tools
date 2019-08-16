package com.hcop.ptn.common.db.dbrunner;

import com.hcop.ptn.common.db.DbManager;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.LazyDynaMap;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 
 * JDBC辅助工具类.
 * 包含了查询结果自动封装成bean的查询方法
 * 支持批量执行sql以及事务
 *
 */
public class DbRunner {
	
	
	
	

	/**
	 * connection manager
	 */
	private DbManager dbm;
	
	/**
	 * 
	 * reference to QueryRunner
	 */
	private QueryRunner delegate = new QueryRunner();
	
	public DbRunner(DbManager dbm){
		this.dbm = dbm;
	}
	
	public void setDBMgr(DbManager dbm){
		this.dbm = dbm;
	}
	
	
	
	/**
	 * 执行sql查询.结果应为一个集合数据.resultSet的每行记录将会被转换为beanClazz所指定的类型的对象
	 * @param <T>
	 * @param sql      查询sql
	 * @param beanClazz 查询结果用于转换成的POJO类型
	 * @param params    preparedStatment语句中的参数值
	 * @return
	 * @throws SQLException
	 */
	public <T> Collection<T> queryBeanCollection(String sql,Class<T> beanClazz, Object ... params) throws SQLException{
		Connection conn = getConnection();
		
		 Collection<T> result = null;
		 ResultSetHandler handler = RSHandlerCreator.createHandlerBy(RSHandlerCreator.HandlerType.StaticClassList, beanClazz);
		try{
			if(params == null|| params.length == 0){
				result = (Collection<T>)delegate.query(conn, sql, handler);
			}else{
				result = (Collection<T>)delegate.query(conn, sql, handler,params);
				
			}
				}catch(SQLException e){
			throw e;
		}finally{
			dbm.closeQuietly(conn, null, null);
		}
		
		return result;
	}
	
	
	
	/**
	 * 执行sql查询.结果应为一条记录.该记录将会被转换为beanClazz指定的类型的对象
	 * @param <T>
	 * @param sql
	 * @param beanClazz
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public <T> T queryBean(String sql, Class<T> beanClazz, Object ... params) throws SQLException{
		Connection conn = getConnection();
		 ResultSetHandler handler = RSHandlerCreator.createHandlerBy(RSHandlerCreator.HandlerType.StaticClass, beanClazz);
		T bean = null;
		try{
			bean = (T)delegate.query(conn, sql,handler, params);
		}catch(SQLException e){
			throw e;
		}finally{
			dbm.closeQuietly(conn, null, null);
		}
		
		return bean;
		 
	}
	
	
	public DynaBean queryAsDynaBean(String sql, DynaClass type, Object ...params) throws SQLException{
		Connection conn = getConnection();
		DynaBean bean = null;
		 ResultSetHandler handler = RSHandlerCreator.createHandlerBy(RSHandlerCreator.HandlerType.DynaClass, type);
		try{
			if(params == null|| params.length == 0){
				bean = (DynaBean)delegate.query(conn, sql, handler);
			}else{
				bean =  (DynaBean)delegate.query(conn, sql, handler, params);
			}
			}catch(SQLException e){
			throw e;
		}finally{
			dbm.closeQuietly(conn, null, null);
		}
		
		return bean;
	}
	
	
	
	public List<DynaBean> queryAsDynaBeanCollection(String sql, DynaClass type, Object ...params)throws SQLException{
		Connection conn = getConnection();
		List<DynaBean> beans = null;
		 ResultSetHandler handler = RSHandlerCreator.createHandlerBy(RSHandlerCreator.HandlerType.DynaClassList, type);
		try{
			if(params == null|| params.length == 0){
				beans = (List<DynaBean>)delegate.query(conn, sql, handler);
			}else{
				beans =  (List<DynaBean>)delegate.query(conn, sql, handler, params);
			}
			}catch(SQLException e){
			throw e;
		}finally{
			dbm.closeQuietly(conn, null, null);
		}
		
		return beans;
	}
	/**
	 * 
	 * 根据传入的sql和params进行查询，并把查询结果包装成DynaBean返回.
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public DynaBean queryDynaBean(String sql, Object ...params ) throws SQLException{
		
		Map<String,Object> map = queryMap(sql, params);
		DynaBean bean = new LazyDynaMap(map);
		return bean;
		
	}
	
	/**
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public Collection<DynaBean> queryDynaBeans(String sql, Object ...params) throws SQLException{
		List<Map<String,Object>> mapList = queryMapList(sql, params);
		List<DynaBean> beans = new ArrayList<DynaBean>();
		for(Map<String,Object> map: mapList){
			DynaBean bean = new LazyDynaMap(map);
			beans.add(bean);
		}
		
		return beans;
	}
	
	/**
	 * 执行sql查询,结果为一条记录.该记录被存储到Map中.key为sql语句中column的别名,值为列值,类型与数据库驱动有关
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public Map<String,Object> queryMap(String sql, Object ... params) throws SQLException{
		Connection conn = getConnection();
		Map<String,Object> result = null;
		 ResultSetHandler handler = RSHandlerCreator.createHandlerBy(RSHandlerCreator.HandlerType.Map, null);
		try{
			if(params == null){
				result = (Map<String,Object>)delegate.query(conn, sql, handler);
			}else{
				result = (Map<String,Object>)delegate.query(conn, sql, handler, params);
			}

		}catch(SQLException e){
			throw e;
		}finally{
			dbm.closeQuietly(conn, null, null);
		}
		return result;
	}
	
	
	/**
	 * 执行sql查询,结果为多条记录.每一条记录被存储到Map中.key为sql语句中column的别名,值为列值,类型与数据库驱动有关
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String,Object>> queryMapList(String sql, Object ... params) throws SQLException{
		Connection conn = getConnection();
		List<Map<String,Object>> result = null;
		 ResultSetHandler handler = RSHandlerCreator.createHandlerBy(RSHandlerCreator.HandlerType.MapList, null);
		try{
			if(params == null){
				result = (List<Map<String,Object>>)delegate.query(conn, sql, handler);
			}else{
				result = (List<Map<String,Object>>)delegate.query(conn, sql, handler, params);
			}
		}catch(SQLException e){
			throw e;
		}finally{
			dbm.closeQuietly(conn, null, null);
		}
		return result;
	}
	
	
	/**
	 * 执行插入或 修改或 删除操作. 返回的是受影响的行数
	 * @param sql
	 * @param params
	 * @return The number of rows updated.
	 * @throws SQLException
	 */
	public int update(String sql, Object ... params) throws SQLException{
		Connection conn = getConnection();
		try{
			return delegate.update(conn, sql, params);
		}catch(SQLException e){
			throw e;
		}finally{
			dbm.closeQuietly(conn, null, null);
		}
		
	}
	
	
	/**
	 * 
	 * 执行批量的插入 修改 删除 操作.
	 * @param sql
	 * @param params
	 * @return  返回每条语句影响的行数
	 * @throws SQLException
	 */
	public int[] batch(String sql, Object[][] params) throws SQLException{
		Connection conn = getConnection();
		boolean auto = conn.getAutoCommit();
		conn.setAutoCommit(false);
		try{
			int[] rows =  delegate.batch(conn, sql, params);
			conn.commit();
			return rows;
		}catch(SQLException e){
			conn.rollback();
			throw e;
		}finally{
			
			conn.setAutoCommit(auto);
			dbm.closeQuietly(conn, null, null);
		}
		
	}
	
	
	
	/**
	 * 执行一个事务.事务中的语句是preSql,params中包含每条preSql对应的参数列表
	 *
	 * @param params
	 * @return  事务是否执行成功
	 * @throws SQLException 
	 */
	public boolean exeTransaction(List<String> preSqls, List<List<Object[]>> params) throws SQLException{
		checkParams(preSqls, params);
		Connection conn = getConnection();
		boolean original = conn.getAutoCommit();
		conn.setAutoCommit(false);
		PreparedStatement ps = null;
		int index = 0;
		boolean result;
		try{
			
		
		for(String sql: preSqls){
			 ps = conn.prepareStatement(sql);
			 List<Object[]> sqlParamList = params.get(index);
			 for(Object[] param: sqlParamList){
				 this.delegate.fillStatement(ps, param);
				 ps.addBatch();
			 }
			
			 ps.executeBatch();
			 index++;
			 ps.close();
		}
		conn.commit();
		
		result = true;
		}catch(SQLException e){
			conn.rollback();
			
			throw e;
		}finally{
			conn.setAutoCommit(original);
			this.dbm.closeQuietly(conn, ps, null);
		}
		return result;
	}
	
	
	/**
	 * 执行一个事务.
	 *
	 * @return  事务是否执行成功
	 * @throws SQLException 
	 */
	public boolean exeTransaction(List<String> sqls) throws SQLException{
		Connection conn = getConnection();
		boolean original = conn.getAutoCommit();
		conn.setAutoCommit(false);
		Statement stmt = conn.createStatement();
		boolean result;
		try{
			
		
		for(String sql: sqls){
			stmt.addBatch(sql);
		}
		stmt.executeBatch();
		conn.commit();
		
		result = true;
		}catch(SQLException e){
			conn.rollback();
			throw e;
		}finally{
			conn.setAutoCommit(original);
			this.dbm.closeQuietly(conn, stmt, null);
		}
		return result;
	}
	
	private void checkParams(List<String> sqls, List<List<Object[]>> params){
		if(sqls.size() != params.size())throw new IllegalArgumentException();
	}
	
	private Connection getConnection()throws SQLException{
		Connection conn = dbm.getConnection();
		if(conn == null){
			throw new SQLException("can't get connection");
		}
		return conn;
	}
}
