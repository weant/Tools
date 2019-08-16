package com.hcop.ptn.common.internal.xos.tl1.response;

import com.alu.tools.basic.collection.CollectionUtil;
import com.hcop.ptn.common.internal.xos.tl1.util.TL1Format;

import java.util.*;

public class TL1MessageData
{
	private boolean isError;

	private List<String> aidList = new LinkedList<String>();

	private List<String> positionalParamList = new LinkedList<String>();

	private Map<String, String> keywordParamList = new HashMap<String, String>();

	private StateParam stateParam = new StateParam();

	@Override
	public String toString()
	{
		return TL1Format.formatTL1MessageData(this);
	}

	void setError(boolean isError)
	{
		this.isError = isError;
	}

	public boolean isError()
	{
		return isError;
	}

	void addAid(String... aid)
	{
		CollectionUtil.addAll(aidList, aid);
	}

	public List<String> getAidList()
	{
		return Collections.unmodifiableList(aidList);
	}

	public String getAid(int index)
	{
		return aidList.get(index);
	}

	public boolean hasAid()
	{
		return !aidList.isEmpty();
	}

	public int aidCount()
	{
		return aidList.size();
	}

	public String getAid()
	{
		return aidList.get(0);
	}

	void addPositionalParam(String... positionalParam)
	{
		CollectionUtil.addAll(positionalParamList, positionalParam);
	}

	public List<String> getPositionalParamList()
	{
		return Collections.unmodifiableList(positionalParamList);
	}

	public int positionalParamCount()
	{
		return positionalParamList.size();
	}

	public String getPositionalParam(int index)
	{
		return positionalParamList.get(index);
	}

	void addKeywordParam(String name, String value)
	{
		keywordParamList.put(name, value);
	}

	public Map<String, String> getKeywordParamList()
	{
		return Collections.unmodifiableMap(keywordParamList);
	}

	public boolean containsKeywordParam(String name)
	{
		return keywordParamList.containsKey(name);
	}

	public String getKeywordParam(String name)
	{
		return keywordParamList.get(name);
	}

	void setPST(String pst)
	{
		stateParam.setPST(pst);
	}

	void setSST(String sst)
	{
		stateParam.setSST(sst);
	}

	void setState(String pst, String sst)
	{
		setPST(pst);
		setSST(sst);
	}

	public StateParam getStateParam()
	{
		return stateParam;
	}
}
