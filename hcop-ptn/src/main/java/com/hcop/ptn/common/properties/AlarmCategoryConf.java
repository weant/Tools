/**
 * FileName: AlarmCategoryConf
 * Author:   Administrator
 * Date:     2019/3/26 21:24
 * Description: AlarmCategory
 */
package com.hcop.ptn.common.properties;

public class AlarmCategoryConf extends PropertiesConfig {
    private static AlarmCategoryConf instance;

    private AlarmCategoryConf(){
        super();
    }

    public static AlarmCategoryConf instance(){
        if(instance == null){
            instance = new AlarmCategoryConf();
        }
        return instance;
    }

    @Override
    protected String getConfFile() {
        // TODO Auto-generated method stub
        return Conf.instance().getProperty("alarm.category");
    }
}
