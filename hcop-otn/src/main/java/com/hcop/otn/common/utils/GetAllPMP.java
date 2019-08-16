package com.hcop.otn.common.utils;

import com.hcop.otn.common.internal.xos.request.PMP;
import com.hcop.otn.common.internal.xos.request.XosPmpService;

import java.util.LinkedList;
import java.util.List;

public class GetAllPMP {
	
	public static List<PMP> getAllPMPs(int groupId, int neId, String neName) {
		List<PMP> pmpList = new LinkedList<PMP>();
		pmpList.addAll(XosPmpService.getAllPMP(groupId, neId, neName, "", "", ""));
		return pmpList;
	}

}
