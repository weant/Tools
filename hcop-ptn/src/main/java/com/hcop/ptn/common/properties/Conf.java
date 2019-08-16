package com.hcop.ptn.common.properties;

public class Conf extends PropertiesConfig {
    private static Conf instance;

    private Conf(){
        super();
    }

    public static Conf instance(){
        if(instance == null){
            instance = new Conf();
        }
        return instance;
    }

    @Override
    protected String getConfFile() {
        // TODO Auto-generated method stub
        return "conf.properties";
    }
}
