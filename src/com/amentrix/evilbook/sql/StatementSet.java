package com.amentrix.evilbook.sql;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StatementSet {
	private List<String> statementList = new ArrayList<>();
	
	public void setProperty(TableType table, String key, String property, Object value) {
		if (value instanceof String) {
			this.statementList.add("UPDATE " + SQL.database + "." + table.getName() + " SET " + property + (value.equals("NULL") ? "=NULL WHERE " : "='" + value + "' WHERE ") + table.getKey() + "='" + key + "';");
		} else if (value instanceof Integer) {
			this.statementList.add("UPDATE " + SQL.database + "." + table.getName() + " SET " + property + (value.equals("NULL") ? "=NULL WHERE " : "=" + value + " WHERE ") + table.getKey() + "='" + key + "';");
		}
	}
	
	public void execute() {
		try (Statement statement = SQL.connection.createStatement()) {
			for (String state : this.statementList) statement.addBatch(state);
			statement.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}