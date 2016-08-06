package com.amentrix.evilbook.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.Rank;

/**
 * SQL framework
 * @author Reece Aaron Lecrivain
 */
public class SQL {

	public static Connection connection;
	
	public static String database;
	
	public static boolean connect() {
		try {
			database = EvilBook.config.getProperty("database");
			
			connection = DriverManager.getConnection("jdbc:mysql://" + EvilBook.config.getProperty("host") + "/" + database + "?user=" + 
					EvilBook.config.getProperty("user") + "&password=" + EvilBook.config.getProperty("password")); 
			
			// Ensure required tables exist
			try (Statement statement = connection.createStatement()) {
				statement.addBatch("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.PlayerProfile.getName() + "(player_name VARCHAR(16), player CHAR(36), rank TINYTEXT, "
						+ "rank_prefix TINYTEXT, money INT, name_title VARCHAR(32), name_alias VARCHAR(20), muted_players TEXT, warp_list TEXT, "
						+ "run_amplifier TINYINT, walk_amplifier DOUBLE, fly_amplifier DOUBLE, jump_amplifier DOUBLE, "
						+ "achievement_list TEXT, last_login DATETIME, total_logins INT, inventory_creative TEXT, inventory_survival TEXT, inventory_skyblock TEXT, ip VARCHAR(15), evilbook_version TEXT, PRIMARY KEY (player))");
				
				String locationTableStatement = "CREATE TABLE IF NOT EXISTS " + database + "." + TableType.PlayerLocation.getName() + "(player_name VARCHAR(16), home_location TINYTEXT, ";
				for (World world : Bukkit.getServer().getWorlds()) {
					String worldName = world.getName();
					if (worldName.contains("Private worlds/")) {
						worldName = worldName.split("Private worlds/")[1];
					}
					locationTableStatement += worldName + " TINYTEXT, ";
				}
				statement.addBatch(locationTableStatement + "PRIMARY KEY (player_name))");
				
				statement.addBatch("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.Warps.getName() + "(warp_name VARCHAR(32), world VARCHAR(36), x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT, pitch FLOAT, PRIMARY KEY (warp_name))");
				
				statement.addBatch("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.DynamicSign.getName() + "(world VARCHAR(36), x INT, y INT, z INT, line1 VARCHAR(15), line2 VARCHAR(15), line3 VARCHAR(15), line4 VARCHAR(15))");
				
				statement.addBatch("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.Statistics.getName() + "(date DATE, economy_growth INT, economy_trade INT, login_total INT, new_players INT, commands_executed INT, messages_sent INT, blocks_broken INT, blocks_placed INT)");

				statement.addBatch("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.PlayerStatistics.getName() + "(player_name VARCHAR(16), mined_coal INT, mined_iron INT, mined_lapis INT, mined_gold INT, mined_diamond INT, mined_redstone INT, mined_emerald INT, mined_netherquartz INT, killed_pigs INT, killed_villagers INT, killed_cavespiders INT, killed_endermen INT, killed_spiders INT, killed_wolves INT, killed_zombiepigs INT, killed_blazes INT, killed_creepers INT, killed_ghasts INT, killed_magmacubes INT, killed_silverfish INT, killed_skeletons INT, killed_slimes INT, killed_witches INT, killed_zombies INT, killed_enderdragons INT, killed_withers INT, killed_players INT, killed_rares INT)");

				statement.addBatch("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.ContainerProtection.getName() + "(world VARCHAR(36), x INT, y INT, z INT, player_name VARCHAR(16))");

				statement.addBatch("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.Emitter.getName() + "(world VARCHAR(36), x INT, y INT, z INT, effect TINYTEXT, data INT, frequency INT)");

				statement.addBatch("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.Region.getName() + "(region_name TINYTEXT, world VARCHAR(36), x1 INT, y1 INT, z1 INT, x2 INT, y2 INT, z2 INT, protected BOOL, player_name VARCHAR(16), welcome_message TINYTEXT, leave_message TINYTEXT, allowed_players TEXT, warp_name VARCHAR(32))");

				statement.addBatch("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.Mail.getName() + "(player_sender VARCHAR(16), player_recipient_UUID CHAR(36), message_text TINYTEXT, date_sent DATE)");

				statement.addBatch("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.CommandBlock.getName() + "(player CHAR(36), world VARCHAR(36), x INT, y INT, z INT)");

				statement.addBatch("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.CommandStatistics.getName() + "(command_name VARCHAR(128) PRIMARY KEY, execution_count INT)");
					
				statement.executeBatch();
			}
			
			return true;
		} catch (RuntimeException exception) {
			return false;
		} catch (Exception exception) {
			return false;
		}
	}
	
	private static String getStringFromCondition(TableType table, String columnName, String condition) {
		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT " + columnName + " FROM " + database + "." + table.getName() + " WHERE " + condition + ";")) {
				if (rs.next()) {
					return rs.getString(columnName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getString(TableType table, String columnName, String keyValue) {
		return getStringFromCondition(table, columnName, table.getKey() + "='" + keyValue + "'");
	}
	
	public static String getString(TableType table, String columnName, String world, int x, int y, int z) {
		return getStringFromCondition(table, columnName, "world='" + world + "' AND x='" + x + "' AND y='" + y + "' AND z='" + z + "'");
	}

	public static int getInt(TableType table, String columnName, String keyValue) {
		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT " + columnName + " FROM " + database + "." + table.getName() + " WHERE " + table.getKey() + "='" + keyValue + "';")) {
				if (rs.next()) return rs.getInt(columnName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static Rank getRank(String playerName) {
		return Rank.valueOf(getString(TableType.PlayerProfile, "rank", playerName));
	}

	public static void setValue(TableType table, String columnName, String keyValue, Object value) {
		try (Statement statement = connection.createStatement()) {
			if (value instanceof String) {
				statement.execute("UPDATE " + database + "." + table.getName() + " SET " + columnName + (value.equals("NULL") ? "=NULL WHERE " : "='" + value + "' WHERE ") + table.getKey() + "='" + keyValue + "';");
			} else if (value instanceof Integer) {
				statement.execute("UPDATE " + database + "." + table.getName() + " SET " + columnName + "=" + value + " WHERE " + table.getKey() + "='" + keyValue + "';");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void deleteRowFromCondition(TableType table, String condition) {
		try (Statement statement = connection.createStatement()) {
			statement.execute("DELETE FROM " + database + "." + table.getName() + " WHERE " + condition + ";");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteRow(TableType table, String key) {
		deleteRowFromCondition(table, table.getKey() + "='" + key + "'");
	}
	
	public static void deleteRow(TableType table, String world, int x, int y, int z) {
		deleteRowFromCondition(table, "world='" + world + "' AND x='" + x + "' AND y='" + y + "' AND z='" + z + "'");
	}
	
	public static void insert(TableType table, String fields, String data) {
		try (Statement statement = connection.createStatement()) {
			statement.execute("INSERT INTO " + database + "." + table.getName() + " (" + fields + ") " + "VALUES (" + data + ");");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insert(TableType table, String data) {
		insert(table, table.getFields(), data);
	}
	
	public static void addColumn(TableType table, String columnName) {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + database + "." + table.getName() + " ADD " + columnName + " NULL;");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Boolean isColumnExistant(TableType table, String columnName) {
		try {
			DatabaseMetaData md = connection.getMetaData();
			try (ResultSet rs = md.getColumns(null, null, table.getName(), columnName)) {
				if (rs.next()) return true;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}
	
	public static Boolean isKeyExistant(TableType table, String key) {
		return getString(table, table.getKey(), key) != null;
	}
	
	public static Boolean isRowExistant(TableType table, String world, int x, int y, int z) {
		return getString(table, table.getKey(), world, x, y, z) != null;
	}
	
	public static int getRowCount(TableType table) {
		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM " + database + "." + table.getName() + ";")) {
				rs.next();
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int getColumnSum(TableType table, String columnName) {
		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT SUM(" + columnName + ") FROM " + database + "." + table.getName() + ";")) {
				rs.next();
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static Location getWarp(String name) {
		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT world, x, y, z, yaw, pitch FROM " + database + "." + TableType.Warps.getName() + " WHERE warp_name='" + name + "';")) {
				if (rs.next()) {
					return new Location(Bukkit.getServer().getWorld(rs.getString("world")), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}