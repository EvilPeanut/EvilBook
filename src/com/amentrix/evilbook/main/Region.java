package com.amentrix.evilbook.main;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;

/**
 * Region instance
 * @author Reece Aaron Lecrivain
 */
public class Region {
	private Boolean isProtected = false;
	private String regionName, welcomeMessage = null, leaveMessage = null, warpName = null, ownerName;
	private Location locationA, locationB;
	private List<String> allowedPlayers = new ArrayList<>();
	
	/**
	 * @return The players who have rights to the region
	 */
	public List<String> getAllowedPlayers(){ return this.allowedPlayers; }
	
	/**
	 * @param allowedPlayers The players who have rights to the region
	 */
	public void setAllowedPlayers(List<String> allowedPlayers) { this.allowedPlayers = allowedPlayers; }
	
	/**
	 * @param playerName The player to add to the region permissions
	 */
	public void addAllowedPlayer(String playerName) { if (this.allowedPlayers.contains(playerName) == false) this.allowedPlayers.add(playerName); }
	
	/**
	 * @param playerName The player to remove region permissions of
	 */
	public void removeAllowedPlayer(String playerName) { if (this.allowedPlayers.contains(playerName)) this.allowedPlayers.remove(playerName); }
	
	/**
	 * @param playerName The player to check for region permissions
	 */
	public Boolean isPermitted(String playerName) { return this.allowedPlayers.contains(playerName) ? true : false; }
	
	/**
	 * @return The region owner's name
	 */
	public String getOwner(){ return this.ownerName; }
	
	/**
	 * @param ownerName The region owner's name
	 */
	public void setOwner(String ownerName) { this.ownerName = ownerName; }
	
	/**
	 * @return The warp redirection name
	 */
	public String getWarp(){ return this.warpName; }
	
	/**
	 * @param warpName The warp redirection name
	 */
	public void setWarp(String warpName) { this.warpName = warpName; }
	
	/**
	 * @return The leave message
	 */
	public String getLeaveMessage(){ return this.leaveMessage; }
	
	/**
	 * @param leaveMessage The leave message
	 */
	public void setLeaveMessage(String leaveMessage) { this.leaveMessage = leaveMessage; }
	
	/**
	 * @return The welcome message
	 */
	public String getWelcomeMessage(){ return this.welcomeMessage; }
	
	/**
	 * @param welcomeMessage The welcome message
	 */
	public void setWelcomeMessage(String welcomeMessage) { this.welcomeMessage = welcomeMessage; }
	
	/**
	 * @return The region nam
	 */
	public String getRegionName(){ return this.regionName; }
	
	/**
	 * @param regionName The region name
	 */
	public void setRegionName(String regionName) { this.regionName = regionName; }
	
	/**
	 * @return If protection is enabled
	 */
	public Boolean isProtected(){ return this.isProtected; }
	
	/**
	 * @param isProtected If protection is enabled
	 */
	public void isProtected(Boolean isProtected) { this.isProtected = isProtected; }
	
	/**
	 * @return The first location point
	 */
	public Location getLocationA(){ return this.locationA; }
	
	/**
	 * @param locationA The first location point
	 */
	public void setLocationA(Location locationA) { this.locationA = locationA; }
	
	/**
	 * @return The second location point
	 */
	public Location getLocationB(){ return this.locationB; }
	
	/**
	 * @param locationB The second location point
	 */
	public void setLocationB(Location locationB) { this.locationB = locationB; }

	/**
	 * Define a new region
	 * @param regionName The name of the region
	 * @param locationA The first location point of the region
	 * @param locationB The second location point of the region
	 * @param isProtected If protection is enabled in the region
	 * @param ownerName The region owner's name
	 * @param welcomeMessage The welcome message of the region
	 * @param leaveMessage The leave message of the region
	 * @param allowedPlayers The players who have rights to the region
	 */
	public Region(String regionName, Location locationA, Location locationB, Boolean isProtected, String ownerName, String welcomeMessage, String leaveMessage, String allowedPlayers, String warpName)
	{
		this.regionName = regionName;
		this.locationA = locationA;
		this.locationB = locationB;
		this.isProtected = isProtected;
		this.ownerName = ownerName;
		if (welcomeMessage != null) this.welcomeMessage = EvilBook.toFormattedString(welcomeMessage);
		if (leaveMessage != null) this.leaveMessage = EvilBook.toFormattedString(leaveMessage);
		if (allowedPlayers != null) this.allowedPlayers = Arrays.asList(allowedPlayers.split(","));
		if (warpName != null) this.warpName = warpName;
	}

	public Region(ResultSet rs) {
		try {
			this.regionName = rs.getString("region_name");
			this.locationA = new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x1"), rs.getInt("y1"), rs.getInt("z1"));
			this.locationB = new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x2"), rs.getInt("y2"), rs.getInt("z2"));
			this.isProtected = rs.getBoolean("protected");
			this.ownerName = rs.getString("player_name");
			this.welcomeMessage = rs.getString("welcome_message");
			this.leaveMessage = rs.getString("leave_message");
			if (rs.getString("allowed_players") != null) this.allowedPlayers = Arrays.asList(rs.getString("allowed_players").split(","));
			this.warpName = rs.getString("warp_name");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public void delete() {
		try (Statement statement = SQL.connection.createStatement()) {
			statement.execute("DELETE FROM " + SQL.database + "." + TableType.Region.tableName + " WHERE region_name='" + this.regionName.replaceAll("'", "''") + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save the region externally
	 */
	public void saveRegion() 
	{
		SQL.insert(TableType.Region, "'" +
		this.regionName.replaceAll("'", "''") + "','" + 
		this.locationA.getWorld().getName() + "','" +
		this.locationA.getBlockX() + "','" +
		this.locationA.getBlockY() + "','" +
		this.locationA.getBlockZ() + "','" +
		this.locationB.getBlockX() + "','" +
		this.locationB.getBlockY() + "','" +
		this.locationB.getBlockZ() + "','" +
		(this.isProtected() ? 1 : 0) + "','" +
		this.ownerName + "'," +
		(this.welcomeMessage == null ? "NULL" : "'" + this.welcomeMessage.replaceAll("'", "''") + "'") + "," +
		(this.leaveMessage == null ? "NULL" : "'" + this.leaveMessage.replaceAll("'", "''") + "'") + "," +
		(this.allowedPlayers == null || this.allowedPlayers.size() == 0 ? "NULL" : "'" + StringUtils.join(this.allowedPlayers, ",").replaceAll("'", "''") + "'")  + "," +
		(this.warpName == null ? "NULL" : "'" + this.warpName.replaceAll("'", "''") + "'"));
	}
}