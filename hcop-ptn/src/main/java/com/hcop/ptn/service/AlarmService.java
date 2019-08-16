package com.hcop.ptn.service;

import com.hcop.ptn.alarm.rootcause.AlarmAnalyzer;
import com.hcop.ptn.alarm.rootcause.common.AlarmSolutionHelper;
import com.hcop.ptn.alarm.rootcause.common.RootCauseAlarmHelper;
import com.hcop.ptn.alarm.event.AlarmAnalyzeImpl;
import com.hcop.ptn.alarm.event.AlarmSynchronizeObserver;
import com.hcop.ptn.common.db.dao.AlarmDao;
import com.hcop.ptn.common.exception.ServiceException;
import com.hcop.ptn.restful.model.Alarm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;

public class AlarmService {
    private Log log = LogFactory.getLog(AlarmService.class);
    private static AlarmService ourInstance = new AlarmService();

    public static AlarmService getInstance() {
        return ourInstance;
    }

    private AlarmService() {
    }

    public Collection<Alarm> getAllAlarms() throws ServiceException {
        if (!AlarmSynchronizeObserver.instance().isSynOver()) {
            log.error("Alarm synchronization not finished.");
            throw new ServiceException(500, "Alarm synchronization not finished.");
        }
        try {
            return AlarmDao.instance().getAllAlarms();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }
    }

    public Collection<String> getSolutionByPcId(String pcId) throws ServiceException {
        try {
            return AlarmSolutionHelper.getInstance().getAlarmSolution(pcId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }
    }

    public Collection<Alarm> getAllRootCauses() throws ServiceException {
        try {
            if (!AlarmAnalyzeImpl.getInstance().isFinished()) {
                throw new ServiceException(500, "Root Cause Analysis not finished.");
            }

            return RootCauseAlarmHelper.getInstance().getAllRootCause();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }
    }

    public Collection<Alarm> getRCAlarmsByAlarmIdAndServer(String alarmId, String server) throws ServiceException {
        try {
            if (!AlarmSynchronizeObserver.instance().isSynOver()) {
                throw new ServiceException(500, "Alarm Synchronization not finished.");
            }

            Alarm alarm = RootCauseAlarmHelper.getInstance().getAlarm(alarmId, server);

            if (alarm == null) {
                throw new ServiceException(500, "Cannot find this alarm.");
            }

            return AlarmAnalyzer.analyze(alarm);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(500, e.getMessage());
        }
    }
}
