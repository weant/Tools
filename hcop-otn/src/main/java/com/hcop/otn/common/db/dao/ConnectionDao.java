package com.hcop.otn.common.db.dao;

import com.hcop.otn.common.db.dbrunner.DbRunner;
import com.hcop.otn.common.db.oms.WdmRunner;
import com.hcop.otn.common.utils.CollectionUtils;
import com.hcop.otn.restful.model.ConnectionType;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

public class ConnectionDao {
    private static Logger logger = Logger.getLogger(ConnectionDao.class);
    private static ConnectionDao ourInstance = new ConnectionDao();
    public static ConnectionDao getInstance() {
        return ourInstance;
    }

    private ConnectionDao() {
    }

    public List<Map<String,Object>> getConnectionByType(ConnectionType type) {
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("SELECT ");
        baseSql.append(" nc.connectionid, ");
        baseSql.append(" nc.connectionname, ");
        baseSql.append(" anode.handle aneid, ");
        baseSql.append(" anode.name anename, ");
        baseSql.append(" atp.tpid atpid, ");
        baseSql.append(" atp.ptpid aptpid, ");
        baseSql.append(" atp.tpnativename atpname, ");
        baseSql.append(" znode.handle zneid, ");
        baseSql.append(" znode.name znename, ");
        baseSql.append(" ztp.tpid ztpid, ");
        baseSql.append(" ztp.ptpid zptpid, ");
        baseSql.append(" ztp.tpnativename ztpname ");
        baseSql.append(" FROM ");
        baseSql.append(" networkconnection nc, ");
        baseSql.append(" nccomponent ncc, ");
        baseSql.append(" tp atp, ");
        baseSql.append(" tp ztp, ");
        baseSql.append(" node anode, ");
        baseSql.append(" node znode, ");
        baseSql.append(" LAYERRATEMAP lrp ");
        baseSql.append(" WHERE ");
        baseSql.append(" ncc.connectionid = nc.connectionid ");
        baseSql.append(" AND nc.connectionrate = lrp.Layerratevalue ");
        baseSql.append(" AND ncc.sinkneid != ncc.srcneid ");
        baseSql.append(" AND ncc.sinktpid = ztp.tpid ");
        baseSql.append(" AND ncc.srctpid = atp.tpid ");
        baseSql.append(" AND ncc.sinkneid = znode.handle ");
        baseSql.append(" AND ncc.srcneid = anode.handle ");
        baseSql.append(" AND lrp.Layerratename LIKE '");
        baseSql.append(getInternalTypeString(type));
        baseSql.append("%'");

        DbRunner runner = new WdmRunner().getRunner();
        List<Map<String,Object>> data = new ArrayList<>();
        try {
            data = runner.queryMapList(baseSql.toString());
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }

    private String getInternalTypeString(ConnectionType type) {
        switch(type) {
            case OTS:
                return "ots";
            case OMS:
                return "oms";
            case OCH:
                return "otu";
            case PATH:
                return "dsr";
            case TRAIL:
                return "o_u";
            default:
                return "ots";
        }
    }

    public List<Map<String,Object>> getAllPath() {
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("SELECT ");
        baseSql.append(" nc.connectionid, ");
        baseSql.append(" nc.connectionname, ");
        baseSql.append(" anode.handle aneid, ");
        baseSql.append(" anode.name anename, ");
        baseSql.append(" atp.tpid atpid, ");
        baseSql.append(" atp.ptpid aptpid, ");
        baseSql.append(" atp.tpnativename atpname, ");
        baseSql.append(" znode.handle zneid, ");
        baseSql.append(" znode.name znename, ");
        baseSql.append(" ztp.tpid ztpid, ");
        baseSql.append(" ztp.ptpid zptpid, ");
        baseSql.append(" ztp.tpnativename ztpname ");
        baseSql.append(" FROM ");
        baseSql.append(" networkconnection nc, ");
        baseSql.append(" nccomponent ncc, ");
        baseSql.append(" tp atp, ");
        baseSql.append(" tp ztp, ");
        baseSql.append(" node anode, ");
        baseSql.append(" node znode ");
        baseSql.append(" WHERE ");
        baseSql.append(" ncc.connectionid = nc.connectionid ");
        baseSql.append(" AND ncc.sinktpid = ztp.tpid ");
        baseSql.append(" AND ncc.srctpid = atp.tpid ");
        baseSql.append(" AND ncc.sinkneid = znode.handle ");
        baseSql.append(" AND ncc.srcneid = anode.handle ");
        baseSql.append(" AND nc.servicerate = 1");

        DbRunner runner = new WdmRunner().getRunner();
        List<Map<String,Object>> data = new ArrayList<>();
        try {
            data = runner.queryMapList(baseSql.toString());
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }


    public List<Map<String,Object>> getConnectionByIds(Collection<String> connIds) {
        if(connIds.size() < 1) {
            return Collections.emptyList();
        }

        StringBuilder baseSql = new StringBuilder();
        baseSql.append("SELECT ");
        baseSql.append(" nc.connectionid, ");
        baseSql.append(" nc.connectionname, ");
        baseSql.append(" anode.handle aneid, ");
        baseSql.append(" anode.name anename, ");
        baseSql.append(" atp.tpid atpid, ");
        baseSql.append(" atp.ptpid aptpid, ");
        baseSql.append(" atp.tpnativename atpname, ");
        baseSql.append(" znode.handle zneid, ");
        baseSql.append(" znode.name znename, ");
        baseSql.append(" ztp.tpid ztpid, ");
        baseSql.append(" ztp.ptpid zptpid, ");
        baseSql.append(" ztp.tpnativename ztpname ");
        baseSql.append(" FROM ");
        baseSql.append(" networkconnection nc, ");
        baseSql.append(" nccomponent ncc, ");
        baseSql.append(" tp atp, ");
        baseSql.append(" tp ztp, ");
        baseSql.append(" node anode, ");
        baseSql.append(" node znode ");
        baseSql.append(" WHERE ");
        baseSql.append(" ncc.connectionid = nc.connectionid ");
        baseSql.append(" AND ncc.sinktpid = ztp.tpid ");
        baseSql.append(" AND ncc.srctpid = atp.tpid ");
        baseSql.append(" AND ncc.sinkneid = znode.handle ");
        baseSql.append(" AND ncc.srcneid = anode.handle ");
        baseSql.append(generateSQLForIntCondition("nc.connectionid", connIds));

        DbRunner runner = new WdmRunner().getRunner();
        List<Map<String,Object>> data = new ArrayList<>();
        try {
            data = runner.queryMapList(baseSql.toString());
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }

    public List<Map<String,Object>> getAllParentConnectionsByIds(List<String> connIds) {
        if(connIds.size() < 1) {
            return Collections.emptyList();
        }
        StringBuilder ids = new StringBuilder();
        connIds.forEach(id->ids.append(id+","));

        StringBuilder baseSql = new StringBuilder();
        baseSql.append("SELECT NNN.NCID, NNN.INUSECONNECTIONID, NNN.SERVERRATE, LAYERRATEMAP.LAYERRATENAME ");
        baseSql.append(" FROM ");
        baseSql.append(" (SELECT DISTINCT(NCID),INUSECONNECTIONID,SERVERRATE ");
        baseSql.append("   FROM ");
        baseSql.append("    LCC START WITH INUSECONNECTIONID IN (");
        baseSql.append(ids.substring(0, ids.length()-1));
        baseSql.append(") CONNECT BY NOCYCLE PRIOR NCID = INUSECONNECTIONID) NNN, ");
        baseSql.append(" LAYERRATEMAP ");
        baseSql.append(" WHERE NNN.SERVERRATE = LAYERRATEMAP.LAYERRATEVALUE ");
        baseSql.append(" ORDER BY NNN.NCID ");

        DbRunner runner = new WdmRunner().getRunner();
        List<Map<String,Object>> data = new ArrayList<>();
        try {
            data = runner.queryMapList(baseSql.toString());
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }

    public List<Map<String,Object>> getAllPathByOchId(String ochId) {
        if(ochId == null || "".equals(ochId)) {
            return Collections.emptyList();
        }
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("SELECT ");
        baseSql.append("	nc.connectionid, ");
        baseSql.append("	nc.connectionname, ");
        baseSql.append("	anode.handle aneid, ");
        baseSql.append("	anode. NAME anename, ");
        baseSql.append("	atp.tpid atpid, ");
        baseSql.append("	atp.ptpid aptpid, ");
        baseSql.append("	atp.tpnativename atpname, ");
        baseSql.append("	znode.handle zneid, ");
        baseSql.append("	znode. NAME znename, ");
        baseSql.append("	ztp.tpid ztpid, ");
        baseSql.append("	ztp.ptpid zptpid, ");
        baseSql.append("	ztp.tpnativename ztpname ");
        baseSql.append("FROM ");
        baseSql.append("	networkconnection nc, ");
        baseSql.append("	nccomponent ncc, ");
        baseSql.append("	tp atp, ");
        baseSql.append("	tp ztp, ");
        baseSql.append("	node anode, ");
        baseSql.append("	node znode, ");
        baseSql.append("	( ");
        baseSql.append("		SELECT ");
        baseSql.append("			cs.CLIENTNCID ");
        baseSql.append("		FROM ");
        baseSql.append("			CONNCLIENTSERVER cs START WITH cs.SERVERNCID = ");
        baseSql.append(ochId);
        baseSql.append(" CONNECT BY cs.SERVERNCID = PRIOR cs.CLIENTNCID ");
        baseSql.append("	) lcaa ");
        baseSql.append("WHERE ");
        baseSql.append("	LCAA.CLIENTNCID = NCC.CONNECTIONID ");
        baseSql.append("AND ncc.connectionid = nc.connectionid ");
        baseSql.append("AND ncc.sinktpid = ztp.tpid ");
        baseSql.append("AND ncc.srctpid = atp.tpid ");
        baseSql.append("AND ncc.sinkneid = znode.handle ");
        baseSql.append("AND ncc.srcneid = anode.handle ");
        baseSql.append("AND nc.servicerate = 1 ");

        DbRunner runner = new WdmRunner().getRunner();
        List<Map<String,Object>> data = new ArrayList<>();
        try {
            data = runner.queryMapList(baseSql.toString());
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }

    public List<Map<String,Object>> getTpAndEquipMap(Collection<String> ptpIds) {
        if(CollectionUtils.isEmpty(ptpIds)) {
            return Collections.emptyList();
        }
        StringBuilder baseSql = new StringBuilder();
        baseSql.append("SELECT TP.TPID, EQ.CARDNAME FROM TP, EQUIPMENT EQ WHERE EQ.EQUIPID = TP.EQUIPID ");
        baseSql.append(generateSQLForIntCondition("TP.TPID", ptpIds));
        DbRunner runner = new WdmRunner().getRunner();
        List<Map<String,Object>> data = new ArrayList<>();
        try {
            data = runner.queryMapList(baseSql.toString());
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }

    private String generateSQLForIntCondition(String fieldName, Collection<String> parameters) {
        if(CollectionUtils.isEmpty(parameters)) {
            return "";
        }

        StringBuilder str = new StringBuilder(" AND " + fieldName + " IN (");

        parameters.forEach(p-> {
            if(p != null && !"".equals(p)) {
                str.append(p+",");
            }
        });

        return str.substring(0, str.lastIndexOf(",")) + ")";
    }
}
