package com.otn.tool.common.db.oms;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.otn.tool.common.properties.OmsConf;
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
		Map<String, String> omsConfMap = OmsConf.instance().getPropertiesMap();
		String version = omsConfMap.get("OMS_VERSION");
		if(omsConfMap.containsKey("EML_INSTANCE") && omsConfMap.get("EML_INSTANCE") != null && omsConfMap.get("EML_INSTANCE").length() > 0) {
			String[] emlInstances = omsConfMap.get("EML_INSTANCE").split(",");
			for(int i = 0; i < emlInstances.length; i++) {
				int instance = Integer.parseInt(emlInstances[i]);
				conCreator.createPoolAndSave(version, DBEnum.SNA, instance, poolCache, "");
				conCreator.createPoolAndSave(version, DBEnum.ETH, instance, poolCache, "");
				conCreator.createPoolAndSave(version, DBEnum.MPLS, instance, poolCache, "");
				conCreator.createPoolAndSave(version, DBEnum.EML_ANALOG, instance, poolCache, "");
				conCreator.createPoolAndSave(version, DBEnum.EML_SDH, instance, poolCache, "");
				conCreator.createPoolAndSave(version, DBEnum.EML_SYS, instance, poolCache, SYS);
			}
		}

		if(omsConfMap.containsKey("OTN_INSTANCE") && omsConfMap.get("OTN_INSTANCE") != null && omsConfMap.get("OTN_INSTANCE").length() > 0) {
			String[] otnInstances = omsConfMap.get("OTN_INSTANCE").split(",");
			for(int i = 0; i < otnInstances.length; i++) {
				int instance = Integer.parseInt(otnInstances[i]);
				conCreator.createPoolAndSave(version, DBEnum.SDH, instance, poolCache, "");
				conCreator.createPoolAndSave(version, DBEnum.WDM, instance, poolCache, "");
				conCreator.createPoolAndSave(version, DBEnum.SDH_SYS, instance, poolCache, SYS);
			}
		}
	}
	
	public Connection getSDHNPRDBConnection(String nmsConfName, int instNum) {
		
		return getConnectionBy(DBEnum.SDH.getCacheKey(nmsConfName, instNum));
	}
	
	public Connection getCurrentSDHNPRDBConnection() {
		Map<String, String> omsConfMap = OmsConf.instance().getPropertiesMap();
		if (!omsConfMap.isEmpty()){
			int currentSDHNPRInst = -1;
			String[] otnInstances = omsConfMap.get("OTN_INSTANCE").split(",");
			for (String instance : otnInstances) {
				currentSDHNPRInst = Integer.parseInt(instance);
			}
			if (currentSDHNPRInst == -1) {
				return null;
			} else {
				return getSDHNPRDBConnection("", currentSDHNPRInst);
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
		Map<String, String> omsConfMap = OmsConf.instance().getPropertiesMap();
		if (!omsConfMap.isEmpty()){
			log.debug(" otn conf not null");
			int currentOTNInst = -1;
			String[] otnInstances = omsConfMap.get("OTN_INSTANCE").split(",");
			for (String instance : otnInstances) {
				currentOTNInst = Integer.parseInt(instance);
			}
			if (currentOTNInst == -1) {
				return null;
			} else {
				return getWDMConnection("", currentOTNInst);
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
					return getSNAConnection("", instNum);
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
		Map<String, String> omsConfMap = OmsConf.instance().getPropertiesMap();
		if (!omsConfMap.isEmpty()){
			int currentSDHNPRInst = -1;
			String[] otnInstances = omsConfMap.get("OTN_INSTANCE").split(",");
			for (String instance : otnInstances) {
				currentSDHNPRInst = Integer.parseInt(instance);
			}
			if (currentSDHNPRInst == -1) {
				return null;
			} else {
				return getSysConnection(DBEnum.SDH_SYS, SYS, currentSDHNPRInst);
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
					return getSysConnection(DBEnum.EML_SYS, SYS, instNum);
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

	public static void main(String[] args) {
		Connection eml = DBMgr.instance().getCurrentEmlSysConnection("100");
	}
}
