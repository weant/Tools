package com.hcop.ptn.common.internal.xos.tl1.response;

import com.alu.tools.basic.collection.Pair;
import com.hcop.ptn.common.internal.xos.response.IllegalResponseException;
import com.hcop.ptn.common.internal.xos.tl1.util.TL1Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TL1ResponseParser
{
	private static final int TIMESTAMP_LENGTH = 17;

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

	static
	{
		DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT0"));
	}

	public static TL1Response parse(String rawTL1Response) throws IllegalResponseException
	{
		Pair<Date, Integer> timestampAndIndex = getTimestamp(rawTL1Response);
		Pair<String, Integer> statusAndIndex =
				getStatusCode(timestampAndIndex.getSecond(), rawTL1Response);
		ResponseStatus status = ResponseStatus.toStatus(statusAndIndex.getFirst());
		TL1Response response = new TL1Response();
		response.setTimestamp(timestampAndIndex.getFirst());
		response.setState(status);
		if (ResponseStatus.isErrorStatus(status))
		{
			response.setErrorCode(getErrorCode(rawTL1Response, statusAndIndex.getSecond()));
		}
		new TL1GenericMessageParser(new TL1MessageDataParser()).parse(response, rawTL1Response,
				statusAndIndex.getSecond());
		return response;
	}

	private static Pair<String, Integer> getStatusCode(int fromIndex, String rawTL1Response)
			throws IllegalResponseException
	{
		int headIndex =
				rawTL1Response.indexOf(TL1Constants.MsgDelimiter.RESPONSE_START, fromIndex);
		if (headIndex < 0) throw new IllegalResponseException("Cannot find statusCode");
		int statusCodeBegin =
				rawTL1Response.indexOf(TL1Constants.MsgDelimiter.BLANK, headIndex
						+ TL1Constants.MsgDelimiter.RESPONSE_START.length());
		if (statusCodeBegin < 0) throw new IllegalResponseException("Cannot get statusCode");
		statusCodeBegin++;
		int statusCodeEnd =
				rawTL1Response.indexOf(TL1Constants.MsgDelimiter.LINE_END, statusCodeBegin);
		if (statusCodeEnd < 0) throw new IllegalResponseException("Cannot get statusCode");
		return new Pair<String, Integer>(
				rawTL1Response.substring(statusCodeBegin, statusCodeEnd), statusCodeEnd);
	}

	private static String getErrorCode(String rawTL1Response, int fromIndex)
			throws IllegalResponseException
	{
		if (rawTL1Response.indexOf(TL1Constants.MsgDelimiter.LINE_START, fromIndex) != fromIndex) throw new IllegalResponseException(
				"Cannot find errorCode");
		int errorCodeBegin = fromIndex + TL1Constants.MsgDelimiter.LINE_START.length();
		int errorCodeEnd =
				rawTL1Response.indexOf(TL1Constants.MsgDelimiter.LINE_END, errorCodeBegin);
		if (errorCodeEnd < 0) throw new IllegalResponseException("Cannot get errorCode");
		return rawTL1Response.substring(errorCodeBegin, errorCodeEnd);
	}

	private static Pair<Date, Integer> getTimestamp(String rawTL1Response)
			throws IllegalResponseException
	{
		int index = rawTL1Response.indexOf(' ');
		if (index < 0) throw new IllegalResponseException("Cannot find timestamp");
		++index;
		try
		{
			return new Pair<Date, Integer>(parseTL1Time(rawTL1Response.substring(index, index
					+ TIMESTAMP_LENGTH)), index + TIMESTAMP_LENGTH);
		}
		catch (Exception e)
		{
			throw new IllegalResponseException("Cannot get timestamp", e);
		}
	}

	private static Date parseTL1Time(String tl1Time) throws ParseException
	{
		synchronized (DATE_FORMAT)
		{
			return DATE_FORMAT.parse(tl1Time);
		}
	}
}
