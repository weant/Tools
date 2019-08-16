package com.hcop.ptn.servlet;

import org.eclipse.jetty.servlets.EventSource;
import org.eclipse.jetty.servlets.EventSourceServlet;

import javax.servlet.http.HttpServletRequest;

public class SSEServlet extends EventSourceServlet
{
    @Override
    protected EventSource newEventSource(HttpServletRequest request)
    {
        return SSESourceMgr.getInstance().add();
    }
}

