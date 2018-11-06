package com.otn.tool.common.internal.xos.tl1.response;

import com.otn.tool.common.internal.xos.tl1.util.TL1Constants;
import com.alu.tools.basic.NullUtil;
import com.alu.tools.basic.StringUtil;
import com.alu.tools.basic.collection.Pair;

public class TL1GenericMessageParser
{
	private TL1MessageDataParser parser;

	public TL1GenericMessageParser(TL1MessageDataParser parser)
	{
		this.parser = NullUtil.notNull(parser);
	}

	public void parse(TL1GenericMessage message, String rawTL1Message, int fromIndex)
	{
		int index = fromIndex;
		while (true)
		{
			Pair<String, Integer> lineDataAndIndex = getNextResponseLine(rawTL1Message, index);
			if (lineDataAndIndex == null) break;
			addLineData(message, lineDataAndIndex.getFirst());
			index = lineDataAndIndex.getSecond();
		}
	}

	private void addLineData(TL1GenericMessage message, String lineData)
	{
		if (isResponseData(lineData))
		{
			TL1MessageData responseData = parser.toMessageData(lineData);
			if (responseData != null) message.addResponseData(responseData);
		}
		else if (isCommentData(lineData))
		{
			message.addComment(toCommentData(lineData));
		}
	}

	private static Pair<String, Integer> getNextResponseLine(String rawTL1Message, int fromIndex)
	{
		int startIndex = fromIndex;
		while (true)
		{
			startIndex = rawTL1Message.indexOf(TL1Constants.MsgDelimiter.LINE_START, startIndex);
			if (startIndex < 0) return null;
			startIndex += TL1Constants.MsgDelimiter.LINE_START.length();
			if (rawTL1Message.startsWith(TL1Constants.MsgDelimiter.DATA_START, startIndex)
					|| rawTL1Message
							.startsWith(TL1Constants.MsgDelimiter.COMMENT_START, startIndex)) break;
		}
		int endIndex = startIndex;
		while (true)
		{
			endIndex = rawTL1Message.indexOf(TL1Constants.MsgDelimiter.LINE_END, endIndex);
			if (endIndex < 0) return null;
			String data = rawTL1Message.substring(0, endIndex);
			if (data.endsWith(TL1Constants.MsgDelimiter.DATA_END)
					|| data.endsWith(TL1Constants.MsgDelimiter.COMMENT_END)) break;
			endIndex += TL1Constants.MsgDelimiter.LINE_END.length();
		}
		String line = rawTL1Message.substring(startIndex, endIndex);
		return new Pair<String, Integer>(line, endIndex);
	}

	private static String toCommentData(String lineData)
	{
		return StringUtil.cut(lineData, TL1Constants.MsgDelimiter.COMMENT_START.length(),
				TL1Constants.MsgDelimiter.COMMENT_END.length());
	}

	private static boolean isResponseData(String lineData)
	{
		return lineData.startsWith(TL1Constants.MsgDelimiter.DATA_START)
				&& lineData.endsWith(TL1Constants.MsgDelimiter.DATA_END)
				&& lineData.length() > TL1Constants.MsgDelimiter.DATA_START.length()
						+ TL1Constants.MsgDelimiter.DATA_END.length();
	}

	private static boolean isCommentData(String lineData)
	{
		return lineData.startsWith(TL1Constants.MsgDelimiter.COMMENT_START)
				&& lineData.endsWith(TL1Constants.MsgDelimiter.COMMENT_END)
				&& lineData.length() > TL1Constants.MsgDelimiter.COMMENT_START.length()
						+ TL1Constants.MsgDelimiter.COMMENT_END.length();
	}
}
