package com.hcop.ptn.common.db.oms;

import com.hcop.ptn.common.jcorba.Utilities;

import java.util.Map;


class CreatorHelper {
     private static final String OMS_VERSION = "OMS_VERSION";
	
	/**创建SDH数据库连接池*/
	public static ConnectionPool createSDHPool(Map<String, String> poolConfs,int inst, DBEnum type){
	
		return normalOraclePool("README","snml","snml", poolConfs, inst, type);
	}
	
	
	/**创建PKT数据库连接池*/
	public static ConnectionPool createPKTPool(Map<String, String> poolConfs,int inst, DBEnum type){
		
		return normalOraclePool("be","bmml","eth", poolConfs, inst, type);
	
	}
	
	
	 public static ConnectionPool normalOraclePool(String prefixOfSid, String userName, String password, Map<String,String> config, int inst, DBEnum type){
        String name;
        if(type.equals(DBEnum.SDH)) {
            if (config.containsKey("OTNE_" + inst + "_IP")) {
                 name = "OTNE";
            } else {
                name = "OTN";
            }
        } else {
            name = "PKT";
        }

	    ConnectionPool pool = new ConnectionPoolImpl(config.get(name + "_" + inst + "_HOSTNAME"),
				 config.get(name + "_" + inst + "_IP"),
					Integer.parseInt(config.get(name + "_" + inst + "_DB_PORT")),
					getSid(prefixOfSid, config, inst), userName, password);

	    return pool;
	}
	 
	 
	 public static String getSid(String prefixOfsid,Map<String,String> config,int inst){
		return prefixOfsid + getSuffix(config.get(OMS_VERSION),inst);
	 }
	 
	 public static String getSuffix(String version, int inst){
		 return Utilities.deleteDot(version) + "_" + inst;
	 }
	 
	 public static String getUsernameOfMPLSorETH(String prefix,Map<String,String> config, int inst){
		 return prefix + getSuffix(config.get(OMS_VERSION),inst);
	 }

}
