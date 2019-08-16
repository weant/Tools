package com.hcop.otn.common.internal.xos.tl1.util;

import com.alu.tools.basic.StringJoiner;
import com.alu.tools.basic.StringJoiner.IStringFormat;
import com.hcop.otn.common.internal.xos.tl1.request.TL1Request;
import com.hcop.otn.common.internal.xos.tl1.response.StateParam;
import com.hcop.otn.common.internal.xos.tl1.response.TL1MessageData;
import com.hcop.otn.common.internal.xos.tl1.response.TL1Response;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TL1Format
{
	private static final String EMPTY_TID = "";

	private static final String EMPTY_CTAG = "";

	private static final String EMPTY_GENERAL = "";

	public static String formatAid(List<String> aidList, String delimiter)
	{
		return StringJoiner.join(delimiter, aidList);
	}

	public static String formatPositionalParam(List<String> positionalParamList)
	{
		return StringJoiner.join(TL1Constants.MsgDelimiter.POSITIONAL_PARAM_DELIMITER, positionalParamList);
	}

	public static String formatKeywordParam(Map<String, String> keywordParam)
	{
		return StringJoiner.join(TL1Constants.MsgDelimiter.KEYWORD_PARAM_DELIMITER,
				new IStringFormat<Entry<String, String>>()
				{
					public void format(StringBuilder output, Entry<String, String> s)
					{
						output.append(s.getKey()).append(TL1Constants.MsgDelimiter.EQUAL).append(s.getValue());
					}
				}, keywordParam.entrySet());
	}

	public static String formatStateParam(StateParam stateParam)
	{
		return stateParam.toString();
	}

	public static String formatTL1Request(TL1Request request)
	{
		StringBuilder output = new StringBuilder();
		StringJoiner.join(output, TL1Constants.MsgDelimiter.PART_DELIMITER, request.getVerb(), EMPTY_TID,
				formatAid(request.getAidList(), TL1Constants.MsgDelimiter.AID_JOINT), EMPTY_CTAG,
				EMPTY_GENERAL, formatPositionalParam(request.getPositionalParamList()),
				formatKeywordParam(request.getKeywordParamList()),
				formatStateParam(request.getStateParam()));
		for (int i = 0; i < 4; i++)
		{
			if (TL1Constants.MsgDelimiter.PART_DELIMITER.equals(output.substring(output.length() - 1))) output
					.setLength(output.length() - 1);
			else break;
		}
		return output.append(TL1Constants.MsgDelimiter.REQUEST_END).toString();
	}

	public static String formatTL1MessageData(TL1MessageData responseData)
	{
		StringBuilder output = new StringBuilder();
		StringJoiner.join(output, TL1Constants.MsgDelimiter.PART_DELIMITER,
				formatAid(responseData.getAidList(), TL1Constants.MsgDelimiter.AID_DELIMITER),
				formatPositionalParam(responseData.getPositionalParamList()),
				formatKeywordParam(responseData.getKeywordParamList()),
				formatStateParam(responseData.getStateParam()));
		return output.toString();
	}

	public static String formatTL1Response(TL1Response response)
	{
		StringBuilder output = new StringBuilder();
		output.append("Timestamp: ").append(response.getTimestamp()).append(", Status: ")
				.append(response.getStatus()).append(", ErrorCode: ")
				.append(response.getErrorCode()).append("\nDate:\n");
		StringJoiner.joinObj(output, "\n", response.getDataList());
		output.append("\nComment:\n");
		StringJoiner.join(output, "\n", response.getCommentList());
		return output.toString();
	}
}
