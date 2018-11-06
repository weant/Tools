package com.otn.tool.common.internal.csg.util;

import org.omg.CORBA.Any;

public class AnyUtil
{
	public interface IAnyInsert<T>
	{
		public void insert(Any any, T value);
	}

	public static Any createAny()
	{
		return OrbInstance.getInstance().createAny();
	}

	public static Any createAny(int value)
	{
		Any any = createAny();
		any.insert_long(value);
		return any;
	}

	public static Any createAny(long value)
	{
		Any any = createAny();
		any.insert_longlong(value);
		return any;
	}

	public static Any createAny(boolean value)
	{
		Any any = createAny();
		any.insert_boolean(value);
		return any;
	}

	public static Any createAny(String value)
	{
		Any any = createAny();
		any.insert_string(value);
		return any;
	}

	public static <T> Any createAny(T value, IAnyInsert<T> insert)
	{
		Any any = createAny();
		insert.insert(any, value);
		return any;
	}
}
