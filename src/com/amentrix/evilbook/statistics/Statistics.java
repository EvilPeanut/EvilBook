package com.amentrix.evilbook.statistics;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;

/**
 * Statistics framework
 * @author Reece Aaron Lecrivain
 */
public class Statistics {
	public static String getStatistic(Statistic statistic) {
		String value = SQL.getProperty(TableType.Statistics, new SimpleDateFormat("yyyy-MM-dd").format(new Date()), statistic.columnName);
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

	public static void setStatistic(Statistic statistic, String value) {
		SQL.setProperty(TableType.Statistics, new SimpleDateFormat("yyyy-MM-dd").format(new Date()), statistic.columnName, value);
	}
	
	public static void incrementStatistic(Statistic statistic, int increment) {
		setStatistic(statistic, Integer.toString(Integer.parseInt(getStatistic(statistic)) + increment));
	}
}