package com.amentrix.evilbook.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.amentrix.evilbook.main.Rank;

/**
 * SQL query framework
 * @author Reece Aaron Lecrivain
 */
public class SQLQuery {
	private TableType tableType;
	private String fields = "";
	private String keyValue, keyName;
	private ResultSet resultSet;
	private Statement statement;
	
	public SQLQuery(TableType tableType, String keyName, String keyValue) {
		this.tableType = tableType;
		this.keyName = keyName;
		this.keyValue = keyValue;
	}
	
	public void addField(String field) {
		fields += fields == "" ? field : "," + field;
	}
	
	public void execute() {
		try {
			statement = SQL.connection.createStatement();
			resultSet = statement.executeQuery("SELECT " + fields + " FROM " + SQL.database + "." + tableType.getName() + " WHERE " + keyName + "='" + keyValue + "';");
			resultSet.next();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getString(String columnName) {
		try {
			return resultSet.getString(columnName);
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return null;
	}
	
	public Integer getInteger(String columnName) {
		try {
			return resultSet.getInt(columnName);
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return null;
	}
	
	public Float getFloat(String columnName) {
		try {
			return resultSet.getFloat(columnName);
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return null;
	}
	
	public Rank getRank(String columnName) {
		try {
			return Rank.valueOf(resultSet.getString(columnName));
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return null;
	}
	
	public void close() {
		try {
			statement.close();
			resultSet.close();
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
	}
}
