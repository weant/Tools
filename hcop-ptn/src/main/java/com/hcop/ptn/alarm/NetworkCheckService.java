package com.hcop.ptn.alarm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.hcop.ptn.common.db.dao.AlarmDao;
import com.hcop.ptn.common.jcorba.AsConnector;
import com.hcop.ptn.common.jcorba.NotifChannelAdmin;
import com.hcop.ptn.common.utils.SshConnect;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alcatel.almap.AlarmSurveillanceOs.ASOS.AlmapAlarmSurvOS;

public class NetworkCheckService implements Runnable {
	
	private final static int DEFAULT_TIME_OUT = 5000;
	private static Log LOG = LogFactory.getLog(NetworkCheckService.class); 
	private volatile AsConnector conn;
	private AlarmReceiver rec;
	// private AtomicBoolean connectState = new AtomicBoolean(true);
	private boolean previousNetworkState;

	private int repeatNum = 0;
	private AtomicBoolean reSyncOver = new AtomicBoolean(true);
	private static final int LOOP_CHECK_NUM=1;

	public void setReSyncOver(boolean reSyncOver) {
		this.reSyncOver.getAndSet(reSyncOver);
	}

	public NetworkCheckService(AlarmReceiver rec, AsConnector conn) {
		this.rec = rec;
		this.conn = conn;
		previousNetworkState = true;
	}

	@Override
	public void run() {
		synchronized (conn) {
			try {
				// 检查网络通达状态
				boolean currentNetworkState = checkNetworkState();
				// 检查与服务器端的TCP连接是否存在
				boolean currentConnectionState = true;
				if (currentNetworkState) {
					currentConnectionState = checkTCPConnection();
				}
				LOG.error("++++++++++++++++++" + conn.getNmsName() + " previous network State: " + previousNetworkState);
				LOG.error("++++++++++++++++++" + conn.getNmsName() + " current network State: " + currentNetworkState);
				LOG.error("++++++++++++++++++" + conn.getNmsName() + " current Connection State: " + currentConnectionState);
				LOG.error("++++++++++++++++++" + conn.getNmsName() + " repeatNum:" + repeatNum);
				LOG.error("++++++++++++++++++" + conn.getNmsName() + " reSyncOver:" + reSyncOver.get());

				LOG.error("++++++++++++++++++AlarmReceiver.count:" + rec.getCount());

				// 如果同步开始2分钟后还未同步完成，则判断未同步失败,，重置计数器并重新开始同步
				if (((repeatNum > LOOP_CHECK_NUM && !reSyncOver.get()) || !currentConnectionState) && currentNetworkState) {
					LOG.error("++++++++++++++++++" + conn.getNmsName() + " reSync failed, try again");
					// repeatNum = 0;
					// AlarmReceiver.this.synOver(conn);
					if (!reSyncOver.get() && rec.getCount().get() > 0) {
						rec.getCount().decrementAndGet();
					}
					previousNetworkState = false;
					reSyncOver.set(true);
				} else if (repeatNum < 10000) {
					repeatNum++;
				} else if (repeatNum > 10000) {
					repeatNum = 1;
				}

				if (!previousNetworkState && reSyncOver.get() && currentNetworkState) {
					repeatNum = 1;
					reSyncOver.set(false);
					LOG.debug("Server reconnected, begin to get alarm from " + conn.getNmsName());

					//AlarmReceiver.this.synStart(conn);
					reSyncAlarm();
					previousNetworkState = true;
				} else if (!currentNetworkState) {
					previousNetworkState = false;
				}
			} catch (Exception e) {
				LOG.error(conn.getNmsName() + " catch the exception");
				// aMgr.setAlarmsGetFinishedState(true);
				e.printStackTrace();
			}
		}
	}

