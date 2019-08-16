/**
 * FileName: DbUtil
 * Author:   Administrator
 * Date:     2018/11/6 23:17
 * Description: DbUtil
 */
package com.hcop.otn.common.db.tool;

import com.hcop.otn.common.db.DbManager;
import com.hcop.otn.common.db.ProcedureParameter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.*;

public class DbUtil {
    private static Log log = LogFactory.getLog(DbUtil.class);
    private DbManager dbm;

    public DbUtil(){
        dbm  = ToolDbMgr.instance();
    }

    public DbUtil(DbManager dbm) {
        this.dbm = dbm;
    }

    public Connection getConnection() throws SQLException {
        return dbm.getConnection();
    }

    public void close(Connection con, Statement stmt, ResultSet rs)
            throws SQLException {
        dbm.close(con, stmt, rs);
    }

    public void closeQuietly(Connection con, Statement stmt, ResultSet rs) {

        dbm.closeQuietly(con, stmt, rs);
    }

    /**
     * 测试数据库连接性。
     * 返回true,则数据库可连接
     * 返回false,则数据库 不可连接
     *
     * @return
     */
    public boolean isConnected(){
        Connection conn = null;
        try {
            conn = getConnection();
            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            log.error("can't get connection",e);
//			e.printStackTrace();
        }finally{
            try {
                close(conn, null, null);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
//				e.printStackTrace();
                log.error("error occured when closing the connection", e);
            }
        }

        return false;
    }
    /**
     *
     * call procedure of DB.
     * the result will put into a HashMap.
     * the key is a reference of ProcedureParameter key field.
     *
     *
     *
     * @param params
     * @param sql
     * @return
     */
    public HashMap<Object,Object> callProcedure(List<ProcedureParameter> params, String sql){
        log.info("SQL: "+sql);
//		synchronized(lock){
        if((params == null) || (params.size() == 0)){
            throw new IllegalArgumentException("params can't be null");
        }
        Collections.sort(params);
        HashMap<Object,Object> result = new HashMap<Object,Object>();
        Connection con = null;
        CallableStatement cs = null;
        try{
            con = dbm.getConnection();
            cs = con.prepareCall(sql);

            for(ProcedureParameter param: params){
                if(param.isInput()){//input
                    cs.setObject(param.getIndex(), param.getValue());
                }else{//output
                    cs.registerOutParameter(param.getIndex(), param.getSqlType());
                }

            }

            cs.execute();

            for(ProcedureParameter param: params){
                if(param.isInput())continue;
                result.put(param.getKey(), cs.getObject(param.getIndex()));
            }

//			cs.close();
        }catch(Exception e){
            log.error(e.getMessage(), e);
        }finally{
            closeQuietly(con, cs, null);
        }

        return result;
//		}
    }

