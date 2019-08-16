package com.hcop.ptn.common.db.oms;

import java.util.Map;

public class WalletCreator implements ConnectionCreator{

	@Override
	public void createPoolAndSave(Map<String, String> omsConfMap, DBEnum type,
			int inst, Map<String, ConnectionPool> cache, String poolName) {
		cache.put(type.getCacheKey(poolName, inst), new DataSourceConnectionPool(type, inst, omsConfMap.get("OMS_VERSION")));
	}

}
