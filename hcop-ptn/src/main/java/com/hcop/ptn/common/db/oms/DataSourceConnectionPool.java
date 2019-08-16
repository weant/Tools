package com.hcop.ptn.common.db.oms;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

public class DataSourceConnectionPool implements ConnectionPool {
	private static Log LOG = LogFactory.getLog(DataSourceConnectionPool.class);
	private static final String DRIVER="oracle.jdbc.OracleDriver";
	private static final String BASE_URL="jdbc:oracle:thin:@";
	private static final String TEST_SQL="select 1 from dual";
	private static final int MAX=6;
	private static final int MIN=2;
	private static final String TNS_KEY = "oracle.net.tns_admin";
	private static final String WALLET_KEY = "oracle.net.wallet_location";
	
	private DataSource ds;
	private DBEnum dbType;
	private int inst;
	public DataSourceConnectionPool(DBEnum dbType, int inst, String version){
		//need set properties
		this.inst = inst;
		this.dbType = dbType;
		ds = setupDataSource(dbType.getAlias(version,inst));
	}


	private Map<String, String> getProperties(DBEnum dbType) {
		Map<String, String> map = new HashMap<String,String>();
		map.put(TNS_KEY, dbType.getWalletPath(inst));
		map.put(WALLET_KEY, dbType.getWalletPath(inst));
		return map;
	}


	private DataSource setupDataSource(String alias) {
		BasicDataSource  ds = new BasicDataSource();
		ds.setDriverClassName(DRIVER);
		ds.setMaxActive(MAX);
		ds.setMinIdle(MIN);
		ds.setMaxWait(-1L);
		ds.setUrl(BASE_URL+alias);
		ds.setTestWhileIdle(true);
		ds.setTimeBetweenEvictionRunsMillis(14400000);
		ds.setNumTestsPerEvictionRun(MAX);
		ds.setValidationQuery(TEST_SQL);
		ds.setValidationQueryTimeout(1000);
		ds.setRemoveAbandoned(true);
		ds.setRemoveAbandonedTimeout(1800);
		return ds;
	}

	@Override
	public Connection getConnection() throws SQLException {
		
		try {
			SystemPropertyHolder.setProperties(getProperties(dbType));
		} catch (InterruptedException e) {
			LOG.error("error in lock SystemProperties", e);
		}
		try{
			return ds.getConnection();
		}finally{
			SystemPropertyHolder.release();
		}
		
	}
	
	
	private static class SystemPropertyHolder {
		private static  Semaphore signal = new Semaphore(1);
		
		public static  void setProperties(Map<String, String> props) throws InterruptedException{
			signal.acquire();
			for(Entry<String,String> entry: props.entrySet()){
				System.setProperty(entry.getKey(), entry.getValue());
			}
		}
		
		
		public static void release(){
			signal.release();
		}
	}

}
