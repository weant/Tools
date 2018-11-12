package com.otn.tool.common.internal.xos.request;

import com.otn.tool.common.internal.xos.request.types.TL1CutThroughRequest;
import com.otn.tool.common.internal.xos.response.IResponseExtractor;
import com.otn.tool.common.internal.xos.response.XosResponse;
import com.otn.tool.common.internal.xos.response.extractors.DummyExtractor;
import com.otn.tool.common.internal.xos.response.extractors.TL1ResponseExtractor;
import com.otn.tool.common.internal.xos.session.IXosSession;
import com.otn.tool.common.internal.xos.session.SessionManager;
import com.otn.tool.common.internal.xos.tl1.request.TL1Request;
import com.otn.tool.common.internal.xos.tl1.response.TL1Response;
import com.otn.tool.common.internal.xos.util.CastorUtil;
import com.otn.tool.common.internal.xos.util.XosException;
import com.otn.tool.common.utils.ConfigKey;

import java.util.concurrent.atomic.AtomicLong;

public class RequestUtil {

	private static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	private static final String HDR_TAG = "hdr";

	private static final String OPREQID_TAG = "opReqId";

	private static final String OPNAME_TAG = "opName";

	private static final String OPREQTIME_TAG = "opReqTime";

	private static final String HOWMANY_TAG = "howMany";

	private static final String OPROLE_NODE = "<opRole>REQ</opRole>";

	private static AtomicLong nextReqID = new AtomicLong(
			System.currentTimeMillis());

	private static boolean useNeID;

	private static long defaultReconnectInterval;

	private static long defaultReqTimeout;

	private static int defaultReqQueueSize;

	private static int defaultParallelReqNum;

	public static void init() {
		useNeID = ConfigLoader.getInstance().getBoolean(
				ConfigKey.XOS_REQUEST_USE_NE_ID,
				ConfigKey.DEFAULT_XOS_REQUEST_USE_NE_ID);

		defaultReconnectInterval = ConfigLoader.getInstance().getInt(
				ConfigKey.XOS_RECONNECT_INTERVAL,
				ConfigKey.DEFAULT_XOS_RECONNECT_INTERVAL);

		defaultReqTimeout = ConfigLoader.getInstance().getInt(
				ConfigKey.XOS_REQUEST_TIMEOUT,
				ConfigKey.DEFAULT_XOS_REQUEST_TIMEOUT);

		defaultReqQueueSize = ConfigLoader.getInstance().getInt(
				ConfigKey.XOS_REQUEST_QUEUE_SIZE,
				ConfigKey.DEFAULT_XOS_REQUEST_QUEUE_SIZE);

		defaultParallelReqNum = ConfigLoader.getInstance().getInt(
				ConfigKey.PARALLEL_XOS_REQUEST_NUM,
				ConfigKey.DEFAULT_PARALLEL_XOS_REQUEST_NUM);
	}

	public static String getNeName(int emlNeID, String neLabel) {
		return String.valueOf(emlNeID);
	}

	public static long defaultReconnectInterval() {
		return defaultReconnectInterval;
	}

	public static long defaultReqTimeout() {
		return defaultReqTimeout;
	}

	public static int defaultReqQueueSize() {
		return defaultReqQueueSize;
	}

	public static int defaultParallelReqNum() {
		return defaultParallelReqNum;
	}

	public static String getNcName(int groupId) {
		return "EML_" + groupId + "_SNA";
	}

	public static String getNextReqID() {
		return String.valueOf(nextReqID.getAndIncrement());
	}

	public static String toReqBody(String reqBodyRoot, Object reqBodyObj) {
		return CastorUtil.marshalNode(reqBodyObj, reqBodyRoot);
	}

	public static String toNextReqBody(String nextReqBodyRoot) {
		StringBuilder output = new StringBuilder();
		writeXmlTag(output, nextReqBodyRoot, false);
		writeXmlNode(output, HOWMANY_TAG,
				String.valueOf(XosConstants.Request.HOW_MANY));
		writeXmlTag(output, nextReqBodyRoot, true);
		return output.toString();
	}

	public static String buildRequest(String reqID, String opName,
			long timestamp, String reqBody) {
		StringBuilder output = new StringBuilder();
		output.append(XML_DECLARATION);
		writeXmlTag(output, XosConstants.NAMESSAGE_TAG, false);
		writeHdr(output, reqID, opName, timestamp);
		output.append(reqBody);
		writeXmlTag(output, XosConstants.NAMESSAGE_TAG, true);
		return output.toString();
	}

	public static <T> T getResult(int groupId, XosRequest request,
			IResponseExtractor<T> extractor) throws XosException {
		IXosSession session = SessionManager.getInstance().getSession(groupId);
		XosResponse<T> response = session.sendRequest(request, extractor);
		return response.getData();
	}

	public static void request(int groupId, XosRequest request)
			throws XosException {
		getResult(groupId, request, new DummyExtractor());
	}

	public static TL1Response tl1Request(int groupId, int neId, String neName,
			TL1Request request) {
		return getResult(groupId, new TL1CutThroughRequest(getNcName(groupId),
				getNeName(neId, neName), request), new TL1ResponseExtractor());
	}

	public static TL1Response tl1Request(int groupId, int neId, String neName,
			String request) {
		return getResult(groupId, new TL1CutThroughRequest(getNcName(groupId),
				getNeName(neId, neName), request), new TL1ResponseExtractor());
	}

	private static void writeHdr(StringBuilder output, String reqID,
			String opName, long timestamp) {
		writeXmlTag(output, HDR_TAG, false);
		output.append(OPROLE_NODE);
		writeXmlNode(output, OPREQID_TAG, reqID);
		writeXmlNode(output, OPNAME_TAG, opName);
		writeXmlNode(output, OPREQTIME_TAG, String.valueOf(timestamp));
		writeXmlTag(output, HDR_TAG, true);
	}

	private static void writeXmlTag(StringBuilder output, String tag,
			boolean end) {
		output.append('<');
		if (end)
			output.append('/');
		output.append(tag);
		output.append('>');
	}

	private static void writeXmlNode(StringBuilder output, String nodeName,
			String nodeValue) {
		writeXmlTag( output, nodeName, false );
        output.append( nodeValue );
        writeXmlTag( output, nodeName, true );
	}
}
