package com.otn.tool.common.internal.xos.request;

import com.otn.tool.common.internal.xos.util.XosUtil;

public class InternalXosRequest
{
	private String reqID;

	private String reqOpName;

	private String nextReqOpName;

	private String reqBody;

	private String nextReqBody;

	private boolean hasNext;

	private long createTime = System.currentTimeMillis();

	public InternalXosRequest(XosRequest request)
	{
		reqOpName = request.getReqOpName();
		reqBody = request.getReqBody();
		hasNext = request.hasNext();
		if (hasNext)
		{
			nextReqOpName = request.getNextReqOpName();
			nextReqBody = request.getNextReqBody();
		}
	}

	@Override
	public String toString()
	{
		StringBuilder output = new StringBuilder();
		output.append("CreateTime: ").append(createTime).append(", ID: '").append(reqID)
				.append("', ReqOpName: '").append(reqOpName).append("'");
		output.append(", ReqBody: '").append(reqBody).append("'");
		if (hasNext)
		{
			output.append(", NextReqOpName: '").append(nextReqOpName).append("'")
					.append(", NextReqBody: '").append(nextReqBody).append("'");
		}
		return output.toString();
	}

	public String getReqID()
	{
		return reqID;
	}

	void setReqID(String reqID)
	{
		this.reqID = reqID;
	}

	public long getCreateTime()
	{
		return createTime;
	}

	public boolean hasNext()
	{
		return hasNext;
	}

	public String getReqMsg()
	{
		return XosUtil.buildRequest(reqID, reqOpName, System.currentTimeMillis(), reqBody);
	}

	public String getNextReqMsg()
	{
		if (!hasNext) throw new RequestException("Next request unsupported for: '" + reqOpName
				+ "'");
		return XosUtil.buildRequest(reqID, nextReqOpName, System.currentTimeMillis(),
				nextReqBody);
	}
}
