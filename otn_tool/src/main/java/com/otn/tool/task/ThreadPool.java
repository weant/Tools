package com.otn.tool.task;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.otn.tool.common.properties.Conf;

public class ThreadPool {
	
	private static ThreadPool pool = new ThreadPool();
	
	private ExecutorService threadPool;
	
	private ThreadPool() {
//		threadPool = Executors.newFixedThreadPool(SimpleAppConfMgr.instance().getIntConf("concurrencyItemNum"));
		/*
		 * 线程池中保留最小10个工作线程（可以修改到配置中），最大为concurrencyItemNum
		 */
		threadPool = new ThreadPoolExecutor(Integer.valueOf(Conf.instance().getProperty("task.thread.pool.core.size")), Integer.valueOf(Conf.instance().getProperty("task.thread.pool.max.size")),
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
	}
	
	public static ThreadPool instance() {
		return pool;
	}
	
	public void execute(Runnable thread)throws NullPointerException {
		if(thread == null) {
			throw new NullPointerException("thread object is null");
		}
		threadPool.execute(thread);
	}
	
	
	public  <T> FutureTask<T> submit(Callable<T> call){
		return (FutureTask<T>)threadPool.submit(call);
	}
	
	public void execute(LinkedList<Runnable> threads)throws NullPointerException  {
		for (Runnable runnable : threads) {
			execute(runnable);
		}
	}
	
	
}
