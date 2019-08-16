package com.hcop.otn.common.internal.xos.request.types;

import com.hcop.otn.common.internal.xos.request.SingleXosRequest;
import com.lucent.oms.xml.naInterface.DsCommon_TPname_T;
import com.lucent.oms.xml.naInterface.ProtMgr_I_RetrieveSNCswitchDatarqst_T;

public class GetSwitchDataRequest extends SingleXosRequest {
	public static final String REQ_OP_NAME = "ProtMgr_I_retrieveSNCswitchData";

	public static final String REQ_BODY_ROOT = REQ_OP_NAME + "rqst";

	private ProtMgr_I_RetrieveSNCswitchDatarqst_T body;

	public GetSwitchDataRequest(String ncName, String neName,
			String ptpName, String ftpName, 
			String ctpName, String tcmNme) {
		
		body = buildReqBody(ncName, neName, ptpName, ftpName, ctpName, tcmNme);
		init(REQ_OP_NAME, REQ_BODY_ROOT, body);
		
	}

	private static ProtMgr_I_RetrieveSNCswitchDatarqst_T buildReqBody(
			String ncName, String neName, 
			String ptpName, String ftpName,
			String ctpName, String tcmNme) {
		
		ProtMgr_I_RetrieveSNCswitchDatarqst_T req = new ProtMgr_I_RetrieveSNCswitchDatarqst_T();
		DsCommon_TPname_T _ctpName = new DsCommon_TPname_T();
		_ctpName.setNcName(ncName);
		_ctpName.setNeName(neName);
		_ctpName.setPtpName(ptpName);
		_ctpName.setFtpName(ftpName);
		_ctpName.setCtpName(ctpName);
		_ctpName.setTcmName(tcmNme);

		req.setCtpName(_ctpName);

		return req;
	}
}
