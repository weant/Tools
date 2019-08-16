package com.hcop.otn.common.internal.xos.request.types;

import com.hcop.otn.common.internal.xos.request.SingleXosRequest;
import com.lucent.oms.xml.naInterface.DsCommon_NEId_T;
import com.lucent.oms.xml.naInterface.GlobalDefs_NVSList_T;
import com.lucent.oms.xml.naInterface.GlobalDefs_NameAndValueString_T;
import com.lucent.oms.xml.naInterface.NEmgrExt_I_GetNEtimerqst_T;

public class GetNeTimeRequest extends SingleXosRequest {
	public static final String REQ_OP_NAME = "NEmgrExt_I_getNEtime";

	public static final String REQ_BODY_ROOT = REQ_OP_NAME + "rqst";

	private NEmgrExt_I_GetNEtimerqst_T body;

	public GetNeTimeRequest(String ncName, String neName) {
		body = buildReqBody(ncName, neName);
		init(REQ_OP_NAME, REQ_BODY_ROOT, body);
	}

	private NEmgrExt_I_GetNEtimerqst_T buildReqBody(String ncName, String neName) {
		NEmgrExt_I_GetNEtimerqst_T req = new NEmgrExt_I_GetNEtimerqst_T();
		// 构建参数
		DsCommon_NEId_T NeId = new DsCommon_NEId_T();
		NeId.setNeGroupId(ncName);
		NeId.setNeId(neName);

		GlobalDefs_NameAndValueString_T vGlobalDefs_NVSList_Telem = new GlobalDefs_NameAndValueString_T();

		GlobalDefs_NVSList_T NVSList = new GlobalDefs_NVSList_T();
		NVSList.addGlobalDefs_NVSList_Telem(vGlobalDefs_NVSList_Telem);

		req.setInAddInfo(NVSList);
		req.setNeIdentifier(NeId);
		return req;
	}
}
