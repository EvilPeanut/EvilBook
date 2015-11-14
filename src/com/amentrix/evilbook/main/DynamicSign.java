package com.amentrix.evilbook.main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;
import com.amentrix.evilbook.statistics.GlobalStatistic;

/**
 * DynamicSign instance
 * @author Reece Aaron Lecrivain
 */
public class DynamicSign {
	public Location location;
	public String[] textLines;
	
	public DynamicSign(Location location, String[] textLines) {
		this.location = location;
		this.textLines = textLines;
		try {
			SQL.insert(TableType.DynamicSign, "world, x, y, z, line1, line2, line3, line4", "'" + 
			location.getWorld().getUID().toString() + "','" + 
			location.getBlockX() + "','" +
			location.getBlockY() + "','" + 
			location.getBlockZ() + "','" +
			textLines[0] + "','" +
			textLines[1] + "','" +
			textLines[2] + "','" +
			textLines[3] + "'");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	DynamicSign(ResultSet properties) {
		try {
			this.location = new Location(Bukkit.getServer().getWorld(UUID.fromString(properties.getString("world"))), properties.getInt("x"), properties.getInt("y"), properties.getInt("z"));
			this.textLines = new String[4];
			this.textLines[0] = properties.getString("line1");
			this.textLines[1] = properties.getString("line2");
			this.textLines[2] = properties.getString("line3");
			this.textLines[3] = properties.getString("line4");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public void delete() {
		try (PreparedStatement statement = SQL.connection.prepareStatement("DELETE FROM " + SQL.database + ".`evilbook-dynamicsigns` WHERE world=? AND x=? AND y=? AND z=?")) {
			statement.setString(1, this.location.getWorld().getUID().toString());
			statement.setInt(2, this.location.getBlockX());
			statement.setInt(3, this.location.getBlockY());
			statement.setInt(4, this.location.getBlockZ());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Only used to create record if sign is destroyed and then remade eg. EvilEdit undo
	public void create() {
		try {
			SQL.insert(TableType.DynamicSign, "'" + 
			this.location.getWorld().getUID().toString() + "','" + 
			this.location.getBlockX() + "','" +
			this.location.getBlockY() + "','" + 
			this.location.getBlockZ() + "','" +
			this.textLines[0] + "','" +
			this.textLines[1] + "','" +
			this.textLines[2] + "','" +
			this.textLines[3] + "'");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public void update() {
		if (isSeen()) {
			Sign sign = (Sign) location.getBlock().getState();
			for (int i = 0; i < 4; i++) {
				sign.setLine(i, textLines[i].replace("[time]", EvilBook.getTime(sign.getBlock().getWorld())
						.replace("[weather]", EvilBook.getWeather(sign.getBlock())))
						.replace("[online]", Integer.toString(Bukkit.getServer().getOnlinePlayers().size()))
						.replace("[blocksbroken]", GlobalStatistic.getStatistic(GlobalStatistic.BlocksBroken))
						.replace("[blocksplaced]", GlobalStatistic.getStatistic(GlobalStatistic.BlocksPlaced))
						);
			}
			sign.update();
		}
	}
	
	public Boolean isSeen() {
		if (!location.getChunk().isLoaded()) return false;
		for (Player player : location.getWorld().getPlayers()) {
			if (Math.abs(player.getLocation().getBlockX() - location.getBlockX()) <= 16
					&& Math.abs(player.getLocation().getBlockZ() - location.getBlockZ()) <= 16) {
				return true;
			}
		}
		return false;
	}
	
	public static Boolean isDynamicSign(Sign sign) {
		return sign.getLine(0).toLowerCase().contains("[time]") || 
			   sign.getLine(1).toLowerCase().contains("[time]") || 
			   sign.getLine(2).toLowerCase().contains("[time]") || 
			   sign.getLine(3).toLowerCase().contains("[time]") ||
			   sign.getLine(0).toLowerCase().contains("[weather]") || 
			   sign.getLine(1).toLowerCase().contains("[weather]") || 
			   sign.getLine(2).toLowerCase().contains("[weather]") || 
			   sign.getLine(3).toLowerCase().contains("[weather]") ||
			   sign.getLine(0).toLowerCase().contains("[online]") || 
			   sign.getLine(1).toLowerCase().contains("[online]") || 
			   sign.getLine(2).toLowerCase().contains("[online]") || 
			   sign.getLine(3).toLowerCase().contains("[online]") ||
			   sign.getLine(0).toLowerCase().contains("[blocksbroken]") || 
			   sign.getLine(1).toLowerCase().contains("[blocksbroken]") || 
			   sign.getLine(2).toLowerCase().contains("[blocksbroken]") || 
			   sign.getLine(3).toLowerCase().contains("[blocksbroken]") ||
			   sign.getLine(0).toLowerCase().contains("[blocksplaced]") || 
			   sign.getLine(1).toLowerCase().contains("[blocksplaced]") || 
			   sign.getLine(2).toLowerCase().contains("[blocksplaced]") || 
			   sign.getLine(3).toLowerCase().contains("[blocksplaced]");
	}
	
	public static void formatDynamicSign(Sign sign) {
		String[] line = sign.getLines();
		for (int i = 0; i < 4; i++) {
			line[i] = EvilBook.replaceAllIgnoreCase(line[i], "[Time]", "[time]");
			line[i] = EvilBook.replaceAllIgnoreCase(line[i], "[Weather]", "[weather]");
			line[i] = EvilBook.replaceAllIgnoreCase(line[i], "[Online]", "[online]");
			line[i] = EvilBook.replaceAllIgnoreCase(line[i], "[BlocksBroken]", "[blocksbroken]");
			line[i] = EvilBook.replaceAllIgnoreCase(line[i], "[BlocksPlaced]", "[blocksplaced]");
		}
		DynamicSign dynamicSign = new DynamicSign(sign.getBlock().getLocation(), line);
		EvilBook.dynamicSignList.get(sign.getWorld()).add(dynamicSign);
		dynamicSign.update();
	}
}
