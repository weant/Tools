package com.hcop.ptn.alarm;

import com.hcop.ptn.common.jcorba.AsConnector;
import com.hcop.ptn.common.properties.Conf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReSyncAlarm {
	private Log log = LogFactory.getLog(ReSyncAlarm.class);

	private AlarmReceiver alarmReceiver;

	private static ReSyncAlarm task = new ReSyncAlarm();

	private ReSyncAlarm() {
	}

	public static ReSyncAlarm instance() {
		return task;
	}

	public void setAlarmReceiver(AlarmReceiver alarmReceiver) {
		this.alarmReceiver = alarmReceiver;
	}

	public void execute() {
		// alarmReceiver.getStpe().shutdown();
		for (AsConnector conn : alarmReceiver.getMgrServerMap().keySet()) {
			synchronized (conn) {
				log.debug("Begin to sync alarm for " + conn.getNmsName());
				Long lastUpdateAlarmTime = alarmReceiver.getLastAlarmUpdateTimeMap().get(conn.getNmsName());
				if (lastUpdateAlarmTime == null) {
					continue;
				}
				Long currentTime = System.currentTimeMillis();
				Long reSyncTimeThreshold = Long.valueOf(Conf.instance().getIntConf("reSyncTimeThresholdHour", "6") * 60 * 60000);

				if ((currentTime - lastUpdateAlarmTime) > reSyncTimeThreshold) {
					alarmReceiver.listenToSingleAlarmServer(conn);
				}
			}
		}
	}
}
