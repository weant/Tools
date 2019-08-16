package com.hcop.otn.service;

import com.hcop.otn.common.db.dao.ConnectionDao;
import com.hcop.otn.common.exception.ServiceException;
import com.hcop.otn.restful.model.Connection;
import com.hcop.otn.restful.model.ConnectionType;
import com.hcop.otn.restful.model.TP;
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

    public List<Connection> getConnectionsByType(ConnectionType type) throws ServiceException {
        List<Connection> results;
        try {
            List<Map<String,Object>> data;
            if(type.equals(ConnectionType.PATH)) {
                data = ConnectionDao.getInstance().getAllPath();
            } else {
                data = ConnectionDao.getInstance().getConnectionByType(type);
            }

            if(data.isEmpty()) {
                return Collections.emptyList();
            }

            results = handleData(data, type);

            if(type.equals(ConnectionType.OTS)) {
                ListIterator<Connection> it = results.listIterator();
                while(it.hasNext()) {
                    Connection con = it.next();
                    for(TP tp : con.getTps()) {
                        if(tp.getTpName().contains("OMD")) {
                            it.remove();
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }

        return results;
    }

    public List<Connection> getDsrByOchId(String ochId) throws ServiceException {
        List<Connection> results;
        try {
            if (ochId.trim().length() < 1) {
                throw new ServiceException( 400, "Parameter Error: empty och id");
            }

            List<Map<String,Object>> data = ConnectionDao.getInstance().getAllPathByOchId(ochId);

            if(data.isEmpty()) {
                return Collections.emptyList();
            }

            results = handleData(data, ConnectionType.PATH);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }

        return results;
    }


    private List<Connection> handleData(List<Map<String,Object>> data, ConnectionType type){
        List<Connection> results = new ArrayList <>();
        Set<String> ptpIds = new HashSet <>();
        Set<String> connIds = new HashSet <>();
        Map<String,String> tpAndPtpMap = new HashMap<>();
        for(Map<String,Object> map : data) {
            TP tpA = new TP();
            tpA.setNeId(map.get("ANEID").toString());
            tpA.setNeName(map.get("ANENAME").toString());
            tpA.setTpId(map.get("ATPID").toString());
            tpA.setTpName(map.get("ATPNAME").toString());

            TP tpZ = new TP();
            tpZ.setNeId(map.get("ZNEID").toString());
            tpZ.setNeName(map.get("ZNENAME").toString());
            tpZ.setTpId(map.get("ZTPID").toString());
            tpZ.setTpName(map.get("ZTPNAME").toString());

            Connection connection = new Connection();

            if(!connIds.contains(map.get("CONNECTIONID").toString())) {
                connection.setConnId(map.get("CONNECTIONID").toString());
                connection.setConnName(map.get("CONNECTIONNAME").toString());
                connection.setConnType(type);
                results.add(connection);
                connIds.add(map.get("CONNECTIONID").toString());
            } else {
                for(Connection con : results) {
                    if(con.getConnId().equals(map.get("CONNECTIONID").toString())) {
                        connection =  con;
                        break;
                    }
                }
            }

            if(!connection.getTps().contains(tpA)) {
                connection.getTps().add(tpA);
            }

            if(!connection.getTps().contains(tpZ)) {
                connection.getTps().add(tpZ);
            }


            if(null == map.get("APTPID") || "".equals(map.get("APTPID").toString())) {
                ptpIds.add(map.get("ATPID").toString());
                tpAndPtpMap.put(map.get("ATPID").toString(), map.get("ATPID").toString());
            } else {
                ptpIds.add(map.get("APTPID").toString());
                tpAndPtpMap.put(map.get("ATPID").toString(), map.get("APTPID").toString());
            }

            if(null == map.get("ZPTPID") || "".equals(map.get("ZPTPID").toString())) {
                ptpIds.add(map.get("ZTPID").toString());
                tpAndPtpMap.put(map.get("ZTPID").toString(), map.get("ZTPID").toString());
            } else {
                ptpIds.add(map.get("ZPTPID").toString());
                tpAndPtpMap.put(map.get("ZTPID").toString(), map.get("ZPTPID").toString());
            }
        }

        List<Map<String,Object>> equipMapList = new ArrayList<>();
        int subSize = 800;
        if(ptpIds.size() > subSize) {
            List<String> allData = new ArrayList<>();
            allData.addAll(ptpIds);
            List<String> subList;
            int Count = ptpIds.size();
            int subPageTotal = (Count / subSize) + ((Count % subSize > 0) ? 1 : 0);
            for (int i = 0, len = subPageTotal - 1; i <= len; i++) {
                int fromIndex = i * subSize;
                int toIndex = ((i == len) ? Count : ((i + 1) * subSize));
                subList = allData.subList(fromIndex, toIndex);
                equipMapList.addAll(ConnectionDao.getInstance().getTpAndEquipMap(subList));
            }
        } else {
            equipMapList.addAll(ConnectionDao.getInstance().getTpAndEquipMap(ptpIds));
        }

        Map<String,String> equipMap = new HashMap <>(equipMapList.size());
        for(Map<String,Object> map : equipMapList) {
            equipMap.put(map.get("TPID").toString(), map.get("CARDNAME").toString());
        }

        for(Connection con : results) {
            for(TP tp : con.getTps()) {
                tp.setEquipName(equipMap.get(tpAndPtpMap.get(tp.getTpId())));
            }
        }

        return results;
    }
}
