package com.hcop.ptn.common.internal.xos.request;

import com.alu.tools.basic.NullUtil;

public class SingleXosRequest extends XosRequest
{
	protected void init(String opName, String opBodyRoot, Object reqBodyObj)
	{
		init(NullUtil.notNull(opName), NullUtil.notNull(opBodyRoot), null, null,
				NullUtil.notNull(reqBodyObj));
	}

	@Override
	public boolean hasNext()
	{
		return false;
	}
}