	private String generateSQLCondition() {
		String sqlCondition;
		String version = conn.getProvider().getDigitVersion();
		if (new Float(version) > 13F && conn.getProvider().getType().name().equals("OTN")) {
			sqlCondition = " and nbi_inst in ('" + conn.getNmsName() + "','EML_" + conn.getProvider().getInst() + "')";
		} else {
			sqlCondition = " and nbi_inst = '" + conn.getNmsName() + "'";
		}
		return sqlCondition;
	}

	public void reSyncAlarm() {
		String sql = generateSQLCondition();
		AlarmDao.instance().delete(sql);
		AlmapAlarmSurvOS _alarmOS = conn.getOs();

		NotifChannelAdmin notifChannel = conn.getNotifChannel();
		// try {
		if (notifChannel != null) {
			notifChannel.getChannel().for_consumers()._release();
			notifChannel.getChannel().for_suppliers()._release();
			notifChannel.getChannel()._release();
			// notifChannel.getChannel().destroy();
		}

		/*
		 * ORBWrapper.instance().getRootPOA().destroy(true, true);
		 * ORBWrapper.instance().getORB().shutdown(true);
		 * ORBWrapper.instance().getORB().destroy();
		 * 
		 * ORBWrapper.instance().init(); } catch (org.omg.CORBA.BAD_INV_ORDER e) {
		 * LOG.error("org.omg.CORBA.BAD_INV_ORDER"); try { ORBWrapper.instance().init();
		 * } catch (InvalidName | AdapterInactive e1) {
		 * block e1.printStackTrace(); } } catch (InvalidName | AdapterInactive e) {
		 * e.printStackTrace(); }
		 */

		if (_alarmOS != null) {
			_alarmOS._release();
		}
		rec.listenToSingleAlarmServer(conn);
		// lastAlarmUpdateTimeMap.put(aMgr.getNbi_inst(), null);
	}

	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("finally")
	public String[] shellExecution(String command, AsConnector conn) {
		String[] resultArr = new String[2];
		try {
			SshConnect ssh = rec.getSshConnectionMap().get(conn);
			if (ssh == null) {
				ssh = new SshConnect(conn.getProvider().getIP(), conn.getProvider().getUserName(),
						conn.getProvider().getPassword());
				rec.getSshConnectionMap().put(conn, ssh);
			}
			LOG.debug("Host: " + conn.getProvider().getHost());
			LOG.debug("NBI_Type: " + conn.getProvider().getType());
			LOG.debug("InstValue: " + conn.getProvider().getInst());
			LOG.debug("Ssh Ip: " + ssh.getIp());
			// LOG.debug("Ssh Username: " + ssh.getUserName());
			// LOG.debug("Ssh Password: " + ssh.getPassword());

			if (ssh.login()) {
				LOG.debug("Command: " + command);
				resultArr = ssh.execShell(command);
				ssh.closeConn();
			}

			return resultArr;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return resultArr;
		}
	}

	@SuppressWarnings({ "finally" })
	public String[] cmdExecution(String cmd) {
		String[] results = new String[2];
		Runtime runtime = Runtime.getRuntime();

		try {
			Process process = runtime.exec(cmd);
			results[0] = processStream(process.getInputStream());
			results[1] = processStream(process.getErrorStream());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return results;
		}
	}

