package com.hcop.ptn.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class SSESourceMgr {
    private final static Logger log = LogManager.getLogger(SSESourceMgr.class);
    static private Map<Integer, SSEServletEventSource> map=new HashMap<Integer, SSEServletEventSource>();
    static private SSESourceMgr instance=new SSESourceMgr();
    static private int count=0;
    private SSESourceMgr(){};
    static public SSESourceMgr getInstance()
    {
        return instance;
    }
    public void remove(int id)
    {
        map.remove(id);
        log.debug("removed");
    }
    public SSEServletEventSource add()
    {
        count++;
        SSEServletEventSource s=new SSEServletEventSource(count);
        add(count, s);
        log.debug("added, count="+count);
        return s;
    }
    private void add(int id, SSEServletEventSource source)
    {
        map.put(id, source);
    }
    public void send(String msg)
    {
        if(map.isEmpty()){
            log.debug("no clients...");
        }else{
            for (SSEServletEventSource s : map.values()) {
                s.emitEvent(msg);
            }
        }

    }

}
