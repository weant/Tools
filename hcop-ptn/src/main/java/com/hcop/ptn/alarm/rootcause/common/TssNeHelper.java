package com.hcop.ptn.alarm.rootcause.common;

import com.hcop.ptn.common.utils.CollectionUtils;
import com.hcop.ptn.restful.model.NE;
import com.hcop.ptn.service.NeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

public class TssNeHelper {
    private Log log = LogFactory.getLog(TssNeHelper.class);
    private AtomicLong lastQueryTime = new AtomicLong(System.currentTimeMillis());
    private static final long TIME_OUT = 10 * 1000;
    private Integer lock = 1;
    private Collection <NE> nes;
    private static TssNeHelper ourInstance = new TssNeHelper();

    public static TssNeHelper getInstance() {
        return ourInstance;
    }

    private TssNeHelper() {
    }

    private void initNes() {
        try {
            synchronized (lock) {
                log.info("begin to init nes in memory.");
                nes = NeService.getInstance().getAllNes();
                lastQueryTime.set(System.currentTimeMillis());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public boolean isTssNeAlarm(String alarmFriendlyName) {
        synchronized (lock) {
            if (CollectionUtils.isEmpty(nes)) {
                initNes();
            }
        }

        boolean flag = isTrue(alarmFriendlyName);

        if (flag) {
            return true;
        } else {
            synchronized (lock) {
                if ((System.currentTimeMillis() - lastQueryTime.get()) > TIME_OUT) {
                    initNes();
                } else {
                    return false;
                }
            }
        }

        return isTrue(alarmFriendlyName);
    }

    private boolean isTrue(String alarmFriendlyName) {
        synchronized (lock) {
            for (NE ne : nes) {
                if (alarmFriendlyName.contains(ne.getNeName())) {
                    return true;
                }
            }

            return false;
        }
    }
}
