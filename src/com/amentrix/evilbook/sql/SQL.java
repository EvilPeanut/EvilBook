package com.amentrix.evilbook.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.Rank;

/**
 * SQL framework
 * @author Reece Aaron Lecrivain
 */
public class SQL {
	//TODO: Add 'owner_name' column to warps table and remove player profiles 'warps' property
	public static Connection connection;
	public static String database;

	public static Boolean connect(EvilBook plugin) {
		try {
			database = EvilBook.config.getProperty("database");
			connection = DriverManager.getConnection("jdbc:mysql://" + EvilBook.config.getProperty("host") + "/" + database + "?user=" + EvilBook.config.getProperty("user") + "&password=" + EvilBook.config.getProperty("password")); 
			// Create the player profile table
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.PlayerProfile.tableName + "(player_name VARCHAR(16), player CHAR(36), rank TINYTEXT, "
						+ "rank_prefix TINYTEXT, money INT, name_title VARCHAR(32), name_alias VARCHAR(20), muted_players TEXT, warp_list TEXT, "
						+ "run_amplifier TINYINT, walk_amplifier DOUBLE, fly_amplifier DOUBLE, jump_amplifier DOUBLE, "
						+ "achievement_list TEXT, last_login DATETIME, total_logins INT, inventory_creative TEXT, inventory_survival TEXT, inventory_skyblock TEXT, ip VARCHAR(15), evilbook_version TEXT, PRIMARY KEY (player))");
			}
			// Create the player location table
			try (Statement stmt = connection.createStatement()) {
				String statement = "CREATE TABLE IF NOT EXISTS " + database + "." + TableType.PlayerLocation.tableName + "(player_name VARCHAR(16), home_location TINYTEXT, ";
				for (World world : plugin.getServer().getWorlds()) {
					String worldName = world.getName();
					if (worldName.contains("Private worlds/")) worldName = worldName.split("Private worlds/")[1];
					statement += worldName + " TINYTEXT, ";
				}
				stmt.execute(statement + "PRIMARY KEY (player_name))");
			}
			// Create the warps table
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.Warps.tableName + "(warp_name VARCHAR(32), location TINYTEXT, PRIMARY KEY (warp_name))");
			}
			// Create dynamic signs table
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.DynamicSign.tableName + "(world CHAR(36), x INT, y INT, z INT, line1 VARCHAR(15), line2 VARCHAR(15), line3 VARCHAR(15), line4 VARCHAR(15))");
			}
			// Create general statistics table
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.Statistics.tableName + "(date DATE, economy_growth INT, economy_trade INT, login_total INT, new_players INT, commands_executed INT, messages_sent INT, blocks_broken INT, blocks_placed INT)");
			}
			// Create player statistics table
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.PlayerStatistics.tableName + "(player_name VARCHAR(16), mined_coal INT, mined_iron INT, mined_lapis INT, mined_gold INT, mined_diamond INT, mined_redstone INT, mined_emerald INT, mined_netherquartz INT, killed_pigs INT, killed_villagers INT, killed_cavespiders INT, killed_endermen INT, killed_spiders INT, killed_wolves INT, killed_zombiepigs INT, killed_blazes INT, killed_creepers INT, killed_ghasts INT, killed_magmacubes INT, killed_silverfish INT, killed_skeletons INT, killed_slimes INT, killed_witches INT, killed_zombies INT, killed_enderdragons INT, killed_withers INT, killed_players INT, killed_rares INT)");
			}
			// Create container protection table
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.ContainerProtection.tableName + "(world TINYTEXT, x INT, y INT, z INT, player_name VARCHAR(16))");
			}
			// Create emitter table
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.Emitter.tableName + "(world TINYTEXT, x INT, y INT, z INT, effect TINYTEXT, data INT, frequency INT)");
			}
			// Create regions table
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.Region.tableName + "(region_name TINYTEXT, world TINYTEXT, x1 INT, y1 INT, z1 INT, x2 INT, y2 INT, z2 INT, protected BOOL, player_name VARCHAR(16), welcome_message TINYTEXT, leave_message TINYTEXT, allowed_players TEXT, warp_name VARCHAR(32))");
			}
			// Create mail table
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.Mail.tableName + "(player_sender VARCHAR(16), player_recipient_UUID CHAR(36), message_text TINYTEXT, date_sent DATE)");
			}
			// Create commandblock table
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.CommandBlock.tableName + "(player CHAR(36), world CHAR(36), x INT, y INT, z INT)");
			}
			// Create command statistics table
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("CREATE TABLE IF NOT EXISTS " + database + "." + TableType.CommandStatistics.tableName + "(command_name VARCHAR(128) PRIMARY KEY, execution_count INT)");
			}
			//
			return true;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	//
	// Get methods
	//
	public static String getString(TableType table, String key, String property) {
		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT " + property + " FROM " + database + "." + table.tableName + " WHERE " + table.keyName + "='" + key + "';")) {
				if (rs.next()) return rs.getString(property);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getStringFromCriteria(TableType table, String criteria, String property) {
		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT " + property + " FROM " + database + "." + table.tableName + " WHERE " + criteria + ";")) {
				if (rs.next()) return rs.getString(property);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static int getInteger(TableType table, String key, String property) {
		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT " + property + " FROM " + database + "." + table.tableName + " WHERE " + table.keyName + "='" + key + "';")) {
				if (rs.next()) return rs.getInt(property);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static Rank getRank(String playerName) {
		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT rank FROM " + database + "." + TableType.PlayerProfile.tableName + " WHERE " + TableType.PlayerProfile.keyName + "='" + playerName + "';")) {
				if (rs.next()) return Rank.valueOf(rs.getString("rank"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void getInventory(Player player, String inventory) {
		player.getInventory().clear();
		player.getInventory().setHelmet(new ItemStack(Material.AIR));
		player.getInventory().setChestplate(new ItemStack(Material.AIR));
		player.getInventory().setLeggings(new ItemStack(Material.AIR));
		player.getInventory().setBoots(new ItemStack(Material.AIR));
		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT inventory_" + inventory + " FROM " + database + "." + TableType.PlayerProfile.tableName + " WHERE " + TableType.PlayerProfile.keyName + "='" + player.getName() + "';")) {
				if (rs.next()) {
					YamlConfiguration config = new YamlConfiguration();
					try {
						String rawInventory = rs.getString("inventory_" + inventory);
						if (rawInventory != null) {
							config.loadFromString(rawInventory);
							for (int i = 0; i < player.getInventory().getSize(); i++) {
								if (config.get(Integer.toString(i)) != null) 
									player.getInventory().setItem(i, (ItemStack)config.get(Integer.toString(i)));
							}
							player.getInventory().setHelmet((ItemStack)config.get("head"));
							player.getInventory().setChestplate((ItemStack)config.get("chest"));
							player.getInventory().setLeggings((ItemStack)config.get("legs"));
							player.getInventory().setBoots((ItemStack)config.get("boots"));
							player.setHealth((double)config.get("health"));
							player.setFoodLevel((int)config.get("hunger"));
							player.setLevel((int)config.get("level"));
							player.setExp((float)config.getDouble("xp"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//
	// Set Methods
	//
	public static void setString(TableType table, String key, String property, String value) {
		try (Statement statement = connection.createStatement()) {
			statement.execute("UPDATE " + database + "." + table.tableName + " SET " + property + (value.equals("NULL") ? "=NULL WHERE " : "='" + value + "' WHERE ") + table.keyName + "='" + key + "';");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setInteger(TableType table, String key, String property, int value) {
		try (Statement statement = connection.createStatement()) {
			statement.execute("UPDATE " + database + "." + table.tableName + " SET " + property + "=" + value + " WHERE " + table.keyName + "='" + key + "';");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setRank(String playerName, Rank rank) {
		try (Statement statement = connection.createStatement()) {
			statement.execute("UPDATE " + database + "." + TableType.PlayerProfile.tableName + " SET rank='" + rank.toString() + "' WHERE " + TableType.PlayerProfile.keyName + "='" + playerName + "';");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setInventory(Player player, String inventory) {
		try (Statement statement = connection.createStatement()) {
			YamlConfiguration config = new YamlConfiguration();
			for (int i = 0; i < player.getInventory().getSize(); i++) {
				ItemStack item = player.getInventory().getItem(i);
				if (item != null) config.set(Integer.toString(i), item);
			}
			config.set("head", player.getInventory().getHelmet());
			config.set("chest", player.getInventory().getChestplate());
			config.set("legs", player.getInventory().getLeggings());
			config.set("boots", player.getInventory().getBoots());
			config.set("health", player.getHealth());
			config.set("hunger", player.getFoodLevel());
			config.set("level", player.getLevel());
			config.set("xp", player.getExp());
			statement.execute("UPDATE " + database + "." + TableType.PlayerProfile.tableName + " SET inventory_" + inventory + "='" + config.saveToString().replaceAll("'", "''") + "' WHERE " + TableType.PlayerProfile.keyName + "='" + player.getName() + "';");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//
	//
	//
	public static void deleteColumn(TableType table, String column) {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + database + "." + table.tableName + " DROP COLUMN " + column + ";");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void deleteRow(TableType table, String key) {
		try (Statement statement = connection.createStatement()) {
			statement.execute("DELETE FROM " + database + "." + table.tableName + " WHERE " + table.keyName + "='" + key + "';");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteRowFromCriteria(TableType table, String criteria) {
		try (Statement statement = connection.createStatement()) {
			statement.execute("DELETE FROM " + database + "." + table.tableName + " WHERE " + criteria + ";");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insert(TableType table, String fields, String data) {
		try (Statement statement = connection.createStatement()) {
			statement.execute("INSERT INTO " + database + "." + table.tableName + " (" + fields + ") " + "VALUES (" + data + ");");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void insert(TableType table, String data) {
		try (Statement statement = connection.createStatement()) {
			statement.execute("INSERT INTO " + database + "." + table.tableName + " (" + table.fields + ") " + "VALUES (" + data + ");");
		} catch (Exception e) {
			EvilBook.logSevere("Failed to execute: INSERT INTO " + database + "." + table.tableName + " (" + table.fields + ") " + "VALUES (" + data + ");");
			e.printStackTrace();
		}
	}

	public static void insertNullColumn(TableType table, String columnName) {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + database + "." + table.tableName + " ADD " + columnName + " NULL;");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Boolean isColumnExistant(TableType table, String columnName) {
		try {
			DatabaseMetaData md = connection.getMetaData();
			try (ResultSet rs = md.getColumns(null, null, table.tableName, columnName)) {
				if (rs.next()) return true;
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;

	}

	public static Boolean isKeyExistant(TableType table, String key) {
		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT * FROM " + database + "." + table.tableName + " WHERE " + table.keyName + "='" + key + "';")) {
				if (rs.next()) return true; 
				return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Boolean isRowExistant(TableType table, String criteria) {
		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT * FROM " + database + "." + table.tableName + " WHERE " + criteria + ";")) {
				if (rs.next()) return true; 
				return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int getRowCount(TableType table) {
		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM " + database + "." + table.tableName + ";")) {
				rs.next();
				return rs.getInt(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int getColumnSum(TableType table, String columnName) {
		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT SUM(" + columnName + ") FROM " + database + "." + table.tableName + ";")) {
				rs.next();
				return rs.getInt(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static Location getWarp(String name) {
		//TODO: Change to SQL.getLocation()
		try {
			if (!isKeyExistant(TableType.Warps, name)) return null;
			String rawWarp = getString(TableType.Warps, name, "Location");
			Location location = new Location(Bukkit.getServer().getWorld(rawWarp.split(">")[0]), 
					Double.valueOf(rawWarp.split(">")[1]),
					Double.valueOf(rawWarp.split(">")[2]), 
					Double.valueOf(rawWarp.split(">")[3]),
					Float.valueOf(rawWarp.split(">")[4]),
					Float.valueOf(rawWarp.split(">")[5]));
			return location;
		} catch (Exception exception) {
			return null;
		}
	}
}
