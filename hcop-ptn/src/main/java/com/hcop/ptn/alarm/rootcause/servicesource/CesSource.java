package com.hcop.ptn.alarm.rootcause.servicesource;

import com.hcop.ptn.alarm.rootcause.beans.TpBean;
import com.hcop.ptn.common.utils.CollectionUtils;
import com.hcop.ptn.restful.model.ConnectionType;

import java.util.List;

public class CesSource extends ServiceSource {
    private static CesSource ourInstance = new CesSource();

    public static CesSource getInstance() {
        return ourInstance;
    }

    private CesSource() {
        super(ConnectionType.CES);
    }

    @Override
    protected boolean isValid(List<String> rInfo, TpBean tp) {
        if (CollectionUtils.isEmpty(rInfo)) {
            return true;
        }

        String[] parts = tp.getTpName().split("-");
        String s ="";
        for (int i = 1; i < parts.length; i++) {
            s += ("-" + parts[i]);
            if (i == 4) {
                break;
            }
        }

        for(int i = 0; i < rInfo.size(); i++) {
            if (rInfo.get(i).contains(s)) {
                return true;
            }
        }

        return false;
    }
}
