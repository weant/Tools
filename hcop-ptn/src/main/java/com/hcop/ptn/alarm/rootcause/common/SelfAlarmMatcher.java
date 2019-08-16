package com.hcop.ptn.alarm.rootcause.common;

import com.hcop.ptn.restful.model.Alarm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelfAlarmMatcher {
    private static Log log = LogFactory.getLog(SelfAlarmMatcher.class);

    private static SelfAlarmMatcher ourInstance = new SelfAlarmMatcher();

    public static SelfAlarmMatcher getInstance() {
        return ourInstance;
    }

    private SelfAlarmMatcher() {
    }

    public List<Alarm> getMatchedAlarms(Alarm orgAlarm, AlarmRelatedInfo info, List<Alarm> alarms) throws Exception {
        List<Alarm> results = new ArrayList<>();
        for (Alarm alarm : alarms) {
            if (isMatched(orgAlarm, info, alarm)) {
                    results.add(alarm);
            }
        }

        return results;
    }

    private boolean isMatched(Alarm orgAlarm, AlarmRelatedInfo info, Alarm alarm) throws Exception {
        // 获取的是查询到的告警的类型，然后再解析
        if (info == null || info.getCategoryEnum() == null) {
            return false;
        }

        switch (info.getCategoryEnum()) {
            case EVC:
                return isMatchedByEvc(orgAlarm, info, alarm);
            case CES:
                return isMatchedByCes(orgAlarm, info, alarm);
            case PW:
                return isMatchedByPw(orgAlarm, info, alarm);
            case TUNNEL:
                return isMatchedByTunnel(orgAlarm, info, alarm);
            case SECTION:
                return isMatchedByEvc(orgAlarm, info, alarm);
            case TIMING:
                return isMatchedByTiming(orgAlarm, info, alarm);
            case SHELF:
                return isMatchedByShelf(orgAlarm, info, alarm);
            case SLOT:
                return isMatchedBySlot(orgAlarm, info, alarm);
            case NE:
                return isMatchedByNe(info, alarm);
            case PTP:
                return isMatchedByPtp(orgAlarm, info, alarm);
            case EQUIPMENT:
                return isMatchedByEquipment(orgAlarm, info, alarm);
            default:
                return false;
        }
    }

    private boolean isMatchedByEvc(Alarm orgAlarm, AlarmRelatedInfo info, Alarm alarm) throws Exception {
        if (AlarmHeadPartHelper.compare(alarm.getFriendlyName(), info.getNeName())) {
            String usefulPart = AlarmHeadPartHelper.getUsefulPartFromFriendlyName(orgAlarm.getFriendlyName());
            Map<String, String> orgAlarmInfo = AlarmHeadPartHelper.parseEvc(usefulPart);

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
                    return isMatched(info.getRelatedInfo(), alarmInfo, AlarmConstants.ORIGINAL);
            }
        }

        return false;
    }

    private boolean isMatched(List<String> orgAlarmInfo, Map<String, String> alarmInfo, String acEnum) {
        for (String aInfo : orgAlarmInfo) {
            if (AlarmHeadPartHelper.compare(aInfo, alarmInfo.get(acEnum))) {
                return true;
            }
        }
        return false;
    }

    private boolean isMatchedByCes(Alarm orgAlarm, AlarmRelatedInfo info, Alarm alarm) throws Exception {
        if (AlarmHeadPartHelper.compare(alarm.getFriendlyName(), info.getNeName())) {
            String usefulPart = AlarmHeadPartHelper.getUsefulPartFromFriendlyName(orgAlarm.getFriendlyName());
            Map<String, String> orgAlarmInfo = AlarmHeadPartHelper.parseCes(usefulPart);

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
                    String cesTpName = AlarmHeadPartHelper.parseCesTp(orgAlarmInfo.get(AlarmConstants.TP));
                    return AlarmHeadPartHelper.compare(cesTpName, alarmInfo.get(AlarmConstants.TP));
                case CES:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.TP), alarmInfo.get(AlarmConstants.TP));
                case NE:
                    return true;
                case SHELF:
                case TIMING:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.SHELF), alarmInfo.get(AlarmConstants.SHELF));
                case SLOT:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.SLOT), alarmInfo.get(AlarmConstants.SLOT));
                default:
                    return isMatched(info.getRelatedInfo(), alarmInfo, AlarmConstants.ORIGINAL);
            }
        }

        return false;
    }

    private boolean isMatchedByPw(Alarm orgAlarm, AlarmRelatedInfo info, Alarm alarm) throws Exception {
        if (AlarmHeadPartHelper.compare(alarm.getFriendlyName(), info.getNeName())) {
            String usefulPart = AlarmHeadPartHelper.getUsefulPartFromFriendlyName(orgAlarm.getFriendlyName());
            Map<String, String> orgAlarmInfo = AlarmHeadPartHelper.parsePw(info, usefulPart);

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
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.TP), alarmInfo.get(AlarmConstants.TP));
                case CES:
                    // 一般不会出现这种情况
                    String cesTpName = AlarmHeadPartHelper.parseCesTp(alarmInfo.get(AlarmConstants.TP));
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.TP), cesTpName);
                case NE:
                    return true;
                case PW:
                    if (AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.TP), alarmInfo.get(AlarmConstants.TP))
                            || AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.SEGMENT), alarmInfo.get(AlarmConstants.SEGMENT))) {
                        return true;
                    }
                    return false;
                case SHELF:
                case TIMING:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.SHELF), alarmInfo.get(AlarmConstants.SHELF));
                case SLOT:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.SLOT), alarmInfo.get(AlarmConstants.SLOT));
                default:
                    return isMatched(info.getRelatedInfo(), alarmInfo, AlarmConstants.ORIGINAL);
            }
        }

        return false;
    }

    private boolean isMatchedByTunnel(Alarm orgAlarm, AlarmRelatedInfo info, Alarm alarm) throws Exception {
        if (AlarmHeadPartHelper.compare(alarm.getFriendlyName(), info.getNeName())) {
            String usefulPart = AlarmHeadPartHelper.getUsefulPartFromFriendlyName(orgAlarm.getFriendlyName());
            Map<String, String> orgAlarmInfo = AlarmHeadPartHelper.parseTunnel(info, usefulPart);

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
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.TP), alarmInfo.get(AlarmConstants.TP));
                case CES:
                    // 一般不会出现这种情况
                    String cesTpName = AlarmHeadPartHelper.parseCesTp(alarmInfo.get(AlarmConstants.TP));
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.TP), cesTpName);
                case NE:
                    return true;
                case TUNNEL:
                    if (AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.TP), alarmInfo.get(AlarmConstants.TP))
                            || AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.SEGMENT), alarmInfo.get(AlarmConstants.SEGMENT))) {
                        return true;
                    }
                    return false;
                case SHELF:
                case TIMING:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.SHELF), alarmInfo.get(AlarmConstants.SHELF));
                case SLOT:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.SLOT), alarmInfo.get(AlarmConstants.SLOT));
                default:
                    return isMatched(info.getRelatedInfo(), alarmInfo, AlarmConstants.ORIGINAL);
            }
        }

        return false;
    }

    private boolean isMatchedByTiming(Alarm orgAlarm, AlarmRelatedInfo info, Alarm alarm) throws Exception {
        if (AlarmHeadPartHelper.compare(alarm.getFriendlyName(), info.getNeName())) {
            String usefulPart = AlarmHeadPartHelper.getUsefulPartFromFriendlyName(orgAlarm.getFriendlyName());
            Map<String, String> orgAlarmInfo = AlarmHeadPartHelper.parseTiming(usefulPart);

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
                case CES:
                case TUNNEL:
                case SLOT:
                case SHELF:
                case TIMING:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.SHELF), alarmInfo.get(AlarmConstants.SHELF));
                case NE:
                    return true;
                default:
                    return isMatched(info.getRelatedInfo(), alarmInfo, AlarmConstants.ORIGINAL);
            }
        }

        return false;
    }

    private boolean isMatchedByNe(AlarmRelatedInfo info, Alarm alarm) {
        if (AlarmHeadPartHelper.compare(alarm.getFriendlyName(), info.getNeName())) {
            return true;
        }

        return false;
    }

    private boolean isMatchedByShelf(Alarm orgAlarm, AlarmRelatedInfo info, Alarm alarm) throws Exception {
        if (AlarmHeadPartHelper.compare(alarm.getFriendlyName(), info.getNeName())) {
            String usefulPart = AlarmHeadPartHelper.getUsefulPartFromFriendlyName(orgAlarm.getFriendlyName());
            Map<String, String> orgAlarmInfo = AlarmHeadPartHelper.parseShelf(usefulPart);

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
                case CES:
                case TUNNEL:
                case SLOT:
                case SHELF:
                case TIMING:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.SHELF), alarmInfo.get(AlarmConstants.SHELF));
                case NE:
                    return true;
                default:
                    return isMatched( info.getRelatedInfo(), alarmInfo, AlarmConstants.ORIGINAL);
            }
        }

        return false;
    }

    private boolean isMatchedBySlot(Alarm orgAlarm, AlarmRelatedInfo info, Alarm alarm) throws Exception {
        if (AlarmHeadPartHelper.compare(alarm.getFriendlyName(), info.getNeName())) {
            String usefulPart = AlarmHeadPartHelper.getUsefulPartFromFriendlyName(orgAlarm.getFriendlyName());
            Map<String, String> orgAlarmInfo = AlarmHeadPartHelper.parseSlot(usefulPart);

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
                case CES:
                case TUNNEL:
                case TIMING:
                case SLOT:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.SLOT), alarmInfo.get(AlarmConstants.SLOT));
                case SHELF:
                    return AlarmHeadPartHelper.compare(orgAlarmInfo.get(AlarmConstants.SHELF), alarmInfo.get(AlarmConstants.SHELF));
                case NE:
                    return true;
                default:
                    return isMatched(info.getRelatedInfo(), alarmInfo, AlarmConstants.ORIGINAL);
            }
        }

        return false;
    }

    private boolean isMatchedByPtp(Alarm orgAlarm, AlarmRelatedInfo info, Alarm alarm) throws Exception {
        if (AlarmHeadPartHelper.compare(alarm.getFriendlyName(), info.getNeName())) {
            String usefulPart = AlarmHeadPartHelper.getUsefulPartFromFriendlyName(orgAlarm.getFriendlyName());
            Map<String, String> orgAlarmInfo = AlarmHeadPartHelper.parsePtp(usefulPart);

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
                    return isMatched(info.getRelatedInfo(), alarmInfo, AlarmConstants.ORIGINAL);
            }
        }

        return false;
    }

    private boolean isMatchedByEquipment(Alarm orgAlarm, AlarmRelatedInfo info, Alarm alarm) throws Exception {
        return isMatchedBySlot(orgAlarm, info, alarm);
    }
}
