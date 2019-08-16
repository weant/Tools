/**
 * FileName: AlarmQueue
 * Author:   Administrator
 * Date:     2019/3/31 17:58
 * Description:
 */
package com.hcop.ptn.alarm.rootcause.common;

import com.hcop.ptn.restful.model.Alarm;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AlarmQueue {
    private BlockingQueue<Alarm> alarmQueue = new LinkedBlockingQueue<>();

    private static AlarmQueue instance = new AlarmQueue();

    private AlarmQueue () {}

    public static AlarmQueue getInstance() {
        return instance;
    }

    public BlockingQueue<Alarm> getAlarmQueue() {
        return alarmQueue;
    }
}
