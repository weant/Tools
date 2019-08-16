package com.hcop.ptn.alarm;

import com.hcop.ptn.common.beans.AlarmLevel;
import com.hcop.ptn.common.db.dao.AlarmDao;

import java.util.List;

public class AlarmHandler {
    public boolean isRaise(AlarmBean alarm) {
        return !(isDeleted(alarm)|| isCleared(alarm)||alarm.isAckRelatedOperation()||alarm.isReserveRelatedOperation()||alarm.isOther());
    }

    public boolean isDeleted(AlarmBean alarm) {
        if (alarm.getClearableStatusObj() == null
                && alarm.getExpirationStatus() == null
                && alarm.getAlarmType() == null
                && alarm.getFriendlyName() == null) {
            return true;
        }

        if (alarm.getAlarmType().equalsIgnoreCase("alarmDeletion")) {
            return true;
        }
        return alarm.getEventType().equalsIgnoreCase("alarmDeletion");
    }

    public boolean isCleared(AlarmBean alarm) {
        //return AlarmLevel.CLEARED.equals(alarm.getAlarmLevel());
        if(AlarmLevel.CLEARED.equals(alarm.getAlarmLevel())) {
            return true;
        }
        return alarm.getEventType().equals("alarmDeletion");
    }

    public boolean isAcked(AlarmBean alarm) {
        if(alarm.isAckRelatedOperation()) {
            return alarm.getAcknowledgementStatusObj().value() == 1;
        }
        return false;
    }

    public void deleteAlarms(List<AlarmBean> alarmList) {
        AlarmDao.instance().delete(alarmList);
    }
}
