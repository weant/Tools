package com.otn.tool.common.internal.xos.util;

import java.util.concurrent.atomic.AtomicLong;

import com.otn.tool.common.internal.util.ConfigKey;
import com.lucent.oms.xml.naInterface.Message_T;
import com.otn.tool.common.properties.Conf;

public class XosUtil
{
	private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	private static final String NAMESSAGE_TAG = "namessage";

	private static final String HDR_TAG = "hdr";

	private static final String OPREQID_TAG = "opReqId";

	private static final String OPNAME_TAG = "opName";

	private static final String OPREQTIME_TAG = "opReqTime";

	private static final String HOWMANY_TAG = "howMany";

	private static final String OPROLE_NODE = "<opRole>REQ</opRole>";

	private static final int HOW_MANY = 100;

	private static AtomicLong nextReqID = new AtomicLong(System.currentTimeMillis());

	private static long reconnectInterval;

	private static long requestTimeout;

	private static int requestQueueSize;

	private static int parallelReqNum;

	public static void init()
	{
		reconnectInterval =
				Conf.instance().getPropertiesMap().containsKey(ConfigKey.XOS_RECONNECT_INTERVAL) ?
						Integer.valueOf(Conf.instance().getProperty(ConfigKey.XOS_RECONNECT_INTERVAL)) :
						ConfigKey.DEFAULT_XOS_RECONNECT_INTERVAL;
		requestTimeout =
				Conf.instance().getPropertiesMap().containsKey(ConfigKey.XOS_REQUEST_TIMEOUT) ?
						Integer.valueOf(Conf.instance().getProperty(ConfigKey.XOS_REQUEST_TIMEOUT)) :
						ConfigKey.DEFAULT_XOS_REQUEST_TIMEOUT;
		requestQueueSize =
				Conf.instance().getPropertiesMap().containsKey(ConfigKey.XOS_REQUEST_QUEUE_SIZE) ?
						Integer.valueOf(Conf.instance().getProperty(ConfigKey.XOS_REQUEST_QUEUE_SIZE)) :
						ConfigKey.DEFAULT_XOS_REQUEST_QUEUE_SIZE;
		parallelReqNum =
				Conf.instance().getPropertiesMap().containsKey(ConfigKey.PARALLEL_XOS_REQUEST_NUM) ?
						Integer.valueOf(Conf.instance().getProperty(ConfigKey.PARALLEL_XOS_REQUEST_NUM)) :
						ConfigKey.DEFAULT_PARALLEL_XOS_REQUEST_NUM;
	}

	public static long reconnectInterval()
	{
		return reconnectInterval;
	}

	public static long requestTimeout()
	{
		return requestTimeout;
	}

	public static int requestQueueSize()
	{
		return requestQueueSize;
	}

	public static int parallelReqNum()
	{
		return parallelReqNum;
	}

	public static String getNcName(int groupID)
	{
		return "EML_" + groupID + "_SNA";
	}

	public static String getNextReqID()
	{
		return String.valueOf(nextReqID.getAndIncrement());
	}

	public static String toReqBody(String reqBodyRoot, Object reqBodyObj)
	{
		return CastorUtil.marshalNode(reqBodyObj, reqBodyRoot);
	}

	public static String toNextReqBody(String nextReqBodyRoot)
	{
		StringBuilder output = new StringBuilder();
		writeXmlTag(output, nextReqBodyRoot, false);
		writeXmlNode(output, HOWMANY_TAG, String.valueOf(HOW_MANY));
		writeXmlTag(output, nextReqBodyRoot, true);
		return output.toString();
	}

	public static String buildRequest(String reqID, String opName, long timestamp,
			String reqBody)
	{
		StringBuilder output = new StringBuilder();
		output.append(XosUtil.XML_DECLARATION);
		writeXmlTag(output, XosUtil.NAMESSAGE_TAG, false);
		writeHdr(output, reqID, opName, timestamp);
		output.append(reqBody);
		writeXmlTag(output, XosUtil.NAMESSAGE_TAG, true);
		return output.toString();
	}

	public static String getErrorInfo(Message_T response)
	{
		try
		{
			return "Response Failure:\n" + CastorUtil.marshal(response, XosUtil.NAMESSAGE_TAG);
		}
		catch (Exception e)
		{
			return "Response Failure";
		}
	}

	private static void writeHdr(StringBuilder output, String reqID, String opName,
			long timestamp)
	{
		writeXmlTag(output, XosUtil.HDR_TAG, false);
		output.append(OPROLE_NODE);
		writeXmlNode(output, XosUtil.OPREQID_TAG, reqID);
		writeXmlNode(output, XosUtil.OPNAME_TAG, opName);
		writeXmlNode(output, OPREQTIME_TAG, String.valueOf(timestamp));
		writeXmlTag(output, XosUtil.HDR_TAG, true);
	}

	private static void writeXmlTag(StringBuilder output, String tag, boolean end)
	{
		output.append('<');
		if (end) output.append('/');
		output.append(tag);
		output.append('>');
	}

	private static void writeXmlNode(StringBuilder output, String nodeName, String nodeValue)
	{
		writeXmlTag(output, nodeName, false);
		output.append(nodeValue);
		writeXmlTag(output, nodeName, true);
	}
}
