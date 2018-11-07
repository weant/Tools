/**
 * FileName: OmsConf
 * Author:   Administrator
 * Date:     2018/11/7 21:59
 * Description: OmsConf
 */
package com.otn.tool.common.properties;

public class OmsConf extends PropertiesConfig {
    private static OmsConf instance;

    private OmsConf(){
        super();
    }

    public static OmsConf instance(){
        if(instance == null){
            instance = new OmsConf();
        }
        return instance;
    }

    @Override
    protected String getConfFile() {
        // TODO Auto-generated method stub
        return "/conf/omsConf.properties";
    }

}
