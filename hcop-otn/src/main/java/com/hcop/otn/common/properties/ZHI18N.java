/**
 * FileName: ZhI18N
 * Author:   Administrator
 * Date:     2018/11/19 22:00
 * Description: ZHI18N
 */
package com.hcop.otn.common.properties;

public class ZHI18N extends PropertiesConfig {
    private static ZHI18N instance;

    private ZHI18N(){
        super();
    }

    public static ZHI18N instance(){
        if(instance == null){
            instance = new ZHI18N();
        }
        return instance;
    }

    @Override
    protected String getConfFile() {
        // TODO Auto-generated method stub
        return Conf.instance().getProperty("zh.i18n.path");
        //return "/conf/i18n_zh.properties";
    }
}
