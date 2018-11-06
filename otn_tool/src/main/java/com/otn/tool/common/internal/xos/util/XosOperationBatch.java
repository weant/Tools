package com.otn.tool.common.internal.xos.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class XosOperationBatch {
	
	private static Log log = LogFactory.getLog(XosOperationBatch.class);
	private ExecutorService service = Executors.newFixedThreadPool(5);
	
	/**
	 * 
	 * @param params
	 */
	public void tl1Request(List<XosOperParameter> params){
		tl1Request(params, false);
	}
	/**
	 * 
	 * @param params
	 * @param isSingleData 是否是单次数据
	 * true:是单次数据，操作时候先在数据库中获取当日数据，
	 * 					若数据库中没有数据则发送tl1命令，并写入到数据库中
	 * false:非单次数据，仅发送TL1命令
	 */
	public void tl1Request(List<XosOperParameter> params, boolean isSingleData){
		List<Callable<Void>> calls = new ArrayList<Callable<Void>>();
		log.debug("params.size()" + params.size());
		for (XosOperParameter par : params) {
			if(par == null) continue;
			XosOperationBatchThread opth = new XosOperationBatchThread(par, isSingleData);
			calls.add(opth);
		}
		try {
			service.invokeAll(calls);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			log.error("concurrent TL1 error", e);
			e.printStackTrace();
		}
	}
}

	