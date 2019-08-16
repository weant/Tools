package com.hcop.otn.common.internal.xos.tl1.response;

import com.hcop.otn.common.internal.xos.response.TL1ResponseFailureException;
import com.hcop.otn.common.internal.xos.tl1.util.TL1Format;

import java.util.Date;

public class TL1Response extends TL1GenericMessage
{
	private Date timestamp;

	private ResponseStatus status;

	private String errorCode;

	public Date getTimestamp()
	{
		return timestamp;
	}

	public ResponseStatus getStatus()
	{
		return status;
	}

	public String getErrorCode()
	{
		return errorCode;
	}

	public void validate() throws TL1ResponseFailureException
	{
		if (status != ResponseStatus.COMPLD) throw new TL1ResponseFailureException(this);
	}

	@Override
	public String toString()
	{
		return TL1Format.formatTL1Response(this);
	}

	void setTimestamp(Date timestamp)
	{
		this.timestamp = timestamp;
	}

	void setState(ResponseStatus state)
	{
		this.status = state;
	}

	void setErrorCode(String errorCode)
	{
		this.errorCode = errorCode;
	}
}
