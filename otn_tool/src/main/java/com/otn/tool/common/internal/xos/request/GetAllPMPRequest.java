package com.otn.tool.common.internal.xos.request;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lucent.oms.xml.naInterface.DsCommon_TPname_T;
import com.lucent.oms.xml.naInterface.DsPerfMgnt_AllPMTPreq_T;
import com.lucent.oms.xml.naInterface.LayerDefs_LayerRateList_T;
import com.lucent.oms.xml.naInterface.PerfMgr_I_GetAllPMTPrqst_T;

public class GetAllPMPRequest extends MultiXosRequest {

	public static final String REQ_OP_NAME = "PerfMgr_I_getAllPMTPs_si";

	public static final String REQ_BODY_ROOT = "PerfMgr_I_getAllPMTPrqst";

	public static final String NEXT_REQ_OP_NAME = "PerfMgr_I_getAllPMTPs_sinext_n";

	public static final String NEXT_REQ_BODY_ROOT = "PerfMgr_I_getAllPMTPnext_nrqst";

	private PerfMgr_I_GetAllPMTPrqst_T body;
	
	private static Log log = LogFactory.getLog(GetAllPMPRequest.class);

	public GetAllPMPRequest(String ncName, String neName, String ptpName,
			String ftpName, String ctpName) {
		
		//log.error("+++ncName:" + ncName + " neName:" + neName + " ptpName:" + ptpName + " ftpName:" + ftpName + " ctpName:" + ctpName);
		
		body = buildReqBody(ncName, neName, ptpName, ftpName, ctpName);

		init(REQ_OP_NAME, REQ_BODY_ROOT, NEXT_REQ_OP_NAME, NEXT_REQ_BODY_ROOT,
				body);
	}

	public GetAllPMPRequest(String ncName, String neName) {
		this(ncName, neName, "", "", "");
	}

	private static PerfMgr_I_GetAllPMTPrqst_T buildReqBody(String ncName,
			String neName, String ptpName, String ftpName, String ctpName) {
		PerfMgr_I_GetAllPMTPrqst_T req = new PerfMgr_I_GetAllPMTPrqst_T();
		DsPerfMgnt_AllPMTPreq_T s = new DsPerfMgnt_AllPMTPreq_T();
		DsCommon_TPname_T tpName = new DsCommon_TPname_T();
		tpName.setNcName(ncName);
		tpName.setNeName(neName);
		tpName.setPtpName(ptpName);
		tpName.setFtpName(ftpName);
		tpName.setCtpName(ctpName);
		s.setTpName(tpName);
		s.setRate(new LayerDefs_LayerRateList_T());
		req.setReq(s);
		req.setHow_Many(1000000);

		return req;
	}

	@Override
	public String getNextReqBody() {
		return "<" + NEXT_REQ_BODY_ROOT + "><how_namy>"
				+ 1000000 + "</how_namy></"
				+ NEXT_REQ_BODY_ROOT + ">";
	}

}
