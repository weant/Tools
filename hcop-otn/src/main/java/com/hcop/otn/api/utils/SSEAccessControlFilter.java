package com.hcop.otn.api.utils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

public class SSEAccessControlFilter implements Filter
{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse hsr = (HttpServletResponse) response;

        hsr.addHeader("Access-Control-Allow-Origin", "*");
        hsr.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH");
        hsr.addHeader("Access-Control-Allow-Headers",
                "X-Requested-With, Content-Type, Accept, Origin, access-control-allow-origin");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}