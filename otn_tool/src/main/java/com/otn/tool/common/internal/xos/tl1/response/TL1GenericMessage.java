package com.otn.tool.common.internal.xos.tl1.response;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TL1GenericMessage
{
	private List<TL1MessageData> dataList = new LinkedList<TL1MessageData>();

	private List<String> commentList = new LinkedList<String>();

	public List<TL1MessageData> getDataList()
	{
		return Collections.unmodifiableList(dataList);
	}

	public List<String> getCommentList()
	{
		return Collections.unmodifiableList(commentList);
	}

	void addResponseData(TL1MessageData responseData)
	{
		dataList.add(responseData);
	}

	void addComment(String comment)
	{
		commentList.add(comment);
	}
}
