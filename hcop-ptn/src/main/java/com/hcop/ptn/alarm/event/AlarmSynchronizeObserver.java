package com.hcop.ptn.alarm.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;

public class AlarmSynchronizeObserver {
	private static AlarmSynchronizeObserver obs = new AlarmSynchronizeObserver();
	
	private ReentrantLock lock = new ReentrantLock();
	
	private List<AlarmSynchronizeListener> listeners = new ArrayList<>();
	
	private boolean isSynOver = false;
	
	private AlarmSynchronizeObserver() {
		listeners.add(AlarmAnalyzeImpl.getInstance());
	}
	
	public static AlarmSynchronizeObserver instance() {
		return obs;
	}
	
	public  void addAlarmListener(AlarmSynchronizeListener listener){
		try {
			lock.lockInterruptibly();
			this.listeners.add(listener);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
		
	}
	
	public boolean removeAlarmListener(AlarmSynchronizeListener listener){
		try {
			lock.lockInterruptibly();
			return this.listeners.remove(listener);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
		return false;
	}
	
	public void notifyAlarmEvent() {
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				List<AlarmSynchronizeListener> listenerList = getListeners();
				for(AlarmSynchronizeListener listener:listenerList) {
					listener.alarmNotify();
				}
			}}
		);
	}
	
	public void synOverAlarmEvent() {
		isSynOver = true;
		List<AlarmSynchronizeListener> listenerList = getListeners();
		for(AlarmSynchronizeListener listener:listenerList) {
			listener.alarmSynOver();
		}
	}
	
	private List<AlarmSynchronizeListener> getListeners(){
		List<AlarmSynchronizeListener> ret = new ArrayList<>();
		try {
			lock.lockInterruptibly();
			ret.addAll(this.listeners);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
		return ret;
	}
	
	public boolean isSynOver() {
		return isSynOver;
	}
	
	public void setSynOver(boolean synOver) {
		this.isSynOver = synOver;
	}
}
