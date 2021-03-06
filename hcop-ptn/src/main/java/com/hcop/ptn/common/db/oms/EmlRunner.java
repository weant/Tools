package com.hcop.ptn.common.db.oms;

import com.hcop.ptn.common.db.AbstractDbManager;
import com.hcop.ptn.common.db.dbrunner.DbRunner;

import java.sql.Connection;
import java.sql.SQLException;

public class EmlRunner implements RunnerApi {
    private String groupId;

    public EmlRunner(String groupId){
        this.groupId = groupId;
    }

    @Override
    public DbRunner getRunner() {
        return new DbRunner(new AbstractDbManager() {
            @Override
            public Connection getConnection() throws SQLException {
                return DBMgr.instance().getCurrentEmlConnection(EmlRunner.this.groupId);
            }
        });
    }
}
