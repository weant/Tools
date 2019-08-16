/**
 * FileName: NotificationAlarmHandler
 * Author:   Administrator
 * Date:     2019/3/31 18:30
 * Description:
 */
package com.hcop.ptn.alarm.rootcause;

import com.hcop.ptn.alarm.rootcause.common.AlarmQueue;
import com.hcop.ptn.alarm.rootcause.common.RootCauseAlarmHelper;
import com.hcop.ptn.alarm.event.AlarmAnalyzeImpl;
import com.hcop.ptn.restful.model.Alarm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

public class NotificationAlarmHandler implements Runnable {
    private static Log log = LogFactory.getLog(NotificationAlarmHandler.class);
    private BlockingQueue<Alarm> alarmQueue = AlarmQueue.getInstance().getAlarmQueue();
    @Override
    public void run() {
        while(true) {
            try {
                if (!AlarmAnalyzeImpl.getInstance().isFinished()) {
                    Thread.sleep(1000);
                    continue;
                }

                Alarm alarm = alarmQueue.take();

                CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(2000);

                        log.info("begin to analyze root cause for alarm: " + alarm.getFriendlyName() + " || " + alarm.getProbableCause());
                        List <Alarm> rootCause = AlarmAnalyzer.analyze(alarm);
                        RootCauseAlarmHelper.getInstance().setRootCause(rootCause, alarm.getEventType().equals("alarmCreation"));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        try {
                            throw e;
                        } catch (Exception e1) {
                            log.error(e1.getStackTrace());
                        }
                    }
                    return "DONE";
                });
                /*.whenComplete((v, e) -> {
                    if (v != null) {
                        // 处理返回结果

                    } else if (e != null) {
                        // 处理返回异常

                    }
                });*/
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
