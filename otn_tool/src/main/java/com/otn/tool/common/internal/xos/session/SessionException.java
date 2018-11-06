package com.otn.tool.common.internal.xos.session;

import com.otn.tool.common.internal.xos.util.XosException;

public class SessionException extends XosException
{
	private static final long serialVersionUID = 169137445482310406L;

	public SessionException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SessionException(String message)
	{
		super(message);
	}
}
