package com.amentrix.evilbook.minigame;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.amentrix.evilbook.main.EvilBook;

public class SkyBlockMinigameListener implements Listener {
	private EvilBook plugin;

	public SkyBlockMinigameListener(EvilBook plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!EvilBook.isInMinigame(event.getBlock().getWorld(), MinigameType.SKYBLOCK)) return;
		Block block = event.getBlock();
		//
		// Harvest cobblestone achievement
		//
		if (block.getType() == Material.COBBLESTONE) {
		
		//
		// Harvest pumpkin achievement
		//
		} else if (block.getType() == Material.PUMPKIN) {
			
		}
	}
}
