package com.hcop.ptn.alarm;

import com.hcop.ptn.common.properties.AlarmProbableConf;

import java.util.Map;

public class AlarmZhName {
    public static void main(String[] args) {
        Map<String,String> ttt = AlarmProbableConf.instance().getPropertiesMap();
        for (Map.Entry <String, String> en : ttt.entrySet()) {
            if(!en.getValue().contains("(") && !en.getValue().contains(")")) {
                System.out.println(en.getKey()+"="+en.getValue());
            }
        }
    }
}
