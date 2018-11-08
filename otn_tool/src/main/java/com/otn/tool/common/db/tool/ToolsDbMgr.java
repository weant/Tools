/**
 * FileName: ToolsDbMgr
 * Author:   Administrator
 * Date:     2018/11/6 23:19
 * Description: ToolsDbMgr
 */
package com.otn.tool.common.db.tool;

import com.otn.tool.common.db.AbstractDbManager;
import com.otn.tool.common.properties.Conf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class ToolsDbMgr extends AbstractDbManager {
    private static Log log = LogFactory.getLog(ToolsDbMgr.class);
    private static ToolsDbMgr mgr = new ToolsDbMgr();
    private String url;
    private String userName;
    private String psw;

    public ToolsDbMgr(){
        Map<String,String> map = Conf.instance().getPropertiesMap();
        this.userName = map.get("db.userName");
        this.psw = map.get("db.password");
        this.url = "jdbc:postgresql://" + map.get("db.host") + ":" + map.get("db.port") + "/" + map.get("db.dbaName")+"?characterEncoding=utf8";
    }

    public static ToolsDbMgr instance(){
        if(mgr == null)
        {
            mgr = new ToolsDbMgr();
        }
        return mgr;
    }
    @Override
    public Connection getConnection() throws SQLException {
        String driverName = "org.postgresql.Driver";
        try {
            Class.forName(driverName);
            return DriverManager.getConnection(url, userName, psw);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("DB getConnection exception", e);
            return null;
        }
    }


}
