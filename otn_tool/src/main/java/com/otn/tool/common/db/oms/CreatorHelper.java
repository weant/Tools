package com.otn.tool.common.db.oms;

import java.util.Map;

 class CreatorHelper {
	public static final String MPLS_FLAG="MPLS";
	public static final String ETH_FLAG="ETH";
	
	/**创建SDH数据库连接池*/
	public static ConnectionPool createSDHPool(Map<String, String> poolConfs,int inst){
	
	//	return normalOraclePool("otn","snml","snml", poolConfs, inst);
		return normalOraclePool("otn","snml",poolConfs.get("db_password"), poolConfs, inst);
	}
	
	
	/**创建PKT数据库连接池*/
	public static ConnectionPool createPKTPool(Map<String, String> poolConfs,int inst){
		
	//	return normalOraclePool("be","bmml","eth", poolConfs, inst);
		return normalOraclePool("be","bmml",poolConfs.get("db_password"), poolConfs, inst);
	}
	
	
	 public static ConnectionPool normalOraclePool(String prefixOfsid, String username, String password,Map<String,String> config,int inst){
		 ConnectionPoolImpl pool = new ConnectionPoolImpl(config.get(ConnectionCreator.KEY_HOST),
				 config.get(ConnectionCreator.KEY_IP),
					Integer.parseInt(config.get(ConnectionCreator.KEY_DB_PORT)),
					getSid(prefixOfsid, config, inst),
					username,
					password);
			return pool;
	}
	 
	 
	 public static String getSid(String prefixOfsid,Map<String,String> config,int inst){
		return prefixOfsid + getSuffix(config.get(ConnectionCreator.KEY_NMS_DIGITAL_VERSION),inst); 
	 }
	 
	 public static String getSuffix(String version, int inst){
		 return deleteDot(version) + "_" + inst;
	 }
	 
	 public static String getUsernameOfMPLSorETH(String prefix,Map<String,String> config, int inst){
		 return prefix + getSuffix(config.get(ConnectionCreator.KEY_NMS_DIGITAL_VERSION),inst); 
	 }

     public static String deleteDot(String dotString) {
         String result = "";
         if (dotString != null && dotString.length() != 0) {
             result = dotString.replace(".", "");
         }
         return result;
     }

}
