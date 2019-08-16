package com.hcop.ptn.alarm.rootcause.common;

import com.hcop.ptn.alarm.rootcause.beans.TpBean;
import com.hcop.ptn.common.utils.CollectionUtils;
import com.hcop.ptn.restful.model.Alarm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PeerServiceAlarmMatcher {
    private static Log log = LogFactory.getLog(PeerServiceAlarmMatcher.class);

    private static PeerServiceAlarmMatcher ourInstance = new PeerServiceAlarmMatcher();

    public static PeerServiceAlarmMatcher getInstance() {
        return ourInstance;
    }

    private PeerServiceAlarmMatcher() {
    }

    public List<Alarm> getMatchedAlarms(List<TpBean> tps, List<Alarm> alarms, AlarmCategoryEnum cEnum) throws Exception {
        if (CollectionUtils.isEmpty(tps) || CollectionUtils.isEmpty(alarms)) {
            log.error("empty tp list or alarm list");
            return null;
        }

        List<Alarm> results = new ArrayList<>();
        for (Alarm alarm : alarms) {
            for (TpBean tp : tps) {
                if (isMatched(tp, alarm, cEnum)) {
                    results.add(alarm);
                }
            }
        }

        return results;
    }

    private boolean isMatched(TpBean tp, Alarm alarm, AlarmCategoryEnum tpEnum) throws Exception {
        switch (tpEnum) {
            case EVC:
            case SECTION:
                return isMatchedEvc(tp, alarm);
            case CES:
                return isMatchedCes(tp, alarm);
            case PW:
                return isMatchedByPw(tp, alarm);
            case TUNNEL:
                return isMatchedByTunnel(tp, alarm);
            default:
                return false;
        }
    }

    // 查询对端根因，业务查询结果都是TP，那么如果：
    // 1.根因发生在端口上，则其必须出现在TP所在端口；
    // 2.根因发生在板卡上，则其必须出现在TP所在板卡上；
    // 3.根因发生在子架上，则其必须出现在TP所在子架上；
    // 4.根因发生在时钟上，则其必须出现在TP所在子架上；
    // 5.根因发生在PW，则需找出PW的端口及PWSEG，看PW所在端口是否与TP是同一个端口或PWSEG；TUNNEL类似(但MEP不确定)；
    // 6.EVC和CES不确定

    private boolean isMatchedEvc(TpBean tp, Alarm alarm) throws Exception {
        if (AlarmHeadPartHelper.compare(alarm.getFriendlyName(), tp.getNeName())) {
            Map<String, String> orgAlarmInfo = AlarmHeadPartHelper.parseEvc(tp.getTpName());
            AlarmRelatedInfo ali = AlarmHeadPartHelper.getAlarmRelatedInfo(alarm);
            if (ali == null || ali.getCategoryEnum() == null) {
                return false;
            }

            AlarmCategoryEnum acEnum = ali.getCategoryEnum();

            Map<String, String> alarmInfo  = AlarmHeadPartHelper.parseFriendlyName(alarm);

            switch (acEnum) {
                case EVC:
                case SECTION:
                case PTP:
                case PW:
                case TUNNEL:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.TP), alarmInfo.get(AlarmConstants.TP));
                case CES:
                    // 一般不会出现这种情况
                    String cesTpName = AlarmHeadPartHelper.parseCesTp(alarmInfo.get(AlarmConstants.TP));
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.TP), cesTpName);
                case NE:
                    return true;
                case SHELF:
                case TIMING:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.SHELF), alarmInfo.get(AlarmConstants.SHELF));
                case SLOT:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.SLOT), alarmInfo.get(AlarmConstants.SLOT));
                default:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.ORIGINAL), alarmInfo.get(AlarmConstants.ORIGINAL));
            }
        }

        return false;
    }

    private boolean isMatchedCes(TpBean tp, Alarm alarm) throws Exception {
        if (AlarmHeadPartHelper.compare(alarm.getFriendlyName(), tp.getNeName())) {
            Map<String, String> orgAlarmInfo = AlarmHeadPartHelper.parseCes(tp.getTpName());
            AlarmRelatedInfo ali = AlarmHeadPartHelper.getAlarmRelatedInfo(alarm);
            if (ali == null || ali.getCategoryEnum() == null) {
                return false;
            }

            AlarmCategoryEnum acEnum = ali.getCategoryEnum();

            Map<String, String> alarmInfo  = AlarmHeadPartHelper.parseFriendlyName(alarm);

            switch (acEnum) {
                case EVC:
                case SECTION:
                case PTP:
                case PW:
                case TUNNEL:
                    // 将端口格式转换成一样的
                    String cesTpName = AlarmHeadPartHelper.parseCesTp(tp.getTpName());
                    return AlarmHeadPartHelper.compare(cesTpName, alarmInfo.get(AlarmConstants.TP));
                case CES:
                    return AlarmHeadPartHelper.compare(tp.getTpName(), alarmInfo.get(AlarmConstants.TP));
                case NE:
                    return true;
                case SHELF:
                case TIMING:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.SHELF), alarmInfo.get(AlarmConstants.SHELF));
                case SLOT:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.SHELF), alarmInfo.get(AlarmConstants.SLOT));
                default:
                    return AlarmHeadPartHelper.compare(tp.getTpName(), alarmInfo.get(AlarmConstants.ORIGINAL));
            }
        }

        return false;
    }

    private boolean isMatchedByPw(TpBean tp, Alarm alarm) throws Exception {
        if (AlarmHeadPartHelper.compare(alarm.getFriendlyName(), tp.getNeName())) {
            String tpName = tp.getTpName();
            String slot = AlarmHeadPartHelper.parseSlotStr(tpName.split("/")[0]);
            String shelf = slot.substring(0, slot.lastIndexOf("-"));
            AlarmRelatedInfo ali = AlarmHeadPartHelper.getAlarmRelatedInfo(alarm);
            if (ali == null || ali.getCategoryEnum() == null) {
                return false;
            }

            AlarmCategoryEnum acEnum = ali.getCategoryEnum();

            Map<String, String> alarmInfo  = AlarmHeadPartHelper.parseFriendlyName(alarm);

            switch (acEnum) {
                case EVC:
                case SECTION:
                case PTP:
                case TUNNEL:
                    return AlarmHeadPartHelper.compare(tpName, alarmInfo.get(AlarmConstants.TP));
                case CES:
                    // 一般不会出现这种情况
                    String cesTpName = AlarmHeadPartHelper.parseCesTp(alarmInfo.get(AlarmConstants.TP));
                    return AlarmHeadPartHelper.compare(tpName, cesTpName);
                case NE:
                    return true;
                case PW:
                    return AlarmHeadPartHelper.compare(tpName, alarmInfo.get(AlarmConstants.TP))
                            || (tp.getExtendedFields().get("segaid") != null
                            && AlarmHeadPartHelper.compare(tp.getExtendedFields().get("segaid").toString(), alarmInfo.get(AlarmConstants.SEGMENT)));
                case SHELF:
                case TIMING:
                    return AlarmHeadPartHelper.compare(shelf, alarmInfo.get(AlarmConstants.SHELF));
                case SLOT:
                    return AlarmHeadPartHelper.compare(slot, alarmInfo.get(AlarmConstants.SLOT));
                default:
                    return AlarmHeadPartHelper.compare(tpName, alarmInfo.get(AlarmConstants.ORIGINAL));
            }
        }

        return false;
    }

    private boolean isMatchedByTunnel(TpBean tp, Alarm alarm) throws Exception {
        if (AlarmHeadPartHelper.compare(alarm.getFriendlyName(), tp.getNeName())) {
            String tpName = tp.getTpName();
            String slot = AlarmHeadPartHelper.parseSlotStr(tpName.split("/")[0]);
            String shelf = slot.substring(0, slot.lastIndexOf("-"));
            AlarmRelatedInfo ali = AlarmHeadPartHelper.getAlarmRelatedInfo(alarm);
            if (ali == null || ali.getCategoryEnum() == null) {
                return false;
            }

            AlarmCategoryEnum acEnum = ali.getCategoryEnum();

            Map<String, String> alarmInfo  = AlarmHeadPartHelper.parseFriendlyName(alarm);

            switch (acEnum) {
                case EVC:
                case SECTION:
                case PTP:
                case PW:
                    return AlarmHeadPartHelper.compare(tpName, alarmInfo.get(AlarmConstants.TP));
                case CES:
                    // 一般不会出现这种情况
                    String cesTpName = AlarmHeadPartHelper.parseCesTp(alarmInfo.get(AlarmConstants.TP));
                    return AlarmHeadPartHelper.compare(tpName, cesTpName);
                case NE:
                    return true;
                case TUNNEL:
                    return AlarmHeadPartHelper.compare(tpName, alarmInfo.get(AlarmConstants.TP))
                            || (tp.getExtendedFields().get("aid") != null
                            && AlarmHeadPartHelper.compare(tp.getExtendedFields().get("aid").toString(), alarmInfo.get(AlarmConstants.SEGMENT)));
                case SHELF:
                case TIMING:
                    return AlarmHeadPartHelper.compare(shelf, alarmInfo.get(AlarmConstants.SHELF));
                case SLOT:
                    return AlarmHeadPartHelper.compare(slot, alarmInfo.get(AlarmConstants.SLOT));
                default:
                    return AlarmHeadPartHelper.compare(tpName, alarmInfo.get(AlarmConstants.ORIGINAL));
            }
        }

        return false;
    }
}
