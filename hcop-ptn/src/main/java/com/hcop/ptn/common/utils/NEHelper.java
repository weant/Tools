package com.hcop.ptn.common.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * NEID 与groupID 和emlID相互转化的帮助类
 *
 */
public class NEHelper {

	/**
	 * PKT数据库C_NE表中的NEID高32位为groupID
	 * 
	 * @param pkt_neId
	 * @return
	 */
	public static int getGroupID(long pkt_neId) {
		return (int) (pkt_neId >> 32);
	}

	/**
	 * PKT数据库C_NE表中的NEID低32位为emlNEID
	 * 
	 * @param pkt_neId
	 * @return
	 */
	public static int getEmlNEID(long pkt_neId) {
		return (int) (pkt_neId & 0xFFFFFFFF);
	}

	/**
	 * 将groupId和emlNEID转化成PKT数据库C_NE表中对应的NEID
	 * 
	 * @param groupId
	 * @param emlNEId
	 * @return
	 */
	public static long getPktNEID(int groupId, int emlNEId) {
		NumberFormat fomatter = new DecimalFormat();
		return Long.parseLong(fomatter.format(groupId * (Math.pow(2, 32)) + emlNEId).replace(",", ""));
	}
}
