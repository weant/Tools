package com.hcop.otn.common.internal.xos.request;

import com.alu.tools.basic.conf.ReferenceFileConfig;

public class ConfigLoader extends ReferenceFileConfig {
	
	private static ConfigLoader instance = new ConfigLoader();
	
	private ConfigLoader() {
		
	}
	
	public static ConfigLoader getInstance() {
		return instance;
	}
	
	public void init(String configFile) {
		setPropertyFile( configFile );
        loadPropertyFile();
	}
}
