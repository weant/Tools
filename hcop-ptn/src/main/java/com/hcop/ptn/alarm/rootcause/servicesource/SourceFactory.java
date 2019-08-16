package com.hcop.ptn.alarm.rootcause.servicesource;

import com.hcop.ptn.alarm.rootcause.beans.ConnectionBean;
import com.hcop.ptn.alarm.rootcause.common.AlarmCategoryEnum;

import java.util.List;

public class SourceFactory {
    private static SourceFactory ourInstance = new SourceFactory();

    public static SourceFactory getInstance() {
        return ourInstance;
    }

    private SourceFactory() {
    }

    public List<ConnectionBean> getService(List<String> rInfo, AlarmCategoryEnum category, String neName) {
        switch (category) {
            case SECTION:
                return SectionSource.getInstance().getServiceByNeName(neName, rInfo);
            case TUNNEL:
                return TunnelSource.getInstance().getServiceByNeName(neName, rInfo);
            case PW:
                return PwSource.getInstance().getServiceByNeName(neName, rInfo);
            case EVC:
                return EvcSource.getInstance().getServiceByNeName(neName, rInfo);
            case CES:
                return CesSource.getInstance().getServiceByNeName(neName, rInfo);
            default:
                return null;
        }
    }
}
