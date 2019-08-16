package com.hcop.ptn.common.db.dao;

import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

import com.hcop.ptn.alarm.AlarmBean;
import com.hcop.ptn.alarm.AlarmHelper;
import com.hcop.ptn.common.beans.AlarmLevel;
import com.hcop.ptn.common.db.tool.DbUtil;
import com.hcop.ptn.common.db.tool.ToolDbMgr;
import com.hcop.ptn.common.properties.AlarmProbableConf;
import com.hcop.ptn.common.utils.CollectionUtils;
import com.hcop.ptn.common.utils.StringUtils;
import com.hcop.ptn.restful.model.Alarm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AlarmDao {
	private static final Log log = LogFactory.getLog(AlarmDao.class);
	private static AlarmDao dao = new AlarmDao();
	private DbUtil mgr = new DbUtil(ToolDbMgr.instance());
    private StampedLock lock = new StampedLock();
	HashMap<String,String> ret = null;
    Map<String,String> alarmProbableLocalMap;
    //private static final int LOCK_TIMEOUT = 5;

	private AlarmDao() {
        alarmProbableLocalMap = AlarmProbableConf.instance().getPropertiesMap();
	}

	public static AlarmDao instance() {
		return dao;
	}


	public void saveAlarmsCleared(List<AlarmBean> alarms) {
		if(alarms == null || alarms.isEmpty()) {
			return;
		}
		String sql = "insert or ignore into alarms_cleared(alarmObjectType,friendlyname,occureTime,probableCause,alarmID,"
				+ " alarmType,alarmLevel,acknowledgementStatus,reservationStatus,clearingStatus,clearableStatus,"
				+ " nbi_Inst,repetitionCounter,managementObjectClass,cleartime "
				+ ",alarmObject,inserttime) values "
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";

		List<Object[]> paramList = new ArrayList<Object[]>();

		for (AlarmBean alarm : alarms) {
			Object[] params = { alarm.getAlarmObjectType(),
					alarm.getFriendlyName(), alarm.getOccurTime(),
					alarm.getProbableCause(), alarm.getAlarmId(),
					alarm.getAlarmType(), alarm.getAlarmLevel().toValue(),
					alarm.getAcknowledgementStatus(),
					alarm.getReservationStatus(), alarm.getClearingStatus(),
					alarm.getClearableStatus(), alarm.getNbi_inst(),
					alarm.getRepetitionCounter(),
					alarm.getManagementObjectClass(), System.currentTimeMillis(), alarm.getAlarmObject(),
					alarm.getInsertTime() };
			paramList.add(params);
		}

		mgr.doBatchSql(sql, paramList);
	}
	
	/**
	 * 查询pcname集合中元素对应饿pcid
	 * @param
	 * @return   当pcname为null时 结果返回为null
	 */
	public HashMap<String,String> queryCausesNameMappingId(Collection<String> pcnames){
		if(pcnames == null) {
		    return null;
        }
		if(ret==null){
			ret = new HashMap<>();
			String sql = "select pc_id  from probableCauses ";
			StringBuilder condition = new StringBuilder("where ");
			int length = pcnames.size();
			int i = 0;
			Object[] params = new Object[length];
			for(String pcname: pcnames){
				String upName = pcname.replace("_", "").replace(" ", "").toUpperCase();
				params[i] = upName;
				
				condition.append(" upper(pc_id)= ? ");
				if(i < length -1){
					condition.append(" or ");
				}
				i++;
			}
			if(length > 0){
				sql = sql + condition.toString();
			}
			
			List<HashMap<Object, Object>>  result = mgr.query(sql, params);
			for(HashMap<Object, Object> unit: result){
				String id = (String)unit.get("pc_id");
				String name = (String)unit.get("pc_id");
				name = name.toUpperCase();
				ret.put(name, id);
			}
		}
		 
		 return ret;
	}
	public void insert(List<AlarmBean> alarms) {
		String sql = "insert or ignore into Alarms "
				+ "(alarmObjectType,friendlyname,occureTime,probableCause"
				+ ",alarmID,alarmType,alarmLevel,acknowledgementStatus,reservationStatus"
				+ ",clearingStatus,clearableStatus,nbi_Inst,repetitionCounter,managementObjectClass"
				+ ",alarmObject)"
				+ " Values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,?)";

		List<Object[]> paramList = new ArrayList<Object[]>();

		for (AlarmBean alarm : alarms) {
			Object[] params = { alarm.getAlarmObjectType(),
					alarm.getFriendlyName(), alarm.getOccurTime(),
					alarm.getProbableCause(), alarm.getAlarmId(),
					alarm.getAlarmType(), alarm.getAlarmLevel().toValue(),
					alarm.getAcknowledgementStatus(),
					alarm.getReservationStatus(), alarm.getClearingStatus(),
					alarm.getClearableStatus(), alarm.getNbi_inst(),
					alarm.getRepetitionCounter(),
					alarm.getManagementObjectClass(), alarm.getAlarmObject() };

			paramList.add(params);
		}
		mgr.doBatchSql(sql, paramList);

	}

	/**
	 * 执行ack相关操作
	 * 
	 * @param alarms
	 */
	public void ackRelated(List<AlarmBean> alarms) {
		if (alarms == null || alarms.size() <= 0) {
            return;
        }
		int size = alarms.size();
		String[] batchSqls = new String[size];
		int index = 0;
		for (AlarmBean alarm : alarms) {
			int ackStatus = alarm.getAcknowledgementStatus();
			String nbi_inst = alarm.getNbi_inst();
			String alarmId = alarm.getAlarmId();
			String sql = " update alarms set acknowledgementStatus = "
					+ ackStatus + " where nbi_inst= '" + nbi_inst
					+ "' and alarmId='" + alarmId + "'";
			batchSqls[index] = sql;
			index++;
		}

        long stamp = 0;
        try {
            //stamp = lock.tryWriteLock(LOCK_TIMEOUT, TimeUnit.SECONDS);
			stamp = lock.writeLock();
            mgr.doBatchSql(batchSqls);
        } catch (Exception e) {
			log.error(e.getMessage(), e);
        } finally {
			if (stamp != 0) {
				lock.unlockWrite(stamp);
			}
        }
	}


	public void updateAlarms(List<AlarmBean> alarms) {
        if (alarms == null || alarms.size() <= 0) {
            return;
        }

        String sql = " update alarms set " +
                " occuretime=?, acknowledgementstatus=?, " +
                " reservationstatus=?, clearingstatus=?, clearablestatus=?, repetitioncounter=? "
                + " where nbi_inst=? and alarmId=?";

        List<Object[]> paramList = new ArrayList<Object[]>();

        for (AlarmBean alarm : alarms) {
            Object[] params = {
                    alarm.getOccurTime(),
                    alarm.getAcknowledgementStatus(),
                    alarm.getReservationStatus(),
                    alarm.getClearingStatus(),
                    alarm.getClearableStatus(),
                    alarm.getRepetitionCounter(),
                    alarm.getNbi_inst(),
                    alarm.getAlarmId()
            };

            paramList.add(params);
        }

        long stamp = 0;
        try {
            //stamp = lock.tryWriteLock(LOCK_TIMEOUT, TimeUnit.SECONDS);
			stamp = lock.writeLock();
            mgr.doBatchSql(sql, paramList);
        } catch (Exception e) {
			log.error(e.getMessage(), e);
        } finally {
        	if (stamp != 0) {
				lock.unlockWrite(stamp);
			}
        }
    }

	/**
	 * 对alarm执行reserve相关操作
	 * @param alarms
	 */
	public void reserveRelated(List<AlarmBean> alarms) {
		if (alarms == null || alarms.size() <= 0) {
			return;
		}
		int size = alarms.size();
		String[] batchSqls = new String[size];
		int index = 0;
		for (AlarmBean alarm : alarms) {
			int reserveStatus = alarm.getReservationStatus();
			String nbi_inst = alarm.getNbi_inst();
			String alarmId = alarm.getAlarmId();
			String sql = " update alarms set ReservationStatus = "
					+ reserveStatus + " where nbi_inst= '" + nbi_inst
					+ "' and alarmId='" + alarmId + "'";
			batchSqls[index] = sql;
			index++;
		}

        long stamp = 0;
        try {
            //stamp = lock.tryWriteLock(LOCK_TIMEOUT, TimeUnit.SECONDS);
			stamp = lock.writeLock();
            mgr.doBatchSql(batchSqls);
        } catch (Exception e) {
			log.error(e.getMessage(), e);
        } finally {
			if (stamp != 0) {
				lock.unlockWrite(stamp);
			}
        }
	}

	public void cleared(List<AlarmBean> alarms) {
		if (alarms == null || alarms.size() <= 0) {
            return;
        }
		String sql = "insert or ignore into alarms(alarmObjectType,friendlyname,occureTime,probableCause,alarmID,"
				+ " alarmType,alarmLevel,acknowledgementStatus,reservationStatus,clearingStatus,clearableStatus,"
				+ " nbi_Inst,repetitionCounter,managementObjectClass "
				+ ",alarmObject) values "
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";

		List<Object[]> paramList = new ArrayList<Object[]>();

		// for(AlarmBean alarm: alarms){
		// Object[] params = {alarm.getAlarmId(), alarm.getNbi_inst()};
		// paramList.add(params);
		// }
		for (AlarmBean alarm : alarms) {
			Object[] params = {
					alarm.getAlarmObjectType(),
					alarm.getFriendlyName(),
					alarm.getOccurTime(),
					alarm.getProbableCause(),
					alarm.getAlarmId(),
					alarm.getAlarmType(),
					alarm.getAlarmLevel() == null ? null : alarm
							.getAlarmLevel().toValue(),
					alarm.getAcknowledgementStatus(),
					alarm.getReservationStatus(), alarm.getClearingStatus(),
					alarm.getClearableStatus(), alarm.getNbi_inst(),
					alarm.getRepetitionCounter(),
					alarm.getManagementObjectClass(), alarm.getAlarmObject(), };

			paramList.add(params);
		}

        long stamp = 0;
        try {
            //stamp = lock.tryWriteLock(LOCK_TIMEOUT, TimeUnit.SECONDS);
			stamp = lock.writeLock();
            mgr.doBatchSql(sql, paramList);
        } catch (Exception e) {
			log.error(e.getMessage(), e);
        } finally {
			if (stamp != 0) {
				lock.unlockWrite(stamp);
			}
        }
	}

	public void saveCreationAlarms(List<AlarmBean> alarms) {
		/*
		 * String sql =
		 * "insert into alarms(alarmObjectType,neName,alarmSource,occureTime,probableCause,alarmID,"
		 * +
		 * " alarmType,alarmLevel,acknowledgementStatus,reservationStatus,clearingStatus,clearableStatus,"
		 * + " nbi_Inst,repetitionCounter,managementObjectClass " +
		 * ",alarmObject) values " + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ) " +
		 * " on duplicate key update  alarmObjectType = ?,neName = ?," +
		 * " alarmSource=?,occureTime=?,probableCause=?,alarmID=?,alarmType = ?, alarmlevel = ?, "
		 * + "acknowledgementStatus=?,reservationStatus=?,clearingStatus=?," +
		 * " clearableStatus=?,nbi_Inst=?,repetitionCounter=repetitionCounter+1,"
		 * + " managementObjectClass=?,alarmObject=? ";
		 */
		if(alarms == null || alarms.isEmpty()) {
			return;
		}
		String sql = "insert or ignore into alarms(alarmObjectType,friendlyname,occureTime,probableCause,alarmID,"
				+ " alarmType,alarmLevel,acknowledgementStatus,reservationStatus,clearingStatus,clearableStatus,"
				+ " nbi_Inst,repetitionCounter,managementObjectClass "
				+ ",alarmObject,inserttime) values "
				+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";

		List<Object[]> paramList = new ArrayList<Object[]>();

		for (AlarmBean alarm : alarms) {
			Object[] params = { alarm.getAlarmObjectType(),
					alarm.getFriendlyName(), alarm.getOccurTime(),
					alarm.getProbableCause(), alarm.getAlarmId(),
					alarm.getAlarmType(), alarm.getAlarmLevel().toValue(),
					alarm.getAcknowledgementStatus(),
					alarm.getReservationStatus(), alarm.getClearingStatus(),
					alarm.getClearableStatus(), alarm.getNbi_inst(),
					alarm.getRepetitionCounter(),
					alarm.getManagementObjectClass(), alarm.getAlarmObject(),alarm.getInsertTime() };
			paramList.add(params);
		}

        long stamp = 0;
        try {
            //stamp = lock.tryWriteLock(LOCK_TIMEOUT, TimeUnit.SECONDS);
			stamp = lock.writeLock();
            mgr.doBatchSql(sql, paramList);
        } catch (Exception e) {
			log.error(e.getMessage(), e);
        } finally {
			if (stamp != 0) {
				lock.unlockWrite(stamp);
			}
        }
	}

	public void delete(List<AlarmBean> alarms) {
		if(alarms == null || alarms.isEmpty()) {
		    return;
        }
		String sql = "delete from Alarms where AlarmID = ? and NBI_Inst = ? ";
		List<Object[]> paramList = new ArrayList<Object[]>();

		for (AlarmBean alarm : alarms) {
			Object[] params = { alarm.getAlarmId(), alarm.getNbi_inst() };
			paramList.add(params);
		}

        long stamp = 0;
        try {
            //stamp = lock.tryWriteLock(LOCK_TIMEOUT, TimeUnit.SECONDS);
			stamp = lock.writeLock();
            mgr.doBatchSql(sql, paramList);
        } catch (Exception e) {
			log.error(e.getMessage(), e);
        } finally {
			if (stamp != 0) {
				lock.unlockWrite(stamp);
			}
        }
	}
	
	public void deleteNoNbi(List<AlarmBean> alarms) {
		if(alarms == null || alarms.isEmpty()) {
		    return;
        }
		String sql = "delete from Alarms where AlarmID = ?";
		List<Object[]> paramList = new ArrayList<Object[]>();

		for (AlarmBean alarm : alarms) {
			Object[] params = { alarm.getAlarmId() };
			paramList.add(params);
		}

        long stamp = 0;
        try {
            //stamp = lock.tryWriteLock(LOCK_TIMEOUT, TimeUnit.SECONDS);
			stamp = lock.writeLock();
            mgr.doBatchSql(sql, paramList);
        } catch (Exception e) {
			log.error(e.getMessage(), e);
        } finally {
			if (stamp != 0) {
				lock.unlockWrite(stamp);
			}
        }
	}

	public void insert(AlarmBean alarm) {
		String sql = "insert or ignore into Alarms "
				+ "(alarmObjectType,friendlyname,occureTime,probableCause"
				+ ",alarmID,alarmType,alarmLevel,acknowledgementStatus,reservationStatus"
				+ ",clearingStatus,clearableStatus,nbi_Inst,repetitionCounter,managementObjectClass"
				+ ",alarmObject)" + " Values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?)";

		Object[] params = { alarm.getAlarmObjectType(),
				alarm.getFriendlyName(), alarm.getOccurTime(),
				alarm.getProbableCause(), alarm.getAlarmId(),
				alarm.getAlarmType(), alarm.getAlarmLevel().toValue(),
				alarm.getAcknowledgementStatus(), alarm.getReservationStatus(),
				alarm.getClearingStatus(), alarm.getClearableStatus(),
				alarm.getNbi_inst(), alarm.getRepetitionCounter(),
				alarm.getManagementObjectClass(), alarm.getAlarmObject() };

        long stamp = 0;
        try {
            //stamp = lock.tryWriteLock(LOCK_TIMEOUT, TimeUnit.SECONDS);
			stamp = lock.writeLock();
            mgr.doSql(sql, params);
        } catch (Exception e) {
			log.error(e.getMessage(), e);
        } finally {
			if (stamp != 0) {
				lock.unlockWrite(stamp);
			}
        }
	}

	public List<AlarmBean> query(String sqlWhere) throws Exception {
		List<AlarmBean> alarms = new ArrayList<AlarmBean>();
		String sql = "select alarmObjectType,friendlyname,occureTime,probableCause"
				+ ",alarmID,alarmType,alarmLevel,acknowledgementStatus,reservationStatus"
				+ ",clearingStatus,clearableStatus,nbi_Inst,repetitionCounter,managementObjectClass"
				+ ",alarmObject " + "from Alarms where 1=1 " + sqlWhere + "";
		Connection conn = null;
		try {
			conn = mgr.getConnection();
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(sql);
			while (rs.next()) {
				AlarmBean alarm = new AlarmBean();
				alarm.setAlarmObjectType(rs.getString("alarmObjectType"));
				alarm.setFriendlyName(rs.getString("friendlyname"));
				alarm.setOccurTime(rs.getTimestamp("occureTime"));
				alarm.setProbableCause(rs.getString("probableCause"));

				alarm.setAlarmId(rs.getString("alarmID"));
				alarm.setAlarmType(rs.getString("alarmType"));
				alarm.setAlarmLevel(AlarmLevel.fromInt(rs.getInt("alarmLevel"),
						false));
				alarm.setAcknowledgementStatus(rs
						.getInt("acknowledgementStatus"));
				alarm.setReservationStatus(rs.getInt("reservationStatus"));

				alarm.setClearingStatus(rs.getInt("clearingStatus"));
				alarm.setClearableStatus(rs.getInt("clearableStatus"));

				alarm.setNbi_inst(rs.getString("nbi_Inst"));
				alarm.setRepetitionCounter(rs.getInt("repetitionCounter"));
				alarm.setManagementObjectClass(rs
						.getString("managementObjectClass"));
				alarm.setAlarmObject(rs.getString("alarmObject"));
				alarms.add(alarm);
			}
			mgr.close(conn, stat, rs);
		} catch (Exception ex) {
			// logger.error("fail to get pg events", ex);
			throw new Exception(ex.getMessage());
		}
		return alarms;
	}

	public AlarmBean getAlarmByIdAndInst(String id, String inst) {
		AlarmBean alarm = null;
		String sql = "select alarmObjectType,friendlyname,occureTime,probableCause"
				+ ",alarmID,alarmType,alarmLevel,acknowledgementStatus,reservationStatus"
				+ ",clearingStatus,clearableStatus,nbi_Inst,repetitionCounter,managementObjectClass"
				+ ",alarmObject " + "from Alarms where 1=1 and alarmid = ? and NBI_Inst = ?";
		Connection conn = null;
		try {
			conn = mgr.getConnection();
			PreparedStatement stat = conn.prepareStatement(sql);
			stat.setString(1,id);
			stat.setString(2,inst);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				alarm = new AlarmBean();
				alarm.setAlarmObjectType(rs.getString("alarmObjectType"));
				alarm.setFriendlyName(rs.getString("friendlyname"));
				alarm.setOccurTime(rs.getTimestamp("occureTime"));
				alarm.setProbableCause(rs.getString("probableCause"));

				alarm.setAlarmId(rs.getString("alarmID"));
				alarm.setAlarmType(rs.getString("alarmType"));
				alarm.setAlarmLevel(AlarmLevel.fromInt(rs.getInt("alarmLevel"),
						false));
				alarm.setAcknowledgementStatus(rs
						.getInt("acknowledgementStatus"));
				alarm.setReservationStatus(rs.getInt("reservationStatus"));

				alarm.setClearingStatus(rs.getInt("clearingStatus"));
				alarm.setClearableStatus(rs.getInt("clearableStatus"));

				alarm.setNbi_inst(rs.getString("nbi_Inst"));
				alarm.setRepetitionCounter(rs.getInt("repetitionCounter"));
				alarm.setManagementObjectClass(rs
						.getString("managementObjectClass"));
				alarm.setAlarmObject(rs.getString("alarmObject"));
				break;
			}
			mgr.close(conn, stat, rs);
		} catch (Exception e) {
			log.error("fail to getAlarmById", e);
		}
		return alarm;
	}

    public List<AlarmBean> getAllAlarmBeans() {
        long stamp = lock.tryOptimisticRead();
        List<AlarmBean> alarmBeans = getAlarmBeans();
        if (!lock.validate(stamp)) {
            try {
                //stamp = lock.tryReadLock(LOCK_TIMEOUT, TimeUnit.SECONDS);
				stamp = lock.readLock();
                alarmBeans = getAlarmBeans();
            } catch (Exception e) {
				log.error(e.getMessage(), e);
            } finally {
                lock.unlockRead(stamp);
            }
        }

        return alarmBeans;
    }

	private List<AlarmBean> getAlarmBeans() {
		List<AlarmBean> alarmBeans = new ArrayList<>();
		String sql = "select * from alarms";
		Connection conn;
		try {
			conn = mgr.getConnection();
			PreparedStatement stat = conn.prepareStatement(sql);
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				AlarmBean alarm = new AlarmBean();
				alarm.setAlarmObjectType(rs.getString("alarmObjectType"));
				alarm.setFriendlyName(rs.getString("friendlyname"));
				alarm.setOccurTime(rs.getTimestamp("occureTime"));
				alarm.setProbableCause(rs.getString("probableCause"));

				alarm.setAlarmId(rs.getString("alarmID"));
				alarm.setAlarmType(rs.getString("alarmType"));
				alarm.setAlarmLevel(AlarmLevel.fromInt(rs.getInt("alarmLevel"),
						false));
				alarm.setAcknowledgementStatus(rs
						.getInt("acknowledgementStatus"));
				alarm.setReservationStatus(rs.getInt("reservationStatus"));

				alarm.setClearingStatus(rs.getInt("clearingStatus"));
				alarm.setClearableStatus(rs.getInt("clearableStatus"));

				alarm.setNbi_inst(rs.getString("nbi_Inst"));
				alarm.setRepetitionCounter(rs.getInt("repetitionCounter"));
				alarm.setManagementObjectClass(rs
						.getString("managementObjectClass"));
				alarm.setAlarmObject(rs.getString("alarmObject"));
				alarmBeans.add(alarm);
			}
			mgr.close(conn, stat, rs);
		} catch (Exception e) {
			log.error("fail to getAllAlarms", e);
		}
		return alarmBeans;
	}


	public List<Alarm> getAllDefinedAlarms() {
		long stamp = lock.tryOptimisticRead();
		Set<String> allDefinedAlarms = AlarmHelper.getAllDefinedAlarms();
		String condition = generateSQLForStringCondition("probablecause", allDefinedAlarms);
		List<Alarm> alarms = queryAlarms(condition);
		if (!lock.validate(stamp)) {
			try {
				//stamp = lock.tryReadLock(LOCK_TIMEOUT, TimeUnit.SECONDS);
				stamp = lock.readLock();
				alarms = queryAlarms(condition);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				lock.unlockRead(stamp);
			}
		}

		return alarms;
	}

    public List<Alarm> getAllAlarms() {
        long stamp = lock.tryOptimisticRead();
        List<Alarm> alarms = queryAlarms("");
        if (!lock.validate(stamp)) {
            try {
                //stamp = lock.tryReadLock(LOCK_TIMEOUT, TimeUnit.SECONDS);
				stamp = lock.readLock();
                alarms = queryAlarms("");
            } catch (Exception e) {
				log.error(e.getMessage(), e);
            } finally {
                lock.unlockRead(stamp);
            }
        }

        return alarms;
    }

    private List<Alarm> queryAlarms(String condition) {
        List<Alarm> alarms = new ArrayList<>();
		String sql = "select * from alarms where 1=1 ";

		if(!StringUtils.isEmpty(condition)) {
			sql += condition;
		}

        Connection conn;
        try {
            conn = mgr.getConnection();
            PreparedStatement stat = conn.prepareStatement(sql);
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                Alarm alarm = new Alarm();
                alarm.setFriendlyName(rs.getString("friendlyname"));
                alarm.setOccurrentTime(rs.getString("occureTime"));
                alarm.setProbableCauseId(rs.getString("probableCause"));
                alarm.setProbableCause(alarmProbableLocalMap.getOrDefault(alarm.getProbableCauseId(), alarm.getProbableCauseId()));
                alarm.setAlarmId(rs.getString("alarmID"));
                alarm.setClearTime(rs.getString("cleartime") == null ? "0" : rs.getString("cleartime"));
                alarm.setAlarmType(rs.getString("alarmType"));
                alarm.setSeverity(AlarmLevel.fromInt(rs.getInt("alarmLevel"), false).toString());
                alarm.setServer(rs.getString("nbi_Inst"));
                alarm.setEventType("alarmCreation");
				alarm.setIsRootCause(false);
                alarms.add(alarm);
            }
            mgr.close(conn, stat, rs);
        } catch (Exception e) {
            log.error("fail to getAllAlarms", e);
        }
        return alarms;
    }

	public void delete(String sqlWhere) {
		String sql = "delete from Alarms where 1=1 " + sqlWhere + "";

        long stamp = 0;
        try {
            //stamp = lock.tryWriteLock(LOCK_TIMEOUT, TimeUnit.SECONDS);
			stamp = lock.writeLock();
            mgr.doSql(sql, null);
        } catch (Exception e) {
			log.error(e.getMessage(), e);
        } finally {
			if (stamp != 0) {
				lock.unlockWrite(stamp);
			}
        }
	}

	public void createTable() {
		String sql = "CREATE TABLE \"main\".\"alarms\" (" +
                "\"alarmobjecttype\"  VARCHAR(50)," +
                "\"occuretime\"  TEXT," +
                "\"cleartime\"  TEXT," +
                "\"probablecause\"  VARCHAR(100)," +
                "\"alarmid\"  VARCHAR(20) NOT NULL," +
                "\"alarmtype\"  VARCHAR(40)," +
                "\"alarmlevel\"  INT," +
                "\"acknowledgementstatus\"  INT," +
                "\"reservationstatus\"  INT," +
                "\"clearingstatus\"  INT," +
                "\"clearablestatus\"  INT," +
                "\"nbi_inst\"  VARCHAR(10) NOT NULL," +
                "\"repetitioncounter\"  INT," +
                "\"managementobjectclass\"  VARCHAR(128)," +
                "\"alarmobject\"  VARCHAR(200)," +
                "\"friendlyname\"  VARCHAR(255)," +
                "\"inserttime\"  INT8,\n" +
                "PRIMARY KEY (\"probablecause\", \"friendlyname\"));";
		mgr.doSql(sql, null);
	}

	public boolean HasTable(String name) {
		// 判断某一个表是否存在
		boolean result = false;
		try {
			DatabaseMetaData meta = (DatabaseMetaData) mgr.getConnection()
					.getMetaData();
			ResultSet set = meta.getTables(null, null, name, null);
			while (set.next()) {
				result = true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	public String generateSQLForStringCondition(String fieldName, Collection<String> parameters) {
		if(CollectionUtils.isEmpty(parameters)) {
			return "";
		}

		StringBuilder str = new StringBuilder(" AND " + fieldName + " IN (");

		parameters.forEach(p-> str.append("'"+p.trim()+"',"));

		return str.substring(0, str.lastIndexOf(",")) + ")";
	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		System.out.println("start:"+start);
		List<AlarmBean> list = AlarmDao.instance().getAllAlarmBeans();
		long end = System.currentTimeMillis();
		System.out.println("length:"+list.size());
		System.out.println("end:"+end);
		System.out.println("cost:"+ (end-start)*1.0/1000);
	}

}
