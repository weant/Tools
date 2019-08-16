/**
 * FileName: AlarmRCPeerEndConf
 * Author:   Administrator
 * Date:     2019/3/30 16:20
 * Description:
 */
package com.hcop.ptn.common.properties;

public class AlarmRCPeerEndConf extends PropertiesConfig {
    private static AlarmRCPeerEndConf instance;

    private AlarmRCPeerEndConf(){
        super();
    }

    public static AlarmRCPeerEndConf instance(){
        if(instance == null){
            instance = new AlarmRCPeerEndConf();
        }
        return instance;
    }

    @Override
    protected String getConfFile() {
        // TODO Auto-generated method stub
        return Conf.instance().getProperty("alarm.root.cause.peer.end");
    }
}