    /**
     *
     * call procedure of DB, and
     * no result being returned
     *
     * @param sql
     */
    public void callProcedure(String sql){
        log.info("SQL: "+sql);
        Connection con = null;
        CallableStatement cs = null;
        try{
            con = dbm.getConnection();
            cs = con.prepareCall(sql);
            cs.execute();
        }catch(Exception e){
            log.error(e.getMessage(), e);
        }finally{
            closeQuietly(con, cs, null);
        }
    }
    /**
     *
     * 执行sql语句，params 是传入的预处理语句中变量对应的值的对象数组
     *
     * @param sql
     * @param params
     */
    public void doSql(String sql, Object[] params) {
        log.info("SQL: "+sql);
//		synchronized(lock){

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = dbm.getConnection();
            if(con==null){
                return ;
            }
            ps = con.prepareStatement(sql);

            if (params != null) {
                for (int i = 0, length = params.length; i < length; i++) {
                    Object paramI = params[i];
                    if(paramI instanceof byte[]){
                        ps.setBytes(i + 1,  (byte[])paramI);
                    }else{
                        ps.setObject(i + 1, params[i]);
                    }

                }
            }

            ps.execute();

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }catch(Exception e){
            log.error("exception was thrown while doing sql ["+sql+"]", e);
        } finally {
            closeQuietly(con, ps, null);
        }
//		}
    }

    public long executeUpdate(String sql, Object[] params, String key) {
        log.info("SQL: "+sql);
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = dbm.getConnection();
            if(con==null){
                return -1;
            }
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            if (params != null) {
                for (int i = 0, length = params.length; i < length; i++) {
                    Object paramI = params[i];
                    if(paramI instanceof byte[]){
                        ps.setBytes(i + 1,  (byte[])paramI);
                    }else{
                        ps.setObject(i + 1, params[i]);
                    }

                }
            }

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                long id = rs.getLong(key);
                return id;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }catch(Exception e){
            log.error("exception was thrown while doing sql ["+sql+"]", e);
        } finally {
            closeQuietly(con, ps, null);
        }
        return -1;
    }

    /**
     * 执行批量sql，无返回结果
     *
     * @param sql 多条SQL组成的字符串，用分号隔开
     */
    public void doBatchSql(String sql) {
        log.info("SQL: "+sql);
//		synchronized(lock){
        Connection con = null;
        PreparedStatement ps = null;

        try {

            con = dbm.getConnection();
            boolean auto = con.getAutoCommit();
            con.setAutoCommit(false);
            ps = con.prepareStatement(sql);
            String[] command = sql.split(";");
            for(String s : command) {
                ps.addBatch(s);
            }

            ps.executeBatch();
            con.commit();
            con.setAutoCommit(auto);

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        } catch(Exception e){
            log.error("doBatchSql error,sql ["+sql+"]",e);
        }finally {
            closeQuietly(con, ps, null);
        }
//		}
    }

    /**
     *
     * 执行批量sql，如果一个出错，这批sql的执行将回滚。
     * 这些sql都应该是insert 或 update 或者是delete的操作。
     * @param batchSqls
     */
    public void doBatchSql(String[] batchSqls){
        if(batchSqls == null || batchSqls.length == 0){
            log.error("batch sql is null ");
            return;
        }
//		synchronized(lock){
        Connection con = null;
        Statement stmt = null;

        try {
            StringBuilder sqlBuilder = new StringBuilder();
            con = dbm.getConnection();
            boolean auto = con.getAutoCommit();
            con.setAutoCommit(false);
            stmt = con.createStatement();
            for(String sql:batchSqls){
                stmt.addBatch(sql);
                sqlBuilder.append(sql);
                sqlBuilder.append(System.getProperty("line.separator"));
            }

            log.info("doBatchSql:"+sqlBuilder.toString());
            stmt.executeBatch();
            con.commit();
            con.setAutoCommit(auto);

        } catch (SQLException e) {
            if(con!= null){
                try {
                    con.rollback();
                    log.info("batch sql excute failed, connection is rollback");
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            log.error(e.getMessage(), e);
        }catch(Exception e){
            log.error("do batch sql error", e);
        } finally {
            closeQuietly(con, stmt, null);
        }
//		}
    }
    /**
     * 执行批量操作，要求sql是相同的，只不过参数是不同的 无返回结果。
     * 适用的场景如向某张表中批量插入记录
     *
     * @param sql
     * @param paramList
     */
    public void doBatchSql(String sql, List<Object[]> paramList) {
        log.info("SQL: "+sql);
//		synchronized(lock){
        Connection con = null;
        PreparedStatement ps = null;

        try {

            con = dbm.getConnection();
            boolean auto = con.getAutoCommit();
            con.setAutoCommit(false);

            ps = con.prepareStatement(sql);

            if (paramList != null) {
                for (Object[] params : paramList) {

                    if (params == null) {
                        throw new IllegalArgumentException(
                                "param can't be null");
                    }
                    for (int i = 0, length = params.length; i < length; i++) {
                        Object paramI = params[i];
                        if(paramI instanceof byte[]){
                            ps.setBytes(i + 1,  (byte[])paramI);
                        }else{
                            ps.setObject(i + 1, params[i]);
                        }
                    }

                    ps.addBatch();
                }
            }

            ps.executeBatch();
            con.commit();
            con.setAutoCommit(auto);

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            SQLException interalEx = e.getNextException();
            if(interalEx != null){
                log.error(interalEx.getMessage(), interalEx);
            }

        }catch(Exception e){
            log.error("do batch sql error", e);
        } finally {
            closeQuietly(con, ps, null);
        }
//		}
    }

    /**
     * 执行查询，结果是一个集合，集合中每个元素是HashMap类型的，key为查询sql中的column label， value为所查询字段对应的值
     *
     * @param sql
     * @param params
     * @return
     */
    public List<HashMap<Object, Object>> query(String sql, Object[] params) {
        //log.info("SQL: "+sql);
//		synchronized(lock){
        List<HashMap<Object, Object>> result = new ArrayList<HashMap<Object, Object>>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = dbm.getConnection();

            ps = con.prepareStatement(sql);
            if (params != null) {
                for (int i = 0, length = params.length; i < length; i++) {
                    Object paramI = params[i];
                    if(paramI instanceof byte[]){
                        ps.setBytes(i + 1,  (byte[])paramI);
                    }else{
                        ps.setObject(i + 1, params[i]);
                    }
                }
            }

            rs = ps.executeQuery();
            if(rs == null) {
                return null;
            }
            ResultSetMetaData rsmd = rs.getMetaData();


            int columns = rsmd.getColumnCount();
            while (rs.next()) {

                HashMap<Object, Object> element = new CaseInsensitiveHashMap();
                for (int i = 0; i < columns; i++) {
                    String label = rsmd.getColumnLabel(i + 1);
//					String classname = rsmd.getColumnClassName(i+1);
//					int dbType = rsmd.getColumnType(i+1);
//					String typeName = rsmd.getColumnTypeName(i+1);
                    Object obj = rs.getObject(label);
                    element.put(label, obj);
                }
                result.add(element);
            }

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }catch(Exception e){
            log.error("exception was thrown while doing query",e);
        } finally {
            closeQuietly(con, ps, rs);
        }
        return result;
//	}
    }


    public static void main(String[] args){

    }



    /**
     * A Map that converts all keys to lowercase Strings for case insensitive
     * lookups.  This is needed for the toMap() implementation because
     * databases don't consistenly handle the casing of column names.
     *
     * <p>The keys are stored as they are given [BUG #DBUTILS-34], so we maintain
     * an internal mapping from lowercase keys to the real keys in order to
     * achieve the case insensitive lookup.
     *
     * <p>Note: This implementation does not allow <tt>null</tt>
     * for key, whereas {@link HashMap} does, because of the code:
     * <pre>
     * key.toString().toLowerCase()
     * </pre>
     */
    private static class CaseInsensitiveHashMap extends HashMap<Object, Object> {
        /**
         * The internal mapping from lowercase keys to the real keys.
         *
         * <p>
         * Any query operation using the key
         * ({@link #get(Object)}, {@link #containsKey(Object)})
         * is done in three steps:
         * <ul>
         * <li>convert the parameter key to lower case</li>
         * <li>get the actual key that corresponds to the lower case key</li>
         * <li>query the map with the actual key</li>
         * </ul>
         * </p>
         */
        private final Map<String,String> lowerCaseMap = new HashMap<String,String>();

        /**
         * Required for serialization support.
         *
         * @see java.io.Serializable
         */
        private static final long serialVersionUID = -2848100435296897392L;

        /** {@inheritDoc} */
        @Override
        public boolean containsKey(Object key) {
            Object realKey = lowerCaseMap.get(key.toString().toLowerCase());
            return super.containsKey(realKey);
            // Possible optimisation here:
            // Since the lowerCaseMap contains a mapping for all the keys,
            // we could just do this:
            // return lowerCaseMap.containsKey(key.toString().toLowerCase());
        }

        /** {@inheritDoc} */
        @Override
        public Object get(Object key) {
            Object realKey = lowerCaseMap.get(key.toString().toLowerCase());
            return super.get(realKey);
        }

        /** {@inheritDoc} */
        @Override
        public Object put(Object key, Object value) {
            /*
             * In order to keep the map and lowerCaseMap synchronized,
             * we have to remove the old mapping before putting the
             * new one. Indeed, oldKey and key are not necessaliry equals.
             * (That's why we call super.remove(oldKey) and not just
             * super.put(key, value))
             */
            Object oldKey = lowerCaseMap.put(key.toString().toLowerCase(), key.toString());
            Object oldValue = super.remove(oldKey);
            super.put(key, value);
            return oldValue;
        }

        /** {@inheritDoc} */
        @Override
        public void putAll(Map<?,?> m) {
            for (Entry<? , ?> entry : m.entrySet()) {
                String key = entry.getKey().toString();
                Object value = entry.getValue();
                this.put(key, value);
            }
        }

        /** {@inheritDoc} */
        @Override
        public Object remove(Object key) {
            Object realKey = lowerCaseMap.remove(key.toString().toLowerCase());
            return super.remove(realKey);
        }
    }

}
