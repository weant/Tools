package com.otn.tool.common.internal.xos.response.extractors;

import com.alu.tools.basic.collection.Container;
import com.lucent.oms.xml.naInterface.Message_T;
import com.otn.tool.common.internal.xos.response.IResponseExtractor;
import com.otn.tool.common.internal.xos.response.IllegalResponseException;
import com.otn.tool.common.internal.xos.tl1.response.TL1Response;
import com.otn.tool.common.internal.xos.tl1.response.TL1ResponseParser;

public class TL1ResponseExtractor implements IResponseExtractor<TL1Response>
{
	public void extractResponse(Message_T response, Container<TL1Response> data)
	{
		TL1Response tl1Response =
				TL1ResponseParser.parse(response
						.getTL1CutThroughMgr_I_SendTL1Commandresp().getResp().getTl1Response());
		if (tl1Response == null) throw new IllegalResponseException(
				"Illegal response, cannot parse to TL1Response");
		data.set(tl1Response);
	}
}
