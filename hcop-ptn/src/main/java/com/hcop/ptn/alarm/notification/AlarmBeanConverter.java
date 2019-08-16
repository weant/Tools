package com.hcop.ptn.alarm.notification;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.hcop.ptn.alarm.AlarmBean;
import com.hcop.ptn.alarm.rootcause.common.TssNeHelper;
import com.hcop.ptn.common.beans.AlarmLevel;
import com.hcop.ptn.common.properties.AlarmFilterConf;
import com.hcop.ptn.common.utils.TimeUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.StructuredEvent;

import com.alcatel.almap.AlarmOSOSDefs.AcknowledgementStatus;
import com.alcatel.almap.AlarmOSOSDefs.AcknowledgementStatusHelper;
import com.alcatel.almap.AlarmOSOSDefs.AlarmAgingCondition;
import com.alcatel.almap.AlarmOSOSDefs.ClearableStatus;
import com.alcatel.almap.AlarmOSOSDefs.ClearableStatusHelper;
import com.alcatel.almap.AlarmOSOSDefs.ClearingStatus;
import com.alcatel.almap.AlarmOSOSDefs.ClearingStatusHelper;
import com.alcatel.almap.AlarmOSOSDefs.ExpirationStatusHelper;
import com.alcatel.almap.AlarmOSOSDefs.ReserveStatus;
import com.alcatel.almap.AlarmOSOSDefs.ReserveStatusHelper;
import com.alcatel.almap.GlobalDefs.AlarmStatusHelper;
import com.alcatel.almap.GlobalDefs.ResourceIdHelper;
import com.alcatel.almap.ITU_X721.AdditionalInformationHelper;
import com.alcatel.almap.ITU_X721.ManagementExtensionType;

public class AlarmBeanConverter {
	private static Log LOG = LogFactory.getLog(Object.class);
	private static List<String> alarmFilters = Arrays.asList(AlarmFilterConf.instance().getProperty("NOT_PTN_ALARM_TYPES").split(","));

