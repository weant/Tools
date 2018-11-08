package com.otn.tool.common.db.oms;

import java.util.Map;

public class WalletCreator implements ConnectionCreator{

	@Override
	public void createPoolAndSave(Map<String, String> poolConfs, DBEnum type,
			int inst, Map<String, ConnectionPool> cache, String poolName) {
		cache.put(type.getCacheKey(poolName, inst), new DataSourceConnectionPool(type, inst,poolConfs.get("nms_digital_version")));
	}

}
