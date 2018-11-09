package com.otn.tool.common.obtainop;

import com.otn.tool.common.beans.Ne;
import com.otn.tool.common.beans.OpticalPower;
import com.otn.tool.common.beans.Tp;
import com.otn.tool.common.internal.xos.request.GetCurrentPMPRequest;
import com.otn.tool.common.internal.xos.response.extractors.GetOpPowerExtractor;
import com.otn.tool.common.internal.xos.session.SessionManager;
import com.otn.tool.common.internal.xos.util.XosException;
import com.otn.tool.common.internal.xos.util.XosOperation;
import com.otn.tool.common.utils.CollectionUtils;
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
        if(CollectionUtils.isEmpty(tps)) {
            return opList;
        }
        for(Tp tp : tps) {
            if(tp.getName().matches(".*OSC(\\w*)?$")) {
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
                        + tp.getName() + "; and the exception is:"
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
                //bean.set("opr", getResult((String) powerBean.get("rx")));
                String oprStr = getResult((String) powerBean.get("rx"));
                bean.setOpr(oprStr);
            }
            if (powerBean.get("tx") != null) {
                //bean.set("opt", getResult((String) powerBean.get("tx")));
                String optStr = getResult((String) powerBean.get("tx"));
                bean.setOpt(optStr);
            }
            //log.error("+++++++++++oprStr: "+ bean.getOpr() + " | optStr: " + bean.getOpt() + "+++++++++++");
            opList.add(bean);
        }
        if (opList.isEmpty()) {
            log.warn("Can't read optical power from ne:" + ne.getName());
        }

        log.info("End to get optical power from ne:" + ne.getName());
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

    private GetCurrentPMPRequest constructRequest(Tp tp) {
        String ncName = "EML_" + String.valueOf(ne.getGroupId()) + "_SNA";
        String neName = String.valueOf(ne.getEmlId());
        String ptpName = handleTpName(tp.getName());
        String ftpName = "";
        String ctpName = "";
        GetCurrentPMPRequest request = new GetCurrentPMPRequest(
                ncName, neName, ptpName, ftpName, ctpName);
        return request;
    }
    private void notFountOP(Tp tp,List<OpticalPower> beansList) {
        OpticalPower bean = new OpticalPower();
        bean.setNe(ne);
        bean.setTp(tp);
        bean.setOpt("--");
        bean.setOpr("--");
        beansList.add(bean);
    }

    private String handleTpName(String tpName) {
        Pattern p = Pattern.compile("-");
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
        Ne ne = new Ne();
        ne.setName("PSS1683");
        ne.setCommunicationState("0");
        ne.setEmlId(1);
        ne.setGroupId(100);
        ne.setId("7");
        ne.setIpAddress("172.24.168.3");
        ne.setType("1830PSS_32");


        Tp tp = new Tp();
        tp.setBoardName("AHPLG");
        tp.setBoardType("OA");
        tp.setId("75893");
        tp.setNe(ne);
        tp.setName("AHPLG-1-2-LINE");

        Tp tp1 = new Tp();
        tp1.setBoardName("AHPLG");
        tp1.setBoardType("OA");
        tp1.setId("76663");
        tp1.setNe(ne);
        tp1.setName("AHPLG-1-2-OSC");
        List<Tp> tps = new ArrayList<>();

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
