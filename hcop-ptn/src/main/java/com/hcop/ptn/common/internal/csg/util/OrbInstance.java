package com.hcop.ptn.common.internal.csg.util;

import com.hcop.ptn.common.jcorba.ORBWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.Any;
import org.omg.CORBA.UserException;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public class OrbInstance
{
    private static OrbInstance instance = new OrbInstance();

    private static Log log = LogFactory.getLog( OrbInstance.class );

    private OrbInstance()
    {
    }

    public static OrbInstance getInstance()
    {
        return OrbInstance.instance;
    }

    public void activeObject( org.omg.PortableServer.Servant obj )
            throws CorbaException
    {
        try
        {
            ORBWrapper.instance().getRootPOA().activate_object( obj );
        }
        catch( UserException e )
        {
            throw new CorbaException( "Failed to active Object", e );
        }
    }

    public void deactiveObject( org.omg.PortableServer.Servant obj )
            throws CorbaException
    {
        try
        {
            ORBWrapper
                    .instance()
                    .getRootPOA()
                    .deactivate_object(
                        ORBWrapper.instance().getRootPOA().servant_to_id( obj ) );
        }
        catch( UserException e )
        {
            throw new CorbaException( "Failed to deactive Object", e );
        }
    }

    public boolean tryDeactiveObject( org.omg.PortableServer.Servant obj )
    {
        try
        {
            deactiveObject( obj );
            return true;
        }
        catch( CorbaException e )
        {
            log.warn( "Failed to deactive object", e );
            return false;
        }
    }

    public org.omg.CORBA.Object stringToObject( String str )
    {
        return ORBWrapper.instance().getORB().string_to_object( str );
    }

    public org.omg.CORBA.Object resolveNSPath( String nameServiceURL,
            String namePath ) throws CorbaException
    {
        NamingContextExt rootNamingContext = NamingContextExtHelper
                .narrow( stringToObject( nameServiceURL ) );
        String[] path = namePath.split( "/" );
        NameComponent[] names = new NameComponent[path.length];
        try
        {
            for( int i = 0; i < path.length; ++i )
            {
                NameComponent[] nc = rootNamingContext.to_name( path[i] );
                names[i] = nc[0];
            }
            return rootNamingContext.resolve( names );
        }
        catch( UserException e )
        {
            throw new CorbaException( "Failed to resolve '" + namePath
                    + "' on name service: '" + nameServiceURL + "'", e );
        }
    }

    public Any createAny()
    {
        return ORBWrapper.instance().getORB().create_any();
    }
}