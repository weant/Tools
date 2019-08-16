package com.hcop.ptn.alarm;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.hcop.ptn.alarm.rootcause.NotificationAlarmHandler;
import com.hcop.ptn.alarm.rootcause.common.AlarmQueue;
import com.hcop.ptn.alarm.rootcause.common.RootCauseAlarmHelper;
import com.hcop.ptn.alarm.event.AlarmSynchronizeObserver;
import com.hcop.ptn.alarm.notification.AlarmBeanConverter;
import com.hcop.ptn.alarm.notification.AlarmBeanPersister;
import com.hcop.ptn.alarm.notification.AlarmSynStatus;
import com.hcop.ptn.common.jcorba.*;
import com.hcop.ptn.common.properties.AlarmProbableConf;
import com.hcop.ptn.common.properties.Conf;
import com.hcop.ptn.common.properties.OmsConf;
import com.hcop.ptn.common.utils.JsonUtils;
import com.hcop.ptn.common.utils.SchedulerUtil;
import com.hcop.ptn.common.utils.SshConnect;
import com.hcop.ptn.restful.model.Alarm;
import com.hcop.ptn.servlet.SSESourceMgr;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNotification.StructuredEvent;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;

public class AlarmReceiver implements NotifHandler {

	private static Log LOG = LogFactory.getLog(AlarmReceiver.class);
	private AtomicInteger count = new AtomicInteger(0);
	private static AlarmReceiver receiver;
		
	private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
	
	private ScheduledThreadPoolExecutor stpe;

	private Map<AsConnector, NetworkCheckService> mgrServerMap;

	private Map<AsConnector, SshConnect> sshConnectionMap;

	private volatile Map<String, Long> lastAlarmUpdateTimeMap;

	private String reSyncAlarmTime;

	private Map<String, String> alarmProbableLocalMap;

	private String serverCommand;
	private String pcClientCommand;
	private static final int LOOP_INTERVAL_SECOND=300;
	private Map<String, String> confMap;

	private BlockingQueue<Alarm> alarmQueue;
	
	private AlarmReceiver() {
		stpe = new ScheduledThreadPoolExecutor(2);
		mgrServerMap = new HashMap<>();
		lastAlarmUpdateTimeMap = new HashMap<>();
		sshConnectionMap = new HashMap<>();
		alarmProbableLocalMap = AlarmProbableConf.instance().getPropertiesMap();
		confMap = Conf.instance().getPropertiesMap();
		alarmQueue = AlarmQueue.getInstance().getAlarmQueue();

		// 分析通知告警根因
		new Thread(new NotificationAlarmHandler(), "RCA4Notification").start();
	}
	public static AlarmReceiver instance() {
		if(receiver == null) {
			receiver = new AlarmReceiver();
		}
		return receiver;
	}
	
