package com.otn.tool.common.internal.xos.session;

import com.otn.tool.common.internal.xos.request.RequestException;
import com.otn.tool.common.internal.xos.request.XosRequest;
import com.otn.tool.common.internal.xos.response.IResponseExtractor;
import com.otn.tool.common.internal.xos.response.IXosResponseHandler;
import com.otn.tool.common.internal.xos.response.XosResponse;

public interface IXosSession
{
	public int getGroupID();

	public void startSession();

	public void endSession();

	public boolean available();

	public <T> XosResponse<T> sendRequest(XosRequest request, IResponseExtractor<T> extractor)
			throws RequestException;

	public <T> XosResponse<T> sendRequest(XosRequest request, IResponseExtractor<T> extractor,
                                          long timeout) throws RequestException;

	public <T> void sendRequest(XosRequest request, IResponseExtractor<T> extractor,
                                IXosResponseHandler<T> handler) throws RequestException;

	public <T> void sendRequest(XosRequest request, IResponseExtractor<T> extractor,
                                IXosResponseHandler<T> handler, long timeout) throws RequestException;
}
