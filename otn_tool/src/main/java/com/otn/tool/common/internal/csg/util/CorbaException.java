package com.otn.tool.common.internal.csg.util;

public class CorbaException extends RuntimeException
{
	private static final long serialVersionUID = 7884573029820425161L;

	public CorbaException()
	{
		super();
	}

	public CorbaException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CorbaException(String message)
	{
		super(message);
	}

	public CorbaException(Throwable cause)
	{
		super(cause);
	}
}
