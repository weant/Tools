/**
 * FileName: RootCausePcIdHelper
 * Author:   Administrator
 * Date:     2019/3/30 20:53
 * Description:
 */
package com.hcop.ptn.alarm.rootcause.common;

import com.hcop.ptn.common.properties.AlarmPCAbbreConf;
import com.hcop.ptn.common.properties.RootCauseConf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RootCausePcIdHelper {
    private static Log log = LogFactory.getLog(RootCausePcIdHelper.class);
    private static Map<String, String> pcAbbreMap = AlarmPCAbbreConf.instance().getPropertiesMap();
    private static Map<String, String> rootCausePcIdMap = RootCauseConf.instance().getPropertiesMap();
    private Map<String, Set<String>> rootCauseMap;

    private static RootCausePcIdHelper instance;

    public static RootCausePcIdHelper getInstance() {
        if (instance == null) {
            instance = new RootCausePcIdHelper();
        }

        return instance;
    }

    private RootCausePcIdHelper() {
        init();
    }

    private void init() {

        rootCauseMap = new HashMap<>();

        for (Map.Entry<String,String> entry : rootCausePcIdMap.entrySet()) {

            String rootCauseStr = entry.getValue();
            if ("NONE".equalsIgnoreCase(rootCauseStr.trim())) {
                log.error("cannot find root cause, configurated root cause is NONE: " + entry.getKey());
                continue;
            }

            String[] rootCauses = rootCauseStr.split(",");

            Set<String> rootCauseSet = new HashSet<>();
            for (int i = 0; i < rootCauses.length; i++) {
                if (!isContained(rootCauses[i].trim())) {
                    log.error("cannot find pc id for alarm abbreviation:" + rootCauses[i]);
                } else {
                    for (Map.Entry<String, String> pcEntry : pcAbbreMap.entrySet()) {
                        if (pcEntry.getValue().trim().equalsIgnoreCase(rootCauses[i].trim())) {
                            rootCauseSet.add(pcEntry.getKey().trim());
                            //break;
                        }
                    }
                }
            }

            if (rootCauseSet.size() <= 0) {
                log.error("empty root cause pc id for alarm abbreviation: " + rootCauseStr);
            } else {
                rootCauseMap.put(entry.getKey(), rootCauseSet);
            }
        }
    }

    private boolean isContained(String rx) {
        return pcAbbreMap.values().stream().anyMatch((a)->a.equalsIgnoreCase(rx));
    }

    public Set<String> getRootCauseSet(String pcId) {
        return rootCauseMap.get(pcId.trim());
    }
}
