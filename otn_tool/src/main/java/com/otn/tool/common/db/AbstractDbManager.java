/**
 * FileName: AbstractDbManager
 * Author:   Administrator
 * Date:     2018/11/6 23:20
 * Description: AbstractDbManager
 */
package com.otn.tool.common.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractDbManager implements DbManager {

    public void close(Connection con, Statement stmt, ResultSet rs) throws SQLException{
        if(rs != null){
            rs.close();
        }
        if(stmt != null){
            stmt.close();
        }
        if(con != null){
            con.close();
        }
    }

    public void closeQuietly(Connection con, Statement stmt, ResultSet rs){

        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                // ignored
            }
        }

        if(stmt != null){
            try {
                stmt.close();
            } catch (SQLException e) {
                //  ignored
            }
        }

        if(con != null){
            try {
                con.close();
            } catch (SQLException e) {
                //  ignored
            }
        }
    }

}