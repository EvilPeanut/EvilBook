package com.amentrix.evilbook.map;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public class MapListener implements Listener {
	private Maps mapModule;
	public static List<MapView> mapViews = new ArrayList<>();
	
	public MapListener(Maps mapModule) {
		this.mapModule = mapModule;
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		final String pName = event.getPlayer().getName();
		for (MapView mapView : mapViews) {
			event.getPlayer().sendMap(mapView);
		}
		mapModule.plugin.getServer().getScheduler().runTaskAsynchronously(mapModule.plugin, new Runnable() {
			@Override
			public void run() {
				mapModule.downloadSkin(pName);
			}
		});
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		mapModule.cleanup(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onItemDespawnEvent(ItemDespawnEvent event) {
		ItemStack item = event.getEntity().getItemStack();
		if (item.getType().equals(Material.MAP)) {
			Maps.cleanLists(item.getDurability());
		}
	}

	@EventHandler
	public void onEntityCombustEvent(EntityCombustEvent event) {
		if (event.getEntity().getType().equals(EntityType.DROPPED_ITEM)) {
			ItemStack item = ((Item) event.getEntity()).getItemStack();
			if (item.getType().equals(Material.MAP)) {
				Maps.cleanLists(item.getDurability());
			}
		}
	}
}
