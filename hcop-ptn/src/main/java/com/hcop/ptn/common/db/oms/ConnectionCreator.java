package com.hcop.ptn.common.db.oms;

import java.util.Map;

public interface ConnectionCreator {
	/**
	 * 
	 * 根据poolConfs提供的信息构造ConnectionPool实例
	 * @param omsConfMap nms信息
	 * @param type     数据库类型
	 * @param cache    用于保存创建出来的connectionPool实例
	 * @param poolName  
	 * @return
	 */
	public void createPoolAndSave(Map<String, String> omsConfMap, DBEnum type, int pktInst, Map <String, ConnectionPool> cache, String poolName);

}
