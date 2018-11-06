package com.otn.tool.common.utils;

import com.otn.tool.common.beans.Ne;
import com.otn.tool.common.beans.OpticalPower;
import com.otn.tool.common.beans.Tp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

public class PhnTpOpUtil implements Callable<List<OpticalPower>> {
    private Log log = LogFactory.getLog(PhnTpOpUtil.class);
    private Ne ne;
    private Collection<Tp> tps;
    //是否返回未找到光功率的端口
    private boolean flag;

    public PhnTpOpUtil(Ne ne, Collection<Tp> tps, boolean flag) {
        this.ne = ne;
        this.tps = tps;
        this.flag = flag;
    }

    @Override
    public List<OpticalPower> call() throws Exception {
        log.info("Begin to get optical power from:" + ne.getName());
        log.info("tp count:" + tps.size());
        return getOp();
    }

    private List<OpticalPower> getOp() {
        List<OpticalPower> opList = new ArrayList<>();
        if(CollectionUtils.isEmpty(opList)) {
            return opList;
        }


    }
}
