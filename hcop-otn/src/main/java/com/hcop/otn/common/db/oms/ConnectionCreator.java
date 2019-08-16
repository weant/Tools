package com.hcop.otn.common.db.oms;

import java.util.Map;

public interface ConnectionCreator {
	/**
	 * 
	 * 根据poolConfs提供的信息构造ConnectionPool实例
	 * @param version 版本信息
	 * @param type     数据库类型
	 * @param cache    用于保存创建出来的connectionPool实例
	 * @param poolName  
	 * @return
	 */
	public void createPoolAndSave(String version, DBEnum type, int pktInst, Map <String, ConnectionPool> cache, String poolName);

}
