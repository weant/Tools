package com.otn.tool.common.enums;

import com.otn.tool.common.utils.I18N;

public enum Period {
    MINUTE,
    HOUR,
    DAY,
    WEEK,
    MONTH,
    QUARTER,
    YEAR;

    public static Period fromInt(int value){
        switch(value){
            case 0: return MINUTE;
            case 1: return HOUR;
            case 2: return DAY;
            case 3: return WEEK;
            case 4: return MONTH;
            case 5: return QUARTER;
            case 6: return YEAR;
            default: return DAY;
        }
    }

    public int toValue(){
        switch(this){
            case MINUTE: return 0;
            case HOUR: return 1;
            case DAY: return 2;
            case WEEK: return 3;
            case MONTH: return 4;
            case QUARTER: return 5;
            case YEAR: return 6;
            default: return -1;
        }
    }

    public String getValueString(){
        switch(this){
            case MINUTE: return I18N.getInstance().getString("tool.minutes");
            case HOUR: return I18N.getInstance().getString("tool.hours");
            case DAY: return I18N.getInstance().getString("tool.days");
            case WEEK: return I18N.getInstance().getString("tool.week");
            case MONTH: return I18N.getInstance().getString("tool.month");
            case QUARTER: return I18N.getInstance().getString("tool.quarter");
            case YEAR: return I18N.getInstance().getString("tool.year");
            default: return "";
        }
    }

    public String toString(){
        return this.getValueString();
    }
}
