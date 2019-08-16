package com.hcop.ptn.alarm.rootcause.servicesource;

import com.hcop.ptn.alarm.rootcause.beans.TpBean;
import com.hcop.ptn.common.utils.CollectionUtils;
import com.hcop.ptn.restful.model.ConnectionType;

import java.util.List;

public class SectionSource extends ServiceSource {

    private static SectionSource ourInstance = new SectionSource();

    public static SectionSource getInstance() {
        return ourInstance;
    }

    private SectionSource() {
        super(ConnectionType.SECTION);
    }

    @Override
    protected boolean isValid(List <String> rInfo, TpBean tp) {
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
