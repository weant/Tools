package com.hcop.ptn.service;

import com.hcop.ptn.alarm.rootcause.beans.ConnectionBean;
import com.hcop.ptn.alarm.rootcause.beans.TpBean;
import com.hcop.ptn.common.db.dao.ConnectionDao;
import com.hcop.ptn.common.exception.ServiceException;
import com.hcop.ptn.restful.model.Connection;
import com.hcop.ptn.restful.model.ConnectionType;
import com.hcop.ptn.restful.model.TP;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class ConnectionService {
    private Log log = LogFactory.getLog(ConnectionService.class);
    private static ConnectionService ourInstance = new ConnectionService();

    public static ConnectionService getInstance() {
        return ourInstance;
    }

    private ConnectionService() {
    }

    public List<Connection> getConnectionsByType(ConnectionType type, String condition) throws ServiceException {
        List<Connection> results;
        try {

            List<Map<String,Object>> data = queryData(type, condition);
            results = handleData(data, type);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }

        return results;
    }

    public List<ConnectionBean> getConnectionBeansByType(ConnectionType type, String condition) throws ServiceException {
        List<ConnectionBean> results;
        try {

            List<Map<String,Object>> data = queryData(type, condition);
            results = handleDatas(data, type);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }

        return results;
    }

    private List<Map<String,Object>> queryData(ConnectionType type, String condition) throws ServiceException {
        List<Map<String,Object>> data = null;

        switch (type) {
            case EVC:
                data = ConnectionDao.getInstance().queryEVC(condition);
                break;
            case CES:
                data = ConnectionDao.getInstance().queryCES(condition);
                break;
            case PW:
                data = ConnectionDao.getInstance().queryPW(condition);
                break;
            case TUNNEL:
                data = ConnectionDao.getInstance().queryTUNNEL(condition);
                break;
            case SECTION:
                data = ConnectionDao.getInstance().querySECTION(condition);
                break;
            default:
        }

        if(data.isEmpty()) {
            return Collections.emptyList();
        }

        return data;
    }

    private List<Connection> handleData(List<Map<String,Object>> data, ConnectionType type) {
        List<Connection> results = new ArrayList <>();
        Set<String> connIds = new HashSet <>();
        for(Map<String,Object> map : data) {
            List<TP> tps = new ArrayList<>();
            if(map.containsKey("ANEID")) {
                TP tp = new TP();
                tp.setNeId(map.get("ANEID").toString());
                tp.setNeName(map.get("ANENAME").toString());
                tp.setTpId(map.get("ATPID").toString());
                tp.setTpName(map.get("ATPNAME").toString());
                tps.add(tp);
            }

            if(map.containsKey("ZNEID")) {
                TP tp = new TP();
                tp.setNeId(map.get("ZNEID").toString());
                tp.setNeName(map.get("ZNENAME").toString());
                tp.setTpId(map.get("ZTPID").toString());
                tp.setTpName(map.get("ZTPNAME").toString());
                tps.add(tp);
            }

            if(map.containsKey("NEID")) {
                TP tp = new TP();
                tp.setNeId(map.get("NEID").toString());
                tp.setNeName(map.get("NENAME").toString());
                tp.setTpId(map.get("TPID").toString());
                tp.setTpName(map.get("TPNAME").toString());
                tps.add(tp);
            }

            Connection connection = new Connection();

            if(!connIds.contains(map.get("CONNID").toString())) {
                connection.setConnId(map.get("CONNID").toString());
                connection.setConnName(map.get("CONNNAME").toString());
                connection.setConnType(type);
                results.add(connection);
                connIds.add(map.get("CONNID").toString());
            } else {
                for(Connection con : results) {
                    if(con.getConnId().equals(map.get("CONNID").toString())) {
                        connection =  con;
                        break;
                    }
                }
            }

            for(TP tp : tps) {
                if (!connection.getTps().contains(tp)) {
                    connection.getTps().add(tp);
                }
            }

            //设置PW的端口角色
            /*if(type.equals(ConnectionType.PW)) {
                Map<String,Object> roleMap = ConnectionDao.getInstance().queryTpRoleAll();
                Object role = roleMap.get(map.get("CONNNAME").toString() + "@" + map.get("TPID").toString());
            }*/
        }

        // TODO: 2019/2/16 设置端口所属板卡的类型

        return results;
    }


    private List<ConnectionBean> handleDatas(List<Map<String,Object>> data, ConnectionType type) {
        List<ConnectionBean> results = new ArrayList <>();
        Set<String> connIds = new HashSet <>();
        for(Map<String,Object> map : data) {
            List<TpBean> tps = new ArrayList<>();
            if(map.containsKey("ANEID")) {
                TpBean tp = new TpBean();
                tp.setNeId(map.get("ANEID").toString());
                tp.setNeName(map.get("ANENAME").toString());
                tp.setTpId(map.get("ATPID").toString());
                tp.setTpName(map.get("ATPNAME").toString());
                addTpExtendedFields(map, tp, type);
                tps.add(tp);
            }

            if(map.containsKey("ZNEID")) {
                TpBean tp = new TpBean();
                tp.setNeId(map.get("ZNEID").toString());
                tp.setNeName(map.get("ZNENAME").toString());
                tp.setTpId(map.get("ZTPID").toString());
                tp.setTpName(map.get("ZTPNAME").toString());
                addTpExtendedFields(map, tp, type);
                tps.add(tp);
            }

            if(map.containsKey("NEID")) {
                TpBean tp = new TpBean();
                tp.setNeId(map.get("NEID").toString());
                tp.setNeName(map.get("NENAME").toString());
                tp.setTpId(map.get("TPID").toString());
                tp.setTpName(map.get("TPNAME").toString());
                addTpExtendedFields(map, tp, type);
                tps.add(tp);
            }

            ConnectionBean connection = new ConnectionBean();

            if(!connIds.contains(map.get("CONNID").toString())) {
                connection.setConnId(map.get("CONNID").toString());
                connection.setConnName(map.get("CONNNAME").toString());
                connection.setConnType(type);
                addConExtendedFields(map, connection, type);
                results.add(connection);
                connIds.add(map.get("CONNID").toString());
            } else {
                for(ConnectionBean con : results) {
                    if(con.getConnId().equals(map.get("CONNID").toString())) {
                        connection =  con;
                        break;
                    }
                }
            }

            for(TpBean tp : tps) {
                if (!connection.getTps().contains(tp)) {
                    connection.getTps().add(tp);
                }
            }

            //设置PW的端口角色
            /*if(type.equals(ConnectionType.PW)) {
                Map<String,Object> roleMap = ConnectionDao.getInstance().queryTpRoleAll();
                Object role = roleMap.get(map.get("CONNNAME").toString() + "@" + map.get("TPID").toString());
            }*/
        }

        // TODO: 2019/2/16 设置端口所属板卡的类型

        return results;
    }

    private void addTpExtendedFields(Map<String,Object> map, TpBean tp, ConnectionType type) {
        Map<String, Object> fields = tp.getExtendedFields();
        try {
            switch ( type ) {
                case EVC:
                    fields.put("rate", map.get("rate".toUpperCase()) == null ? "" : map.get("rate".toUpperCase()).toString());
                    break;
                case CES:
                    break;
                case PW:
                    fields.put("segaid", map.get("segaid".toUpperCase()) == null ? "" : map.get("segaid".toUpperCase()).toString());
                    fields.put("segname", map.get("segname".toUpperCase()) == null ? "" : map.get("segname".toUpperCase()).toString());
                    fields.put("segmentid", map.get("segmentid".toUpperCase()) == null ? "" : map.get("segmentid".toUpperCase()).toString());
                    fields.put("traillayer", map.get("traillayer".toUpperCase()) == null ? "" : map.get("traillayer".toUpperCase()).toString());
                    fields.put("belongid", map.get("belongid".toUpperCase()) == null ? "" : map.get("belongid".toUpperCase()).toString());
                    fields.put("serverid", map.get("serverid".toUpperCase()) == null ? "" : map.get("serverid".toUpperCase()).toString());
                    fields.put("bandwidth", map.get("bandwidth".toUpperCase()) == null ? "" : map.get("bandwidth".toUpperCase()).toString());
                    break;
                case TUNNEL:
                    String preStr = "";
                    if (map.containsKey("ANEID")) {
                        if (tp.getNeName().equals(map.get("ANENAME").toString())) {
                            preStr = "A";
                        }
                    }

                    if (map.containsKey("ZNEID")) {
                        if (tp.getNeName().equals(map.get("ZNENAME").toString())) {
                            preStr = "Z";
                        }
                    }

                    if (map.containsKey("NEID")) {
                        if (tp.getNeName().equals(map.get("NENAME").toString())) {
                            preStr = "";
                        }
                    }

                    fields.put("rate", map.get(preStr + "rate".toUpperCase()) == null ? "" : map.get(preStr + "rate".toUpperCase()).toString());
                    fields.put("aid", map.get(preStr + "aid".toUpperCase()) == null ? "" : map.get(preStr + "aid".toUpperCase()).toString());
                    fields.put("segid", map.get(preStr + "segid".toUpperCase()) == null ? "" : map.get(preStr + "segid".toUpperCase()).toString());
                    fields.put("mepid", map.get(preStr + "mepid".toUpperCase()) == null ? "" : map.get(preStr + "mepid".toUpperCase()).toString());

                    break;
                case SECTION:
                    String pre = "";
                    if (map.containsKey("ANEID")) {
                        if (tp.getNeName().equals(map.get("ANENAME").toString())) {
                            pre = "A";
                        }
                    }

                    if (map.containsKey("ZNEID")) {
                        if (tp.getNeName().equals(map.get("ZNENAME").toString())) {
                            pre = "Z";
                        }
                    }

                    if (map.containsKey("NEID")) {
                        if (tp.getNeName().equals(map.get("NENAME").toString())) {
                            pre = "";
                        }
                    }

                    fields.put("rate", map.get("rate".toUpperCase()) == null ? "" : map.get(pre + "rate".toUpperCase()).toString());
                    break;
                default:
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void addConExtendedFields(Map<String,Object> map, ConnectionBean con, ConnectionType type) {
        Map<String, Object> fields = con.getExtendedFields();

        try {
            switch ( type ) {
                case EVC:
                    fields.put("comments", map.get("comments".toUpperCase()) == null ? "" : map.get("comments".toUpperCase()).toString());
                    fields.put("comments2", map.get("comments2".toUpperCase()) == null ? "" : map.get("comments2".toUpperCase()).toString());
                    fields.put("comments3", map.get("comments3".toUpperCase()) == null ? "" : map.get("comments3".toUpperCase()).toString());
                    fields.put("text1", map.get("text1".toUpperCase()) == null ? "" : map.get("text1".toUpperCase()).toString());
                    fields.put("text2", map.get("text2".toUpperCase()) == null ? "" : map.get("text2".toUpperCase()).toString());
                    fields.put("text3", map.get("text3".toUpperCase()) == null ? "" : map.get("text3".toUpperCase()).toString());
                    break;
                case CES:
                    fields.put("confStat", map.get("confStat".toUpperCase()) == null ? "" : map.get("confStat".toUpperCase()).toString());
                    fields.put("encapType", map.get("encapType".toUpperCase()) == null ? "" : map.get("encapType".toUpperCase()).toString());
                    fields.put("azFrameFormat", map.get("azFrameFormat".toUpperCase()) == null ? "" : map.get("azFrameFormat".toUpperCase()).toString());
                    fields.put("zaFrameFormat", map.get("zaFrameFormat".toUpperCase()) == null ? "" : map.get("zaFrameFormat".toUpperCase()).toString());
                    fields.put("availabilityState", map.get("availabilityState".toUpperCase()) == null ? "" : map.get("availabilityState".toUpperCase()).toString());
                    break;
                case PW:
                    fields.put("pir", map.get("pir".toUpperCase()) == null ? "" : map.get("pir".toUpperCase()).toString());
                    fields.put("cir", map.get("cir".toUpperCase()) == null ? "" : map.get("cir".toUpperCase()).toString());
                    fields.put("cbs", map.get("cbs".toUpperCase()) == null ? "" : map.get("cbs".toUpperCase()).toString());
                    fields.put("pbs", map.get("pbs".toUpperCase()) == null ? "" : map.get("pbs".toUpperCase()).toString());
                    fields.put("comment1", map.get("comment1".toUpperCase()) == null ? "" : map.get("comment1".toUpperCase()).toString());
                    break;
                case TUNNEL:
                    fields.put("pir", map.get("pir".toUpperCase()) == null ? "" : map.get("pir".toUpperCase()).toString());
                    fields.put("cir", map.get("cir".toUpperCase()) == null ? "" : map.get("cir".toUpperCase()).toString());
                    fields.put("cbs", map.get("cbs".toUpperCase()) == null ? "" : map.get("cbs".toUpperCase()).toString());
                    fields.put("pbs", map.get("pbs".toUpperCase()) == null ? "" : map.get("pbs".toUpperCase()).toString());
                    fields.put("comment1", map.get("comment1".toUpperCase()) == null ? "" : map.get("comment1".toUpperCase()).toString());
                    break;
                case SECTION:
                    break;
                default:
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
