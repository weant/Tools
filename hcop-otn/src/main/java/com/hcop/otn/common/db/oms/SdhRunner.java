package com.hcop.otn.common.db.oms;

import com.hcop.otn.common.db.AbstractDbManager;
import com.hcop.otn.common.db.dbrunner.DbRunner;

import java.sql.Connection;
import java.sql.SQLException;

public class SdhRunner implements RunnerApi {
    @Override
    public DbRunner getRunner() {
        return new DbRunner(new AbstractDbManager() {
            @Override
            public Connection getConnection() throws SQLException {
                return DBMgr.instance().getCurrentSDHNPRDBConnection();
            }
        });
    }
}
