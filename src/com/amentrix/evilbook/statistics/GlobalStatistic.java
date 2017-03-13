package com.amentrix.evilbook.statistics;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;

/**
 * Global statistic enumerator
 * @author Reece Aaron Lecrivain
 */
public enum GlobalStatistic {
	ECONOMY_GROWTH,
	ECONOMY_TRADE,
	LOGIN_TOTAL,
	NEW_PLAYERS,
	COMMANDS_EXECUTED,
	MESSAGES_SENT,
	BLOCKS_BROKEN,
	BLOCKS_PLACED;
	
	public static String getStatistic(GlobalStatistic statistic) {
		//TODO: SQL: Change to SQL.getInteger()
		String value = SQL.getString(TableType.Statistics, statistic.name(), new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		if (value == null) {
			try {
				SQL.insert(TableType.Statistics, "'" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "','0','0','0','0','0','0','0','0'");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "0";
		}
		return value;
	}

	private static void setStatistic(GlobalStatistic statistic, int value) {
		SQL.setValue(TableType.Statistics, statistic.name(), new SimpleDateFormat("yyyy-MM-dd").format(new Date()), value);
	}
	
	public static void incrementStatistic(GlobalStatistic statistic, int increment) {
		setStatistic(statistic, Integer.parseInt(getStatistic(statistic)) + increment);
	}
}
