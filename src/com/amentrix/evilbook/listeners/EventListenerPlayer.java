package com.amentrix.evilbook.listeners;

import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Monster;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Spider;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
//import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.material.Dye;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
//import org.bukkit.util.Vector;





import com.amentrix.evilbook.achievement.Achievement;
import com.amentrix.evilbook.eviledit.utils.EditWandMode;
import com.amentrix.evilbook.main.DynamicSign;
import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.PlayerProfile;
import com.amentrix.evilbook.main.PlayerProfileAdmin;
import com.amentrix.evilbook.main.PlayerProfileNormal;
import com.amentrix.evilbook.main.Rank;
import com.amentrix.evilbook.main.Region;
import com.amentrix.evilbook.minigame.MinigameType;
import com.amentrix.evilbook.nametag.NametagManager;
import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;
import com.amentrix.evilbook.statistics.GlobalStatistic;
import com.amentrix.evilbook.statistics.GlobalStatistics;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

/**
 * Player event listener
 * @author Reece Aaron Lecrivain
 */
public class EventListenerPlayer implements Listener {
	private EvilBook plugin;

	public EventListenerPlayer(EvilBook plugin) {
		this.plugin = plugin;
	}

	/**
	 * Called when a player attempts to login
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (event.getResult().equals(Result.KICK_BANNED)) event.setKickMessage("§cYou are banned! E-mail §6support@amentrix.com §cfor support");
	}

	/**
	 * Called when a player joins the server after login
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) {
		for (PlayerProfile profile : EvilBook.playerProfiles.values()) {
			if (profile.isInvisible) event.getPlayer().hidePlayer(profile.getPlayer());
		}
		// Make sure player statistics entry exists
		if (!SQL.isKeyExistant(TableType.PlayerStatistics, event.getPlayer().getName())) {
			SQL.insert(TableType.PlayerStatistics, "'" + event.getPlayer().getName() + "',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL");
		}
		//
		if (SQL.getProperty(TableType.PlayerProfile, event.getPlayer().getName(), "Rank") != null && Rank.valueOf(SQL.getProperty(TableType.PlayerProfile, event.getPlayer().getName(), "Rank")).isAdmin()) {
			EvilBook.playerProfiles.put(event.getPlayer().getName().toLowerCase(Locale.UK), new PlayerProfileAdmin(plugin, event.getPlayer()));
		} else {
			EvilBook.playerProfiles.put(event.getPlayer().getName().toLowerCase(Locale.UK), new PlayerProfileNormal(plugin, event.getPlayer()));
		}
		event.setJoinMessage(null);
		// Name Tag
		NametagManager.sendTeamsToPlayer(event.getPlayer());
		NametagManager.clear(event.getPlayer().getName());
		// Statistics
		GlobalStatistics.incrementStatistic(GlobalStatistic.LoginTotal, 1);
		// Regions
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
		PlayerProfile profile = EvilBook.getProfile(event.getPlayer());
		profile.lastActionTime = System.currentTimeMillis();
		if (profile.isAway) {
			profile.isAway = false;
			profile.updatePlayerListName();
		}
		/*
		if (event.getTo().getBlockX() > 12550820 || event.getTo().getBlockX() < -12550820 || event.getTo().getBlockZ() > 12550820 || event.getTo().getBlockZ() < -12550820) {
			event.getPlayer().sendMessage("§7The Far Lands are blocked");
			event.getPlayer().teleport(event.getTo().add(event.getTo().getBlockX() > 12550820 ? -2 : event.getTo().getBlockX() < -12550820 ? 2 : 0, 0, event.getTo().getBlockZ() > 12550820 ? -2 : event.getTo().getBlockZ() < -12550820 ? 2 : 0));
			event.setCancelled(true);
		} else {
			// Drugcraft
			if (profile.isDrunk) {
				Vector velocity = event.getPlayer().getVelocity();
				event.getPlayer().setVelocity(new Vector((velocity.getX() + (1 - new Random().nextInt(3))) / 3, velocity.getY(), (velocity.getZ() + (1 - new Random().nextInt(3))) / 3));
			}
			//
			if (!EvilBook.isInSurvival(event.getPlayer())) {
				if (profile.jumpAmplifier != 0 && !event.getPlayer().isFlying() && event.getFrom().getY() < event.getTo().getY() && event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) event.getPlayer().setVelocity(event.getPlayer().getVelocity().setY(profile.jumpAmplifier));
				if (profile.runAmplifier != 0 && event.getPlayer().isSprinting()) event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, profile.runAmplifier), true);
			}
			// Regions
			for (Region region : EvilBook.regionList) {
				if (region.getLeaveMessage() != null && EvilBook.isInRegion(region, event.getFrom()) && EvilBook.isInRegion(region, event.getTo()) == false) {
					event.getPlayer().sendMessage(region.getLeaveMessage());
				} else if (EvilBook.isInRegion(region, event.getTo())) {
					if (region.getWelcomeMessage() != null && EvilBook.isInRegion(region, event.getFrom()) == false) event.getPlayer().sendMessage(region.getWelcomeMessage());
					if (region.getWarp() != null && SQL.getWarp(region.getWarp()) != null) {
						event.getPlayer().teleport(SQL.getWarp(region.getWarp()));
						break;
					}
				}
			}
		}
		*/
	}

	/**
	 * Called when a player throws an egg
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerEggThrow(PlayerEggThrowEvent event) {
		if (event.getPlayer().getNearbyEntities(64, 64, 64).size() >= 400 && EvilBook.getProfile(event.getPlayer()).rank.isAdmin() == false) event.setHatching(false);
	}

	/**
	 * Called when a player is about to be teleported by a portal
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPortal(PlayerPortalEvent event) {
		// Regions
		for (Region region : EvilBook.regionList) {
			if (EvilBook.isInRegion(region, event.getFrom())) {
				if (region.getWarp() != null && SQL.getWarp(region.getWarp()) != null) {
					event.setCancelled(true);
					return;
				}
			}
		}
		//
		String fromWorldName = event.getFrom().getWorld().getName();
		if (fromWorldName.equals("SurvivalLand")) {
			if (event.getCause() == TeleportCause.NETHER_PORTAL) {
				event.getPlayer().teleport(Bukkit.getServer().getWorld("SurvivalLandNether").getSpawnLocation());
			} else if (event.getCause() == TeleportCause.END_PORTAL) {
				event.getPlayer().teleport(Bukkit.getServer().getWorld("SurvivalLandTheEnd").getSpawnLocation());
			}
		} else if (fromWorldName.equals("SurvivalLandNether") || fromWorldName.equals("SurvivalLandTheEnd")) {
			event.getPlayer().teleport(Bukkit.getServer().getWorld("SurvivalLand").getSpawnLocation());
		}
	}

	/**
	 * Called when a player drops an item
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (!EvilBook.getProfile(event.getPlayer()).rank.isAdmin() && !EvilBook.isInSurvival(event.getPlayer())) {
			int dropCount = 0;
			for (Entity e : event.getPlayer().getLocation().getWorld().getEntities()) if (e.getType() == EntityType.DROPPED_ITEM) dropCount++;
			if (dropCount >= 128) event.setCancelled(true);
		}
	}

	/**
	 * Called when a player empties a bucket
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		if (!EvilBook.getProfile(player).rank.isAdmin() && !EvilBook.isInMinigame(player, MinigameType.SKYBLOCK) && (!EvilBook.isInSurvival(player) || !EvilBook.getProfile(player).rank.isHigher(Rank.ARCHITECT))) {
			player.sendMessage((event.getBucket() == Material.LAVA_BUCKET ? "§dLava buckets" : "§dWater buckets") + " are an §5Admin §donly feature");
			player.sendMessage("§dPlease type §6/admin §dto learn how to become admin");
			event.setCancelled(true);
		}
	}

	/**
	 * Called when a player dies
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		EvilBook.getProfile(player).lastLocation = player.getLocation();
		if (player.getLastDamageCause() == null) return;
		EntityDamageEvent damageEvent = player.getLastDamageCause();
		EntityDamageEvent.DamageCause damageCause = damageEvent.getCause();
		if (damageEvent instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent kie = (EntityDamageByEntityEvent)damageEvent;
			Entity damager = kie.getDamager();
			if (damageCause == DamageCause.ENTITY_ATTACK) {
				if (damager instanceof Player)
				{
					Player attackPlayer = (Player) damager;
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " was murdered by " + attackPlayer.getDisplayName() + ChatColor.RED + " wielding their " + (attackPlayer.getItemInHand().getType() == Material.AIR ? "fists" : attackPlayer.getItemInHand().getType().toString().toLowerCase().replace("_", " ")));
				} else if (damager instanceof Zombie) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their brains eaten by a Zombie");
				} else if (damager instanceof Spider) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their flesh devoured by a Spider");
				} else if (damager instanceof CaveSpider) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their flesh devoured by a Cave-Spider");
				} else if (damager instanceof Enderman) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their soul consumed by an Enderman");
				} else if (damager instanceof Silverfish) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their insides ripped out by a Silverfish");
				} else if (damager instanceof MagmaCube) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their corpse burnt to ashes by a Magma-Cube");
				} else if (damager instanceof Slime) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their corpse dissolved in a Slime");
				} else if (damager instanceof Wolf) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their corpse ripped appart by a Wolf");
				} else if (damager instanceof PigZombie) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their corpse cut in two by a Pigman-Zombie");
				} else if (damager instanceof IronGolem) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their corpse crushed by an Iron-Golem");
				} else if (damager instanceof Giant) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their corpse crushed by a Giant");
				} else if (damager instanceof Blaze) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their corpse incinerated by a Blaze");
				} else if (damager instanceof Endermite) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their corpse chewed apart by an Endermite");
				} else if (damager instanceof Guardian) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their corpse zapped by a Guardian");
				}
			} else if (damageCause == DamageCause.PROJECTILE) {
				Projectile pro = (Projectile)damager;
				if (pro.getShooter() instanceof Player) {
					Player attackPlayer = (Player) pro.getShooter();
					if (pro instanceof Arrow) {
						event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " was killed by " + attackPlayer.getDisplayName() + ChatColor.RED + "'s arrow");
					} else if (pro instanceof Snowball) {
						event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " was killed by " + attackPlayer.getDisplayName() + ChatColor.RED + "'s snowball");
					} else if (pro instanceof Egg) {
						event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " was killed by " + attackPlayer.getDisplayName() + ChatColor.RED + "'s egg");
					} else {
						event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " was killed by " + attackPlayer.getDisplayName() + ChatColor.RED + "'s projectile");
					}
				} else if (pro instanceof Arrow) {
					if ((pro.getShooter() instanceof Skeleton)) {
						event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their bones smashed by a Skeleton's arrow");
					} else {
						event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their bones smashed by an arrow trap");
					}
				} else if (pro instanceof Snowball) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their blood frozen by a Snowman's snowball");
				} else if (pro instanceof Fireball) {
					if (pro.getShooter() instanceof Ghast) {
						event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their insides boiled by a Ghast's fireball");
					} else if ((pro.getShooter() instanceof Blaze)) {
						event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their insides boiled by a Blazes's fireball");
					} else if ((pro.getShooter() instanceof Wither)) {
						event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their insides boiled by a Wither's fireball");
					} else {
						event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their insides boiled by a fireball");
					}
				}
			} else if (damageCause == DamageCause.ENTITY_EXPLOSION) {
				if (damager instanceof Creeper) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their corpse blown apart by a Creeper");
				} else if (damager instanceof TNTPrimed) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " was blown up by TNT");
				}
			} else if (damageCause == DamageCause.MAGIC) {
				if (damager instanceof Player) {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their corpse turned into mush by " + ((Player)damager).getDisplayName());
				} else {
					event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their corpse turned into mush");
				}
			} else {
				event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " died");
			}
		} else {
			switch (damageCause) {
			case DROWNING: event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " drowned to death"); break;
			case STARVATION: event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " starved to death"); break;
			case CONTACT: event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " was playing with a cactus when it pricked back"); break;
			case FIRE: event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their corpse burnt to ashes by fire"); break;
			case FIRE_TICK: event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their corpse burnt to ashes by fire"); break;
			case LAVA: event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their corpse burnt to ashes by lava"); break;
			case LIGHTNING: event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " had their corpse burnt to ashes by lightning"); break;
			case POISON: event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " died from poisoning"); break;
			case SUFFOCATION: event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " suffocated to death"); break;
			case VOID: event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " was crushed by the never ending black hole"); break;
			case FALL: event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " fell to their death"); break;
			case SUICIDE: event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " committed suicide"); break;
			case MAGIC: event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " was destroyed at the hands of a sourcerer"); break;
			case WITHER: event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " was crushed by a wither"); break;
			default: event.setDeathMessage(player.getDisplayName() + ChatColor.RED + " died");
			}
		}
	}

	/**
	 * Called when a player respawns
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (EvilBook.isInSurvival(event.getPlayer())) {
			if (EvilBook.isInSurvival(event.getPlayer())) event.setRespawnLocation(Bukkit.getServer().getWorld("SurvivalLand").getSpawnLocation());
		}
		// Regions
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

	/**
	 * Called when a player leaves the server
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				PlayerProfile profile = EvilBook.getProfile(player);
				if (profile != null) {
					profile.saveProfile();
					EvilBook.playerProfiles.remove(player.getName().toLowerCase());
				}
			}
		});
		// Handle leaving minigame world
		if (EvilBook.isInMinigame(event.getPlayer(), MinigameType.SKYBLOCK)) {
			// Unload world
			if (event.getPlayer().getWorld().getPlayers().size() >= 1) {
				plugin.getServer().unloadWorld(event.getPlayer().getWorld(), true);
			}
			// Save skyblock inventory
			YamlConfiguration config = new YamlConfiguration();
			for (int i = 0; i < player.getInventory().getSize(); i++) {
				ItemStack item = player.getInventory().getItem(i);
				if (item != null) config.set(Integer.toString(i), item);
			}
			config.set("head", player.getInventory().getHelmet());
			config.set("chest", player.getInventory().getChestplate());
			config.set("legs", player.getInventory().getLeggings());
			config.set("boots", player.getInventory().getBoots());
			config.set("health", player.getHealth());
			config.set("hunger", player.getFoodLevel());
			config.set("level", player.getLevel());
			config.set("xp", player.getExp());
			SQL.setProperty(TableType.PlayerProfile, player.getName(), "inventory_skyblock", config.saveToString().replaceAll("'", "''"));
		}
		//
		event.setQuitMessage(ChatColor.GRAY + event.getPlayer().getName() + " has left the game");
	}

	/**
	 * Called when a player is kicked from the server
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerKick(PlayerKickEvent event) {
		event.setLeaveMessage(ChatColor.GRAY + event.getPlayer().getName() + " has left the game");
	}

	/**
	 * Called when a player sends a chat message
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		PlayerProfile profile = EvilBook.getProfile(player);
		profile.lastActionTime = System.currentTimeMillis();
		if (profile.isAway) {
			profile.isAway = false;
			EvilBook.getProfile(event.getPlayer()).updatePlayerListName();
		}
		event.setCancelled(true);
		String message = event.getMessage();
		EvilBook.broadcastPlayerMessage(player.getName(), profile.rank.getPrefix(profile) + " §" + profile.rank.getColor(profile) 
				+ "<§f" + player.getDisplayName() + "§" + profile.rank.getColor(profile) 
				+ "> §f" + EvilBook.toFormattedString(message));
		// Statistics
		GlobalStatistics.incrementStatistic(GlobalStatistic.MessagesSent, 1);
	}

	/**
	 * Called when a player edits a book
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerEditBook(PlayerEditBookEvent event) {
		BookMeta book = event.getNewBookMeta();
		for (int page = 1; page <= book.getPageCount(); page++) book.setPage(page, EvilBook.toFormattedString(book.getPage(page)));
		event.setNewBookMeta(book);
	}

	/**
	 * Called when a player interacts with an entity
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		if (entity.getType() != EntityType.MINECART_CHEST && entity.getType() != EntityType.MINECART_FURNACE && 
				entity.getType() != EntityType.MINECART_HOPPER && entity.getType() != EntityType.MINECART_MOB_SPAWNER && 
				entity.getType() != EntityType.MINECART_TNT && entity.getType() != EntityType.ITEM_FRAME && 
				entity.getType() != EntityType.FALLING_BLOCK && entity.getType() != EntityType.PRIMED_TNT &&
				entity.getType() != EntityType.PLAYER && entity.getType() != EntityType.PAINTING && 
				entity.getType() != EntityType.ARMOR_STAND && entity.getType() != EntityType.MINECART_COMMAND &&
				entity.getType() != EntityType.VILLAGER && (entity instanceof Monster == false || !EvilBook.isInSurvival(entity)) 
				&& entity instanceof Tameable == false && entity.getPassenger() == null && entity != event.getPlayer().getPassenger() &&
				(entity.getType() != EntityType.SHEEP || event.getPlayer().getItemInHand().getType() != Material.INK_SACK)) {
			entity.setPassenger(event.getPlayer());
		}
	}

	/**
	 * Called when a player interacts with an object or air
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		//
		// Drugcraft
		//
		if (event.hasItem()) {
			if (player.getItemInHand().getItemMeta().getLore() != null && player.getItemInHand().getItemMeta().getLore().size() == 1) {
				if (player.getItemInHand().getItemMeta().getLore().get(0).equals("Ruff stuff")) { //Cocain
					player.setItemInHand(null);
					EvilBook.broadcastPlayerMessage(player.getName(), player.getDisplayName() + ChatColor.GRAY + " sniffed cocain");
					for (int blocks = 0; blocks != 1200; blocks++) {
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("EvilBook"), new Runnable() {
							@Override
							public void run() {
								int x = player.getLocation().getBlockX() + (10 - new Random().nextInt(20));
								int z = player.getLocation().getBlockZ() + (10 - new Random().nextInt(20));
								player.sendBlockChange(new Location(player.getWorld(), x, player.getWorld().getHighestBlockYAt(x, z) - 1, z), Material.WOOL, (byte)new Random().nextInt(15));
							}
						}, blocks);
					}
				} else if (player.getItemInHand().getItemMeta().getLore().get(0).equals("Shroooms!")) { //Shrooms
					player.setItemInHand(null);
					EvilBook.broadcastPlayerMessage(player.getName(), player.getDisplayName() + ChatColor.GRAY + " is tripping on shrooms");
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1200, 1));
				} else if (player.getItemInHand().getItemMeta().getLore().get(0).equals("Hick!")) { //Alcohol
					player.setItemInHand(null);
					if (!EvilBook.getProfile(player).isDrunk) {
						EvilBook.broadcastPlayerMessage(player.getName(), player.getDisplayName() + ChatColor.GRAY + " is drunk");
						EvilBook.getProfile(player).isDrunk = true;
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("EvilBook"), new Runnable() {
							@Override
							public void run() {
								EvilBook.getProfile(player).isDrunk = false;
							}
						}, 1200L);
					}
				}
			}
		}
		//
		//
		//
		if (!EvilBook.getProfile(player).isCanEditWorld(player.getWorld())) {
			player.sendMessage(ChatColor.RED + "You need to rank up to edit this world");
			event.setCancelled(true);
			return;
		}
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			//
			// Ammeter
			//
			if (block.getType() == Material.REDSTONE_WIRE) {
				if (!event.hasItem() || event.getItem().getType() == Material.REDSTONE || event.getItem().getType() == Material.COAL) {
					Byte data = block.getData();
			        StringBuilder line = new StringBuilder(25);
			        line.append(ChatColor.YELLOW).append("[");
			        if (data > 10)
			            line.append(ChatColor.DARK_GREEN);
			        else if (data > 5)
			            line.append(ChatColor.GOLD);
			        else if (data > 0)
			            line.append(ChatColor.DARK_RED);
			        for (int i = 0; i < data; i++)
			            line.append("|");
			        line.append(ChatColor.BLACK);
			        for (int i = data; i < 15; i++)
			            line.append("|");
			        line.append(ChatColor.YELLOW).append("] ");
			        player.sendMessage(ChatColor.YELLOW + "Ammeter " + line + ChatColor.WHITE + data + " A");
				}
			}
			//
			// Command block ownership logging and protection
			//
			if (block.getType() == Material.COMMAND) {
				if (SQL.getPropertyFromCriteria(TableType.CommandBlock, "world='" + block.getWorld().getName() + "' AND x='" + block.getX() + "' AND y='" + block.getY() + "' AND z='" + block.getZ() + "'", "player") == null) {
					SQL.insert(TableType.CommandBlock, "'" + player.getUniqueId().toString() + "','" + block.getWorld().getName() + "'," + block.getX() + "," + block.getY() + "," + block.getZ());
				} else if (!SQL.getPropertyFromCriteria(TableType.CommandBlock, "world='" + block.getWorld().getName() + "' AND x='" + block.getX() + "' AND y='" + block.getY() + "' AND z='" + block.getZ() + "'"
						, "player").equals(player.getUniqueId().toString()) &&
						!EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
					player.sendMessage(ChatColor.GRAY + "You don't have permission to edit this command block");
					player.closeInventory();
					event.setCancelled(true);
					return;
				}
			}
			//
			// Mob spawner creature selection menu
			//
			if (block.getType() == Material.MOB_SPAWNER && (!event.hasItem() || event.getItem().getType() != Material.GOLD_SPADE)) {
				EvilBook.getProfile(player).lastBlockInteraction = block.getLocation();
				player.openInventory(EventListenerInventory.entitySpawnerMenu);
			}
			//
			// Sign features
			//
			if (block.getState() instanceof Sign) {
				if ((!event.hasItem() || ((event.getItem().getType() != Material.GOLD_SPADE || !player.isOp()) && event.getItem().getType() != Material.INK_SACK && (event.getItem().getType() != Material.WOOD_PICKAXE || !player.isOp())))) {
					Sign sign = (Sign)block.getState();
					String[] signText = sign.getLines();
					//
					// Sign warps
					//
					for (int i = 0; i < 3; i++) {
						if (EvilBook.toStrippedString(signText[i]).equalsIgnoreCase("[warp]")) {
							if (SQL.isKeyExistant(TableType.Warps, EvilBook.toStrippedString(signText[i + 1]).toLowerCase(Locale.UK).replaceAll("'", "''"))) {
								player.teleport(SQL.getWarp(EvilBook.toStrippedString(signText[i + 1]).toLowerCase(Locale.UK).replaceAll("'", "''")));
								player.sendMessage("§7You have been warped to §d" + EvilBook.toStrippedString(signText[i + 1]));
							} else {
								player.sendMessage("§7A warp with that name doesn't exist");
							}
							return;
						}
					}
					//
					// Sign editing
					//
					if (!EvilBook.isInProtectedRegion(block.getLocation(), player)) {
						try {
							for (Iterator<DynamicSign> iterator = EvilBook.dynamicSignList.iterator(); iterator.hasNext();) {
								DynamicSign dynamicSign = iterator.next();
								if (dynamicSign.location.getBlockX() == sign.getLocation().getBlockX() && 
										dynamicSign.location.getBlockY() == sign.getLocation().getBlockY() &&
										dynamicSign.location.getBlockZ() == sign.getLocation().getBlockZ()) {
									dynamicSign.delete();
									EvilBook.dynamicSignList.remove(dynamicSign);
									signText[0] = dynamicSign.textLines[0];
									signText[1] = dynamicSign.textLines[1];
									signText[2] = dynamicSign.textLines[2];
									signText[3] = dynamicSign.textLines[3];
									break;
								}
							}
							signText[0] = signText[0].replaceAll("§", "&");
							signText[1] = signText[1].replaceAll("§", "&");
							signText[2] = signText[2].replaceAll("§", "&");
							signText[3] = signText[3].replaceAll("§", "&");
							PacketContainer signUpdatePacket = new PacketContainer(PacketType.Play.Server.UPDATE_SIGN);
							signUpdatePacket.getBlockPositionModifier().write(0, new BlockPosition(block.getX(), block.getY(), block.getZ()));
							WrappedChatComponent[] signTextArray = {WrappedChatComponent.fromText(signText[0]), WrappedChatComponent.fromText(signText[1]),
									WrappedChatComponent.fromText(signText[2]), WrappedChatComponent.fromText(signText[3])};
							signUpdatePacket.getChatComponentArrays().write(0, signTextArray);
							ProtocolLibrary.getProtocolManager().sendServerPacket(player, signUpdatePacket);
							PacketContainer signEditPacket = new PacketContainer(PacketType.Play.Server.OPEN_SIGN_ENTITY);
							signEditPacket.getBlockPositionModifier().write(0, new BlockPosition(block.getX(), block.getY(), block.getZ()));
							ProtocolLibrary.getProtocolManager().sendServerPacket(player, signEditPacket);
						} catch (Exception exception) {
							EvilBook.logSevere("Failed to run sign editing module");
						}
					} else {
						player.sendMessage("§cYou don't have permission to edit this sign");
						event.setCancelled(true);
					}
				}
			}
			//
			// Bonemeal
			//
			if (!EvilBook.getProfile(player.getName()).rank.isHigher(Rank.BUILDER) && player.getItemInHand().getType() == Material.INK_SACK && ((Dye)player.getItemInHand().getData()).getColor() == DyeColor.WHITE && (block.getType() == Material.RED_MUSHROOM || block.getType() == Material.BROWN_MUSHROOM || block.getType() == Material.SAPLING || block.getType() == Material.GRASS)) {
				player.sendMessage("§dBone meal requires §5Creator §drank or higher");
				event.setCancelled(true);
				//
				// Command blocks
				//
			} else if ((block.getType() == Material.COMMAND || block.getType() == Material.COMMAND_MINECART) && !EvilBook.getProfile(player.getName()).rank.isAdmin()) {
				player.sendMessage("§dCommand Blocks are an §5Admin §donly feature");
				player.sendMessage("§dPlease type §6/admin §dto learn how to become admin");
				event.setCancelled(true);
				//
				// Survival container protection and ender chest blocking
				//
			} else if (EvilBook.isInSurvival(player) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
				if (block.getType() == Material.ENDER_CHEST) {
					player.sendMessage("§7Ender chests are blocked in survival");
					event.setCancelled(true);
				} else if (block.getState() instanceof InventoryHolder && EvilBook.isContainerProtected(event.getClickedBlock().getLocation(), player)) {
					player.sendMessage(ChatColor.GRAY + "You don't have permission to use this " + EvilBook.getFriendlyName(block.getType()).toLowerCase());
					event.setCancelled(true);
				}
			} else if (event.hasItem()) {
				if (EvilBook.getProfile(player).rank.isHigher(Rank.STAFF_LAPIS) && event.getItem().getType() == Material.GOLD_SPADE && (EvilBook.isInSurvival(player) == false || EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) && ((PlayerProfileAdmin)EvilBook.getProfile(player)).wandMode != EditWandMode.None) {
					if (EvilBook.getProfile(player).wandMode == EditWandMode.Selection) {
						EvilBook.getProfile(player).actionLocationB = block.getLocation();
						player.sendMessage("§7Second point selected (" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ")");
					} else if (EvilBook.getProfile(player).wandMode == EditWandMode.Tree) { 
						player.getWorld().generateTree(block.getRelative(BlockFace.UP).getLocation(), TreeType.TREE);
					}
					event.setCancelled(true);
				} else {
					// Regions
					if (EvilBook.isInProtectedRegion(block.getLocation(), player) == true) {
						if (event.getItem().getType() == Material.INK_SACK) {
							player.sendMessage("§cYou don't have permission to dye blocks here");
						} else {
							player.sendMessage("§cYou don't have permission to use this here");
						}
						event.setCancelled(true);
					} else if (event.getItem().getType() == Material.INK_SACK) {
						// Dying
						if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
							ChatColor dyeTextColor;
							switch (((Dye)player.getItemInHand().getData()).getColor()) {
							case RED: dyeTextColor = ChatColor.RED; break;
							case GREEN: dyeTextColor = ChatColor.DARK_GREEN; break;
							case BLUE: dyeTextColor = ChatColor.BLUE; break;
							case PURPLE: dyeTextColor = ChatColor.DARK_PURPLE; break;
							case CYAN: dyeTextColor = ChatColor.DARK_AQUA; break;
							case SILVER: dyeTextColor = ChatColor.GRAY; break;
							case GRAY: dyeTextColor = ChatColor.DARK_GRAY; break;
							case MAGENTA: dyeTextColor = ChatColor.LIGHT_PURPLE; break;
							case LIME: dyeTextColor = ChatColor.GREEN; break;
							case YELLOW: dyeTextColor = ChatColor.YELLOW; break;
							case LIGHT_BLUE: dyeTextColor = ChatColor.AQUA; break;
							case PINK: dyeTextColor = ChatColor.LIGHT_PURPLE; break;
							case ORANGE: dyeTextColor = ChatColor.GOLD; break;
							case WHITE: dyeTextColor = ChatColor.WHITE; break;
							default: dyeTextColor = ChatColor.BLACK;
							}
							for (DynamicSign dynamicSign : EvilBook.dynamicSignList) {
								if (dynamicSign.location.equals(block.getLocation())) {
									if (dynamicSign.textLines[0].length() != 0) dynamicSign.textLines[0] = dyeTextColor + (dynamicSign.textLines[0].startsWith("§") && !dynamicSign.textLines[0].startsWith("§l") && !dynamicSign.textLines[0].startsWith("§k") && !dynamicSign.textLines[0].startsWith("§n") && !dynamicSign.textLines[0].startsWith("§m") && !dynamicSign.textLines[0].startsWith("§o") && !dynamicSign.textLines[0].startsWith("§r") ? dynamicSign.textLines[0].substring(2, dynamicSign.textLines[0].length()) : dynamicSign.textLines[0]);
									if (dynamicSign.textLines[1].length() != 0) dynamicSign.textLines[1] = dyeTextColor + (dynamicSign.textLines[1].startsWith("§") && !dynamicSign.textLines[1].startsWith("§l") && !dynamicSign.textLines[1].startsWith("§k") && !dynamicSign.textLines[1].startsWith("§n") && !dynamicSign.textLines[1].startsWith("§m") && !dynamicSign.textLines[1].startsWith("§o") && !dynamicSign.textLines[1].startsWith("§r") ? dynamicSign.textLines[1].substring(2, dynamicSign.textLines[1].length()) : dynamicSign.textLines[1]);
									if (dynamicSign.textLines[2].length() != 0) dynamicSign.textLines[2] = dyeTextColor + (dynamicSign.textLines[2].startsWith("§") && !dynamicSign.textLines[2].startsWith("§l") && !dynamicSign.textLines[2].startsWith("§k") && !dynamicSign.textLines[2].startsWith("§n") && !dynamicSign.textLines[2].startsWith("§m") && !dynamicSign.textLines[2].startsWith("§o") && !dynamicSign.textLines[2].startsWith("§r") ? dynamicSign.textLines[2].substring(2, dynamicSign.textLines[2].length()) : dynamicSign.textLines[2]);
									if (dynamicSign.textLines[3].length() != 0) dynamicSign.textLines[3] = dyeTextColor + (dynamicSign.textLines[3].startsWith("§") && !dynamicSign.textLines[3].startsWith("§l") && !dynamicSign.textLines[3].startsWith("§k") && !dynamicSign.textLines[3].startsWith("§n") && !dynamicSign.textLines[3].startsWith("§m") && !dynamicSign.textLines[3].startsWith("§o") && !dynamicSign.textLines[3].startsWith("§r") ? dynamicSign.textLines[3].substring(2, dynamicSign.textLines[3].length()) : dynamicSign.textLines[3]);
									//dynamicSign.save();
									return;
								}
							}
							Sign s = (Sign) event.getClickedBlock().getState();
							if (s.getLine(0).length() != 0) s.setLine(0, dyeTextColor + (s.getLine(0).startsWith("§") && !s.getLine(0).startsWith("§l") && !s.getLine(0).startsWith("§k") && !s.getLine(0).startsWith("§n") && !s.getLine(0).startsWith("§m") && !s.getLine(0).startsWith("§o") && !s.getLine(0).startsWith("§r") ? s.getLine(0).substring(2, s.getLine(0).length()) : s.getLine(0)));
							if (s.getLine(1).length() != 0) s.setLine(1, dyeTextColor + (s.getLine(1).startsWith("§") && !s.getLine(1).startsWith("§l") && !s.getLine(1).startsWith("§k") && !s.getLine(1).startsWith("§n") && !s.getLine(1).startsWith("§m") && !s.getLine(1).startsWith("§o") && !s.getLine(1).startsWith("§r") ? s.getLine(1).substring(2, s.getLine(1).length()) : s.getLine(1)));
							if (s.getLine(2).length() != 0) s.setLine(2, dyeTextColor + (s.getLine(2).startsWith("§") && !s.getLine(2).startsWith("§l") && !s.getLine(2).startsWith("§k") && !s.getLine(2).startsWith("§n") && !s.getLine(2).startsWith("§m") && !s.getLine(2).startsWith("§o") && !s.getLine(2).startsWith("§r") ? s.getLine(2).substring(2, s.getLine(2).length()) : s.getLine(2)));
							if (s.getLine(3).length() != 0) s.setLine(3, dyeTextColor + (s.getLine(3).startsWith("§") && !s.getLine(3).startsWith("§l") && !s.getLine(3).startsWith("§k") && !s.getLine(3).startsWith("§n") && !s.getLine(3).startsWith("§m") && !s.getLine(3).startsWith("§o") && !s.getLine(3).startsWith("§r") ? s.getLine(3).substring(2, s.getLine(3).length()) : s.getLine(3)));
							s.update();
						} else if (block.getType() == Material.WOOL || block.getType() == Material.STAINED_CLAY || block.getType() == Material.STAINED_GLASS || block.getType() == Material.STAINED_GLASS_PANE ||
								block.getType() == Material.HARD_CLAY || block.getType() == Material.GLASS || block.getType() == Material.THIN_GLASS || block.getType() == Material.CARPET) {
							if (block.getType() == Material.HARD_CLAY) block.setType(Material.STAINED_CLAY);
							else if (block.getType() == Material.GLASS) block.setType(Material.STAINED_GLASS);
							else if (block.getType() == Material.THIN_GLASS) block.setType(Material.STAINED_GLASS_PANE);
							Byte dyeColor = 15;
							switch (player.getItemInHand().getData().getData()) {
							case 0: dyeColor = 15; break;
							case 1: dyeColor = 14; break;
							case 2: dyeColor = 13; break;
							case 3: dyeColor = 12; break;
							case 4: dyeColor = 11; break;
							case 5: dyeColor = 10; break;
							case 6: dyeColor = 9; break;
							case 7: dyeColor = 8; break;
							case 8: dyeColor = 7; break;
							case 9: dyeColor = 6; break;
							case 10: dyeColor = 5; break;
							case 11: dyeColor = 4; break;
							case 12: dyeColor = 3; break;
							case 13: dyeColor = 2; break;
							case 14: dyeColor = 1; break;
							case 15: dyeColor = 0; break;
							default:
								break;
							}
							block.setData(dyeColor);
						}
					}
				}
			}
		}
	}

	/**
	 * Called when a player sends a command
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (EvilBook.getProfile(player).rank == Rank.SERVER_HOST) return;
		if (event.getMessage().toLowerCase().startsWith("/evilbook:") || event.getMessage().toLowerCase().startsWith("/bukkit:") || event.getMessage().toLowerCase().startsWith("/minecraft:")) {
			event.setCancelled(true); 
		} else if (System.currentTimeMillis() - EvilBook.getProfile(player).lastMessageTime <= 250 && event.getMessage().equals(EvilBook.getProfile(player).lastMessage)) {
			player.kickPlayer("§cSpam is not tollerated");
			event.setCancelled(true);
		} else {
			EvilBook.getProfile(player).lastMessage = event.getMessage();
			EvilBook.getProfile(player).lastMessageTime = System.currentTimeMillis();
			if (EvilBook.commandBlacklist.containsKey(event.getMessage().toLowerCase().split(" ")[0]) == false) return;
			if (!EvilBook.getProfile(player).rank.isHigher(EvilBook.commandBlacklist.get(event.getMessage().toLowerCase().split(" ")[0]).getPreviousRank())) {
				if (EvilBook.getProfile(player).rank.isAdmin() == false && EvilBook.commandBlacklist.get(event.getMessage().toLowerCase().split(" ")[0]).equals(Rank.ADMIN)) {
					player.sendMessage("§dThis is an §5Admin §donly command");
					player.sendMessage("§dPlease type §6/admin §dto learn how to become admin");
				} else if (EvilBook.commandBlacklist.get(event.getMessage().toLowerCase().split(" ")[0]).equals(Rank.SERVER_HOST)) {
					player.sendMessage("§7This command is blocked for security reasons");
				} else {
					player.sendMessage("§dThis is an §" + EvilBook.commandBlacklist.get(event.getMessage().toLowerCase().split(" ")[0]).getColor() + EvilBook.commandBlacklist.get(event.getMessage().toLowerCase().split(" ")[0]).getName() + " §drank and higher only command");
					if (EvilBook.commandBlacklist.get(event.getMessage().toLowerCase().split(" ")[0]).isAdmin()) {
						player.sendMessage("§dPlease type §6/donate §dto learn how to become §" + EvilBook.commandBlacklist.get(event.getMessage().toLowerCase().split(" ")[0]).getColor() + EvilBook.commandBlacklist.get(event.getMessage().toLowerCase().split(" ")[0]).getName() + " §drank");
					} else {
						player.sendMessage("§dPlease type §6/ranks §dto learn how to become §" + EvilBook.commandBlacklist.get(event.getMessage().toLowerCase().split(" ")[0]).getColor() + EvilBook.commandBlacklist.get(event.getMessage().toLowerCase().split(" ")[0]).getName() + " §drank");
					}
				}
				event.setCancelled(true);
			}
		}
	}

	/**
	 * Called when a player teleports
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		EvilBook.getProfile(player).lastLocation = event.getFrom();
		for (String world : EvilBook.paidWorldList) {
			if (event.getTo().getWorld().getName().toLowerCase().endsWith(world.toLowerCase())) {
				if (!EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON) && !EvilBook.getPrivateWorldProperty(event.getTo().getWorld().getName().split("plugins/EvilBook/Private worlds/")[1], "AllowedPlayers").contains(player.getName().toLowerCase())) {
					player.sendMessage("§7You don't have access to this private world");
					event.setCancelled(true);
					return;
				}
			}
		}
		if (event.getTo().getBlockX() > 12550820 || event.getTo().getBlockX() < -12550820 || event.getTo().getBlockZ() > 12550820 || event.getTo().getBlockZ() < -12550820) {
			player.sendMessage("§7The Far Lands are blocked");
			event.setCancelled(true);
		} else {
			if (event.getTo().getWorld() != event.getFrom().getWorld() && !event.getFrom().getWorld().getName().equals("SurvivalLandTheEnd") && !event.getFrom().getWorld().getName().equals("SurvivalLandNether") && !event.getFrom().getWorld().getName().equals("Amentrix_nether") && !event.getFrom().getWorld().getName().equals("Amentrix_the_end")) EvilBook.getProfile(player.getName()).setWorldLastPosition(event.getFrom());
			// Teleport particle effect
			if (!EvilBook.getProfile(player).isInvisible && ((event.getTo().getWorld() == event.getFrom().getWorld() && event.getFrom().distance(event.getTo()) > 2)) || event.getTo().getWorld() != event.getFrom().getWorld()) {
				event.getFrom().getWorld().playEffect(event.getFrom(), Effect.SMOKE, 0);
				event.getTo().getWorld().playEffect(event.getTo(), Effect.ENDER_SIGNAL, 0);
			}
			// Regions
			for (Region region : EvilBook.regionList) {
				if (region.getLeaveMessage() != null && EvilBook.isInRegion(region, event.getFrom()) && EvilBook.isInRegion(region, event.getTo()) == false) {
					event.getPlayer().sendMessage(region.getLeaveMessage());
				} else if (EvilBook.isInRegion(region, event.getTo())) {
					if (region.getWelcomeMessage() != null && EvilBook.isInRegion(region, event.getFrom()) == false) event.getPlayer().sendMessage(region.getWelcomeMessage());
					if (SQL.getWarp(region.getWarp()) != null) {
						event.getPlayer().teleport(SQL.getWarp(region.getWarp()));
						break;
					}
				}
			}
		}
	}

	/**
	 * Called when a player changes world
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		World from = event.getFrom();
		World to = player.getWorld();
		//
		// Handle inventory saving
		//
		if (EvilBook.isInSurvival(from) && !EvilBook.isInSurvival(to)) {
			// Save survival inventory
			YamlConfiguration config = new YamlConfiguration();
			for (int i = 0; i < player.getInventory().getSize(); i++) {
				ItemStack item = player.getInventory().getItem(i);
				if (item != null) config.set(Integer.toString(i), item);
			}
			config.set("head", player.getInventory().getHelmet());
			config.set("chest", player.getInventory().getChestplate());
			config.set("legs", player.getInventory().getLeggings());
			config.set("boots", player.getInventory().getBoots());
			config.set("health", player.getHealth());
			config.set("hunger", player.getFoodLevel());
			config.set("level", player.getLevel());
			config.set("xp", player.getExp());
			SQL.setProperty(TableType.PlayerProfile, player.getName(), "inventory_survival", config.saveToString().replaceAll("'", "''"));
		} else if (EvilBook.isInMinigame(from, MinigameType.SKYBLOCK) && !EvilBook.isInMinigame(to, MinigameType.SKYBLOCK)) {
			// Save skyblock inventory
			YamlConfiguration config = new YamlConfiguration();
			for (int i = 0; i < player.getInventory().getSize(); i++) {
				ItemStack item = player.getInventory().getItem(i);
				if (item != null) config.set(Integer.toString(i), item);
			}
			config.set("head", player.getInventory().getHelmet());
			config.set("chest", player.getInventory().getChestplate());
			config.set("legs", player.getInventory().getLeggings());
			config.set("boots", player.getInventory().getBoots());
			config.set("health", player.getHealth());
			config.set("hunger", player.getFoodLevel());
			config.set("level", player.getLevel());
			config.set("xp", player.getExp());
			SQL.setProperty(TableType.PlayerProfile, player.getName(), "inventory_skyblock", config.saveToString().replaceAll("'", "''"));
			// Unload the minigame world if empty
			if (from.getPlayers().size() == 0) {
				plugin.getServer().unloadWorld(from, true);
			}
		} else {
			// Save creative inventory
			YamlConfiguration config = new YamlConfiguration();
			for (int i = 0; i < player.getInventory().getSize(); i++) {
				ItemStack item = player.getInventory().getItem(i);
				if (item != null) config.set(Integer.toString(i), item);
			}
			config.set("head", player.getInventory().getHelmet());
			config.set("chest", player.getInventory().getChestplate());
			config.set("legs", player.getInventory().getLeggings());
			config.set("boots", player.getInventory().getBoots());
			config.set("health", player.getHealth());
			config.set("hunger", player.getFoodLevel());
			config.set("level", player.getLevel());
			config.set("xp", player.getExp());
			SQL.setProperty(TableType.PlayerProfile, player.getName(), "inventory_creative", config.saveToString().replaceAll("'", "''"));
		}
		//
		// Handle inventory loading
		//
		if (!EvilBook.isInSurvival(from) && EvilBook.isInSurvival(to)) {
			// Load survival inventory
			player.setGameMode(GameMode.SURVIVAL);
			for (PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
			if (EvilBook.getProfile(player).isInvisible && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
				for (Player other : Bukkit.getServer().getOnlinePlayers()) other.showPlayer(player);
				EvilBook.getProfile(player).isInvisible = false;
				player.sendMessage("§7Vanish isn't allowed in survival, you are now visible");
			}
			player.getInventory().clear();
			String inventory = SQL.getProperty(TableType.PlayerProfile, player.getName(), "inventory_survival");
			if (inventory == null) {
				player.getInventory().setHelmet(new ItemStack(Material.AIR));
				player.getInventory().setChestplate(new ItemStack(Material.AIR));
				player.getInventory().setLeggings(new ItemStack(Material.AIR));
				player.getInventory().setBoots(new ItemStack(Material.AIR));
			} else {
				YamlConfiguration config = new YamlConfiguration();
				try {
					config.loadFromString(inventory);
				} catch (Exception e) {
					e.printStackTrace();
				}	
				for (int i = 0; i < player.getInventory().getSize(); i++) {
					if (config.get(Integer.toString(i)) != null) 
						player.getInventory().setItem(i, (ItemStack)config.get(Integer.toString(i)));
				}
				player.getInventory().setHelmet((ItemStack)config.get("head"));
				player.getInventory().setChestplate((ItemStack)config.get("chest"));
				player.getInventory().setLeggings((ItemStack)config.get("legs"));
				player.getInventory().setBoots((ItemStack)config.get("boots"));
				player.setHealth((double)config.get("health"));
				player.setFoodLevel((int)config.get("hunger"));
				player.setLevel((int)config.get("level"));
				player.setExp((float)config.getDouble("xp"));
			}
		} else if (!EvilBook.isInMinigame(from, MinigameType.SKYBLOCK) && EvilBook.isInMinigame(to, MinigameType.SKYBLOCK)) {
			// Load skyblock inventory
			player.setGameMode(GameMode.SURVIVAL);
			player.sendMessage("§bWelcome to Skyblock Survival");
			player.sendMessage("§7Reset the map using /reset");
			EvilBook.getProfile(player).addAchievement(Achievement.GLOBAL_WORLD_SKYBLOCK);
			player.getInventory().clear();
			String inventory = SQL.getProperty(TableType.PlayerProfile, player.getName(), "inventory_skyblock");
			if (inventory == null) {
				player.getInventory().setHelmet(new ItemStack(Material.AIR));
				player.getInventory().setChestplate(new ItemStack(Material.AIR));
				player.getInventory().setLeggings(new ItemStack(Material.AIR));
				player.getInventory().setBoots(new ItemStack(Material.AIR));
			} else {
				YamlConfiguration config = new YamlConfiguration();
				try {
					config.loadFromString(inventory);
				} catch (Exception e) {
					e.printStackTrace();
				}	
				for (int i = 0; i < player.getInventory().getSize(); i++) {
					if (config.get(Integer.toString(i)) != null) 
						player.getInventory().setItem(i, (ItemStack)config.get(Integer.toString(i)));
				}
				player.getInventory().setHelmet((ItemStack)config.get("head"));
				player.getInventory().setChestplate((ItemStack)config.get("chest"));
				player.getInventory().setLeggings((ItemStack)config.get("legs"));
				player.getInventory().setBoots((ItemStack)config.get("boots"));
				player.setHealth((double)config.get("health"));
				player.setFoodLevel((int)config.get("hunger"));
				player.setLevel((int)config.get("level"));
				player.setExp((float)config.getDouble("xp"));
			}
		} else {
			// Load creative inventory
			player.setGameMode(GameMode.CREATIVE);
			player.getInventory().clear();
			String inventory = SQL.getProperty(TableType.PlayerProfile, player.getName(), "inventory_creative");
			if (inventory == null) {
				player.getInventory().setHelmet(new ItemStack(Material.AIR));
				player.getInventory().setChestplate(new ItemStack(Material.AIR));
				player.getInventory().setLeggings(new ItemStack(Material.AIR));
				player.getInventory().setBoots(new ItemStack(Material.AIR));
			} else {
				YamlConfiguration config = new YamlConfiguration();
				try {
					config.loadFromString(inventory);
				} catch (Exception e) {
					e.printStackTrace();
				}	
				for (int i = 0; i < player.getInventory().getSize(); i++) {
					if (config.get(Integer.toString(i)) != null) 
						player.getInventory().setItem(i, (ItemStack)config.get(Integer.toString(i)));
				}
				player.getInventory().setHelmet((ItemStack)config.get("head"));
				player.getInventory().setChestplate((ItemStack)config.get("chest"));
				player.getInventory().setLeggings((ItemStack)config.get("legs"));
				player.getInventory().setBoots((ItemStack)config.get("boots"));
				player.setHealth((double)config.get("health"));
				player.setFoodLevel((int)config.get("hunger"));
				player.setLevel((int)config.get("level"));
				player.setExp((float)config.getDouble("xp"));
			}
		}
		//
		// Handle world messages and achievements
		//
		if (player.getWorld().getName().equals("FlatLand")) {
			player.sendMessage("§7Welcome to the Flat Lands");
			EvilBook.getProfile(player).addAchievement(Achievement.GLOBAL_WORLD_FLATLAND);
		} else if (player.getWorld().getName().equals("SkyLand")) {
			player.sendMessage("§7Welcome to the Sky Lands");
			EvilBook.getProfile(player).addAchievement(Achievement.GLOBAL_WORLD_SKYLAND);
		} else if (player.getWorld().getName().equals("SurvivalLand")) {
			player.sendMessage("§7Welcome to the Survival Lands");
			EvilBook.getProfile(player).addAchievement(Achievement.GLOBAL_WORLD_SURVIVALLAND);
		} else if (player.getWorld().getName().equals("SurvivalLandNether")) {
			player.sendMessage("§7Welcome to the Survival Nether");
			EvilBook.getProfile(player).addAchievement(Achievement.GLOBAL_WORLD_SURVIVALLANDNETHER);
		} else if (player.getWorld().getName().equals("SurvivalLandTheEnd")) {
			player.sendMessage("§7Welcome to the Survival End");
			EvilBook.getProfile(player).addAchievement(Achievement.GLOBAL_WORLD_SURVIVALLANDTHEEND);
		}
	}

	/**
	 * Called when a player has their game mode changed
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
		if (EvilBook.getProfile(event.getPlayer()).rank.isHigher(Rank.TYCOON)) return;
		if (event.getNewGameMode() != GameMode.SURVIVAL && EvilBook.isInSurvival(event.getPlayer())) event.setCancelled(true);
	}
}
