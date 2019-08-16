package com.hcop.ptn.alarm.rootcause.servicesource;

import com.hcop.ptn.alarm.rootcause.beans.TpBean;
import com.hcop.ptn.common.utils.CollectionUtils;
import com.hcop.ptn.restful.model.ConnectionType;

import java.util.List;

public class EvcSource extends ServiceSource {

    private static EvcSource ourInstance = new EvcSource();

    public static EvcSource getInstance() {
        return ourInstance;
    }

    private EvcSource() {
        super(ConnectionType.EVC);
    }

    @Override
    protected boolean isValid(List<String> rInfo, TpBean tp) {
        if (CollectionUtils.isEmpty(rInfo)) {
            return true;
        }

        for(int i = 0; i < rInfo.size(); i++) {
            if (rInfo.get(i).contains(tp.getTpName())) {
                return true;
            }
        }

        return false;
    }
}
