package com.otn.tool.common.db.oms;

import java.util.Map;

 class PwdCreator implements ConnectionCreator {

	@Override
	public void createPoolAndSave(Map<String, String> poolConfs, DBEnum type,
			int inst, Map<String, ConnectionPool> cache, String poolName) {
		// TODO Auto-generated method stub
		ConnectionPool pool = null;
		switch(type){
		case SDH:  pool = CreatorHelper.createSDHPool(poolConfs, inst);break;
//		case PKT: pool =  CreatorHelper.createPKTPool(poolConfs, inst);break;
		case SNA: pool = CreatorHelper.normalOraclePool("eml", "sna",  "dbmanager", poolConfs, inst);
		case MPLS:{
			  String username = CreatorHelper.getUsernameOfMPLSorETH(CreatorHelper.MPLS_FLAG, poolConfs, inst);
			  pool = CreatorHelper.normalOraclePool("eml", username,poolConfs.get("mpls_password"), poolConfs, inst);
		}
		case ETH:{
			String username = CreatorHelper.getUsernameOfMPLSorETH(CreatorHelper.ETH_FLAG, poolConfs, inst);
			pool = CreatorHelper.normalOraclePool("eml", username, poolConfs.get("eth_password"), poolConfs, inst);
		}
		default: throw new IllegalArgumentException("unsupported DBType:"+type);
		}
		
		cache.put(type.getCacheKey(poolName, inst), pool);

	}
}
