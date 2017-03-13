package com.amentrix.evilbook.main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;

/**
 * DynamicSign instance
 * @author Reece Aaron Lecrivain
 */
public class DynamicSign {
	public Location location;
	public String[] textLines;
	
	DynamicSign(Location location, String[] textLines) {
		this.location = location;
		this.textLines = textLines;
		create();
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
}
