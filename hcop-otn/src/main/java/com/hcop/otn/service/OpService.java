package com.hcop.otn.service;

import com.hcop.otn.common.beans.InternalNe;
import com.hcop.otn.common.beans.InternalTp;
import com.hcop.otn.common.beans.OpticalPower;
import com.hcop.otn.common.db.dao.ConnectionDao;
import com.hcop.otn.common.db.dao.NeDao;
import com.hcop.otn.common.db.dao.TpDao;
import com.hcop.otn.common.exception.ServiceException;
import com.hcop.otn.common.obtainop.PhnTpOpUtil;
import com.hcop.otn.common.utils.CollectionUtils;
import com.hcop.otn.restful.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.*;

public class OpService {
    private Log log = LogFactory.getLog(OpService.class);
    private static OpService ourInstance = new OpService();
    private static final String NE_REACHABLE = "0";

    public static OpService getInstance() {
        return ourInstance;
    }

    private OpService() {
    }


    public List<ConnOp> getConnectionOps(List<String> connIds) throws ServiceException {
        if (CollectionUtils.isEmpty(connIds)) {
            throw new ServiceException( 400, "Parameter Error: empty parameter");
        }
        try {

            List<ConnOp> results = new LinkedList <>();

            List<Map<String, Object>> allLayerData = ConnectionDao.getInstance().getAllParentConnectionsByIds(connIds);
            //根据树形结果生成连接id与OTS之间的映射关系
            Map<String, Set<String>> conMap = new HashMap <>(connIds.size());
            for(String id : connIds) {
                if(!conMap.containsKey(id)) {
                    conMap.put(id, new HashSet <>());
                }

                conMap.get(id).add(id);
                conMap.get(id).addAll(getOtsIds(id, allLayerData));
            }

            Set<String> ids = new LinkedHashSet <>();
            conMap.values().forEach(set->ids.addAll(set));

            //根据连接Id获取连接端口信息
            List <Map <String, Object>> queryData = ConnectionDao.getInstance().getConnectionByIds(ids);
            if(CollectionUtils.isEmpty(queryData)) {
                return Collections.emptyList();
            }

            Set<String> ptpIds = new HashSet <>();
            Set<String> tempConnId = new HashSet <>();
            Map<String,String> tpAndPtpMap = new HashMap<>();
            for(Map<String,Object> map : queryData) {
                TpOp tpA = new TpOp();
                tpA.setNeId(map.get("ANEID").toString());
                tpA.setNeName(map.get("ANENAME").toString());
                tpA.setTpId(map.get("ATPID").toString());
                tpA.setTpName(map.get("ATPNAME").toString());

                TpOp tpZ = new TpOp();
                tpZ.setNeId(map.get("ZNEID").toString());
                tpZ.setNeName(map.get("ZNENAME").toString());
                tpZ.setTpId(map.get("ZTPID").toString());
                tpZ.setTpName(map.get("ZTPNAME").toString());

                ConnOp connOp = new ConnOp();

                if (!tempConnId.contains(map.get("CONNECTIONID").toString())) {
                    connOp.setConnId(map.get("CONNECTIONID").toString());
                    connOp.setTpOps(new LinkedList <>());
                    results.add(connOp);
                    tempConnId.add(map.get("CONNECTIONID").toString());
                } else {
                    for (ConnOp con : results) {
                        if (con.getConnId().equals(map.get("CONNECTIONID").toString())) {
                            connOp = con;
                            break;
                        }
                    }
                }

                if(!connOp.getTpOps().contains(tpA)) {
                    connOp.getTpOps().add(tpA);
                }

                if(!connOp.getTpOps().contains(tpZ)) {
                    connOp.getTpOps().add(tpZ);
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

            Map<InternalNe, List<InternalTp>> neTpMap = getInternalNeAndTpMap(ptpIds);

            List <TpOp> ops = batchObtainOp(neTpMap);

            //处理光功率数据
            for(ConnOp con : results) {
                for(TpOp tp : con.getTpOps()) {
                    for (TpOp op : ops) {
                        if(op.getNeId().equals(tp.getNeId())
                                && op.getTpId().equals(tpAndPtpMap.get(tp.getTpId()))) {
                            tp.setOpr(op.getOpr());
                            tp.setOpt(op.getOpt());
                            break;
                        }
                    }
                }
            }

            List<ConnOp> realResults = new LinkedList <>();
            for(String id : conMap.keySet()) {
                ConnOp con = new ConnOp();
                con.setConnId(id);
                for(ConnOp cop : results) {
                    if(conMap.get(id).contains(cop.getConnId())) {
                        con.getTpOps().addAll(cop.getTpOps());
                    }
                }
                realResults.add(con);
            }

            return realResults;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException( 500, e.getMessage());
        }
    }

    public Set<String> getOtsIds(String id, List<Map<String, Object>> allLayerData) {
        Set<String> otsIds = new HashSet <>();

        for(Map<String, Object> map : allLayerData) {
            if(map.get("INUSECONNECTIONID").toString().equals(id)) {
                String parentId = map.get("NCID").toString();
                String layer = map.get("LAYERRATENAME").toString();
                if("ots".equalsIgnoreCase(layer)) {
                    otsIds.add(parentId);
                } else {
                    otsIds.addAll(getOtsIds(parentId, allLayerData));
                }
            }
        }

        return otsIds;
    }

    public List<TpOp> getTpOps(List<NeTp> neTps) throws ServiceException {
        try {
            if (CollectionUtils.isEmpty(neTps)) {
                throw new ServiceException( 400, "Parameter Error: empty parameter");
            }

            List<String> tpIds = new ArrayList<>();
            neTps.forEach(neTp->tpIds.addAll(neTp.getTpIds()));
            if(CollectionUtils.isEmpty(tpIds)){
                throw new ServiceException( 400, "Parameter Error: empty Tp");
            }

            Map<InternalNe, List<InternalTp>> neTpMap = getInternalNeAndTpMap(tpIds);

            List <TpOp> ops = batchObtainOp(neTpMap);

            //处理没在ops中的端口或网元
            List<String> tpInOps = new ArrayList<>();
            ops.forEach(op->tpInOps.add(op.getNeId()+"@"+op.getTpId()));

            for(NeTp neTp : neTps) {
                for(String tpId : neTp.getTpIds()) {
                    if(!tpInOps.contains(neTp.getNeId() + "@" + tpId)) {
                        TpOp temp = new TpOp();
                        temp.setNeId(neTp.getNeId());
                        temp.setTpId(tpId);
                        temp.setOpr("--");
                        temp.setOpt("--");

                        label:
                        for (Map.Entry<InternalNe, List<InternalTp>> entry : neTpMap.entrySet()) {
                            if (entry.getKey().getNeId().equals(neTp.getNeId())) {
                                temp.setNeName(entry.getKey().getNeName());
                                for (InternalTp iTp : entry.getValue()) {
                                    if (iTp.getTpId().equals(tpId)) {
                                        temp.setTpName(iTp.getTpName());
                                        break label;
                                    }
                                }
                            }
                        }

                        ops.add(temp);
                    }
                }
            }

            return ops;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException( 500, e.getMessage());
        }
    }

    private Map<InternalNe, List<InternalTp>> getInternalNeAndTpMap(Collection<String> tpIds){
        Collection<InternalNe> nes = NeDao.getInstance().getAllInternalNes();
        Collection<InternalTp> tps = TpDao.getInstance().getInternalTpsByTpId(tpIds);
        log.info("tps size:" + tps.size());
        Map<InternalNe, List<InternalTp>> neTpMap = new HashMap<>(nes.size());

        tps.forEach(tp->{
            for(InternalNe ne : nes) {
                if(ne.getNeId().equals(tp.getNeId())) {
                    if(!neTpMap.containsKey(ne)) {
                        neTpMap.put(ne, new ArrayList<>());
                    }
                    neTpMap.get(ne).add(tp);
                    break;
                }
            }

        });
        return neTpMap;
    }

    private List <TpOp> batchObtainOp(Map<InternalNe, List<InternalTp>> neTpMap) throws ServiceException {
        ExecutorService service = null;
        try {
            log.info("neTpMap size: " + neTpMap.size());
            List <TpOp> ops = new LinkedList <>();
            final List<Callable<List<OpticalPower>>> calls = new ArrayList<>();
            neTpMap.forEach((key,value)->{
                if(NE_REACHABLE.equals(key.getCommunicationState())) {
                    PhnTpOpUtil call = new PhnTpOpUtil(key, value, true);
                    calls.add(call);
                } else {
                    log.error("ne unreachable: " + key.getNeName());
                }
            });

            service = Executors.newFixedThreadPool(5);

            List<Future<List<OpticalPower>>> futures = service.invokeAll(calls);

            for(Future<List<OpticalPower>> future : futures) {
                try {
                    List<OpticalPower> beans = future.get();
                    if (beans != null) {
                        beans.forEach(bean->{
                            TpOp oo = new TpOp();
                            oo.setNeId(bean.getNe().getNeId());
                            oo.setNeName(bean.getNe().getNeName());
                            oo.setTpId(bean.getTp().getTpId());
                            oo.setTpName(bean.getTp().getTpName());
                            oo.setOpr(bean.getOpr());
                            oo.setOpt(bean.getOpt());
                            ops.add(oo);
                        });
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    continue;
                }
            }

            calls.clear();
            service.shutdown();
            return ops;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException( 500, e.getMessage());
        } finally {
            if(service != null) {
                if(service.isShutdown()) {
                    //service.awaitTermination(2000, TimeUnit.MILLISECONDS);
                    service.shutdownNow();
                }
            }
        }
    }
}
