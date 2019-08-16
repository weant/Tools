/**
 * FileName: DbManager
 * Author:   Administrator
 * Date:     2018/11/6 23:21
 * Description: DbManager
 */
package com.hcop.otn.common.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface DbManager {

    Connection getConnection() throws SQLException;

    void close(Connection con, Statement stmt, ResultSet rs) throws SQLException;

    void closeQuietly(Connection con, Statement stmt, ResultSet rs);

}
