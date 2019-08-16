package com.hcop.otn.common.internal.xos.request;

import com.hcop.otn.common.internal.xos.util.XosException;

public class RequestException extends XosException
{
	private static final long serialVersionUID = 4646467588955734078L;

	public RequestException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public RequestException(String message)
	{
		super(message);
	}
}
