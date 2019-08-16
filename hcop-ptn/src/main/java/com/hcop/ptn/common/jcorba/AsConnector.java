package com.hcop.ptn.common.jcorba;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNotification.StructuredEvent;

import com.alcatel.almap.AlarmSurveillance.AlarmReplyHandler;
import com.alcatel.almap.AlarmSurveillance.AlarmReplyHandlerPOA;
import com.alcatel.almap.AlarmSurveillanceOs.ASOS.AlmapAlarmSurvOS;
import com.alcatel.almap.AlarmSurveillanceOs.ASOS.AlmapAlarmSurvOSHelper;
import com.alcatel.almap.GeneralErrorDefs.ErrorType;
import com.alcatel.almap.GeneralErrorDefs.GeneralException;
import com.alcatel.almap.GeneralErrorDefs.InvalidParameterException;
import com.alcatel.almap.GeneralErrorDefs.OperationResult;
import com.alcatel.almap.GlobalDefsOS.ConnectionProblem;
import com.alcatel.almap.OSReply.ServerOsReplyHandler;
import com.alcatel.almap.OSReply.ServerOsReplyHandlerPOA;

public class AsConnector {
	private static Log log = LogFactory.getLog(AsConnector.class);
	
	private AsConfProvider provider;
	private NotifChannelAdmin notifChannel;
	private AlmapAlarmSurvOS os;

	public AsConnector(AsConfProvider provider) {
		this.provider = provider;
		notifChannel = new NotifChannelAdmin();
	}

	public NotifChannelAdmin getNotifChannel() {
		return notifChannel;
	}
	
	public AlmapAlarmSurvOS getOs() {
		return os;
	}

	public AsConfProvider getProvider() {
		return provider;
	}
	
	/**
	 * 建立主动获取连接
	 * @return
	 */
	public boolean connect(){
		String url = getUrl(provider);
		String alarmOsName = getAlarmOsName();
		NamingContextExt nameCtx;
		try {
			nameCtx = NamingContextExtHelper.narrow(ORBWrapper.instance().getORB().string_to_object(url));
			Object obj = Utilities.resolveNSPath(nameCtx, alarmOsName);
			if(os!=null){
				os._release();
				os = null;
			}
			os =  AlmapAlarmSurvOSHelper.narrow(obj);
		} catch (Exception e) {
			log.error("can't connect to Alarm System with alarm os Name: " + alarmOsName+",url:"+url);
			return false;
		} 
		return true;
	}

	private String getAlarmOsName() {
		return getParentPath()  + "AlmapAlarmSurvOS";
		
	}

	private String getOSName(AsConfProvider prov){
		ASType type = prov.getType();
		float dv = Float.parseFloat(prov.getDigitVersion());
		switch(type){
		case EML: return "ASIM_EML_"+prov.getInst();
		case OTN:  if(dv > 13) {
			return "ASIM_OTNE_"+prov.getInst();
		}else {
			return "ASIM_SDH_"+prov.getInst();
		}
		case PKT: return "ASIM_PKT_"+prov.getInst();
		default: throw new RuntimeException("unsupported AS type");
		}
	}
	private String getUrl(AsConfProvider prov) {
		return "corbaloc:iiop:" + prov.getIP() + ":" + prov.getNsPort() + "/DefaultNamingContext"; 
	}
	
	
	/**
	 * 
	 * 连接通知通道
	 * @param alarmNotifHandler
	 */
	public void connectAlarmEventChannel(NotifHandler alarmNotifHandler){
		try {
			notifChannel.connect(provider.getIP(), provider.getNsPort(), getAlarmEventChannelName(), getNmsName(), alarmNotifHandler);
		} catch (Exception e) {
			log.error("can't connect to event channel ", e);
		} 
	}
	
	public String getNmsName(){
		return provider.getType().name() + "_" + provider.getInst();
	}

	private String getAlarmEventChannelName() {
		return  getParentPath() + "EventChannelAlarmOS";
	}

	private String getParentPath() {
		return "ALMAPServers/FaultServers/" + getOSName(provider) + "-" + provider.getDigitVersion() +"/";
	}
	
	
	public void getAlarms(NotifHandler alarmNotifHandler){
		
		ORB orb = ORBWrapper.instance().getORB();
			try {
				AlarmReplyHandler replyHandler = new UTPAlarmReplyHandler(alarmNotifHandler)._this(orb);
				ServerOsReplyHandler serverHanlder = new UTPServerOsReplyHandler(alarmNotifHandler)._this(orb);
				os.get_active_alarms(replyHandler, serverHanlder);
			} catch (Exception e) {
				log.error("can't retrieve alarms", e);
			}
	}
	
	class UTPAlarmReplyHandler extends AlarmReplyHandlerPOA {
		private NotifHandler realHandler;
		UTPAlarmReplyHandler(NotifHandler handler){
			this.realHandler = handler;
		}
		@Override
		public void end_of_replies(OperationResult result) throws InvalidParameterException, GeneralException {
			log.info("alarm fetch completed");
		}
		@Override
		public void send_alarms(StructuredEvent[] alarms, OperationResult result) throws InvalidParameterException, GeneralException {			
			if (result.discriminator() == ErrorType.E_NO_ERROR) {
//				for (StructuredEvent event : alarms) {
//					realHandler.handle(getNmsName(), event, false);
//				}
				realHandler.handle(getNmsName(), alarms, false);
			}
		}
		
	}
	
	
	class UTPServerOsReplyHandler extends ServerOsReplyHandlerPOA {
		NotifHandler alarmNotifHandler ;
		public UTPServerOsReplyHandler(NotifHandler alarmNotifHandler){
			this.alarmNotifHandler = alarmNotifHandler;
		}
		@Override
		public void end_of_replies(int sessionID, Object objRef) throws ConnectionProblem {
			log.info("alarm retrieving completed");
			alarmNotifHandler.synOver(AsConnector.this);
		}
	}

}
