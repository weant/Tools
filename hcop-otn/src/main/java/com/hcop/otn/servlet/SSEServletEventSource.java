package com.hcop.otn.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

class SSEServletEventSource implements org.eclipse.jetty.servlets.EventSource
{
    private final static Logger log = LogManager.getLogger(SSEServletEventSource.class);
    private Emitter emitter;
    private int id=-1;

    public SSEServletEventSource(int id)
    {
        this.id=id;
    }
    public void onOpen(Emitter emitter) throws IOException
    {
        this.emitter = emitter;
        log.debug("onOpen");
    }

    public void emitEvent(String dataToSend)
    {
        /*try {
        NE ne=new NE();
        ne.setNeId("001");
        ne.setNeName("PSS32-1");
        ne.setNeIp("135.251.99.15");
        ne.setNeType("PSS-32");
        ne.setCommunicationState(NECommunicationState.REACHABLE);
            dataToSend=(new ObjectMapper()).writeValueAsString(ne);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }*/
        log.debug("emitEvent:"+dataToSend);
        try {

            this.emitter.data(dataToSend+"\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClose()
    {
        log.debug("onClose");
        SSESourceMgr.getInstance().remove(this.id);
    }
}
