/**
 * FileName: AlarmAnalyzer
 * Author:   Administrator
 * Date:     2019/3/30 21:32
 * Description:
 */
package com.hcop.ptn.alarm.rootcause;

import com.hcop.ptn.alarm.rootcause.beans.ConnectionBean;
import com.hcop.ptn.alarm.rootcause.beans.TpBean;
import com.hcop.ptn.alarm.rootcause.common.*;
import com.hcop.ptn.alarm.rootcause.servicesource.SourceFactory;
import com.hcop.ptn.common.utils.CollectionUtils;
import com.hcop.ptn.restful.model.Alarm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class AlarmAnalyzer {
    private static Log log = LogFactory.getLog(AlarmAnalyzer.class);

    public static List<Alarm> analyze(Alarm alarm) throws Exception {
        if (alarm == null) {
            log.error("+++++++++++++alarm is null...");
            return Collections.emptyList();
        }

        AlarmRelatedInfo info = AlarmHeadPartHelper.getAlarmRelatedInfo(alarm);


        if (info == null) {
            log.error("+++++++++++++alarm related info is null...");
            return Collections.emptyList();
        }

        if (info.getRcPcIds().size() == 1) {
            for(String s : info.getRcPcIds()) {
                if (s.equals(alarm.getProbableCauseId())) {
                    log.error("+++++++++++++itself as the root cause: " + alarm.getFriendlyName() + " || " + alarm.getProbableCause());
                    return Collections.singletonList(alarm);
                }
            }
        }

        if (!info.getServiceEnum().equals(AlarmCategoryEnum.UNKNOWN)) {
            //return null;
            // 2019/3/30 根据网元和相关信息获取连
            List<ConnectionBean> services = SourceFactory.getInstance().getService(info.getRelatedInfo(), info.getServiceEnum(), info.getNeName());

            if (CollectionUtils.isEmpty(services)) {
                //log.error("+++++++++++++peer:::empty service list, connection type:" + info.getServiceEnum().toString() + ",  neName:" + info.getNeName());
                for (String info1 : info.getRelatedInfo()) {
                    log.error("+++++++++++++peer:::empty service list, connection type:" + info.getServiceEnum().toString() + ",  neName:" + info.getNeName() + ", info:" + info1);
                }
                return null;
            }

            List<TpBean> tps = new ArrayList<>();
            Set<String> neNames = new HashSet<>();

            for (ConnectionBean con : services) {
                log.error("+++++++++++++peer:::connection type:" + con.getConnType().toString() + ", name:"+ con.getConnName());
                for (TpBean tp : con.getTps()) {
                    if (!tp.getNeName().equals(info.getNeName())) {
                        tps.add(tp);
                        neNames.add(tp.getNeName());
                    }
                }
            }

            if (CollectionUtils.isEmpty(neNames) || CollectionUtils.isEmpty(info.getRcPcIds())) {
                log.error("+++++++++++++peer:::empty ne or probable cause id");
                return null;
            }

            List<Alarm> probableRCAlarms = RootCauseAlarmHelper.getInstance().getAlarmsByNeNamesAndPcIds(neNames, info.getRcPcIds());

            if (CollectionUtils.isEmpty(probableRCAlarms)) {
                log.error("+++++++++++++peer:::can't find probable alarms by the ne name and pc id: " + alarm.getFriendlyName() + " || " + alarm.getProbableCauseId());
                return null;
            }

            List<Alarm> alarms = PeerServiceAlarmMatcher.getInstance().getMatchedAlarms(tps, probableRCAlarms, info.getServiceEnum());
            log.error("+++++++++++++peer:::root cause alarm num:" + (alarms == null ? "null" : alarms.size())
                    + ",  for alarm:" + alarm.getFriendlyName() + ", pcid:" + alarm.getProbableCauseId());
            return alarms;
        } else {
            Set<String> neNames = new HashSet<>();
            neNames.add(info.getNeName());
            if (CollectionUtils.isEmpty(info.getRcPcIds())) {
                log.error("+++++++++++++self:::empty probable cause id");
                return null;
            }

            List<Alarm> probableRCAlarms = RootCauseAlarmHelper.getInstance().getAlarmsByNeNamesAndPcIds(neNames, info.getRcPcIds());
            if (CollectionUtils.isEmpty(probableRCAlarms)) {
                log.error("+++++++++++++self:::can't find probable alarms by the ne name and pc id: " + alarm.getFriendlyName() + " || " + alarm.getProbableCauseId());
                return null;
            }

            List<Alarm> alarms =  SelfAlarmMatcher.getInstance().getMatchedAlarms(alarm, info, probableRCAlarms);
            log.error("+++++++++++++self:::root cause alarm num:" + (alarms == null ? "null" : alarms.size())
                    + ",  for alarm:" + alarm.getFriendlyName() + ", pcid:" + alarm.getProbableCauseId());
            return alarms;
        }

    }

}
