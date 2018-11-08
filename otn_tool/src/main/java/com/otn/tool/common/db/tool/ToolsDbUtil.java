/**
 * FileName: DbUtil
 * Author:   Administrator
 * Date:     2018/11/6 23:14
 * Description: DBUtil
 */
package com.otn.tool.common.db.tool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ToolsDbUtil {
    private static Log log = LogFactory.getLog(ToolsDbUtil.class);
    private static ToolsDbUtil util = new ToolsDbUtil();
    private DbUtil mgr = new DbUtil(ToolsDbMgr.instance());

    private ToolsDbUtil() {

    }

    public static ToolsDbUtil instance() {
        return util;
    }

    /**
     * 带参数（带?）的sql查询
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public List<HashMap<Object,Object>> query(String sql, Object[] params) {
        return mgr.query(sql, params);
    }

    /**
     * 不带参的sql查询
     * @param sql
     * @return
     * @throws SQLException
     */
    public List<HashMap<Object,Object>> query(String sql) {
        return mgr.query(sql, null);
    }

    /**
     * 批量执行查询，一个带参的sql多个不同值的参数传入
     * 例：
     * 	sql：select * from table where a=? and b=?
     * 	params	{
     * 				{"a1","b1"},
     * 				{"a2","b2"},
     * 				{"a3","b3"},
     * 				{"a4","b4"}
     * 			}
     * 以上输入将进行四次查询，并将四次查询的结果统一返回
     * @param sql
     * @param params 参数list的size与查询执行次数一致
     * @return
     * @throws SQLException
     */
    public List<HashMap<Object,Object>> queryBatch(String sql, List<Object[]> params)  {
        List<HashMap<Object,Object>> resultList = new ArrayList<HashMap<Object,Object>>();
        for (Object[] objects : params) {
            resultList.addAll(mgr.query(sql, objects));
        }
        return resultList;
    }

    /**
     * 带参数（带?）的sql执行
     * @param sql
     * @param params
     * @throws SQLException
     */
    public void execute(String sql, Object[] params) {
        mgr.doSql(sql, params);
    }

    /**
     * 不带参的sql执行
     * @param sql
     * @throws SQLException
     */
    public void execute(String sql) {
        mgr.doSql(sql, null);
    }

    /**
     * 批量执行新增、修改、删除，一个带参的sql多个不同值的参数传入
     * @param sql
     * @param params
     * @throws SQLException
     */
    public void executeBatch(String sql, List<Object[]> params) {
        mgr.doBatchSql(sql, params);
    }


    /**
     * 批量执行sql
     * @param sqls
     */
    public void executeBatch(String[] sqls){
        mgr.doBatchSql(sqls);
    }
}
