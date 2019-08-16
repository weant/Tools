package com.hcop.ptn.alarm.rootcause.common;

import com.hcop.ptn.common.utils.StringUtils;

public enum AlarmCategoryEnum {
    UNKNOWN,
    TIMING,
    SHELF,
    SLOT,
    NE,
    TUNNEL,
    PW,
    CES,
    PTP,
    EVC,
    SECTION,
    EQUIPMENT;

    public static AlarmCategoryEnum fromString(String s) {
        if(StringUtils.isEmpty(s)) {
            return UNKNOWN;
        }

        switch (s.trim().toUpperCase()) {
            case "NE":
                return NE;
            case "TUNNEL":
                return TUNNEL;
            case "PW":
                return PW;
            case "CES":
                return CES;
            case "PTP":
                return PTP;
            case "SECTION":
                return SECTION;
            case "EVC":
                return EVC;
            case "SHELF":
                return SHELF;
            case "SLOT":
                return SLOT;
            case "TIMING":
                return TIMING;
            case "EQUIPMENT":
                return EQUIPMENT;
            default:
                return UNKNOWN;
        }
    }

    @Override
    public String toString(){
        switch (this) {
            case NE:
                return "NE";
            case TUNNEL:
                return "TUNNEL";
            case PW:
                return "PW";
            case CES:
                return "CES";
            case PTP:
                return "PTP";
            case SECTION:
                return "SECTION";
            case SHELF:
                return "SHELF";
            case EVC:
                return "EVC";
            case SLOT:
                return "SLOT";
            case TIMING:
                return "TIMING";
            case EQUIPMENT:
                return "EQUIPMENT";
            default:
                return "UNKNOWN";
        }
    }
}
