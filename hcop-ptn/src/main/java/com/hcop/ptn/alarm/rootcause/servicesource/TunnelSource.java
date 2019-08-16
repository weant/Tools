package com.hcop.ptn.alarm.rootcause.servicesource;

import com.hcop.ptn.alarm.rootcause.beans.TpBean;
import com.hcop.ptn.common.utils.CollectionUtils;
import com.hcop.ptn.restful.model.ConnectionType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class TunnelSource extends ServiceSource  {
    private static Log log = LogFactory.getLog(TunnelSource.class);

    private static TunnelSource ourInstance = new TunnelSource();

    public static TunnelSource getInstance() {
        return ourInstance;
    }

    private TunnelSource() {
        super(ConnectionType.TUNNEL);
    }

    @Override
    protected boolean isValid(List<String> rInfo, TpBean tp) {
        if (CollectionUtils.isEmpty(rInfo)) {
            return true;
        }

        String aid = tp.getExtendedFields().get("aid") == null ? "" : tp.getExtendedFields().get("aid").toString();
        //log.error("+++++++++++++Tunnel AID:" + aid + ", ne name:" + tp.getNeName() + ", tp name:" + tp.getTpName());
        for(int i = 0; i < rInfo.size(); i++) {
            if (rInfo.get(i).trim().equals(aid)
                    || rInfo.get(i).trim().equals(tp.getTpName())) {
                return true;
            }

            // TODO: 2019/4/2 MEP#1#1#1无法处理，不清楚其意义
        }

        return false;
    }
}
