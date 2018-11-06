package com.otn.tool.common.internal.xos.request.types;

import com.otn.tool.common.internal.xos.request.SingleXosRequest;
import com.lucent.oms.xml.naInterface.DsCommon_TPname_T;
import com.lucent.oms.xml.naInterface.TPmgr_I_GetTPrqst_T;

public class GetTPRequest extends SingleXosRequest {
	public static final String REQ_OP_NAME = "TPmgr_I_getTP";

	public static final String REQ_BODY_ROOT = REQ_OP_NAME + "rqst";

	private TPmgr_I_GetTPrqst_T body;

	public GetTPRequest(String ncName, 
			String neName,
			String ptpName,
			String ctpName,
			String tcmName,
			String ftpName) {
		
		body = buildReqBody(ncName, 
				neName,
				ptpName,
				ctpName,
				tcmName,
				ftpName);
		
		init(REQ_OP_NAME, REQ_BODY_ROOT, body);
	}

	private TPmgr_I_GetTPrqst_T buildReqBody(String ncName, 
			String neName,
			String ptpName,
			String ctpName,
			String tcmName,
			String ftpName) {
		
		DsCommon_TPname_T tpName = new DsCommon_TPname_T();
		tpName.setNcName(ncName);
		tpName.setNeName(neName);
		tpName.setPtpName(ptpName);
		tpName.setCtpName(ctpName);
		tpName.setTcmName(tcmName);
		tpName.setFtpName(ftpName);
		
		TPmgr_I_GetTPrqst_T req = new TPmgr_I_GetTPrqst_T();
		req.setTpName(tpName);
		
		return req;
	}

}
