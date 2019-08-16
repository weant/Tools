package com.hcop.ptn.alarm.rootcause.servicesource;

import com.hcop.ptn.alarm.rootcause.beans.ConnectionBean;
import com.hcop.ptn.alarm.rootcause.beans.TpBean;
import com.hcop.ptn.common.utils.CollectionUtils;
import com.hcop.ptn.restful.model.ConnectionType;
import com.hcop.ptn.service.ConnectionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.StampedLock;

public abstract class ServiceSource {
    private static final Log log = LogFactory.getLog(ServiceSource.class);
    private AtomicLong lastQueryTime = new AtomicLong(System.currentTimeMillis());
    private static final long TIME_OUT = 10 * 1000;
    private Integer lock  = 1;
    private ConnectionType type;
    protected List<ConnectionBean> service;

    public ServiceSource(ConnectionType type) {
        this.type = type;
        queryAllService();
    }

    private void queryAllService() {
        synchronized (lock) {
            try {
                log.info("begin to query connection data for: " + type.toString());
                service = ConnectionService.getInstance().getConnectionBeansByType(type, "");
                lastQueryTime.set(System.currentTimeMillis());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public List<ConnectionBean> getServiceByNeName(String neName, List<String> rInfo) {
        synchronized (lock) {
            if (CollectionUtils.isEmpty(service)) {
                queryAllService();
            }
        }

        List<ConnectionBean> list = getServices(neName, rInfo);

        if (!CollectionUtils.isEmpty(list)) {
            return list;
        } else {
            synchronized (lock) {
                if ((System.currentTimeMillis() - lastQueryTime.get()) > TIME_OUT) {
                    queryAllService();
                } else {
                    return list;
                }
            }
        }

        return getServices(neName, rInfo);

    }

    private List<ConnectionBean> getServices(String neName, List<String> rInfo) {
        List<ConnectionBean> list = new ArrayList<>();
        synchronized (lock) {
            for (ConnectionBean con : service) {
                for (TpBean tp : con.getTps()) {

                    if (tp.getNeName().trim().equalsIgnoreCase(neName.trim())) {

                        if (isValid(rInfo, tp)) {
                            list.add(con);
                            break;
                        }
                    }

                }
            }

            return list;
        }
    }

    protected abstract boolean isValid(List<String> rInfo, TpBean tp);
}
