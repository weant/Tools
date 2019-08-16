package com.hcop.ptn.common.db.dao;

import com.hcop.ptn.common.beans.InternalTp;
import com.hcop.ptn.common.db.dbrunner.DbRunner;
import com.hcop.ptn.common.db.oms.PktRunner;
import com.hcop.ptn.common.db.oms.WdmRunner;
import com.hcop.ptn.common.utils.CollectionUtils;
import com.hcop.ptn.restful.model.TP;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

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
        baseSql.append("SELECT TP.NEID, TP.TPID, TP.PTPID, TP.TPNATIVENAME AS TPNAME, ");
        baseSql.append(" EQUIPMENT.NATIVENAME AS EQUIPNAME, TP.FREQUENCY, TP.EFFECTIVERATE, ");
        baseSql.append(" TPPARAMETERS.NETWORKPARAMETERVALUE AS IFINDEX ");
        baseSql.append(" FROM TP, TPPARAMETERS, TPPARAMNAMEMAP, EQUIPMENT ");
        baseSql.append(" WHERE TP.TPID = TPPARAMETERS.TPID AND TP.EQUIPID = EQUIPMENT.EQUIPID ");
        baseSql.append(" AND TPPARAMETERS.PARAMETERNAME = TPPARAMNAMEMAP.PARAMENUMNAME ");
        baseSql.append(" AND TPPARAMNAMEMAP.PARAMSTRINGNAME = 'INDEX' ");
        baseSql.append(generateSQLForIntCondition("TP.TPID", tpIds));

        Collection<InternalTp> data = new ArrayList<>();

        DbRunner runner = new WdmRunner().getRunner();

        try {
            data = runner.queryBeanCollection(baseSql.toString(), InternalTp.class, null);
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

    public List<TP> getTps(String neId, String tpId){
        List<TP> list = new ArrayList<>();
        String sql1 = " SELECT "
                +" 	port.PHYSICALPORTID portID, "
                +" 	port.NODEID nodeID, "
                +" 	port.userlabel portlabel, "
                +" 	ne.userlabel neName, "
                +" 	port.INTERFACETYPE portType, "
                +" 	port.USEDBANDWIDTH bandWidth, "
                +" 	port.EGRESSSHAPINGRATE rate, "
                +" 	NVL ( "
                +" 		ETHPRT.USAGESTATE + 1, "
                +" 		port.USAGESTATE "
                +" 	) AS portState, "
                +" c_domain.USERLABEL AS domain "
                +" FROM  "
                +" 	c_ne ne, "
                +" 	c_ethport port, "
                +" 	ETHPRT, "
                +" 	C_DOMAIN_RESOURCES, "
                +" 	c_domain "
                +" WHERE "
                +" port.NODEID = ne.neid "
                +" AND ETHPRT.COREID (+) = PORT.physicalportid "
                +" AND port.AGGREGATIONID = - 1 "
                +" AND ETHPRT.NEID (+) = PORT.NODEID "
                +" AND port.NODEID = " + neId
                + (tpId.length() > 0 ? " AND port.PHYSICALPORTID = " + tpId : " ")
                +" AND C_DOMAIN_RESOURCES.NODEID = port.NODEID "
                +" AND C_DOMAIN_RESOURCES.ENTITYID = port.PHYSICALPORTID "
                +" AND C_DOMAIN_RESOURCES.DOMAINID =c_domain.domainid "
                +" AND c_domain.DOMAINTYPE = 3 ";
        String sql2 = " select "
                + "port.PHYSICALPORTID portID,"
                + "port.NODEID nodeID,    "
                +" port.userlabel portlabel, "
                + "ne.userlabel neName,   "
                +" port.INTERFACETYPE portType,"
                + "port.USEDBANDWIDTH bandWidth,"
                +" port.EGRESSSHAPINGRATE rate,   "
                +" NVL(ETHPRT.USAGESTATE+1,port.USAGESTATE) AS portState "
                +" from c_ne ne, c_ethport port,ETHPRT    "
                +" where port.nodeid= ne.neid AND    "
                +" ETHPRT.COREID(+) = PORT.physicalportid and port.AGGREGATIONID = -1 "
                +" AND ETHPRT.NEID(+) = PORT.NODEID  "
                +" and port.NODEID=" + neId
                + (tpId.length() > 0 ? " AND port.PHYSICALPORTID = " + tpId : " ");

        logger.info("getPortData sql1: " + sql1);

        DbRunner runner = new PktRunner().getRunner();

        List<Map<String, Object>> result1 = null;
        try {
            result1 = runner.queryMapList(sql1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Collection<TP> portLists1 = parsePortLists(result1);
        StringBuilder ids = new StringBuilder();
        for(TP bean:portLists1){
            ids.append(bean.getTpId());
            ids.append(",");
        }

        if(ids != null && ids.length() > 0) {
            String idsStr = ids.substring(0, ids.length()-1);
            sql2+=" AND port.PHYSICALPORTID NOT IN ("+idsStr+")";
        }

        logger.info("getPortData sql2: " + sql2);
        List<Map<String, Object>> result2 = null;
        try {
            result2 = runner.queryMapList(sql2);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Collection<TP> portLists2 = parsePortLists(result2);
        list.addAll(portLists1);
        list.addAll(portLists2);
        return list;
    }
    private Collection<TP> parsePortLists(List<Map<String, Object>> result1) {
        Collection<TP> tpList = new ArrayList<>();
        for (Map<String, Object> hashMap : result1) {
            /*String tpId = validateObj(hashMap.get("PORTID"));
            String neId = validateObj(hashMap.get("NODEID"));
            String tpName = validateObj(hashMap.get("PORTLABEL"));
            String tpType = validateObj(hashMap.get("PORTTYPE"));
            String neName = validateObj(hashMap.get("NENAME"));
            long bandWidth = Long.parseLong(validateObj(hashMap.get("BANDWIDTH")));
            long rate = Long.parseLong(validateObj(hashMap.get("RATE")));
            String portState = validateObj(hashMap.get("PORTSTATE"));
            String domain = validateObj(hashMap.get("DOMAIN"));*/

            TP tp = new TP();
            tp.setNeId(validateObj(hashMap.get("NODEID")));
            tp.setNeName(validateObj(hashMap.get("NENAME")));
            tp.setTpId(validateObj(hashMap.get("PORTID")));
            tp.setTpName(validateObj(hashMap.get("PORTLABEL")));
            tp.setEquipName(validateObj(hashMap.get("PORTTYPE")));

            tpList.add(tp);
        }
        return tpList;
    }

    private String validateObj(Object obj) {
        if (obj != null) {
            return obj.toString();
        } else {
            return "";
        }
    }
}
