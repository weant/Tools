package com.hcop.ptn.common.internal.xos.util;

import com.alu.tools.basic.exception.BasicRuntimeException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public class CastorUtil
{
	public static void marshal(Object obj, Writer output, String rootName)
			throws BasicRuntimeException
	{
		marshal(obj, output, rootName, true);
	}

	public static void marshalNode(Object obj, Writer output, String rootName)
			throws BasicRuntimeException
	{
		marshal(obj, output, rootName, false);
	}

	public static void marshal(Object obj, Writer output) throws BasicRuntimeException
	{
		marshal(obj, output, true);
	}

	public static void marshalNode(Object obj, Writer output) throws BasicRuntimeException
	{
		marshal(obj, output, false);
	}

	public static String marshal(Object obj, String rootName) throws BasicRuntimeException
	{
		return marshal(obj, rootName, true);
	}

	public static String marshalNode(Object obj, String rootName) throws BasicRuntimeException
	{
		return marshal(obj, rootName, false);
	}

	public static String marshal(Object obj) throws BasicRuntimeException
	{
		return marshal(obj, true);
	}

	public static String marshalNode(Object obj) throws BasicRuntimeException
	{
		return marshal(obj, false);
	}

	private static void marshal(Object obj, Writer output, String rootName,
			boolean marshalAsDocument) throws BasicRuntimeException
	{
		try
		{
			Marshaller marshaller = new Marshaller(output);
			if (rootName != null) marshaller.setRootElement(rootName);
			marshaller.setMarshalAsDocument(marshalAsDocument);
			marshaller.marshal(obj);
		} catch (Exception e) {
			throw new BasicRuntimeException("Failed to marshal object", e);
		}
	}

	private static void marshal(Object obj, Writer output, boolean marshalAsDocument)
			throws BasicRuntimeException
	{
		marshal(obj, output, null, marshalAsDocument);
	}

	private static String marshal(Object obj, String rootName, boolean marshalAsDocument)
			throws BasicRuntimeException
	{
		StringWriter output = new StringWriter();
		marshal(obj, output, rootName, marshalAsDocument);
		return output.toString();
	}

	private static String marshal(Object obj, boolean marshalAsDocument)
			throws BasicRuntimeException
	{
		return marshal(obj, (String) null, marshalAsDocument);
	}

	@SuppressWarnings("unchecked")
	public static <T> T unmarshal(String str, Class<T> clazz) throws BasicRuntimeException
	{
		StringReader in = new StringReader(str);
		try
		{
			Unmarshaller unmarshaller = new Unmarshaller(clazz);
			unmarshaller.setWhitespacePreserve(true);
			return (T) unmarshaller.unmarshal(in);
		}
		catch (Exception e)
		{
			throw new BasicRuntimeException("Failed to unmarshal string", e);
		}
	}
}
