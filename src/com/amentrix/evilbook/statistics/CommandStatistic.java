package com.amentrix.evilbook.statistics;

import java.sql.Statement;

import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;

/**
 * Command statistics framework
 * @author Reece Aaron Lecrivain
 */
public class CommandStatistic {
	public static void increment(String commandName) {
		try (Statement statement = SQL.connection.createStatement()) {
			statement.execute("INSERT INTO " + SQL.database + "." + TableType.CommandStatistics.tableName + "(command_name, execution_count) VALUES ('" + commandName + "', 1) ON DUPLICATE KEY UPDATE execution_count = execution_count + 1;");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
