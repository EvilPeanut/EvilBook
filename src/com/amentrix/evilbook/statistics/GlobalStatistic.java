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
	EconomyGrowth("economy_growth"),
	EconomyTrade("economy_trade"),
	LoginTotal("login_total"),
	LoginNewPlayers("new_players"),
	CommandsExecuted("commands_executed"),
	MessagesSent("messages_sent"),
	BlocksBroken("blocks_broken"),
	BlocksPlaced("blocks_placed");
	
	private String columnName;
	
	GlobalStatistic(String columnName) {
		this.columnName = columnName;
	}
	
	public static String getStatistic(GlobalStatistic statistic) {
		//TODO: SQL: Change to SQL.getInteger()
		String value = SQL.getString(TableType.Statistics, statistic.columnName, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
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
		SQL.setValue(TableType.Statistics, statistic.columnName, new SimpleDateFormat("yyyy-MM-dd").format(new Date()), value);
	}
	
	public static void incrementStatistic(GlobalStatistic statistic, int increment) {
		setStatistic(statistic, Integer.parseInt(getStatistic(statistic)) + increment);
	}
}
