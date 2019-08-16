package com.hcop.otn.common.obtainop;

import com.hcop.otn.common.beans.InternalNe;
import com.hcop.otn.common.beans.OpticalPower;
import com.hcop.otn.common.beans.InternalTp;
import com.hcop.otn.common.internal.xos.request.GetCurrentPMPRequest;
import com.hcop.otn.common.internal.xos.response.extractors.GetOpPowerExtractor;
import com.hcop.otn.common.internal.xos.session.SessionManager;
import com.hcop.otn.common.internal.xos.util.XosException;
import com.hcop.otn.common.internal.xos.util.XosOperation;
import com.hcop.otn.common.utils.CollectionUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhnTpOpUtil implements Callable<List<OpticalPower>> {
    private Logger log = LogManager.getLogger(PhnTpOpUtil.class);
    private InternalNe ne;
    private Collection<InternalTp> tps;
    //是否返回未找到光功率的端口
    private boolean flag;
    private static Pattern p = Pattern.compile("-");

    public PhnTpOpUtil(InternalNe ne, Collection<InternalTp> tps, boolean flag) {
        this.ne = ne;
        this.tps = tps;
        this.flag = flag;
    }

    @Override
    public List<OpticalPower> call() throws Exception {
        log.info("Begin to get optical power from:" + ne.getNeName());
        log.info("tp count:" + tps.size());
        return getOp();
    }

    private List<OpticalPower> getOp() {
        List<OpticalPower> opList = new ArrayList<>();
        if(CollectionUtils.isEmpty(tps)) {
            return opList;
        }
        for(InternalTp tp : tps) {
            if(tp.getTpName().matches(".*OSC(\\w*)?$")) {
                try {
                    opList.add(XosOpPmpUtil.getOPByTp(ne.getGroupId(), ne.getEmlId(), ne, tp));
                } catch(Exception e) {
                    e.printStackTrace();
                    continue;
                } finally {
                }

                continue;
            }

            GetCurrentPMPRequest request = constructRequest(tp);
            DynaBean powerBean = null;
            try {
                powerBean = XosOperation.getResult(ne.getGroupId(), request,
                        new GetOpPowerExtractor());
            } catch (XosException e) {
                log.error("Error occured when retrive data from pmp:"
                        + tp.getTpName() + "; and the exception is:"
                        + e);
                if(flag) {
                    notFountOP(tp,opList);
                }
                continue;
            }

            if ((powerBean.get("rx") == null) && (powerBean.get("tx") == null)) {
                if(flag) {
                    notFountOP(tp,opList);
                }
                continue;
            }

            OpticalPower bean = new OpticalPower();
            bean.setNe(ne);
            bean.setTp(tp);
            bean.setOpt("--");
            bean.setOpr("--");

            if (powerBean.get("rx") != null) {
                bean.setOpr(getResult((String) powerBean.get("rx")));
                log.debug(bean.getNe().getNeName() + "/" + bean.getTp().getTpName() + "  rx:" + bean.getOpr());
            }
            if (powerBean.get("tx") != null) {
                bean.setOpt(getResult((String) powerBean.get("tx")));
                log.debug(bean.getNe().getNeName() + "/" + bean.getTp().getTpName() + "  tx:" + bean.getOpt());
            }
            opList.add(bean);
        }
        if (opList.isEmpty()) {
            log.warn("Can't read optical power from ne:" + ne.getNeName());
        }

        log.info("End to get optical power from ne:" + ne.getNeName());
        return opList;
    }

    private String getResult(String value) {
        if (value.matches("^[-+]?\\d+(\\.\\d+)?$")) {
            float temp = Float.valueOf(value);
            if(temp < -40F) {
                return "--";
            }
        }
        if(value.equals("NA")) {
            return "--";
        }
        return value;
    }

    private GetCurrentPMPRequest constructRequest(InternalTp tp) {
        String ncName = "EML_" + String.valueOf(ne.getGroupId()) + "_SNA";
        String neName = String.valueOf(ne.getEmlId());
        String ptpName = handleTpName(tp.getTpName());
        String ftpName = "";
        String ctpName = "";
        GetCurrentPMPRequest request = new GetCurrentPMPRequest(
                ncName, neName, ptpName, ftpName, ctpName);
        return request;
    }
    private void notFountOP(InternalTp tp, List<OpticalPower> beansList) {
        OpticalPower bean = new OpticalPower();
        bean.setNe(ne);
        bean.setTp(tp);
        bean.setOpt("--");
        bean.setOpr("--");
        beansList.add(bean);
    }

    private String handleTpName(String tpName) {
        Matcher m = p.matcher(tpName);
        int index = 0,time = 0;
        while(m.find()) {
            index = m.start();
            ++ time;
            if(time > 3) {
                return tpName.substring(0, index);
            }
        }
        return tpName;
    }

    public static void main(String[] args) {
        SessionManager.getInstance().init();
        InternalNe ne = new InternalNe();
        ne.setNeName("WGPHN-3");
        ne.setCommunicationState("0");
        ne.setEmlId(22);
        ne.setGroupId(100);
        ne.setNeId("35");
        ne.setNeIp("10.10.10.114");


        InternalTp tp = new InternalTp();
        tp.setEquipName("30AN300");
        tp.setTpId("37");
        tp.setNeId(ne.getNeId());
        tp.setTpName("30AN300-1-6-2");

        InternalTp tp1 = new InternalTp();
        tp1.setEquipName("30AN300");
        tp1.setTpId("10");
        tp1.setNeId(ne.getNeId());
        tp1.setTpName("30AN300-1-6-1");

        InternalTp tp2 = new InternalTp();
        tp2.setEquipName("4UC400");
        tp2.setTpId("42");
        tp2.setNeId(ne.getNeId());
        tp2.setTpName("4UC400-1-21-1");

        InternalTp tp3 = new InternalTp();
        tp3.setEquipName("4UC400");
        tp3.setTpId("16");
        tp3.setNeId(ne.getNeId());
        tp3.setTpName("4UC400-1-21-2");
        List<InternalTp> tps = new ArrayList<>();

        tps.add(tp);
        tps.add(tp1);
        tps.add(tp2);
        tps.add(tp3);

        PhnTpOpUtil op = new PhnTpOpUtil(ne, tps, true);
        try {
            List<OpticalPower> ops = op.call();

            for(OpticalPower op1 : ops) {
                System.out.println("opr:" +op1.getOpr() + "\topt:" +op1.getOpt());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
