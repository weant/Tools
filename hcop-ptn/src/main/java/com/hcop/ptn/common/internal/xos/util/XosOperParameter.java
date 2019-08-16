package com.hcop.ptn.common.internal.xos.util;

import java.util.ArrayList;
import java.util.List;


public class XosOperParameter {

	private int groupID;
	private int neID;
	private String aid;
	private String tl1Constants;
//	private TL1Request request;
	private String response;
	private Exception tl1Exception;
	private List<String> logs;
	
	public List<String> getLogs() {
		if(this.logs == null) {
			logs = new ArrayList<String>();
		}
		return logs;
	}
	public void setLogs(List<String> logs) {
		this.logs = logs;
	}
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public String getTl1Constants() {
		return tl1Constants;
	}
	public void setTl1Constants(String tl1Constants) {
		this.tl1Constants = tl1Constants;
	}
	public Exception getTl1Exception() {
		return tl1Exception;
	}
	public void setTl1Exception(Exception tl1Exception) {
		this.tl1Exception = tl1Exception;
	}
	public int getGroupID() {
		return groupID;
	}
	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}
	public int getNeID() {
		return neID;
	}
	public void setNeID(int neID) {
		this.neID = neID;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
}
