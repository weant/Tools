package com.otn.tool.common.internal.csg.util;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.TCKind;
import org.omg.CosNaming.NameComponent;

public class CsgUtil
{
	public static NameComponent[] toNeName(int neID)
	{
		return new NameComponent[] { new NameComponent(String.valueOf(neID), "") };
	}

	public static int[] toInstance(Any[] instance)
	{
		int[] value = new int[instance.length];
		int i = 0;
		for (Any a : instance)
		{
			value[i++] = a.extract_long();
		}
		return value;
	}

	public static String toString(Any[] instance)
	{
		StringBuilder sb = new StringBuilder();
		for (Any a : instance)
		{
			sb.append(a.extract_long()).append('.');
		}
		return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
	}

	public static String toString(int[] instance)
	{
		StringBuilder sb = new StringBuilder();
		for (int i : instance)
		{
			sb.append(i).append('.');
		}
		return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
	}

	public static String concat(String oid, int index)
	{
		return oid + '.' + index;
	}

	public static String concat(String oid, String index)
	{
		return oid + '.' + index;
	}

	public static Any[] toInstance(int... instance)
	{
		Any[] value = new Any[instance.length];
		int i = 0;
		for (int v : instance)
		{
			value[i++] = AnyUtil.createAny(v);
		}
		return value;
	}

	public static Any[] toInstance(String instance)
	{
		String[] numList = instance.split("\\.");
		Any[] value = new Any[numList.length];
		int i = 0;
		try
		{
			for (String num : numList)
			{
				value[i++] = AnyUtil.createAny(Integer.parseInt(num));
			}
			return value;
		}
		catch (NumberFormatException e)
		{
			throw new SnmpException("Invalid SNMP instance: '" + instance + "'");
		}
	}

	public static int extractEnum(Any any)
	{
		if (any.type().kind() != TCKind.tk_enum) throw new BAD_OPERATION(
				"Cannot extract enum, kind=" + any.type().kind().value());
		return any.create_input_stream().read_long();
	}
}