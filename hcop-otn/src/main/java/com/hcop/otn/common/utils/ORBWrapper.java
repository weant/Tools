package com.hcop.otn.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;

import java.util.Properties;

public class ORBWrapper {
	private static Log log = LogFactory.getLog(ORBWrapper.class);
	private static ORBWrapper _instance;
	private ORB orb_c;
	private org.omg.PortableServer.POA rootPOA_c;
	
	private ORBWrapper() {
	}
	
	public static ORBWrapper instance() {
		if (_instance == null) {
			_instance = new ORBWrapper();
		}
		
		return _instance;
	}
	
	public void init() throws InvalidName, AdapterInactive {
		Properties props = new Properties();
        props.put( "org.omg.CORBA.ORBClass", "org.jacorb.orb.ORB" );
        props.put( "org.omg.CORBA.ORBSingletonClass", "org.jacorb.orb.ORBSingleton" );
        //props.put( "org.omg.PortableInterceptor.ORBInitializerClass.myTest", "com.alu.opd.utp.util.Test" );
        props.put( "jacorb.connection.server.timeout", "3600000");//3600000 1 hour
        props.put( "jacorb.connection.client.idle_timeout", "60000");
        //props.put( "jacorb.retries","3");
        
        String oaPort = System.getProperty("OAPort");
        if (oaPort == null || oaPort.length() == 0) {
        	System.setProperty("OAPort", "13305");
        	oaPort = "13305";
        	log.info("Using OAPort : " + oaPort);
        }
        props.put( "OAPort", oaPort);
        
        
        String usedIP = System.getProperty("UsedIP");
        if (usedIP != null && usedIP.length() != 0) {
        	log.info("Using IP address : " + usedIP);
        	props.put( "OAIAddr", usedIP);
        }
        
		log.info("Initializing ORB...");
		
		orb_c = ORB.init((String[]) null, props);
		log.info("ORB initialized");
		
		log.info("Initializing Transient POA...");
		
		rootPOA_c = POAHelper.narrow(orb_c.resolve_initial_references("RootPOA"));
		rootPOA_c.the_POAManager().activate();
		log.info("POA activated");
	}
	
	public ORB getORB() {
		return orb_c;
	}
	
	public org.omg.PortableServer.POA getRootPOA() {
		return rootPOA_c;
	}
}