	public static AlarmBean convert(String nms, StructuredEvent event, Boolean isNotif) {
		boolean isEml = false;
        long clearingTime = 0L;
        String alarmID = "";
        String alarmName = null;
        String alarmTypeStr = "NONE";
        String severity = "";
        String probableCause = "";
        String probableCauseGroup = "";
        long nmsRaiseTime = 0L;
        String npc = "NONE";
        boolean isClearingAlarm = false;
        //alarmUpdate,alarmDeletion,alarmCreation
        String alarmOperateStr=event.header.fixed_header.event_type.type_name;
        ClearingStatus clearingStatus = null;
        String additionalInfo_type = "";
        String additionalInfo_layerRate = "";
        String addidtionlInfo_neid = "";
        AcknowledgementStatus acknowledgementStatus = null;
        ReserveStatus reservationStatus = null;
        ClearableStatus clearableStatus = null;
        AlarmAgingCondition[] expirationStatus = null;

        int repetitionCounter = 0;

        boolean isAckRelatedOperation;
        boolean isReserveRelatedOperation;
        boolean isDeleted;
        boolean isRaise;
        //boolean isUpdate;

        NameComponent[] resourceId = null;

        String managementObjectClass = null;

        String eventType = event.header.fixed_header.event_type.type_name;

        for (Property data : event.filterable_data) {
            if (data.name.equals("currentAlarmId")) {
                alarmID = String.valueOf(data.value.extract_long());
                continue;
            }
            if (data.name.equalsIgnoreCase("friendlyName")) {
                try {
                    alarmName = new String(data.value.extract_string().getBytes("ISO8859_1") , "UTF-8");
                } catch (BAD_OPERATION | UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    LOG.error(e.getStackTrace());
                }

                if (nms.contains("EML") && !TssNeHelper.getInstance().isTssNeAlarm(alarmName)) {
                	LOG.error("alarm is not Tss ne alarm: " + alarmName);
					return null;
				}
                continue;
            }
            if (data.name.equalsIgnoreCase("resourceId")) {// ?
                resourceId = ResourceIdHelper.read(data.value.create_input_stream());
                continue;
            }

            if (data.name.equalsIgnoreCase("perceivedSeverity")) {// ?
                severity = severityConvert(AlarmStatusHelper.read(data.value.create_input_stream()).value());
                continue;
            }

            if (data.name.equalsIgnoreCase("probableCause")) {
                probableCause = data.value.extract_string();
                continue;
            }
            if (data.name.equalsIgnoreCase("alarmType")) {
                alarmTypeStr = convertToType(data.value.extract_string());
                continue;
            }
		/*	if (data.name.equalsIgnoreCase("clearableStatus")) {

				clearableStatus = ClearableStatusHelper.read(data.value.create_input_stream());
				continue;

			}*/
            if (data.name.equalsIgnoreCase("clearingStatus")) {

                clearingStatus = ClearingStatusHelper.read(data.value.create_input_stream());
                continue;
            }
            if (data.name.equals("acknowledgementStatus")) {
                acknowledgementStatus = AcknowledgementStatusHelper.read(data.value.create_input_stream());
                continue;
            }
            if(data.name.equals("reservationStatus")){
                reservationStatus = ReserveStatusHelper.read(data.value.create_input_stream());
                continue;
            }
            if(data.name.equals("clearableStatus")){
                clearableStatus = ClearableStatusHelper.read(data.value.create_input_stream());
                continue;
            }
            if(data.name.equals("expirationStatus")) {
                expirationStatus = ExpirationStatusHelper.read(data.value.create_input_stream());
                continue;
            }
            if (data.name.equals("resourceClass")) {
                managementObjectClass = data.value.extract_string();
                if (alarmFilters.contains(managementObjectClass)) {
					//LOG.error("managementObjectClass ancorrect: " + managementObjectClass);
                	return null;
				}
                continue;
            }
            if(data.name.equals("repetitionCounter")){
                repetitionCounter =  data.value.extract_long();
                continue;
            }
		/*	if (data.name.equalsIgnoreCase("expirationStatus")) {

				expirationStatus = ExpirationStatusHelper.read(data.value.create_input_stream());
				continue;
			}*/

            if (data.name.equalsIgnoreCase("clearingTime")) {
                String timeStr = data.value.extract_string();
                int index = timeStr.indexOf(".");
                if (index == -1) {
                    index = timeStr.indexOf("Z");
                }
                // 该时间是GMT标准时间，需要转换为locale的时间
                timeStr = timeStr.substring(0, index);
                Date time = TimeUtilities.instance().parse("yyyyMMddHHmmss", timeStr);
                clearingTime = time.getTime() + TimeZone.getDefault().getRawOffset();

                continue;
            }

            if (data.name.equalsIgnoreCase("eventTime")) {
                String timeStr = data.value.extract_string();
                int index = timeStr.indexOf(".");
                if (index == -1) {
                    index = timeStr.indexOf("Z");
                }
                // 该时间是GMT标准时间，需要转换为locale的时间
                timeStr = timeStr.substring(0, index);
                Date time = TimeUtilities.instance().parse("yyyyMMddHHmmss", timeStr);
                nmsRaiseTime = time.getTime() + TimeZone.getDefault().getRawOffset();

                continue;
            }

            if (data.name.equalsIgnoreCase("additionalInformation")) {

                ManagementExtensionType[] mets = AdditionalInformationHelper.read(data.value.create_input_stream());

                if (getValueByIdInMETs(mets, "1.3.6.1.4.1.12.2.2.20") == null) {// eml
                    isEml = true;
                    addidtionlInfo_neid = getAdditionalInfo_neid(mets);
                    additionalInfo_layerRate = getAdditionalInfo_layerRate(mets);
                    additionalInfo_type = getAdditionalInfo_type(mets);
                } else {// nml
                    isEml = false;
                    // 有可能是EML网管的告警（RMObjectType=eml）,此告警应作为根告警，且无衍生告警

                }

                continue;
            }

        }

        isClearingAlarm = isClearAlarm(alarmOperateStr, clearingStatus, alarmName, clearingTime, severity);

        isAckRelatedOperation = isAckRelatedOperationAlarm(acknowledgementStatus, alarmName);

        isReserveRelatedOperation = isReserveRelatedOperationAlarm(reservationStatus, alarmName);

        isDeleted = isDeletedAlarm(clearableStatus, expirationStatus, alarmTypeStr, alarmName, eventType, severity);

		if(isClearingAlarm){
			severity = AlarmLevel.CLEARED.toString();
			eventType = "alarmDeletion";
		}

		boolean isOther = isOtherAlarm(severity);
		//isUpdate = isUpdatedAlarm(eventType, severity);

		isRaise = isRaiseAlarm(isClearingAlarm, isAckRelatedOperation, isReserveRelatedOperation, isDeleted, isOther);

//		nms = getNbi_Inst(isEml, nms);

		if ("NONE".equals(probableCauseGroup) && isEml && !isClearingAlarm) {
			if(LOG.isWarnEnabled()){
				LOG.warn("unsupported npc '" + npc + "', alarmName='" + alarmName + "'");
			}

		}
		if (isNotif != null && isNotif) {
			LOG.info("received alarm info:"+"--alarmID:"+alarmID+" --alarmName:"+alarmName+" --alarmTypeStr:"+alarmTypeStr+
					" --severity:"+severity+" --probableCause:"+probableCause+" --nmsRaiseTime:"+nmsRaiseTime+" --isAckRelatedOperation:"+isAckRelatedOperation+
					" --isReserveRelatedOperation:"+isReserveRelatedOperation+" --isDeleted:"+isDeleted+" --isRaise:"+isRaise+ " --additionalInfo_type:"+additionalInfo_type+
					" --additionalInfo_layerRate:"+additionalInfo_layerRate+" --addidtionlInfo_neid:"+addidtionlInfo_neid+" --managementObjectClass:"+managementObjectClass+
					" --nms:"+nms+" --repetitionCounter:"+repetitionCounter+" --acknowledgementStatus:"+acknowledgementStatus+" --reservationStatus:"+reservationStatus+
					" --clearingStatus:"+clearingStatus+" --clearableStatus:"+clearableStatus+" --resourceId:"+resourceId+" --expirationStatus:"+expirationStatus);
		}
		AlarmBean alarm = new AlarmBean(alarmID, alarmName, alarmTypeStr, severity,
				probableCause, nmsRaiseTime,
				isAckRelatedOperation, isReserveRelatedOperation, isDeleted, isRaise,
				additionalInfo_type, additionalInfo_layerRate, addidtionlInfo_neid,
				managementObjectClass, nms, repetitionCounter, acknowledgementStatus, reservationStatus,
				clearingStatus, clearableStatus, resourceId, expirationStatus);
		alarm.setEmlAlarm(isEml);
		alarm.setEventType(eventType);
		alarm.setIsOther(isOther);
		return alarm;
	}

