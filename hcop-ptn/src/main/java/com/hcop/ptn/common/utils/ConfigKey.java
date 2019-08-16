package com.hcop.ptn.common.utils;

public class ConfigKey
{
	public static final String JACORB_CONF_FILE = "jacorbConfFile";

	public static final String SNA_CONF_FILE = "snaConfFile";

	public static final String XOS_RECONNECT_INTERVAL = "xosReconnectInterval";

	public static final int DEFAULT_XOS_RECONNECT_INTERVAL = 30000;

	public static final String XOS_REQUEST_TIMEOUT = "xosRequestTimeout";

	public static final int DEFAULT_XOS_REQUEST_TIMEOUT = 60000;

	public static final String XOS_REQUEST_QUEUE_SIZE = "xosRequestQueueSize";

	public static final int DEFAULT_XOS_REQUEST_QUEUE_SIZE = 10000;

	public static final String PARALLEL_XOS_REQUEST_NUM = "parallelXosRequestNum";

	public static final int DEFAULT_PARALLEL_XOS_REQUEST_NUM = 30;

	public static final String CSG_EMLVIEW_CONF_FILE = "csgConfFile";

	public static final String CSG_EMLVIEW_NAME_PATTERN = "csgEmlNamePattern";

	public static final String DEFAULT_CSG_EMLVIEW_NAME_PATTERN = "EMLServers/EML_IM_#/SnmpEMLViewServer";

	public static final String NAME_SERVICE_KEY = "nameServiceKey";

	public static final String DEFAULT_NAME_SERVICE_KEY = "DefaultNamingContext";

	public static final String EMLVIEW_HEART_BEAT_INTERVAL = "emlHeartBeatInterval";

	public static final int DEFAULT_EMLVIEW_HEART_BEAT_INTERVAL = 60000;
	
	public static final String XOS_REQUEST_USE_NE_ID = "XOS_REQUEST_USE_NE_ID";
	
	public static final boolean DEFAULT_XOS_REQUEST_USE_NE_ID = true;
}
