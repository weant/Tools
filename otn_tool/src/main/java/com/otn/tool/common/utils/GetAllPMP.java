package com.otn.tool.common.utils;

import java.util.LinkedList;
import java.util.List;

import com.otn.tool.common.internal.xos.request.PMP;
import com.otn.tool.common.internal.xos.request.XosPmpService;

public class GetAllPMP {
	
	public static List<PMP> getAllPMPs(int groupId, int neId, String neName) {
		List<PMP> pmpList = new LinkedList<PMP>();
		pmpList.addAll(XosPmpService.getAllPMP(groupId, neId, neName, "", "", ""));
		return pmpList;
	}

}
