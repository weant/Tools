package com.hcop.otn.common.db.dao;

import com.hcop.otn.common.beans.InternalTp;
import com.hcop.otn.common.db.dbrunner.DbRunner;
import com.hcop.otn.common.db.oms.WdmRunner;
import com.hcop.otn.common.utils.CollectionUtils;
import com.hcop.otn.restful.model.TP;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TpDao {
    private static Logger logger = Logger.getLogger(TpDao.class);
    private static TpDao ourInstance = new TpDao();

    public static TpDao getInstance() {
        return ourInstance;
    }

    private TpDao() {
    }

    public Collection<InternalTp> getInternalTpsByTpId(Collection<String> tpIds){
        if(CollectionUtils.isEmpty(tpIds)) {
            return Collections.emptyList();
        }

        StringBuilder baseSql = new StringBuilder();
        /*baseSql.append("SELECT TP.NEID, TP.TPID, TP.PTPID, TP.TPNATIVENAME AS TPNAME, ");
        baseSql.append(" EQUIPMENT.NATIVENAME AS EQUIPNAME, TP.FREQUENCY, TP.EFFECTIVERATE, ");
        baseSql.append(" TPPARAMETERS.NETWORKPARAMETERVALUE AS IFINDEX ");
        baseSql.append(" FROM TP, TPPARAMETERS, TPPARAMNAMEMAP, EQUIPMENT ");
        baseSql.append(" WHERE TP.TPID = TPPARAMETERS.TPID AND TP.EQUIPID = EQUIPMENT.EQUIPID ");
        baseSql.append(" AND TPPARAMETERS.PARAMETERNAME = TPPARAMNAMEMAP.PARAMENUMNAME ");
        baseSql.append(" AND TPPARAMNAMEMAP.PARAMSTRINGNAME = 'INDEX' ");*/


        baseSql.append(" SELECT TP.NEID,NODE.NAME AS NENAME,TP.TPID,TP.PTPID,TP.TPNATIVENAME AS TPNAME, ");
        baseSql.append(" EQUIPMENT.NATIVENAME AS EQUIPNAME,TP.FREQUENCY,TP.EFFECTIVERATE ");
        baseSql.append(" FROM TP, EQUIPMENT, NODE ");
        baseSql.append(" WHERE TP.EQUIPID = EQUIPMENT.EQUIPID (+) ");
        baseSql.append(" AND TP.NEID = NODE.HANDLE(+) ");
        baseSql.append(generateSQLForIntCondition("TP.TPID", tpIds));

        Collection<InternalTp> data = new ArrayList<>();

        DbRunner runner = new WdmRunner().getRunner();
        //logger.info("sql: " + baseSql.toString());
        try {
            data = runner.queryBeanCollection(baseSql.toString(), InternalTp.class, null);
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }

    public Collection<TP> getTpsByNeIds(List<String> neIds) {
        if(CollectionUtils.isEmpty(neIds)) {
            return Collections.emptyList();
        }

        StringBuilder baseSql = new StringBuilder();
        baseSql.append("SELECT TP.NEID, NODE.NAME AS NENAME, TP.TPID, TP.TPNATIVENAME AS TPNAME, EQUIPMENT.NATIVENAME AS EQUIPNAME ");
        baseSql.append(" FROM TP, EQUIPMENT, NODE ");
        baseSql.append(" WHERE TP.EQUIPID = EQUIPMENT.EQUIPID ");
        baseSql.append(" AND TP.NEID = NODE.HANDLE ");
        baseSql.append(" AND TP.TPTYPE = 0 AND TP.PTPID IS NULL ");
        baseSql.append(generateSQLForIntCondition("TP.NEID", neIds));

        Collection<TP> data = new ArrayList<>();

        DbRunner runner = new WdmRunner().getRunner();

        try {
            data = runner.queryBeanCollection(baseSql.toString(), TP.class, null);
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }

    public Collection<TP> getTpByNeIdAndTpId(String neId, String tpId) {
        if(neId.trim().length() < 1 || tpId.trim().length() < 1) {
            return Collections.emptyList();
        }

        StringBuilder baseSql = new StringBuilder();
        baseSql.append("SELECT TP.NEID, NODE.NAME AS NENAME, TP.TPID, TP.TPNATIVENAME AS TPNAME, EQUIPMENT.NATIVENAME AS EQUIPNAME ");
        baseSql.append(" FROM TP, EQUIPMENT, NODE ");
        baseSql.append(" WHERE TP.EQUIPID = EQUIPMENT.EQUIPID ");
        baseSql.append(" AND TP.NEID = NODE.HANDLE ");
        baseSql.append(" AND TP.TPTYPE = 0 AND TP.PTPID IS NULL ");
        baseSql.append(" AND TP.NEID = " + neId);
        baseSql.append(" AND TP.TPID = " + tpId);

        Collection<TP> data = new ArrayList<>();

        DbRunner runner = new WdmRunner().getRunner();

        try {
            data = runner.queryBeanCollection(baseSql.toString(), TP.class, null);
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }


    public String generateSQLForIntCondition(String fieldName, Collection<String> parameters) {
        if(CollectionUtils.isEmpty(parameters)) {
            return "";
        }

        StringBuilder str = new StringBuilder(" AND " + fieldName + " IN (");

        parameters.forEach(p-> str.append(p+","));

        return str.substring(0, str.lastIndexOf(",")) + ")";
    }

    public String generateSQLForStringCondition(String fieldName, Collection<String> parameters) {
        if(CollectionUtils.isEmpty(parameters)) {
            return "";
        }

        StringBuilder str = new StringBuilder(" AND " + fieldName + " IN (");

        parameters.forEach(p-> str.append("'"+p+"',"));

        return str.substring(0, str.lastIndexOf(",")) + ")";
    }
}
