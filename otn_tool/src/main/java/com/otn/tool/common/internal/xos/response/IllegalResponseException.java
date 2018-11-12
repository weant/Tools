package com.otn.tool.common.internal.xos.response;

import com.lucent.oms.xml.naInterface.Message_T;
import com.otn.tool.common.internal.xos.util.CastorUtil;
import com.otn.tool.common.internal.xos.util.XosException;

public class IllegalResponseException extends XosException
{
	private static final long serialVersionUID = -8338566482758072726L;

	public IllegalResponseException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public IllegalResponseException(String message)
	{
		super(message);
	}
	 public static IllegalResponseException create( String message,
	            Message_T response )
	    {
	        try
	        {
	            return new IllegalResponseException( message + "\nResponse:"
	                    + CastorUtil.marshalNode( response ) );
	        }
	        catch( Exception e )
	        {
	            return new IllegalResponseException( message );
	        }
	    }
}
