package com.amentrix.evilbook.main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;

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
			location.getWorld().getName() + "','" + 
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
	
	public DynamicSign(ResultSet properties) {
		try {
			this.location = new Location(Bukkit.getServer().getWorld(properties.getString("world")), properties.getInt("x"), properties.getInt("y"), properties.getInt("z"));
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
		try (PreparedStatement statement = SQL.connection.prepareStatement("DELETE FROM ?.? WHERE world='?' AND x='?' AND y='?' AND z='?';")) {
			statement.setString(1, SQL.database);
			statement.setString(2, TableType.DynamicSign.tableName);
			statement.setString(3, this.location.getWorld().getName());
			statement.setInt(4, this.location.getBlockX());
			statement.setInt(5, this.location.getBlockY());
			statement.setInt(6, this.location.getBlockZ());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Only used to create record if sign is destroyed and then remade eg. EvilEdit undo
	public void create() {
		try {
			SQL.insert(TableType.DynamicSign, "'" + 
			this.location.getWorld().getName() + "','" + 
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
}
