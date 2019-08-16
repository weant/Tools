package com.hcop.ptn.common.properties;

public class AlarmProbableConf extends PropertiesConfig {
    private static AlarmProbableConf instance;

    private AlarmProbableConf(){
        super();
    }

    public static AlarmProbableConf instance(){
        if(instance == null){
            instance = new AlarmProbableConf();
        }
        return instance;
    }

    @Override
    protected String getConfFile() {
        // TODO Auto-generated method stub
        return Conf.instance().getProperty("alarm.probable.cause.zh.name.path");
    }
}