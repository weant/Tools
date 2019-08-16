/**
 * FileName: AlarmHelper
 * Author:   Administrator
 * Date:     2019/3/31 17:16
 * Description:
 */
package com.hcop.ptn.alarm;

import com.hcop.ptn.common.properties.AlarmCategoryConf;
import com.hcop.ptn.common.properties.AlarmPCAbbreConf;
import com.hcop.ptn.common.properties.RootCauseConf;
import com.hcop.ptn.common.utils.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

public class AlarmHelper {
    private static Set<String> definedAlarms = new HashSet <>();

    public static Set<String> getAllDefinedAlarms() {
        if(CollectionUtils.isEmpty(definedAlarms)) {
            initData();
        }

        return definedAlarms;
    }

    private static void initData(){
        definedAlarms.addAll(AlarmCategoryConf.instance().getPropertiesMap().keySet());
        definedAlarms.addAll(AlarmPCAbbreConf.instance().getPropertiesMap().keySet());
        definedAlarms.addAll(RootCauseConf.instance().getPropertiesMap().keySet());
    }
}
