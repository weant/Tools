package com.hcop.ptn.common.internal.xos.response.extractors;

import com.alu.tools.basic.collection.Container;
import com.hcop.ptn.common.internal.xos.response.IResponseExtractor;
import com.hcop.ptn.common.internal.xos.response.IllegalResponseException;
import com.hcop.ptn.common.internal.xos.tl1.response.TL1Response;
import com.hcop.ptn.common.internal.xos.tl1.response.TL1ResponseParser;
import com.lucent.oms.xml.naInterface.Message_T;

public class TL1ResponseExtractor implements IResponseExtractor<TL1Response>
{
	@Override
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
