/**
 * FileName: AlarmAnalyzeImpl
 * Author:   Administrator
 * Date:     2019/3/31 16:56
 * Description:
 */
package com.hcop.ptn.alarm.event;

import com.hcop.ptn.alarm.rootcause.AlarmAnalyzer;
import com.hcop.ptn.alarm.rootcause.common.RootCauseAlarmHelper;
import com.hcop.ptn.common.db.dao.AlarmDao;
import com.hcop.ptn.restful.model.Alarm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AlarmAnalyzeImpl implements AlarmSynchronizeListener {
    private static Log log = LogFactory.getLog(AlarmAnalyzeImpl.class);
    private AtomicBoolean isFinished = new AtomicBoolean(false);

    private static AlarmAnalyzeImpl instance;

    public static AlarmAnalyzeImpl getInstance() {
        if (instance == null) {
            instance = new AlarmAnalyzeImpl();
        }
        return instance;
    }

    private AlarmAnalyzeImpl() {}

    @Override
    public void alarmNotify() {

    }

    @Override
    public void alarmSynOver() {
        isFinished.set(false);
        RootCauseAlarmHelper.getInstance().clearAll();

        //获取所有可能的告警
        List<Alarm> definedAlarms = AlarmDao.instance().getAllDefinedAlarms();
        RootCauseAlarmHelper.getInstance().addAlarms(definedAlarms);

        for (Alarm alarm : definedAlarms) {
            try {
                List <Alarm> rcs = AlarmAnalyzer.analyze(alarm);
                RootCauseAlarmHelper.getInstance().setRootCause(rcs, true);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                continue;
            }
        }

        isFinished.set(true);
    }

    public boolean isFinished() {
        return isFinished.get();
    }

    public static void main(String[] args) {
        AlarmAnalyzeImpl.getInstance().alarmSynOver();
    }
}
