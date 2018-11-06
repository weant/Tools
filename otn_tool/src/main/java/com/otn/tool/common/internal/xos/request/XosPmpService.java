package com.otn.tool.common.internal.xos.request;

import java.util.List;

import com.otn.tool.common.internal.xos.response.extractors.GetAllPMPExtractor;
import com.otn.tool.common.internal.xos.util.XosOperation;

public class XosPmpService {

	public static List<PMP> getAllPMP(int groupId, int neId, String neName,
			String ptpName, String ftpName, String ctpName)
			throws RequestException {
		List<PMP> list = XosOperation
				.getResult(groupId,
						new GetAllPMPRequest(RequestUtil.getNcName(groupId),
								RequestUtil.getNeName(neId, neName), ptpName,
								ftpName, ctpName), new GetAllPMPExtractor(
								groupId, neId, neName));
		return list;
	}

}
