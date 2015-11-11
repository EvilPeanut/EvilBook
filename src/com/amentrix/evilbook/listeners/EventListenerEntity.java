package com.amentrix.evilbook.listeners;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.PlayerProfile;
import com.amentrix.evilbook.statistics.PlayerStatistic;
import com.amentrix.evilbook.statistics.PlayerStatistics;

/**
 * Entity and hanging entity event listener
 * @author Reece Aaron Lecrivain
 */
public class EventListenerEntity implements Listener {
	/**
	 * Called when an entity dies
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		//
		// Rare mob drops
		//
		if (EvilBook.isInSurvival(entity) && EvilBook.rareSpawnList.contains(entity.getUniqueId())) {
			Random rand = new Random();
		    int randomNum = rand.nextInt(1000);
			if (randomNum >= 0 && randomNum <= 100) {
				event.getDrops().add(new ItemStack(Material.DIAMOND_SWORD));
			} else if (randomNum >= 101 && randomNum <= 150) {
				ItemStack cocain = new ItemStack(Material.SUGAR);
				ItemMeta meta = cocain.getItemMeta();
				meta.setDisplayName("Cocain");
				meta.setLore(Arrays.asList("Ruff stuff"));
				cocain.setItemMeta(meta);
				event.getDrops().add(cocain);
			} else if (randomNum >= 151 && randomNum <= 200) {
				ItemStack shrooms = new ItemStack(Material.RED_MUSHROOM);
				ItemMeta meta = shrooms.getItemMeta();
				meta.setDisplayName("Hallucinogenic Mushroom");
				meta.setLore(Arrays.asList("Shroooms!"));
				shrooms.setItemMeta(meta);
				event.getDrops().add(shrooms);
			} else if (randomNum >= 201 && randomNum <= 250) {
				ItemStack alcohol = new ItemStack(Material.POTION);
				ItemMeta meta = alcohol.getItemMeta();
				meta.setDisplayName("Alcohol");
				meta.setLore(Arrays.asList("Hick!"));
				alcohol.setItemMeta(meta);
				event.getDrops().add(alcohol);
			} else if (randomNum >= 251 && randomNum <= 300) {
				event.getDrops().add(new ItemStack(Material.ANVIL));
			} else if (randomNum >= 301 && randomNum <= 400) {
				event.getDrops().add(new ItemStack(Material.IRON_BLOCK));
			} else if (randomNum >= 401 && randomNum <= 450) {
				event.getDrops().add(new ItemStack(Material.GOLD_BLOCK));
			} else if (randomNum >= 451 && randomNum <= 500) {
				event.getDrops().add(new ItemStack(Material.DIAMOND_BLOCK));
			} else if (randomNum >= 501 && randomNum <= 525) {
				event.getDrops().add(new ItemStack(Material.EMERALD_BLOCK));
			} else if (randomNum >= 526 && randomNum <= 600) {
				event.getDrops().add(new ItemStack(Material.GOLD_HELMET));
			} else if (randomNum >= 601 && randomNum <= 700) {
				event.getDrops().add(new ItemStack(Material.DIAMOND_HELMET));
			} else if (randomNum >= 701 && randomNum <= 800) {
				event.getDrops().add(new ItemStack(Material.DIAMOND_CHESTPLATE));
			} else if (randomNum >= 801 && randomNum <= 900) {
				event.getDrops().add(new ItemStack(Material.DIAMOND_LEGGINGS));
			} else {
				event.getDrops().add(new ItemStack(Material.DIAMOND_BOOTS));
			}
			event.setDroppedExp(event.getDroppedExp() * 4);
		}
		//
		// Statistics
		//
		if (entity.getKiller() != null) {
			Player player = entity.getKiller();
			if (!EvilBook.isInSurvival(player)) return;
			if (EvilBook.rareSpawnList.contains(entity.getUniqueId())) {
				PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_RARES, 1);
				EvilBook.rareSpawnList.remove(entity.getUniqueId());
			}
			switch (event.getEntityType()) {
			case PIG: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_PIGS, 1); break;
			case VILLAGER: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_VILLAGERS, 1); break;
			case CAVE_SPIDER: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_CAVESPIDERS, 1); break;
			case ENDERMAN: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_ENDERMEN, 1); break;
			case SPIDER: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_SPIDERS, 1); break;
			case WOLF: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_WOLVES, 1); break;
			case PIG_ZOMBIE: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_ZOMBIEPIGS, 1); break;
			case BLAZE: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_BLAZES, 1); break;
			case CREEPER: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_CREEPERS, 1); break;
			case GHAST: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_GHASTS, 1); break;
			case MAGMA_CUBE: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_MAGMACUBES, 1); break;
			case SILVERFISH: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_SILVERFISH, 1); break;
			case SKELETON: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_SKELETONS, 1); break;
			case SLIME: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_SLIMES, 1); break;
			case WITCH: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_WITCHES, 1); break;
			case ZOMBIE: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_ZOMBIES, 1); break;
			case ENDER_DRAGON: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_ENDERDRAGONS, 1); break;
			case WITHER: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_WITHERS, 1); break;
			case PLAYER: PlayerStatistics.incrementStatistic(player.getName(), PlayerStatistic.KILLED_PLAYERS, 1); break;
			default: break;
			}
		}
	}

	/**
	 * Called when an entity is damaged by another entity
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player)event.getDamager();
			if (!EvilBook.getProfile(player).isCanEditWorld(event.getEntity().getWorld())) {
				player.sendMessage(ChatColor.RED + "You need to rank up to edit this world");
				event.setCancelled(true);
			} else if (EvilBook.isInProtectedRegion(event.getEntity().getLocation(), player) == true) {
				// Regions
				player.sendMessage("§cYou don't have permission to build here");
				event.setCancelled(true);
			}
		}
	}

	/**
	 * Called when a hanging entity is created
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingPlaceEvent(HangingPlaceEvent event) {
		if (!EvilBook.getProfile(event.getPlayer()).isCanEditWorld(event.getBlock().getWorld())) {
			event.getPlayer().sendMessage(ChatColor.RED + "You need to rank up to edit this world");
			event.setCancelled(true);
		} else if (EvilBook.isInProtectedRegion(event.getBlock().getLocation(), event.getPlayer()) == true) {
			// Regions
			event.getPlayer().sendMessage("§cYou don't have permission to build here");
			event.setCancelled(true);
		}
	}

	/**
	 * Called when a hanging entity is removed
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingBreak(HangingBreakEvent event) {
		if (!EvilBook.isInSurvival(event.getEntity()) && !EvilBook.isInPrivateWorld(event.getEntity()) 
				&& event.getCause() == RemoveCause.EXPLOSION) event.setCancelled(true);
	}

	/**
	 * Called when a hanging entity is removed by an entity
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
		if (event.getRemover() instanceof Player) {
			if (!EvilBook.getProfile(event.getRemover()).isCanEditWorld(event.getEntity().getWorld())) {
				((Player)event.getRemover()).sendMessage(ChatColor.RED + "You need to rank up to edit this world");
				event.setCancelled(true);
			} else if (EvilBook.isInProtectedRegion(event.getEntity().getLocation(), (Player)event.getRemover())) {
				// Regions
				((Player)event.getRemover()).sendMessage("§cYou don't have permission to build here");
				event.setCancelled(true);
			}
		}
	}

	/**
	 * Called when an entity targets another living entity
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
		if (event.getTarget() instanceof Player && EvilBook.getProfile(((Player)event.getTarget()).getName()).isAway) event.setCancelled(true);
	}

	/**
	 * Called when an entity explodes
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (!EvilBook.isInSurvival(event.getEntity()) && !EvilBook.isInPrivateWorld(event.getEntity())) {
			event.setCancelled(true);
		} else {
			for (Iterator<Block> it = event.blockList().iterator(); it.hasNext();) {
				if (EvilBook.isContainerProtected(it.next().getLocation())) {
					//TODO: Check this actually removes the protected container
					// Async?
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
		if (!EvilBook.isInSurvival(event.getEntity()) && !EvilBook.isInPrivateWorld(event.getEntity()) && (event.getEntityType() == EntityType.ENDERMAN || event.getEntityType() == EntityType.SILVERFISH)) event.setCancelled(true);
	}

	/**
	 * Called when an entity is damaged
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && EvilBook.getProfile(event.getEntity()).isAway) {
			event.setCancelled(true);
		} else {
			for (PlayerProfile profile : EvilBook.playerProfiles.values()) {
				if (profile.disguise == event.getEntity()) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	/**
	 * Called when a creature is spawned
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		LivingEntity spawnedEntity = event.getEntity();
		if (!EvilBook.isInSurvival(spawnedEntity)) {
			if (event.getEntityType() == EntityType.SHEEP) ((Sheep)spawnedEntity).setColor(DyeColor.values()[new Random().nextInt(DyeColor.values().length)]);
			if (event.getSpawnReason() == SpawnReason.SPAWNER && spawnedEntity.getNearbyEntities(32, 32, 32).size() >= 200) {
				event.setCancelled(true);
			}
		} else if (event.getSpawnReason() != SpawnReason.SPAWNER && spawnedEntity instanceof Monster) {
			Random rand = new Random();
			if (rand.nextInt(50) == 0) {
				List<Entity> entityList = spawnedEntity.getNearbyEntities(32, 32, 32);
				for (Entity entity : entityList) {
					if (entity instanceof Player) {
						Player player = (Player) entity;
						player.sendMessage("§a☠ A rare creature has spawned near you! ☠");
					}
				}
				EvilBook.rareSpawnList.add(spawnedEntity.getUniqueId());
				spawnedEntity.setMaxHealth(spawnedEntity.getMaxHealth() * 4);
				String[] names = {"Hercules", "Achilles", "Theseus", "Odysseus", "Perseus", "Bellerophon", "Orpheus", "Cadmus"};
				spawnedEntity.setCustomName(names[rand.nextInt(names.length)]);
				spawnedEntity.setCustomNameVisible(true);
			}
		}
	}
}