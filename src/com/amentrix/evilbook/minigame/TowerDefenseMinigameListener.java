package com.amentrix.evilbook.minigame;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.amentrix.evilbook.main.EvilBook;

class TowerDefenseMinigameListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (!EvilBook.isInMinigame(player, MinigameType.TOWER_DEFENSE)) return;
		Block block = event.getClickedBlock();
		//
		// Tower placement handling
		//
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block.getType() == Material.GRASS) {
			Block towerBlock = block.getRelative(0, 1, 0);
			towerBlock.setType(Material.DISPENSER);
			if (player.getItemInHand().getType() == Material.ARROW) {
				
			} else if (player.getItemInHand().getType() == Material.ARROW) {

			} else if (player.getItemInHand().getType() == Material.BLAZE_POWDER) {

			} else if (player.getItemInHand().getType() == Material.FIREWORK_CHARGE) {

			} else if (player.getItemInHand().getType() == Material.BOW) {

			} else if (player.getItemInHand().getType() == Material.SNOW_BALL) {

			} else if (player.getItemInHand().getType() == Material.EGG) {

			} else if (player.getItemInHand().getType() == Material.POTION) {

			} else if (player.getItemInHand().getType() == Material.GOLD_INGOT) {

			}
		}
	}
}
