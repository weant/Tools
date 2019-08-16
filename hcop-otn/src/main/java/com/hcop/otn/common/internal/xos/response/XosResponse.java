package com.hcop.otn.common.internal.xos.response;

import com.alu.tools.basic.NullUtil;

public class XosResponse<T>
{
	private boolean success;

	private Object error;

	private T data;

	private XosResponse(T data)
	{
		success = true;
		this.data = NullUtil.notNull(data);
	}

	private XosResponse(String error)
	{
		success = false;
		this.error = NullUtil.notNull(error);
	}

	private XosResponse(TL1ResponseFailureException exception)
	{
		success = false;
		this.error = NullUtil.notNull(exception);
	}

	public static <T> XosResponse<T> successResponse(T data)
	{
		return new XosResponse<T>(data);
	}

	public static <T> XosResponse<T> errorResponse(String error)
	{
		return new XosResponse<T>(error);
	}

	public static <T> XosResponse<T> errorResponse(TL1ResponseFailureException exception)
	{
		return new XosResponse<T>(exception);
	}

	public boolean isSuccess()
	{
		return success;
	}

	public boolean isTL1Error()
	{
		return (error instanceof TL1ResponseFailureException);
	}

	public Object getError()
	{
		return (error == null) ? "" : error;
	}

	public TL1ResponseFailureException getTL1Error()
	{
		return isTL1Error() ? (TL1ResponseFailureException) error : null;
	}

	public T getData() throws IllegalResponseException
	{
		validate();
		return data;
	}

	public void validate() throws IllegalResponseException
	{
		if (success) return;
		if (error instanceof TL1ResponseFailureException) throw (TL1ResponseFailureException) error;
		throw new IllegalResponseException(getError().toString());
	}
}