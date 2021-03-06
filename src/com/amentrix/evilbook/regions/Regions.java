package com.amentrix.evilbook.regions;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.Rank;
import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;

public class Regions {
	//Key: World name TODO: Regions: Change to World UUID
	//Value: List of regions
	private static final Map<String, ArrayList<Region>> regionList = new HashMap();
	
	public static void load() {
		for (World world : Bukkit.getServer().getWorlds()) {
			Regions.initWorld(world.getName());
		}
		try (Statement statement = SQL.connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.Region.getName() + ";")) {
				while (rs.next()) {
					if (Bukkit.getServer().getWorld(rs.getString("world")) != null) {
						if (rs.getString("region_name").startsWith("PlotRegion")) {
							EvilBook.plotRegionList.add(new Region(rs));
						} else {
							Regions.addRegion(rs.getString("world"), new Region(rs));
						}
					} else {
						EvilBook.logInfo("Region " + rs.getString("region_name") + " in " + rs.getString("world") + " not loaded location unavailable");
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public static void initWorld(String worldName) {
		regionList.put(worldName, new ArrayList());
	}
	
	public static ArrayList<Region> getRegions(String worldName) {
		return regionList.get(worldName);
	}
	
	public static Region getRegion(String regionName) {
		for (ArrayList<Region> regions : regionList.values()) {
			for (Region region : regions) {
				if (region.getName().equalsIgnoreCase(regionName)) return region;
			}
		}
		return null;
	}
	
	public static void addRegion(String worldName, Region region) {
		regionList.get(worldName).add(region);
	}
	
	public static void removeRegion(String worldName, Region region) {
		regionList.get(worldName).remove(region);
	}
	
	public static Boolean isInProtectedRegion(Location location, Player player) {
		if (EvilBook.getProfile(player).rank.isAdmin()) return false;
		for (Region region : regionList.get(location.getWorld().getName())) {
			if (region.isProtected() == false) continue;
			if (region.getOwner().equals(player.getName()) || region.getAllowedPlayers().contains(player.getName())) continue;
			if (isInRegion(region, location)) {
				return true;
			}
		}
		return false;
	}
	
	public static Boolean isInPlotworldRegion(Location location) {
		for (Region region : EvilBook.plotRegionList) {
			if (isInRegion(region, location)) {
				return true;
			}
		}
		return false;
	}
	
	public static Boolean isInProtectedPlotworldRegion(Location location, Player player) {
		if (EvilBook.getProfile(player).rank == Rank.SERVER_HOST) return false;
		for (Region region : EvilBook.plotRegionList) {
			if (isInRegion(region, location)) {
				if (region.getOwner().equals(player.getName()) || region.getAllowedPlayers().contains(player.getName())) {
					return false;
				}
				return true;
			}
		}
		return false;
	}
	
	public static Boolean isInRegion(Region region, Location location) {
		if (location.getBlockX() >= region.getLocationA().getBlockX() && location.getBlockX() <= region.getLocationB().getBlockX()) {
			if (location.getBlockY() >= region.getLocationA().getBlockY() && location.getBlockY() <= region.getLocationB().getBlockY()) {
				if (location.getBlockZ() >= region.getLocationA().getBlockZ() && location.getBlockZ() <= region.getLocationB().getBlockZ()) {
					return true;
				}
			}
		}
		return false;
	}
}
