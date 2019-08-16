/**
 * FileName: AlarmSolutionConf
 * Author:   Administrator
 * Date:     2019/3/26 21:24
 * Description: AlarmSolutionConf
 */
package com.hcop.ptn.common.properties;

public class AlarmSolutionConf extends PropertiesConfig {
    private static AlarmSolutionConf instance;

    private AlarmSolutionConf(){
        super();
    }

    public static AlarmSolutionConf instance(){
        if(instance == null){
            instance = new AlarmSolutionConf();
        }
        return instance;
    }

    @Override
    protected String getConfFile() {
        // TODO Auto-generated method stub
        return Conf.instance().getProperty("alarm.solution");
    }
}
