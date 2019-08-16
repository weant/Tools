package com.hcop.ptn.alarm.notification;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.hcop.ptn.alarm.AlarmBean;
import com.hcop.ptn.alarm.AlarmHandler;
import com.hcop.ptn.common.db.dao.AlarmDao;
import com.hcop.ptn.common.properties.Conf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class AlarmBeanPersister extends Observable {
	private static Log log = LogFactory.getLog(AlarmBeanPersister.class);
	
	private static AlarmBeanPersister alarmPersister = new AlarmBeanPersister();

	private static Map<String, Long> clearedMap = new ConcurrentHashMap<>();

	private List<AlarmBean> newAlarmList;

	private static int debounceTime = Conf.instance().getIntConf("alarm.debouncing.second","180");
	
	private AlarmBeanPersister() {

	}
	
	public static AlarmBeanPersister getInstance() {
		return alarmPersister;
	}
	
	
	public void clearAllAlarm() {
		AlarmDao.instance().delete("");
	}
	public void persistAlarm(List<AlarmBean> alarms) {
		
		initNMLAlarmProbableCauseID(alarms);
	/*	for(AlarmBean alarm: alarms){
			if(!AlarmAPIFactory.getInstance().getAPIInterface().isRaise(alarm))continue;
		
		}*/
		
		persist(alarms, null);
	}

	public void persistAlarm(List<AlarmBean> alarms, Boolean isNotif) {
		initNMLAlarmProbableCauseID(alarms);
		persist(alarms, isNotif);
	}
	
	/**
	 * 初始化NML告警的probablecause id
	 * @param alarms
	 */
	private void initNMLAlarmProbableCauseID(List<AlarmBean> alarms){
		HashSet<String> nmlAlarmsProbableCauseNames = new HashSet<String>();
		List<AlarmBean> nmlAlarms  = new ArrayList<AlarmBean>();
		for(AlarmBean alarm: alarms){
			if(alarm.getNbi_inst()!=null&&isAlarmInNML(alarm)){//如果是NML告警，则需要根据probableCause name来查询id
				String probableCause = alarm.getProbableCause();
				if(probableCause == null) {
				    continue;//当网管发来一种删除的告警时,probableCause域可以为空,此时不需要再查询PCid
                }
				nmlAlarmsProbableCauseNames.add(alarm.getProbableCause());
				nmlAlarms.add(alarm);
			}
		}
		
		//查询probableCause name与id的映射关系
		//HashMap<String,String> mappings = AlarmDao.instance().queryCausesNameMappingId(nmlAlarmsProbableCauseNames);
		
		/*for(AlarmBean alarm:  nmlAlarms){
			String pcName = alarm.getProbableCause();
			
			pcName = pcName.replace("_", "").replace(" ", "").toUpperCase();
			String pcId = mappings.get(pcName);
			if(pcId != null){
				alarm.setProbableCause(pcId);
			}
		}*/
	}
	
	private boolean isAlarmInNML(AlarmBean alarm) {
		
		return !alarm.getNbi_inst().toUpperCase().contains("EML");
	}


	private void persist(List<AlarmBean> alarms, Boolean isNotif) {
		AlarmHandler handler = new AlarmHandler();
		if(alarms != null && !alarms.isEmpty()){
			ArrayList<AlarmBean> raisedAlarm = new ArrayList<>();
			ArrayList<AlarmBean> clearedAlarm = new ArrayList<>();
			ArrayList<AlarmBean> ackedRelatedAlarms = new ArrayList<>();
			ArrayList<AlarmBean> reservedRelatedAlarms = new ArrayList<>();
            //ArrayList<AlarmBean> updatedAlarm = new ArrayList<>();

            ListIterator<AlarmBean> listIterator = alarms.listIterator();

            while(listIterator.hasNext()) {
				AlarmBean alarm = listIterator.next();
				if(handler.isCleared(alarm) || alarm.isDeleted()){
					clearedAlarm.add(alarm);
					Long clearedTime;
					if (alarm.getClearingTime() > 0) {
						clearedTime = alarm.getClearingTime();
					} else {
						clearedTime = System.currentTimeMillis();
					}
					if (!alarm.getEventType().equals("alarmDeletion")) {
						alarm.setEventType("alarmDeletion");
					}
					clearedMap.put(alarm.getProbableCause()+"@"+alarm.getFriendlyName(), clearedTime);
					continue;
				}
				if (alarm.isAckRelatedOperation()) {
					ackedRelatedAlarms.add(alarm);
					continue;
				}
				if (alarm.isReserveRelatedOperation()) {
					reservedRelatedAlarms.add(alarm);
					continue;
				}
				if (alarm.isOther()) {
					log.warn("alarmId:" + alarm.getAlarmId() + " occurTime:" + alarm.getOccurTime() + " is empty alarm, can not handle!!!");
					listIterator.remove();
					continue;
				}
				if (handler.isRaise(alarm)) {
					Long raiseTime;
					Long clearedTime = 0L;
					if (alarm.getOccurTime().getTime() > 0) {
						raiseTime = alarm.getOccurTime().getTime();
					} else {
						raiseTime = System.currentTimeMillis();
					}

					if (clearedMap.containsKey(alarm.getProbableCause()+"@"+alarm.getFriendlyName())) {
						clearedTime = clearedMap.get(alarm.getProbableCause()+"@"+alarm.getFriendlyName());
					}

					if ((raiseTime - clearedTime) > (debounceTime * 1000)) {
						raisedAlarm.add(alarm);
						if (!alarm.getEventType().equals("alarmCreation")) {
							alarm.setEventType("alarmCreation");
						}
					} else {
						listIterator.remove();
					}

					continue;
				}

				/*if(alarm.isUpdate()) {
					updatedAlarm.add(alarm);
					continue;
				}*/

				listIterator.remove();
			}
			AlarmDao.instance().reserveRelated(reservedRelatedAlarms);
			AlarmDao.instance().ackRelated(ackedRelatedAlarms);
			AlarmDao.instance().saveCreationAlarms(raisedAlarm);
            //AlarmDao.instance().updateAlarms(updatedAlarm);
			if (isNotif && !alarms.isEmpty()) {
				log.info("we saved new Alarms: "+alarms.size());
				log.info("we saved creationAlarms: "+raisedAlarm.size());
                //log.info("we saved updatedAlarms: "+updatedAlarm.size());
				log.info("we saved clearedAlarms: " + clearedAlarm.size());
				log.info("we saved reservedRelatedAlarms: " + reservedRelatedAlarms.size());
				log.info("we saved ackedRelatedAlarms: " + ackedRelatedAlarms.size());
				List<AlarmBean> list = new ArrayList<>();
				list.addAll(raisedAlarm);
				list.addAll(clearedAlarm);
				if (list.size() > 0) {
					log.info("new create alarm received: " + raisedAlarm.size() +" ," +"new clear alarm received: " + clearedAlarm.size());
					this.setNewAlarmList(list);
				}

				for (Map.Entry<String, Long> entry : clearedMap.entrySet()) {
					if ((System.currentTimeMillis() - entry.getValue()) > debounceTime * 1000) {
						clearedMap.remove(entry.getKey());
					}
				}

			}

			handler.deleteAlarms(clearedAlarm);
		}
	}

	public List<AlarmBean> getNewAlarmList() {
		return this.newAlarmList;
	}

	public void setNewAlarmList(List<AlarmBean> list) {
		this.newAlarmList = list;
		setChanged();
		notifyObservers();
	}
}
