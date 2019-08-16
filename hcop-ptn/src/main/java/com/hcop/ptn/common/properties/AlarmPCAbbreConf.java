/**
 * FileName: AlarmPCAbbreConf
 * Author:   Administrator
 * Date:     2019/3/26 21:25
 * Description: ProbableCauseAbbre
 */
package com.hcop.ptn.common.properties;

public class AlarmPCAbbreConf extends PropertiesConfig {
    private static AlarmPCAbbreConf instance;

    private AlarmPCAbbreConf(){
        super();
    }

    public static AlarmPCAbbreConf instance(){
        if(instance == null){
            instance = new AlarmPCAbbreConf();
        }
        return instance;
    }

    @Override
    protected String getConfFile() {
        // TODO Auto-generated method stub
        return Conf.instance().getProperty("alarm.probable.cause.abbre");
    }
}
