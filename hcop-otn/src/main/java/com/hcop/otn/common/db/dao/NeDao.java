package com.hcop.otn.common.db.dao;

import com.hcop.otn.common.beans.InternalNe;
import com.hcop.otn.common.db.dbrunner.DbRunner;
import com.hcop.otn.common.db.oms.SdhSysRunner;
import com.hcop.otn.common.utils.CollectionUtils;
import com.hcop.otn.restful.model.NE;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NeDao {
    private static Logger logger = Logger.getLogger(NeDao.class);
    private static NeDao ourInstance = new NeDao();

    public static NeDao getInstance() {
        return ourInstance;
    }

    private NeDao() {

    }

    public Collection<InternalNe> getAllInternalNes(){
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("SELECT WDMNE.HANDLE AS NEID, NPRNE.USERLABEL AS NENAME, ");
        baseSql.append(" NPRNE.MASTERSHELFTYPE AS NETYPE, NPRNE.EMLNEID AS EMLID, ");
        baseSql.append(" NPRNE.EMLDOMID AS GROUPID, NPRNE.IPADDRESS AS NEIP, ");
        baseSql.append(" NPRNE. VERSION AS NEVERSION, NPRNE.NEID AS NPRID, ");
        baseSql.append(" NPRNE.COMMUNICATIONSTATE AS COMMUNICATIONSTATE, ");
        baseSql.append(" NPRNE.SUPERVISIONSTATE AS SUPERVISIONSTATE ");
        baseSql.append(" FROM ");
        baseSql.append(" SNML.NE NPRNE, WDM.NODE WDMNE ");
        baseSql.append(" WHERE ");
        baseSql.append(" WDMNE.NPRCOMPONENTNEID = NPRNE.NEID");

        Collection<InternalNe> data = new ArrayList<>();

        DbRunner runner = new SdhSysRunner().getRunner();

        try {
            data = runner.queryBeanCollection(baseSql.toString(), InternalNe.class, null);
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }

    public Collection<InternalNe> getInternalNesById(String neId){
        if(neId.trim().length() < 1) {
            return Collections.emptyList();
        }
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("SELECT WDMNE.HANDLE AS NEID, NPRNE.USERLABEL AS NENAME, ");
        baseSql.append(" NPRNE.MASTERSHELFTYPE AS NETYPE, NPRNE.EMLNEID AS EMLID, ");
        baseSql.append(" NPRNE.EMLDOMID AS GROUPID, NPRNE.IPADDRESS AS NEIP, ");
        baseSql.append(" NPRNE. VERSION AS NEVERSION, NPRNE.NEID AS NPRID, ");
        baseSql.append(" NPRNE.COMMUNICATIONSTATE AS COMMUNICATIONSTATE, ");
        baseSql.append(" NPRNE.SUPERVISIONSTATE AS SUPERVISIONSTATE ");
        baseSql.append(" FROM ");
        baseSql.append(" SNML.NE NPRNE, WDM.NODE WDMNE ");
        baseSql.append(" WHERE ");
        baseSql.append(" WDMNE.NPRCOMPONENTNEID = NPRNE.NEID ");
        baseSql.append(" AND WDMNE.HANDLE = ");
        baseSql.append(neId);

        Collection<InternalNe> data = new ArrayList<>();

        DbRunner runner = new SdhSysRunner().getRunner();

        try {
            data = runner.queryBeanCollection(baseSql.toString(), InternalNe.class, null);
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }

    public Collection<NE> getAllNes(){
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("SELECT WDMNE.HANDLE AS NEID, NPRNE.USERLABEL AS NENAME, ");
        baseSql.append(" NVL(NPRNE.MASTERSHELFTYPE, NPRNE.EMLNETYPE) AS NETYPE, NPRNE.IPADDRESS AS NEIP, ");
        baseSql.append(" NPRNE. VERSION AS NEVERSION, ");
        baseSql.append(" CASE WHEN NPRNE.COMMUNICATIONSTATE = 0 THEN 'REACHABLE' ELSE 'UNREACHABLE' END AS COMMUNICATIONSTATE ");
        baseSql.append(" FROM ");
        baseSql.append(" SNML.NE NPRNE, WDM.NODE WDMNE ");
        baseSql.append(" WHERE ");
        baseSql.append(" WDMNE.NPRCOMPONENTNEID = NPRNE.NEID");

        Collection<NE> data = new ArrayList<>();

        DbRunner runner = new SdhSysRunner().getRunner();

        try {
            data = runner.queryBeanCollection(baseSql.toString(), NE.class, null);
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }

    public Collection<NE> getNeByIds(List<String> neIds){
        if(CollectionUtils.isEmpty(neIds)) {
            return Collections.emptyList();
        }
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("SELECT WDMNE.HANDLE AS NEID, NPRNE.USERLABEL AS NENAME, ");
        baseSql.append(" NPRNE.MASTERSHELFTYPE AS NETYPE, NPRNE.IPADDRESS AS NEIP, ");
        baseSql.append(" NPRNE. VERSION AS NEVERSION, ");
        baseSql.append(" CASE WHEN NPRNE.COMMUNICATIONSTATE = 0 THEN 'REACHABLE' ELSE 'UNREACHABLE' END AS COMMUNICATIONSTATE ");
        baseSql.append(" FROM ");
        baseSql.append(" SNML.NE NPRNE, WDM.NODE WDMNE ");
        baseSql.append(" WHERE ");
        baseSql.append(" WDMNE.NPRCOMPONENTNEID = NPRNE.NEID ");
        baseSql.append(generateSQLForIntCondition("WDMNE.HANDLE", neIds));

        Collection<NE> data = new ArrayList<>();

        DbRunner runner = new SdhSysRunner().getRunner();

        try {
            data = runner.queryBeanCollection(baseSql.toString(), NE.class, null);
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }

    public Collection<NE> getNeByNames(List<String> neNames){
        if(CollectionUtils.isEmpty(neNames)) {
            return Collections.emptyList();
        }
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("SELECT WDMNE.HANDLE AS NEID, NPRNE.USERLABEL AS NENAME, ");
        baseSql.append(" NPRNE.MASTERSHELFTYPE AS NETYPE, NPRNE.IPADDRESS AS NEIP, ");
        baseSql.append(" NPRNE. VERSION AS NEVERSION, ");
        baseSql.append(" CASE WHEN NPRNE.COMMUNICATIONSTATE = 0 THEN 'REACHABLE' ELSE 'UNREACHABLE' END AS COMMUNICATIONSTATE ");
        baseSql.append(" FROM ");
        baseSql.append(" SNML.NE NPRNE, WDM.NODE WDMNE ");
        baseSql.append(" WHERE ");
        baseSql.append(" WDMNE.NPRCOMPONENTNEID = NPRNE.NEID ");
        baseSql.append(generateSQLForStringCondition("NPRNE.USERLABEL", neNames));

        Collection<NE> data = new ArrayList<>();

        DbRunner runner = new SdhSysRunner().getRunner();

        try {
            data = runner.queryBeanCollection(baseSql.toString(), NE.class, null);
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }

    public Collection<NE> getNeByName(String neName){
        if(neName.trim().length() < 1) {
            return Collections.emptyList();
        }
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("SELECT WDMNE.HANDLE AS NEID, NPRNE.USERLABEL AS NENAME, ");
        baseSql.append(" NPRNE.MASTERSHELFTYPE AS NETYPE, NPRNE.IPADDRESS AS NEIP, ");
        baseSql.append(" NPRNE. VERSION AS NEVERSION, ");
        baseSql.append(" CASE WHEN NPRNE.COMMUNICATIONSTATE = 0 THEN 'REACHABLE' ELSE 'UNREACHABLE' END AS COMMUNICATIONSTATE ");
        baseSql.append(" FROM ");
        baseSql.append(" SNML.NE NPRNE, WDM.NODE WDMNE ");
        baseSql.append(" WHERE ");
        baseSql.append(" WDMNE.NPRCOMPONENTNEID = NPRNE.NEID ");
        baseSql.append(" AND LOWER(NPRNE.USERLABEL) LIKE '%"+ neName.toLowerCase() + "%'");

        Collection<NE> data = new ArrayList<>();

        DbRunner runner = new SdhSysRunner().getRunner();

        try {
            data = runner.queryBeanCollection(baseSql.toString(), NE.class, null);
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }

    public String generateSQLForIntCondition(String fieldName, List<String> parameters) {
        if(CollectionUtils.isEmpty(parameters)) {
            return "";
        }

        StringBuilder str = new StringBuilder(" AND " + fieldName + " IN (");

        parameters.forEach(p-> str.append(p+","));

        return str.substring(0, str.lastIndexOf(",")) + ")";
    }

    public String generateSQLForStringCondition(String fieldName, List<String> parameters) {
        if(CollectionUtils.isEmpty(parameters)) {
            return "";
        }

        StringBuilder str = new StringBuilder(" AND " + fieldName + " IN (");

        parameters.forEach(p-> str.append("'"+p+"',"));

        return str.substring(0, str.lastIndexOf(",")) + ")";
    }
}
