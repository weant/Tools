/**
 * FileName: AlarmRelatedInfoHelper
 * Author:   Administrator
 * Date:     2019/3/30 14:58
 * Description:
 */
package com.hcop.ptn.alarm.rootcause.common;

import com.hcop.ptn.alarm.rootcause.beans.ConnectionBean;
import com.hcop.ptn.alarm.rootcause.beans.TpBean;
import com.hcop.ptn.alarm.rootcause.servicesource.SourceFactory;
import com.hcop.ptn.common.properties.AlarmCategoryConf;
import com.hcop.ptn.common.properties.AlarmRCPeerEndConf;
import com.hcop.ptn.common.utils.StringUtils;
import com.hcop.ptn.restful.model.Alarm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class AlarmRelatedInfoHelper {
    private static Log log = LogFactory.getLog(AlarmRelatedInfoHelper.class);
    private static Map<String, String> alarmCategoryMap = AlarmCategoryConf.instance().getPropertiesMap();
    private static Map<String, String> peerEndCategoryMap = AlarmRCPeerEndConf.instance().getPropertiesMap();

    public static AlarmRelatedInfo getAlarmRelatedInfo(Alarm alarm) throws Exception {
        if(alarm == null) {
            return null;
        }

        String friendlyName = alarm.getFriendlyName();
        if (StringUtils.isEmpty(friendlyName)) {
            log.error("alarm friendlyName is empty : " + alarm.getProbableCauseId());
            return null;
        }

        Set<String> rcs = RootCausePcIdHelper.getInstance().getRootCauseSet(alarm.getProbableCauseId());
        if (rcs == null) {
            log.error("root cause not config, set the root cause to itself : " + alarm.getProbableCauseId());
            rcs = new HashSet <>();
            rcs.add(alarm.getProbableCauseId());
        }

        AlarmRelatedInfo info = new AlarmRelatedInfo();
        info.setRcPcIds(rcs);

        String defCategory = alarmCategoryMap.get(alarm.getProbableCauseId());
        if (StringUtils.isEmpty(defCategory)) {
            log.error("+++++++set alarm category as UNKNOWN since cannot find it for alarm: " + alarm.getFriendlyName() + ", pcid:" + alarm.getProbableCauseId());
            defCategory = AlarmCategoryEnum.UNKNOWN.toString();
        }

        List<AlarmCategoryEnum> categoryList = new ArrayList<>();
        Arrays.asList(defCategory.split(",")).forEach(category -> categoryList.add(AlarmCategoryEnum.fromString(category)));

        info.setServiceEnum(AlarmCategoryEnum.fromString(peerEndCategoryMap.get(alarm.getProbableCauseId())));
        String[] parts = friendlyName.split(",");
        String[] headers = parts[0].split("/");

        info.setNeName(headers[0]);
        if (headers.length > 1) {
            StringBuilder s = new StringBuilder();
            for(int i=1; i<headers.length; i++) {
                s.append(headers[i]);
                s.append("/");
            }
            info.getRelatedInfo().add(s.toString().substring(0, s.lastIndexOf("/")));
        }

        for (AlarmCategoryEnum category : categoryList) {
            // NE
            if ((category.equals(AlarmCategoryEnum.EQUIPMENT) && !friendlyName.contains("/"))
                    || !friendlyName.contains("/")
                    || friendlyName.contains("System")
                    || category.equals(AlarmCategoryEnum.NE)) {

                info.setCategoryEnum(AlarmCategoryEnum.NE);

                return info;
            }

            // TIMING
            if (category.equals(AlarmCategoryEnum.TIMING)
                    || friendlyName.contains("TIMING")
                    || friendlyName.contains("COREA")) {

                info.setCategoryEnum(AlarmCategoryEnum.TIMING);

                return info;
            }

            // SHELF
            if ((category.equals(AlarmCategoryEnum.EQUIPMENT)
                    && (friendlyName.contains("SHELF") || headers[1].split("-").length == 3))
                    || friendlyName.contains("SHELF")) {

                info.setCategoryEnum(AlarmCategoryEnum.SHELF);

                return info;
            }

            // SLOT
            if ((category.equals(AlarmCategoryEnum.EQUIPMENT)
                    && (friendlyName.contains("SLOT") || headers[1].split("-").length == 4))
                    || friendlyName.contains("SLOT")) {

                info.setCategoryEnum(AlarmCategoryEnum.SLOT);

                return info;
            }

            // TUNNEL
            if ((category.equals(AlarmCategoryEnum.TUNNEL)
                    && (friendlyName.contains("/MEP#") || friendlyName.contains("/TUSEG-")))
                    || friendlyName.contains("/MEP#")
                    || friendlyName.contains("/TUSEG-")) {

                info.setCategoryEnum(AlarmCategoryEnum.TUNNEL);

                List<ConnectionBean> conns = SourceFactory.getInstance().getService(info.getRelatedInfo(), info.getCategoryEnum(), info.getNeName());
                if (conns != null) {
                    for (ConnectionBean bean : conns) {
                        //log.error("++++++++++Alarm Related Info for tunnel, tunnel name:" +  bean.getConnName());
                        for (TpBean tp : bean.getTps()) {
                            //log.error("++++++++++Alarm Related Info for tunnel,ne:" + tp.getNeName() + ", tp:" +  tp.getTpName() + ", aid:" + tp.getExtendedFields().get("aid").toString());
                            if (tp.getNeName().equals(info.getNeName())) {
                                String aid = tp.getExtendedFields().get("aid") == null ? "" : tp.getExtendedFields().get("aid").toString();
                                String tpName = tp.getTpName();

                                if (friendlyName.contains("/TUSEG-")) {
                                    info.getRelatedInfo().add(tpName);
                                } else {
                                    info.getRelatedInfo().add(aid);
                                }

                                // TODO: 2019/4/2 未处理MEP#1#1#1的情况
                            }
                        }
                    }
                }

                return info;
            }

            // PW
            if ((category.equals(AlarmCategoryEnum.PW)
                    && friendlyName.contains("/PWSEG-"))
                    || friendlyName.contains("/PWSEG-")) {

                info.setCategoryEnum(AlarmCategoryEnum.PW);

                List<ConnectionBean> conns = SourceFactory.getInstance().getService(info.getRelatedInfo(), info.getCategoryEnum(), info.getNeName());
                if (conns != null) {
                    for (ConnectionBean bean : conns) {
                        for (TpBean tp : bean.getTps()) {
                            if (tp.getNeName().equals(info.getNeName())) {
                                String aid = tp.getExtendedFields().get("segaid") == null ? "" : tp.getExtendedFields().get("segaid").toString();
                                String tpName = tp.getTpName();

                                if (friendlyName.contains("/PWSEG-")) {
                                    info.getRelatedInfo().add(tpName);
                                } else {
                                    info.getRelatedInfo().add(aid);
                                }

                                // TODO: 2019/4/2 未处理MEP#1#1#1的情况
                            }
                        }
                    }
                }

                return info;
            }

            // CES
            if (category.equals(AlarmCategoryEnum.CES)) {
                info.setCategoryEnum(AlarmCategoryEnum.CES);

                return info;
            }

            // EVC
            // TODO: 2019/3/30
            if (category.equals(AlarmCategoryEnum.EVC)) {
                info.setCategoryEnum(AlarmCategoryEnum.EVC);

                return info;
            }

            // SECTION
            // TODO: 2019/3/30
            if (category.equals(AlarmCategoryEnum.SECTION)) {
                info.setCategoryEnum(AlarmCategoryEnum.SECTION);

                return info;
            }

            // PTP
            if ((category.equals(AlarmCategoryEnum.EQUIPMENT)
                    && (friendlyName.contains("/ETHLocPort#") || headers[1].split("-").length == 5))
                    || (category.equals(AlarmCategoryEnum.PTP))) {
                info.setCategoryEnum(AlarmCategoryEnum.PTP);

                return info;
            }
        }

        return null;
    }
}
