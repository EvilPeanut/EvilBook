package com.amentrix.evilbook.dynmap;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;

import org.bukkit.Location;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.sql.SQL;

public class WarpMarkers {
	static MarkerSet set;
	static MarkerIcon icon;

	public static void loadWarps() {
		set = EvilBook.markerAPI.getMarkerSet("evilbook.warps");
		if (set == null) {
			set = EvilBook.markerAPI.createMarkerSet("evilbook.warps", "Warps", null, false);
		} else {
			set.setMarkerSetLabel("warps");
		}
		set.setMinZoom(4);
		icon = EvilBook.markerAPI.getMarkerIcon("pin");	
		try (Statement statement = SQL.connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT warp_name, location FROM " + SQL.database + ".`evilbook-warps`;")) {
				while (rs.next()) {
					set.createMarker("evilbook.warps." + rs.getString("warp_name").toLowerCase(Locale.UK), rs.getString("warp_name"), 
							rs.getString("location").split(">")[0], Double.parseDouble(rs.getString("location").split(">")[1]),
							Double.parseDouble(rs.getString("location").split(">")[2]), Double.parseDouble(rs.getString("location").split(">")[3]), icon, false);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public static void setWarp(String warpName, Location warpLocation) {
		Marker warpMarker = set.findMarker("evilbook.warps." + warpName.toLowerCase(Locale.UK));
		if (warpMarker != null) {
			warpMarker.setLocation(warpLocation.getWorld().getName(), warpLocation.getX(), warpLocation.getY(), warpLocation.getZ());
		} else {
			set.createMarker("evilbook.warps." + warpName.toLowerCase(Locale.UK), warpName, 
					warpLocation.getWorld().getName(), warpLocation.getX(),
					warpLocation.getY(), warpLocation.getZ(), icon, false);
		}
	}
	
	public static void removeWarp(String warpName) {
		Marker warpMarker = set.findMarker("evilbook.warps." + warpName.toLowerCase(Locale.UK));
		warpMarker.deleteMarker();
	}
}
