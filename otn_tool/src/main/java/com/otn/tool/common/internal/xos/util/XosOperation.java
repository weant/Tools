package com.otn.tool.common.internal.xos.util;

import com.otn.tool.common.internal.xos.request.XosRequest;
import com.otn.tool.common.internal.xos.request.types.TL1CutThroughRequest;
import com.otn.tool.common.internal.xos.response.IResponseExtractor;
import com.otn.tool.common.internal.xos.response.XosResponse;
import com.otn.tool.common.internal.xos.response.extractors.DummyExtractor;
import com.otn.tool.common.internal.xos.response.extractors.TL1ResponseExtractor;
import com.otn.tool.common.internal.xos.session.IXosSession;
import com.otn.tool.common.internal.xos.session.SessionManager;
import com.otn.tool.common.internal.xos.tl1.request.TL1Request;
import com.otn.tool.common.internal.xos.tl1.response.TL1Response;

public class XosOperation
{
	public static <T> T getResult(int groupID, XosRequest request,
			IResponseExtractor<T> extractor) throws XosException
	{
		IXosSession session = SessionManager.getInstance().getSession(groupID);
		XosResponse<T> response = session.sendRequest(request, extractor);
        return response.getData();
	}

	public static void request(int groupID, XosRequest request) throws XosException
	{
		getResult(groupID, request, new DummyExtractor());
	}

	public static TL1Response tl1Request(int groupID, int neID, TL1Request request)
	{
		TL1Response response =
				getResult(groupID,
						new TL1CutThroughRequest(XosUtil.getNcName(groupID), String.valueOf(neID),
								request), new TL1ResponseExtractor());
		response.validate();
		return response;
	}

	public static TL1Response tl1Request(int groupID, int neID, String request)
	{
		TL1Response response =
				getResult(groupID,
						new TL1CutThroughRequest(XosUtil.getNcName(groupID), String.valueOf(neID),
								request), new TL1ResponseExtractor());
		response.validate();
		return response;
	}
}
