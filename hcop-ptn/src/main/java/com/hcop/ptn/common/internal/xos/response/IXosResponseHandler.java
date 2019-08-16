package com.hcop.ptn.common.internal.xos.response;

public interface IXosResponseHandler<T>
{
	public void handle(XosResponse <T> response);
}
