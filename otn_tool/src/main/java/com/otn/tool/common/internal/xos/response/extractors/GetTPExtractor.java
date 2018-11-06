package com.otn.tool.common.internal.xos.response.extractors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.otn.tool.common.internal.xos.request.types.GetTPRequest;
import com.otn.tool.common.internal.xos.response.IResponseExtractor;
import com.otn.tool.common.internal.xos.response.IllegalResponseException;
import com.alu.tools.basic.collection.Container;
import com.lucent.oms.xml.naInterface.Message_T;
import com.lucent.oms.xml.naInterface.TPmgr_I_GetTPresp_T;

public class GetTPExtractor implements IResponseExtractor<TPmgr_I_GetTPresp_T> {
	private static Log log = LogFactory.getLog(GetTPExtractor.class);
	
	@Override
	public void extractResponse(Message_T response, Container<TPmgr_I_GetTPresp_T> data)
			throws IllegalResponseException {
		TPmgr_I_GetTPresp_T resp = response.getTPmgr_I_GetTPresp();

		if (response.getHdr().getOpRespStatus().equals("FAILURE")) {
			log.error("get TP failed.");
		} else {
			/*
			DsTPdata_LayeredParameters_T[] aVals = resp.getTp().getTransmissionParams().getDsTPdata_LayeredParameterList_Telem();
			for(DsTPdata_LayeredParameters_T dlp : aVals) {
				for(GlobalDefs_NameAndValueString_T nav : dlp.getTransmissionParams().getGlobalDefs_NVSList_Telem()) {
					if("voaSet".equalsIgnoreCase(nav.getName())) {
						data.set(nav.getValue());
						return ;
					}
				}
				
				
			}
			*/
			if (!response.getHdr().getOpName().equals(GetTPRequest.REQ_OP_NAME)) {
				throw IllegalResponseException.create("Invalid data in response", response);
			}

			if(resp != null) {
				data.set(resp);
			}
		}
	}

}
