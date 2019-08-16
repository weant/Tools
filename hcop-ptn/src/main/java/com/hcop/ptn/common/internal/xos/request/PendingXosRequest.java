package com.hcop.ptn.common.internal.xos.request;

import com.alu.tools.basic.ArgumentUtil;
import com.alu.tools.basic.NullUtil;
import com.alu.tools.basic.collection.Container;
import com.hcop.ptn.common.internal.xos.response.*;
import com.hcop.ptn.common.internal.xos.util.XosUtil;
import com.lucent.oms.xml.naInterface.Message_T;
import com.lucent.oms.xml.naInterface.types.ResponseStatus_T;

public class PendingXosRequest<T>
{
	public static enum State
	{
		pending, firstRequest, nextRequest, finished
	}

	private InternalXosRequest request;

	private IResponseExtractor<T> responseExtreacor;

	private IXosResponseHandler<T> responseHandler;

	private Container<T> responseData;

	private long timeout;

	private State state = State.pending;

	private volatile boolean responseReceived = false;

	public PendingXosRequest(XosRequest request, IResponseExtractor<T> responseExtreacor,
			IXosResponseHandler<T> responseHandler, long timeout)
	{
		this.request = new InternalXosRequest(request);
		this.responseExtreacor = NullUtil.notNull(responseExtreacor);
		this.responseHandler = NullUtil.notNull(responseHandler);
		this.timeout = ArgumentUtil.checkMin(timeout, 1);
		responseData = new Container<T>();
	}

	@Override
	public String toString()
	{
		return request.toString() + ", State: " + state;
	}

	public String getReqID()
	{
		return request.getReqID();
	}

	public void fillReqID()
	{
		request.setReqID(XosUtil.getNextReqID());
	}

	public State getState()
	{
		return state;
	}

	public boolean isFinished()
	{
		return state == State.finished;
	}

	public boolean isTimeout()
	{
		return (!responseReceived && (System.currentTimeMillis() - request.getCreateTime()) > timeout);
	}

	public String getRequestMsg() throws IllegalStateException
	{
		switch (state)
		{
			case pending:
			case firstRequest:
				return request.getReqMsg();
			case nextRequest:
				return request.getNextReqMsg();
			default:
				throw new IllegalStateException("Request in state:" + state);
		}
	}

	public Container<T> getResponseData()
	{
		return responseData;
	}

	public void prepareSend()
	{
		forwordState();
		responseReceived = false;
	}

	public void receiveResponse(Message_T response)
	{
		if (!response.getHdr().getOpReqId().equals(request.getReqID())) throw new IllegalResponseException(
				"Response ID: '" + response.getHdr().getOpReqId() + "'and Request ID: '"
						+ request.getReqID() + "' not match");
		responseReceived = true;
		if (response.getHdr().getOpRespStatus() == ResponseStatus_T.FAILURE)
		{
			requestFailure(XosUtil.getErrorInfo(response));
			return;
		}
		try
		{
			responseExtreacor.extractResponse(response, responseData);
		}
		catch (TL1ResponseFailureException e)
		{
			requestFailure(e);
			return;
		}
		catch (IllegalResponseException e)
		{
			requestFailure("Illegal response for request: " + request.getReqID() + ". Error: "
					+ e.getMessage());
			return;
		}
		if (!response.getHdr().getMore())
		{
			requestSuccess();
		}
	}

	public void timeout()
	{
		requestFailure("XoS request timeout");
	}

	public void rollbackRequest()
	{
		state = State.pending;
		request.setReqID(null);
		responseData.set(null);
	}

	private void forwordState()
	{
		switch (state)
		{
			case pending:
				state = State.firstRequest;
				break;
			case firstRequest:
				if (request.hasNext()) state = State.nextRequest;
				break;
			case nextRequest:
				break;
			default:
		}
	}

	private void requestSuccess()
	{
		requestFinished(XosResponse.successResponse(responseData.get()));
	}

	private void requestFailure(String error)
	{
		requestFinished(XosResponse.<T>errorResponse(error));
	}

	private void requestFailure(TL1ResponseFailureException exception)
	{
		requestFinished(XosResponse.<T>errorResponse(exception));
	}

	private synchronized void requestFinished(XosResponse<T> response)
	{
		if (state == State.finished) return;
		state = State.finished;
		responseHandler.handle(response);
	}
}
