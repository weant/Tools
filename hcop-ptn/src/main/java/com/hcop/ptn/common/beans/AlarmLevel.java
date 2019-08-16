package com.hcop.ptn.common.beans;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public enum AlarmLevel {
	// 已清除
	CLEARED,
	// 指示告警
	INDETERMINATE,
	// 警告告警
	WARNING,
	// 次要告警
	MINOR,
	// 主要告警
	MAJOR,
	// 紧急告警
	CRITICAL,
	// 挂起
	PENDING,
	// 全部
	ALL;

	/***
	* @Description remove the alarmLevel : CLEARED
	*/
	private static AlarmLevel[] alarmLevel = { ALL, CRITICAL, MAJOR, MINOR, WARNING, INDETERMINATE, PENDING, CLEARED };
	
	/***
	* @Description 此alarmLevel数组里面，定义了存在数据库probable_sort里的Level数据的信息，请别轻易变动此数组的顺序和值。它关系到告警发送短信功能是否能正常工作。
	*/
	private static AlarmLevel[] alarmChooserLevel = { CRITICAL, MAJOR, MINOR, WARNING, INDETERMINATE, PENDING, CLEARED };

	private static AlarmLevel[] alarmChooserLevelUsedOnlyForUpdate = { CRITICAL, MAJOR, MINOR, WARNING, INDETERMINATE, PENDING, CLEARED };
	
	public static AlarmLevel[] getAlarmLevel() {

		return alarmLevel;
	}
	
	public static AlarmLevel[] getAlarmChooserLevel() {
		return alarmChooserLevel;
	}

	public static AlarmLevel[] getAlarmChooserLevelUsedOnlyForUpdate() {
		return alarmChooserLevelUsedOnlyForUpdate;
	}
	
	public static List<AlarmLevel> getSelectedLevlesByLCUsedOnlyForUpdate(String level) {
		List<AlarmLevel> levels = new ArrayList<>();
		if (level == null || level.isEmpty() || level.length() < 6) {
			return levels;
		}
		int i = 0;
		for (AlarmLevel config : alarmChooserLevelUsedOnlyForUpdate) {
			char value = level.charAt(i);
			if (value != -1 && "1".charAt(0) == value) {
				levels.add(config);
			}
			i ++;
		}
		return levels;
	}
	
	public static List<AlarmLevel> getSelectedLevlesByLC(String level) {
		List<AlarmLevel> levels = new ArrayList<>();
		if (level == null || level.isEmpty() || level.length() < alarmChooserLevel.length) {
			return levels;
		}
		int i = 0;
		for (AlarmLevel config : alarmChooserLevel) {
			char value = level.charAt(i);
			if (value != -1 && "1".charAt(0) == value) {
				levels.add(config);
			}
			i ++;
		}
		return levels;
	}
	/**
	 * 由于通过corba收到的告警 级别与网管系统告警级别正好是对称的 即是0对6 1对5 2对4 3则是3.所以 如果是从corba收到的告警
	 * 则需要用6来减去级别值。
	 * 
	 * @param value
	 * @return
	 */
	public static AlarmLevel fromInt(int value, boolean fromNMS) {
		if (fromNMS) {
			value = 6 - value;
		}

		switch (value) {
		case 0:
			return CLEARED;
		case 1:
			return INDETERMINATE;
		case 2:
			return WARNING;
		case 3:
			return MINOR;
		case 4:
			return MAJOR;
		case 5:
			return CRITICAL;
		case 6:
			return PENDING;
		default:
			throw new IllegalArgumentException("unknown alarmType");
		}
	}

	public int getIndex() {
		for (int index = 0; index < alarmLevel.length; index++) {
			if (this == alarmLevel[index]) {
				return index;
			}
		}
		return 0;
	}

	public String toLocalString() {
		switch (this) {
			// 已清除
			case CLEARED:
				return CLEARED.toString();
			// 指示告警
			case INDETERMINATE:
				return INDETERMINATE.toString();
			// 警告告警
			case WARNING:
				return WARNING.toString();
			// 次要告警
			case MINOR:
				return MINOR.toString();
			// 主要告警
			case MAJOR:
				return MAJOR.toString();
			// 紧急告警
			case CRITICAL:
				return CRITICAL.toString();
			// 待处理
			case PENDING:
				return PENDING.toString();
			// 全部
			case ALL:
				return ALL.toString();
			default:
				throw new IllegalArgumentException("unknown alarmType");
		}
	}

	/**
	 * return type value
	 * 
	 * @return
	 */
	public int toValue() {
		switch (this) {
		case CLEARED:
			return 0;
		case INDETERMINATE:
			return 1;
		case WARNING:
			return 2;
		case MINOR:
			return 3;
		case MAJOR:
			return 4;
		case CRITICAL:
			return 5;
		case PENDING:
			return 6;
		case ALL:
			return -1;
		default:
			throw new IllegalArgumentException("unknown alarmType");
		}
	}

	public int compare(AlarmLevel level) {
		int curValue = this.toValue();
		int levelValue = level.toValue();
		if (curValue > levelValue) {
			return 1;
		} else if (curValue == levelValue) {
			return 0;
		}
		return -1;

	}

	public Color getPaintColor() {
		switch (this) {
		case CLEARED:
			return Color.GREEN;
		case INDETERMINATE:
			return Color.WHITE;
		case WARNING:
			return Color.CYAN;
		case MINOR:
			return Color.YELLOW;
		case MAJOR:
			return Color.ORANGE;
		case CRITICAL:
			return Color.RED;
		case PENDING:
			return Color.GRAY;
		default:
			throw new IllegalArgumentException("unknown alarmType");
		}
	}

	public short toShort() {
		switch (this) {
		case CLEARED:
			return 42; // 绿色
		case INDETERMINATE:
			return 9; // 白色
		case WARNING:
			return 15; // 蓝色
		case MINOR:
			return 13; // 黄色
		case MAJOR:
			return 52; // 橙色
		case CRITICAL:
			return 10; // 红色
		case PENDING:
			return 22; // 灰色
		default:
			throw new IllegalArgumentException("unknown alarmType");
		}
	}

	public static AlarmLevel getType(String value) {
		switch (value.toUpperCase()) {
		case "CLEARED":
			return CLEARED;
		case "INDETERMINATE":
			return INDETERMINATE;
		case "WARNING":
			return WARNING;
		case "MINOR":
			return MINOR;
		case "MAJOR":
			return MAJOR;
		case "CRITICAL":
			return CRITICAL;
		case "PENDING":
			return PENDING;
		case "INDICATION":
			return INDETERMINATE;
		default:
			return null;
		}

	}

}
