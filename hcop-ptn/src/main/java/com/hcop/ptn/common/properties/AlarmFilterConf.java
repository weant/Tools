package com.hcop.ptn.common.properties;

public class AlarmFilterConf extends PropertiesConfig {
    private static AlarmFilterConf instance;

    private AlarmFilterConf(){
        super();
    }

    public static AlarmFilterConf instance(){
        if(instance == null){
            instance = new AlarmFilterConf();
        }
        return instance;
    }

    @Override
    protected String getConfFile() {
        // TODO Auto-generated method stub
        return Conf.instance().getProperty("alarm.filter");
    }
}