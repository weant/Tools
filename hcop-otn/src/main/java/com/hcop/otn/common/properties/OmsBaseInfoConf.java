package com.hcop.otn.common.properties;

public class OmsBaseInfoConf extends PropertiesConfig {
    private static OmsBaseInfoConf instance;

    private OmsBaseInfoConf(){
        super();
    }

    public static OmsBaseInfoConf instance(){
        if(instance == null){
            instance = new OmsBaseInfoConf();
        }
        return instance;
    }

    @Override
    protected String getConfFile() {
        // TODO Auto-generated method stub
        return Conf.instance().getProperty("oms.base.info.path");
        //return "/conf/omsConf.properties";
    }
}
