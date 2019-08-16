/**
 * FileName: RootCauseConf
 * Author:   Administrator
 * Date:     2019/3/26 21:25
 * Description: RootCause
 */
package com.hcop.ptn.common.properties;

public class RootCauseConf extends PropertiesConfig {
    private static RootCauseConf instance;

    private RootCauseConf(){
        super();
    }

    public static RootCauseConf instance(){
        if(instance == null){
            instance = new RootCauseConf();
        }
        return instance;
    }

    @Override
    protected String getConfFile() {
        // TODO Auto-generated method stub
        return Conf.instance().getProperty("alarm.root.cause");
    }
}
