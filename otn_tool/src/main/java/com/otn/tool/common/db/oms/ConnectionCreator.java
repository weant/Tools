package com.otn.tool.common.db.oms;

import java.util.Map;

public interface ConnectionCreator {
	
	  String KEY_HOST = "host";
	  String KEY_IP = "ip";
	  String KEY_NMS_DIGITAL_VERSION = "nms_digital_version";
	  String KEY_DB_PORT = "db_port";
	  String KEY_DB_URL = "db_url";
	/**
	 * 
	 * 根据poolConfs提供的信息构造ConnectionPool实例
	 * @param poolConfs 配置信息
	 * @param type     数据库类型
	 * @param cache    用于保存创建出来的connectionPool实例
	 * @param poolName  
	 * @return
	 */
	public void createPoolAndSave(Map<String, String> poolConfs, DBEnum type, int pktInst, Map<String, ConnectionPool> cache, String poolName);

}
