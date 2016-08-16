package com.amentrix.evilbook.minigame;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import com.amentrix.evilbook.achievement.Achievement;
import com.amentrix.evilbook.main.EvilBook;

public class SkyBlockMinigameListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onItemCraft(CraftItemEvent event) {
		if (!EvilBook.isInMinigame(event.getWhoClicked(), MinigameType.SKYBLOCK)) return;
		ItemStack item = event.getRecipe().getResult();
		//
		// Craft stone button achievement
		//
		if (item.getType() == Material.STONE_BUTTON) {
			EvilBook.getProfile(event.getViewers().get(0)).addAchievement(Achievement.SKYBLOCK_CRAFT_STONEBUTTON);
		//
		// Craft boat achievement
		//
		} else if (item.getType() == Material.BOAT) {
			EvilBook.getProfile(event.getViewers().get(0)).addAchievement(Achievement.SKYBLOCK_CRAFT_BOAT);
		//
		// Craft wooden door achievement
		//
		} else if (item.getType() == Material.WOOD_DOOR) {
			EvilBook.getProfile(event.getViewers().get(0)).addAchievement(Achievement.SKYBLOCK_CRAFT_DOOR);
		//
		// Craft bow achievement
		//
		} else if (item.getType() == Material.BOW) {
			EvilBook.getProfile(event.getViewers().get(0)).addAchievement(Achievement.SKYBLOCK_CRAFT_BOW);
		//
		// Craft fishing rod achievement
		//
		} else if (item.getType() == Material.FISHING_ROD) {
			EvilBook.getProfile(event.getViewers().get(0)).addAchievement(Achievement.SKYBLOCK_CRAFT_ROD);
		//
		// Craft bed achievement
		//
		} else if (item.getType() == Material.BED) {
			EvilBook.getProfile(event.getViewers().get(0)).addAchievement(Achievement.SKYBLOCK_CRAFT_BED);	
		//
		// Craft jukebox achievement
		//
		} else if (item.getType() == Material.JUKEBOX) {
			EvilBook.getProfile(event.getViewers().get(0)).addAchievement(Achievement.SKYBLOCK_CRAFT_JUKEBOX);	
		}
	}
}
