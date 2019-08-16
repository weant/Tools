package com.hcop.ptn.common.db.oms;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionPool {

	Connection getConnection() throws SQLException;

}