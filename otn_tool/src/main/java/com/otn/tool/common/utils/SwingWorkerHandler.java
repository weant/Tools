package com.otn.tool.common.utils;

/**
 * 用于SwingWorker的处理
 *
 */
public interface SwingWorkerHandler {
	
	/**
	 * doInBackground的实现方法
	 * 
	 */
	public Object backgroundRunnable(); 
	
	/**
	 * done的实现方法
	 * 
	 */
	public void doneRunnabel(Object values);
}