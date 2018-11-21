package com.otn.tool.common.enums;

import com.otn.tool.common.utils.I18N;

public enum TaskState {
    IDLE,
    RUNNING;

    public static TaskState fromInt(int index) {
        switch(index){
            case 0:
                return IDLE;
            case 1:
                return RUNNING;
            default:
                return IDLE;
        }
    }

    public static TaskState fromString(String name) {
        if ("IDLE".equalsIgnoreCase(name)) {
            return TaskState.IDLE;
        } else if ("RUNNING".equalsIgnoreCase(name)) {
            return TaskState.RUNNING;
        }

        return TaskState.IDLE;
    }

    public String getValueString(){
        switch(this){
            case IDLE: return I18N.getInstance().getString("tool.task.group.idle");
            case RUNNING: return I18N.getInstance().getString("tool.task.group.running");
            default: return "";
        }
    }
}
