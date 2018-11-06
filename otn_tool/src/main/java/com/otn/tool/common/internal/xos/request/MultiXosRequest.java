package com.otn.tool.common.internal.xos.request;

import com.alu.tools.basic.NullUtil;

public class MultiXosRequest extends XosRequest
{
	@Override
	protected void init(String reqOpName, String reqBodyRoot, String nextReqOpName,
			String nextReqBodyRoot, Object reqBodyObj)
	{
		super.init(NullUtil.notNull(reqOpName), NullUtil.notNull(reqBodyRoot),
				NullUtil.notNull(nextReqOpName), NullUtil.notNull(nextReqBodyRoot),
				NullUtil.notNull(reqBodyObj));
	}

	@Override
	public boolean hasNext()
	{
		return true;
	}
}
