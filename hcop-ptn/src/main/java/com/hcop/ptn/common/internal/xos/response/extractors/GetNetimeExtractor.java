package com.hcop.ptn.common.internal.xos.response.extractors;

import com.alu.tools.basic.collection.Container;
import com.hcop.ptn.common.internal.xos.response.IResponseExtractor;
import com.hcop.ptn.common.internal.xos.response.IllegalResponseException;
import com.lucent.oms.xml.naInterface.Message_T;
import com.lucent.oms.xml.naInterface.NEmgrExt_I_GetNEtimeresp_T;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;

public class GetNetimeExtractor implements IResponseExtractor<Date> {
	private static Log LOG = LogFactory.getLog(GetNetimeExtractor.class);

	/*
	 * private static DateFormat UTC_TMF_FORMAT = new SimpleDateFormat(
	 * "yyyyMMddHHmmss.0'Z'");
	 */

	@Override
	public void extractResponse(Message_T response, Container<Date> data)
			throws IllegalResponseException {

		NEmgrExt_I_GetNEtimeresp_T resp = response
				.getNEmgrExt_I_GetNEtimeresp();

		if (response.getHdr().getOpRespStatus().equals("FAILURE")) {
			LOG.error("can't get ne time");

		} else {
			long time = resp.getNeTime().getTime();
			Date date = new Date(time);
			// data.set(UTC_TMF_FORMAT.format(date));
			data.set(date);
			return;
		}
	}

}
