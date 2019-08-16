package com.hcop.otn.common.db.dao;

import com.hcop.otn.common.db.dbrunner.DbRunner;
import com.hcop.otn.common.db.oms.EmlRunner;
import com.hcop.otn.restful.model.Equipment;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class EquipmentDao {
    private static Logger logger = Logger.getLogger(EquipmentDao.class);
    private static EquipmentDao ourInstance = new EquipmentDao();

    public static EquipmentDao getInstance() {
        return ourInstance;
    }

    private EquipmentDao() {
    }

    public Collection<Equipment> getEquipmentsByNeId(int emlNeId, int groupId) {
        if(emlNeId < 1 || groupId < 1) {
            return Collections.emptyList();
        }

        StringBuilder baseSql = new StringBuilder();
        baseSql.append("SELECT EQP.NEID, EQPA.CPKTYPE AS EQUIPNAME,SUBSTR(EQP.EQUIPAID,INSTR(EQP.EQUIPAID, '-')+1) AS SLOT, ATT.SN, ");
        baseSql.append(" CASE WHEN ATT.MDATE IS NULL THEN '' ELSE '20'||ATT.MDATE END AS MDATE ");
        baseSql.append(" FROM EQUIPMENT EQP, EQUIPMENT EQPA, ");
        baseSql.append(" (SELECT EQUIPID, ");
        baseSql.append(" listagg(tnCardSerialNumber,'') WITHIN GROUP( ORDER BY EQUIPID) AS SN, ");
        baseSql.append(" listagg(tnCardDate,'') WITHIN GROUP( ORDER BY EQUIPID) AS MDATE ");
        baseSql.append(" FROM (SELECT EQUIPID,  ");
        baseSql.append(" DECODE(ATTRIBUTENAME,'tnCardSerialNumber',Trim(ATTRIBUTEVALUE),'') AS tnCardSerialNumber, ");
        baseSql.append(" DECODE(ATTRIBUTENAME,'tnCardDate',Trim(ATTRIBUTEVALUE),'') AS tnCardDate ");
        baseSql.append(" FROM EQUIPATTRIBUTE ");
        baseSql.append(" WHERE ");
        baseSql.append(" ATTRIBUTENAME IN ('tnCardSerialNumber','tnCardDate')) GROUP BY EQUIPID) ATT ");
        baseSql.append(" WHERE EQP.PARENTID = EQPA.EQUIPID(+) ");
        baseSql.append(" AND EQP.EQUIPID = ATT.EQUIPID(+) ");
        baseSql.append(" AND EQP.EQUIPAID NOT LIKE 'YCABLE%' ");
        baseSql.append(" AND EQP.EQUIPTYPE = 4 ");
        baseSql.append(" AND EQP.NEID = ");
        baseSql.append(emlNeId);

        Collection<Equipment> data = new ArrayList<>();

        DbRunner runner = new EmlRunner(String.valueOf(groupId)).getRunner();

        try {
            data = runner.queryBeanCollection(baseSql.toString(), Equipment.class, null);
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }
}
