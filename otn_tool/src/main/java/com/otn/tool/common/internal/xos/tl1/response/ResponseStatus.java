package com.otn.tool.common.internal.xos.tl1.response;

public enum ResponseStatus
{
	COMPLD, DENY, PRTL, DELAY, CANCLD;

	public static ResponseStatus toStatus(String status)
	{
		try
		{
			return valueOf(status);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static boolean isErrorStatus(ResponseStatus status)
	{
		return (status == DENY);
	}
}