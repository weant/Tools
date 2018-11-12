package com.otn.tool.common.internal.xos.response.extractors;

import com.alu.tools.basic.collection.Container;
import com.lucent.oms.xml.naInterface.Message_T;
import com.otn.tool.common.internal.xos.response.IResponseExtractor;

public class TL1RawResponseExtractor implements IResponseExtractor<String>
{
	public void extractResponse(Message_T response, Container<String> data)
	{
		data.set(response.getTL1CutThroughMgr_I_SendTL1Commandresp().getResp().getTl1Response());
	}
}
