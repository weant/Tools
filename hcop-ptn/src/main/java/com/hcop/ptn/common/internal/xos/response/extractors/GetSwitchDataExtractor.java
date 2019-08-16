package com.hcop.ptn.common.internal.xos.response.extractors;

import com.alu.tools.basic.collection.Container;
import com.hcop.ptn.common.internal.xos.request.types.GetSwitchDataRequest;
import com.hcop.ptn.common.internal.xos.response.IResponseExtractor;
import com.hcop.ptn.common.internal.xos.response.IllegalResponseException;
import com.lucent.oms.xml.naInterface.Message_T;
import com.lucent.oms.xml.naInterface.ProtMgr_I_RetrieveSNCswitchDataresp_T;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GetSwitchDataExtractor implements IResponseExtractor<ProtMgr_I_RetrieveSNCswitchDataresp_T> {

	final private static Log log = LogFactory.getLog(GetSwitchDataExtractor.class);
	
	@Override
	public void extractResponse(Message_T response,
			Container<ProtMgr_I_RetrieveSNCswitchDataresp_T> data)
			throws IllegalResponseException {
		if (response.getHdr().getOpRespStatus().equals("FAILURE")) {
			log.error("get SwitchData failed.");
		} else {
			if (!response.getHdr().getOpName().equals(GetSwitchDataRequest.REQ_OP_NAME)) {
				throw IllegalResponseException.create("Invalid data in response", response);
			}

			ProtMgr_I_RetrieveSNCswitchDataresp_T protMgr_I_RetrieveSNCswitchDataresp = response.getProtMgr_I_RetrieveSNCswitchDataresp();
			if(protMgr_I_RetrieveSNCswitchDataresp != null) {
				data.set(protMgr_I_RetrieveSNCswitchDataresp);
			}
		}
	}

}
