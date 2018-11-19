package com.otn.tool.common.utils;

import com.otn.tool.common.properties.Conf;
import com.otn.tool.common.properties.ZHI18N;

import java.util.HashMap;
import java.util.Map;

public class I18N {
    private String language;
    private static final String DEFAULT_LANGUAGE = "zh";

    private Map<String, String> languageMap = new HashMap<>();

    public static I18N getInstance() {
        return ourInstance;
    }

    private static I18N ourInstance = new I18N();

    private I18N() {
        init();
    }

    public void reload(){

    }

    private void init(){
        Map<String, String> conf = Conf.instance().getPropertiesMap();
        language = conf.get("tool.language");
        if(language == null || language.length() <=0 ) {
            language = DEFAULT_LANGUAGE;
        }
        if(language.equalsIgnoreCase("zh")) {
            languageMap = ZHI18N.instance().getPropertiesMap();
        }
    }

    public String getString(String string) {
        if(languageMap.containsKey(string) && languageMap.get(string).length() > 0) {
            return languageMap.get(string);
        }

        return string;
    }
}
