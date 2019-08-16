package com.hcop.otn.common.internal.xos.response.extractors;

import com.alu.tools.basic.collection.Container;
import com.hcop.otn.common.internal.xos.response.IResponseExtractor;
import com.hcop.otn.common.internal.xos.response.IllegalResponseException;
import com.lucent.oms.xml.naInterface.*;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class GetOpPowerExtractor implements IResponseExtractor<DynaBean> {

	private static Log log = LogFactory.getLog(GetOpPowerExtractor.class);

	@Override
	public void extractResponse(Message_T response, Container<DynaBean> data)
			throws IllegalResponseException {
		DynaBean bean = new LazyDynaMap();
		data.set(bean);
		// TPmgr_I_getTPWithAdditionalInforesp
		TPmgr_I_GetTPWithAdditionalInforesp_T tpResponse = response
				.getTPmgr_I_GetTPWithAdditionalInforesp();
		if (response.getHdr().getOpRespStatus().equals("FAILURE")) {
			log.error("Extracor fail and the error is:" + response.toString());
			return;
		} else {
			DsTPdata_TerminationPoint_T tp = tpResponse.getTp();
			for (DsTPdata_LayeredParameters_T layeredParam : tp
					.getTransmissionParams()
					.getDsTPdata_LayeredParameterList_Telem()) {
				for (GlobalDefs_NameAndValueString_T param : layeredParam
						.getTransmissionParams().getGlobalDefs_NVSList_Telem()) {
					log.info(param.getName()+"****"+param.getValue());
					if (("receivePower".equalsIgnoreCase(param.getName()))
							|| ("totalInputPower".equalsIgnoreCase(param
									.getName()))) {
						bean.set("rx", convertValue(param.getValue()));
					}
					else if(("transmitPower".equalsIgnoreCase(param.getName()))
							|| ("totalOutputPower".equalsIgnoreCase(param
									.getName()))) {
						bean.set("tx", convertValue(param.getValue()));
					}
				}
			}
		}
		return;
	}
	
	
	private String convertValue(String value) {
		String start = null;
		String convertValue = null;
		if(value.startsWith("-")) {
			start = "-";
			value = value.substring(1, value.length());
		}
		int length = value.length();
		if(length == 1) {
			convertValue = "0" + "." + "0" + value;
		}
		else if(length == 2) {
			convertValue = "0" + "." + value;
		}
		else {
			int index = length - 2;
			String str1 = value.substring(0, index);
			String str2 = value.substring(index);
			convertValue = str1 + "." + str2;
		}
		if(start == null) {
			return convertValue;
		}
		else {
			return start + convertValue;
		}
	}

}
