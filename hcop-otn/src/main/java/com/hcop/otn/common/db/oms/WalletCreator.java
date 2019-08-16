package com.hcop.otn.common.db.oms;

import java.util.Map;

public class WalletCreator implements ConnectionCreator{

	@Override
	public void createPoolAndSave(String version, DBEnum type,
			int inst, Map<String, ConnectionPool> cache, String poolName) {
		cache.put(type.getCacheKey(poolName, inst), new DataSourceConnectionPool(type, inst, version));
	}

}
