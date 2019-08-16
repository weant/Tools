package com.hcop.ptn.alarm;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RestartSyncAllAlarmJob implements Job{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		ReSyncAlarm.instance().execute();
	}

}
