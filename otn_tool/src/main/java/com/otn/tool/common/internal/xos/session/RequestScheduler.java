package com.otn.tool.common.internal.xos.session;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.otn.tool.common.internal.xos.request.PendingXosRequest;
import com.otn.tool.common.internal.xos.request.RequestException;
import com.otn.tool.common.internal.xos.request.XosRequest;
import com.otn.tool.common.internal.xos.response.IResponseExtractor;
import com.otn.tool.common.internal.xos.response.IXosResponseHandler;
import com.alu.tools.basic.ArgumentUtil;
import com.alu.tools.basic.NullUtil;

public class RequestScheduler
{
	private static final long CHECK_TIMEOUT_INTERVAL = 10000;

	private Queue<PendingXosRequest<?>> requestQueue = new LinkedList<PendingXosRequest<?>>();

	private Map<String, PendingXosRequest<?>> reqIdToReq =
			new HashMap<String, PendingXosRequest<?>>();

	private Timer timeoutChecker = new Timer();

	private IScheduleListener scheduleListener;

	private final int requestQueueSize;

	private final int parallelRequestNum;

	private static Log log = LogFactory.getLog(RequestScheduler.class);

	public RequestScheduler(int requestQueueSize, int parallelRequestNum,
			IScheduleListener scheduleListener)
	{
		this.requestQueueSize = ArgumentUtil.checkMin(requestQueueSize, 1);
		this.parallelRequestNum = ArgumentUtil.checkMin(parallelRequestNum, 1);
		this.scheduleListener = NullUtil.notNull(scheduleListener);
		timeoutChecker.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				checkTimeout();
			}
		}, CHECK_TIMEOUT_INTERVAL, CHECK_TIMEOUT_INTERVAL);
	}

	public void stop()
	{
		timeoutChecker.cancel();
		requestQueue.clear();
		reqIdToReq.clear();
	}

	public <T> void enqueueRequest(XosRequest request, IResponseExtractor<T> extractor,
			IXosResponseHandler<T> handler, long timeout)
	{
		synchronized (requestQueue)
		{
			if (requestQueue.size() >= requestQueueSize) throw new RequestException(
					"Max request queue size exceeded: " + requestQueueSize);
			PendingXosRequest<T> pendingReq =
					new PendingXosRequest<T>(request, extractor, handler, timeout);
			enqueueRequest(pendingReq);
			log.debug("+++++request is: " + request.getReqBody());
			log.debug("Add new request to queue: " + pendingReq);
		}
	}

	public PendingXosRequest<?> scheduleRequest()
	{
		synchronized (reqIdToReq)
		{
			if (reqIdToReq.size() >= parallelRequestNum) return null;
			PendingXosRequest<?> pendingRequest = dequeueRequest();
			if (pendingRequest == null) return null;
			pendingRequest.fillReqID();
			reqIdToReq.put(pendingRequest.getReqID(), pendingRequest);
			log.debug("Schedule new request: '" + pendingRequest.getReqID() + "'");
			return pendingRequest;
		}
	}

	public PendingXosRequest<?> getRequest(String reqID)
	{
		return reqIdToReq.get(reqID);
	}

	public void requestFinished(String reqID)
	{
		synchronized (reqIdToReq)
		{
			reqIdToReq.remove(reqID);
		}
	}

	public void rollbackAllRequest()
	{
		synchronized (reqIdToReq)
		{
			for (Entry<String, PendingXosRequest<?>> entry : reqIdToReq.entrySet())
			{
				log.debug("Rollback request: '" + entry.getValue().getReqID() + "'");
				entry.getValue().rollbackRequest();
				enqueueRequest(entry.getValue());
			}
			reqIdToReq.clear();
		}
	}

	private void enqueueRequest(PendingXosRequest<?> request)
	{
		synchronized (requestQueue)
		{
			requestQueue.add(request);
		}
	}

	private PendingXosRequest<?> dequeueRequest()
	{
		synchronized (requestQueue)
		{
			return requestQueue.poll();
		}
	}

	private void checkTimeout()
	{
		List<PendingXosRequest<?>> timeoutList = null;
		synchronized (requestQueue)
		{
			for (Iterator<PendingXosRequest<?>> iterator = requestQueue.iterator(); iterator
					.hasNext();)
			{
				PendingXosRequest<?> request = iterator.next();
				if (request.isTimeout())
				{
					iterator.remove();
					if (timeoutList == null) timeoutList = new LinkedList<PendingXosRequest<?>>();
					timeoutList.add(request);
				}
			}
		}
        synchronized( reqIdToReq )
        {
            for( PendingXosRequest<?> request : reqIdToReq.values() )
            {
                if( request.isTimeout() )
                {
                    if( timeoutList == null )
                        timeoutList = new LinkedList<PendingXosRequest<?>>();
                    timeoutList.add( request );
                }
            }
        }
		procTimeoutRequest(timeoutList);
		scheduleListener.startSchedule();
	}

	private void procTimeoutRequest(Collection<PendingXosRequest<?>> timeoutRequests)
	{
		if (timeoutRequests == null) return;
		for (PendingXosRequest<?> request : timeoutRequests)
		{
			String reqID = request.getReqID();
			if (reqID != null)
			{
				log.debug("Request timeout, ReqID: '" + reqID + "'");
				requestFinished(reqID);
			}
			else
			{
				log.debug("Request not send yet but already timeout: " + request);
			}
			request.timeout();
		}
	}

	public static interface IScheduleListener
	{
		public void startSchedule();
	}
}