	public static AlarmBean convert(String nms, StructuredEvent event) {
		return convert(nms, event, null);
	}

	private static String getNbi_Inst(String addidtionlInfo_neid, String nms) {
		if (nms != null) {
			String[] nmsArr = nms.split("_");
			if (nmsArr.length > 1) {
				if (addidtionlInfo_neid != null && !"null".equals(addidtionlInfo_neid)) {
					nms = "EML_"+nmsArr[1];
				} else {
					nms = "WDM_"+nmsArr[1];
				}
			}
		}

		return nms;
	}
	
	private static String getNbi_Inst(boolean isEml, String nms) {
		if (nms != null) {
			String[] nmsArr = nms.split("_");
			if (nmsArr.length > 1) {
				if (isEml) {
					nms = "EML_"+nmsArr[1];
				} else {
					nms = "WDM_"+nmsArr[1];
				}
			}
		}

		return nms;
	}

	private static boolean isOtherAlarm(String severity) {
		if (severity == null || severity.trim().isEmpty()) {
			return true;
		}
		return false;
	}

	private static boolean isRaiseAlarm(boolean isClearingAlarm, boolean isAckRelatedOperation,
			boolean isReserveRelatedOperation, boolean isDeleted, boolean isOther) {
		return !isClearingAlarm && !isAckRelatedOperation && !isReserveRelatedOperation && !isDeleted && !isOther;
	}

