package com.hcop.ptn.alarm.rootcause.servicesource;

import com.hcop.ptn.alarm.rootcause.beans.TpBean;
import com.hcop.ptn.common.utils.CollectionUtils;
import com.hcop.ptn.restful.model.ConnectionType;

import java.util.List;

public class PwSource extends ServiceSource {

    private static PwSource ourInstance = new PwSource();

    public static PwSource getInstance() {
        return ourInstance;
    }

    private PwSource() {
        super(ConnectionType.PW);
    }

    @Override
    protected boolean isValid(List<String> rInfo, TpBean tp) {
        if (CollectionUtils.isEmpty(rInfo)) {
            return true;
        }

        String segment = tp.getExtendedFields().get("segaid") == null ? "" : tp.getExtendedFields().get("segaid").toString();
        for(int i = 0; i < rInfo.size(); i++) {
            if (rInfo.get(i).trim().equals(segment)) {
                return true;
            }
            if (rInfo.get(i).trim().equals(tp.getTpName())) {
                return true;
            }
        }

        return false;
    }
}
