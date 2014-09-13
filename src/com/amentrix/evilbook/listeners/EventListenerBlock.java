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
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.SignChangeEvent;

import com.amentrix.evilbook.eviledit.utils.EditWandMode;
import com.amentrix.evilbook.main.DynamicSign;
import com.amentrix.evilbook.main.Emitter;
import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.PlayerProfile;
import com.amentrix.evilbook.main.PlayerProfileAdmin;
import com.amentrix.evilbook.main.Rank;
import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;
import com.amentrix.evilbook.statistics.GlobalStatistic;
import com.amentrix.evilbook.statistics.GlobalStatistics;
import com.amentrix.evilbook.statistics.PlayerStatistic;
import com.amentrix.evilbook.statistics.PlayerStatistics;

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
		Player player = event.getPlayer();
		Block block = event.getBlock();
		PlayerProfile profile = EvilBook.getProfile(player);
		if (!profile.isCanEditWorld(block.getWorld())) {
			event.setCancelled(true);
		} else if (profile.rank.isHigher(Rank.LapisStaff) && player.getItemInHand().getType() == Material.GOLD_SPADE 
				&& (EvilBook.isInSurvival(player) == false || profile.rank == Rank.ServerHost) 
				&& ((PlayerProfileAdmin)profile).wandMode != EditWandMode.None) {
			if (profile.wandMode == EditWandMode.Selection) {
				profile.actionLocationA = block.getLocation();
				player.sendMessage(ChatColor.GRAY + "First point selected (" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ")");
			} else if (profile.wandMode == EditWandMode.Tree) { 
				player.getWorld().generateTree(block.getRelative(BlockFace.UP).getLocation(), TreeType.TREE);
			}
			event.setCancelled(true);
		} else {
			// Regions
			if (EvilBook.isInProtectedRegion(block.getLocation(), player) == true) {
				player.sendMessage(ChatColor.RED + "You don't have permission to build here");
				event.setCancelled(true);
			} else {
				if (EvilBook.isInSurvival(player) && (block.getType() == Material.DISPENSER || block.getType() == Material.CHEST 
						|| block.getType() == Material.WORKBENCH || block.getType() == Material.FURNACE || block.getType() == Material.BURNING_FURNACE 
						|| block.getType() == Material.BREWING_STAND || block.getType() == Material.ANVIL || block.getType() == Material.TRAPPED_CHEST 
						|| block.getType() == Material.DROPPER)) {
					if (EvilBook.isContainerProtected(block.getLocation(), player)) {
						player.sendMessage(ChatColor.GRAY + "You don't have permission to break the " + 
								EvilBook.getFriendlyName(block.getType()).toLowerCase(Locale.UK));
						event.setCancelled(true);
						return;
					}
					EvilBook.unprotectContainer(block.getLocation());
					player.sendMessage(ChatColor.GRAY + EvilBook.getFriendlyName(block.getType()) + " protection removed");
				}
				// Dynamic signs
				if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
					Iterator<DynamicSign> dynamicSigns = EvilBook.dynamicSignList.iterator();
					while (dynamicSigns.hasNext()) {
						DynamicSign dynamicSign = dynamicSigns.next();
						if (dynamicSign.location.getWorld() == block.getWorld() && dynamicSign.location.getBlockX() == block.getX() 
								&& dynamicSign.location.getBlockY() == block.getY() && dynamicSign.location.getBlockZ() == block.getZ()) {
							dynamicSign.delete();
							dynamicSigns.remove();
						}
					}
				}
				// Emitters								
				Location emitterLocation = block.getRelative(BlockFace.UP).getLocation();
				SQL.deleteRowFromCriteria(TableType.Emitter, "world='" + emitterLocation.getWorld().getName() + 
						"' AND x='" + emitterLocation.getBlockX() + "' AND y='" + emitterLocation.getBlockY() + "' AND z='" + 
						emitterLocation.getBlockZ() + "'");
				Iterator<Emitter> emit = EvilBook.emitterList.iterator();
				while (emit.hasNext()) {
					Emitter emitter = emit.next();
					if (emitter.location.getBlock().equals(emitterLocation.getBlock())) emit.remove();
				}
				// Achievements
				if (EvilBook.isInSurvival(player)) {
					switch (block.getType()) {
					case COAL_ORE: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.MINED_COAL, 1); break;
					case IRON_ORE: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.MINED_IRON, 1); break;
					case LAPIS_ORE: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.MINED_LAPIS, 1); break;
					case GOLD_ORE: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.MINED_GOLD, 1); break;
					case DIAMOND_ORE: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.MINED_DIAMOND, 1); break;
					case REDSTONE_ORE: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.MINED_REDSTONE, 1); break;
					case GLOWING_REDSTONE_ORE: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.MINED_REDSTONE, 1); break;
					case EMERALD_ORE: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.MINED_EMERALD, 1); break;
					case QUARTZ_ORE: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.MINED_NETHERQUARTZ, 1); break;
					default: break;
					}
				}
				// Statistics
				GlobalStatistics.incrementStatistic(GlobalStatistic.BlocksBroken, 1);
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
		PlayerProfile profile = EvilBook.getProfile(player);
		if (!profile.isCanEditWorld(block.getWorld())) {
			event.setCancelled(true);
		} else if (!EvilBook.isInSurvival(player) && !profile.rank.isHigher(Rank.Builder) && (block.getType() == Material.ANVIL 
				|| block.getType() == Material.SAPLING || block.getType() == Material.SAND || block.getType() == Material.GRAVEL)) {
			player.sendMessage(ChatColor.LIGHT_PURPLE + "This block requires " + ChatColor.DARK_PURPLE + "Advanced Builder " + ChatColor.LIGHT_PURPLE + "rank or higher");
			event.setCancelled(true);
		} else if (!profile.rank.isAdmin() && (block.getType() == Material.WATER ||
				block.getType() == Material.STATIONARY_WATER || block.getType() == Material.LAVA ||
				block.getType() == Material.STATIONARY_LAVA || block.getType() == Material.TNT ||
				block.getType() == Material.FIRE || block.getType() == Material.MOB_SPAWNER || 
				block.getType() == Material.PORTAL || block.getType() == Material.DRAGON_EGG) &&
				(!EvilBook.isInSurvival(player) || !profile.rank.isHigher(Rank.Architect))) {
			player.sendMessage(ChatColor.LIGHT_PURPLE + "This block is " + ChatColor.DARK_PURPLE + "Admin " + ChatColor.LIGHT_PURPLE + "only");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "Please type " + ChatColor.GOLD + "/admin " + ChatColor.LIGHT_PURPLE + "to learn how to become admin");
			event.setCancelled(true);
		} else if (EvilBook.isInProtectedRegion(block.getLocation(), player)) {
			player.sendMessage(ChatColor.RED + "You don't have permission to build here");
			event.setCancelled(true);
		} else {
			// Free-player ice to packed-ice security
			if (!profile.rank.isAdmin() && !EvilBook.isInSurvival(player) && block.getType() == Material.ICE) block.setType(Material.PACKED_ICE);
			// Survival container protection
			if (EvilBook.isInSurvival(player) && (block.getType() == Material.DISPENSER || block.getType() == Material.CHEST 
					|| block.getType() == Material.WORKBENCH || block.getType() == Material.FURNACE || block.getType() == Material.BREWING_STAND 
					|| block.getType() == Material.ANVIL || block.getType() == Material.TRAPPED_CHEST || block.getType() == Material.DROPPER)) {
				EvilBook.protectContainer(block.getLocation(), player);
				player.sendMessage(ChatColor.GRAY + EvilBook.getFriendlyName(block.getType()) + " protected");
			}
			// Statistics
			GlobalStatistics.incrementStatistic(GlobalStatistic.BlocksPlaced, 1);
		}
	}

	/**
	 * Called when a block is dispensed
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockDispense(BlockDispenseEvent event) {
		if (!event.getEntity().getWorld().getName().contains("Private worlds") && 
				(event.getItem().getType() == Material.WATER_BUCKET || event.getItem().getType() == Material.LAVA_BUCKET)) event.setCancelled(true);
	}

	/**
	 * Called when a block is ignited
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockIgnite(BlockIgniteEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if (player != null && !EvilBook.getProfile(player).isCanEditWorld(block.getWorld())) {
			event.setCancelled(true);
		} else if (EvilBook.isInSurvival(block.getWorld())) {
			if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) event.setCancelled(true);
		} else {
			if (event.getCause() != BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL) event.setCancelled(true);
		}
	}

	/**
	 * Called when a sign is changed by a player
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();		
		if (!EvilBook.getProfile(player).isCanEditWorld(block.getWorld())) {
			event.setCancelled(true);
		} else {
			for (int i = 0; i < 4; i++) event.setLine(i, EvilBook.toFormattedString(event.getLine(i)));
		}
	}

	/**
	 * Called when an entity forms a block
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityBlockForm(EntityBlockFormEvent event) {
		if (!EvilBook.isInSurvival(event.getEntity())) event.setCancelled(true);
	}
}