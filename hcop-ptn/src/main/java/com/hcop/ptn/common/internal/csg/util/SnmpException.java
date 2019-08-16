package com.hcop.ptn.common.internal.csg.util;

import com.alu.tools.basic.exception.BasicRuntimeException;

public class SnmpException extends BasicRuntimeException
{
	private static final long serialVersionUID = -9029169449887545627L;

	public SnmpException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SnmpException(String message)
	{
		super(message);
	}

	public SnmpException(Throwable cause)
	{
		super(cause);
	}
}
