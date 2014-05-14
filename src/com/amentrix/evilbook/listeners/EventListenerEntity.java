package com.amentrix.evilbook.listeners;

import java.util.Iterator;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;

import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.PlayerProfile;

/**
 * Entity and hanging entity event listener
 * @author Reece Aaron Lecrivain
 */
public class EventListenerEntity implements Listener {
	/**
	 * Called when an entity is damaged by another entity
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player == false) return;
		// Regions
		if (EvilBook.isInProtectedRegion(event.getEntity().getLocation(), (Player)event.getDamager()) == true) {
			((Player)event.getDamager()).sendMessage("§cYou don't have permission to build here");
			event.setCancelled(true);
		}
		//
	}
	
	/**
	 * Called when a hanging entity is created
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingPlaceEvent(HangingPlaceEvent event) {
		// Regions
		if (EvilBook.isInProtectedRegion(event.getBlock().getLocation(), event.getPlayer()) == true) {
			event.getPlayer().sendMessage("§cYou don't have permission to build here");
			event.setCancelled(true);
		}
		//
	}
	
	/**
	 * Called when a hanging entity is removed
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingBreak(HangingBreakEvent event) {
		if (!EvilBook.isInSurvival(event.getEntity()) && event.getCause() == RemoveCause.EXPLOSION) event.setCancelled(true);
	}
	
	/**
	 * Called when a hanging entity is removed by an entity
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
		if (event.getRemover() instanceof Player == false) return;
		// Regions
		if (EvilBook.isInProtectedRegion(event.getEntity().getLocation(), (Player)event.getRemover())) {
			((Player)event.getRemover()).sendMessage("§cYou don't have permission to build here");
			event.setCancelled(true);
		}
	}
	
	/**
	 * Called when an entity targets another living entity
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
		if (event.getTarget() != null && event.getTarget() instanceof Player && EvilBook.getProfile(((Player)event.getTarget()).getName()).isAway) event.setCancelled(true);
	}
	
	/**
	 * Called when an entity explodes
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (!EvilBook.isInSurvival(event.getEntity()) && !event.getEntity().getWorld().getName().contains("Private worlds")) {
			event.setCancelled(true);
		} else {
			for (Iterator<Block> it = event.blockList().iterator(); it.hasNext();) {
				if (EvilBook.isContainerProtected(it.next().getLocation())) {
					it.remove();
				}
			}
		}
	}
	
	/**
	 * Called when an entity changes a block except players
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		if (EvilBook.isInSurvival(event.getEntity()) == false && (event.getEntityType() == EntityType.ENDERMAN || event.getEntityType() == EntityType.SILVERFISH)) event.setCancelled(true);
	}
	
	/**
	 * Called when an entity is damaged
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && EvilBook.getProfile(((Player)event.getEntity())).isAway) event.setCancelled(true);
		for (PlayerProfile profile : EvilBook.playerProfiles.values()) {
			if (profile.disguise == event.getEntity()) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	/**
	 * Called when a creature is spawned
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (!EvilBook.isInSurvival(event.getEntity()) && event.getEntityType() == EntityType.SHEEP) ((Sheep)event.getEntity()).setColor(DyeColor.values()[new Random().nextInt(DyeColor.values().length)]);
	}
}
