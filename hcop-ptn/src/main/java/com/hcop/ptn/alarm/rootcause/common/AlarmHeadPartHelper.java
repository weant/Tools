package com.hcop.ptn.alarm.rootcause.common;

import com.hcop.ptn.restful.model.Alarm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlarmHeadPartHelper {
    private static Log log = LogFactory.getLog(AlarmHeadPartHelper.class);
    private static String[] unknownPtp = new String[]{"INFiberINBand", "GFPConf"};

    public static Map<String, String> parseFriendlyName(Alarm alarm) throws Exception {
        AlarmRelatedInfo ali = getAlarmRelatedInfo(alarm);
        Map<String, String> result = new HashMap<>(5);
        String usefulPart = getUsefulPartFromFriendlyName(alarm.getFriendlyName());

        if (ali == null || ali.getCategoryEnum() == null) {
            log.error("cannot find alarm related info: " + alarm.getFriendlyName() + " || " + alarm.getProbableCauseId());
            result.put(AlarmConstants.ORIGINAL, usefulPart);
            return result;
        }

        switch (ali.getCategoryEnum()) {
            case EVC:
            case SECTION:
                return parseEvc(usefulPart);
            case PTP:
                return parsePtp(usefulPart);
            case CES:
                return parseCes(usefulPart);
            case NE:
                result.put(AlarmConstants.ORIGINAL, usefulPart);
                return result;
            case PW:
                return parsePw(ali, usefulPart);
            case TUNNEL:
                return parseTunnel(ali, usefulPart);
            case SHELF:
                return parseShelf(usefulPart);
            case SLOT:
                return parseSlot(usefulPart);
            case TIMING:
                return parseTiming(usefulPart);
            default:
                result.put(AlarmConstants.ORIGINAL, usefulPart);
                return result;
        }
    }

    public static Map<String, String> parsePw(AlarmRelatedInfo ali, String headPart) {
        Map<String, String> map = new HashMap <>(5);
        List<String> ris = ali.getRelatedInfo();
        for(String ri : ris) {
            if (compare(ri, "PWSEG")) {
                map.put(AlarmConstants.SEGMENT, ri);
            }

            if (compare(ri, "ETHLocPort")) {
                map.put(AlarmConstants.TP, ri);
                String slot = parseSlotStr(headPart.split("/")[0]);
                String shelf = slot.substring(0, slot.lastIndexOf("-"));
                map.put(AlarmConstants.SHELF, shelf);
                map.put(AlarmConstants.SLOT, slot);
            }
        }

        map.put(AlarmConstants.ORIGINAL, headPart);

        return map;
    }

    public static Map<String, String> parseTunnel(AlarmRelatedInfo ali, String headPart) {
        Map<String, String> map = new HashMap <>(5);
        List<String> ris = ali.getRelatedInfo();
        for(String ri : ris) {

            if (compare(ri, "MEP#")) {
                log.error("cannot find Tunnel info according to " + ri);
                map.put(AlarmConstants.MEP, ri);
                continue;
            }

            if (compare(ri, "TUSEG")) {
                map.put(AlarmConstants.SEGMENT, ri);
                continue;
            }

            if (compare(ri, "ETHLocPort")) {
                map.put(AlarmConstants.TP, ri);
                String slot = parseSlotStr(headPart.split("/")[0]);
                String shelf = slot.substring(0, slot.lastIndexOf("-"));
                map.put(AlarmConstants.SHELF, shelf);
                map.put(AlarmConstants.SLOT, slot);
            }
        }

        map.put(AlarmConstants.ORIGINAL, headPart);

        return map;
    }

    public static Map<String, String> parseTiming(String headPart) {
        Map<String, String> map = new HashMap <>(2);
        map.put(AlarmConstants.ORIGINAL, headPart);
        map.put(AlarmConstants.SHELF, headPart.substring(headPart.indexOf("-"), headPart.lastIndexOf("-")));

        return map;
    }

    public static Map<String, String> parseShelf(String headPart) {
        Map<String, String> map = new HashMap <>(2);
        map.put(AlarmConstants.ORIGINAL, headPart);
        map.put(AlarmConstants.SHELF, headPart.substring(headPart.indexOf("-")));

        return map;
    }

    public static Map<String, String> parseSlot(String headPart) {
        Map<String, String> map = new HashMap <>(3);
        map.put(AlarmConstants.ORIGINAL, headPart);
        map.put(AlarmConstants.SHELF, headPart.substring(headPart.indexOf("-"), headPart.lastIndexOf("-")));
        map.put(AlarmConstants.SLOT, headPart.substring(headPart.indexOf("-")));

        return map;
    }

    public static Map<String, String> parsePtp(String headPart) {
        Map<String, String> map = new HashMap <>(4);
        String tp = "";
        if (headPart.matches("(/)?r\\d+sr\\d+sl\\d+[\\W\\w]+")) {
            tp = parseTp(headPart);
        } else {
           tp = parseCesTp(headPart);
        }

        String slot = parseSlotStr(headPart.split("/")[0]);
        String shelf = slot.substring(0, slot.lastIndexOf("-"));

        map.put(AlarmConstants.SHELF, shelf);
        map.put(AlarmConstants.SLOT, slot);
        map.put(AlarmConstants.TP, tp);
        map.put(AlarmConstants.ORIGINAL, headPart);
        return map;
    }

    public static Map<String, String> parseEvc(String headPart) {
        Map<String, String> map = new HashMap <>(4);
        if (compare(headPart, "/ETHLocPort#")) {
            String slot = parseSlotStr(headPart.split("/")[0]);
            String shelf = slot.substring(0, slot.lastIndexOf("-"));

            map.put(AlarmConstants.SHELF, shelf);
            map.put(AlarmConstants.SLOT, slot);
        }

        map.put(AlarmConstants.TP, headPart);
        map.put(AlarmConstants.ORIGINAL, headPart);
        return map;
    }

    public static Map<String, String> parseCes(String headPart) {
        Map<String, String> map = new HashMap <>(4);
        String[] parts = headPart.split("-");
        String port ="";
        String slot ="";
        String shelf ="";
        for (int i = 1; i < parts.length; i++) {
            port += ("-" + parts[i]);
            if (i == 2) {
                shelf = port;
            }
            if( i == 3) {
                slot = port;
            }
            if (i == 4) {
                break;
            }
        }

        map.put(AlarmConstants.TP, port);
        map.put(AlarmConstants.SLOT, slot);
        map.put(AlarmConstants.SHELF, shelf);
        map.put(AlarmConstants.ORIGINAL, headPart);
        return map;
    }

    public static String getUsefulPartFromFriendlyName(String friendlyName) {
        String header = "";
        if (compare(friendlyName, ",")) {
            header = friendlyName.split(",")[0];
        } else {
            header = friendlyName;
        }

        if (compare(header, "/")) {
            if (compare(header, "System")) {
                return header.substring(0, header.indexOf("/"));
            }
            return header.substring(header.indexOf("/") + 1);
        }

        return header;
    }

    public static String getNeName(String friendlyName) {
        String header = "";
        if (compare(friendlyName, ",")) {
            header = friendlyName.split(",")[0];
        } else {
            header = friendlyName;
        }

        if (compare(header, "/")) {
            return header.substring(0, header.indexOf("/"));
        }

        return header;
    }

    public static int getIndexOfAppearNum(String str, String ch, int num) {
        Pattern pattern = Pattern.compile(ch);
        Matcher findMatcher = pattern.matcher(str);
        int number = 0;
        while (findMatcher.find()) {
            number++;
            if (number == num) {
                return findMatcher.start();
            }
        }

        return -1;
    }

    public static String parseSlotStr(String tpName) {
        return tpName.replace("sr","-")
                .replace("sl","-")
                .replace("r","-");
    }

    public static String parseCesTp(String cesOrgName) {
        String[] cesParts = cesOrgName.split("-");
        return "r"+cesParts[1]+"sr"+cesParts[2]+"sl"+cesParts[3]+"/ETHLocPort#"+cesParts[4]+"#1";

    }

    public static AlarmRelatedInfo getAlarmRelatedInfo(Alarm alarm) throws Exception {
        AlarmRelatedInfo ali = RootCauseAlarmHelper.getInstance().getAlarmRelatedInfo(alarm);
        if (ali == null) {
            ali =  AlarmRelatedInfoHelper.getAlarmRelatedInfo(alarm);
            if (ali == null) {
                log.error("cannot find alarm related info: " + alarm.getFriendlyName() + " || " + alarm.getProbableCauseId());
            }
        }

        return ali;
    }

    public static String parseTp(String orgName) {
        /*if (compare(orgName, "ETHLocPort")) {
            return orgName;
        }*/
        for (int i = 0; i < unknownPtp.length; i++) {
            if (compare(orgName, unknownPtp[i])) {
                String tp = orgName.replace(unknownPtp[i], "ETHLocPort");
                tp += "#1";
                return tp;
            }
        }

        return orgName;
    }

    public static boolean compare(String s1, String s2) {
        if (s1 != null) {
            if (s2 != null) {
                return s1.contains(s2);
            }
        }

        return false;
    }
}