	private String processStream(InputStream in) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		StringBuilder result = new StringBuilder();
		String lineSeparator = System.getProperty("line.separator");
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			result.append(line);
			result.append(lineSeparator);
		}
		return result.toString();
	}

	public boolean checkTCPConnection() {
		// boolean flag = true;
		// check server's connection
		if (!rec.getServerCommand().isEmpty()) {
			String[] results = shellExecution(rec.getServerCommand(), conn);
			String result = results[0];
			if (result != null && result.indexOf(System.getProperty("OAPort")) == -1) {
				LOG.error("Server shell Command: " + rec.getServerCommand());
				LOG.error("Server shell exec result is : " + result);
                LOG.error("cannot find socket in server: " + conn.getNmsName());
				return false;
			}
		}

		// check local connection
		if (!rec.getPcClientCommand().isEmpty()) {
			String[] results = cmdExecution(rec.getPcClientCommand());
			if (results[1].isEmpty() && !results[0].contains(conn.getProvider().getIP())) {
				LOG.error("Local cmd Command: " + rec.getPcClientCommand());
				LOG.error("Local cmd exec result is : " + results[0]);
                LOG.error("cannot find socket in local: " + conn.getNmsName());
				return false;
			}
		}

		return true;
	}

	public boolean checkNetworkState() {
		List<String> ipList = Arrays.asList(RecevierHelper.getV4Ips().split("\\|"));
		String usedIp = StringUtils.trimToEmpty(System.getProperty("UsedIP"));
		// check localhost IP list
		if (usedIp != null && !usedIp.isEmpty() && !ipList.contains(usedIp)) {
            LOG.error("cannot find specified ip: " + conn.getNmsName() + ", IP: " + usedIp);
			return false;
		}

		// ping
		boolean socketflag = true;
		Socket socket = new Socket();
		try {
			if (usedIp != null && !usedIp.isEmpty()) {
				socket.bind(new InetSocketAddress(usedIp, 0));
	            socket.connect(new InetSocketAddress(conn.getProvider().getIP(), conn.getProvider().getNsPort()), DEFAULT_TIME_OUT);
	            //此种方式无法设置超时
				//InetAddress localaddress = InetAddress.getByName(usedIp);
				//socket = new Socket(conn.getProvider().getIP(), conn.getProvider().getNsPort(), localaddress, 0);
			} else {
				//socket = new Socket(conn.getProvider().getIP(), conn.getProvider().getNsPort());
				socket.connect(new InetSocketAddress(conn.getProvider().getIP(), conn.getProvider().getNsPort()), DEFAULT_TIME_OUT);
			}
			
			if(!socket.isConnected()) {
				socketflag = false;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			socketflag = false;
		} finally {
			if(socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(!socketflag) {
				LOG.error("++++++++" + conn.getNmsName() + ":" + conn.getProvider().getIP() + " is unreachable");
				return false;
			}
		}
		
//		以下测试服务器可达状态的方式有问题，但保留
//		try {
//			if (!usedIp.isEmpty()) {
//				Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
//				lableA: while (nifs.hasMoreElements()) {
//					NetworkInterface nif = nifs.nextElement();
//					Enumeration<InetAddress> addresses = nif.getInetAddresses();
//					while (addresses.hasMoreElements()) {
//						InetAddress addr = addresses.nextElement();
//						if ((addr instanceof Inet4Address) && addr.getHostAddress().equals(usedIp)) {
//							if (!InetAddress.getByName(conn.getProvider().getIP()).isReachable(nif, 5,
//									DEFAULT_TIME_OUT)) {
//								LOG.error("++++++++++++++++++" + conn.getNmsName() + " Ping false.");
//								return false;
//								// System.out.println("Unreachable by isReachable("+addr.getHostAddress()+", 5,
//								// 1000)");
//							}
//							break lableA;
//						}
//					}
//				}
//			} else if (!InetAddress.getByName(conn.getProvider().getIP()).isReachable(DEFAULT_TIME_OUT)) {
//				LOG.error("++++++++++++++++++" + conn.getNmsName() + " Ping false.");
//				return false;
//			}
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		// nms stopped
		/*try {
			conn.getNotifChannel().getChannel().for_suppliers()._non_existent();
		} catch (org.omg.CORBA.BAD_OPERATION | org.omg.CORBA.BAD_INV_ORDER e) {
			LOG.debug("++++++++++++++++++org.omg.CORBA.BAD_OPERATION EXCEPTION");
			// return true;
		} catch (org.omg.CORBA.TRANSIENT e) {
			LOG.error("++++++++++++++++++org.omg.CORBA.TRANSIENT EXCEPTION");
			ClientLogBuffer.LOG(I18N.instance().getString("alarm_processe_stopped_in_server") + conn.getNmsName() +"@red");
			return false;
		}*/

		return true;
	}
}
