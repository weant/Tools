package com.hcop.ptn.common.db.oms;

import com.hcop.ptn.common.jcorba.Utilities;

import java.util.Map;

class HPUnixCreator implements ConnectionCreator {
	private static final String OMS_VERSION = "OMS_VERSION";

	@Override
	public void createPoolAndSave(Map<String, String> omsConfMap, DBEnum type, int inst, Map <String, ConnectionPool> cache, String poolName) {
		ConnectionPool pool;
		switch(type) {
			case SDH: {
				pool = CreatorHelper.createSDHPool(omsConfMap, inst, type);
			}
			break;
			case PKT: {
				pool =  CreatorHelper.createPKTPool(omsConfMap, inst, type);
			}
			break;
			case SNA:
			case ETH:
			case MPLS:
				createPoolsByEML(omsConfMap,inst,cache,poolName);return;
			default: throw new IllegalArgumentException("unsupported DBType:"+type);
		}
	
		cache.put(type.getCacheKey(poolName, inst), pool);
	}

	/**
	 * 
	 * 
	 * 创建与EML配置相关的数据库。包括（SNA，CPM中的ETH，MPLS）
	 * @param poolConfs
	 * @param inst
	 * @param cache
	 * @param poolName
	 */
	private void createPoolsByEML(Map<String, String> poolConfs,int inst, Map<String,ConnectionPool> cache,String poolName) {

		String name;
		if(poolConfs.containsKey("EML_" + inst + "_IP")){
			name = "EML";
		} else if(poolConfs.containsKey("OTN_" + inst + "_IP")) {
			name = "OTN";
		} else {
			name = "OTNE";
		}

		String ip = poolConfs.get(name + "_" + inst + "_IP");
		String dbPort = poolConfs.get(name + "_" + inst + "_DB_PORT");
			
		//create SNA pool
		ConnectionPool pool;
		if(!cache.containsKey(DBEnum.SNA.getCacheKey(poolName, inst))) {
			pool = createSNA(ip, dbPort);
			cache.put(DBEnum.SNA.getCacheKey(poolName, inst), pool);
		}

			
		String version = poolConfs.get(OMS_VERSION);
			
		//create ETH pool in CPM database
		if(!cache.containsKey(DBEnum.ETH.getCacheKey(poolName, inst))) {
			pool = createCPMPool("ETH", ip, dbPort, version, inst);
			cache.put(DBEnum.ETH.getCacheKey(poolName, inst), pool);
		}
			
		//create MPLS pool in CPM database
		if(!cache.containsKey(DBEnum.MPLS.getCacheKey(poolName, inst))) {
			pool = createCPMPool("MPLS", ip, dbPort, version, inst);
			cache.put(DBEnum.MPLS.getCacheKey(poolName, inst), pool);
		}
	}


	private static ConnectionPool createSNA(String ip, String dbPort){
		ConnectionPool pool = new ConnectionPoolImpl(createMysqlUrl(ip,dbPort,"SNA"), "axadmin", "axadmin");
		return pool;
	}
	

	private static ConnectionPool createCPMPool(String prefix, String ip, String dbPort,String version,int inst){
		String schema = prefix+Utilities.deleteDot(version)+"_"+inst;
		String url =createMysqlUrl(ip, dbPort, schema);
		ConnectionPool pool = new ConnectionPoolImpl(url, "axadmin", "axadmin");
		return pool;
	}
	private static String createMysqlUrl(String ip, String port, String schema){
		return "jdbc:mysql://"+ip+":"+port+"/"+schema+"?";
	}
}
