package com.otn.tool.common.internal.xos.tl1.response;

import com.alu.tools.basic.NullUtil;
import com.alu.tools.basic.StringUtil;
import com.otn.tool.common.internal.xos.tl1.util.TL1Constants.MsgDelimiter;

public class StateParam
{
	private String pst;

	private String sst;

	public void setPST(String pst)
	{
		this.pst = NullUtil.nullToEmpty(pst);
	}

	public void setSST(String sst)
	{
		this.sst = NullUtil.nullToEmpty(sst);
	}

	public String getPst()
	{
		return pst;
	}

	public String getSst()
	{
		return sst;
	}

	@Override
	public String toString()
	{
		StringBuilder output = new StringBuilder();
		if (!StringUtil.isBlank(pst)) output.append(pst);
		if (!StringUtil.isBlank(sst)) output.append(MsgDelimiter.STATE_PARAM_DELIMITER).append(
				sst);
		return output.toString();
	}
}