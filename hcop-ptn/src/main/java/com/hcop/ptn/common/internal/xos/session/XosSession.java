package com.hcop.ptn.common.internal.xos.session;

import com.alu.tools.basic.ArgumentUtil;
import com.alu.tools.basic.NullUtil;
import com.hcop.ptn.common.internal.xos.request.PendingXosRequest;
import com.hcop.ptn.common.internal.xos.request.RequestException;
import com.hcop.ptn.common.internal.xos.request.XosRequest;
import com.hcop.ptn.common.internal.xos.response.IResponseExtractor;
import com.hcop.ptn.common.internal.xos.response.IXosResponseHandler;
import com.hcop.ptn.common.internal.xos.response.XosResponse;
import com.hcop.ptn.common.internal.xos.session.RequestScheduler.IScheduleListener;
import com.lucent.oms.xml.naInterface.Message_T;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XosSession extends AbstractXosSession
{
	private final long defaultTimeout;

	private volatile boolean connected = false;

	private RequestScheduler scheduler;

	private static Log log = LogFactory.getLog(XosSession.class);

	public XosSession(int groupID, String host, int port, long reconnectInterval,
			int requestQueueSize, int parallelRequestNum, long defaultTimeout)
	{
		super(groupID, host, port, reconnectInterval);
		scheduler =
				new RequestScheduler(requestQueueSize, parallelRequestNum, new IScheduleListener()
				{
					@Override
					public void startSchedule()
					{
						scheduleRequest();
					}
				});
		this.defaultTimeout = ArgumentUtil.checkMin(defaultTimeout, 1);
	}

	@Override
	public void endSession()
	{
		scheduler.stop();
		super.endSession();
	}

	@Override
	public <T> XosResponse<T> sendRequest(XosRequest request, IResponseExtractor<T> extractor,
										  long timeout) throws RequestException
	{
		SyncXosResponseHandler<T> handler = new SyncXosResponseHandler<T>();
		scheduler.enqueueRequest(request, extractor, handler, timeout);
		handler.setGuard(handler);
		synchronized (handler)
		{
			scheduleRequest();
			try
			{
				handler.wait();
			}
			catch (InterruptedException e)
			{
				throw new RequestException("Waiting response interrupted", e);
			}
			return handler.getResponse();
		}
	}

	@Override
	public <T> void sendRequest(XosRequest request, IResponseExtractor<T> extractor,
								IXosResponseHandler<T> handler, long timeout) throws RequestException
	{
		scheduler.enqueueRequest(request, extractor, handler, timeout);
		scheduleRequest();
	}

	@Override
	public <T> XosResponse<T> sendRequest(XosRequest request, IResponseExtractor<T> extractor)
			throws RequestException
	{
		return sendRequest(request, extractor, defaultTimeout);
	}

	@Override
	public <T> void sendRequest(XosRequest request, IResponseExtractor<T> extractor,
			IXosResponseHandler<T> handler) throws RequestException
	{
		sendRequest(request, extractor, handler, defaultTimeout);
	}

	@Override
	protected void responseReceived(Message_T response)
	{
		PendingXosRequest<?> request = scheduler.getRequest(response.getHdr().getOpReqId());
		if (request == null) return;
		request.receiveResponse(response);
		if (!request.isFinished())
		{
			log.debug("Request '" + request.getReqID() + "' not finished yet, send next request");
			sendRequest(request);
		}
		else
		{
			log.debug("Request '" + request.getReqID() + "' finished");
			scheduler.requestFinished(response.getHdr().getOpReqId());
			scheduleRequest();
		}
	}

	@Override
	protected void connectionEstablished()
	{
		connected = true;
		scheduleRequest();
	}

	@Override
	protected void connectionLost()
	{
		connected = false;
		scheduler.rollbackAllRequest();
	}

	private void scheduleRequest()
	{
		if (!connected) return;
		PendingXosRequest<?> pendingRequest;
		while ((pendingRequest = scheduler.scheduleRequest()) != null)
		{
			sendRequest(pendingRequest);
		}
	}

	private void sendRequest(PendingXosRequest<?> request)
	{
		request.prepareSend();
		sendRequest(request.getRequestMsg());
	}
}

class SyncXosResponseHandler<T> implements IXosResponseHandler<T>
{
	private XosResponse<T> response;

	private Object guard;

	public void setGuard(Object guard)
	{
		this.guard = NullUtil.notNull(guard);
	}

	@Override
	public void handle(XosResponse<T> response)
	{
		this.response = NullUtil.notNull(response);
		synchronized (guard)
		{
			guard.notify();
		}
	}

	public XosResponse<T> getResponse()
	{
		return response;
	}
}