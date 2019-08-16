package com.hcop.ptn.common.internal.xos.request;
/**
 * pmpData
 */
public class PmpCurrentData {
	
	private long retrievalTime;
	
	private String parameterName;

	private String unit;

	private String intervalStatus;

	private float value;
	
	private String objectId;
	

	private String objectName;
	
	private String objectType;

	private String objectValue;

	public PmpCurrentData(long retrievalTime, String parameterName,
			String unit, String intervalStatus, float value) {
		super();
		this.retrievalTime = retrievalTime;
		this.parameterName = parameterName;
		this.unit = unit;
		this.intervalStatus = intervalStatus;
		this.value = value;
	}
	
	public PmpCurrentData() {
		
	}

	public long getRetrievalTime() {
		return retrievalTime;
	}

	public String getParameterName() {
		return parameterName;
	}
	
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getUnit() {
		return unit;
	}

	public String getIntervalStatus() {
		return intervalStatus;
	}

	public float getValue() {
		return value;
	}
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectValue() {
		return objectValue;
	}

	public void setObjectValue(String objectValue) {
		this.objectValue = objectValue;
	}
	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

}
