package com.amentrix.evilbook.listeners;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Spider;
import org.bukkit.entity.TNTPrimed;
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
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.material.Dye;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.amentrix.evilbook.achievement.Achievement;
import com.amentrix.evilbook.eviledit.utils.EditWandMode;
import com.amentrix.evilbook.main.DynamicSign;
import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.PlayerProfile;
import com.amentrix.evilbook.main.PlayerProfileAdmin;
import com.amentrix.evilbook.main.PlayerProfileNormal;
import com.amentrix.evilbook.main.Rank;
import com.amentrix.evilbook.main.Region;
import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;
import com.amentrix.evilbook.statistics.GlobalStatistic;
import com.amentrix.evilbook.statistics.GlobalStatistics;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

/**
 * Player event listener
 * @author Reece Aaron Lecrivain
 */
public class EventListenerPlayer implements Listener {
	/**
	 * Called when a player attempts to login
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (event.getResult().equals(Result.KICK_BANNED)) {
			event.setKickMessage("§cYou are banned! E-mail §6amentrix@hotmail.co.uk §cfor support");
		} else {
			EvilBook.updateWebPlayerStatistics(Bukkit.getServer().getOnlinePlayers().size() + 1);
		}
	}

	/**
	 * Called when a player joins the server after login
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (player != event.getPlayer() && EvilBook.getProfile(player).isInvisible) event.getPlayer().hidePlayer(player);
		}
		// Make sure player statistics entry exists
		if (!SQL.isKeyExistant(TableType.PlayerStatistics, event.getPlayer().getName())) {
			SQL.insert(TableType.PlayerStatistics, "'" + event.getPlayer().getName() + "',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL");
		}
		//
		if (SQL.getProperty(TableType.PlayerProfile, event.getPlayer().getName(), "Rank") != null && Rank.valueOf(SQL.getProperty(TableType.PlayerProfile, event.getPlayer().getName(), "Rank")).isAdmin()) {
			EvilBook.playerProfiles.put(event.getPlayer().getName().toLowerCase(Locale.UK), new PlayerProfileAdmin(event.getPlayer()));
		} else {
			EvilBook.playerProfiles.put(event.getPlayer().getName().toLowerCase(Locale.UK), new PlayerProfileNormal(event.getPlayer()));
		}
		event.setJoinMessage(null);
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
		EvilBook.getProfile(event.getPlayer()).lastActionTime = System.currentTimeMillis();
		if (EvilBook.getProfile(event.getPlayer()).isAway) {
			EvilBook.getProfile(event.getPlayer()).isAway = false;
			EvilBook.getProfile(event.getPlayer()).updatePlayerListName();
		}
		if (event.getTo().getBlockX() > 12550820 || event.getTo().getBlockX() < -12550820 || event.getTo().getBlockZ() > 12550820 || event.getTo().getBlockZ() < -12550820) {
			event.getPlayer().sendMessage("§7The Far Lands are blocked");
			event.getPlayer().teleport(event.getTo().add(event.getTo().getBlockX() > 12550820 ? -2 : event.getTo().getBlockX() < -12550820 ? 2 : 0, 0, event.getTo().getBlockZ() > 12550820 ? -2 : event.getTo().getBlockZ() < -12550820 ? 2 : 0));
			event.setCancelled(true);
		} else {
			if (!EvilBook.isInSurvival(event.getPlayer())) {
				if (EvilBook.getProfile(event.getPlayer()).jumpAmplifier != 0 && !event.getPlayer().isFlying() && event.getFrom().getY() < event.getTo().getY() && event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) event.getPlayer().setVelocity(event.getPlayer().getVelocity().setY(EvilBook.getProfile(event.getPlayer()).jumpAmplifier));
				if (EvilBook.getProfile(event.getPlayer()).runAmplifier != 0 && event.getPlayer().isSprinting()) event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, EvilBook.getProfile(event.getPlayer()).runAmplifier), true);
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
		if (event.getFrom().getWorld().getName().equals("SurvivalLand")) {
			if (event.getCause() == TeleportCause.NETHER_PORTAL) {
				event.getPlayer().teleport(Bukkit.getServer().getWorld("SurvivalLandNether").getSpawnLocation());
			} else if (event.getCause() == TeleportCause.END_PORTAL) {
				event.getPlayer().teleport(Bukkit.getServer().getWorld("SurvivalLandTheEnd").getSpawnLocation());
			}
		} else if (event.getFrom().getWorld().getName().equals("SurvivalLandNether")) {
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
		if (!EvilBook.getProfile(player).rank.isAdmin() && (!EvilBook.isInSurvival(player) || !EvilBook.getProfile(player).rank.isHigher(Rank.ARCHITECT))) {
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
					event.setDeathMessage(player.getDisplayName() + " was murdered by " + attackPlayer.getDisplayName() + " wielding their " + (attackPlayer.getItemInHand().getType() == Material.AIR ? "fists" : attackPlayer.getItemInHand().getType().toString().toLowerCase().replace("_", " ")));
				} else if (damager instanceof Zombie) {
					event.setDeathMessage(player.getDisplayName() + " had their brains eaten by a Zombie");
				} else if (damager instanceof Spider) {
					event.setDeathMessage(player.getDisplayName() + " had their flesh devoured by a Spider");
				} else if (damager instanceof CaveSpider) {
					event.setDeathMessage(player.getDisplayName() + " had their flesh devoured by a Cave-Spider");
				} else if (damager instanceof Enderman) {
					event.setDeathMessage(player.getDisplayName() + " had their soul consumed by an Enderman");
				} else if (damager instanceof Silverfish) {
					event.setDeathMessage(player.getDisplayName() + " had their insides ripped out by a Silverfish");
				} else if (damager instanceof MagmaCube) {
					event.setDeathMessage(player.getDisplayName() + " had their corpse burnt to ashes by a Magma-Cube");
				} else if (damager instanceof Slime) {
					event.setDeathMessage(player.getDisplayName() + " had their corpse dissolved in a Slime");
				} else if (damager instanceof Wolf) {
					event.setDeathMessage(player.getDisplayName() + " had their corpse ripped appart by a Wolf");
				} else if (damager instanceof PigZombie) {
					event.setDeathMessage(player.getDisplayName() + " had their corpse cut in two by a Pigman-Zombie");
				} else if (damager instanceof IronGolem) {
					event.setDeathMessage(player.getDisplayName() + " had their corpse crushed by an Iron-Golem");
				} else if (damager instanceof Giant) {
					event.setDeathMessage(player.getDisplayName() + " had their corpse crushed by a Giant");
				}
			} else if (damageCause == DamageCause.PROJECTILE) {
				Projectile pro = (Projectile)damager;
				if (pro.getShooter() instanceof Player) {
					Player attackPlayer = (Player) pro.getShooter();
					if (pro instanceof Arrow) {
						event.setDeathMessage(player.getDisplayName() + " was killed by " + attackPlayer.getDisplayName() + "'s arrow");
					} else if (pro instanceof Snowball) {
						event.setDeathMessage(player.getDisplayName() + " was killed by " + attackPlayer.getDisplayName() + "'s snowball");
					} else if (pro instanceof Egg) {
						event.setDeathMessage(player.getDisplayName() + " was killed by " + attackPlayer.getDisplayName() + "'s egg");
					} else {
						event.setDeathMessage(player.getDisplayName() + " was killed by " + attackPlayer.getDisplayName() + "'s projectile");
					}
					return;
				}
				if (pro instanceof Arrow)
				{
					if ((pro.getShooter() instanceof Skeleton)) {
						event.setDeathMessage(player.getDisplayName() + " had their bones smashed by a Skeleton's arrow");
					} else {
						event.setDeathMessage(player.getDisplayName() + " had their bones smashed by an arrow trap");
					}
				} else if (pro instanceof Snowball) {
					event.setDeathMessage(player.getDisplayName() + " had their blood frozen by a Snowman's snowball");
				} else if (pro instanceof Fireball) {
					if (pro.getShooter() instanceof Ghast) {
						event.setDeathMessage(player.getDisplayName() + " had their insides boiled by a Ghast's fireball");
					} else if ((pro.getShooter() instanceof Blaze)) {
						event.setDeathMessage(player.getDisplayName() + " had their insides boiled by a Blazes's fireball");
					} else if ((pro.getShooter() instanceof Wither)) {
						event.setDeathMessage(player.getDisplayName() + " had their insides boiled by a Wither's fireball");
					} else {
						event.setDeathMessage(player.getDisplayName() + " had their insides boiled by a fireball");
					}
				}
			} else if (damageCause == DamageCause.ENTITY_EXPLOSION) {
				if (damager instanceof Creeper) {
					event.setDeathMessage(player.getDisplayName() + " was blown up by a Creeper");
				} else if (damager instanceof TNTPrimed) {
					event.setDeathMessage(player.getDisplayName() + " was blown up by TNT");
				}
			}
		} else {
			switch (damageCause) {
			case DROWNING: event.setDeathMessage(player.getDisplayName() + " drowned to death"); break;
			case STARVATION: event.setDeathMessage(player.getDisplayName() + " starved to death"); break;
			case CONTACT: event.setDeathMessage(player.getDisplayName() + " was playing with a cactus when it pricked back"); break;
			case FIRE: event.setDeathMessage(player.getDisplayName() + " had their corpse burnt to ashes by fire"); break;
			case FIRE_TICK: event.setDeathMessage(player.getDisplayName() + " had their corpse burnt to ashes by fire"); break;
			case LAVA: event.setDeathMessage(player.getDisplayName() + " had their corpse burnt to ashes by lava"); break;
			case LIGHTNING: event.setDeathMessage(player.getDisplayName() + " had their corpse burnt to ashes by lightning"); break;
			case POISON: event.setDeathMessage(player.getDisplayName() + " died from poisoning"); break;
			case SUFFOCATION: event.setDeathMessage(player.getDisplayName() + " suffocated to death"); break;
			case VOID: event.setDeathMessage(player.getDisplayName() + " was crushed by the never ending black hole"); break;
			case FALL: event.setDeathMessage(player.getDisplayName() + " fell to their death"); break;
			case SUICIDE: event.setDeathMessage(player.getDisplayName() + " committed suicide"); break;
			case MAGIC: event.setDeathMessage(player.getDisplayName() + " was destroyed at the hands of a sourcerer"); break;
			case WITHER: event.setDeathMessage(player.getDisplayName() + " was crushed by a wither"); break;
			default: event.setDeathMessage(player.getDisplayName() + " died");
			}
		}
	}

	/**
	 * Called when a player respawns
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (EvilBook.isInSurvival(event.getPlayer())) {
			EvilBook.setSurvivalInventory(event.getPlayer());
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
	public void onPlayerQuit(PlayerQuitEvent event) {
		PlayerProfile profile = EvilBook.getProfile(event.getPlayer());
		if (profile != null) {
			profile.saveProfile();
			EvilBook.playerProfiles.remove(event.getPlayer().getName().toLowerCase());
			event.setQuitMessage(ChatColor.GRAY + event.getPlayer().getName() + " has left the game");
			EvilBook.updateWebPlayerStatistics(Bukkit.getServer().getOnlinePlayers().size() - 1);
		}
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
		EvilBook.getProfile(player).lastActionTime = System.currentTimeMillis();
		if (EvilBook.getProfile(player).isAway) {
			EvilBook.getProfile(player).isAway = false;
			EvilBook.getProfile(event.getPlayer()).updatePlayerListName();
		}
		event.setCancelled(true);
		EvilBook.broadcastPlayerMessage(player.getName(), EvilBook.getProfile(player).rank.getPrefix(EvilBook.getProfile(player)) + " §" + EvilBook.getProfile(player).rank.getColor(EvilBook.getProfile(player)) 
				+ "<§f" + player.getDisplayName() + "§" + EvilBook.getProfile(player).rank.getColor(EvilBook.getProfile(player)) 
				+ "> §f" + EvilBook.toFormattedString(event.getMessage()));
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
		if (event.getRightClicked().getType() != EntityType.MINECART_CHEST && event.getRightClicked().getType() != EntityType.MINECART_FURNACE && 
				event.getRightClicked().getType() != EntityType.MINECART_HOPPER && event.getRightClicked().getType() != EntityType.MINECART_MOB_SPAWNER && 
				event.getRightClicked().getType() != EntityType.MINECART_TNT && event.getRightClicked().getType() != EntityType.ITEM_FRAME && 
				event.getRightClicked().getType() != EntityType.FALLING_BLOCK && event.getRightClicked().getType() != EntityType.PRIMED_TNT &&
				event.getRightClicked().getType() != EntityType.PLAYER && event.getRightClicked().getType() != EntityType.WOLF &&
				event.getRightClicked().getType() != EntityType.PAINTING &&
				event.getRightClicked().getPassenger() == null && event.getRightClicked() != event.getPlayer().getPassenger() &&
				(event.getRightClicked().getType() != EntityType.SHEEP || event.getPlayer().getItemInHand().getType() != Material.INK_SACK)) {
			event.getRightClicked().setPassenger(event.getPlayer());
		}
	}

	/**
	 * Called when a player interacts with an object or air
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (!EvilBook.getProfile(player).isCanEditWorld(player.getWorld())) {
			event.setCancelled(true);
			return;
		}
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
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
						signUpdatePacket.getIntegers().write(0, block.getX());
						signUpdatePacket.getIntegers().write(1, block.getY());
						signUpdatePacket.getIntegers().write(2, block.getZ());
						signUpdatePacket.getStringArrays().write(0, signText);
						try {
							ProtocolLibrary.getProtocolManager().sendServerPacket(player, signUpdatePacket);
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
						PacketContainer signEditPacket = new PacketContainer(PacketType.Play.Server.OPEN_SIGN_ENTITY);
						signEditPacket.getIntegers().write(0, block.getX());
						signEditPacket.getIntegers().write(1, block.getY());
						signEditPacket.getIntegers().write(2, block.getZ());
						try {
							ProtocolLibrary.getProtocolManager().sendServerPacket(player, signEditPacket);
						} catch (InvocationTargetException e) {
							e.printStackTrace();
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
				player.sendMessage("§dBone meal requires §5Advanced Builder §drank or higher");
				event.setCancelled(true);
				//
				// Command blocks
				//
			} else if (block.getType() == Material.COMMAND && !EvilBook.getProfile(player.getName()).rank.isAdmin()) {
				player.sendMessage("§dCommand Blocks are an §5Admin §donly feature");
				player.sendMessage("§dPlease type §6/admin §dto learn how to become admin");
				event.setCancelled(true);
				//
				// Survival container protection and ender chest blocking
				//
			} else if (EvilBook.isInSurvival(player) && EvilBook.getProfile(player.getName()).rank != Rank.SERVER_HOST) {
				if (block.getType() == Material.ENDER_CHEST) {
					player.sendMessage("§7Ender chests are blocked in survival");
					event.setCancelled(true);
				} else if (block.getState() instanceof InventoryHolder && EvilBook.isContainerProtected(event.getClickedBlock().getLocation(), player)) {
					player.sendMessage(ChatColor.GRAY + "You don't have permission to use this " + EvilBook.getFriendlyName(block.getType()).toLowerCase());
					event.setCancelled(true);
				}
			} else if (event.hasItem()) {
				if (EvilBook.getProfile(player).rank.isHigher(Rank.STAFF_LAPIS) && event.getItem().getType() == Material.GOLD_SPADE && (EvilBook.isInSurvival(player) == false || EvilBook.getProfile(player).rank == Rank.SERVER_HOST) && ((PlayerProfileAdmin)EvilBook.getProfile(player)).wandMode != EditWandMode.None) {
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
						player.sendMessage("§cYou don't have permission to dye blocks here");
						event.setCancelled(true);
					} else {
						// Dying
						if (event.getItem().getType() == Material.INK_SACK) {
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
				if (EvilBook.getProfile(player).rank != Rank.SERVER_HOST && !EvilBook.getPrivateWorldProperty(event.getTo().getWorld().getName().split("plugins/EvilBook/Private worlds/")[1], "AllowedPlayers").contains(player.getName().toLowerCase())) {
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
			if (!EvilBook.getProfile(player).isInvisible) {
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
		if (EvilBook.isInSurvival(player) && !EvilBook.isInSurvival(event.getFrom())) {
			EvilBook.setCreativeInventory(player);
			EvilBook.getSurvivalInventory(player);
			for (PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
			player.setGameMode(GameMode.SURVIVAL);
			if (EvilBook.getProfile(player).isInvisible && EvilBook.getProfile(player).rank != Rank.SERVER_HOST) {
				for (Player other : Bukkit.getServer().getOnlinePlayers()) other.showPlayer(player);
				EvilBook.getProfile(player).isInvisible = false;
				player.sendMessage("§7Vanish isn't allowed in survival, you are now visible");
			}
		} else if (!EvilBook.isInSurvival(player) && EvilBook.isInSurvival(event.getFrom())) {
			EvilBook.setSurvivalInventory(player);
			EvilBook.getCreativeInventory(player);
			player.setGameMode(GameMode.CREATIVE);
		}
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
		if (EvilBook.getProfile(event.getPlayer()).rank == Rank.SERVER_HOST) return;
		if (event.getNewGameMode() != GameMode.SURVIVAL && EvilBook.isInSurvival(event.getPlayer())) event.setCancelled(true);
	}
}
