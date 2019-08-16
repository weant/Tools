package com.hcop.ptn.common.db.dao;

import com.hcop.ptn.common.db.dbrunner.DbRunner;
import com.hcop.ptn.common.db.oms.PktRunner;
import com.hcop.ptn.common.db.oms.WdmRunner;
import com.hcop.ptn.common.utils.CollectionUtils;
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


    public List<Map<String,Object>> queryEVC(String condition) {
        String sql = "select evcprt.evcID connId,evc.userlabel connName,ethprt.neid neId, t_node.userlabel neName,"
                + " ethprt.interfaceindex  tpId, ethprt.userLabel tpName, ethprt.rate, "
                + " evc.serviceType evcServiceType, evc.provisioningState state, evc.servicestate servicestate, "
                + " evc.comments comments, evc.comments2 comments2, evc.comments3 comments3, "
                + " evc.text1 text1, evc.text2 text2, evc.text3 text3 "
                + " from evcprt, ethprt, t_node , evc, c_ne, c_domain cdo "
                + " where evcprt.ethPortID = ethprt.ethPortID"
                + " and t_node.tid = evcprt.neID"
                + " and c_ne.userlabel(+) = t_node.userlabel "
                + " and evc.evcid(+) = evcprt.evcid"
                + " and evc.provisioningState in (1,3)"
                + " and cdo.DOMAINTYPE = 3 and CDO.DOMAINID = evc.DOMAINID "
                +  condition
                + " order by evcprt.evcID ";

        return doQuery(sql);
    }

    public List<Map<String,Object>> queryCES(String condition) {
        String sql = "select  CES_NE.USERLABEL as NENAME, CES_NE.NEID, CES_PORT.PORTID tpId, CES_PORT.Userlabel as TPNAME, " +
                " CES.USERLABEL as connName, CES.CESID as connId,CES.CONFSTATE as confStat,CES.ENCAPTYPE as encapType," +
                " CES.AZFRAMEFMT as azFrameFormat, CES.ZAFRAMEFMT as zaFrameFormat, NVL(AVAILABILITYSTATE,1) AS availabilityState " +
                " from CES, CES_TUNNELs, CES_VLANPATH, CES_TRAILPOINT, CES_PORT, CES_NE,F_CES " +
                " where CES.CESID=CES_TUNNELS.CESID and CES_TUNNELS.VLANPATHID=CES_VLANPATH.VLANPATHID " +
                " and CES_VLANPATH.useExtTrail=0 and CES_TRAILPOINT.TRAILID=CES_VLANPATH.TRAILID " +
                " and CES_TRAILPOINT.POSINTRAIL=0 and  CES_TRAILPOINT.PORTID=CES_PORT.PORTID and  " +
                " CES_PORT.NEID=CES_NE.NEID and CES.CESID=F_CES.CESID(+)";
        if(condition!= null && condition.length() > 0){
            sql += " "+condition;
        }

        return doQuery(sql);
    }

    public List<Map<String,Object>> queryPW(String condition) {
        String sql = "select trailid connId, pwname connName, pir,comment1, cir, cbs,pbs,segaid, node.tid neId, node.userlabel as neName,"
                + " port.portid tpId, port.userlabel as tpName, bandwidth from (select trailid,userlabel as pwname,pir,"
                + " comment1,cbs,pbs,cir from t_trail where traillayer=1 "+ condition +") trail "
                + " inner join "
                + " (select userlabel as segname,portid,serverid,nodeid,segmentid,belongid,aidonne as segaid,traillayer "
                + " from t_segment where traillayer=1 and posintrail != 1) "
                + " seg on trail.trailid = seg.belongid "
                + " inner join t_node node "
                + " on node.nodeid = seg.nodeid "
                + " inner join t_port port "
                + " on port.portid = seg.portid";

        return doQuery(sql);
    }

    public List<Map<String,Object>> queryTUNNEL(String condition) {
        String sql = "select distinct trail.trailid connId, trail.userlabel connName, " +
                "trail.PIR,trail.PBS,trail.CIR,trail.CBS, " +
                "case when nodea.externalntw = 1 then nodea.userlabel else nea.userlabel end aNeName, " +
                "nea.neid aNeId, porta.userlabel aTpName, porta.portid aTpId, " +
                "porta.bandwidth aRate,case when nodez.externalntw = 1 then nodez.userlabel else nez.userlabel end zNeName, " +
                "nez.neid zNeId, portz.userlabel zTpName, portz.portid zTpId, portz.bandwidth zRate, sega.aidonne aaid, " +
                "sega.SEGMENTID asegid, mepa.mepid amepid, segz.aidonne zaid, segz.SEGMENTID zsegid, mepz.mepid zmepid, trail.comment1 comment1 " +
                "from t_trail trail,t_segment sega,t_segment segz,t_port porta,t_node nodea,t_port portz,t_node nodez,c_ne nea,c_ne nez,t_mep mepa, t_mep mepz " +
                "where trail.trailid = sega.belongid and sega.posintrail=0 " +
                "and  trail.trailid = segz.belongid and segz.posintrail=0 " +
                "and  sega.segmentid > segz.segmentid " +
                "and trail.traillayer=0 " +
                "and sega.portid = porta.portid and nodea.nodeid = porta.nodeid " +
                "and segz.portid = portz.portid and nodez.nodeid = portz.nodeid " +
                "and sega.SEGMENTID = mepa.SEGMENTID(+) " +
                "and segz.SEGMENTID = mepz.SEGMENTID(+) " +
                "and nodea.userlabel = nea.userlabel(+) " +
                "and nodez.userlabel = nez.userlabel(+) "
                + condition;

        return doQuery(sql);
    }

    public List<Map<String,Object>> querySECTION(String condition) {
        String sql = "select t.sectionId as connId,loc.userlabel as connName, loc.aportid as aTpId, " +
                " NVL(pa.userlabel, porta.userlabel) as aTpName, na.neid as aNeId, NVL(na.userlabel, nodea.userlabel) as aNeName, " +
                " loc.zportid as zTpId,NVL(pb.userlabel, portz.userlabel) as zTpName, nb.neid as zNeId, " +
                " NVL(nb.userlabel, nodez.userlabel) as zNeName, t.bandwidthaz aRate, t.bandwidthza zRate " +
                " from c_locable loc, c_ethport pa, c_ne na, c_ethport pb, c_ne nb, " +
                " t_section t, t_port porta, t_port portz, t_node nodea, t_node nodez " +
                " where loc.aportnodeid = na.neid(+) " +
                " and loc.aportnodeid = pa.nodeid(+) " +
                " and loc.aportid=pa.physicalportid(+) " +
                " and loc.zportnodeid = nb.neid(+)  "+
                " and loc.zportnodeid = pb.nodeid(+) " +
                " and loc.zportid=pb.physicalportid(+) " +
                " and t.aportid = porta.portid(+) " +
                " and t.zportid = portz.portid(+) " +
                " and loc.aportnodeid = nodea.tid(+) "+
                " and loc.zportnodeid = nodez.tid(+) "+
//				" and t.userlabel=loc.userlabel "
                " and t.coreid=loc.linkovercableid " +
                " and t.technology = 0 "
                + condition;

        return doQuery(sql);
    }

    private List<Map<String,Object>> doQuery(String sql) {
        DbRunner runner = new PktRunner().getRunner();
        List<Map<String,Object>> data = new ArrayList<>();
        try {
            data = runner.queryMapList(sql);
        } catch (SQLException e) {
            logger.error(e, e);
        }

        return data;
    }

    public Map<String,Object> queryTpRoleAll() {
        String sql = "select s.TERMINATIONROLE,s.PORTID,T.USERLABEL from T_TRAIL T, T_SEGMENT S where T.TRAILID=S.BELONGID and T.TRAILLAYER= 1";
        DbRunner runner = new PktRunner().getRunner();
        List<Map<String,Object>> data = new ArrayList<>();
        try {
            data = runner.queryMapList(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Map<String,Object> result = new HashMap<>();

        if(CollectionUtils.isEmpty(data)) {
            return result;
        }

        data.forEach(d->result.put(d.get("USERLABEL").toString() + "@" + d.get("PORTID").toString(), d.get("TERMINATIONROLE")));

        return result;
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
