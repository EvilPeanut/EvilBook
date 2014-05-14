package com.amentrix.evilbook.listeners;

import java.util.Iterator;
import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.amentrix.evilbook.eviledit.utils.EditWandMode;
import com.amentrix.evilbook.main.DynamicSign;
import com.amentrix.evilbook.main.Emitter;
import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.PlayerProfileAdmin;
import com.amentrix.evilbook.main.Rank;
import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;
import com.amentrix.evilbook.statistics.Statistic;
import com.amentrix.evilbook.statistics.Statistics;

/**
 * Block event listener
 * @author Reece Aaron Lecrivain
 */
public class EventListenerBlock implements Listener {
	/**
	 * Called when a block is broken
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (EvilBook.isInSurvival(event.getPlayer()) && event.getPlayer().getItemInHand().getType() == Material.STICK && event.getPlayer().getItemInHand().getItemMeta().getLore() != null && event.getPlayer().getItemInHand().getItemMeta().getLore().get(0).equals("Preforms ancient magical arts")) {
			event.setCancelled(true);
		} else {
			if (EvilBook.getProfile(player).rank.isHigher(Rank.LapisStaff) && event.getPlayer().getItemInHand().getType() == Material.GOLD_SPADE && (EvilBook.isInSurvival(player) == false || EvilBook.getProfile(player).rank == Rank.ServerHost) && ((PlayerProfileAdmin)EvilBook.getProfile(player)).wandMode != EditWandMode.None) {
				if (EvilBook.getProfile(player).wandMode == EditWandMode.Selection) {
					EvilBook.getProfile(player).actionLocationA = block.getLocation();
					player.sendMessage("§7First point selected (" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ")");
				} else if (EvilBook.getProfile(player).wandMode == EditWandMode.Tree) { 
					player.getWorld().generateTree(block.getRelative(BlockFace.UP).getLocation(), TreeType.TREE);
				}
				event.setCancelled(true);
			} else {
				// Regions
				if (EvilBook.isInProtectedRegion(block.getLocation(), player) == true) {
					player.sendMessage("§cYou don't have permission to build here");
					event.setCancelled(true);
				} else {
					if (EvilBook.isInSurvival(player) && (block.getType() == Material.DISPENSER || block.getType() == Material.CHEST || block.getType() == Material.WORKBENCH || block.getType() == Material.FURNACE || block.getType() == Material.BURNING_FURNACE || block.getType() == Material.BREWING_STAND || block.getType() == Material.ANVIL || block.getType() == Material.TRAPPED_CHEST || block.getType() == Material.DROPPER)) {
						if (EvilBook.isContainerProtected(block.getLocation(), player)) {
							player.sendMessage(ChatColor.GRAY + "You don't have permission to break the " + EvilBook.getFriendlyName(block.getType()).toLowerCase(Locale.UK));
							event.setCancelled(true);
							return;
						}
						EvilBook.unprotectContainer(event.getBlock().getLocation());
						player.sendMessage(ChatColor.GRAY + EvilBook.getFriendlyName(block.getType()) + " protection removed");
					}
					// Dynamic signs
					if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
						Iterator<DynamicSign> dynamicSigns = EvilBook.dynamicSignList.iterator();
						while (dynamicSigns.hasNext()) {
							DynamicSign dynamicSign = dynamicSigns.next();
							if (dynamicSign.location.getWorld() == block.getWorld() && dynamicSign.location.getBlockX() == block.getX() && dynamicSign.location.getBlockY() == block.getY() && dynamicSign.location.getBlockZ() == block.getZ()) {
								dynamicSign.delete();
								dynamicSigns.remove();
							}
						}
					}
					// Emitters								
					Location emitterLocation = block.getRelative(BlockFace.UP).getLocation();
					SQL.deleteRowFromCriteria(TableType.Emitter, "world='" + emitterLocation.getWorld().getName() + 
							"' AND x='" + emitterLocation.getBlockX() + "' AND y='" + emitterLocation.getBlockY() + "' AND z='" + emitterLocation.getBlockZ() + "'");
					Iterator<Emitter> emit = EvilBook.emitterList.iterator();
					while (emit.hasNext()) {
						Emitter emitter = emit.next();
						if (emitter.location.getBlock().equals(emitterLocation.getBlock())) emit.remove();
					}
					// Statistics
					Statistics.incrementStatistic(Statistic.BlocksBroken, 1);
				}
			}
		}
	}
	
	/**
	 * Called when a block is placed
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if (!EvilBook.isInSurvival(player) && !EvilBook.getProfile(player).rank.isHigher(Rank.Builder) && (block.getType() == Material.ANVIL || block.getType() == Material.SAPLING || block.getType() == Material.SAND || block.getType() == Material.GRAVEL)) {
			player.sendMessage("§dThis block requires §5Advanced Builder §drank or higher");
			event.setCancelled(true);
		} else if (EvilBook.getProfile(player).rank.isAdmin() == false && (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER || block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA || block.getType() == Material.TNT || block.getType() == Material.FIRE || block.getType() == Material.MOB_SPAWNER || block.getType() == Material.PORTAL || block.getType() == Material.DRAGON_EGG)) {
			player.sendMessage("§dThis block is §5Admin §donly");
			player.sendMessage("§dPlease type §6/admin §dto learn how to become admin");
			event.setCancelled(true);
		} else if (EvilBook.isInProtectedRegion(event.getBlock().getLocation(), player)) {
			player.sendMessage("§cYou don't have permission to build here");
			event.setCancelled(true);
		} else {
			// Free-player ice to packed-ice security
			if (!EvilBook.getProfile(player).rank.isAdmin() && block.getType() == Material.ICE) block.setType(Material.PACKED_ICE);
			// Survival container protection
			if (EvilBook.isInSurvival(player) && (block.getType() == Material.DISPENSER || block.getType() == Material.CHEST || block.getType() == Material.WORKBENCH || block.getType() == Material.FURNACE || block.getType() == Material.BURNING_FURNACE || block.getType() == Material.BREWING_STAND || block.getType() == Material.ANVIL || block.getType() == Material.TRAPPED_CHEST || block.getType() == Material.DROPPER)) {
				EvilBook.protectContainer(event.getBlock().getLocation(), player);
				player.sendMessage(ChatColor.GRAY + EvilBook.getFriendlyName(block.getType()) + " protected");
			}
			// Statistics
			Statistics.incrementStatistic(Statistic.BlocksPlaced, 1);
		}
	}
	
	/**
	 * Called when a block is dispensed
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockDispense(BlockDispenseEvent event) {
		if (event.getItem().getType() == Material.WATER_BUCKET || event.getItem().getType() == Material.LAVA_BUCKET) event.setCancelled(true);
	}

	/**
	 * Called when a block is ignited
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (EvilBook.isInSurvival(event.getBlock().getLocation().getWorld())) {
			if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) event.setCancelled(true);
		} else {
			if (event.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) event.setCancelled(true);
		}
	}
	
	/**
	 * Called when a block is formed
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockForm(BlockFormEvent event) {
		if (event.getNewState().getType() == Material.SNOW && EvilBook.isInSurvival(event.getBlock().getWorld()) == false) event.setCancelled(true);
	}
	
	/**
	 * Called when a sign is changed by a player
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent e) {
		for (int i = 0; i < 4; i++) e.setLine(i, EvilBook.toFormattedString(e.getLine(i)));
	}
	
	/**
	 * Called when an entity forms a block
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityBlockForm(EntityBlockFormEvent event) {
		if (!EvilBook.isInSurvival(event.getEntity())) event.setCancelled(true);
	}
}
