package com.amentrix.evilbook.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.sql.SQL;

/**
 * Region event listener
 * @author Reece Aaron Lecrivain
 */
public class RegionListener implements Listener {
	private EvilBook plugin;

	public RegionListener(EvilBook plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Called when a player joins the server after login
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) {
		for (Region region : EvilBook.regionList) {
			if (EvilBook.isInRegion(region, event.getPlayer().getLocation())) {
				if (region.getWelcomeMessage() != null) event.getPlayer().sendMessage(region.getWelcomeMessage());
				if (region.getWarp() != null) {
					event.getPlayer().teleport(SQL.getWarp(region.getWarp()));
					break;
				}
			}
		}
	}
	
	/**
	 * Called when a player moves
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerMove(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		final Location to = event.getTo();
		final Location from = event.getFrom();
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				try {
					for (Region region : EvilBook.regionList) {
						if (region.getLeaveMessage() != null && EvilBook.isInRegion(region, from) && EvilBook.isInRegion(region, to) == false) {
							player.sendMessage(region.getLeaveMessage());
						} else if (EvilBook.isInRegion(region, to)) {
							if (region.getWelcomeMessage() != null && EvilBook.isInRegion(region, from) == false) player.sendMessage(region.getWelcomeMessage());
							if (region.getWarp() != null && SQL.getWarp(region.getWarp()) != null) {
								player.teleport(SQL.getWarp(region.getWarp()));
								break;
							}
						}
					}
				} catch (Exception e) {
					//Ignore rare async entity world add - WARNING: May be cause of crashes if thrown in large quantity
				}
			}
		});
	}
	
	/**
	 * Called when a player respawns
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		for (Region region : EvilBook.regionList) {
			if (EvilBook.isInRegion(region, event.getRespawnLocation())) {
				if (region.getWelcomeMessage() != null) event.getPlayer().sendMessage(region.getWelcomeMessage());
				if (region.getWarp() != null && SQL.getWarp(region.getWarp()) != null) {
					event.getPlayer().teleport(SQL.getWarp(region.getWarp()));
					break;
				}
			}
		}
	}
}
