package com.hcop.ptn.common.internal.xos.response;

import com.alu.tools.basic.StringJoiner;
import com.hcop.ptn.common.internal.xos.tl1.response.ResponseStatus;
import com.hcop.ptn.common.internal.xos.tl1.response.TL1Response;

import java.util.List;

public class TL1ResponseFailureException extends IllegalResponseException
{
	private static final long serialVersionUID = 1461376683903565697L;

	private ResponseStatus status;

	private String errorCode;

	private String tl1Comment;

	public TL1ResponseFailureException(ResponseStatus status, String errorCode,
			List<String> tl1Comment)
	{
		super("Status: " + status + ", ErrorCode: " + errorCode + ", Comments: " + tl1Comment);
		this.status = status;
		this.errorCode = errorCode;
		this.tl1Comment = StringJoiner.join("\n", tl1Comment);
	}

	public TL1ResponseFailureException(TL1Response tl1Response)
	{
		this(tl1Response.getStatus(), tl1Response.getErrorCode(), tl1Response.getCommentList());
	}

	public ResponseStatus getResponseStatus()
	{
		return status;
	}

	public String getErrorCode()
	{
		return errorCode;
	}

	public String getTL1Comment()
	{
		return tl1Comment;
	}
}