	private static boolean isDeletedAlarm(ClearableStatus clearableStatus, AlarmAgingCondition[] expirationStatus,
			String alarmTypeStr, String alarmName, String eventType, String severity) {
        if(clearableStatus == null && expirationStatus == null && alarmTypeStr == null && alarmName == null) {
            return true;
        }
        if("CLEARED".equals(severity)) {
        	return true;
		}
        return eventType.equalsIgnoreCase("alarmDeletion");
	}

	private static boolean isReserveRelatedOperationAlarm(ReserveStatus reservationStatus, String alarmName) {
        return reservationStatus != null && alarmName == null;
	}

	private static boolean isAckRelatedOperationAlarm(AcknowledgementStatus acknowledgementStatus, String alarmName) {
        return acknowledgementStatus != null && alarmName == null;
	}

	private static boolean isUpdatedAlarm(String eventType, String severity) {
	    if("alarmUpdate".equalsIgnoreCase(eventType) &&  !"CLEARED".equals(severity)) {
            return true;
        }
        return false;
    }

    private static boolean isClearAlarm(String alarmTypeStr, ClearingStatus clearingStatus, String alarmName, long clearingTime, String severity) {
        if (clearingStatus == null  && alarmTypeStr == null
                && alarmName == null) {
            return true;
        }
        if(clearingStatus == ClearingStatus.CLEARED) {
            return true;
        }
        if("CLEARED".equals(severity)) {
        	return true;
		}
        if(clearingTime != 0L) {
            return true;
        }
        return alarmTypeStr.equalsIgnoreCase("alarmDeletion");
    }

	private static String getAdditionalInfo_type(ManagementExtensionType[] mets) {
		Any obj = getValueByIdInMETs(mets, "type");
		if(obj!=null) {
			return obj.extract_string();
		}
		return null;
	}
	
	private static String getAdditionalInfo_layerRate(ManagementExtensionType[] mets) {
		Any obj = getValueByIdInMETs(mets, "layerRate");
		if(obj!=null) {
			return obj.extract_string();
		}
		return null;
	}
	
	private static String getAdditionalInfo_neid(ManagementExtensionType[] mets) {
		Any obj = getValueByIdInMETs(mets, "neid");
		if(obj!=null) {
			return obj.extract_string();
		}
		return null;
	}

	private static Any getValueByIdInMETs(ManagementExtensionType[] mets, String id) {
		for (ManagementExtensionType met : mets) {
			if (met.id.equalsIgnoreCase(id)) {
				return met.info;
			}
		}
		return null;
	}
	
	private static String severityConvert(int value) {
		/*
		 * CLEARED, INDETERMINATE, WARNING, MINOR, MAJOR, CRITICAL;
		 */
		switch (value) {
		case 6:
			return "CLEARED";
		case 5:
			return "INDETERMINATE";
		case 4:
			return "WARNING";
		case 3:
			return "MINOR";
		case 2:
			return "MAJOR";
		case 1:
			return "CRITICAL";
		case 0:
			return "PENDING";
		}

		return null;
	}

	public static String convertToType(String extract_string) {
		/*
		 * COMMUNICATION, ENVIRONMENT, EQUIPMENT, PROCESSING_ERROR, QoS
		 */

		if (extract_string.toLowerCase().contains("communication")) {
			return "COMMUNICATION";
		} else if (extract_string.toLowerCase().contains("environment")) {
			return "ENVIRONMENT";
		} else if (extract_string.toLowerCase().contains("equipment")) {
			return "EQUIPMENT";
		} else if (extract_string.toLowerCase().contains("quality")) {
			return "QOS";
		} else if (extract_string.toLowerCase().contains("processing")) {
			return "PROCESSING_ERROR";
		} else if (extract_string.toLowerCase().contains("qos")) {
			return "QOS";
		} else {// unsupported!
			LOG.error("unsupported alarm type " + extract_string);
			return "NONE";
		}
	}

	public static void main(String[] args) {
		
	}
}
