package com.hcop.ptn.alarm.notification;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * 告警的同步状态.包含了所有告警平台(EML,WDM)的状态
 *
 */
public class AlarmSynStatus {
	
	private static AlarmSynStatus status = new AlarmSynStatus();
	private AtomicBoolean  allOver = new AtomicBoolean(false);
	private AlarmSynStatus(){
		
	}
	public static AlarmSynStatus instance(){
		return status;
	}
	
	
	public boolean synOver(){
		return allOver.get();
	}
	
	
	public void setSynOver(boolean synOver){
		allOver.compareAndSet(false, synOver);
	}

}
