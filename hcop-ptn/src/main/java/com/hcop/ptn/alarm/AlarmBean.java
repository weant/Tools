package com.hcop.ptn.alarm;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.hcop.ptn.common.beans.AlarmLevel;
import org.omg.CosNaming.NameComponent;

import com.alcatel.almap.AlarmOSOSDefs.AcknowledgementStatus;
import com.alcatel.almap.AlarmOSOSDefs.AlarmAgingCondition;
import com.alcatel.almap.AlarmOSOSDefs.ClearableStatus;
import com.alcatel.almap.AlarmOSOSDefs.ClearingStatus;
import com.alcatel.almap.AlarmOSOSDefs.ReserveStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AlarmBean {
	private String alarmId = null;
	private String friendlyName = null;
	private String alarmType = null;
	private AlarmLevel alarmLevel = null;
	private String probableCause = null;
	private Timestamp occurTime = new Timestamp(System.currentTimeMillis());//由于从enms获取的alarm 可能不包含此字段,所以此处以当前时间为默认值
	//是否是eml告警
	private boolean eml = false;
	//是否是与ack相关的操作(包括 ack，unack)
	private boolean isAckRelatedOperation;
	//是否是对告警进行与reserve相关的操作
	private boolean isReserveRelatedOperation;
	private boolean isDeleted;
	private boolean isRaise;
	
	private String additionalInfo_type = "";
	private String additionalInfo_layerRate = "";
	private String addidtionlInfo_neid = "";
	
	private ClearingStatus clearingStatus;
	private AcknowledgementStatus acknowledgementStatus;
	private ReserveStatus reservationStatus;
	private ClearableStatus clearableStatus;
	private int repetitionCounter;
	private String managementObjectClass;
	private String nbi_inst;
	private long insertTime;
	private String alarmObjectType;
	private String alarmObject;
	private String objectId;
	
	private String neUserlabel;
	private String neId = "";
	private String enProbableCauseName;
	private String alarmHistoryComments = "";
	private AlarmAgingCondition[] expirationStatus;
	private String eventType = null;
	private long clearingTime;
    private boolean isOther;
    private String alarmRate;
    private NameComponent[] resourceId;
    private String systemName;
	
//	private String probableCauseName;
//	private String localname;
//	private String alarmstate;
//	private String comment1;
//	private String comment2;
	
	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	public boolean isEmlAlarm() {
		return eml;
	}

	@JsonProperty("nmsRaiseTime")
	public void setNmsRaiseTime(long nmsRaiseTime){
		if(nmsRaiseTime!=-1){
			this.occurTime = new Timestamp(nmsRaiseTime);
		}
	}
	@JsonProperty("alarmLevelType")
	public void setAlarmLevelType(String alarmLevel){
		this.eml = alarmLevel.equalsIgnoreCase("EML");
	}
	public void setEmlAlarm(boolean eml) {
		this.eml = eml;
	}

	public String getAlarmId() {
		return alarmId;
	}

	@JsonProperty("alarmID")
	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public String getObjectId() {
		return objectId;
	}

	@JsonProperty("objectId")
	public void setObjectID_(String objectId) {
		this.objectId = objectId;
	}

	public String getObjectLabel_() {
		return friendlyName;
	}

	@JsonProperty("objectLabel") 
	public void setObjectLabel_(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getAlarmType() {
		return alarmType;
	}
	
	@JsonProperty("eventType")
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	
	public AlarmLevel getAlarmLevel() {
		return alarmLevel;
	}
	
	public void setAlarmLevel(AlarmLevel alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	@JsonProperty("severity")
	public void setAlarmLevel(String severity){
		this.alarmLevel = AlarmLevel.valueOf(severity);
	}

	@JsonProperty("nativeProbableCause")   
	public void setProbableCause(String probableCause) {
		this.probableCause = probableCause;
	}

	public boolean isEml() {
		return eml;
	}

	public void setEml(boolean eml) {
		this.eml = eml;
	}
	
	@JsonProperty("additionalInfo")
	public void setProbableCause(LinkedList<Map<String, Object>> additionalInfos) {
		for (Iterator<Map<String, Object>> it = additionalInfos.iterator(); it.hasNext();) {
			Map<String, Object> map = it.next();
			Object key = map.get("key");
			if (key != null && key.toString().equals("alarmOID")) {
				this.probableCause = map.get("value").toString();
			} else if(key != null && key.toString().equals("neUserLabel")){
				this.neUserlabel = map.get("value").toString() + "/";
			}
		}
	}

    public boolean isOther() {
        return isOther;
    }

    public void setIsOther(boolean other) {
        this.isOther = other;
    }

    public AlarmBean(){
		
	}
	public AlarmBean(String alarmID, String objectLabel, String alarmType,
			String severity, String probableCause,
			 Long nmsRaiseTime,
			boolean isAckRelatedOperation, boolean isReserveRelatedOperation, boolean isDeleted, boolean isRaise,
			String additionalInfo_type, String additionalInfo_layerRate, String addidtionlInfo_neid, 
			String managementObjectClass, String nms, int repetitionCounter, AcknowledgementStatus acknowledgementStatus,
			ReserveStatus reservationStatus, ClearingStatus clearingStatus, ClearableStatus clearableStatus, 
			NameComponent[] resourceId, AlarmAgingCondition[] expirationStatus) {
		this.alarmId = alarmID;
		
		this.friendlyName = objectLabel;
		this.alarmType = alarmType;
		this.alarmLevel = (severity == null || "".equals(severity)) ? null : AlarmLevel.valueOf(severity);
		this.probableCause = probableCause;
		if(nmsRaiseTime>0) {
			occurTime = new Timestamp(nmsRaiseTime);
		}
		
		this.isAckRelatedOperation = isAckRelatedOperation;
		this.isReserveRelatedOperation = isReserveRelatedOperation;
		this.isDeleted = isDeleted;
		this.isRaise = isRaise;
		this.additionalInfo_type = additionalInfo_type;
		this.additionalInfo_layerRate = additionalInfo_layerRate;
		this.addidtionlInfo_neid = addidtionlInfo_neid;
		this.repetitionCounter = repetitionCounter;
		this.clearingStatus = clearingStatus;
		this.acknowledgementStatus = acknowledgementStatus;
		this.reservationStatus = reservationStatus;
		this.clearableStatus = clearableStatus;
		this.managementObjectClass = managementObjectClass;
		this.nbi_inst = nms;
		this.expirationStatus = expirationStatus;
	}

	public boolean isClearAlarm(){
		return alarmLevel == AlarmLevel.CLEARED;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public AlarmBean saveToDB() {
		return this;// no need to save to db
	}


	public String getProbableCause() {
		return probableCause;
	}

	public boolean isAckRelatedOperation() {
		return isAckRelatedOperation;
	}

	public void setAckRelatedOperation(boolean isAckRelatedOperation) {
		this.isAckRelatedOperation = isAckRelatedOperation;
	}

	public boolean isReserveRelatedOperation() {
		return isReserveRelatedOperation;
	}

	public void setReserveRelatedOperation(boolean isReserveRelatedOperation) {
		this.isReserveRelatedOperation = isReserveRelatedOperation;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public boolean isRaise() {
		return isRaise;
	}

	public void setRaise(boolean isRaise) {
		this.isRaise = isRaise;
	}

	public String getAdditionalInfo_type() {
		return additionalInfo_type;
	}

	public void setAdditionalInfo_type(String additionalInfo_type) {
		this.additionalInfo_type = additionalInfo_type;
	}

	public String getAdditionalInfo_layerRate() {
		return additionalInfo_layerRate;
	}

	public void setAdditionalInfo_layerRate(String additionalInfo_layerRate) {
		this.additionalInfo_layerRate = additionalInfo_layerRate;
	}

	public String getAddidtionlInfo_neid() {
		return addidtionlInfo_neid;
	}

	public void setAddidtionlInfo_neid(String addidtionlInfo_neid) {
		this.addidtionlInfo_neid = addidtionlInfo_neid;
	}
	
	public Timestamp getOccurTime() {
		return this.occurTime;
	}
	
	@JsonProperty("objectId")
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public int getClearingStatus() {
		if(clearingStatus == null) return clearingStatus._CLEARED;
		return clearingStatus.value();
	}

	public void setClearingStatus(int clearingStatus) {
		this.clearingStatus = ClearingStatus.from_int(clearingStatus);
	}

	public String getManagementObjectClass() {
		return managementObjectClass;
	}
	@JsonProperty("objectType")
	public void setManagementObjectClass(String managementObjectClass) {
		this.managementObjectClass = managementObjectClass;
	}

	public String getNbi_inst() {
		return nbi_inst;
	}

	public void setNbi_inst(String nbi_inst) {
		this.nbi_inst = nbi_inst;
	}

	public long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}

	public String getAlarmObjectType() {
		return alarmObjectType;
	}

	public void setAlarmObjectType(String alarmObjectType) {
		this.alarmObjectType = alarmObjectType;
	}

	public String getAlarmObject() {
		return alarmObject;
	}

	public void setAlarmObject(String alarmObject) {
		this.alarmObject = alarmObject;
	}
	
	public String getNeUserlabel(){
		return neUserlabel;
	}

//	public String getProbableCauseName() {
//		return probableCauseName;
//	}
//
//	public void setProbableCauseName(String probableCauseName) {
//		this.probableCauseName = probableCauseName;
//	}
//
//	public String getLocalname() {
//		return localname;
//	}
//
//	public void setLocalname(String localname) {
//		this.localname = localname;
//	}
//
//	public String getAlarmstate() {
//		return alarmstate;
//	}
//
//	public void setAlarmstate(String alarmstate) {
//		this.alarmstate = alarmstate;
//	}
//
//	public String getComment1() {
//		return comment1;
//	}
//
//	public void setComment1(String comment1) {
//		this.comment1 = comment1;
//	}
//
//	public String getComment2() {
//		return comment2;
//	}
//
//	public void setComment2(String comment2) {
//		this.comment2 = comment2;
//	}

	public int getAcknowledgementStatus() {
		if(acknowledgementStatus == null) {
			return acknowledgementStatus._UNACKNOWLEDGED;
		}
		return acknowledgementStatus.value();
	}

	public void setAcknowledgementStatus(int acknowledgementStatus) {
		this.acknowledgementStatus =AcknowledgementStatus.from_int(acknowledgementStatus);
	}

	public int getReservationStatus() {
		if(reservationStatus == null) {
			return reservationStatus._UNRESERVED;
		}
		return reservationStatus.value();
	}

	public void setReservationStatus(int reservationStatus) {
		this.reservationStatus = ReserveStatus.from_int(reservationStatus);
	}

	public int getClearableStatus() {
		if(clearableStatus == null) {
			return clearableStatus._NOTCLEARABLE;
		}
		return clearableStatus.value();
	}

	public void setClearableStatus(int clearableStatus) {
		this.clearableStatus = ClearableStatus.from_int(clearableStatus);
	}

	public int getRepetitionCounter() {
		return repetitionCounter;
	}

	public void setRepetitionCounter(int repetitionCounter) {
		this.repetitionCounter = repetitionCounter;
	}

	public void setOccurTime(Timestamp occurTime) {
		this.occurTime = occurTime;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public String getEnProbableCauseName() {
		return enProbableCauseName;
	}

	public void setEnProbableCauseName(String enProbableCauseName) {
		this.enProbableCauseName = enProbableCauseName;
	}

	public String getAlarmHistoryComments() {
		return alarmHistoryComments;
	}

	public void setAlarmHistoryComments(String alarmHistoryComments) {
		this.alarmHistoryComments = alarmHistoryComments;
	}
	
	public String getNEName()
	{
		if(alarmObject != null)
		{
			return alarmObject.split("/")[0];
		}
		return null;
	}

	public NameComponent[] getResourceId() {
		return resourceId;
	}

	public void setResourceId(NameComponent[] resourceId) {
		this.resourceId = resourceId;
	}
	
	public ClearingStatus getClearingStatusObj() {
		return clearingStatus;
	}
	
	public AcknowledgementStatus getAcknowledgementStatusObj() {
		return acknowledgementStatus;
	}
	
	public ReserveStatus getReserveStatusObj() {
		return reservationStatus;
	}
	
	public ClearableStatus getClearableStatusObj() {
		return clearableStatus;
	}

	public AlarmAgingCondition[] getExpirationStatus() {
		return expirationStatus;
	}

	public void setExpirationStatus(AlarmAgingCondition[] expirationStatus) {
		this.expirationStatus = expirationStatus;
	}
	
	public long getClearingTime() {
		return clearingTime;
	}

	public void setClearingTime(long clearingTime) {
		this.clearingTime = clearingTime;
	}

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
}
