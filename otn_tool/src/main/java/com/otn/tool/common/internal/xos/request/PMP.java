package com.otn.tool.common.internal.xos.request;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.otn.tool.common.internal.xos.request.EmlAttributes.PMP.SnmpPmDomain;
import com.alu.tools.basic.function.IPredicate;
import com.alu.tools.basic.function.PredicateUtil;

public class PMP {
	public static enum Location {
		PML_NEAR_END_Rx, PML_FAR_END_Rx, PML_FORWARD, PML_BACKWARD, PML_BIDIRECTIONAL, PML_NEAR_END, PML_FAR_END, PML_ALL, PML_NA
	}

	public static enum Direction {
		PMD_RVC, PMD_TRMT, PMD_ALL, PMD_NA
	}

	public static enum Granularity {
		GRN_15M, GRN_1H, GRN_24H, GRN_BOTH, GRN_NA
	}

	public static enum ThresholdType {
		highest, high, low, lowest
	}

	public static class Threshold {
		private ThresholdType type;

		private boolean trigger;

		private long value;

		public Threshold(ThresholdType type, boolean trigger, long value) {
			this.type = type;
			this.trigger = trigger;
			this.value = value;
		}

		@Override
		public String toString() {
			return type + ", " + (trigger ? "trigger" : "clear") + value;
		}

		public ThresholdType getType() {
			return type;
		}

		public boolean isTrigger() {
			return trigger;
		}

		public long getValue() {
			return value;
		}
	}

	public static class ParameterWithThresholds {
		private String paramName;

		List<Threshold> thresholds;

		public ParameterWithThresholds(String paramName) {
			this.paramName = paramName;
		}

		@Override
		public String toString() {
			return "'" + paramName + "'"
					+ (thresholds == null ? "" : "{" + thresholds + "}");
		}

		public void addThreshold(Threshold threshold) {
			if (thresholds == null)
				thresholds = new LinkedList<Threshold>();
			thresholds.add(threshold);
		}

		public String getParamName() {
			return paramName;
		}

		public List<Threshold> getThresholds() {
			return thresholds;
		}
	}

	private String tcmName = "";

	private int groupID;

	private int neID;

	private String neName;

	private String ptpName;

	private String ftpName;

	private String ctpName;

	private SnmpPmDomain domain;

	private String tableOid;

	private int ifIndex;

	private String pmpName;

	private short layerRate;

	private Location location;

	private Direction direction;

	private Granularity granularity;

	private Collection<ParameterWithThresholds> paramWithThresholdsList = new LinkedList<ParameterWithThresholds>();

	private PMP(int groupID, int neID, String neName, String ptpName,
			String ftpName, String ctpName, SnmpPmDomain domain,
			String tableOid, int ifIndex, String pmpName, short layerRate,
			Location location, Direction direction, Granularity granularity) {
		this.groupID = groupID;
		this.neID = neID;
		this.neName = neName;
		this.ptpName = ptpName;
		this.ftpName = ftpName;
		this.ctpName = ctpName;
		this.domain = domain;
		this.tableOid = tableOid;
		this.ifIndex = ifIndex;
		this.pmpName = pmpName;
		this.layerRate = layerRate;
		this.location = location;
		this.direction = direction;
		this.granularity = granularity;
	}

	public PMP(int groupID, int neID, String neName, String ptpName,
			String ftpName, String ctpName, String pmpName, short layerRate,
			Location location, Direction direction, Granularity granularity) {
		this(groupID, neID, neName, ptpName, ftpName, ctpName, null, null, -1,
				pmpName, layerRate, location, direction, granularity);
	}

	public PMP(int groupID, int neID, String neName, SnmpPmDomain domain,
			String tableOid, int ifIndex, String pmpName, short layerRate,
			Location location, Direction direction, Granularity granularity) {
		this(groupID, neID, neName, null, null, null, domain, tableOid,
				ifIndex, pmpName, layerRate, location, direction, granularity);
	}

	@Override
	public String toString() {
		return "GroupID: " + groupID + ", NeID: " + neID + ", NeName: '"
				+ neName + "', PTP: '" + ptpName +"', FTP: '" + ftpName + "', CTP: '" + ctpName +"', SnmpPmDomain: " + domain
				+ ", IfIndex: " + ifIndex + ", Name: '" + pmpName
				+ "', LayerRate: " + layerRate + ", Location: " + location
				+ ", Direction: " + direction + ", Granularity: " + granularity
				+ ", Params: " + paramWithThresholdsList;
	}

	public void addParam(String paramName) {
		paramWithThresholdsList.add(new ParameterWithThresholds(paramName));
	}

	public void addParam(String[] paramNames) {
		for (String param : paramNames) {
			addParam(param);
		}
	}

	public ParameterWithThresholds find(final String paramName) {
		return PredicateUtil.findFirst(paramWithThresholdsList,
				new IPredicate<ParameterWithThresholds>() {
					public boolean apply(
							ParameterWithThresholds paramWithThresholds) {
						return paramName.equals(paramWithThresholds
								.getParamName());
					}
				});
	}

	public int getGroupID() {
		return groupID;
	}

	public int getNeID() {
		return neID;
	}

	public String getNeName() {
		return neName;
	}

	public String getPtpName() {
		return ptpName;
	}
	
	public String getFtpName() {
		return ftpName;
	}
	
	public String getCtpName() {
		return ctpName;
	}

	public SnmpPmDomain getDomain() {
		return domain;
	}

	public String getTableOid() {
		return tableOid;
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public String getPmpName() {
		return pmpName;
	}

	public short getLayerRate() {
		return layerRate;
	}

	public Location getLocation() {
		return location;
	}

	public Direction getDirection() {
		return direction;
	}

	public Granularity getGranularity() {
		return granularity;
	}

	public Collection<ParameterWithThresholds> getParamWithThresholdsList() {
		return paramWithThresholdsList;
	}

	public String getTcmName() {
		return tcmName;
	}

	public void setTcmName(String tcmName) {
		this.tcmName = tcmName;
	}
}
