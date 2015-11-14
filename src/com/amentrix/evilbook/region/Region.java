package com.amentrix.evilbook.region;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.StatementSet;
import com.amentrix.evilbook.sql.TableType;
import com.amentrix.evilbook.statistics.GlobalStatistic;

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
	public String getLeaveMessage() { 
		return this.leaveMessage == null ? null : this.leaveMessage.replaceAll("(?i)(\\[time\\])", EvilBook.getTime(locationA.getWorld()))
				.replaceAll("(?i)(\\[weather\\])", EvilBook.getWeather(locationA.getBlock()))
				.replaceAll("(?i)(\\[online\\])", Integer.toString(Bukkit.getServer().getOnlinePlayers().size()))
				.replaceAll("(?i)(\\[blocksbroken\\])", GlobalStatistic.getStatistic(GlobalStatistic.BlocksBroken))
				.replaceAll("(?i)(\\[blocksplaced\\])", GlobalStatistic.getStatistic(GlobalStatistic.BlocksBroken));
	}
	
	/**
	 * @param leaveMessage The leave message
	 */
	public void setLeaveMessage(String leaveMessage) { this.leaveMessage = leaveMessage; }
	
	/**
	 * @return The welcome message
	 */
	public String getWelcomeMessage() { 
		return this.welcomeMessage == null ? null : this.welcomeMessage.replaceAll("(?i)(\\[time\\])", EvilBook.getTime(locationA.getWorld()))
				.replaceAll("(?i)(\\[weather\\])", EvilBook.getWeather(locationA.getBlock()))
				.replaceAll("(?i)(\\[online\\])", Integer.toString(Bukkit.getServer().getOnlinePlayers().size()))
				.replaceAll("(?i)(\\[blocksbroken\\])", GlobalStatistic.getStatistic(GlobalStatistic.BlocksBroken))
				.replaceAll("(?i)(\\[blocksplaced\\])", GlobalStatistic.getStatistic(GlobalStatistic.BlocksBroken));
	}
	
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
		try (PreparedStatement statement = SQL.connection.prepareStatement("DELETE FROM " + SQL.database + ".`evilbook-regions` WHERE region_name=?")) {
			statement.setString(1, this.regionName.replaceAll("'", "''"));
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save the region externally
	 */
	public void saveRegion() 
	{
		if (SQL.isKeyExistant(TableType.Region, this.regionName.replaceAll("'", "''"))) {
			StatementSet saveAgent = new StatementSet();
			saveAgent.setProperty(TableType.Region, this.regionName, "region_name", this.regionName);
			saveAgent.setProperty(TableType.Region, this.regionName, "world", this.locationA.getWorld().getName());
			saveAgent.setProperty(TableType.Region, this.regionName, "x1", this.locationA.getBlockX());
			saveAgent.setProperty(TableType.Region, this.regionName, "y1", this.locationA.getBlockY());
			saveAgent.setProperty(TableType.Region, this.regionName, "z1", this.locationA.getBlockZ());
			saveAgent.setProperty(TableType.Region, this.regionName, "x2", this.locationB.getBlockX());
			saveAgent.setProperty(TableType.Region, this.regionName, "y2", this.locationB.getBlockY());
			saveAgent.setProperty(TableType.Region, this.regionName, "z2", this.locationB.getBlockZ());
			saveAgent.setProperty(TableType.Region, this.regionName, "protected", (this.isProtected() ? 1 : 0));
			saveAgent.setProperty(TableType.Region, this.regionName, "player_name", this.ownerName);
			saveAgent.setProperty(TableType.Region, this.regionName, "welcome_message", (this.welcomeMessage == null ? "NULL" : this.welcomeMessage.replaceAll("'", "''")));
			saveAgent.setProperty(TableType.Region, this.regionName, "leave_message", (this.leaveMessage == null ? "NULL" : this.leaveMessage.replaceAll("'", "''")));
			saveAgent.setProperty(TableType.Region, this.regionName, "allowed_players", (this.allowedPlayers == null || this.allowedPlayers.size() == 0 ? "NULL" : StringUtils.join(this.allowedPlayers, ",").replaceAll("'", "''")));
			saveAgent.setProperty(TableType.Region, this.regionName, "warp_name", (this.warpName == null ? "NULL" : this.warpName.replaceAll("'", "''")));
			saveAgent.execute();
		} else {
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
}