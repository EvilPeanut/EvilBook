package com.amentrix.evilbook.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.amentrix.evilbook.eviledit.utils.EditWandMode;
import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;

/**
 * PlayerProfile parent instance
 * @author Reece Aaron Lecrivain
 */
public class PlayerProfile {
	public String name, lastMessage, teleportantName, lastMsgPlayer;
	public List<String> warps = new ArrayList<>(), mutedPlayers = new ArrayList<>();
	public Boolean isAway = false, isInvisible = false;
	public List<Achievement> achievements = new ArrayList<>();
	public long lastMessageTime = 0, lastActionTime = 0;
	public double jumpAmplifier = 0, flyAmplifier = 0, walkAmplifier = 0;
	public Rank rank = Rank.Builder;
	public Entity disguise;
	public Location deathLocation, homeLocation, lastBlockInteraction;
	public int runAmplifier = 4, money = 0;
	public EditWandMode wandMode = EditWandMode.Selection;
	public Location actionLocationA, actionLocationB;
	
	/**
	 * Returns if the specified player is muted
	 * @param playerName The player to test with
	 * @return If the player if muted
	 */
	public Boolean isMuted(String playerName) {
		return this.mutedPlayers.contains(playerName.toLowerCase(Locale.UK));
	}

	/**
	 * Set a player profile property
	 * @param property The property to set
	 * @param value The value of the property
	 */
	public void setProperty(String property, String value) {
		SQL.setProperty(TableType.PlayerProfile, this.name, property, value);
	}
	
	/**
	 * Get a player profile property
	 * @param property The property to get
	 * @return The value of the property
	 */
	public String getProperty(String property) {
		return SQL.getProperty(TableType.PlayerProfile, this.name, property);
	}
	
	/**
	 * Set a player profile property
	 * @param property The property to set
	 * @param value The value of the property
	 */
	public void setProperty(TableType tableType, String property, String value) {
		SQL.setProperty(tableType, this.name, property, value);
	}
	
	/**
	 * Get a player profile property
	 * @param property The property to get
	 * @return The value of the property
	 */
	public String getProperty(TableType tableType, String property) {
		return SQL.getProperty(tableType, this.name, property);
	}

	/**
	 * Set the player's last position in the world
	 * Use when they teleport to a different world
	 * @param location Their location in the world
	 */
	public void setWorldLastPosition(Location location) {
		String worldName = location.getWorld().getName();
		if (worldName.contains("Private worlds/")) worldName = worldName.split("Private worlds/")[1];
		SQL.setProperty(TableType.PlayerLocation, this.name, worldName, location.getX() + ">" + location.getY() + ">" + location.getZ());
	}

	/**
	 * Get the player's last position in the world
	 * @param world The world to get their last location in
	 */
	public Location getWorldLastPosition(String world) {
		String worldName = world;
		if (worldName.contains("Private worlds/")) worldName = worldName.split("Private worlds/")[1];
		String result = SQL.getProperty(TableType.PlayerLocation, this.name, worldName);
		if (result == null) return Bukkit.getServer().getWorld(world).getSpawnLocation();
		String[] location = result.split(">");
		return new Location(Bukkit.getServer().getWorld(world), Double.valueOf(location[0]), Double.valueOf(location[1]), Double.valueOf(location[2]));
	}
	
	/**
	 * Save the player profile
	 */
	public void saveProfile() {
		if (this instanceof PlayerProfileAdmin) {
			((PlayerProfileAdmin)this).saveProfile();
		} else {
			((PlayerProfileNormal)this).saveProfile();
		}
	}
	
	/**
	 * Update the player's name in the player list
	 */
	public void updatePlayerListName() {
		if (this instanceof PlayerProfileAdmin) {
			((PlayerProfileAdmin)this).updatePlayerListName();
		} else {
			((PlayerProfileNormal)this).updatePlayerListName();
		}
	}
}