	/**
	 * 
	 * 初始化corba接口
	 */
	public Boolean initCorba() {
		Boolean result = false;
		try {
			ORBWrapper.instance().init();
			result = true;
		} catch (InvalidName e) {
			e.printStackTrace();
		} catch (AdapterInactive e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Restart the CORBA interface
	 */
	public Boolean restart() {
		Boolean result = false;
		ORBWrapper.instance().reset();
		try {
			if (initCorba()) {
				result = startup();
			}
		} catch (Exception e) {
			LOG.error("Failed to init Jcorba.", e);
		}
		return result;
	}
	
	/**
	 *
	 * 开启告警收集功能.在调用此方法前请保证corba接口已经初始化完成,或者调用initCorba方法
	 * corbar初始化失败!
	 * @throws Exception 
	 */
	public Boolean startup() throws Exception{
		initCommandString();
		Boolean result = true;

		LOG.info("Ready to connect AS, clearing Alarms in local storage first.");
		AlarmBeanPersister.getInstance().clearAllAlarm();
		LOG.info("clear local alarms end.");

		LOG.info("start sync alarm from PKT.");
		result = result & listenToAlamSystem(ASType.PKT);
		LOG.info("sync alarm from PKT end.");

		if(confMap.get("alarm.otn.sync").equalsIgnoreCase("on")) {
			LOG.info("start sync alarm from SDH/OTN.");
			result = result & listenToAlamSystem(ASType.OTN);
			LOG.info("sync alarm from SDH/OTN end.");
		}

		LOG.info("start sync alarm from EML.");
		result = result & listenToAlamSystem(ASType.EML);
		LOG.info("sync alarm from EML end.");
		
		// 定时同步告警及开关
		startResyncJob();
		
		return result;
	}
	
	public void initCommandString() {
		String usedIP = System.getProperty("UsedIP");
		String oaPort = System.getProperty("OAPort");
		
		if(!oaPort.isEmpty()) {
			//serverCommand
			if(usedIP !=null && !usedIP.isEmpty()) {
				serverCommand = " netstat -an | grep " + usedIP + "." + oaPort;
			} else {
				String[] ips = RecevierHelper.getV4Ips().split("\\|");
				StringBuilder ipAndPortString = new StringBuilder();
				for(int i = 0; i < ips.length; i++) {
					ipAndPortString.append(ips[i]);
					ipAndPortString.append(".");
					ipAndPortString.append(oaPort);
					ipAndPortString.append("|");
				}
				serverCommand = "netstat -an | egrep '(" + ipAndPortString.substring(0, ipAndPortString.length() - 1) +")'";
			}
			
			//pcClientCommand
			if(confMap.get("system.type").equalsIgnoreCase("windows")) {
				pcClientCommand = " cmd /C netstat -ano | findstr \"" + oaPort + "\"";
			} else {
				//shell
				pcClientCommand = "netstat -anp | grep " + oaPort;
			}
		}
	}
	
	public void startResyncJob() {
		String reSync = Conf.instance().getProperty("reSyncAlarmSwitch", "off").toLowerCase();
		try {
			if (reSync.equalsIgnoreCase("on")) {
				reSyncAlarmTime = Conf.instance().getProperty("reSyncAlarmTime", "0 0 2 * * ?");
				LOG.debug("++++++++++++++++reSyncAlarmTime:" + reSyncAlarmTime);
				ReSyncAlarm.instance().setAlarmReceiver(this);
				SchedulerUtil.instance().addJobs("RestartSyncAllAlarmJob", RestartSyncAllAlarmJob.class,
						reSyncAlarmTime, null, null);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public AtomicInteger getCount() {
		return count;
	}
	
	private Boolean listenToAlamSystem(ASType type){
		Boolean result = true;
		List<AsConfProvider> providers = AsConfProviderFactory.instance().getProviders(type);
		for(AsConfProvider provider: providers){
				LOG.info("Provider:" + provider);
				AsConnector conn = new AsConnector(provider);
				result = result & listenToSingleAlarmServer(conn);
		}
		return result;
		
	}

	public boolean listenToSingleAlarmServer(AsConnector conn) {
		if (conn.connect()) {
			conn.connectAlarmEventChannel(this);
			conn.getAlarms(this);
			count.incrementAndGet();
			LOG.debug("+++++++conn:" + conn.getNmsName() + " count current size:" + count.get());

			if (!mgrServerMap.containsKey(conn)) {
				NetworkCheckService task = new NetworkCheckService(this, conn);
				// stpe.scheduleAtFixedRate(task, 1, 1, TimeUnit.MINUTES);
				mgrServerMap.put(conn, task);
				stpe.scheduleWithFixedDelay(task, LOOP_INTERVAL_SECOND, LOOP_INTERVAL_SECOND, TimeUnit.SECONDS);
			}
			return true;
		} else {
			return false;
		}
	}
	
	public Map<String, Long> getLastAlarmUpdateTimeMap() {
		return lastAlarmUpdateTimeMap;
	}

	public void setLastAlarmUpdateTimeMap(Map<String, Long> lastAlarmUpdateTimeMap) {
		this.lastAlarmUpdateTimeMap = lastAlarmUpdateTimeMap;
	}

	public Map<AsConnector, NetworkCheckService> getMgrServerMap() {
		return mgrServerMap;
	}

	public Map<AsConnector, SshConnect> getSshConnectionMap() {
		return sshConnectionMap;
	}
	public String getServerCommand() {
		return serverCommand;
	}
	public String getPcClientCommand() {
		return pcClientCommand;
	}
	@Override
	public void handle(String nmsName, StructuredEvent[] events, boolean isNotif) {
		try{
			
			//ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
			Collection<Callable<AlarmBean>> calls = new ArrayList<>(20);
			for(StructuredEvent event:events) {
				//parallel
				calls.add(()-> AlarmBeanConverter.convert(nmsName, event, isNotif));
			}
			 List<Future<AlarmBean>> fts = fixedThreadPool.invokeAll(calls);
			 List<AlarmBean> list = new ArrayList<>();
			 for(Iterator<Future<AlarmBean>> it = fts.iterator(); it.hasNext();) {
				 Future<AlarmBean> f =  it.next();
				 AlarmBean bean = f.get();
				 if(bean == null) {
				 	continue;
				 }
				 list.add(bean);
			 }
			 AlarmBeanPersister.getInstance().persistAlarm(list,isNotif);
			 if(isNotif) {
			 	List<Alarm> alarms = new ArrayList <>();
			 	list.forEach(bean->{
					 Alarm alarm = new Alarm();
					 alarm.setAlarmId(bean.getAlarmId());
					 alarm.setAlarmType(bean.getAlarmType());
					 alarm.setOccurrentTime(String.valueOf(bean.getOccurTime().getTime()));
					 alarm.setClearTime(String.valueOf(bean.getClearingTime()));
					 alarm.setFriendlyName(bean.getFriendlyName());
					 alarm.setProbableCause(alarmProbableLocalMap.getOrDefault(bean.getProbableCause(), bean.getProbableCause()));
					 alarm.setProbableCauseId(bean.getProbableCause());
					 alarm.setSeverity(bean.getAlarmLevel() == null ? "": bean.getAlarmLevel().toString());
					 alarm.setServer(bean.getNbi_inst());
					 alarm.setIsRootCause(false);
					 alarm.setEventType(bean.getEventType());
					 alarms.add(alarm);

					 if (!bean.getEventType().equalsIgnoreCase("alarmUpdate") &&
							 AlarmHelper.getAllDefinedAlarms().contains(alarm.getProbableCauseId())) {
						 try {
						 	if (bean.getEventType().equalsIgnoreCase("alarmCreation")) {
						 		RootCauseAlarmHelper.getInstance().addAlarm(alarm);
						 	} else {
						 		RootCauseAlarmHelper.getInstance().removeAlarm(alarm);
						 	}

						 	alarmQueue.offer(alarm, 3, TimeUnit.SECONDS);
						 } catch (Exception e) {
							 LOG.error(e.getMessage(), e);
						 }
					 }
				 });

				 // RootCauseAlarmHelper.addAlarms(newAlarms);
				 // RootCauseAlarmHelper.removeAlarms(delAlarms);
				 // String jsonStr = JsonUtils.toJson(alarms);
				 SSESourceMgr.getInstance().send(JsonUtils.toJson(alarms));
			 }
			 //AlarmSynchronizeObserver.instance().notifyAlarmEvent();
		}catch(Exception e){
			LOG.error("fail to handle alarm", e);
		}
	}
	
	
	/**
	 * 
	 * provider factory 类
	 *
	 */
	private static class AsConfProviderFactory{
		private AsConfProviderFactory(){}
		
		private static AsConfProviderFactory fact = new AsConfProviderFactory();
		private Map<String, String> omsConfMap = OmsConf.instance().getPropertiesMap();
		static AsConfProviderFactory instance(){
			return fact;
		}
		
		public List<AsConfProvider> getProviders(ASType type) {
			Set<Map<String,String>> data = getServerInfo(type);

			if(data == null) {
				throw new RuntimeException("no config about type: " + type);
			}
			
			return createConfProviders(data,type);
		}

		private Set<Map<String,String>> getServerInfo(ASType type) throws RuntimeException {
			Set<Map<String,String>> data = new HashSet <>();
			String [] instances;
			switch(type){
				case EML: instances = omsConfMap.get("EML_INSTANCE").split(",");break;
				case OTN: instances = omsConfMap.get("OTN_INSTANCE").split(",");break;
				case PKT: instances = omsConfMap.get("PKT_INSTANCE").split(",");break;
				default: throw new RuntimeException("unsupported alarm system type: " + type);
			}

			for(int i = 0; i < instances.length; i++) {
				String name = type.name();
				if(type.equals(ASType.OTN)) {
					if(omsConfMap.containsKey("OTN_" + instances[i] +"_IP")){
						name = "OTN";
					} else if (omsConfMap.containsKey("OTNE_" + instances[i] + "_IP")) {
						name = "OTNE";
					} else {
						continue;
					}
				}

				if(omsConfMap.containsKey(name + "_" + instances[i] + "_IP")) {
					Map<String, String> temp = new HashMap <>(7);
					temp.put("ip", omsConfMap.get(name + "_" + instances[i] + "_IP"));
					temp.put("hostName", omsConfMap.get(name + "_" + instances[i] + "_HOSTNAME"));
					temp.put("nsPort", omsConfMap.get(name + "_" + instances[i] + "_NS_PORT"));
					temp.put("userName", omsConfMap.get(name + "_" + instances[i] + "_USER"));
					temp.put("password", omsConfMap.get(name + "_" + instances[i] + "_PASSWORD"));
					temp.put("digitVersion", omsConfMap.get("OMS_VERSION"));
					temp.put("instNum", String.valueOf(instances[i]));
					data.add(temp);
				}
			}

			return data;
		}

		private List<AsConfProvider> createConfProviders(Set<Map<String, String>> data, final ASType type) {
			
			List<AsConfProvider> providers = new ArrayList<>();
			for(final Map<String,String> entry: data){
				providers.add(new AsConfProvider(){

					@Override
					public String getIP() {
						return entry.get("ip");
					}
					
					@Override
					public String getHost() {
						return entry.get("hostName");
					}

					@Override
					public int getNsPort() {
						return Integer.parseInt(entry.get("nsPort"));
					}

					@Override
					public int getInst() {
						return Integer.parseInt(entry.get("instNum"));
					}

					@Override
					public String getDigitVersion() {
						String version = entry.get("digitVersion");
						if(version.indexOf('.')!=-1) {
							version = version.substring(0, version.indexOf('.'))+version.substring(version.indexOf('.'));
						}
						return version;
					}

					@Override
					public ASType getType() {
						return type;
					}

					@Override
					public String toString(){
						return "[host="+this.getHost()+",ip="+this.getIP()+",ns_port="+this.getNsPort()+",inst_num="+getInst()+",digitVersion="+getDigitVersion()+",type="+getType()+"]";
					}

					@Override
					public String getUserName() {
						return entry.get("userName");
					}

					@Override
					public String getPassword() {
						return entry.get("password");
					}
				} );
				
			}
			return providers;
		}

	}

	@Override
	public void synOver(AsConnector conn) {
		mgrServerMap.get(conn).setReSyncOver(true);
		LOG.debug("+++++++count size:" + count.get());
		LOG.debug("alarm sync end: " + conn.getNmsName());
		//all over
		if(count.decrementAndGet() <= 0){
			count.set(0);
			AlarmSynStatus.instance().setSynOver(true);
			LOG.debug("+++++++sync alarm over");
			AlarmSynchronizeObserver.instance().synOverAlarmEvent();
		}
		
	}

	public static void main(String[] args) {
		new Thread(()->{
			AlarmReceiver rec = new AlarmReceiver();
			rec.initCorba();
			try {
				rec.startup();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();

		try {
			TimeUnit.MINUTES.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}