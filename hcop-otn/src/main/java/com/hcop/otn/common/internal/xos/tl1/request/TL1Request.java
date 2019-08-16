package com.hcop.otn.common.internal.xos.tl1.request;

import com.alu.tools.basic.ArgumentUtil;
import com.alu.tools.basic.NullUtil;
import com.hcop.otn.common.internal.xos.tl1.response.StateParam;
import com.hcop.otn.common.internal.xos.tl1.util.TL1Format;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TL1Request
{
	private static final String EMPTY_PARAM = "";

	private String verb;

	private List<String> aidList = new LinkedList<String>();

	private List<String> positionalParamList = new LinkedList<String>();

	private Map<String, String> keywordParamList = new HashMap<String, String>();

	private StateParam stateParam = new StateParam();

	public TL1Request(String verb, String aid)
	{
		setVerb(verb);
		setAid(aid);
	}

	public TL1Request(String verb)
	{
		setVerb(verb);
		setAid("");
	}

	@Override
	public String toString()
	{
		return TL1Format.formatTL1Request(this);
	}

	public String getVerb()
	{
		return verb;
	}

	public void setVerb(String verb)
	{
		this.verb = ArgumentUtil.notBlank(verb);
	}

	public List<String> getAidList()
	{
		return aidList;
	}

	public void setAid(String aid)
	{
		NullUtil.notNull(aid);
		aidList.clear();
		aidList.add(aid);
	}

	public void addAid(String aid)
	{
		aidList.add(ArgumentUtil.notBlank(aid));
	}

	public List<String> getPositionalParamList()
	{
		return positionalParamList;
	}

	public void addPositionalParam(String param)
	{
		positionalParamList.add(NullUtil.notNull(param));
	}

	public void setPositionalParam(int index, String param)
	{
		ArgumentUtil.checkMin(index, 0);
		NullUtil.notNull(param);
		if (index >= positionalParamList.size())
		{
			for (int i = positionalParamList.size(); i < index; i++)
			{
				positionalParamList.add(EMPTY_PARAM);
			}
			positionalParamList.add(param);
		}
		else
		{
			positionalParamList.set(index, param);
		}
	}

	public void clearPositionalParam()
	{
		positionalParamList.clear();
	}

	public Map<String, String> getKeywordParamList()
	{
		return keywordParamList;
	}

	public void putKeywordParam(String name, String value)
	{
		keywordParamList.put(ArgumentUtil.notBlank(name), ArgumentUtil.notBlank(value));
	}

	public void removeKeywordParam(String name)
	{
		keywordParamList.remove(name);
	}

	public void clearKeywordParam()
	{
		keywordParamList.clear();
	}

	public StateParam getStateParam()
	{
		return stateParam;
	}

	public void setPST(String pst)
	{
		stateParam.setPST(pst);
	}

	public void setSST(String sst)
	{
		stateParam.setSST(sst);
	}
}
