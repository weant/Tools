package com.hcop.otn.common.internal.xos.response.extractors;

import com.alu.tools.basic.collection.Container;
import com.hcop.otn.common.internal.xos.response.IResponseExtractor;
import com.lucent.oms.xml.naInterface.Message_T;

public class DummyExtractor implements IResponseExtractor<DummyExtractor.Null>
{
	public static class Null
	{
		public static Null instance = new Null();

		private Null()
		{
		}
	}

	@Override
	public void extractResponse(Message_T response, Container<Null> data)
	{
		data.set(Null.instance);
	}
}
