package com.hcop.otn.common.obtainop;

import com.hcop.otn.common.beans.InternalNe;
import com.hcop.otn.common.beans.OpticalPower;
import com.hcop.otn.common.beans.InternalTp;
import com.hcop.otn.common.internal.xos.request.GetPmDataRequest;
import com.hcop.otn.common.internal.xos.request.PMP;
import com.hcop.otn.common.internal.xos.request.PmpCurrentData;
import com.hcop.otn.common.internal.xos.request.XosPmpService;
import com.hcop.otn.common.internal.xos.response.extractors.GetPmDataExtractor;
import com.hcop.otn.common.internal.xos.util.XosException;
import com.hcop.otn.common.internal.xos.util.XosOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class XosOpPmpUtil {
    private static Log log = LogFactory.getLog(XosOpPmpUtil.class);
    public static OpticalPower getOPByTp(int groupId, int emlId, InternalNe ne, InternalTp tp) {

        List<PMP> pmpList = XosPmpService.getAllPMP(groupId, emlId, ne.getNeName(), tp.getTpName(), "", "");

        PMP[] pmpStrs = getPMPByPortName(pmpList, tp.getTpName());

        String receive = "--";
        String send = "--";

        if(pmpStrs[0] != null) {
            GetPmDataRequest rxRequest = constructRequest(pmpStrs[0]);
            List<PmpCurrentData> rxData = null;
            try {
                rxData = (List<PmpCurrentData>) XosOperation
                        .getResult(groupId, rxRequest, new GetPmDataExtractor());
                if (!rxData.isEmpty()) {
                    for (PmpCurrentData currentData : rxData) {
                        //log.error("+++++currentData:" + currentData.getParameterName() + " Port:" + ne.getUserLabel() + "/" + portInfo.getPortName());
                        if (currentData.getParameterName().equals("OPR-AVG")) {
                            receive = getResult(currentData.getValue());
                        }
                    }
                }
            }catch(XosException e) {
                e.getMessage();
            }
        }

        if(pmpStrs[1] != null) {
            GetPmDataRequest txRequest = constructRequest(pmpStrs[1]);
            List<PmpCurrentData> txData = null;
            try {
                txData = (List<PmpCurrentData>) XosOperation.getResult(groupId,
                        txRequest, new GetPmDataExtractor());
                if (!txData.isEmpty()) {
                    for (PmpCurrentData currentData : txData) {
                        //log.error("+++++currentData:" + currentData.getParameterName() + " Port:" + ne.getUserLabel() + "/" + portInfo.getPortName());
                        if (currentData.getParameterName().equals("OPT-AVG")) {
                            send = getResult(currentData.getValue());
                        }
                    }
                }
            } catch (XosException e) {
                e.getMessage();
            }
        }

        OpticalPower bean = new OpticalPower();
        bean.setNe(ne);
        bean.setTp(tp);
        bean.setOpr(receive);
        bean.setOpt(send);

        return bean;
    }
    /**
     * 从PMP获取端口的收发PMP信息 PMP[0] rx PMP[1] tx
     */
    private static PMP[] getPMPByPortName(List<PMP> pmps, String portName) {
        PMP[] pmpStrs = new PMP[2];
        for (PMP pmp : pmps) {
            //log.error("+++++PMP:" + pmp.toString());
            if ((pmp.getPtpName().equals(portName))
                    //&& (pmp.getGranularity() == Granularity.GRN_15M)
                    && (pmp.getLocation() == PMP.Location.PML_NEAR_END)) {
                if (pmp.getDirection() == PMP.Direction.PMD_RVC) {
                    pmpStrs[0] = pmp;
                } else if (pmp.getDirection() == PMP.Direction.PMD_TRMT) {
                    pmpStrs[1] = pmp;
                }
            }
        }
        return pmpStrs;
    }

    private static GetPmDataRequest constructRequest(PMP pmp) {
        String ncName = String.valueOf(pmp.getGroupID());
        String neName = String.valueOf(pmp.getNeID());
        String ptpName = pmp.getPtpName();
        String ftpName = pmp.getFtpName();
        String ctpName = pmp.getCtpName();
        String tcmpName = "";
        int rate = pmp.getLayerRate();
        int pmLocation = pmp.getLocation().ordinal();
        int granularity = 0;
        int direction = pmp.getDirection().ordinal();
        GetPmDataRequest request = new GetPmDataRequest(ncName, neName,
                ptpName, ftpName, ctpName, tcmpName, rate, pmLocation,
                granularity, direction);
        return request;
    }
    private static String getResult(float value) {
        if(value < -40F) {
            return "--";
        }
        return String.valueOf(value);
    }

}
