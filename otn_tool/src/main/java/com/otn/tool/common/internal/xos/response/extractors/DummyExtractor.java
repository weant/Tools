package com.otn.tool.common.internal.xos.response.extractors;

import com.alu.tools.basic.collection.Container;
import com.lucent.oms.xml.naInterface.Message_T;
import com.otn.tool.common.internal.xos.response.IResponseExtractor;

public class DummyExtractor implements IResponseExtractor<DummyExtractor.Null>
{
	public static class Null
	{
		public static Null instance = new Null();

		private Null()
		{
		}
	}

	public void extractResponse(Message_T response, Container<Null> data)
	{
		data.set(Null.instance);
	}
}
