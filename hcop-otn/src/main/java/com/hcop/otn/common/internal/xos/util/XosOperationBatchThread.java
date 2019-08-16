package com.hcop.otn.common.internal.xos.util;

import com.hcop.otn.common.db.tool.ToolDbUtil;
import com.hcop.otn.common.internal.xos.tl1.request.TL1Request;
import com.hcop.otn.common.internal.xos.tl1.response.TL1Response;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * 进行多线程发送TL1命令操作
 *
 */
public class XosOperationBatchThread implements Callable<Void>{
	private XosOperParameter param = null;
	private long threadid;
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 是否是单次数据
	 * true:是单次数据，操作时候先在数据库中获取当日数据，
	 * 					若数据库中没有数据则发送tl1命令，并写入到数据库中
	 * false:非单次数据，仅发送TL1命令
	 */
	private boolean isSingleData = false;
	
	public XosOperationBatchThread(XosOperParameter param){
		this.param = param;
	}
	
	public XosOperationBatchThread(XosOperParameter param, boolean isSingleData){
		this.param = param;
		this.isSingleData = isSingleData; 
	}
	
	
	@Override
	public Void call() throws Exception {
		// TODO Auto-generated method stub
		run();
		return null;
	}
	
	public void run() {
		try {
			threadid = Thread.currentThread().getId();
			param.getLogs().add(getlogHead() + "start execute TL1 thread");
			if(isSingleData) {
				param.getLogs().add(getlogHead() + " send TL1 check in db");
				Map tl1sch = queryTL1();
				//结果不存在则发送TL1命令，并记录本次发送结果
				if(tl1sch == null || tl1sch.get("response") == null ||
						tl1sch.get("response").toString().trim().equals("")) {
					
					param.getLogs().add(getlogHead() + "send TL1, neid[" + param.getNeID() + "] groupID[" + 
							param.getGroupID() + "] Aid[" + param.getAid() + "] Tl1Constants[" + param.getTl1Constants() + "]");
					TL1Response response = null;
					if(param.getAid() == null || param.getAid().trim().equals("")) {
						response = XosOperation.tl1Request(param.getGroupID(), param.getNeID(), 
								new TL1Request(param.getTl1Constants()));
					}else {
						response = XosOperation.tl1Request(param.getGroupID(), param.getNeID(), 
								new TL1Request(param.getTl1Constants(), param.getAid()));
					}
					this.param.setResponse(response == null ? "" : response.toString());
					param.getLogs().add(getlogHead() + "send finish TL1");
					saveTL1sch(tl1sch);
				} else { //数据库中存在结果则直接返回结果
					param.getLogs().add(getlogHead() + " there is TL1 info in db, return the info");
					this.param.setResponse(tl1sch.get("response").toString());
				}
				
			}else {
				param.getLogs().add(getlogHead() + " send TL1 command and don't check db");
				TL1Response response = XosOperation.tl1Request(param.getGroupID(), param.getNeID(), 
						new TL1Request(param.getTl1Constants(), param.getAid()));
				this.param.setResponse(response.toString());
			}
			param.getLogs().add(getlogHead() + "Thread execute finish");
		}catch(Exception e) {
			this.param.setTl1Exception(e);
		}
	}
	
	/**
	 * 保存TL1信息
	 * @param tl1sch
	 */
	private void saveTL1sch(Map tl1sch) {
		if(tl1sch == null || tl1sch.get("response") == null) {
			if(tl1sch != null && tl1sch.get("id") != null) {
				tl1sch.put("response", param.getResponse());
				tl1sch.put("tl1constants",param.getTl1Constants());
				updateTL1(tl1sch);
			}else {
				tl1sch = new HashMap();
				tl1sch.put("groupid", param.getGroupID());
				tl1sch.put("emlneid", param.getNeID());
				tl1sch.put("equipaid", param.getAid());
				tl1sch.put("exetime", new Date().getTime());
				tl1sch.put("response", param.getResponse());
				tl1sch.put("tl1constant", param.getTl1Constants());
				tl1sch.put("id", new Date().getTime());
				insertTL1(tl1sch);
			}
		}
	}
	
	private String getlogHead() {
		String logHead = threadid + " " + format.format(new Date()) + " >> ";
		return logHead;
	}
	
	private void insertTL1(Map tl1sch) {
		param.getLogs().add(getlogHead() + "insert data");
		String sql = "insert into TL1Schedule(id,groupid,emlNeId,equipaId,exeTime,response,tl1Constant) values(?,?,?,?,?,?,?) ";
		Object[] param = new Object[] {
				tl1sch.get("id"),tl1sch.get("groupid"),tl1sch.get("emlneid"),tl1sch.get("equipaid"),tl1sch.get("exetime"),
				tl1sch.get("response"),tl1sch.get("tl1constant")
		};
		try {
			ToolDbUtil.instance().execute(sql, param);
		} catch (Exception e) {
			this.param.getLogs().add(getlogHead() + "insert oper exception:" + e.getMessage());
		}
	}
	
	private void updateTL1(Map tl1sch) {
		param.getLogs().add(getlogHead() + "update data");
		String sql = "update TL1Schedule set groupid=?,emlneid=?,equipaid=?,exetime=?,response=?,tl1constant=? where id=?";
		Object[] param = new Object[] {
			tl1sch.get("groupid"),tl1sch.get("emlneid"),tl1sch.get("equipaid"),tl1sch.get("exetime"),
			tl1sch.get("response"),tl1sch.get("tl1constant"),tl1sch.get("id")
		};
		
		try {
			ToolDbUtil.instance().execute(sql, param);
		} catch (Exception e) {
			this.param.getLogs().add(getlogHead() + "update oper exception:" + e.getMessage());
		}
	}
	
	private Map queryTL1(){
		//获取时间为当前时间之前一小时的时间
		Calendar before1hour = Calendar.getInstance();
		before1hour.add(Calendar.HOUR, -1);
		
		//获取开始和结束时间，开始结束时间之间间隔1小时
		Date endTime = before1hour.getTime();
		Date startTime = new Date();
		
		param.getLogs().add(getlogHead() + "query TL1p");
		String sql = "select id,groupid,emlneid,equipaid,exetime,response,tl1constant " +
				" from TL1Schedule where exeTime > " + startTime.getTime() + " and exeTime < " + endTime.getTime()  + 
			" and groupid=" + param.getGroupID() + " and emlNeId=" + param.getNeID() + 
			" and equipaId='" + param.getAid() + "' and tl1Constant='" + param.getTl1Constants() + "'";
		
		try {
			List<HashMap<Object, Object>> result = ToolDbUtil.instance().query(sql);
			if(result != null && result.size() > 0)
				return result.get(0);
		} catch (Exception e) {
			this.param.getLogs().add(getlogHead() + "query TL1p exception:" + e.getMessage());
		}
		return null;
	}

	
}
