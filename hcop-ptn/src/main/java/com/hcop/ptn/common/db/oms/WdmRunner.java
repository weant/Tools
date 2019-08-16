package com.hcop.ptn.common.db.oms;

import com.hcop.ptn.common.db.AbstractDbManager;
import com.hcop.ptn.common.db.dbrunner.DbRunner;

import java.sql.Connection;
import java.sql.SQLException;

public class WdmRunner implements RunnerApi {
    @Override
    public DbRunner getRunner() {
        return new DbRunner(new AbstractDbManager() {
            @Override
            public Connection getConnection() throws SQLException {
                return DBMgr.instance().getCurrentWDMDBConnection();
            }
        });
    }
}