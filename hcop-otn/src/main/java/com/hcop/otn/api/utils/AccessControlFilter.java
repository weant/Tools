package com.hcop.otn.api.utils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

public class AccessControlFilter implements ContainerResponseFilter
{
    @Override
    public void filter( ContainerRequestContext requestContext,
            ContainerResponseContext responseContext ) throws IOException
    {
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();

        headers.add( "Access-Control-Allow-Origin", "*" );
        headers.add( "Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH" );
        headers.add( "Access-Control-Allow-Headers",
            "X-Requested-With, Content-Type, Accept, Origin, access-control-allow-origin" );
    }
}