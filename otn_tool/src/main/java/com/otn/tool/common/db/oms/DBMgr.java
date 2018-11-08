package com.otn.tool.common.db.oms;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DBMgr {
	private static Log log = LogFactory.getLog(DBMgr.class);
	private HashMap<String, ConnectionPool> poolCache;
	private static DBMgr _instance;
	
	private  ConnectionCreator conCreator;
	private static final String SYS = "_sys";
	public static DBMgr instance() {
		if (_instance == null) {
			_instance = new DBMgr();
		}
		return _instance;
	}
	
	private DBMgr() {
		poolCache =new HashMap<>();
		conCreator = new WalletCreator();
		log.debug("creator created!");
		init();
		log.debug("DBMgr init!");
	}
	
	private void init() {
		Map<String, NMSConfEntry> nmsConfs = NMSConfMgr.instance().get_nmsConfEntries();
		for (NMSConfEntry nmsConfEntry : nmsConfs.values()) {
//			Set<Integer> pktInsts = nmsConfEntry._pktConfs.keySet();
//			for (int pktInst : pktInsts) {
//				Map<String, String> pktConfs = nmsConfEntry._pktConfs.get(pktInst);
//				if (checkPKTDBConfIntegrity(pktConfs)) {
//					conCreator.createPoolAndSave(pktConfs, DBEnum.PKT, pktInst, poolCache, nmsConfEntry._name);
//
//					conCreator.createPoolAndSave(pktConfs, DBEnum.PKT_SYS, pktInst, poolCache, nmsConfEntry._name+SYS);
//				}
//			}
			
			Set<Integer> sdhNPRInsts = nmsConfEntry._sdhNPRConfs.keySet();
			for (int sdhNPRInst : sdhNPRInsts) {
				Map<String, String> sdhNPRConfs = nmsConfEntry._sdhNPRConfs.get(sdhNPRInst);
				if (checkSDHNPRDBConfIntegrity(sdhNPRConfs)) {
					conCreator.createPoolAndSave(sdhNPRConfs, DBEnum.SDH, sdhNPRInst, poolCache, nmsConfEntry._name);
					conCreator.createPoolAndSave(sdhNPRConfs, DBEnum.WDM, sdhNPRInst, poolCache, nmsConfEntry._name);
					conCreator.createPoolAndSave(sdhNPRConfs, DBEnum.SDH_SYS, sdhNPRInst, poolCache, nmsConfEntry._name+SYS);
				}
			}
			
			Set<Integer> emlInsts = nmsConfEntry._emlConfs.keySet();
			for (int emlInst : emlInsts) {
				Map<String, String> emlConfs = nmsConfEntry._emlConfs.get(emlInst);
				if (checkEMLDBConfIntegrity(emlConfs)) {
					conCreator.createPoolAndSave(emlConfs, DBEnum.SNA, emlInst, poolCache, nmsConfEntry._name);
					conCreator.createPoolAndSave(emlConfs, DBEnum.ETH, emlInst, poolCache, nmsConfEntry._name);
					conCreator.createPoolAndSave(emlConfs, DBEnum.MPLS, emlInst, poolCache, nmsConfEntry._name);
					conCreator.createPoolAndSave(emlConfs, DBEnum.EML_ANALOG, emlInst, poolCache, nmsConfEntry._name);
					conCreator.createPoolAndSave(emlConfs, DBEnum.EML_SDH, emlInst, poolCache, nmsConfEntry._name);
					conCreator.createPoolAndSave(emlConfs, DBEnum.EML_SYS, emlInst, poolCache, nmsConfEntry._name+SYS);
				}
			}
		}
	}
	
	
	private boolean checkSDHNPRDBConfIntegrity(Map<String, String> confItems) {
		if (confItems.containsKey(ConnectionCreator.KEY_HOST) && confItems.get(ConnectionCreator.KEY_HOST).length() != 0 &&
			confItems.containsKey(ConnectionCreator.KEY_IP) && confItems.get(ConnectionCreator.KEY_IP).length() != 0 &&
			confItems.containsKey(ConnectionCreator.KEY_NMS_DIGITAL_VERSION) && confItems.get(ConnectionCreator.KEY_NMS_DIGITAL_VERSION).length() != 0 &&
			confItems.containsKey(ConnectionCreator.KEY_DB_PORT) && confItems.get(ConnectionCreator.KEY_DB_PORT).length() != 0) {
			return true;
		}
		return false;
	}
	
	private boolean checkEMLDBConfIntegrity(Map<String, String> confItems) {
		if (confItems.containsKey(ConnectionCreator.KEY_HOST) && confItems.get(ConnectionCreator.KEY_HOST).length() != 0 &&
				confItems.containsKey(ConnectionCreator.KEY_IP) && confItems.get(ConnectionCreator.KEY_IP).length() != 0 &&
				confItems.containsKey(ConnectionCreator.KEY_NMS_DIGITAL_VERSION) && confItems.get(ConnectionCreator.KEY_NMS_DIGITAL_VERSION).length() != 0 &&
				confItems.containsKey(ConnectionCreator.KEY_DB_PORT) && confItems.get(ConnectionCreator.KEY_DB_PORT).length() != 0) {
				return true;
		}
		return false;
	}
	
	public Connection getSDHNPRDBConnection(String nmsConfName, int instNum) {
		
		return getConnectionBy(DBEnum.SDH.getCacheKey(nmsConfName, instNum));
	}
	
	public Connection getCurrentSDHNPRDBConnection() {
		NMSConfEntry currentConf = NMSConfMgr.instance().get_currentConf();
		if (currentConf != null){	
			int currentSDHNPRInst = -1;
			Set<Integer> sdhNPRInsts = currentConf._sdhNPRConfs.keySet();
			for (int sdhNPRInst : sdhNPRInsts) {
				currentSDHNPRInst = sdhNPRInst;
			}
			if (currentSDHNPRInst == -1) {
				return null;
			} else {
				return getSDHNPRDBConnection(currentConf._name, currentSDHNPRInst);
			}
		} else {
			return null;
		}
	} 
	
	
	
	public Connection getWDMConnection(String nmsConfName, int instNum) {
		return getConnectionBy(DBEnum.WDM.getCacheKey(nmsConfName, instNum));
	}

	private Connection getConnectionBy(String key) {
		ConnectionPool pool = poolCache.get(key);
		try {
			return pool.getConnection();
		} catch (SQLException e) {
			log.error("can't get  connection from the pool key:" + key, e);
		}
		return null;
	}
	
	public Connection getCurrentWDMDBConnection() {
		log.debug("get wdm connection start");
		NMSConfEntry currentConf = NMSConfMgr.instance().get_currentConf();
		if (currentConf != null){
			log.debug(" otn conf not null");
			int currentPKTInst = -1;
			Set<Integer> pktInsts = currentConf._sdhNPRConfs.keySet();
			for (int pktInst : pktInsts) {
				currentPKTInst = pktInst;
			}
			if (currentPKTInst == -1) {
				return null;
			} else {
				return getWDMConnection(currentConf._name, currentPKTInst);
			}
		} else {
			log.debug(" otn conf is null, connection will return null");
			return null;
		}
	}
	
	public Connection getETHConnection(String nmsConfName, int instNum){
		return getConnectionBy(DBEnum.ETH.getCacheKey(nmsConfName, instNum));
	}
	
	public Connection getMPLSConnection(String nmsConfName, int instNum){
		return getConnectionBy(DBEnum.MPLS.getCacheKey(nmsConfName, instNum));
	}
	public Connection getSNAConnection(String nmsConfName, int instNum) {
		return getConnectionBy(DBEnum.SNA.getCacheKey(nmsConfName, instNum));
	}
	
	public Connection getEML_SDHConnection(String nmsConfName, int instNum) {
		return getConnectionBy(DBEnum.EML_SDH.getCacheKey(nmsConfName, instNum));
	}
	
	public Connection getEML_ANALOGConnection(String nmsConfName, int instNum) {
		return getConnectionBy(DBEnum.EML_ANALOG.getCacheKey(nmsConfName, instNum));
	}
	
	public Connection getCurrentEmlConnection(String groupId) {
		Connection sdhNPRConn = getCurrentSDHNPRDBConnection();
		if (sdhNPRConn != null) {
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = sdhNPRConn.createStatement();
				rs = stmt.executeQuery("select NMSYSTINST from EML where NMSYSTINST is not null and EMLDOMID=" + groupId);
				String emlInst = "";
				while (rs.next()) {
					emlInst = rs.getString(1);
				}
				rs.close();
				stmt.close();
				sdhNPRConn.close();
				
				if (emlInst.length() > 0) {
					int pos = emlInst.indexOf('_');
					int pos1 = emlInst.indexOf('-');
					String sInstNum = emlInst.substring(pos+1, pos1);
					int instNum = Integer.parseInt(sInstNum);
					String currentConfName = NMSConfMgr.instance().getCurrentConfName();
					return getSNAConnection(currentConfName, instNum);
				}
			} catch (SQLException e) {
				return null;
			} finally {
				try {
					if(rs != null) {
						rs.close();
					}
					
					if(stmt != null) {
						stmt.close();
					}
					
					if(sdhNPRConn != null) {
						sdhNPRConn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public Connection getSysConnection(DBEnum type, String nmsConfName, int instNum){
		return getConnectionBy(type.getCacheKey(nmsConfName, instNum));
	}
	
	public Connection getCurrentSDHNPRDBSysConnection() {
		NMSConfEntry currentConf = NMSConfMgr.instance().get_currentConf();
		if (currentConf != null){	
			int currentSDHNPRInst = -1;
			Set<Integer> sdhNPRInsts = currentConf._sdhNPRConfs.keySet();
			for (int sdhNPRInst : sdhNPRInsts) {
				currentSDHNPRInst = sdhNPRInst;
			}
			if (currentSDHNPRInst == -1) {
				return null;
			} else {
				return getSysConnection(DBEnum.SDH_SYS, currentConf._name+SYS, currentSDHNPRInst);
			}
		} else {
			return null;
		}
	} 
	
	
	public Connection getCurrentEmlSysConnection(String groupId){

		Connection sdhNPRConn = getCurrentSDHNPRDBConnection();
		if (sdhNPRConn != null) {
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = sdhNPRConn.createStatement();
				rs = stmt.executeQuery("select NMSYSTINST from EML where NMSYSTINST is not null and EMLDOMID=" + groupId);
				String emlInst = "";
				while (rs.next()) {
					emlInst = rs.getString(1);
				}
				//rs.close();
				//stmt.close();
				//sdhNPRConn.close();
				
				if (emlInst.length() > 0) {
					int pos = emlInst.indexOf('_');
					int pos1 = emlInst.indexOf('-');
					String sInstNum = emlInst.substring(pos+1, pos1);
					int instNum = Integer.parseInt(sInstNum);
					String currentConfName = NMSConfMgr.instance().getCurrentConfName();
					return getSysConnection(DBEnum.EML_SYS, currentConfName+SYS, instNum);
				}
			} catch (SQLException e) {
				return null;
			} finally {
				try {
					if(rs != null) {
						rs.close();
					}
					
					if(stmt != null) {
						stmt.close();
					}
					
					if(sdhNPRConn != null) {
						sdhNPRConn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public Connection getEMLANALOGConnection(String confName, int inst) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getEMLInstanceByNEIp(String neIp) {
		if(neIp == null || "".equals(neIp)) {
			log.error("getEMLInstanceByNEIp: neIp is blank");
			return -1;
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append(" select NE.IPADDRESS, NE.EMLDOMID, EML.NMSYSTINST, substr(EML.NMSYSTINST, instr(EML.NMSYSTINST, '_') + 1, instr(EML.NMSYSTINST, '-') - instr(EML.NMSYSTINST, '_') - 1) EMLINST ");
		sql.append(" from SNML.EML, SNML.NE ");
		sql.append(" where NE.IPADDRESS = '").append(neIp).append("' ");
		sql.append(" and NE.EMLDOMID = EML.EMLDOMID ");
		
		Connection sdhNPRConn = getCurrentSDHNPRDBConnection();
		if (sdhNPRConn != null) {
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = sdhNPRConn.createStatement();
				rs = stmt.executeQuery(sql.toString());
				while (rs.next()) {
					int emlInst = rs.getInt("EMLINST");
					log.info("neIp:" + neIp + ", emlInst:" + emlInst);
					return emlInst;
				}
				
				//rs.close();
				//stmt.close();
				//sdhNPRConn.close();
			} catch (SQLException e) {
				log.error("Exception, neIp:" + neIp);
				e.printStackTrace();
				return -1;
			} finally {
				try {
					if(rs != null) {
						rs.close();
					}
					
					if(stmt != null) {
						stmt.close();
					}
					
					if(sdhNPRConn != null) {
						sdhNPRConn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		log.error("can not find emlInst for neIp:" + neIp);
		return -1;
	}
	
}
