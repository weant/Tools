package com.hcop.ptn.common.obtainop;

import com.hcop.ptn.common.beans.InternalNe;
import com.hcop.ptn.common.beans.InternalTp;
import com.hcop.ptn.common.beans.OpticalPower;
import com.hcop.ptn.common.internal.xos.request.GetCurrentPMPRequest;
import com.hcop.ptn.common.internal.xos.response.extractors.GetOpPowerExtractor;
import com.hcop.ptn.common.internal.xos.session.SessionManager;
import com.hcop.ptn.common.internal.xos.util.XosException;
import com.hcop.ptn.common.internal.xos.util.XosOperation;
import com.hcop.ptn.common.utils.CollectionUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhnTpOpUtil implements Callable<List<OpticalPower>> {
    private Log log = LogFactory.getLog(PhnTpOpUtil.class);
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
            }
            if (powerBean.get("tx") != null) {
                bean.setOpt(getResult((String) powerBean.get("tx")));
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
        ne.setNeName("WGPHN-2");
        ne.setCommunicationState("0");
        ne.setEmlId(21);
        ne.setGroupId(100);
        ne.setNeId("24");
        ne.setNeIp("10.70.10.113");
        ne.setNeType("1830PSS_24X");


        InternalTp tp = new InternalTp();
        tp.setEquipName("2UC400");
        tp.setTpId("181860");
        tp.setNe(ne);
        tp.setTpName("2UC400-1-18-1");

        InternalTp tp1 = new InternalTp();
        tp1.setEquipName("2UC400");
        tp1.setTpId("181819");
        tp1.setNe(ne);
        tp1.setTpName("2UC400-1-18-2");
        List<InternalTp> tps = new ArrayList<>();

        tps.add(tp);
        tps.add(tp1);

        PhnTpOpUtil op = new PhnTpOpUtil(ne, tps, false);
        try {
            List<OpticalPower> ops = op.call();

            System.out.println("opr:" +ops.get(0).getOpr() + "\topt:" +ops.get(0).getOpt());
            System.out.println("opr:" +ops.get(1).getOpr() + "\topt:" +ops.get(1).getOpt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
