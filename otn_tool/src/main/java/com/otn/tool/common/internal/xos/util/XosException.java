package com.otn.tool.common.internal.xos.util;

import com.alu.tools.basic.exception.BasicRuntimeException;

public class XosException extends BasicRuntimeException
{
	private static final long serialVersionUID = -4225037934533809815L;

	public XosException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public XosException(String message)
	{
		super(message);
	}

	public XosException(Throwable cause)
	{
		super(cause);
	}
}
