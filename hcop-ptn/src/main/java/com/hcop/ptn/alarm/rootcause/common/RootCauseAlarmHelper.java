/**
 * FileName: RootCauseAlarmHelper
 * Author:   Administrator
 * Date:     2019/3/31 15:46
 * Description:
 */
package com.hcop.ptn.alarm.rootcause.common;

import com.hcop.ptn.common.utils.CollectionUtils;
import com.hcop.ptn.restful.model.Alarm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RootCauseAlarmHelper {
    private static Log log = LogFactory.getLog(RootCauseAlarmHelper.class);
    private Map<Alarm, AlarmRelatedInfo> rootCausesMap = new HashMap<>();

    private static RootCauseAlarmHelper instance;

    private RootCauseAlarmHelper() {}

    public static RootCauseAlarmHelper getInstance() {
        if (instance == null) {
            instance = new RootCauseAlarmHelper();
        }

        return instance;
    }

    public synchronized List<Alarm> getAllRootCause() {
        if (rootCausesMap.isEmpty()) {
            return Collections.emptyList();
        }

        List<Alarm> rcs = new ArrayList <>();
        for (Alarm alarm : rootCausesMap.keySet()) {
            if (alarm.isIsRootCause()) {
                rcs.add(alarm);
            }
        }

        return rcs;
    }

    public synchronized AlarmRelatedInfo getAlarmRelatedInfo(Alarm alarm) {
        if (rootCausesMap.isEmpty()) {
            return null;
        }

        Iterator<Map.Entry<Alarm, AlarmRelatedInfo>> iterator = rootCausesMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Alarm, AlarmRelatedInfo> entry = iterator.next();
            if (entry.getKey().getFriendlyName().equals(alarm.getFriendlyName())
                    && entry.getKey().getProbableCauseId().equals(alarm.getProbableCauseId())) {

                return entry.getValue();
            }
        }

        return null;
    }

    public synchronized Alarm getAlarm(String alarmId, String server) {
        if (rootCausesMap.isEmpty()) {
            return null;
        }

        for (Alarm alarm : rootCausesMap.keySet()) {
            if (alarm.getAlarmId().trim().equals(alarmId.trim())
                    && alarm.getServer().toUpperCase().trim().equals(server.toUpperCase().trim())) {
                return alarm;
            }
        }

        return null;
    }

    public synchronized List<Alarm> getAlarmsByNeNamesAndPcIds(Collection<String> neNames, Collection<String> pcIds) {
        if (rootCausesMap.isEmpty()) {
            log.error("+++rootCausesMap is empty");
            return null;
        }

        if (CollectionUtils.isEmpty(neNames)) {
            log.error("+++neNames is empty");
            return null;
        }
        if (CollectionUtils.isEmpty(pcIds)) {
            log.error("+++pcIds is empty");
            return null;
        }

        List<Alarm> alarms = new ArrayList <>();

        for (Alarm alarm : rootCausesMap.keySet()) {
            /*String header;
            String neName;
            if (alarm.getFriendlyName().contains(",")) {
                header = alarm.getFriendlyName().split(",")[0];
            } else {
                header = alarm.getFriendlyName();
            }

            if (header.contains("/")) {
                neName = header.substring(0, header.indexOf("/"));
            } else {
                neName = header;
            }

            if (pcIds.contains(alarm.getProbableCauseId()) && neNames.contains(neName)) {
                alarms.add(alarm);
            }*/

           boolean flag = false;
           for (String neName : neNames) {
               if (alarm.getFriendlyName().contains(neName)) {
                   flag = true;
               }
            }

            if (flag && pcIds.contains(alarm.getProbableCauseId())) {
                alarms.add(alarm);
            }
        }

        return alarms;
    }

    public synchronized void setRootCause(List<Alarm> alarms, boolean isRootCause) {
        if (CollectionUtils.isEmpty(alarms)) {
            return;
        }

        for (Alarm alarm : alarms) {
            Iterator<Map.Entry<Alarm, AlarmRelatedInfo>> iterator = rootCausesMap.entrySet().iterator();
            boolean flag = false;
            while (iterator.hasNext()) {
                Map.Entry<Alarm, AlarmRelatedInfo> entry = iterator.next();
                if (entry.getKey().getFriendlyName().equals(alarm.getFriendlyName())
                        && entry.getKey().getProbableCauseId().equals(alarm.getProbableCauseId())) {
                    entry.getKey().setIsRootCause(isRootCause);

                    flag = true;
                    //break;
                }
            }

            if (!flag) {
                log.error("Not found alarm in rootCause Pool: " + (alarm.getFriendlyName() == null ? "???" : alarm.getFriendlyName()) + " || " + (alarm.getProbableCause() == null ? "???" : alarm.getProbableCause()));
            }
        }
    }

    public synchronized void addAlarms(List<Alarm> alarms) {
        for(Alarm alarm : alarms) {
            try {
                AlarmRelatedInfo info = AlarmRelatedInfoHelper.getAlarmRelatedInfo(alarm);
                if (info == null) {
                    log.error("cannot find alarm related info.");
                }
                rootCausesMap.put(alarm, info);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public synchronized void addAlarm(Alarm alarm) throws Exception {
        AlarmRelatedInfo info = AlarmRelatedInfoHelper.getAlarmRelatedInfo(alarm);
        if (info == null) {
            log.error("cannot find alarm related info.");
        }
        rootCausesMap.put(alarm, info);
    }

    public synchronized void clearAll() {
        rootCausesMap.clear();
    }

    public synchronized void removeAlarm(Alarm alarm) {

        if (alarm == null) {
            return;
        }

        //rootCausesMap.computeIfPresent(alarm, (key, value)-> null);

        Iterator<Map.Entry<Alarm, AlarmRelatedInfo>> iterator = rootCausesMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Alarm, AlarmRelatedInfo> entry = iterator.next();
            if (alarm.getFriendlyName().equals(entry.getKey().getFriendlyName())
                    && alarm.getProbableCauseId().equals(entry.getKey().getProbableCauseId())) {
                iterator.remove();
            }
        }
    }

    public synchronized void removeAlarms(List<Alarm> alarms) {

        if (CollectionUtils.isEmpty(alarms)) {
            return;
        }

        //alarms.forEach(alarm->rootCausesMap.computeIfPresent(alarm, (key, value)-> null));

        List<String> strs = new ArrayList<>();
        alarms.forEach(alarm->strs.add(alarm.getFriendlyName()+alarm.getProbableCauseId()));

        Iterator<Map.Entry<Alarm, AlarmRelatedInfo>> iterator = rootCausesMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Alarm, AlarmRelatedInfo> entry = iterator.next();
            if (strs.contains(entry.getKey().getFriendlyName()+entry.getKey().getProbableCauseId())) {
                iterator.remove();
            }
        }
    }

}
