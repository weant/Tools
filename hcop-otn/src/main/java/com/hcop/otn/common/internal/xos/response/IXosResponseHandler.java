package com.hcop.otn.common.internal.xos.response;

public interface IXosResponseHandler<T>
{
	public void handle(XosResponse <T> response);
}
