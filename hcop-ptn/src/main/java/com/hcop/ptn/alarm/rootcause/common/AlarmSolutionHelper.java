package com.hcop.ptn.alarm.rootcause.common;

import com.hcop.ptn.common.properties.AlarmSolutionConf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class AlarmSolutionHelper {
    private static Log log = LogFactory.getLog(AlarmSolutionHelper.class);
    private static Map<String, Set<String>> alarmSolutionMap;

    private static Map<String, String> alarmSolutionPcIdMap;
    private static AlarmSolutionHelper ourInstance = new AlarmSolutionHelper();

    public static AlarmSolutionHelper getInstance() {
        return ourInstance;
    }

    private AlarmSolutionHelper() {
        init();
    }

    private void init() {
        alarmSolutionMap = new HashMap<>();
        alarmSolutionPcIdMap = AlarmSolutionConf.instance().getPropertiesMap();
        for (Map.Entry<String,String> entry : alarmSolutionPcIdMap.entrySet()) {
            String solutionStr = entry.getValue();

            String[] solutions = solutionStr.split("\\|\\|\\|");

            Set<String> rootCauseSet = new HashSet<>();
            rootCauseSet.addAll(Arrays.asList(solutions));

            if (rootCauseSet.size() <= 0) {
                log.error("empty root cause pc id for alarm abbreviation: " + entry.getKey());
            } else {
                alarmSolutionMap.put(entry.getKey(), rootCauseSet);
            }
        }
    }

    public Set<String> getAlarmSolution(String pcId) {
        return alarmSolutionMap.get(pcId.trim());
    }
}
