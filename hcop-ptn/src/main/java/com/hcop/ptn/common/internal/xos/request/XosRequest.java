package com.hcop.ptn.common.internal.xos.request;

import com.hcop.ptn.common.internal.xos.util.XosUtil;

public abstract class XosRequest
{
	private String reqOpName;

	private String reqBodyRoot;

	private String nextReqOpName;

	private String nextReqBodyRoot;

	private Object reqBodyObj;

	XosRequest()
	{
	}

	void init(String reqOpName, String reqBodyRoot, String nextReqOpName,
			String nextReqBodyRoot, Object reqBodyObj)
	{
		this.reqOpName = reqOpName;
		this.reqBodyRoot = reqBodyRoot;
		this.nextReqOpName = nextReqOpName;
		this.nextReqBodyRoot = nextReqBodyRoot;
		this.reqBodyObj = reqBodyObj;
	}

	public String getReqOpName()
	{
		return reqOpName;
	}

	public String getNextReqOpName()
	{
		if (!hasNext()) throw new RequestException("Next request unsupported: '" + reqOpName
				+ "'");
		return nextReqOpName;
	}

	public String getReqBody()
	{
		return XosUtil.toReqBody(reqBodyRoot, reqBodyObj);
	}

	public String getNextReqBody()
	{
		if (!hasNext()) throw new RequestException("Next request unsupported: '" + reqOpName
				+ "'");
		return XosUtil.toNextReqBody(nextReqBodyRoot);
	}

	abstract public boolean hasNext();
}
