package com.amentrix.evilbook.listeners;

import java.util.Iterator;
import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.InventoryHolder;

import com.amentrix.evilbook.eviledit.utils.EditWandMode;
import com.amentrix.evilbook.main.DynamicSign;
import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.PlayerProfile;
import com.amentrix.evilbook.main.PlayerProfileAdmin;
import com.amentrix.evilbook.main.Rank;
import com.amentrix.evilbook.minigame.MinigameType;
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
	private EvilBook plugin;
	
	public EventListenerBlock(EvilBook plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Called when the server is deciding if the block can be placed
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockCanBuild(BlockCanBuildEvent event) {
		if (!event.isBuildable() && !EvilBook.isInSurvival(event.getBlock().getWorld()) && (event.getBlock().isEmpty() || event.getBlock().isLiquid())){
			event.setBuildable(true);
		}
	}

	/**
	 * Called when a block is broken
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		final Player player = event.getPlayer();
		final Block block = event.getBlock();
		PlayerProfile profile = EvilBook.getProfile(player);
		if (!profile.isCanEditWorld(block.getWorld())) {
			player.sendMessage(ChatColor.RED + "You need to rank up to edit this world");
			event.setCancelled(true);
		} else if (profile.rank.isHigher(Rank.STAFF_LAPIS) && player.getItemInHand().getType() == Material.GOLD_SPADE 
				&& (EvilBook.isInSurvival(player) == false || EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) 
				&& ((PlayerProfileAdmin)profile).wandMode != EditWandMode.None) {
			if (profile.wandMode == EditWandMode.Selection) {
				profile.actionLocationA = block.getLocation();
				player.sendMessage(ChatColor.GRAY + "First point selected (" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ")");
			} else if (profile.wandMode == EditWandMode.Tree) { 
				player.getWorld().generateTree(block.getRelative(BlockFace.UP).getLocation(), TreeType.TREE);
			}
			event.setCancelled(true);
		} else if (EvilBook.isInProtectedRegion(block.getLocation(), player) == true) {
			player.sendMessage(ChatColor.RED + "You don't have permission to build here");
			event.setCancelled(true);
		} else {
			if (EvilBook.isInPlotWorld(player)) {
				// Plotworld plot protection
				if (EvilBook.isInPlotworldRegion(block.getLocation())) {
					if (EvilBook.isInProtectedPlotworldRegion(block.getLocation(), player)) {
						player.sendMessage(ChatColor.RED + "You don't have permission to build here");
						event.setCancelled(true);
						return;
					}
				} else {
					player.sendMessage(ChatColor.DARK_PURPLE + "You don't have permission to build here");
					player.sendMessage(ChatColor.LIGHT_PURPLE + "This plot can be purchased for $100 using /claim");
					event.setCancelled(true);
					return;
				}
			} else if (EvilBook.isInSurvival(player)) {
				// Survival container protection
				if (block.getState() instanceof InventoryHolder) {
					if (EvilBook.isContainerProtected(block.getLocation(), player)) {
						player.sendMessage(ChatColor.GRAY + "You don't have permission to break the " + 
								EvilBook.getFriendlyName(block.getType()).toLowerCase(Locale.UK));
						event.setCancelled(true);
						return;
					}
					EvilBook.unprotectContainer(block.getLocation());
					player.sendMessage(ChatColor.GRAY + EvilBook.getFriendlyName(block.getType()) + " protection removed");
				}
				// Achievements
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
			plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
				@Override
				public void run() {
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
					// Command block logging
					if (block.getType() == Material.COMMAND && SQL.getPropertyFromCriteria(TableType.CommandBlock, "player_owner='" + player.getName() + 
							"' AND x='" + block.getX() + "' AND y='" + block.getY() + "' AND z='" + block.getZ() + "'", "player_owner") != null) {
						SQL.deleteRowFromCriteria(TableType.CommandBlock, "player_owner='" + player.getName() + 
								"' AND x='" + block.getX() + "' AND y='" + block.getY() + "' AND z='" + block.getZ() + "'");
					}
					// Statistics
					GlobalStatistics.incrementStatistic(GlobalStatistic.BlocksBroken, 1);
				}
			});
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
			player.sendMessage(ChatColor.RED + "You need to rank up to edit this world");
			event.setCancelled(true);
		} else if (event.getBlockAgainst().getState() instanceof Sign) {
			event.setCancelled(true);
		} else if (!EvilBook.isInSurvival(player) && !EvilBook.isInMinigame(player, MinigameType.SKYBLOCK) && !profile.rank.isHigher(Rank.BUILDER) && !EvilBook.isInPrivateWorld(player) && (block.getType() == Material.ANVIL 
				|| block.getType() == Material.SAPLING || block.getType() == Material.SAND || block.getType() == Material.GRAVEL || block.getType() == Material.MOB_SPAWNER)) {
			player.sendMessage(ChatColor.LIGHT_PURPLE + "This block requires " + ChatColor.DARK_PURPLE + Rank.ADVANCED_BUILDER.getName() + " " + ChatColor.LIGHT_PURPLE + "rank or higher");
			event.setCancelled(true);
		} else if (!profile.rank.isAdmin() && !EvilBook.isInPrivateWorld(player) && !EvilBook.isInMinigame(player, MinigameType.SKYBLOCK) && (block.getType() == Material.WATER ||
				block.getType() == Material.STATIONARY_WATER || block.getType() == Material.LAVA ||
				block.getType() == Material.STATIONARY_LAVA || block.getType() == Material.TNT ||
				block.getType() == Material.FIRE || block.getType() == Material.PORTAL ||
				block.getType() == Material.DRAGON_EGG) &&
				(!EvilBook.isInSurvival(player) || !profile.rank.isHigher(Rank.ARCHITECT))) {
			player.sendMessage(ChatColor.LIGHT_PURPLE + "This block is " + ChatColor.DARK_PURPLE + "Admin " + ChatColor.LIGHT_PURPLE + "only");
			player.sendMessage(ChatColor.LIGHT_PURPLE + "Please type " + ChatColor.GOLD + "/admin " + ChatColor.LIGHT_PURPLE + "to learn how to become admin");
			event.setCancelled(true);
		} else if (EvilBook.isInProtectedRegion(block.getLocation(), player)) {
			player.sendMessage(ChatColor.RED + "You don't have permission to build here");
			event.setCancelled(true);
		} else {
			// Plotworld plot protection
			if (EvilBook.isInPlotWorld(player)) {
				if (EvilBook.isInPlotworldRegion(block.getLocation())) {
					if (EvilBook.isInProtectedPlotworldRegion(block.getLocation(), player)) {
						player.sendMessage(ChatColor.RED + "You don't have permission to build here");
						event.setCancelled(true);
						return;
					}
				} else {
					player.sendMessage(ChatColor.DARK_PURPLE + "You don't have permission to build here");
					player.sendMessage(ChatColor.LIGHT_PURPLE + "This plot can be purchased for $100 using /claim");
					event.setCancelled(true);
					return;
				}
			}
			// Free-player ice to packed-ice security
			if (!profile.rank.isAdmin() && !EvilBook.isInSurvival(player) && !EvilBook.isInMinigame(player, MinigameType.SKYBLOCK) && block.getType() == Material.ICE) block.setType(Material.PACKED_ICE);
			// Survival container protection
			if (EvilBook.isInSurvival(player) && block.getState() instanceof InventoryHolder) {
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
		if (!EvilBook.isInPrivateWorld(event.getBlock().getWorld()) && 
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
			player.sendMessage(ChatColor.RED + "You need to rank up to edit this world");
			event.setCancelled(true);
		} else if (EvilBook.isInSurvival(block.getWorld())) {
			if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) event.setCancelled(true);
		} else if (!EvilBook.isInPrivateWorld(block.getWorld())) {
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
			player.sendMessage(ChatColor.RED + "You need to rank up to edit this world");
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
		if (!EvilBook.isInSurvival(event.getEntity()) && !EvilBook.isInPrivateWorld(event.getEntity())) event.setCancelled(true);
	}

	/**
	 * Tweak to prevent hatch (Trap Door) physics
	 * which prevents hatchs from breaking when supporting block isn't present
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPhysics(BlockPhysicsEvent event)
	{
		Block block = event.getBlock();

		if (block.getType() == Material.TRAP_DOOR) {
			event.setCancelled(true);
		}
	}
}