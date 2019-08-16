package com.hcop.otn.common.internal.xos.request.types;

import com.hcop.otn.common.internal.xos.request.SingleXosRequest;
import com.hcop.otn.common.internal.xos.tl1.request.TL1Request;
import com.lucent.oms.xml.naInterface.DsCommon_NEname_T;
import com.lucent.oms.xml.naInterface.DsTL1mgnt_TL1req_T;
import com.lucent.oms.xml.naInterface.TL1CutThroughMgr_I_SendTL1Commandrqst_T;

public class TL1CutThroughRequest extends SingleXosRequest
{
	public static final String REQ_OP_NAME = "TL1CutThroughMgr_I_sendTL1Command";

	public static final String REQ_BODY_ROOT = REQ_OP_NAME + "rqst";

	private TL1CutThroughMgr_I_SendTL1Commandrqst_T body;

	public TL1CutThroughRequest(String ncName, String neName, String tl1Command)
	{
		body = buildRequest(ncName, neName, tl1Command);
		init(REQ_OP_NAME, REQ_BODY_ROOT, body);
	}

	public TL1CutThroughRequest(String ncName, String neName, TL1Request tl1Request)
	{
		this(ncName, neName, tl1Request.toString());
	}

	public TL1CutThroughRequest()
	{
		this("", "", "");
	}

	public void setNcName(String ncName)
	{
		body.getReq().getNeName().setNcName(ncName);
	}

	public void setNeName(String neName)
	{
		body.getReq().getNeName().setNeName(neName);
	}

	public void setTL1Command(String tl1Command)
	{
		body.getReq().setTl1Command(tl1Command);
	}

	private static TL1CutThroughMgr_I_SendTL1Commandrqst_T buildRequest(String ncName,
                                                                        String neName, String tl1Command)
	{
		TL1CutThroughMgr_I_SendTL1Commandrqst_T req =
				new TL1CutThroughMgr_I_SendTL1Commandrqst_T();
		DsTL1mgnt_TL1req_T reqBody = new DsTL1mgnt_TL1req_T();
		DsCommon_NEname_T name = new DsCommon_NEname_T();
		name.setNcName(ncName);
		name.setNeName(neName);
		reqBody.setNeName(name);
		reqBody.setTl1Command(tl1Command);
		reqBody.setUserId("");
		req.setReq(reqBody);
		return req;
	}
}
