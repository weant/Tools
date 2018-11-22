/**
 * FileName: ToolDbMgr
 * Author:   Administrator
 * Date:     2018/11/6 23:19
 * Description: ToolDbMgr
 */
package com.otn.tool.common.db.tool;

import com.otn.tool.common.db.AbstractDbManager;
import com.otn.tool.common.properties.Conf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class ToolDbMgr extends AbstractDbManager {
    private static Log log = LogFactory.getLog(ToolDbMgr.class);
    private static ToolDbMgr mgr = new ToolDbMgr();
    private String dbPath;

    public ToolDbMgr(){
        Map<String,String> map = Conf.instance().getPropertiesMap();
        this.dbPath = map.get("db.path");
    }

    public static ToolDbMgr instance(){
        if(mgr == null)
        {
            mgr = new ToolDbMgr();
        }
        return mgr;
    }

    @Override
    public Connection getConnection() throws SQLException {
        String driverName = "org.sqlite.JDBC";
        try {
            Class.forName(driverName);
            File file = new File(".");
            String path = file.getAbsolutePath() + dbPath;
            return DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("DB getConnection exception", e);
            return null;
        }
    }


}
