package com.hcop.otn.common.internal.xos.response;

import com.alu.tools.basic.collection.Container;
import com.lucent.oms.xml.naInterface.Message_T;

public interface IResponseExtractor<T>
{
	public void extractResponse(Message_T response, Container <T> data)
			throws IllegalResponseException;
}
