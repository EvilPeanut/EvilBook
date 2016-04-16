package com.amentrix.evilbook.main;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerAPI;

import com.amentrix.evilbook.achievement.Achievement;
import com.amentrix.evilbook.dynmap.PlayerHomeMarkers;
import com.amentrix.evilbook.dynmap.WarpMarkers;
import com.amentrix.evilbook.eviledit.Session;
import com.amentrix.evilbook.eviledit.utils.EditWandMode;
import com.amentrix.evilbook.listeners.EventListenerBlock;
import com.amentrix.evilbook.listeners.EventListenerEntity;
import com.amentrix.evilbook.listeners.EventListenerInventory;
import com.amentrix.evilbook.listeners.EventListenerPacket;
import com.amentrix.evilbook.listeners.EventListenerPlayer;
import com.amentrix.evilbook.listeners.EventListenerVehicle;
import com.amentrix.evilbook.map.Maps;
import com.amentrix.evilbook.minigame.MinigameDifficulty;
import com.amentrix.evilbook.minigame.MinigameType;
import com.amentrix.evilbook.minigame.SkyBlockMinigameListener;
import com.amentrix.evilbook.minigame.TowerDefenseMinigame;
import com.amentrix.evilbook.nametag.NametagManager;
import com.amentrix.evilbook.reference.BlockReference;
import com.amentrix.evilbook.regions.Region;
import com.amentrix.evilbook.regions.RegionListener;
import com.amentrix.evilbook.regions.Regions;
import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;
import com.amentrix.evilbook.statistics.CommandStatistic;
import com.amentrix.evilbook.statistics.GlobalStatistic;
import com.amentrix.evilbook.statistics.PlayerStatistic;
import com.amentrix.evilbook.worldgen.PlotlandGenerator;
import com.amentrix.evilbook.worldgen.SkylandGenerator;

import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;
import net.minecraft.server.v1_9_R1.ChatClickable.EnumClickAction;

/**
 * EvilBook core class
 * @author Reece Aaron Lecrivain
 */
public class EvilBook extends JavaPlugin {
	public static final Map<String, PlayerProfile> playerProfiles = new HashMap<>();
	private static final Map<String, Boolean> cmdBlockWhitelist = new HashMap<>();
	public static final Map<World, List<DynamicSign>> dynamicSignList = new HashMap<>();
	public static final List<String> paidWorldList = new ArrayList<>();
	//public static final List<Region> regionList = new ArrayList<>();
	public static final List<Region> plotRegionList = new ArrayList<>();
	public static final List<Emitter> emitterList = new ArrayList<>();
	//
	public static final List<UUID> rareSpawnList = new ArrayList<>();
	//
	private static List<Location> inUseSurvivalWorkbenchesList = new ArrayList<>();
	private Session editSession;
	private Random random = new Random();
	// Log block API
	public static Consumer lbConsumer = null;
	// Dynmap API
	private static DynmapAPI dynmapAPI;
	public static MarkerAPI markerAPI;
	// Maps Module
	private Maps maps;
	// Config
	public static Properties config;

	/**
	 * Called when the plugin is enabled
	 */
	@Override
	public void onEnable() {
		//
		// Check mandatory files and folders exist
		//
		File check = new File("plugins/EvilBook");
		if (check.exists() == false && check.mkdir() == false) logSevere("Failed to create directory 'plugins/EvilBook'");
		check = new File("plugins/EvilBook/SkyBlock");
		if (check.exists() == false && check.mkdir() == false) logSevere("Failed to create directory 'plugins/EvilBook/SkyBlock'");
		check = new File("plugins/EvilBook/Private worlds");
		if (check.exists() == false && check.mkdir() == false) logSevere("Failed to create directory 'plugins/EvilBook/Private worlds'");
		//
		// Load or create config file
		//
		File propertiesFile = new File("plugins/EvilBook/Config.yml");
		if (!propertiesFile.exists()) {
			Properties properties = new Properties();
			properties.setProperty("server_name", "Amentrix");
			properties.setProperty("server_host", "EvilPeanut");
			properties.setProperty("host", "localhost");
			properties.setProperty("database", "evilbook");
			properties.setProperty("user", "root");
			properties.setProperty("password", "");
			properties.save("plugins/EvilBook/Config.yml", "EvilBook Configuration");
		}
		config = new Properties(propertiesFile);
		//
		// Register event listeners
		//
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new EventListenerBlock(this), this);
		pluginManager.registerEvents(new EventListenerEntity(), this);
		pluginManager.registerEvents(new EventListenerInventory(), this);
		pluginManager.registerEvents(new EventListenerPlayer(this), this);
		pluginManager.registerEvents(new EventListenerVehicle(), this);		
		pluginManager.registerEvents(new SkyBlockMinigameListener(), this);
		pluginManager.registerEvents(new RegionListener(this), this);
		//
		// Initialize EvilEdit session
		//
		setEditSession(new Session(this));
		//
		// Maps Module
		//
		this.maps = new Maps(this);
		getCommand("map").setExecutor(this.maps);
		//
		// World generators
		//
		WorldCreator flatLand = new WorldCreator("FlatLand");
		flatLand.type(WorldType.FLAT);
		getServer().createWorld(flatLand);
		getServer().createWorld(new WorldCreator("SurvivalLand"));
		getServer().createWorld(new WorldCreator("Minigame"));
		WorldCreator survivalLandNether = new WorldCreator("SurvivalLandNether");
		survivalLandNether.environment(Environment.NETHER);
		getServer().createWorld(survivalLandNether);
		WorldCreator survivalLandTheEnd = new WorldCreator("SurvivalLandTheEnd");
		survivalLandTheEnd.environment(Environment.THE_END);
		getServer().createWorld(survivalLandTheEnd);
		WorldCreator skyLand = new WorldCreator("SkyLand");
		skyLand.generator(new SkylandGenerator());
		getServer().createWorld(skyLand);
		getServer().createWorld(new WorldCreator("OldAmentrix"));
		WorldCreator plotLand = new WorldCreator("PlotLand");
		plotLand.generator(new PlotlandGenerator());
		getServer().createWorld(plotLand);
		// Paid world generator
		for (String world : new File("plugins/EvilBook/Private worlds/").list()) {
			WorldCreator privateWorld = new WorldCreator("plugins/EvilBook/Private worlds/" + world);
			switch (getPrivateWorldProperty(world, "WorldType")) {
			case "FLAT": privateWorld.type(WorldType.FLAT); break;
			case "NETHER": privateWorld.environment(Environment.NETHER); break;
			case "LARGE_BIOMES": privateWorld.type(WorldType.LARGE_BIOMES); break;
			case "SKY": privateWorld.generator(new SkylandGenerator()); break;
			default: break;
			}
			paidWorldList.add(world);
			getServer().createWorld(privateWorld);
		}
		//
		// Load EvilBook-NametagEdit module
		//
		NametagManager.load();
		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

				for (Player p : onlinePlayers)
				{
					NametagManager.clear(p.getName());
				}
			}
		});
		//
		// Connect EvilBook to MySQL
		//
		if (!SQL.connect(this)) {
			logSevere("Failed to load MySQL module");
			getServer().shutdown();
			return;
		}
		// Fix missing player UUID's
		if (SQL.isColumnExistant(TableType.PlayerProfile, "player")) {
			try (Statement statement = SQL.connection.createStatement()) {
				try (ResultSet rs = statement.executeQuery("SELECT player, player_name FROM " + SQL.database + ".`evilbook-playerprofiles` WHERE player IS NULL;")) {
					while (rs.next()) {
						try (Statement setStatement = SQL.connection.createStatement()) {
							OfflinePlayer player = getServer().getOfflinePlayer(rs.getString("player_name"));
							setStatement.execute("UPDATE " + SQL.database + "." + TableType.PlayerProfile.tableName + " SET player='" + player.getUniqueId().toString() + "' WHERE player_name='" + rs.getString("player_name") + "';");
							logInfo("Auto-fixed missing UUID for " + rs.getString("player_name"));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		// Make sure the SQL emitter table contains all emitter effect types
		String prepStatement = "ALTER TABLE " + SQL.database + ".`evilbook-emitters` MODIFY effect ENUM(";
		for (EmitterEffect effect : EmitterEffect.values()) prepStatement += "'" + effect.name() + "',";
		prepStatement = prepStatement.substring(0, prepStatement.length() - 1) + ");";
		try (Statement statement = SQL.connection.createStatement()) {
			statement.execute(prepStatement);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Fix missing world player location entries
		for (World world : getServer().getWorlds()) {
			String worldName = world.getName();
			if (worldName.contains("Private worlds/")) worldName = worldName.split("Private worlds/")[1];
			if (!SQL.isColumnExistant(TableType.PlayerLocation, worldName)) {
				try {
					SQL.insertNullColumn(TableType.PlayerLocation, worldName + " TINYTEXT");
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				logInfo("Auto-fixed missing world " + worldName + " in player locations table");
			}
		}
		//
		// Load regions
		//
		for (World world : getServer().getWorlds()) {
			Regions.initWorld(world.getName());
		}
		try (Statement statement = SQL.connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.Region.tableName + ";")) {
				while (rs.next()) {
					if (getServer().getWorld(rs.getString("world")) != null) {
						if (rs.getString("region_name").startsWith("PlotRegion")) {
							plotRegionList.add(new Region(rs));
						} else {
							Regions.addRegion(rs.getString("world"), new Region(rs));
						}
					} else {
						logInfo("Region " + rs.getString("region_name") + " in " + rs.getString("world") + " not loaded location unavailable");
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		//
		// Load dynamic signs
		//
		for (World world : getServer().getWorlds()) {
			dynamicSignList.put(world, new ArrayList());
		}
		try (Statement statement = SQL.connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.DynamicSign.tableName + ";")) {
				while (rs.next()) {
					World world = getServer().getWorld(UUID.fromString(rs.getString("world")));
					if (world != null) {
						dynamicSignList.get(world).add(new DynamicSign(rs));
					} else {
						logInfo("Dynamic sign in " + rs.getString("world") + " at " + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + " not loaded location unavailable");
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		//
		// Load emitters
		//
		try (Statement statement = SQL.connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.Emitter.tableName + ";")) {
				while (rs.next()) {
					if (getServer().getWorld(rs.getString("world")) != null) {
						emitterList.add(new Emitter(this, rs));
					} else {
						logInfo("Emitter in " + rs.getString("world") + " at " + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + " not loaded location unavailable");
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		//
		// Load Command Block Whitelist
		//
		cmdBlockWhitelist.put("say", true);
		cmdBlockWhitelist.put("broadcast", true);
		cmdBlockWhitelist.put("setblock", false);
		cmdBlockWhitelist.put("testfor", true);
		cmdBlockWhitelist.put("testforblock", true);
		cmdBlockWhitelist.put("testforblocks", true);
		cmdBlockWhitelist.put("scoreboard", true);
		cmdBlockWhitelist.put("effect", false);
		cmdBlockWhitelist.put("me", true);
		cmdBlockWhitelist.put("clear", false);
		cmdBlockWhitelist.put("tell", true);
		cmdBlockWhitelist.put("toggledownfall", false);
		cmdBlockWhitelist.put("weather", false);
		cmdBlockWhitelist.put("time", false);
		//
		// Log Block Integration
		//
		final Plugin plugin = pluginManager.getPlugin("EvilBook-Logging");
		if (plugin != null) {
			lbConsumer = ((LogBlock) plugin).getConsumer();
		} else {
			logSevere("Failed to load EvilBook-Logging module");
			getServer().shutdown();
			return;
		}
		//
		// Dynmap Integration
		//
		final Plugin dynmapPlugin = pluginManager.getPlugin("dynmap");
		if (dynmapPlugin != null) {
			try {
				dynmapAPI = ((DynmapAPI) dynmapPlugin);
				markerAPI = dynmapAPI.getMarkerAPI();
				PlayerHomeMarkers.loadPlayerHomes();
				WarpMarkers.loadWarps();
			} catch (Exception exception) {
				logSevere("Failed to load Dynmap module, is Dynmap out of date?");
			}
		} else {
			logSevere("Failed to load Dynmap module");
			getServer().shutdown();
			return;
		}
		//
		// Register protocolLib listeners
		//
		EventListenerPacket.registerSignUpdatePacketReceiver(this);
		//
		// Scheduler
		//
		Scheduler scheduler = new Scheduler(this);
		scheduler.sqlBatchUpdate();
		scheduler.tipsAutosave();
		scheduler.updateServices();
		scheduler.updateDisguise();
	}

	/**
	 * Called when the plugin is disabled
	 */
	@Override
	public void onDisable() {
		this.maps.saveMapIdList();
	}
	
	/**
	 * Called when a command is executed
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		//
		// Command block command handling
		//
		if (sender instanceof BlockCommandSender) {
			Boolean isFound = false;
			for (String cmd : cmdBlockWhitelist.keySet()) {
				if (cmd.equalsIgnoreCase(command.getName()) && (!isInSurvival(((BlockCommandSender)sender).getBlock().getWorld()) || cmdBlockWhitelist.get(cmd))) {
					isFound = true;
					break;
				}
			}
			if (!isFound) {
				logInfo("Command block command blocked: " + command.getName());
				return true;
			}
	    }
		//
		// Statistics
		//
		GlobalStatistic.incrementStatistic(GlobalStatistic.CommandsExecuted, 1);
		CommandStatistic.increment(command.getName());
		//
		// Alt Command
		//
		if (command.getName().equalsIgnoreCase("alt")) {
			if (args.length == 1) {
				try (Statement statement = SQL.connection.createStatement()) {
					try (ResultSet rs = statement.executeQuery("SELECT player_name, ip FROM " + SQL.database + "." + TableType.PlayerProfile.tableName + " WHERE ip='" + getPlayerIP(args[0]) + "' AND not(player_name='" + args[0] + "');")) {
						if (!rs.isBeforeFirst()) {
						    sender.sendMessage("§7No accounts are associated with this player's ip");
						} else {
							sender.sendMessage("§7" + getServer().getOfflinePlayer(args[0]).getName() + "'s §lpossible §7alternative accounts:");
							while (rs.next()) if (!args[0].equalsIgnoreCase(rs.getString("player_name"))) sender.sendMessage("§7" + getServer().getOfflinePlayer(rs.getString("player_name")).getName());
						}
						sender.sendMessage("§7Please note IPs change and bans should not be based on IP");
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(sender, "/alt [playerName]");
			}
			return true;
		}
		//
		// IP Command
		//
		if (command.getName().equalsIgnoreCase("ip")) {
			if (args.length == 1) {
				String ip = getPlayerIP(args[0]);
				if (ip == null) {
					sender.sendMessage("§7" + getServer().getOfflinePlayer(args[0]).getName() + "'s IP isn't logged");
				} else {
					sender.sendMessage("§7" + getServer().getOfflinePlayer(args[0]).getName() + "'s §llast known IP §7is " + ip);
				}
				sender.sendMessage("§7Please note IPs change and bans should not be based on IP");
			} else {
				ChatExtensions.sendCommandHelpMessage(sender, "/ip [playerName]");
			}
			return true;
		}
		//
		// Dr. Watson Command
		//
		if (command.getName().equalsIgnoreCase("drwatson")) {
			if (args.length == 0) {
				ChatExtensions.sendCommandHelpMessage(sender, 
						Arrays.asList("/drwatson sql",
								"/drwatson sqlclean",
								"/drwatson memstat",
								"/drwatson liststat",
								"/drwatson listscan",
								"/drwatson opfix",
								"/drwatson worldinfo [worldName]"));
			} else if (args[0].equalsIgnoreCase("sql")) {
				//
				// Check `evilbook-commandblock` table
				//
				sender.sendMessage("§7Dr. Watson scanning `evilbook-commandblock`...");
				try (Statement statement = SQL.connection.createStatement()) {
					try (ResultSet rs = statement.executeQuery("SELECT world, x, y, z FROM " + SQL.database + "." + TableType.CommandBlock.tableName + ";")) {
						while (rs.next()) {
							if (rs.getString("world") == null) {
								sender.sendMessage("§7--> World is not available");
							} else if (new Location(Bukkit.getWorld(UUID.fromString(rs.getString("world"))), rs.getInt("x"), rs.getInt("y"), rs.getInt("z")).getBlock().getType() != Material.COMMAND) {
								sender.sendMessage("§7--> Location (" + rs.getString("world") + ", " + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + ") is not a command block"); 
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				//
				// Check `evilbook-dynamicsigns` table
				//
				sender.sendMessage("§7Dr. Watson scanning `evilbook-dynamicsigns`...");
				try (Statement statement = SQL.connection.createStatement()) {
					try (ResultSet rs = statement.executeQuery("SELECT world, x, y, z FROM " + SQL.database + "." + TableType.DynamicSign.tableName + ";")) {
						while (rs.next()) {
							if (Bukkit.getWorld(UUID.fromString(rs.getString("world"))) == null) {
								sender.sendMessage("§7--> World '" + rs.getString("world") + "' is not loaded");
							} else if (new Location(Bukkit.getWorld(UUID.fromString(rs.getString("world"))), rs.getInt("x"), rs.getInt("y"), rs.getInt("z")).getBlock().getState() instanceof Sign == false) {
								sender.sendMessage("§7--> Location (" + rs.getString("world") + ", " + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + ") is not a sign"); 
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				//
				// Check `evilbook-emitters` table
				//
				sender.sendMessage("§7Dr. Watson scanning `evilbook-emitters`...");
				try (Statement statement = SQL.connection.createStatement()) {
					try (ResultSet rs = statement.executeQuery("SELECT world, x, y, z FROM " + SQL.database + "." + TableType.Emitter.tableName + ";")) {
						while (rs.next()) {
							if (Bukkit.getWorld(rs.getString("world")) == null) {
								sender.sendMessage("§7--> World '" + rs.getString("world") + "' is not loaded"); 
							} else if (new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x"), rs.getInt("y"), rs.getInt("z")).getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
								sender.sendMessage("§7--> Location under (" + rs.getString("world") + ", " + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + ") is air"); 
							} else if (rs.getInt("y") > Bukkit.getWorld(rs.getString("world")).getMaxHeight()) {
								sender.sendMessage("§7--> Location (" + rs.getString("world") + ", " + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + ") is above world max height"); 
							} else if (rs.getInt("y") < 0) {
								sender.sendMessage("§7--> Location (" + rs.getString("world") + ", " + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + ") is below world min height"); 
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				//
				// Check `evilbook-protectedcontainers` table
				//
				sender.sendMessage("§7Dr. Watson scanning `evilbook-protectedcontainers`...");
				try (Statement statement = SQL.connection.createStatement()) {
					try (ResultSet rs = statement.executeQuery("SELECT world, x, y, z FROM " + SQL.database + "." + TableType.ContainerProtection.tableName + ";")) {
						while (rs.next()) {
							if (Bukkit.getWorld(rs.getString("world")) == null) {
								sender.sendMessage("§7--> World '" + rs.getString("world") + "' is not loaded"); 
							} else if (new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x"), rs.getInt("y"), rs.getInt("z")).getBlock().getState() instanceof InventoryHolder == false) {
								sender.sendMessage("§7--> Location (" + rs.getString("world") + ", " + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + ") is not a container"); 
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				//
				// Check `evilbook-regions` table
				//
				sender.sendMessage("§7Dr. Watson scanning `evilbook-regions`...");
				try (Statement statement = SQL.connection.createStatement()) {
					try (ResultSet rs = statement.executeQuery("SELECT world FROM " + SQL.database + "." + TableType.Region.tableName + ";")) {
						while (rs.next()) {
							if (Bukkit.getWorld(rs.getString("world")) == null) {
								sender.sendMessage("§7--> World '" + rs.getString("world") + "' is not loaded"); 
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				//
				// Check `evilbook-warps` table
				//
				sender.sendMessage("§7Dr. Watson scanning `evilbook-warps`...");
				try (Statement statement = SQL.connection.createStatement()) {
					try (ResultSet rs = statement.executeQuery("SELECT location FROM " + SQL.database + "." + TableType.Warps.tableName + ";")) {
						while (rs.next()) {
							if (Bukkit.getWorld(rs.getString("location").split(">")[0]) == null) {
								sender.sendMessage("§7--> World '" + rs.getString("location").split(">")[0] + "' is not loaded"); 
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				//
				// Check for region warp issues
				//
				//TODO: Regions: Re-add
				/*
				sender.sendMessage("§7Dr. Watson scanning regions for warp issues...");
				for (Region region : regionList) {
					if (region.getWarp() != null) {
						Location warp = SQL.getWarp(region.getWarp());
						if (warp != null) {
							if (Region.isInRegion(region, warp)) {
								sender.sendMessage("§7--> Region " + region.getRegionName() + " has an infinite warp loop"); 
							}
						} else {
							sender.sendMessage("§7--> Region " + region.getRegionName() + " has a non-existant warp"); 
						}
					}
				}*/
				sender.sendMessage("§7Dr. Watson scan finished");
			} else if (args[0].equalsIgnoreCase("sqlclean")) {
				//
				// Clean `evilbook-commandblock` table
				//
				sender.sendMessage("§7Dr. Watson cleaning `evilbook-commandblock`...");
				try (Statement statement = SQL.connection.createStatement()) {
					try (ResultSet rs = statement.executeQuery("SELECT world, x, y, z FROM " + SQL.database + "." + TableType.CommandBlock.tableName + ";")) {
						while (rs.next()) {
							if (rs.getString("world") == null) {
								sender.sendMessage("§7--> FIXED: World is not available");
								SQL.deleteRowFromCriteria(TableType.CommandBlock, "x='" + rs.getString("x") + "' AND y='" + rs.getString("y") + "' AND z='" + rs.getString("z") + "' AND world IS NULL");
							} else if (new Location(Bukkit.getWorld(UUID.fromString(rs.getString("world"))), rs.getInt("x"), rs.getInt("y"), rs.getInt("z")).getBlock().getType() != Material.COMMAND) {
								sender.sendMessage("§7--> FIXED: Location (" + rs.getString("world") + ", " + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + ") is not a command block"); 
								SQL.deleteRowFromCriteria(TableType.CommandBlock, "world='" + rs.getString("world") + "' AND x='" + rs.getString("x") + "' AND y='" + rs.getString("y") + "' AND z='" + rs.getString("z") + "'");
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				//
				// Clean `evilbook-dynamicsigns` table
				//
				sender.sendMessage("§7Dr. Watson cleaning `evilbook-dynamicsigns`...");
				try (Statement statement = SQL.connection.createStatement()) {
					try (ResultSet rs = statement.executeQuery("SELECT world, x, y, z FROM " + SQL.database + ".`evilbook-dynamicsigns`;")) {
						while (rs.next()) {
							if (Bukkit.getWorld(UUID.fromString(rs.getString("world"))) == null) {
								sender.sendMessage("§7--> FIXED: World '" + rs.getString("world") + "' is not loaded"); 
								SQL.deleteRowFromCriteria(TableType.DynamicSign, "world='" + rs.getString("world") + "'");
							} else if (new Location(Bukkit.getWorld(UUID.fromString(rs.getString("world"))), rs.getInt("x"), rs.getInt("y"), rs.getInt("z")).getBlock().getState() instanceof Sign == false) {
								sender.sendMessage("§7--> FIXED: Location (" + rs.getString("world") + ", " + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + ") is not a sign"); 
								SQL.deleteRowFromCriteria(TableType.DynamicSign, "world='" + rs.getString("world") + "' AND x='" + rs.getString("x") + "' AND y='" + rs.getString("y") + "' AND z='" + rs.getString("z") + "'");
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				//
				// Clean `evilbook-emitters` table
				//
				sender.sendMessage("§7Dr. Watson cleaning `evilbook-emitters`...");
				try (Statement statement = SQL.connection.createStatement()) {
					try (ResultSet rs = statement.executeQuery("SELECT world, x, y, z FROM " + SQL.database + "." + TableType.Emitter.tableName + ";")) {
						while (rs.next()) {
							if (Bukkit.getWorld(rs.getString("world")) == null) {
								sender.sendMessage("§7--> FIXED: World '" + rs.getString("world") + "' is not loaded"); 
								SQL.deleteRowFromCriteria(TableType.Emitter, "world='" + rs.getString("world") + "'");
							} else if (new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x"), rs.getInt("y"), rs.getInt("z")).getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
								sender.sendMessage("§7--> FIXED: Location under (" + rs.getString("world") + ", " + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + ") is air"); 
								SQL.deleteRowFromCriteria(TableType.Emitter, "world='" + rs.getString("world") + "' AND x='" + rs.getString("x") + "' AND y='" + rs.getString("y") + "' AND z='" + rs.getString("z") + "'");
							} else if (rs.getInt("y") > Bukkit.getWorld(rs.getString("world")).getMaxHeight()) {
								sender.sendMessage("§7--> FIXED: Location (" + rs.getString("world") + ", " + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + ") is above world max height"); 
								SQL.deleteRowFromCriteria(TableType.Emitter, "world='" + rs.getString("world") + "' AND x='" + rs.getString("x") + "' AND y='" + rs.getString("y") + "' AND z='" + rs.getString("z") + "'");
							} else if (rs.getInt("y") < 0) {
								sender.sendMessage("§7--> FIXED: Location (" + rs.getString("world") + ", " + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + ") is below world min height"); 
								SQL.deleteRowFromCriteria(TableType.Emitter, "world='" + rs.getString("world") + "' AND x='" + rs.getString("x") + "' AND y='" + rs.getString("y") + "' AND z='" + rs.getString("z") + "'");
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				//
				// Clean `evilbook-protectedcontainers` table
				//
				sender.sendMessage("§7Dr. Watson cleaning `evilbook-protectedcontainers`...");
				try (Statement statement = SQL.connection.createStatement()) {
					try (ResultSet rs = statement.executeQuery("SELECT world, x, y, z FROM " + SQL.database + "." + TableType.ContainerProtection.tableName + ";")) {
						while (rs.next()) {
							if (Bukkit.getWorld(rs.getString("world")) == null) {
								sender.sendMessage("§7--> FIXED: World '" + rs.getString("world") + "' is not loaded"); 
								SQL.deleteRowFromCriteria(TableType.ContainerProtection, "world='" + rs.getString("world") + "'");
							} else if (new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x"), rs.getInt("y"), rs.getInt("z")).getBlock().getState() instanceof InventoryHolder == false) {
								sender.sendMessage("§7--> FIXED: Location (" + rs.getString("world") + ", " + rs.getString("x") + ", " + rs.getString("y") + ", " + rs.getString("z") + ") is not a container"); 
								SQL.deleteRowFromCriteria(TableType.ContainerProtection, "world='" + rs.getString("world") + "' AND x='" + rs.getString("x") + "' AND y='" + rs.getString("y") + "' AND z='" + rs.getString("z") + "'");
							}	
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				//
				// Clean `evilbook-regions` table
				//
				sender.sendMessage("§7Dr. Watson cleaning `evilbook-regions`...");
				try (Statement statement = SQL.connection.createStatement()) {
					try (ResultSet rs = statement.executeQuery("SELECT world FROM " + SQL.database + "." + TableType.Region.tableName + ";")) {
						while (rs.next()) {
							if (Bukkit.getWorld(rs.getString("world")) == null) {
								sender.sendMessage("§7--> FIXED: World '" + rs.getString("world") + "' is not loaded"); 
								SQL.deleteRowFromCriteria(TableType.Region, "world='" + rs.getString("world") + "'");
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				//
				// Clean `evilbook-warps` table
				//
				sender.sendMessage("§7Dr. Watson cleaning `evilbook-warps`...");
				try (Statement statement = SQL.connection.createStatement()) {
					try (ResultSet rs = statement.executeQuery("SELECT location FROM " + SQL.database + "." + TableType.Warps.tableName + ";")) {
						while (rs.next()) {
							if (Bukkit.getWorld(rs.getString("location").split(">")[0]) == null) {
								sender.sendMessage("§7--> FIXED: World '" + rs.getString("location").split(">")[0] + "' is not loaded"); 
								SQL.deleteRowFromCriteria(TableType.Warps, "location='" + rs.getString("location") + "'");
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				//
				// Clean region warp issues
				//
				//TODO: Regions: Re-add
				/*
				sender.sendMessage("§7Dr. Watson fixing region warp issues...");
				for (Region region : regionList) {
					if (region.getWarp() != null) {
						Location warp = SQL.getWarp(region.getWarp());
						if (warp != null) {
							if (Region.isInRegion(region, warp)) {
								region.setWarp(null);
								region.saveRegion();
								sender.sendMessage("§7--> FIXED: Region " + region.getRegionName() + " has an infinite warp loop"); 
							}
						} else {
							region.setWarp(null);
							region.saveRegion();
							sender.sendMessage("§7--> FIXED: Region " + region.getRegionName() + " has a non-existant warp"); 
						}
					}
				}*/
				sender.sendMessage("§7Dr. Watson scan finished");
			} else if (args[0].equalsIgnoreCase("memstat")) {
				//
				// Memory statistics
				//
				sender.sendMessage("§dTotal memory §5" + Runtime.getRuntime().maxMemory() / 1048576 + "MB");
				int freePercentage = (int) Math.round((double)(Runtime.getRuntime().freeMemory()) / (double)(Runtime.getRuntime().maxMemory()) * 100);
				sender.sendMessage("§dUsed memory " + ((100 - freePercentage) > 90 ? "§c" : "§a") + ((Runtime.getRuntime().maxMemory() / 1048576) - (Runtime.getRuntime().freeMemory() / 1048576)) + "MB §e(" + (100 - freePercentage) + "%)");
				sender.sendMessage("§dFree memory " + (freePercentage > 10 ? "§a" : "§c") + Runtime.getRuntime().freeMemory() / 1048576 + "MB §e(" + freePercentage + "%)");
			} else if (args[0].equalsIgnoreCase("liststat")) {
				sender.sendMessage("§5Lists Size Information");
				sender.sendMessage("§dPlayer Profiles = " + playerProfiles.size());
				sender.sendMessage("§dDynamic Signs = " + dynamicSignList.size());
				sender.sendMessage("§dPaid Worlds = " + paidWorldList.size());
				//TODO: Regions: Re-add?
				//sender.sendMessage("§dRegions = " + regionList.size());
				sender.sendMessage("§dPlot Regions = " + plotRegionList.size());
				sender.sendMessage("§dEmitters = " + emitterList.size());
				sender.sendMessage("§dRare Spawns = " + rareSpawnList.size());
				sender.sendMessage("§dIn Use Survival Workbenches = " + inUseSurvivalWorkbenchesList.size());
			} else if (args[0].equalsIgnoreCase("listscan")) {
				sender.sendMessage("§7Dr. Watson scanning dynamicSignList...");
				for (final World world : EvilBook.dynamicSignList.keySet()) {
					for (DynamicSign dynamicSign : EvilBook.dynamicSignList.get(world)) {
						if (dynamicSign.location.getBlock().getType() != Material.SIGN_POST && dynamicSign.location.getBlock().getType() != Material.WALL_SIGN) {
							sender.sendMessage("§7--> Dynamic sign at (" + dynamicSign.location.getBlockX() + ", " + dynamicSign.location.getBlockY() + ", " + 
									dynamicSign.location.getBlockZ() + ") isn't a sign");
						}
					}	
				}
				sender.sendMessage("§7Dr. Watson scan finished");
			} else if (args[0].equalsIgnoreCase("opfix")) {
				getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
					@Override
					public void run() {
						try (Statement statement = SQL.connection.createStatement()) {
							try (ResultSet rs = statement.executeQuery("SELECT player_name, rank FROM " + SQL.database + ".`evilbook-playerprofiles`;")) {
								while (rs.next()) {
									if (Rank.valueOf(rs.getString("rank")).isHigher(Rank.POLICE)) {
										OfflinePlayer player = getServer().getOfflinePlayer(rs.getString("player_name"));
										if (!player.isOp()) {
											getServer().getOfflinePlayer(rs.getString("player_name")).setOp(true);
											logInfo("Auto-fixed player " + rs.getString("player_name") + "'s OP status");
										} else {
											logInfo("Passed player " + rs.getString("player_name") + "'s OP status");
										}
									} else {
										OfflinePlayer player = getServer().getOfflinePlayer(rs.getString("player_name"));
										if (player.isOp()) {
											getServer().getOfflinePlayer(rs.getString("player_name")).setOp(false);
											logInfo("Auto-fixed player " + rs.getString("player_name") + "'s OP status");
										} else {
											logInfo("Passed player " + rs.getString("player_name") + "'s OP status");
										}
									}
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				});
			} else if (args[0].equalsIgnoreCase("worldinfo")) {
				if (args.length == 2) {
					World world = getServer().getWorld(args[1]);
					if (world == null) {
						sender.sendMessage("§7The world " + args[1] + " isn't loaded");
					} else {
						sender.sendMessage("§5" + args[1] + " World Information");
						sender.sendMessage("§dAutosave = " + world.isAutoSave());
						sender.sendMessage("§dWeather duration = " + world.getWeatherDuration());
						sender.sendMessage("§dSeed = " + world.getSeed());
						sender.sendMessage("§dDifficulty = " + world.getDifficulty().name());
						sender.sendMessage("§dSpawn kept in memory = " + world.getKeepSpawnInMemory());
						sender.sendMessage("§dPlayers in world = " + world.getPlayers().size());
					}
				} else {
					ChatExtensions.sendCommandHelpMessage(sender, "/drwatson worldinfo [worldName]");
				}
			}
			return true;
		}
		//
		// Broadcast Command
		//
		if (command.getName().equalsIgnoreCase("broadcast") || command.getName().equalsIgnoreCase("say")) {
			if (args.length > 0) {
				broadcastPlayerMessage(sender.getName(), "§d[§5" + (sender.getName().equals("CONSOLE") ? "Server" : sender.getName()) + "§d] " + toFormattedString(StringUtils.join(args, " ")));
			} else {
				ChatExtensions.sendCommandHelpMessage(sender, "/" + command.getName().toLowerCase(Locale.UK) + " [message]");
			}
			return true;
		}
		//
		// Me Command
		//
		if (command.getName().equalsIgnoreCase("me")) {
			String me = "";
			for (String msg : args) me += " " + msg;
			broadcastPlayerMessage(sender.getName(), "* " + (sender instanceof Player ? ((Player)sender).getDisplayName() : sender.getName()) + EvilBook.toFormattedString(me.toString()));
			return true;
		}
		//
		// Block command blocks executing commands below here
		//
		if (sender instanceof BlockCommandSender) return true;
		//
		// View Money Balance Command
		//
		if (command.getName().equalsIgnoreCase("money") || command.getName().equalsIgnoreCase("balance") || command.getName().equalsIgnoreCase("bal")) {
			if (args.length == 0 && sender instanceof Player) {
				sender.sendMessage("§5Your account balance is §d$" + getProfile(sender).money);
			} else if (args.length == 1) {
				if (isProfileExistant(args[0])) {
					if (getPlayer(args[0]) != null) {
						sender.sendMessage("§5" + getPlayer(args[0]).getDisplayName() + "'s account balance is §d$" + getProfile(args[0]).money);
					} else {
						sender.sendMessage("§5" + getServer().getOfflinePlayer(args[0]).getName() + "'s account balance is §d$" + SQL.getInteger(TableType.PlayerProfile, args[0], "money"));
					}
				} else {
					sender.sendMessage("§7You can't view a player's account balance who doesn't exist");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(sender, "/" + command.getName().toLowerCase(Locale.UK) + " <player>");
			}
			return true;
		}
		//
		// Make Admin Command
		//
		if (command.getName().equalsIgnoreCase("makeadmin")) {
			if (args.length == 1) {
				if (isProfileExistant(args[0])) {
					if (getPlayer(args[0]) != null) {
						getProfile(args[0]).rank = Rank.ADMIN;
						getProfile(args[0]).updatePlayerListName();
						// Player profile type conversion
						if (getProfile(args[0]) instanceof PlayerProfileNormal) {
							getProfile(args[0]).saveProfile();
							playerProfiles.remove(getPlayer(args[0]).getName().toLowerCase(Locale.UK));
							playerProfiles.put(getPlayer(args[0]).getName().toLowerCase(Locale.UK), new PlayerProfileAdmin(this, getPlayer(args[0]), false));
						}
						// Set nametag color
						getProfile(args[0]).updateNametag("§" + getProfile(getPlayer(args[0])).rank.getColor((getProfile(getPlayer(args[0])))), null);
						//
						broadcastPlayerMessage(getPlayer(args[0]).getName(), "§c" + getPlayer(args[0]).getDisplayName() + " §dhas been promoted to Admin rank");
					} else {
						SQL.setRank(args[0], Rank.ADMIN);
						broadcastPlayerMessage(getServer().getOfflinePlayer(args[0]).getName(), "§c" + getServer().getOfflinePlayer(args[0]).getName() + " §dhas been promoted to Admin rank");
					}
					getServer().getOfflinePlayer(args[0]).setOp(true);
				} else {
					sender.sendMessage("§7You can't give admin rank to a player who doesn't exist");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(sender, "/makeadmin [player]");
			}
			return true;
		}
		//
		// Stop Command
		//
		if (command.getName().equalsIgnoreCase("stop")) {
			String message = StringUtils.join(args, " ");
			for (Player p : getServer().getOnlinePlayers()) {
				getProfile(p).saveProfile();
				p.kickPlayer(message);
			}
			getServer().shutdown();
			return true;
		}
		//
		// List Command
		//
		if (command.getName().equalsIgnoreCase("list") || command.getName().equalsIgnoreCase("plist") || command.getName().equalsIgnoreCase("who") || command.getName().equalsIgnoreCase("playerlist") || command.getName().equalsIgnoreCase("online")) {
			sender.sendMessage(getServer().getOnlinePlayers().size() == 0 ? "§dThere are no online players" : "§5" + getServer().getOnlinePlayers().size() + (getServer().getOnlinePlayers().size() > 1 ? " §dOnline players" : " §dOnline player"));
			for (Player p : getServer().getOnlinePlayers()) sender.sendMessage("  " + ((getProfile(p.getName()) instanceof PlayerProfileAdmin && ((PlayerProfileAdmin)getProfile(p.getName())).nameAlias == null) || (getProfile(p.getName()) instanceof PlayerProfileNormal) ? p.getDisplayName() : p.getDisplayName() + " §7(" + p.getName() + ")"));
			return true;
		}
		//
		// Demote Command
		//
		if (command.getName().equalsIgnoreCase("demote")) {
			if (args.length == 1) {
				if (isProfileExistant(args[0])) {
					if (getPlayer(args[0]) != null) {
						getProfile(args[0]).rank = getProfile(args[0]).rank.getPreviousRank();
						// Alert owner that player has been demoted via mail
						if (!sender.getName().equals(EvilBook.config.getProperty("server_host"))) SQL.insert(TableType.Mail, "'Server Report','" + getServer().getOfflinePlayer(EvilBook.config.getProperty("server_host")).getUniqueId().toString() + "','" + sender.getName() + " has demoted " + args[0] + " to " + getProfile(args[0]).rank.getName() + " rank','" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "'");
						//
						if (getProfile(args[0]).rank == Rank.POLICE) { 
							getServer().getOfflinePlayer(args[0]).setOp(false);
							// Player profile type conversion
							if (getProfile(args[0]) instanceof PlayerProfileAdmin) {
								getProfile(args[0]).saveProfile();
								playerProfiles.remove(getPlayer(args[0]).getName().toLowerCase(Locale.UK));
								playerProfiles.put(getPlayer(args[0]).getName().toLowerCase(Locale.UK), new PlayerProfileNormal(this, getPlayer(args[0]), false));
							}
							//
						}
						// Set nametag color
						getProfile(args[0]).updateNametag("§" + getProfile(getPlayer(args[0])).rank.getColor((getProfile(getPlayer(args[0])))), null);
						//
						getProfile(args[0]).updatePlayerListName();
						broadcastPlayerMessage(getPlayer(args[0]).getName(), "§c" + getPlayer(args[0]).getDisplayName() + " §dhas been demoted to " + getProfile(args[0]).rank.getName() + " rank");
					} else {
						if (SQL.getRank(args[0]).getPreviousRank() == Rank.POLICE) getServer().getOfflinePlayer(args[0]).setOp(false);
						SQL.setRank(args[0], SQL.getRank(args[0]).getPreviousRank());
						broadcastPlayerMessage(getServer().getOfflinePlayer(args[0]).getName(), "§c" + getServer().getOfflinePlayer(args[0]).getName() + " §dhas been demoted to " + SQL.getRank(args[0]).getName() + " rank");
					}
				} else {
					sender.sendMessage("§7You can't demote a player who doesn't exist");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(sender, "/demote [player]");
			}
			return true;
		}
		//
		// Rules Command
		//
		if (command.getName().equalsIgnoreCase("rules")) {
			sender.sendMessage("§d" + config.getProperty("server_name") + " Server Rules");
			sender.sendMessage("  §5§l1 §5Do not grief, even in survival");
			sender.sendMessage("  §5§l2 §5Do not advertise other servers");
			sender.sendMessage("  §5§l3 §5Do not spam at any time");
			sender.sendMessage("  §5§l4 §5Do not exploit bugs");
			sender.sendMessage("§7Breaking a rule §lwill §7result in permanent ban");
			sender.sendMessage("§7MCBans is operating on the server");
			return true;
		}
		//
		// Ranks Command
		//
		if (command.getName().equalsIgnoreCase("ranks")) {
			if (args.length == 0 || args[0].equals("1")) {
				sender.sendMessage("§dRanks §5- §dPage 1 of 3 §5- §7/ranks <page>");
				sender.sendMessage("  §0[§EBuilder§0] §7Begin your journey on " + config.getProperty("server_name"));
				sender.sendMessage("  §0[§5Creator§0] §7Show an admin your first creation");
				sender.sendMessage("  §0[§DDesigner§0] §7Impress an admin with multiple creations");
				sender.sendMessage("  §0[§9Architect§0] §7Help an admin or impress with amazing builds");
				sender.sendMessage("  §0[§3Engineer§0] §7Show commitment and continue to impress");
				sender.sendMessage("  §7To purchase a rank please see /admin");
			} else if (args[0].equals("2")) {
				sender.sendMessage("§dRanks §5- §dPage 2 of 3 §5- §7/ranks <page>");
				sender.sendMessage("  §0[§EStaff§0] §7Selected by the Admin Staff team");
				sender.sendMessage("  §0[§7Staff§0] §7Selected by the Admin Staff team");
				sender.sendMessage("  §0[§6Staff§0] §7Selected by the Admin Staff team");
				sender.sendMessage("  §0[§1Staff§0] §7Selected by the Admin Staff team");
				sender.sendMessage("  §0[§BStaff§0] §7Selected by the Admin Staff team");
				sender.sendMessage("  §7To purchase a rank please see /admin");
			} else if (args[0].equals("3")) {
				sender.sendMessage("§dRanks §5- §dPage 3 of 3 §5- §7/ranks <page>");
				sender.sendMessage("  §0[§4Admin§0] §7Purchased from the " + config.getProperty("server_name") + " website");
				sender.sendMessage("  §0[§ACouncillor§0] §7Purchased from the " + config.getProperty("server_name") + " website");
				sender.sendMessage("  §0[§CElite§0] §7Purchased from the " + config.getProperty("server_name") + " website");
				sender.sendMessage("  §0[§6Investor§0] §7Purchased from the " + config.getProperty("server_name") + " website");
				sender.sendMessage("  §0[§6§OTycoon§0] §7Purchased from the " + config.getProperty("server_name") + " website");
				sender.sendMessage("  §0[§B§OAdmin Staff§0] §7Elected admin staff team");
				sender.sendMessage("  §0[§BServer Host§0] §7" + EvilBook.config.getProperty("server_host"));
				sender.sendMessage("  §7To purchase a rank please see /admin");
			}
			return true;
		}
		//
		// Clean Command
		//
		if (command.getName().equalsIgnoreCase("clean")) {
			int entitiesRemoved = 0;
			for (World w : getServer().getWorlds()) {
				entitiesRemoved += w.getEntities().size();
				if (isInSurvival(w) || isInMinigame(w, MinigameType.SKYBLOCK)) continue;
				for (Entity entity : w.getEntities()) {
					if (entity.getType() == EntityType.PLAYER 
							|| entity.getType() == EntityType.ARMOR_STAND
							|| entity.getType() == EntityType.PAINTING
							|| entity.getType() == EntityType.FISHING_HOOK 
							|| entity.getType() == EntityType.ITEM_FRAME
							|| (entity instanceof Tameable && ((Tameable)entity).isTamed())) {
						entitiesRemoved--;
					} else {
						entity.remove();
					}
				}
			}
			sender.sendMessage("§dServer clean removed §5" + entitiesRemoved + "§d entities");
			return true;
		}
		//
		// View Colors Command
		//
		if (command.getName().equalsIgnoreCase("colors") || command.getName().equalsIgnoreCase("colours")) {
			if (args.length == 0 || args[0].equals("1")) {
				sender.sendMessage("§dColors §5- §dPage 1 of 2 §5- §7/colors <page>");
				sender.sendMessage("  §0&0 Black");
				sender.sendMessage("  §1&1 Dark Blue");
				sender.sendMessage("  §2&2 Dark Green");
				sender.sendMessage("  §3&3 Dark Aqua");
				sender.sendMessage("  §4&4 Dark Red");
				sender.sendMessage("  §5&5 Dark Purple");
				sender.sendMessage("  §6&6 Gold");
				sender.sendMessage("  §7&7 Gray");
				sender.sendMessage("  §8&8 Dark Grey");
			} else if (args[0].equals("2")) {
				sender.sendMessage("§dColors §5- §dPage 2 of 2 §5- §7/colors <page>");
				sender.sendMessage("  §9&9 Blue");
				sender.sendMessage("  §a&a Green");
				sender.sendMessage("  §b&b Aqua");
				sender.sendMessage("  §c&c Red");
				sender.sendMessage("  §d&d Light Purple");
				sender.sendMessage("  §e&e Yellow");
				sender.sendMessage("  §f&f White");
			}
			return true;
		}
		//
		// Promote Command
		//
		if (command.getName().equalsIgnoreCase("promote")) {
			if (args.length == 1) {
				if (isProfileExistant(args[0])) {
					if (getPlayer(args[0]) != null) {
						if (sender instanceof Player && getProfile(args[0]).rank.getNextRank().isHigher(Rank.MODERATOR) && !getProfile(sender).rank.isHigher(Rank.ELITE)) {
							sender.sendMessage("§7You can't promote a player to above architect rank");
						} else if (sender instanceof Player && getProfile(args[0]).rank.getNextRank().isHigher(Rank.POLICE) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
							sender.sendMessage("§7You can't promote a player to above engineer rank");
						} else if (sender instanceof Player && getProfile(args[0]).rank.getNextRank().isHigher(Rank.STAFF_DIAMOND) && getProfile(sender).rank != Rank.SERVER_HOST) {
							sender.sendMessage("§7You can't promote a player to above diamond staff rank");
						} else {
							//getServer().getPlayer(args[0]).getInventory().addItem(Rank.getUnlocksBook(getProfile(args[0]).rank, getProfile(args[0]).rank.getNextRank()));
							getProfile(args[0]).rank = getProfile(args[0]).rank.getNextRank();
							if (getProfile(args[0]).rank.equals(Rank.STAFF_COPPER)) {
								getServer().getOfflinePlayer(args[0]).setOp(true);
							} else if (getProfile(args[0]).rank.equals(Rank.ADMIN)) {
								// Player profile type conversion
								if (getProfile(args[0]) instanceof PlayerProfileNormal) {
									getProfile(args[0]).saveProfile();
									playerProfiles.remove(getPlayer(args[0]).getName().toLowerCase(Locale.UK));
									playerProfiles.put(getPlayer(args[0]).getName().toLowerCase(Locale.UK), new PlayerProfileAdmin(this, getPlayer(args[0]), false));
								}
								//
							}
							// Set nametag color
							getProfile(args[0]).updateNametag("§" + getProfile(getPlayer(args[0])).rank.getColor((getProfile(getPlayer(args[0])))), null);
							//
							getProfile(args[0]).updatePlayerListName();
							broadcastPlayerMessage(getPlayer(args[0]).getName(), "§c" + getPlayer(args[0]).getDisplayName() + " §dhas been promoted to " + getProfile(args[0]).rank.getName() + " rank");
						}
					} else {
						if (sender instanceof Player && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
							sender.sendMessage("§7You can't promote offline players");
							return true;
						}
						if (SQL.getRank(args[0]).getNextRank().equals(Rank.STAFF_COPPER)) getServer().getOfflinePlayer(args[0]).setOp(true);
						SQL.setRank(args[0], SQL.getRank(args[0]).getNextRank());
						broadcastPlayerMessage(getServer().getOfflinePlayer(args[0]).getName(), "§c" + getServer().getOfflinePlayer(args[0]).getName() + " §dhas been promoted to " + SQL.getRank(args[0]).getName() + " rank");
					}
				} else {
					sender.sendMessage("§7You can't promote a player who doesn't exist");
				}
			} else if (args.length == 2) {
				if (isProfileExistant(args[0])) {
					if (Rank.contains(args[1].toUpperCase())) {
						Rank rank = Rank.valueOf(args[1].toUpperCase());
						if (getPlayer(args[0]) != null) {
							if (sender instanceof Player && rank.isHigher(Rank.MODERATOR) && !getProfile(sender).rank.isHigher(Rank.ELITE)) {
								sender.sendMessage("§7You can't promote a player to above architect");
							} else if (sender instanceof Player && rank.isHigher(Rank.POLICE) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
								sender.sendMessage("§7You can't promote a player to above engineer");
							} else {
								getProfile(args[0]).rank = rank;
								if (rank.isHigher(Rank.POLICE) && !getServer().getOfflinePlayer(args[0]).isOp()) {
									getServer().getOfflinePlayer(args[0]).setOp(true);
								} else if (rank.isHigher(Rank.STAFF_DIAMOND) && getProfile(args[0]) instanceof PlayerProfileNormal) {
									// Player profile type conversion
									getProfile(args[0]).saveProfile();
									playerProfiles.remove(getPlayer(args[0]).getName().toLowerCase(Locale.UK));
									playerProfiles.put(getPlayer(args[0]).getName().toLowerCase(Locale.UK), new PlayerProfileAdmin(this, getPlayer(args[0]), false));
									//
								}
								// Set nametag color
								getProfile(args[0]).updateNametag("§" + getProfile(getPlayer(args[0])).rank.getColor((getProfile(getPlayer(args[0])))), null);
								//
								getProfile(args[0]).updatePlayerListName();
								broadcastPlayerMessage(getPlayer(args[0]).getName(), "§c" + getPlayer(args[0]).getDisplayName() + " §dhas been promoted to " + rank.getName() + " rank");
							}
						} else {
							if (sender instanceof Player && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
								sender.sendMessage("§7You can't promote offline players");
								return true;
							}
							if (rank.isHigher(Rank.POLICE) && !getServer().getOfflinePlayer(args[0]).isOp()) getServer().getOfflinePlayer(args[0]).setOp(true);
							SQL.setRank(args[0], rank);
							broadcastPlayerMessage(getServer().getOfflinePlayer(args[0]).getName(), "§c" + getServer().getOfflinePlayer(args[0]).getName() + " §dhas been promoted to " + SQL.getRank(args[0]).getName() + " rank");
						}
					} else {
						sender.sendMessage("§5This rank doesn't exist");
						String rankNames = "";
						for (Rank rank : Rank.values()) rankNames += rank.name().toLowerCase() + " ";
						sender.sendMessage("§dRanks: ");
						sender.sendMessage(ChatColor.LIGHT_PURPLE + rankNames);
					}
				} else {
					sender.sendMessage("§7You can't promote a player who doesn't exist");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(sender, "/promote [player] <rank>");
			}
			return true;
		}
		//-------------------------------------------------------------------------------------------------------------------------------
		// COMMANDS BELOW THIS POINT ARE NOT AVAILABLE VIA CONSOLE
		//-------------------------------------------------------------------------------------------------------------------------------
		//
		// Check to see if the sender is a player
		//
		if (sender instanceof Player == false) {
			logInfo("This command is not supported by the console");
			return true;
		}
		final Player player = (Player)sender;
		//
		// Claim Command
		//
		if (command.getName().equalsIgnoreCase("claim")) {
			if (isInPlotWorld(player)) {
				if (Regions.isInPlotworldRegion(player.getLocation())) {
					if (Regions.isInProtectedPlotworldRegion(player.getLocation(), player)) {
						player.sendMessage(ChatColor.GRAY + "This plot has already been claimed");
					} else {
						player.sendMessage(ChatColor.GRAY + "You already have permission to build here");
					}
				} else {
					if (getProfile(sender).money >= 100) {
						Location pointA = new Location(player.getWorld(), roundDown((long) player.getLocation().getX() + 4, 41) - 4, 0, roundDown((long) player.getLocation().getZ() + 4, 41) - 4);
						Location pointB = new Location(player.getWorld(), roundUp((long) player.getLocation().getX() + 4, 41) - 4, player.getWorld().getMaxHeight(), roundUp((long) player.getLocation().getZ() + 4, 41) - 4);
						Region region = new Region("PlotRegion" + pointA.getBlockX() + "," + pointA.getBlockZ() + "," + pointB.getBlockX() + "," + pointB.getBlockZ(),
								pointA,
								pointB,
								true,
								sender.getName(),
								null,
								null,
								null,
								null);
						region.saveRegion();
						plotRegionList.add(region);
						getProfile(sender).money -= 100;
						incrementOwnerBalance(100);
						sender.sendMessage(ChatColor.GRAY + "You have successfully purchased this plot " + ChatColor.RED + "-$100");
					} else {
						sender.sendMessage("§5You don't have enough money to purchase this region");
						sender.sendMessage("§dYou need to earn $" + (100 - getProfile(sender).money));
					}
				}
			} else {
				player.sendMessage(ChatColor.GRAY + "You have to be in the plot world to use this command");
			}
			return true;
		}
		//
		// Feedback Command
		//
		if (command.getName().equalsIgnoreCase("feedback")) {
			if (args.length > 0) {
				StringBuilder message = new StringBuilder();
				for (int i = 0; i < args.length; i++) message.append(" " + args[i].replaceAll("'", "''"));
				SQL.insert(TableType.Mail, "'" + player.getName() + "','" + getServer().getOfflinePlayer(EvilBook.config.getProperty("server_host")).getUniqueId().toString() + "','" + message + "','" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "'");
				sender.sendMessage("§7Message sent to the server host");
				sender.sendMessage("§7Thank you for your feedback");
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/feedback [message]");
			}
			return true;
		}
		//
		// Workbench Command
		//
		if (command.getName().equalsIgnoreCase("workbench")) {
			if ((!isInSurvival(player) && !isInMinigame(player, MinigameType.SKYBLOCK)) || getProfile(player).rank.isAdmin()) {
				player.openWorkbench(null, true);
			} else {
				sender.sendMessage("§dAdmin rank is required to use this command in survival");
				player.sendMessage("§dPlease type §6/admin §dto learn how to become admin");
			}
			return true;
		}
		//
		// Repair Command
		//
		if (command.getName().equalsIgnoreCase("repair")) {
			if ((!isInSurvival(player) && !isInMinigame(player, MinigameType.SKYBLOCK)) || getProfile(player).rank == Rank.SERVER_HOST) {
				player.getItemInHand().setDurability((short) 0);
				sender.sendMessage("§7Your item has been fully repaired");
			} else {
				player.sendMessage("§7This command can't be used in survival");
			}
			return true;
		}
		//
		// Feed Command
		//
		if (command.getName().equalsIgnoreCase("feed")) {
			if ((!isInSurvival(player) && !isInMinigame(player, MinigameType.SKYBLOCK)) || getProfile(player).rank == Rank.SERVER_HOST) {
				player.setFoodLevel(20);
				sender.sendMessage("§7You have been fully fed");
			} else {
				player.sendMessage("§7This command can't be used in survival");
			}
			return true;
		}
		//
		// Heal Command
		//
		if (command.getName().equalsIgnoreCase("heal")) {
			if ((!isInSurvival(player) && !isInMinigame(player, MinigameType.SKYBLOCK)) || getProfile(player).rank == Rank.SERVER_HOST) {
				player.setHealth(player.getMaxHealth());
				sender.sendMessage("§7You have been fully healed");
			} else {
				player.sendMessage("§7This command can't be used in survival");
			}
			return true;
		}
		//
		// Drug Command
		//
		if (command.getName().equalsIgnoreCase("drug")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("cocain")) {
					ItemStack cocain = new ItemStack(Material.SUGAR);
					ItemMeta meta = cocain.getItemMeta();
					meta.setDisplayName("Cocain");
					meta.setLore(Arrays.asList("Ruff stuff"));
					cocain.setItemMeta(meta);
					player.getInventory().addItem(cocain);
				} else if (args[0].equalsIgnoreCase("shrooms")) {
					ItemStack shrooms = new ItemStack(Material.RED_MUSHROOM);
					ItemMeta meta = shrooms.getItemMeta();
					meta.setDisplayName("Hallucinogenic Mushroom");
					meta.setLore(Arrays.asList("Shroooms!"));
					shrooms.setItemMeta(meta);
					player.getInventory().addItem(shrooms);
				} else if (args[0].equalsIgnoreCase("alcohol")) {
					ItemStack alcohol = new ItemStack(Material.POTION);
					ItemMeta meta = alcohol.getItemMeta();
					meta.setDisplayName("Alcohol");
					meta.setLore(Arrays.asList("Hick!"));
					alcohol.setItemMeta(meta);
					player.getInventory().addItem(alcohol);
				} else {
					sender.sendMessage("§7This drug doesn't exist");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/drug cocain",
								"/drug shrooms",
								"/drug alcohol"));
			}
			return true;
		}
		//
		// Statistics Command
		//
		if (command.getName().equalsIgnoreCase("statistics") || command.getName().equalsIgnoreCase("stats")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("server")) {
					sender.sendMessage("§5Server General Statistics");
					sender.sendMessage("§dEconomic growth = $" + GlobalStatistic.getStatistic(GlobalStatistic.EconomyGrowth) + " today §7$" + SQL.getColumnSum(TableType.Statistics, "economy_growth") + " total");
					sender.sendMessage("§dEconomic trade = $" + GlobalStatistic.getStatistic(GlobalStatistic.EconomyTrade) + " today §7$" + SQL.getColumnSum(TableType.Statistics, "economy_trade") + " total");
					sender.sendMessage("§dPlayer logins = " + GlobalStatistic.getStatistic(GlobalStatistic.LoginTotal) + " today §7" + SQL.getColumnSum(TableType.Statistics, "login_total") + " total");
					sender.sendMessage("§dNew players = " + GlobalStatistic.getStatistic(GlobalStatistic.LoginNewPlayers) + " today");
					sender.sendMessage("§dTotal unique players = " + SQL.getRowCount(TableType.PlayerProfile));
					sender.sendMessage("§dCommands executed = " + GlobalStatistic.getStatistic(GlobalStatistic.CommandsExecuted) + " today §7" + SQL.getColumnSum(TableType.Statistics, "commands_executed") + " total");
					sender.sendMessage("§dMessages sent = " + GlobalStatistic.getStatistic(GlobalStatistic.MessagesSent) + " today §7" + SQL.getColumnSum(TableType.Statistics, "messages_sent") + " total");
					sender.sendMessage("§dBlocks broken = " + GlobalStatistic.getStatistic(GlobalStatistic.BlocksBroken) + " today §7" + SQL.getColumnSum(TableType.Statistics, "blocks_broken") + " total");
					sender.sendMessage("§dBlocks placed = " + GlobalStatistic.getStatistic(GlobalStatistic.BlocksPlaced) + " today §7" + SQL.getColumnSum(TableType.Statistics, "blocks_placed") + " total");
				} else if (args[0].equalsIgnoreCase("player")) {
					sender.sendMessage("§5" + player.getName() + "'s General Statistics");
					sender.sendMessage("§dMoney = $" + SQL.getInteger(TableType.PlayerProfile, player.getName(), "money"));
					sender.sendMessage("§dTotal logins = " + SQL.getInteger(TableType.PlayerProfile, player.getName(), "total_logins"));
					sender.sendMessage("§dLast login = " + new SimpleDateFormat("dd-MM-yyyy").format(new Date(player.getLastPlayed())));
				} else if (args[0].equalsIgnoreCase("survival")) {
					List<String> text = new ArrayList<>();
					text.add("§5Server Survival Statistics\n\n§7Page 1 - Mined ores\n§7Page 2 - Mob kills\n§7Page 3 - Mob kills");
					text.add("§5§oTotal ores mined\n\n§dCoal = " + SQL.getColumnSum(TableType.PlayerStatistics, "mined_coal") + "\n" +
							"§dIron = " + SQL.getColumnSum(TableType.PlayerStatistics, "mined_iron") + "\n" +
							"§dRedstone = " + SQL.getColumnSum(TableType.PlayerStatistics, "mined_redstone") + "\n" +
							"§dLapis Lazuli = " + SQL.getColumnSum(TableType.PlayerStatistics, "mined_lapis") + "\n" +
							"§dQuartz = " + SQL.getColumnSum(TableType.PlayerStatistics, "mined_netherquartz") + "\n" +
							"§dGold = " + SQL.getColumnSum(TableType.PlayerStatistics, "mined_gold") + "\n" +
							"§dDiamond = " + SQL.getColumnSum(TableType.PlayerStatistics, "mined_diamond") + "\n" +
							"§dEmerald = " + SQL.getColumnSum(TableType.PlayerStatistics, "mined_emerald") + "\n");
					text.add("§5§oTotal mobs killed\n\n§dPigs = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_pigs") + "\n" +
							"§dVillagers = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_villagers") + "\n" +
							"§dCave Spiders = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_cavespiders") + "\n" +
							"§dEndermen = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_endermen") + "\n" +
							"§dSpiders = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_spiders") + "\n" +
							"§dWolves = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_wolves") + "\n" +
							"§dZombie Pigmen = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_zombiepigs") + "\n" +
							"§dBlazes = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_blazes") + "\n" + 
							"§dCreepers = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_creepers") + "\n" +
							"§dGhasts = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_ghasts") + "\n" +
							"§dMagmacubes = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_magmacubes") + "\n");
					text.add("§5§oTotal mobs killed\n\n§dSilverfish = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_silverfish") + "\n" +
							"§dSkeletons = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_skeletons") + "\n" +
							"§dSlimes = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_slimes") + "\n" +
							"§dWitches = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_witches") + "\n" +
							"§dZombies = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_zombies") + "\n" +
							"§dEnder Dragons = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_enderdragons") + "\n" +
							"§dWithers = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_withers") + "\n" +
							"§dPlayers = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_players") + "\n" +
							"§dRare Mobs = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_rares"));
					player.getInventory().addItem(getBook("Server Survival Statistics", config.getProperty("server_name"), text));
					sender.sendMessage("§7Please check your inventory for the survival statistics guide");
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("player")) {
					OfflinePlayer statPlayer = getServer().getOfflinePlayer(args[1]);
					if (statPlayer.hasPlayedBefore()) {
						sender.sendMessage("§5" + statPlayer.getName() + "'s General Statistics");
						sender.sendMessage("§dMoney = $" + SQL.getInteger(TableType.PlayerProfile, statPlayer.getName(), "money"));
						sender.sendMessage("§dTotal logins = " + SQL.getInteger(TableType.PlayerProfile, statPlayer.getName(), "total_logins"));
						sender.sendMessage("§dLast login = " + new SimpleDateFormat("dd-MM-yyyy").format(new Date(statPlayer.getLastPlayed())));
					} else {
						sender.sendMessage("§7Statistics for this player weren't found");
					}
				} else if (args[0].equalsIgnoreCase("survival")) {
					OfflinePlayer statPlayer = getServer().getOfflinePlayer(args[1]);
					if (statPlayer.hasPlayedBefore()) {
						List<String> text = new ArrayList<>();
						text.add("§5" + statPlayer.getName() + "'s Survival Statistics\n\n§7Page 1 - Mined ores\n§7Page 2 - Mob kills\n§7Page 3 - Mob kills");
						text.add("§5§oTotal ores mined\n\n§dCoal = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.MINED_COAL) + "\n" +
								"§dIron = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.MINED_IRON) + "\n" +
								"§dRedstone = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.MINED_REDSTONE) + "\n" +
								"§dLapis Lazuli = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.MINED_LAPIS) + "\n" +
								"§dQuartz = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.MINED_NETHERQUARTZ) + "\n" +
								"§dGold = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.MINED_GOLD) + "\n" +
								"§dDiamond = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.MINED_DIAMOND) + "\n" +
								"§dEmerald = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.MINED_EMERALD) + "\n");
						text.add("§5§oTotal mobs killed\n\n§dPigs = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_PIGS) + "\n" +
								"§dVillagers = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_VILLAGERS) + "\n" +
								"§dCave Spiders = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_CAVESPIDERS) + "\n" +
								"§dEndermen = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_ENDERMEN) + "\n" +
								"§dSpiders = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_SPIDERS) + "\n" +
								"§dWolves = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_WOLVES) + "\n" +
								"§dZombie Pigmen = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_ZOMBIEPIGS) + "\n" +
								"§dBlazes = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_BLAZES) + "\n" + 
								"§dCreepers = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_CREEPERS) + "\n" +
								"§dGhasts = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_GHASTS) + "\n" +
								"§dMagmacubes = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_MAGMACUBES) + "\n");
						text.add("§5§oTotal mobs killed\n\n§dSilverfish = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_SILVERFISH) + "\n" +
								"§dSkeletons = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_SKELETONS) + "\n" +
								"§dSlimes = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_SLIMES) + "\n" +
								"§dWitches = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_WITCHES) + "\n" +
								"§dZombies = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_ZOMBIES) + "\n" +
								"§dEnder Dragons = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_ENDERDRAGONS) + "\n" +
								"§dWithers = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_WITHERS) + "\n" +
								"§dPlayers = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_PLAYERS) + "\n" +
								"§dRare Mobs = " + PlayerStatistic.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_RARES));
						player.getInventory().addItem(getBook(statPlayer.getName() + "'s Survival Statistics", config.getProperty("server_name"), text));
						sender.sendMessage("§7Please check your inventory for the survival statistics guide");
					} else {
						sender.sendMessage("§7Statistics for this player weren't found");
					}
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/" + command.getName().toLowerCase(Locale.UK) + " server",
								"/" + command.getName().toLowerCase(Locale.UK) + " player [player]",
								"/" + command.getName().toLowerCase(Locale.UK) + " survival <player>"));
			}
			return true;
		}
		//
		// Title Command
		//
		if (command.getName().equalsIgnoreCase("title")) {
			if (args.length == 0) {
				//
				// List the players unlocked titles
				//
				sender.sendMessage("§5My unlocked titles");
				String titles = getUnlockedTitles(player);
				sender.sendMessage("§d" + (titles.equals("") ? "You haven't unlocked any titles" : titles));
				sender.sendMessage("§7§oSet a title using /title [title]");
			} else if (args.length >= 1) {
				//
				// Player is trying to set a title
				//
				if (args[0].equalsIgnoreCase("remove")) {
					getProfile(player).setNameTitle(null);
					sender.sendMessage("§7You have removed your title");
				} else {
					for (String title : getUnlockedTitles(player).split(" ")) {
						if (args[0].equalsIgnoreCase(title)) {
							getProfile(player).setNameTitle(title);
							sender.sendMessage("§7You have changed your title to §d" + title);
							return true;
						}
					}
					if (!getProfile(player).rank.isHigher(Rank.ADMIN)) {
						sender.sendMessage("§5This title doesn't exist or hasn't been unlocked");
						sender.sendMessage("§dYou can't use this title, councillor rank is required to use custom titles");
						sender.sendMessage("§dTo view the titles you have unlocked please see /title");
					} else {		
						String title = "";
						for (String arg : args) title += arg + " ";
						title = title.trim();
						String formattedTitle = toFormattedString(title);
						if (ChatColor.stripColor(formattedTitle).length() <= 16) {
							if (title.toLowerCase(Locale.UK).contains("&k")) {
								sender.sendMessage("§7Titles can't contain &k");
							} else {
								getProfile(player).setNameTitle(formattedTitle);
								sender.sendMessage("§7You have changed your title to §d" + formattedTitle);
							}
						} else {
							sender.sendMessage("§7The maximum title length excluding colors is 16 characters");
						}
					}
				}	
			} else {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/title",
								"/title [title]",
								"/title remove"));
			}
			return true;
		}
		//
		// More Command
		//
		if (command.getName().equalsIgnoreCase("more")) {
			player.getItemInHand().setAmount(player.getItemInHand().getMaxStackSize());
			return true;
		}
		//
		// Hyperhorse Command
		//
		if (command.getName().equalsIgnoreCase("hyperhorse")) {
			if (player.getVehicle() != null && player.getVehicle() instanceof Horse) {
				Horse entity = (Horse)player.getVehicle();
				entity.setMaxHealth(Double.MAX_VALUE);
				entity.setCustomName("Hyperhorse");
				entity.setAgeLock(true);
				entity.setHealth(entity.getMaxHealth());
				entity.setJumpStrength(2);
				entity.setVariant(Variant.SKELETON_HORSE);
				entity.setTamed(true);
			} else {
				sender.sendMessage("§7You have to be riding a horse to make it a hyperhorse");
			}
			return true;
		}
		//
		// Mail Command
		//
		if (command.getName().equalsIgnoreCase("mail")) {
			if (args.length == 0) {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/mail inbox",
								"/mail send [player] [message]"));
			} else if (args[0].equalsIgnoreCase("inbox")) {
				getProfile(sender).viewMailInbox();
			} else if (args[0].equalsIgnoreCase("send")) {
				if (args.length > 2) {
					if (isProfileExistant(args[1])) {
						StringBuilder message = new StringBuilder();
						for (int i = 2; i < args.length; i++) message.append(" " + args[i].replaceAll("'", "''"));
						SQL.insert(TableType.Mail, "'" + player.getName() + "','" + getServer().getOfflinePlayer(args[1]).getUniqueId().toString() + "','" + message + "','" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "'");
						sender.sendMessage("§7Message sent");
					} else {
						sender.sendMessage("§7The recipient player doesn't exist");
					}
				} else {
					ChatExtensions.sendCommandHelpMessage(player, "/mail send [player] [message]");
				}
			}
			return true;
		}
		//
		// Inbox Command
		//
		if (command.getName().equalsIgnoreCase("inbox")) {
			getProfile(sender).viewMailInbox();
			return true;
		}
		//
		// Skyblock Command
		//
		if (command.getName().equalsIgnoreCase("skyblock")) {
			File skyblockMap = new File("plugins/EvilBook/SkyBlock/" + player.getUniqueId() + "/");
			if (skyblockMap.exists()) {
				WorldCreator skyblockWorld = new WorldCreator("plugins/EvilBook/SkyBlock/" + player.getUniqueId());
				getServer().createWorld(skyblockWorld);
				player.teleport(new Location(getServer().getWorld("plugins/EvilBook/SkyBlock/" + player.getUniqueId()), 0.5, 67, -2.5));
			} else {
				File template = new File("plugins/EvilBook/SkyBlock/Template");
				try {
					FileUtils.copyDirectory(template, skyblockMap);
					getServer().getScheduler().runTaskLater(this, new Runnable() {
						@Override
						public void run() {
							WorldCreator skyblockWorld = new WorldCreator("plugins/EvilBook/SkyBlock/" + player.getUniqueId());
							getServer().createWorld(skyblockWorld);
							player.teleport(new Location(getServer().getWorld("plugins/EvilBook/SkyBlock/" + player.getUniqueId()), 0.5, 67, -2.5));
						}
					}, 1);
				} catch (IOException e) {
					e.printStackTrace();
					sender.sendMessage("§cFailed to load minigame");
				}
			}
			return true;
		}
		//
		// Minigame Command
		//
		if (command.getName().equalsIgnoreCase("minigame")) {
			if (args.length != 1) {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/minigame skyBlock",
								"/minigame towerDefense"));
			} else if (args[0].equalsIgnoreCase("skyBlock")) {
				File skyblockMap = new File("plugins/EvilBook/SkyBlock/" + player.getUniqueId() + "/");
				if (skyblockMap.exists()) {
					WorldCreator skyblockWorld = new WorldCreator("plugins/EvilBook/SkyBlock/" + player.getUniqueId());
					getServer().createWorld(skyblockWorld);
					player.teleport(new Location(getServer().getWorld("plugins/EvilBook/SkyBlock/" + player.getUniqueId()), 0.5, 67, -2.5));
				} else {
					File template = new File("plugins/EvilBook/SkyBlock/Template");
					try {
						FileUtils.copyDirectory(template, skyblockMap);
						getServer().getScheduler().runTaskLater(this, new Runnable() {
							@Override
							public void run() {
								WorldCreator skyblockWorld = new WorldCreator("plugins/EvilBook/SkyBlock/" + player.getUniqueId());
								getServer().createWorld(skyblockWorld);
								player.teleport(new Location(getServer().getWorld("plugins/EvilBook/SkyBlock/" + player.getUniqueId()), 0.5, 67, -2.5));
							}
						}, 1);
					} catch (IOException e) {
						e.printStackTrace();
						sender.sendMessage("§cFailed to load minigame");
					}
				}
			} else if (args[0].equalsIgnoreCase("towerDefense")) {
				if (getProfile(sender).rank != Rank.SERVER_HOST) {
					sender.sendMessage("§cThis minigame is currently in beta testing");
					sender.sendMessage("§cIt will be available to the public shortly");
				} else {
					getProfile(sender).towerDefenseMinigame = new TowerDefenseMinigame(player, MinigameDifficulty.NORMAL, this);
					player.teleport(getServer().getWorld("Minigame").getSpawnLocation());
				}
			} else {
				sender.sendMessage("§5§oPlease select a minigame to play");
				sender.sendMessage("§7The minigame you entered doesn't exist");
				ChatExtensions.sendClickableMessage(player, "§d/minigame skyBlock", EnumClickAction.SUGGEST_COMMAND, "/minigame skyBlock");
			}
			return true;
		}
		//
		// Reset Command
		//
		if (command.getName().equalsIgnoreCase("reset")) {
			for (Player p : getServer().getWorld("plugins/EvilBook/SkyBlock/" + player.getUniqueId()).getPlayers()) {
				p.teleport(getServer().getWorld(Bukkit.getWorlds().get(0).getName()).getSpawnLocation());
			}
			getServer().unloadWorld("plugins/EvilBook/SkyBlock/" + player.getUniqueId(), false);
			SQL.setString(TableType.PlayerProfile, player.getName(), "inventory_skyblock", "NULL");
			File skyblockMap = new File("plugins/EvilBook/SkyBlock/" + player.getUniqueId() + "/");
			try {
				FileUtils.deleteDirectory(skyblockMap);
				sender.sendMessage("§7The minigame map has been reset");
			} catch (IOException e) {
				e.printStackTrace();
				sender.sendMessage("§cFailed to reset minigame map");
			}
			return true;
		}
		//
		// Message Command
		//
		if (command.getName().equalsIgnoreCase("msg")) {
			if (args.length > 1) {
				if (getPlayer(args[0]) != null) {
					if (!getProfile(getPlayer(args[0])).isMuted(player.getName())) {
						StringBuilder message = new StringBuilder();
						for (int i = 1; i < args.length; i++) message.append(" " + args[i]);
						sender.sendMessage("§7To " + getPlayer(args[0]).getDisplayName() + "§7:§f" + EvilBook.toFormattedString(message.toString()));
						getPlayer(args[0]).sendMessage("§7From " + ((Player) sender).getDisplayName() + "§7:§f" + EvilBook.toFormattedString(message.toString()));
						getProfile(player).lastMsgPlayer = getPlayer(args[0]).getName();
						getProfile(args[0]).lastMsgPlayer = sender.getName();
					} else {
						sender.sendMessage("§7You can't message a player who has muted you");
					}
				} else {
					sender.sendMessage("§7You can't message an offline player");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/msg [player] [message]");
			}
			return true;
		}
		//
		// Reply Command
		//
		if (command.getName().equalsIgnoreCase("r") || command.getName().equalsIgnoreCase("reply")) {
			if (getProfile(player).lastMsgPlayer != null) {
				if (getServer().getPlayer(getProfile(player).lastMsgPlayer) != null) {
					if (!getProfile(getPlayer(getProfile(player).lastMsgPlayer)).isMuted(player.getName())) {
						StringBuilder message = new StringBuilder();
						for (String msg : args) message.append(" " + msg);
						sender.sendMessage("§7To " + getServer().getPlayer(getProfile(player).lastMsgPlayer).getDisplayName() + "§7:§f" + EvilBook.toFormattedString(message.toString()));
						getServer().getPlayer(getProfile(player).lastMsgPlayer).sendMessage("§7From " + ((Player) sender).getDisplayName() + "§7:§f" + EvilBook.toFormattedString(message.toString()));
					} else {
						sender.sendMessage("§7You can't message a player who has muted you");
					}
				} else {
					sender.sendMessage("§7You can't message an offline player");
				}
			} else {
				player.sendMessage("§7There is no message to reply to");
			}
			return true;
		}
		//
		// Troll Command
		//
		if (command.getName().equalsIgnoreCase("troll")) {
			if (player.getName().equals(EvilBook.config.getProperty("server_host"))) {
				StringBuilder broadcast = new StringBuilder();
				for (int i = 1; i < args.length; i++) broadcast.append(" " + args[i]);
				for (Player p : getServer().getOnlinePlayers()) {
					if (getProfile(p) != getProfile(args[0])) {
						p.sendMessage(getProfile(args[0]).rank.getPrefix(getProfile(args[0])) + " §" + getProfile(args[0]).rank.getColor(getProfile(args[0])) 
								+ "<§f" + getPlayer(args[0]).getDisplayName() + "§" + getProfile(args[0]).rank.getColor(getProfile(args[0])) 
								+ ">§f" + toFormattedString(broadcast.toString()));
					}
				}
			}
			return true;
		}
		//
		// Tool Command
		//
		if (command.getName().equalsIgnoreCase("tool")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("none")) {
					getProfile(player).wandMode = EditWandMode.None;
					player.sendMessage("§7Edit wand disabled");
				} else if (args[0].equalsIgnoreCase("selection")) {
					getProfile(player).wandMode = EditWandMode.Selection;
					player.sendMessage("§7Edit wand in selection mode");
				} else if (args[0].equalsIgnoreCase("tree")) {
					getProfile(player).wandMode = EditWandMode.Tree;
					player.sendMessage("§7Edit wand in tree mode");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/tool none",
								"/tool selection",
								"/tool tree"));
			}
			return true;
		}
		//
		// Effect Command
		//
		if (command.getName().equalsIgnoreCase("effect")) {
			if (args.length == 0) {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/effect remove",
								"/effect [effect]",
								"/effect [effect] [frequency]",
								"/effect [effect] [frequency] [amount]"));
				String effects = "";
				for (EmitterEffect effectType : EmitterEffect.values()) effects += effectType.name().toLowerCase() + " ";
				//TODO: ChatExtensions on each effect listed
				sender.sendMessage("§7Effects: " + effects);
			} else if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("remove")) {
					Location emitterLocation = player.getLocation();
					SQL.deleteRowFromCriteria(TableType.Emitter, "world='" + emitterLocation.getWorld().getName() + 
							"' AND x='" + emitterLocation.getBlockX() + "' AND y='" + emitterLocation.getBlockY() + "' AND z='" + 
							emitterLocation.getBlockZ() + "'");
					Iterator<Emitter> emit = EvilBook.emitterList.iterator();
					while (emit.hasNext()) {
						Emitter emitter = emit.next();
						if (emitter.location.getBlock().equals(emitterLocation.getBlock())) {
							emit.remove();
							sender.sendMessage("§7Effect emitter removed");
							break;
						}
					}
				} else {
					if (EmitterEffect.contains(args[0])) {
						EmitterEffect effect = EmitterEffect.parse(args[0]);
						if (getProfile(player).rank.isHigher(effect.minimumRank.getPreviousRank())) {
							if (args.length == 1) {
								Emitter emitter = new Emitter(player.getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5), effect, 1, 6);
								emitterList.add(emitter);
								emitter.save();
								sender.sendMessage("§7Created " + effect.name() + " effect");
								sender.sendMessage("§7It can be removed by standing on top of it and using /effect remove");
							} else if (args.length == 2) {
								if (isInteger(args[1])) {
									Emitter emitter = new Emitter(player.getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5), effect, 1, Integer.parseInt(args[1]));
									emitterList.add(emitter);
									emitter.save();
									sender.sendMessage("§7Created " + effect.name() + " effect");
									sender.sendMessage("§7It can be removed by standing on top of it and using /effect remove");
								} else {
									sender.sendMessage("§5Please enter a valid frequency");
								}
							} else if (args.length == 3) {
								if (isInteger(args[1]) && isInteger(args[2])) {
									Emitter emitter = new Emitter(player.getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5), effect, Integer.parseInt(args[2]), Integer.parseInt(args[1]));
									emitterList.add(emitter);
									emitter.save();
									sender.sendMessage("§7Created " + effect.name() + " effect");
									sender.sendMessage("§7It can be removed by standing on top of it and using /effect remove");
								} else {
									sender.sendMessage("§5Please enter a valid frequency and amount");
								}
							} else {
								ChatExtensions.sendCommandHelpMessage(player, 
										Arrays.asList("/effect " + effect.name(),
												"/effect " + effect.name() + " [frequency]",
												"/effect " + effect.name() + " [frequency] [amount]"));
							}
						} else {
							sender.sendMessage("§5You have to be a higher rank");
							sender.sendMessage("§d" + effect.minimumRank.getName() + " rank is required to create this effect");
						}
					} else {
						ChatExtensions.sendCommandHelpMessage(player, 
								Arrays.asList("/effect [effect]",
										"/effect [effect] [frequency]",
										"/effect [effect] [frequency] [amount]"));
						String effects = "";
						for (EmitterEffect effectType : EmitterEffect.values()) effects += effectType.name().toLowerCase() + " ";
						//TODO: ChatExtensions on each effect listed
						sender.sendMessage("§7Effects: " + effects);
					}
				}
			}
			return true;
		}
		//
		// Chimney Command
		//
		if (command.getName().equalsIgnoreCase("chimney")) {
			if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
				if (args.length == 0) {
					Emitter chimney = new Emitter(player.getLocation(), EmitterEffect.Smoke, 4, 2);
					emitterList.add(chimney);
					chimney.save();
					sender.sendMessage("§7Created chimney");
					sender.sendMessage("§7It can be removed by standing on top of it and using /effect remove");
				} else if (args.length == 1) {
					if (isInteger(args[0])) {
						Emitter chimney = new Emitter(player.getLocation(), EmitterEffect.Smoke, Integer.parseInt(args[0]), 2);
						emitterList.add(chimney);
						chimney.save();
						sender.sendMessage("§7Created chimney");
						sender.sendMessage("§7It can be removed by standing on top of it and using /effect remove");
					} else {
						sender.sendMessage("§5Please enter a valid direction");
						sender.sendMessage("§d0 - South East");
						sender.sendMessage("§d1 - South");
						sender.sendMessage("§d2 - South West");
						sender.sendMessage("§d3 - East");
						sender.sendMessage("§d4 - Up");
						sender.sendMessage("§d5 - West");
						sender.sendMessage("§d6 - North East");
						sender.sendMessage("§d7 - North");
						sender.sendMessage("§d8 - North West");
					}
				} else if (args.length == 2) {
					if (isInteger(args[0]) && isInteger(args[1]) && Integer.parseInt(args[1]) >= 0) {
						Emitter chimney = new Emitter(player.getLocation(), EmitterEffect.Smoke, Integer.parseInt(args[0]), Integer.parseInt(args[1]));
						emitterList.add(chimney);
						chimney.save();
						sender.sendMessage("§7Created chimney");
						sender.sendMessage("§7It can be removed by standing on top of it and using /effect remove");
					} else {
						sender.sendMessage("§5Please enter a valid direction and frequency");
						sender.sendMessage("§d0 - South East");
						sender.sendMessage("§d1 - South");
						sender.sendMessage("§d2 - South West");
						sender.sendMessage("§d3 - East");
						sender.sendMessage("§d4 - Up");
						sender.sendMessage("§d5 - West");
						sender.sendMessage("§d6 - North East");
						sender.sendMessage("§d7 - North");
						sender.sendMessage("§d8 - North West");
					}
				} else {
					ChatExtensions.sendCommandHelpMessage(player, 
							Arrays.asList("/chimney",
									"/chimney [direction]",
									"/chimney [direction] [frequency]"));
				}
			} else {
				sender.sendMessage("§7Please stand on a block before creating a chimney");
			}
			return true;
		}
		//
		// World Command
		//
		if (command.getName().equalsIgnoreCase("world")) {
			if (args.length < 1) {
				List<String> help = new ArrayList<>();
				help.add("/world [worldName]");
				help.add("/world invite [worldName] [player]");
				help.add("/world uninvite [worldName] [player]");
				help.add("/world list");
				if (getProfile(sender).rank == Rank.SERVER_HOST) {
					help.add("/world load [worldName]");
					help.add("/world unload [worldName]");
					help.add("/world create [worldName] [worldType] [worldOwner]");
				}
				ChatExtensions.sendCommandHelpMessage(player, help);
			} else {
				if (args[0].equalsIgnoreCase("list")) {
					sender.sendMessage("§5Private worlds");
					sender.sendMessage(paidWorldList.size() == 0 ? "§dThere are no private worlds" : "§d" + paidWorldList.toString().split("\\[")[1].split("\\]")[0]);
				} else if (args[0].equalsIgnoreCase("load") && args.length == 2) {
					if (player.getName().equals(EvilBook.config.getProperty("server_host"))) {
						WorldCreator privateWorld = new WorldCreator("plugins/EvilBook/Private worlds/" + args[1]);
						switch (getPrivateWorldProperty(args[1], "WorldType")) {
						case "FLAT": privateWorld.type(WorldType.FLAT); break;
						case "NETHER": privateWorld.environment(Environment.NETHER); break;
						case "LARGE_BIOMES": privateWorld.type(WorldType.LARGE_BIOMES); break;
						case "SKY": privateWorld.generator(new SkylandGenerator()); break;
						default: break;
						}
						paidWorldList.add(args[1]);
						getServer().createWorld(privateWorld);
						Regions.initWorld(privateWorld.name());
						sender.sendMessage("§7Loaded private world");
					} else {
						sender.sendMessage("§cYou don't have permission to load private worlds");
					}
				} else if (args[0].equalsIgnoreCase("unload") && args.length == 2) {
					if (player.getName().equals(EvilBook.config.getProperty("server_host"))) {
						getServer().unloadWorld(args[1], true);
						sender.sendMessage("§7Unloaded private world");
					} else {
						sender.sendMessage("§cYou don't have permission to unload private worlds");
					}
				} else if (args[0].equalsIgnoreCase("invite")) {
					if (args.length == 3) {
						if (isProfileExistant(args[2])) {
							Properties prop = new Properties("plugins/EvilBook/Private worlds/" + args[1] + "/Config.properties");
							List<String> allowedPlayers = new ArrayList<>();
							allowedPlayers.addAll(Arrays.asList(prop.getProperty("AllowedPlayers").split(",")));
							allowedPlayers.add(args[2].toLowerCase(Locale.UK));
							prop.setProperty("AllowedPlayers", StringUtils.join(allowedPlayers, ","));
							prop.save();
							sender.sendMessage("§7" + (getPlayer(args[2]) == null ? getServer().getOfflinePlayer(args[2]).getName() : getPlayer(args[2]).getDisplayName()) + " has been granted access to the private world " + args[1]);
							if (getPlayer(args[2]) != null) getPlayer(args[2]).sendMessage("§7You have been granted access to the private world " + args[1]);
						} else {
							sender.sendMessage("§7A player with this name does not exist");
						}
					} else {
						ChatExtensions.sendCommandHelpMessage(player, "/world invite [worldName] [player]");
					}
				} else if (args[0].equalsIgnoreCase("uninvite")) {
					if (args.length == 3) {
						if (isProfileExistant(args[2])) {
							Properties prop = new Properties("plugins/EvilBook/Private worlds/" + args[1] + "/Config.properties");
							List<String> allowedPlayers = new ArrayList<>();
							allowedPlayers.addAll(Arrays.asList(prop.getProperty("AllowedPlayers").split(",")));
							allowedPlayers.remove(args[2].toLowerCase(Locale.UK));
							prop.setProperty("AllowedPlayers", StringUtils.join(allowedPlayers, ","));
							prop.save();
							sender.sendMessage("§7" + (getPlayer(args[2]) == null ? getServer().getOfflinePlayer(args[2]).getName() : getPlayer(args[2]).getDisplayName()) + " has been denied access to the private world " + args[1]);
							if (getPlayer(args[2]) != null) getPlayer(args[2]).sendMessage("§7You have been denied access to the private world " + args[1]);
						} else {
							sender.sendMessage("§7A player with this name does not exist");
						}
					} else {
						ChatExtensions.sendCommandHelpMessage(player, "/world invite [worldName] [player]");
					}
				} else if (args[0].equalsIgnoreCase("create") && args.length == 4) {
					if (player.getName().equals(EvilBook.config.getProperty("server_host"))) {
						paidWorldList.add(args[1]);
						WorldCreator newWorld = new WorldCreator("plugins/EvilBook/Private worlds/" + args[1]);
						if (!new File("plugins/EvilBook/Private worlds/" + args[1]).mkdir()) logSevere("Failed to create 'plugins/EvilBook/Private worlds/" + args[1] + "/'");
						try {
							if (!new File("plugins/EvilBook/Private worlds/" + args[1] + "/Config.properties").createNewFile()) logSevere("Failed to create 'plugins/EvilBook/Private worlds/" + args[1] + "/Config.properties'");
						} catch (Exception e) {
							e.printStackTrace();
						}
						Properties prop = new Properties("plugins/EvilBook/Private worlds/" + args[1] + "/Config.properties");
						if (args[2].equalsIgnoreCase("flat")) {
							newWorld.type(WorldType.FLAT);
							prop.setProperty("WorldType", "FLAT");
						} else if (args[2].equalsIgnoreCase("nether")) {
							newWorld.environment(Environment.NETHER);
							prop.setProperty("WorldType", "NETHER");
						} else if (args[2].equalsIgnoreCase("largebiome")) {
							newWorld.type(WorldType.LARGE_BIOMES);
							prop.setProperty("WorldType", "LARGE_BIOMES");
						} else if (args[2].equalsIgnoreCase("sky")) {
							newWorld.generator(new SkylandGenerator());
							prop.setProperty("WorldType", "SKY");
						} else {
							prop.setProperty("WorldType", "NORMAL");
						}
						getServer().createWorld(newWorld);
						prop.setProperty("AllowedPlayers", args[3]);
						prop.save();
						if (!SQL.isColumnExistant(TableType.PlayerLocation, args[1])) {
							try {
								SQL.insertNullColumn(TableType.PlayerLocation, args[1] + " TINYTEXT");
							} catch (Exception exception) {
								exception.printStackTrace();
							}
						}
						Regions.initWorld(newWorld.name());
					} else {
						sender.sendMessage("§cYou don't have permission to create private worlds");
					}
				} else {
					for (String world : paidWorldList) {
						if (world.equalsIgnoreCase(args[0])) {
							if (getPrivateWorldProperty(args[0], "AllowedPlayers").contains(sender.getName().toLowerCase()) || getProfile(sender).rank.isHigher(Rank.TYCOON)) {
								player.teleport(getProfile(player).getWorldLastPosition("plugins/EvilBook/Private worlds/" + args[0]));
							} else {
								player.sendMessage("§7You don't have access this private world");
							}
							return true;
						}
					}
					sender.sendMessage("§7A world with this name does not exist");
				}
			}
			return true;
		}
		//
		// Achievements Command
		//
		if (command.getName().equalsIgnoreCase("achievements") || command.getName().equalsIgnoreCase("ach")) {
			sender.sendMessage("§5Your achievement score is " + ChatColor.YELLOW + getProfile(player).getAchievementScore());
			List<String> text = new ArrayList<>();
			int achievementCount = 0;
			String pageText = "";
			for (Achievement ach : Achievement.values()) {
				if (achievementCount == 2) {
					achievementCount = 0;
					text.add(pageText);
					pageText = "";
				}
				if (getProfile(player).hasAchievement(ach)) {
					if (ach.getReward() != null) {
						pageText += ChatColor.DARK_GREEN + ach.getName() + ChatColor.BOLD + " " + ach.getValue() + "\n" + ChatColor.GREEN + ach.getDescription() + ChatColor.ITALIC + "\nReward: " + ach.getReward() + "\n\n";
					} else {
						pageText += ChatColor.DARK_GREEN + ach.getName() + ChatColor.BOLD + " " + ach.getValue() + "\n" + ChatColor.GREEN + ach.getDescription() + "\n\n";
					}
				} else {
					if (ach.getReward() != null) {
						pageText += ChatColor.DARK_GRAY + ach.getName() + ChatColor.BOLD + " " + ach.getValue() + "\n" + ChatColor.GRAY + ach.getDescription() + ChatColor.ITALIC + "\nReward: " + ach.getReward() + "\n\n";
					} else {
						pageText += ChatColor.DARK_GRAY + ach.getName() + ChatColor.BOLD + " " + ach.getValue() + "\n" + ChatColor.GRAY + ach.getDescription() + "\n\n";
					}
				}
				achievementCount++;
			}
			player.getInventory().addItem(getBook("Achievement Guide", config.getProperty("server_name"), text));
			sender.sendMessage("§7Please check your inventory for the achievements guide");
			return true;
		}
		//
		// Donate Command
		//
		if (command.getName().equalsIgnoreCase("donate") || command.getName().equalsIgnoreCase("admin")) {
			getProfile(player).addAchievement(Achievement.GLOBAL_COMMAND_DONATE);
			sender.sendMessage("§c❤ §bHow to visit our server shop §c❤");
			ChatExtensions.sendClickableMessage(player, "  §3Simply go to our website at §ahttp://minecraft.amentrix.com", EnumClickAction.OPEN_URL, "http://minecraft.amentrix.com");
			sender.sendMessage("  §3Click the §aShop §3button on the site");
			sender.sendMessage("  §7The shop sells ranks and private worlds ☺");
			return true;
		}
		//
		// Help Command
		//
		if (command.getName().equalsIgnoreCase("help")) {
			if (args.length != 1) {
				sender.sendMessage("§dHelp §5- §dMain page");
				sender.sendMessage("  §5Admin assistance");
				sender.sendMessage("    §2You can request admin assistance using the §a/req §2command");
				sender.sendMessage("  §5Command help");
				sender.sendMessage("    §2Type a command with no parameters for help with it");
				sender.sendMessage("  §5Help topics");
				sender.sendMessage("    §a/help evilbook §2for EvilBook help");
				sender.sendMessage("    §a/help eviledit §2for EvilEdit help");
				sender.sendMessage("    §a/help regions §2for regions help");
				sender.sendMessage("    §a/help tips §2for useful tips");
			} else if (args[0].equalsIgnoreCase("evilbook")) {
				sender.sendMessage("§dHelp §5- §dEvilBook");
				sender.sendMessage("  §5What is EvilBook?");
				sender.sendMessage("    §2EvilBook is the core plugin running on the server");
				sender.sendMessage("  §5What does it do?");
				sender.sendMessage("    §2EvilBook provides a ranking system, multiple worlds,");
				sender.sendMessage("    §2regions, warps, commands, anti-grief and more on the server");
				sender.sendMessage("  §5Where can i get EvilBook?");
				sender.sendMessage("    §2EvilBook is developed by Amentrix and is available on request");
			} else if (args[0].equalsIgnoreCase("eviledit")) {
				sender.sendMessage("§dHelp §5- §dEvilEdit");
				sender.sendMessage("  §5What is EvilEdit?");
				sender.sendMessage("    §2EvilEdit is a clone of WorldEdit included in EvilBook");
				sender.sendMessage("    §2designed to be use friendly and more resource efficient");
				sender.sendMessage("  §5How do I use EvilEdit?");
				sender.sendMessage("    §2To use EvilEdit you must be at least Admin rank");
				sender.sendMessage("    §2Step 1 - Select point 1 with golden shovel tool");
				sender.sendMessage("    §2Step 2 - Select point 2 with golden shovel tool");
				sender.sendMessage("    §2Step 3 - Execute an EvilEdit command on the area");
			} else if (args[0].equalsIgnoreCase("regions")) {
				sender.sendMessage("§dHelp §5- §dRegions");
				sender.sendMessage("  §5What is Regions?");
				sender.sendMessage("    §2Regions is a region protection module included in EvilBook");
				sender.sendMessage("    §2allowing areas to be protected or preform operations");
				sender.sendMessage("  §5How do I use Regions?");
				sender.sendMessage("    §2To use Regions you must be at least Admin rank");
				sender.sendMessage("    §2Step 1 - Select point 1 with golden shovel tool");
				sender.sendMessage("    §2Step 2 - Select point 2 with golden shovel tool");
				sender.sendMessage("    §2Step 3 - Execute a /region command on the area");
			} else if (args[0].equalsIgnoreCase("tips")) {
				sender.sendMessage("§dHelp §5- §dTips");
				sender.sendMessage("  §5Easy wool dyeing");
				sender.sendMessage("    §2You can easily dye wool which is placed by right");
				sender.sendMessage("    §2clicking the wool with a dye, the same can be");
				sender.sendMessage("    §2done on stained glass, signs and clays");
				sender.sendMessage("  §5Changing travel speeds");
				sender.sendMessage("    §2You can change your walking speed with /speed walk [speed]");
				sender.sendMessage("    §2You can change your running speed with /speed run [speed]");
				sender.sendMessage("    §2You can change your flying speed with /speed fly [speed]");
				sender.sendMessage("    §2You can change your jump height with /jump");
			}
			return true;
		}
		//
		// Kill Player Command
		//
		if (command.getName().equalsIgnoreCase("kill")) {
			player.setHealth(0);
			player.sendMessage("§7You killed yourself");
			return true;
		}
		//
		// Slap Command
		//
		if (command.getName().equalsIgnoreCase("slap")) {
			if (args.length == 1) {
				if (getPlayer(args[0]) != null) {
					if (isInSurvival(getPlayer(args[0])) == false) {
						getPlayer(args[0]).setVelocity(new Vector(this.random.nextDouble() * 2.0D - 1.0D, this.random.nextDouble() * 1.0D, this.random.nextDouble() * 2.0D - 1.0D));
						getPlayer(args[0]).sendMessage("§7You have been slapped by " + player.getDisplayName());
						sender.sendMessage("§7You slapped " + getPlayer(args[0]).getDisplayName());
					} else {
						sender.sendMessage("§7You can't slap players in survival");
					}
				} else {
					sender.sendMessage("§7You can't slap an offline player");
				}
			} else if (args.length == 0) {
				player.setVelocity(new Vector(this.random.nextDouble() * 2.0D - 1.0D, this.random.nextDouble() * 1.0D, this.random.nextDouble() * 2.0D - 1.0D));
				sender.sendMessage("§7You slapped yourself");
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/slap <player>");
			}
			return true;
		}
		//
		// Shock Command
		//
		if (command.getName().equalsIgnoreCase("shock")) {
			if (args.length == 1) {
				if (getPlayer(args[0]) != null) {
					if (isInSurvival(getPlayer(args[0])) == false) {
						getPlayer(args[0]).getWorld().strikeLightning(getPlayer(args[0]).getLocation());
						getPlayer(args[0]).sendMessage("§7You have been shocked by " + player.getDisplayName());
						sender.sendMessage("§7You shocked " + getPlayer(args[0]).getDisplayName());
					} else {
						sender.sendMessage("§7You can't shock players in survival");
					}
				} else {
					sender.sendMessage("§7You can't shock an offline player");
				}
			} else if (args.length == 0) {
				player.getWorld().strikeLightning(player.getLocation());
				sender.sendMessage("§7You shocked yourself");
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/shock <player>");
			}
			return true;
		}
		//
		// Rocket Command
		//
		if (command.getName().equalsIgnoreCase("rocket")) {
			if (args.length == 1) {
				if (getPlayer(args[0]) != null) {
					if (isInSurvival(getPlayer(args[0])) == false) {
						getPlayer(args[0]).setVelocity(new Vector(0, 20, 0));
						getPlayer(args[0]).sendMessage("§7You have been rocketed by " + player.getDisplayName());
						sender.sendMessage("§7You rocketed " + getPlayer(args[0]).getDisplayName());
					} else {
						sender.sendMessage("§7You can't rocket players in survival");
					}
				} else {
					sender.sendMessage("§7You can't rocket an offline player");
				}
			} else if (args.length == 0) {
				player.setVelocity(new Vector(0, 20, 0));
				sender.sendMessage("§7You rocketed yourself");
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/rocket <player>");
			}
			return true;
		}
		//
		// Set Rank Command
		//
		if (command.getName().equalsIgnoreCase("setrank")) {
			if (args.length >= 1) {
				if (args[0].toLowerCase(Locale.UK).contains("&k")) {
					sender.sendMessage("§7Custom rank names can't contain &k");
					return true;
				}
				for (Rank rank : Rank.values()) {
					if (args[0].equalsIgnoreCase(rank.toString()) && !getProfile(player).rank.isHigher(rank.getPreviousRank())) {
						sender.sendMessage("§7This rank's name is blocked");
						return true;
					}
				}
				String rankDisplayName = "";
				for (String arg : args) rankDisplayName += arg + " ";
				rankDisplayName = rankDisplayName.trim();
				String formattedRankDisplayName = args[0].startsWith("&") ? EvilBook.toFormattedString(rankDisplayName) : ChatColor.GOLD + EvilBook.toFormattedString(rankDisplayName);
				if (ChatColor.stripColor(formattedRankDisplayName).length() <= 16) {
					((PlayerProfileAdmin)getProfile(player)).customRankColor = formattedRankDisplayName.substring(1, 2);
					((PlayerProfileAdmin)getProfile(player)).customRankPrefix = "§0[" + formattedRankDisplayName + "§0]";
					getProfile(player).updateNametag("§" + ((PlayerProfileAdmin)getProfile(player)).rank.getColor((getProfile(player))), null);
					sender.sendMessage("§7You have changed your custom rank name to §0[" + formattedRankDisplayName + "§0]");
				} else {
					sender.sendMessage("§7The maximum custom rank name length excluding colors is 16 characters");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/setrank [rank]");
			}
			return true;
		}
		//
		// Teleport To FlatLand Command
		//
		if (command.getName().equalsIgnoreCase("flatland")) {
			player.teleport(getProfile(sender).getWorldLastPosition("FlatLand"));
			return true;
		}
		//
		// Teleport To PlotLand Command
		//
		if (command.getName().equalsIgnoreCase("plotland")) {
			player.teleport(getProfile(sender).getWorldLastPosition("PlotLand"));
			return true;
		}
		//
		// Teleport To SkyLand Command
		//
		if (command.getName().equalsIgnoreCase("skyland") || command.getName().equalsIgnoreCase("sky")) {
			player.teleport(getProfile(sender).getWorldLastPosition("SkyLand"));
			return true;
		}
		//
		// Teleport To Creative Command
		//
		if (command.getName().equalsIgnoreCase("creative")) {
			player.teleport(getProfile(sender).getWorldLastPosition(Bukkit.getWorlds().get(0).getName()));
			return true;
		}
		//
		// Teleport To Survival Command
		//
		if (command.getName().equalsIgnoreCase("survival")) {
			player.teleport(getProfile(sender).getWorldLastPosition("SurvivalLand"));
			return true;
		}
		//
		// OldCreative Command
		//
		if (command.getName().equalsIgnoreCase("oldcreative")) {
			player.teleport(getProfile(sender).getWorldLastPosition("OldAmentrix"));
			return true;
		}
		//
		// Hub Command
		//
		if (command.getName().equalsIgnoreCase("hub")) {
			player.teleport(getServer().getWorld(Bukkit.getWorlds().get(0).getName()).getSpawnLocation());
			return true;
		}
		//
		// Set Jump Height Command
		//
		if (command.getName().equalsIgnoreCase("jump")) {
			if (args.length == 1) {
				if (isFloat(args[0]) && Float.valueOf(args[0]) <= 41) {
					getProfile(sender).jumpAmplifier = Float.valueOf(args[0]) / 4;
					sender.sendMessage("§7Your jump height has been set");
				} else {
					sender.sendMessage("§7Please enter a valid jump height");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/jump [height]");
			}
			return true;
		}
		//
		// Speed Command
		//
		if (command.getName().equalsIgnoreCase("speed")) {
			if (args.length == 0) {
				getProfile(sender).walkAmplifier = 0.2f;
				player.setWalkSpeed(0.2f);
				getProfile(sender).flyAmplifier = 0.1f;
				player.setFlySpeed(0.1f);
				getProfile(sender).runAmplifier = 0;
				sender.sendMessage("§7Reset all speed limits to their default values");
			} else if (args.length == 1) {
				if (isInteger(args[0])) {
					int speed = Integer.parseInt(args[0]);
					if (speed > 0 && speed <= 100) {
						float floatSpeed = (float)speed / 100;
						getProfile(sender).walkAmplifier = floatSpeed;
						player.setWalkSpeed(floatSpeed);
						getProfile(sender).flyAmplifier = floatSpeed;
						player.setFlySpeed(floatSpeed);
						getProfile(sender).runAmplifier = speed;
						sender.sendMessage("§7Set all speed limits to " + args[0]);
					} else {
						sender.sendMessage("§7Please enter a valid speed above 0 and below 100");
					}
				} else {
					sender.sendMessage("§7Please enter a valid numerical speed");
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("walk")) {
					if (isInteger(args[1])) {
						int speed = Integer.parseInt(args[1]);
						if (speed > 0 && speed <= 100) {
							float floatSpeed = (float)speed / 100;
							getProfile(sender).walkAmplifier = floatSpeed;
							player.setWalkSpeed(floatSpeed);
							sender.sendMessage("§7Set walk speed limit to " + args[1]);
						} else {
							sender.sendMessage("§7Please enter a valid speed above 0 and below 100");
						}
					} else {
						sender.sendMessage("§7Please enter a valid numerical speed");
					}
				} else if (args[0].equalsIgnoreCase("fly")) {
					if (isInteger(args[1])) {
						int speed = Integer.parseInt(args[1]);
						if (speed > 0 && speed <= 100) {
							float floatSpeed = (float)speed / 100;
							getProfile(sender).flyAmplifier = floatSpeed;
							player.setFlySpeed(floatSpeed);
							sender.sendMessage("§7Set fly speed limit to " + args[1]);
						} else {
							sender.sendMessage("§7Please enter a valid speed above 0 and below 100");
						}
					} else {
						sender.sendMessage("§7Please enter a valid numerical speed");
					}
				} else if (args[0].equalsIgnoreCase("run")) {
					if (isInteger(args[1])) {
						int speed = Integer.parseInt(args[1]);
						if (speed > 0 && speed <= 100) {
							getProfile(sender).runAmplifier = speed;
							sender.sendMessage("§7Set run speed limit to " + args[1]);
						} else {
							sender.sendMessage("§7Please enter a valid speed above 0 and below 100");
						}
					} else {
						sender.sendMessage("§7Please enter a valid numerical speed");
					}
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/speed",
								"/speed [speed]",
								"/speed walk [speed]",
								"/speed fly [speed]",
								"/speed run [speed]"));
			}
			return true;
		}
		//
		// Disguise Command
		//
		if (command.getName().equalsIgnoreCase("disguise")) {
			if ((isInSurvival(player) || isInMinigame(player, MinigameType.SKYBLOCK)) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
				sender.sendMessage("§7Mob disguise can't be used in survival");
			} else {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("remove")) { 
						getProfile(player).disguise.remove();
						getProfile(player).disguise = null;
						for (Player other : getServer().getOnlinePlayers()) other.showPlayer(player);
						getProfile(player).isInvisible = false;
					} else {
						EntityType entityType = getEntity(args[0]);
						if (entityType != null) {
							if ((entityType != EntityType.ENDER_DRAGON && entityType != EntityType.WITHER && entityType != EntityType.ENDER_CRYSTAL) || getProfile(sender).rank.isHigher(Rank.ELITE)) {
								for (Player other : getServer().getOnlinePlayers()) other.hidePlayer(player);
								getProfile(player).disguise = player.getWorld().spawnEntity(player.getLocation(), entityType);
								sender.sendMessage("§7You are now disguised as a " + args[0].toLowerCase(Locale.UK));
								alert(sender.getName() + " disguised themselves as a " + args[0].toLowerCase(Locale.UK));
							}
						}
					}
				} else {
					ChatExtensions.sendCommandHelpMessage(player, 
							Arrays.asList("/disguise [mobName]",
									"/disguise [mobID]",
									"/disguise remove"));
				}
			}
			return true;
		}
		//
		// Name Command
		//
		if (command.getName().equalsIgnoreCase("name")) {
			if (args.length >= 1) {
				if (player.getItemInHand().getType() != Material.AIR) {
					if (player.getItemInHand().getType() == Material.SKULL_ITEM) {
						ItemStack skull;
						if (args[0].equalsIgnoreCase("Creeper")) {
							skull = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.CREEPER.ordinal());
						} else if (args[0].equalsIgnoreCase("Skeleton")) {
							skull = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.SKELETON.ordinal());
						} else if (args[0].equalsIgnoreCase("Wither")) {
							skull = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.WITHER.ordinal());
						} else if (args[0].equalsIgnoreCase("Zombie")) {
							skull = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.ZOMBIE.ordinal());
						} else {
							skull = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.ordinal());
						}
						SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
						skullMeta.setOwner(args[0]);
						skullMeta.setDisplayName(args[0] + "'s Skull");
						skull.setItemMeta(skullMeta);
						player.setItemInHand(skull);
						sender.sendMessage("§7Skull owner set to §d" + args[0]);
					} else {
						String name = "";
						for (String arg : args) name += arg + " ";
						ItemMeta meta = player.getItemInHand().getItemMeta();
						meta.setDisplayName(name.trim());
						player.getItemInHand().setItemMeta(meta);
						sender.sendMessage("§7Item renamed to §d" + name.trim());
					}
				} else {
					sender.sendMessage("§7You must be holding an item to rename it");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/name [name]");
			}
			return true;
		}
		//
		// Skull Command
		//
		if (command.getName().equalsIgnoreCase("skull")) {
			if (args.length == 1) {
				ItemStack skull;
				if (args[0].equalsIgnoreCase("Creeper")) {
					skull = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.CREEPER.ordinal());
				} else if (args[0].equalsIgnoreCase("Skeleton")) {
					skull = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.SKELETON.ordinal());
				} else if (args[0].equalsIgnoreCase("Wither")) {
					skull = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.WITHER.ordinal());
				} else if (args[0].equalsIgnoreCase("Zombie")) {
					skull = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.ZOMBIE.ordinal());
				} else {
					skull = new ItemStack(Material.SKULL_ITEM, 1, (short)SkullType.PLAYER.ordinal());
				}
				SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
				skullMeta.setOwner(args[0]);
				skullMeta.setDisplayName(args[0] + "'s Skull");
				skull.setItemMeta(skullMeta);
				player.setItemInHand(skull);
				sender.sendMessage("§7Skull owner set to §d" + args[0]);
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/skull [owner]");
			}
			return true;
		}
		//
		// Advertise Command
		//
		if (command.getName().equalsIgnoreCase("advert") || command.getName().equalsIgnoreCase("advertise")) {
			if (args.length > 0) {
				if (getProfile(sender).money >= 40 || getProfile(player).rank.isHigher(Rank.INVESTOR)) {
					String broadcast = "";
					for (String msg : args) broadcast += " " + msg;
					broadcastPlayerMessage(sender.getName(), "§d[§5Advert§d]" + broadcast);
					alert(sender.getName() + " executed the advertise command");
					if (!getProfile(player).rank.isHigher(Rank.INVESTOR)) {
						getProfile(sender).money -= 40;
						incrementOwnerBalance(40);
						sender.sendMessage("§7Created advert §c-$40");
					}
				} else {
					sender.sendMessage("§5You don't have enough money for this item");
					sender.sendMessage("§dYou need to earn $" + (40 - getProfile(sender).money));
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/" + command.getName().toLowerCase(Locale.UK) + " [message]");
			}
			return true;
		}
		//
		// Helmet command
		//
		if (command.getName().equalsIgnoreCase("helmet") || command.getName().equalsIgnoreCase("hat") || command.getName().equalsIgnoreCase("head")) {
			if (player.getInventory().getItemInHand().getType() == Material.AIR) {
				sender.sendMessage("§7You can't wear an air helmet");
			} else {
				if (isInSurvival(player) || isInMinigame(player, MinigameType.SKYBLOCK)) {
					ItemStack itemHelmet = player.getInventory().getItemInHand();
					if (player.getInventory().getHelmet() != null) {
						player.getInventory().addItem(player.getInventory().getHelmet());
					}
					player.getInventory().setHelmet(player.getInventory().getItemInHand());
					player.getInventory().getHelmet().setAmount(1);
					if (player.getInventory().getItemInHand().getAmount() != 1) {
						player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
					} else {
						player.getInventory().removeItem(player.getInventory().getItemInHand());
					}
					if (itemHelmet.getType().isBlock() == false) {
						sender.sendMessage("§7You are now wearing a custom helmet");
					} else {
						sender.sendMessage("§7You are now wearing a " + BlockReference.getFriendlyName(itemHelmet.getType()) + " helmet");
					}
				} else {
					player.getInventory().setHelmet(player.getInventory().getItemInHand());
					if (player.getInventory().getItemInHand().getType().isBlock() == false) {
						sender.sendMessage("§7You are now wearing a custom helmet");
					} else {
						sender.sendMessage("§7You are now wearing a " + BlockReference.getFriendlyName(player.getInventory().getItemInHand().getType()) + " helmet");
					}
				}
				getProfile(player).addAchievement(Achievement.GLOBAL_COMMAND_HELMET);
			}
			return true;
		}
		//
		// Butter command
		//
		if (command.getName().equalsIgnoreCase("butter")) {
			if (isInSurvival(player) || isInMinigame(player, MinigameType.SKYBLOCK)) {
				sender.sendMessage("§7Butter is illegal in survival");
				return true;
			}
			ItemStack butter = new ItemStack(Material.GOLD_INGOT);
			ItemMeta yourItemStackMeta = butter.getItemMeta();
			yourItemStackMeta.setDisplayName("Butter");
			yourItemStackMeta.setLore(Arrays.asList("This butter was made by JacobClark"));
			butter.setItemMeta(yourItemStackMeta);
			butter.setAmount(64);
			player.getInventory().addItem(butter);
			butter = new ItemStack(Material.GOLD_HELMET);
			yourItemStackMeta = butter.getItemMeta();
			yourItemStackMeta.setDisplayName("Butter Helmet");
			yourItemStackMeta.setLore(Arrays.asList("A helmet made by the butter gods"));
			butter.setItemMeta(yourItemStackMeta);
			player.getInventory().setHelmet(butter);
			butter = new ItemStack(Material.GOLD_CHESTPLATE);
			yourItemStackMeta = butter.getItemMeta();
			yourItemStackMeta.setDisplayName("Butter Chestplate");
			yourItemStackMeta.setLore(Arrays.asList("A chestplate made by the butter gods"));
			butter.setItemMeta(yourItemStackMeta);
			player.getInventory().setChestplate(butter);
			butter = new ItemStack(Material.GOLD_LEGGINGS);
			yourItemStackMeta = butter.getItemMeta();
			yourItemStackMeta.setDisplayName("Butter Leggings");
			yourItemStackMeta.setLore(Arrays.asList("Leggings made by the butter gods"));
			butter.setItemMeta(yourItemStackMeta);
			player.getInventory().setLeggings(butter);
			butter = new ItemStack(Material.GOLD_BOOTS);
			yourItemStackMeta = butter.getItemMeta();
			yourItemStackMeta.setDisplayName("Butter Boots");
			yourItemStackMeta.setLore(Arrays.asList("Boots made by the butter gods"));
			butter.setItemMeta(yourItemStackMeta);
			player.getInventory().setBoots(butter);
			sender.sendMessage("§7You have been blessed by the gods of butter");
			getProfile(player).addAchievement(Achievement.GLOBAL_EQUIP_BUTTERARMOUR);
			return true;
		}
		//
		// Region command
		//
		if (command.getName().equalsIgnoreCase("region")) {
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("scan")) {
					List<String> regionsFoundList = new ArrayList<>();
					for (Region region : Regions.getRegions(player.getWorld().getName())) {
						if (Regions.isInRegion(region, player.getLocation())) {
							regionsFoundList.add(ChatColor.LIGHT_PURPLE + region.getName() + " region owned by " + region.getOwner());
						}
					}
					if (regionsFoundList.size() != 0) {
						sender.sendMessage("§5You are in " + regionsFoundList.size() + (regionsFoundList.size() == 1 ? " region" : " regions"));
						for (String name : regionsFoundList) sender.sendMessage(name);
					} else {
						sender.sendMessage("§7You are not in any regions");
					}
				} else if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("create")) {
					if (args.length == 2) {
						if (getProfile(player).actionLocationA == null || getProfile(player).actionLocationB == null) {
							sender.sendMessage("§7Please select two region boundaries using the golden shovel tool");
						} else {
							Region region = Regions.getRegion(args[1]);
							if (region == null) {
								region = new Region(args[1],
										getProfile(player).actionLocationA,
										getProfile(player).actionLocationB,
										false,
										sender.getName(),
										null,
										null,
										null,
										null);
								region.saveRegion();
								Regions.addRegion(player.getWorld().getName(), region);
								sender.sendMessage("§7Region " + ChatColor.ITALIC + region.getName() + ChatColor.GRAY + " created");
							} else {
								sender.sendMessage(ChatColor.GRAY + "The region " + ChatColor.ITALIC + region.getName() + ChatColor.GRAY + " already exists");
							}
						}
					} else {
						ChatExtensions.sendCommandHelpMessage(player, "/region add [regionName]");
					}
				} else if (args[0].equalsIgnoreCase("protect")) {
					if (args.length == 2) {
						Region region = Regions.getRegion(args[1]);
						if (region == null) {
							if (getProfile(player).actionLocationA == null || getProfile(player).actionLocationB == null) {
								sender.sendMessage("§7Please select two region boundaries using the golden shovel tool");
							} else {
								region = new Region(args[1],
										getProfile(player).actionLocationA,
										getProfile(player).actionLocationB,
										true,
										sender.getName(),
										null,
										null,
										null,
										null);
								region.saveRegion();
								Regions.addRegion(player.getWorld().getName(), region);
								sender.sendMessage("§7Region " + ChatColor.ITALIC + args[1] + ChatColor.GRAY + " created and protected");
							}
						} else {
							if (!sender.getName().equals(region.getOwner()) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
								sender.sendMessage("§7You don't have ownership of this region");
							} else {
								region.isProtected(true);	
								region.saveRegion();
								sender.sendMessage("§7Region " + ChatColor.ITALIC + args[1] + ChatColor.GRAY + " protected");
							}
						}
					} else {
						ChatExtensions.sendCommandHelpMessage(player, "/region protect [regionName]");
					}
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (args.length == 2) {
						Region region = Regions.getRegion(args[1]);
						if (region == null) {
							sender.sendMessage("§7No regions with this name exist");
						} else {
							if (!sender.getName().equals(region.getOwner()) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
								sender.sendMessage("§7You don't have ownership of this region");
							} else {
								region.delete();
								Regions.removeRegion(region.getLocationA().getWorld().getName(), region);
								sender.sendMessage("§7Region " + ChatColor.ITALIC + region.getName() + ChatColor.GRAY + " removed");
							}
						}
					} else {
						ChatExtensions.sendCommandHelpMessage(player, "/region remove [regionName]");
					}
				} else if (args[0].equalsIgnoreCase("setwelcome")) {
					if (args.length > 2) {
						Region region = Regions.getRegion(args[1]);
						if (region == null) {
							sender.sendMessage("§7No regions with this name exist");
						} else {
							if (!sender.getName().equals(region.getOwner()) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
								sender.sendMessage("§7You don't have ownership of this region");
							} else {
								StringBuilder message = new StringBuilder();
								for (int i = 2; i < args.length; i++) message.append(args[i] + " ");
								region.setWelcomeMessage(toFormattedString(message.toString().trim()));
								region.saveRegion();
								sender.sendMessage("§7Region " + ChatColor.ITALIC + region.getName() + ChatColor.GRAY + "'s welcome message set");
							}
						}
					} else {
						ChatExtensions.sendCommandHelpMessage(player, "/region setWelcome [regionName] [welcomeMessage]");
					}
				} else if (args[0].equalsIgnoreCase("setleave")) {
					if (args.length > 2) {
						Region region = Regions.getRegion(args[1]);
						if (region == null) {
							sender.sendMessage("§7No regions with this name exist");
						} else {
							if (!sender.getName().equals(region.getOwner()) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
								sender.sendMessage("§7You don't have ownership of this region");
							} else {
								StringBuilder message = new StringBuilder();
								for (int i = 2; i < args.length; i++) message.append(args[i] + " ");
								region.setLeaveMessage(toFormattedString(message.toString().trim()));	
								region.saveRegion();
								sender.sendMessage("§7Region " + ChatColor.ITALIC + region.getName() + ChatColor.GRAY + "'s leave message set");
							}
						}
					} else {
						ChatExtensions.sendCommandHelpMessage(player, "/region setLeave [regionName] [leaveMessage]");
					}
				} else if (args[0].equalsIgnoreCase("allow")) {
					if (args.length == 3) {
						Region region = Regions.getRegion(args[1]);
						if (region == null) {
							sender.sendMessage("§7No regions with this name exist");
						} else {
							if (!sender.getName().equals(region.getOwner()) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
								sender.sendMessage("§7You don't have ownership of this region");
							} else {
								if (isProfileExistant(args[2])) {
									OfflinePlayer regionPlayer = getServer().getOfflinePlayer(args[2]);
									region.addAllowedPlayer(regionPlayer.getName());
									region.saveRegion();
									sender.sendMessage("§7" + regionPlayer.getName() + "'s allowed permissions for region " + ChatColor.ITALIC + region.getName());
								} else {
									sender.sendMessage("§7This player doesn't exist");
								}
							}
						}	
					} else {
						ChatExtensions.sendCommandHelpMessage(player, "/region allow [regionName] [playerName]");
					}
				} else if (args[0].equalsIgnoreCase("deny")) {
					if (args.length == 3) {
						Region region = Regions.getRegion(args[1]);
						if (region == null) {
							sender.sendMessage("§7No regions with this name exist");
						} else {
							if (!sender.getName().equals(region.getOwner()) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
								sender.sendMessage("§7You don't have ownership of this region");
							} else {
								if (isProfileExistant(args[2])) {
									OfflinePlayer regionPlayer = getServer().getOfflinePlayer(args[2]);
									region.removeAllowedPlayer(regionPlayer.getName());
									region.saveRegion();
									sender.sendMessage("§7" + regionPlayer.getName() + "'s permissions removed for region " + ChatColor.ITALIC + region.getName());
								} else {
									sender.sendMessage("§7This player doesn't exist");
								}
							}
						}
					} else {
						ChatExtensions.sendCommandHelpMessage(player, "/region deny [regionName] [playerName]");
					}
				} else if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("tpa")) {
					if (args.length == 2) {
						Region region = Regions.getRegion(args[1]);
						if (region == null) {
							sender.sendMessage("§7No regions with this name exist");
						} else {
							double x = (region.getLocationA().getX() + region.getLocationB().getX()) / 2;
							double y = (region.getLocationA().getY() + region.getLocationB().getY()) / 2;
							double z = (region.getLocationA().getZ() + region.getLocationB().getZ()) / 2;
							player.teleport(new Location(region.getLocationA().getWorld(), x, y, z));
							sender.sendMessage("§7Teleported to region " + ChatColor.ITALIC + region.getName());
						}
					} else {
						ChatExtensions.sendCommandHelpMessage(player, "/region tp [regionName]");
					}
				} else if (args[0].equalsIgnoreCase("setWarp")) {
					if (args.length == 3) {
						Region region = Regions.getRegion(args[1]);
						if (region == null) {
							sender.sendMessage("§7No regions with this name exist");
						} else {
							if (!sender.getName().equals(region.getOwner()) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
								sender.sendMessage("§7You don't have ownership of this region");
							//TODO: Regions: Don't use SQL here?
							} else if (SQL.isKeyExistant(TableType.Warps, args[2].toLowerCase(Locale.UK))) {
								region.setWarp(args[2].toLowerCase(Locale.UK));	
								region.saveRegion();
								sender.sendMessage("§7Region " + ChatColor.ITALIC + region.getName() + ChatColor.GRAY + " warp set");
							} else {
								sender.sendMessage("§7No warps with this name exist");
							}
						}
					} else {
						ChatExtensions.sendCommandHelpMessage(player, "/region setWarp [regionName] [warpName]");
					}
				} else if (args[0].equalsIgnoreCase("removeWarp")) {
					if (args.length == 2) {
						Region region = Regions.getRegion(args[1]);
						if (region == null) {
							sender.sendMessage("§7No regions with this name exist");
						} else {
							if (!sender.getName().equals(region.getOwner()) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
								sender.sendMessage("§7You don't have ownership of this region");
							} else {
								region.setWarp(null);	
								region.saveRegion();
								sender.sendMessage("§7Region " + ChatColor.ITALIC + region.getName() + ChatColor.GRAY + " warp removed");
							}
						}
					} else {
						ChatExtensions.sendCommandHelpMessage(player, "/region removeWarp [regionName]");
					}
				} else if (args[0].equalsIgnoreCase("select")) {
					if (args.length == 2) {
						Region region = Regions.getRegion(args[1]);
						if (region == null) {
							sender.sendMessage("§7No regions with this name exist");
						} else {
							getProfile(sender).actionLocationA = region.getLocationA();
							getProfile(sender).actionLocationB = region.getLocationB();
							sender.sendMessage("§7Region " + ChatColor.ITALIC + region.getName() + ChatColor.GRAY + " boundaries selected");
						}
					} else {
						ChatExtensions.sendCommandHelpMessage(player, "/region select [regionName]");
					}
				} else if (args[0].equalsIgnoreCase("inherit")) {
					if (args.length == 3) {
						Region region = Regions.getRegion(args[1]);
						if (region == null) {
							sender.sendMessage("§7No regions with this name exist");
						} else {
							Region regionToInherit = Regions.getRegion(args[2]);
							if (regionToInherit == null) {
								sender.sendMessage("§7The region to inherit from couldn't be found");
							} else {
								if (!sender.getName().equals(region.getOwner()) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
									sender.sendMessage("§7You don't have ownership of this region");
								} else {
									region.setAllowedPlayers(regionToInherit.getAllowedPlayers());
									region.setLeaveMessage(regionToInherit.getLeaveMessage());
									region.setWarp(regionToInherit.getWarp());
									region.setWelcomeMessage(regionToInherit.getWelcomeMessage());
									region.saveRegion();
									sender.sendMessage("§7Region " + ChatColor.ITALIC + region.getName() + ChatColor.GRAY + "'s settings inherited from " + ChatColor.ITALIC + regionToInherit.getName());
								}
							}
						}
					} else {
						ChatExtensions.sendCommandHelpMessage(player, "/region inherit [regionName] [regionToInherit]");
					}
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/region scan",
								"/region create [regionName]",
								"/region protect [regionName]",
								"/region remove [regionName]",
								"/region select [regionName]",
								"/region inherit [regionName] [regionToInherit]",
								"/region setWelcome [regionName] [welcomeMessage]",
								"/region setLeave [regionName] [leaveMessage]",
								"/region tp [regionName]",
								"/region setWarp [regionName] [warpName]",
								"/region removeWarp [regionName]",
								"/region allow [regionName] [playerName]",
								"/region deny [regionName] [playerName]"));
			}
			return true;
		}
		//
		// Admin assistance request command
		//
		if (command.getName().equalsIgnoreCase("req") || command.getName().equalsIgnoreCase("helpop")) {
			if (args.length >= 1) {
				String message = "§c";
				for (String msg : args) message += msg + " ";
				Boolean adminOnline = false;
				for (Player p : getServer().getOnlinePlayers()) {
					if (getProfile(p).rank.isHigher(Rank.ARCHITECT) && !getProfile(p).isMuted(sender.getName())) {
						p.sendMessage(sender.getName() + " requires assistance: " + message.trim());
						adminOnline = true;
					}
				}
				if (!adminOnline) {
					sender.sendMessage("§7No staff are online to recieve your request");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/" + command.getName().toLowerCase(Locale.UK) + " [message]");
			}
			return true;
		}
		//
		// Pay Command
		//
		if (command.getName().equalsIgnoreCase("pay") || command.getName().equalsIgnoreCase("givemoney")) {
			if (args.length == 2) {
				if (isProfileExistant(args[0])) {
					if (isInteger(args[1]) && Integer.parseInt(args[1]) > 0) {
						if (getProfile(sender).money >= Integer.parseInt(args[1])) {
							if (getPlayer(args[0]) != null) {
								getProfile(sender).money -= Integer.parseInt(args[1]);
								getProfile(args[0]).money += Integer.parseInt(args[1]);
								getPlayer(args[0]).sendMessage("§7You have recieved §a$" + args[1] + " §7from " + player.getDisplayName());
								sender.sendMessage("§7You have paid " + getPlayer(args[0]).getDisplayName() + " §c$" + args[1]);
								GlobalStatistic.incrementStatistic(GlobalStatistic.EconomyTrade, Integer.parseInt(args[1]));
							} else {
								SQL.setInteger(TableType.PlayerProfile, args[0], "money", SQL.getInteger(TableType.PlayerProfile, args[0], "money") + Integer.parseInt(args[1]));
								getProfile(sender).money -= Integer.parseInt(args[1]);
								sender.sendMessage("§7You have paid " + getServer().getOfflinePlayer(args[0]).getName() + " §c$" + args[1]);
								GlobalStatistic.incrementStatistic(GlobalStatistic.EconomyTrade, Integer.parseInt(args[1]));
							}
						} else {
							sender.sendMessage("§7You don't have enough money to do this");
						}
					} else {
						sender.sendMessage("§7Please enter a valid amount of money to pay");
					}
				} else {
					sender.sendMessage("§7You can't pay a player who doesn't exist");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/" + command.getName().toLowerCase(Locale.UK) + " [player] [amount]");
			}
			return true;
		}
		//
		// Gamemode Command
		//
		if (command.getName().equalsIgnoreCase("gamemode") || command.getName().equalsIgnoreCase("gm")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("survival") || args[0].equals("0")) {
					player.setGameMode(GameMode.SURVIVAL);
					sender.sendMessage("§7Your gamemode has been changed to survival");
				} else if (args[0].equalsIgnoreCase("creative") || args[0].equals("1")) {
					if ((!isInSurvival(player) && !isInMinigame(player, MinigameType.SKYBLOCK)) || getProfile(sender).rank.isHigher(Rank.TYCOON)) {
						player.setGameMode(GameMode.CREATIVE);
						sender.sendMessage("§7Your gamemode has been changed to creative");
					} else {
						sender.sendMessage("§7Creative gamemode can't be used in survival");
					}
				} else if (args[0].equalsIgnoreCase("adventure") || args[0].equals("2")) {
					player.setGameMode(GameMode.ADVENTURE);
					sender.sendMessage("§7Your gamemode has been changed to adventure");
				} else {
					sender.sendMessage("§7This gamemode doesn't exist");
				}
			} else if (args.length == 2 && getProfile(sender).rank.isHigher(Rank.TYCOON)) {
				if (getPlayer(args[1]) != null) {
					if (args[0].equalsIgnoreCase("survival") || args[0].equals("0")) {
						getPlayer(args[1]).setGameMode(GameMode.SURVIVAL);
						getPlayer(args[1]).sendMessage(player.getDisplayName() + "§7 has changed your gamemode to survival");
						sender.sendMessage(getPlayer(args[1]).getDisplayName() + "§7's gamemode has been change to survival");
						alert(sender.getName() + " changed " + getPlayer(args[1]).getName() + "'s gamemode to survival");
					} else if (args[0].equalsIgnoreCase("creative") || args[0].equals("1")) {
						if (!isInSurvival(getPlayer(args[1])) && !isInMinigame(getPlayer(args[1]), MinigameType.SKYBLOCK)) {
							getPlayer(args[1]).setGameMode(GameMode.CREATIVE);
							getPlayer(args[1]).sendMessage(player.getDisplayName() + "§7 has changed your gamemode to creative");
							sender.sendMessage(getPlayer(args[1]).getDisplayName() + "§7's gamemode has been change to creative");
							alert(sender.getName() + " changed " + getPlayer(args[1]).getName() + "'s gamemode to creative");
						} else {
							sender.sendMessage("§7Creative gamemode can't be used on a player in survival");
						}
					} else if (args[0].equalsIgnoreCase("adventure") || args[0].equals("2")) {
						getPlayer(args[1]).setGameMode(GameMode.ADVENTURE);
						getPlayer(args[1]).sendMessage(player.getDisplayName() + "§7 has changed your gamemode to adventure");
						sender.sendMessage(getPlayer(args[1]).getDisplayName() + "§7's gamemode has been change to adventure");
						alert(sender.getName() + " changed " + getPlayer(args[1]).getName() + "'s gamemode to adventure");
					} else {
						sender.sendMessage("§7This gamemode doesn't exist");
					}
				} else {
					sender.sendMessage("§7You change the gamemode of an offline player");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/" + command.getName().toLowerCase(Locale.UK) + " [mode]");
			}
			return true;
		}
		//
		// Rename Command
		//
		if (command.getName().equalsIgnoreCase("rename") || command.getName().equalsIgnoreCase("nick") || command.getName().equalsIgnoreCase("nickname")) {
			if (args.length >= 1) {
				String name = "";
				for (String arg : args) name += arg + " ";
				name = name.trim();
				String formattedName = toFormattedString(name);
				if (ChatColor.stripColor(formattedName).length() <= 16) {
					if (name.toLowerCase(Locale.UK).contains("&k")) {
						sender.sendMessage("§7Name aliases can't contain &k");
					} else {
						((PlayerProfileAdmin)getProfile(player)).setNameAlias(formattedName);
						sender.sendMessage("§7You have renamed yourself to " + formattedName);
					}
				} else {
					sender.sendMessage("§7The maximum rename length excluding colors is 16 characters");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/" + command.getName().toLowerCase(Locale.UK) + " [name]");
			}
			return true;
		}
		//
		// Mute Command
		//
		if (command.getName().equalsIgnoreCase("mute") || command.getName().equalsIgnoreCase("ignore")) {
			if (args.length == 1) {
				getProfile(sender).mutedPlayers.add(args[0].toLowerCase(Locale.UK));
				sender.sendMessage("§7You have muted " + args[0]);
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/" + command.getName().toLowerCase(Locale.UK) + " [player]");
			}
			return true;
		}
		//
		// Un-mute Command
		//
		if (command.getName().equalsIgnoreCase("unmute")) {
			if (args.length == 1) {
				if (getProfile(sender).mutedPlayers == null) {
					sender.sendMessage("§7You haven't muted any players");
				} else {
					getProfile(sender).mutedPlayers.remove(args[0].toLowerCase(Locale.UK));
					sender.sendMessage("§7You have unmuted " + args[0]);
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/unmute [player]");
			}
			return true;
		}
		//
		// Vanish Command
		//
		if (command.getName().equalsIgnoreCase("vanish") || command.getName().equalsIgnoreCase("hide")) {
			if (isInSurvival(player) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
				sender.sendMessage("§7Vanish can't be used in survival");
				return true;
			}
			for (Player other : getServer().getOnlinePlayers()) other.hidePlayer(player);
			getProfile(player).isInvisible = true;
			sender.sendMessage("§7You are now invisible");
			alert(sender.getName() + " made themselves invisible");
			return true;
		}
		//
		// Unvanish Command
		//
		if (command.getName().equalsIgnoreCase("unvanish") || command.getName().equalsIgnoreCase("show")) {
			for (Player other : getServer().getOnlinePlayers()) other.showPlayer(player);
			getProfile(player).isInvisible = false;
			sender.sendMessage("§7You are now visible");
			return true;
		}
		//
		// Set Home Command
		//
		if (command.getName().equalsIgnoreCase("sethome") || command.getName().equalsIgnoreCase("createhome")) {
			getProfile(sender).homeLocation = player.getLocation();
			PlayerHomeMarkers.setPlayerHome(player.getName(), player.getLocation());
			sender.sendMessage("§7Your home location has been set");
			return true;
		}
		//
		// Delete Home Command
		//
		if (command.getName().equalsIgnoreCase("delhome") || command.getName().equalsIgnoreCase("remhome") || command.getName().equalsIgnoreCase("rmhome")) {
			getProfile(sender).homeLocation = null;
			PlayerHomeMarkers.setPlayerHome(player.getName(), null);
			sender.sendMessage("§7Your home location has been removed");
			return true;
		}
		//
		// Teleport To Home Command
		//
		if (command.getName().equalsIgnoreCase("home") || command.getName().equalsIgnoreCase("homes")) {
			if (args.length == 0 || !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
				if (getProfile(sender).homeLocation != null) {
					player.teleport(getProfile(sender).homeLocation);
					sender.sendMessage("§7Welcome home");
				} else {
					sender.sendMessage("§7Please set a home first using /sethome");
				}
			} else {
				//TODO: SQL: Change to SQL.getLocation()
				if (SQL.getString(TableType.PlayerLocation, args[0], "home_location") != null) {
					String[] location = SQL.getString(TableType.PlayerLocation, args[0], "home_location").split(">");
					player.teleport(new Location(Bukkit.getServer().getWorld(location[3]), Double.valueOf(location[0]), Double.valueOf(location[1]), Double.valueOf(location[2])));
					sender.sendMessage("§7Welcome to " + args[0] + "'s home");
				} else {
					sender.sendMessage("§7This player has not set a home");
				}
			}
			return true;
		}
		//
		// Spawn Creature Command
		//
		if (command.getName().equalsIgnoreCase("spawncreature") || command.getName().equalsIgnoreCase("cspawn") || command.getName().equalsIgnoreCase("mob") || command.getName().equalsIgnoreCase("spawnmob")) {
			if ((!isInSurvival(player) && !isInMinigame(player, MinigameType.SKYBLOCK)) || getProfile(sender).rank.isHigher(Rank.TYCOON)) {
				if (args.length >= 1) {
					EntityType entityType = getEntity(args[0]);
					if (entityType != null) {
						if ((entityType != EntityType.ENDER_DRAGON && entityType != EntityType.WITHER && entityType != EntityType.ENDER_CRYSTAL) || getProfile(sender).rank.isHigher(Rank.ELITE)) {
							if (args.length == 1) {
								if (player.getNearbyEntities(64, 64, 64).size() + 1 >= 400) {
									sender.sendMessage("§7Nearby entity limit reached");
								} else {
									player.getWorld().spawnEntity(player.getLocation(), entityType);
									sender.sendMessage("§7Spawned a " + args[0].toLowerCase(Locale.UK));
									alert(sender.getName() + " spawned a " + args[0].toLowerCase(Locale.UK));
								}
							} else if (args.length == 2) {
								if (isInteger(args[1])) {
									if (player.getNearbyEntities(64, 64, 64).size() + Integer.parseInt(args[1]) >= 400) {
										sender.sendMessage("§7Nearby entity limit reached");
									} else {
										int amount = Integer.parseInt(args[1]);
										for (int i = 0; i < amount; i++) player.getWorld().spawnEntity(player.getLocation(), entityType);
										sender.sendMessage("§7Spawned " + args[1] + " " + args[0].toLowerCase(Locale.UK) + "'s");
										alert(sender.getName() + " spawned " + args[1] + " " + args[0].toLowerCase(Locale.UK) + "'s");
									}
								} else {
									sender.sendMessage("§7Please enter a valid number of creatures to spawn");
								}
							} else if (args.length == 3) {
								if (args[2].equalsIgnoreCase("boom")) {
									if (isInteger(args[1])) {
										if (player.getNearbyEntities(64, 64, 64).size() + Integer.parseInt(args[1]) >= 400) {
											sender.sendMessage("§7Nearby entity limit reached");
										} else {
											int amount = Integer.parseInt(args[1]);
											final List<Entity> explosiveEntityList = new ArrayList<>();
											for (int i = 0; i < amount; i++) explosiveEntityList.add(player.getWorld().spawnEntity(player.getLocation(), entityType));
											sender.sendMessage("§7Spawned " + args[1] + " " + args[0].toLowerCase(Locale.UK) + "'s");
											alert(sender.getName() + " spawned " + args[1] + " " + args[0].toLowerCase(Locale.UK) + "'s");
											Random explosionRandomizer = new Random();
											for (final Entity entity : explosiveEntityList) {
												this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
													@Override
													public void run() {
														entity.getLocation().getWorld().playEffect(entity.getLocation(), Effect.EXPLOSION_HUGE, 0);
														entity.remove();
													}
												}, 80 + ((long)(explosionRandomizer.nextDouble() * 120)));
											}
										}
									} else {
										sender.sendMessage("§7Please enter a valid number of creatures to spawn");
									}
								}
							}
						} else {
							sender.sendMessage("§7This creature is banned");
						}
					} else {
						sender.sendMessage("§7This creature doesn't exist");
					}
				} else {
					ChatExtensions.sendCommandHelpMessage(player, "/" + command.getName().toLowerCase(Locale.UK) + " [mob] <amount>");
				}
			} else {
				sender.sendMessage("§7Creatures can't be spawned in survival");
			}
			return true;
		}
		//
		// Warp Command
		//
		if (command.getName().equalsIgnoreCase("warp")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {
					if (!getProfile(sender).warps.isEmpty()) {
						//TODO: ChatExtensions on each effect listed
						sender.sendMessage("§5My warps");
						sender.sendMessage("§d" + getProfile(sender).warps.toString().substring(1).substring(0, getProfile(sender).warps.toString().length() - 2));
					} else {
						sender.sendMessage("§7You don't own any warps");
					}
				} else {
					if (SQL.isKeyExistant(TableType.Warps, args[0].toLowerCase(Locale.UK).replaceAll("'", "''"))) {
						player.teleport(SQL.getWarp(args[0].toLowerCase(Locale.UK).replaceAll("'", "''")));
						player.sendMessage("§7You have been warped to §d" + args[0]);
					} else {
						sender.sendMessage("§7A warp with that name doesn't exist");
					}
				}
			} else if (args.length == 2 && args[0].equalsIgnoreCase("list") && getProfile(sender).rank.isHigher(Rank.TYCOON)) {
				String playerWarps = SQL.getString(TableType.PlayerProfile, args[1], "warp_list");
				if (playerWarps != null) {
					List<String> warps = Arrays.asList(playerWarps.split(","));
					//TODO: ChatExtensions on each effect listed
					sender.sendMessage("§5" + args[1] + "'s warps");
					sender.sendMessage("§d" + warps.toString().substring(1).substring(0, warps.toString().length() - 2));
				} else {
					sender.sendMessage("§7" + args[1] + " doesn't own any warps");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/warp [warpName]");
			}
			return true;
		}
		//
		// Delete Warp Command
		//
		if (command.getName().equalsIgnoreCase("delwarp") || command.getName().equalsIgnoreCase("deletewarp") || command.getName().equalsIgnoreCase("remwarp") || command.getName().equalsIgnoreCase("rmwarp")) {
			if (args.length == 1) {
				if (getProfile(sender).warps.size() != 0) {
					if (getProfile(sender).rank.isHigher(Rank.ELITE) || getProfile(sender).warps.contains(args[0].toLowerCase(Locale.UK))) {
						getProfile(sender).warps.remove(args[0].toLowerCase(Locale.UK));
						SQL.deleteRow(TableType.Warps, args[0].toLowerCase(Locale.UK));
						for (Region region : Regions.getRegions(player.getWorld().getName())) {
							if (region.getWarp() != null && region.getWarp().equals(args[0].toLowerCase(Locale.UK))) {
								region.setWarp(null);
							}
						}
						sender.sendMessage("§7Deleted warp §d" + args[0]);
						WarpMarkers.removeWarp(args[0]);
					} else {
						sender.sendMessage("§7You don't own this warp");
					}
				} else {
					sender.sendMessage("§7You don't own any warps");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/" + command.getName().toLowerCase(Locale.UK) + " [warpName]");
			}
			return true;
		}
		//
		// Set Warp Command
		//
		if (command.getName().equalsIgnoreCase("setwarp") || command.getName().equalsIgnoreCase("createwarp")) {
			if (args.length == 1) {
				if (getProfile(sender).rank.isAdmin() || getProfile(sender).money >= 20) {
					if (args[0].length() <= 32) {
						Location loc = player.getLocation();
						if (loc.getY() < 1) {
							sender.sendMessage("§7You can't create a warp below bedrock");
						} else {
							if (args[0].contains(",")) {
								sender.sendMessage("§7The ',' character is blocked in warp names");
							} else {
								if (!SQL.isKeyExistant(TableType.Warps, args[0].toLowerCase(Locale.UK).replaceAll("'", "''"))) {
									try {
										SQL.insert(TableType.Warps, "'" + args[0].toLowerCase(Locale.UK).replaceAll("'", "''") + "','" + loc.getWorld().getName() + ">" + loc.getX() + ">" + loc.getY() + ">" + loc.getZ() + ">" + loc.getYaw() + ">" + loc.getPitch() + "'");
									} catch (Exception exception) {
										exception.printStackTrace();
									}
									if (getProfile(sender).rank.isAdmin()) {
										sender.sendMessage("§7Created warp §d" + args[0]);
									} else {
										getProfile(sender).money -= 20;
										incrementOwnerBalance(20);
										sender.sendMessage("§7Created warp §d" + args[0] + " §c-$20");
									}
									getProfile(sender).warps.add(args[0].toLowerCase(Locale.UK));
									WarpMarkers.setWarp(args[0], loc);
								} else {
									sender.sendMessage("§7A warp named §d" + args[0] + " §7already exists");
								}
							}
						}
					} else {
						sender.sendMessage("§7The maximum warp name length is 32 characters");
					}
				} else {
					sender.sendMessage("§5You don't have enough money for this item");
					sender.sendMessage("§dYou need to earn $" + (20 - getProfile(sender).money));
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/" + command.getName().toLowerCase(Locale.UK) + " [warpName]");
			}
			return true;
		}
		//
		// Enchant Command
		//
		if (command.getName().equalsIgnoreCase("enchant")) {
			if ((!isInSurvival(player) && !isInMinigame(player, MinigameType.SKYBLOCK)) || getProfile(sender).rank.isHigher(Rank.TYCOON)) {
				if (args.length == 2) {
					if (isInteger(args[1])) {
						try {
							player.getItemInHand().addEnchantment(getEnchantment(args[0].toUpperCase()), Integer.parseInt(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.ARROW_DAMAGE) sender.sendMessage("§7Item enchanted with the power of damage " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.ARROW_FIRE) sender.sendMessage("§7Item enchanted with flame");
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.ARROW_INFINITE) sender.sendMessage("§7Item enchanted with infinite arrows");
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.ARROW_KNOCKBACK) sender.sendMessage("§7Item enchanted with knockback " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.DAMAGE_ALL) sender.sendMessage("§7Item enchanted with sharpness " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.DAMAGE_ARTHROPODS) sender.sendMessage("§7Item enchanted with bane of arthropods " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.DAMAGE_UNDEAD) sender.sendMessage("§7Item enchanted with smite " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.DIG_SPEED) sender.sendMessage("§7Item enchanted with efficiency " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.DURABILITY) sender.sendMessage("§7Item enchanted with unbreaking " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.FIRE_ASPECT) sender.sendMessage("§7Item enchanted with fire aspect " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.KNOCKBACK) sender.sendMessage("§7Item enchanted with knockback " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.LOOT_BONUS_BLOCKS) sender.sendMessage("§7Item enchanted with fortune " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.LOOT_BONUS_MOBS) sender.sendMessage("§7Item enchanted with looting " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.OXYGEN) sender.sendMessage("§7Item enchanted with respiration " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.PROTECTION_ENVIRONMENTAL) sender.sendMessage("§7Item enchanted with protection " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.PROTECTION_EXPLOSIONS) sender.sendMessage("§7Item enchanted with explosive protection " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.PROTECTION_FALL) sender.sendMessage("§7Item enchanted with fall protection " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.PROTECTION_FIRE) sender.sendMessage("§7Item enchanted with fire protection " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.PROTECTION_PROJECTILE) sender.sendMessage("§7Item enchanted with projectile protection " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.SILK_TOUCH) sender.sendMessage("§7Item enchanted with silk touch");
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.THORNS) sender.sendMessage("§7Item enchanted with thorns " + toRomanNumerals(args[1]));
							if (getEnchantment(args[0].toUpperCase()) == Enchantment.WATER_WORKER) sender.sendMessage("§7Item enchanted with aqua affinity");
						} catch (Exception exception) {
							if (getEnchantment(args[0].toUpperCase()) == null) {
								sender.sendMessage("§7This enchantment doesn't exist");
							} else if (Integer.parseInt(args[1]) > getEnchantment(args[0].toUpperCase()).getMaxLevel()) {
								sender.sendMessage("§7The maximum level for this enchantment is " + getEnchantment(args[0].toUpperCase()).getMaxLevel());
							} else {
								sender.sendMessage("§7This item can't have this enchantment");
							}
						}
					} else {
						sender.sendMessage("§7Please enter a valid enchantment level");
					}
				} else {
					ChatExtensions.sendCommandHelpMessage(player, "/enchant [enchantmentID] [enchantmentLevel]");
				}
			} else {
				sender.sendMessage("§7Items can't be enchanted in survival via command");
			}
			return true;
		}
		//
		// Back Command
		//
		if (command.getName().equalsIgnoreCase("back") || command.getName().equalsIgnoreCase("return")) {
			if (getProfile(sender).lastLocation != null) {
				player.teleport(getProfile(sender).lastLocation);
				sender.sendMessage("§7Teleported to your last position");
			} else {
				sender.sendMessage("§7You haven't teleported or died recently");
			}
			return true;
		}
		//
		// Storm Command
		//
		if (command.getName().equalsIgnoreCase("storm")) {
			if ((!isInSurvival(player) && !isInMinigame(player, MinigameType.SKYBLOCK)) || getProfile(sender).rank.isHigher(Rank.ELITE)) {
				player.getWorld().setThundering(true);
				player.getWorld().setStorm(true);
				sender.sendMessage("§7World weather changed to stormy");
			} else {
				sender.sendMessage("§7Weather can't be changed in survival");
			}
			return true;
		}
		//
		// Rain Command
		//
		if (command.getName().equalsIgnoreCase("rain")) {
			if ((!isInSurvival(player) && !isInMinigame(player, MinigameType.SKYBLOCK)) || getProfile(sender).rank.isHigher(Rank.ELITE)) {
				player.getWorld().setStorm(true);
				sender.sendMessage("§7World weather changed to rainy");
			} else {
				sender.sendMessage("§7Weather can't be changed in survival");
			}
			return true;
		}
		//
		// Sun Command
		//
		if (command.getName().equalsIgnoreCase("sun")) {
			if ((!isInSurvival(player) && !isInMinigame(player, MinigameType.SKYBLOCK)) || getProfile(sender).rank.isHigher(Rank.ELITE)) {
				player.getWorld().setThundering(false);
				player.getWorld().setStorm(false);
				sender.sendMessage("§7World weather changed to sunny");
			} else {
				sender.sendMessage("§7Weather can't be changed in survival");
			}
			return true;
		}
		//
		// AFK Command
		//
		if (command.getName().equalsIgnoreCase("afk") || command.getName().equalsIgnoreCase("away")) {
			if (!getProfile(player).isAway) {
				getProfile(player).isAway = true;
				broadcastPlayerMessage(sender.getName(), "§5" + player.getDisplayName() + " §dhas gone AFK");
				getProfile(player).updatePlayerListName();
			} else {
				sender.sendMessage("§7You are already AFK");
			}
			return true;
		}
		//
		// Time Command
		//
		if (command.getName().equalsIgnoreCase("time")) {
			if ((!isInSurvival(player) && !isInMinigame(player, MinigameType.SKYBLOCK)) || getProfile(sender).rank.isHigher(Rank.ELITE)) {
				if (args.length == 1) {
					if (isInteger(args[0])) {
						player.getWorld().setTime(Long.parseLong(args[0]));
					} else if (args[0].equalsIgnoreCase("dawn")) {
						player.getWorld().setTime(0L);
					} else if (args[0].equalsIgnoreCase("day")) {
						player.getWorld().setTime(6000L);
					} else if (args[0].equalsIgnoreCase("dusk")) {
						player.getWorld().setTime(12000L);
					} else if (args[0].equalsIgnoreCase("night")) {
						player.getWorld().setTime(18000L);
					} else {
						sender.sendMessage("§7This is not a valid time");
						return true;
					}
					sender.sendMessage("§7World time changed");
				} else {
					ChatExtensions.sendCommandHelpMessage(player, 
							Arrays.asList("/time [time]",
									"/time dawn",
									"/time day",
									"/time dusk",
									"/time night"));
				}
			} else {
				sender.sendMessage("§7Time can't be changed in survival");
			}
			return true;
		}
		//
		// Set Spawn Command
		//
		if (command.getName().equalsIgnoreCase("setspawn")) {
			player.getWorld().setSpawnLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
			sender.sendMessage("§7World spawn set to " + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ());
			return true;
		}
		//
		// Spawn Command
		//
		if (command.getName().equalsIgnoreCase("spawn")) {
			player.teleport(player.getWorld().getSpawnLocation());
			return true;
		}
		//
		// Butcher Command
		//
		if (command.getName().equalsIgnoreCase("butcher") || command.getName().equalsIgnoreCase("remove") || command.getName().equalsIgnoreCase("killall") || command.getName().equalsIgnoreCase("mobkill")) {
			if ((!isInSurvival(player) && !isInMinigame(player, MinigameType.SKYBLOCK)) || getProfile(sender).rank.isHigher(Rank.TYCOON)) {
				int entities = player.getWorld().getLivingEntities().size();
				for (LivingEntity entity : player.getWorld().getLivingEntities()) if (entity.getType() == EntityType.PLAYER || (entity instanceof Tameable && ((Tameable)entity).isTamed())) entities--; else entity.remove();
				sender.sendMessage("§5" + entities + "§d animals butchered");
				alert(sender.getName() + " executed the butcher command");
			} else {
				sender.sendMessage("§7Animals can't be butchered in survival");
			}
			return true;
		}
		//
		// Hug Command
		//
		if (command.getName().equalsIgnoreCase("hug")) {
			if (args.length >= 1) {
				if (getPlayer(args[0]) != null) {
					broadcastPlayerMessage(sender.getName(), player.getDisplayName() + " hugs " + getPlayer(args[0]).getDisplayName());
				} else {
					broadcastPlayerMessage(sender.getName(), player.getDisplayName() + " hugs " + args[0]);
				}
			} else {
				broadcastPlayerMessage(sender.getName(), player.getDisplayName() + " hugs themselves");
			}
			return true;
		}
		//
		// Kiss Command
		//
		if (command.getName().equalsIgnoreCase("kiss")) {
			if (args.length >= 1) {
				if (getPlayer(args[0]) != null) {
					broadcastPlayerMessage(sender.getName(), player.getDisplayName() + " kisses " + getPlayer(args[0]).getDisplayName());
				} else {
					broadcastPlayerMessage(sender.getName(), player.getDisplayName() + " kisses " + args[0]);
				}
			} else {
				broadcastPlayerMessage(sender.getName(), player.getDisplayName() + " kisses themselves");
			}
			return true;
		}
		//
		// Clear Inventory Command
		//
		if (command.getName().equalsIgnoreCase("clear")) {
			player.getInventory().clear();
			sender.sendMessage("§7Your inventory has been cleared");
			return true;
		}
		//
		// Dawn Time Command
		//
		if (command.getName().equalsIgnoreCase("dawn")) {
			if ((!isInSurvival(player) && !isInMinigame(player, MinigameType.SKYBLOCK)) || getProfile(sender).rank.isHigher(Rank.ELITE)) {
				player.getWorld().setTime(0L);
				sender.sendMessage("§7The world time has been changed to dawn");
			} else {
				sender.sendMessage("§7Time can't be changed in survival");
			}
			return true;
		}
		//
		// Day Time Command
		//
		if (command.getName().equalsIgnoreCase("day")) {
			if ((!isInSurvival(player) && !isInMinigame(player, MinigameType.SKYBLOCK)) || getProfile(sender).rank.isHigher(Rank.ELITE)) {
				player.getWorld().setTime(6000L);
				sender.sendMessage("§7The world time has been changed to day");
			} else {
				sender.sendMessage("§7Time can't be changed in survival");
			}
			return true;
		}
		//
		// Dusk Time Command
		//
		if (command.getName().equalsIgnoreCase("dusk")) {
			if ((!isInSurvival(player) && !isInMinigame(player, MinigameType.SKYBLOCK)) || getProfile(sender).rank.isHigher(Rank.ELITE)) {
				player.getWorld().setTime(12000L);
				sender.sendMessage("§7The world time has been changed to dusk");
			} else {
				sender.sendMessage("§7Time can't be changed in survival");
			}
			return true;
		}
		//
		// Night Time Command
		//
		if (command.getName().equalsIgnoreCase("night")) {
			if ((!isInSurvival(player) && !isInMinigame(player, MinigameType.SKYBLOCK)) || getProfile(sender).rank.isHigher(Rank.ELITE)) {
				player.getWorld().setTime(18000L);
				sender.sendMessage("§7The world time has been changed to night");
			} else {
				sender.sendMessage("§7Time can't be changed in survival");
			}
			return true;
		}
		//
		// Accept Teleport Request Command
		//
		if (command.getName().equalsIgnoreCase("accept") || command.getName().equalsIgnoreCase("tpaccept")) {
			if (getProfile(sender).teleportantName != null) {
				if (getPlayer(getProfile(sender).teleportantName) != null) {
					getPlayer(getProfile(sender).teleportantName).teleport(player);
					getPlayer(getProfile(sender).teleportantName).sendMessage("§7Teleport request accepted");
					getProfile(sender).teleportantName = null;
				} else {
					sender.sendMessage("§7The player who sent the request isn't online");
				}
			}
			return true;
		}
		//
		// Teleport To Player Command
		//
		if (command.getName().equalsIgnoreCase("tp") || command.getName().equalsIgnoreCase("tpa") || command.getName().equalsIgnoreCase("teleport")) {
			if (args.length == 1) {
				if (getPlayer(args[0]) != null) {
					if ((isInMinigame(getPlayer(args[0]), MinigameType.SKYBLOCK) && getProfile(sender).rank != Rank.SERVER_HOST) || !getProfile(sender).rank.isHigher(getProfile(getPlayer(args[0])).rank.getPreviousRank())) {
						getProfile(getPlayer(args[0])).teleportantName = sender.getName();
						getPlayer(args[0]).sendMessage("§d" + player.getDisplayName() + " §dwishes to teleport to you");
						getPlayer(args[0]).sendMessage("§aTo accept the request type /accept");
						if (isInMinigame(getPlayer(args[0]), MinigameType.SKYBLOCK)) sender.sendMessage(getPlayer(args[0]).getDisplayName() + "§7 is in the SkyBlock Survival minigame");
						sender.sendMessage("§7A teleport request has been sent to " + getPlayer(args[0]).getDisplayName());
					} else {
						player.teleport(getPlayer(args[0]));
					}
				} else {
					sender.sendMessage("§7You can't teleport to an offline player");
				}
			} else if (args.length == 3) {
				if (isDouble(args[0]) && isDouble(args[1]) && isDouble(args[2])) {
					Location destination = player.getLocation();
					destination.setX(Double.valueOf(args[0]));
					destination.setY(Double.valueOf(args[1]));
					destination.setZ(Double.valueOf(args[2]));
					if (destination.getBlockX() > 12550820 || destination.getBlockX() < -12550820 || destination.getBlockZ() > 12550820 || destination.getBlockZ() < -12550820) {
						player.sendMessage("§7The Far Lands are blocked");
					} else {
						player.teleport(destination);
						sender.sendMessage("§7Teleported to " + args[0] + ", " + args[1] + ", " + args[2]);
					}
				} else {
					ChatExtensions.sendCommandHelpMessage(player, "/" + command.getName().toLowerCase(Locale.UK) + " [x] [y] [z]");
					sender.sendMessage("§7The X, Y and Z values must be numbers");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/" + command.getName().toLowerCase(Locale.UK) + " [player]",
								"/" + command.getName().toLowerCase(Locale.UK) + " [x] [y] [z]"));
			}
			return true;
		}
		//
		// Teleport Player Here Command
		//
		if (command.getName().equalsIgnoreCase("tphere") || command.getName().equalsIgnoreCase("teleporthere")) {
			if (args.length == 1) {
				if (getPlayer(args[0]) != null) {
					if ((!isInSurvival(getPlayer(args[0])) && !isInMinigame(getPlayer(args[0]), MinigameType.SKYBLOCK)) || getProfile(sender).rank.isHigher(Rank.ELITE)) {
						getPlayer(args[0]).teleport(player);
					} else {
						sender.sendMessage("§7You can't teleport a player who is in survival");
					}
				} else {
					sender.sendMessage("§7You can't teleport an offline player");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, "/" + command.getName().toLowerCase(Locale.UK) + " [player]");
			}
			return true;
		}
		//
		// Give Command
		//
		if (command.getName().equalsIgnoreCase("item") || command.getName().equalsIgnoreCase("give")) {
			if ((isInSurvival(player) || isInMinigame(player, MinigameType.SKYBLOCK)) && getProfile(sender).rank != Rank.SERVER_HOST) {
				sender.sendMessage("§7Spawning items is blocked in survival");
			} else {
				if (args.length == 1) {
					Material material = BlockReference.getBlockMaterial(args[0]);
					if (material == null) {
						player.sendMessage("§7Please enter a valid name or ID");
					} else {
						player.getInventory().addItem(new ItemStack(material));
					}
				} else if (args.length == 2) {
					Material material = BlockReference.getBlockMaterial(args[0]);
					if (material == null) {
						player.sendMessage("§7Please enter a valid name or ID");
					} else if (!isInteger(args[1])) {
						player.sendMessage("§7Please enter a valid amount");
					} else {
						player.getInventory().addItem(new ItemStack(material, Integer.parseInt(args[1])));
					}
				} else if (args.length == 3) {
					Material material = BlockReference.getBlockMaterial(args[0]);
					if (material == null) {
						player.sendMessage("§7Please enter a valid name or ID");
					} else if (!isInteger(args[1])) {
						player.sendMessage("§7Please enter a valid amount");
					} else if (!isByte(args[2])) {
						player.sendMessage("§7Please enter a valid data value");
					} else {
						player.getInventory().addItem(new ItemStack(material, Integer.parseInt(args[1]), Byte.parseByte(args[2])));
					}
				} else {
					ChatExtensions.sendCommandHelpMessage(player, "/" + command.getName().toLowerCase(Locale.UK) + " [ID / name] <amount> <data>");
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Log information to the minecraft server console
	 * @param information The information to log
	 */
	public static void logInfo(String information) {
		Logger.getLogger("Minecraft").log(Level.INFO, "[EvilBook] " + information);
	}

	/**
	 * Log severe information to the minecraft server console
	 * @param information The severe information to log
	 */
	public static void logSevere(String information) {
		Logger.getLogger("Minecraft").log(Level.SEVERE, "[EvilBook] " + information);
	}

	/**
	 * Log warning information to the minecraft server console
	 * @param information The warning information to log
	 */
	public static void logWarning(String information) {
		Logger.getLogger("Minecraft").log(Level.WARNING, "[EvilBook] " + information);
	}

	/**
	 * Check if a string can be casted to an integer
	 * @param string The string to execute the cast check with
	 * @return If the string can be casted to an integer
	 */
	public static Boolean isInteger(String string) {
		try {Integer.parseInt(string); return true;} catch (Exception exception) {return false;}
	}

	/**
	 * Check if a string can be casted to a double
	 * @param string The string to execute the cast check with
	 * @return If the string can be casted to a double
	 */
	public static Boolean isDouble(String string) {
		try {Double.valueOf(string); return true;} catch (Exception exception) {return false;}
	}
	
	/**
	 * Check if a string can be casted to a float
	 * @param string The string to execute the cast check with
	 * @return If the string can be casted to a float
	 */
	public static Boolean isFloat(String string) {
		try {Float.valueOf(string); return true;} catch (Exception exception) {return false;}
	}

	/**
	 * Check if a string can be casted to a byte
	 * @param string The string to execute the cast check with
	 * @return If the string can be casted to a byte
	 */
	public static Boolean isByte(String string) {
		try {Byte.valueOf(string); return true;} catch (Exception exception) {return false;}
	}

	/**
	 * Returns a player profile for the command sender
	 * @param sender The sender to fetch the profile of
	 * @return The player profile of the sender
	 */
	public static PlayerProfile getProfile(CommandSender sender) {
		return playerProfiles.get(sender.getName().toLowerCase(Locale.UK));
	}

	/**
	 * Returns a player profile for the player
	 * @param player The player name to fetch the profile of
	 * @return The player profile of the player
	 */
	public static PlayerProfile getProfile(String player) {
		if (playerProfiles.containsKey(getPlayer(player).getName().toLowerCase(Locale.UK))) return playerProfiles.get(getPlayer(player).getName().toLowerCase(Locale.UK));
		return null;
	}

	/**
	 * Sends a message alert to copper staff and above ranks
	 * @param alert The message
	 */
	private static void alert(String alert) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			PlayerProfile profile = getProfile(player);
			if (profile.rank.isHigher(Rank.POLICE)) Bukkit.getServer().getPlayer(profile.name).sendMessage("§7§O" + alert);
		}
		/*
		 * Issue with profiles not unloading?
		for (PlayerProfile profile : playerProfiles.values()) {
			if (profile.rank.isHigher(Rank.POLICE)) Bukkit.getServer().getPlayer(profile.name).sendMessage("§7§O" + alert);
		}
		*/
	}

	/**
	 * Return the entity type from the entity type name
	 * @param name The name of the entity type
	 * @return The entity type
	 */
	private static EntityType getEntity(String name) {
		if (EntityType.fromName(name) != null) return EntityType.fromName(name);
		try {if (EntityType.fromId(Integer.parseInt(name)) != null) return EntityType.fromId(Integer.parseInt(name));} catch (Exception exception) { /*This is fine it just means its not a number*/ }
		if (name.equalsIgnoreCase("mooshroom")) return EntityType.MUSHROOM_COW;
		if (name.equalsIgnoreCase("ocelot") || name.equalsIgnoreCase("cat")) return EntityType.OCELOT;
		if (name.equalsIgnoreCase("zombiepigman")) return EntityType.PIG_ZOMBIE;
		if (name.equalsIgnoreCase("dog")) return EntityType.WOLF;
		if (name.equalsIgnoreCase("magmacube")) return EntityType.MAGMA_CUBE;
		if (name.equalsIgnoreCase("witherskeleton")) return EntityType.WITHER_SKULL;
		if (name.equalsIgnoreCase("snowgolem")) return EntityType.SNOWMAN;
		if (name.equalsIgnoreCase("irongolem") || name.equalsIgnoreCase("ironman")) return EntityType.IRON_GOLEM;
		if (name.equalsIgnoreCase("wither")) return EntityType.WITHER;
		if (name.equalsIgnoreCase("firework")) return EntityType.FIREWORK;
		if (name.equalsIgnoreCase("lightning")) return EntityType.LIGHTNING;
		if (name.equalsIgnoreCase("tnt")) return EntityType.PRIMED_TNT;
		if (name.equalsIgnoreCase("horse")) return EntityType.HORSE;
		return null;
	}

	/**
	 * Return the enchantment from the enchantment name
	 * @param name The name of the enchantment
	 * @return The enchantment
	 */
	private static Enchantment getEnchantment(String name) {
		if (Enchantment.getByName(name) != null) return Enchantment.getByName(name);
		try {if (Enchantment.getById(Integer.parseInt(name)) != null) return Enchantment.getById(Integer.parseInt(name));} catch (Exception exception) { /*This is fine it just means its not a number*/ }
		if (name.equalsIgnoreCase("power")) return Enchantment.ARROW_DAMAGE;
		if (name.equalsIgnoreCase("flame")) return Enchantment.ARROW_FIRE;
		if (name.equalsIgnoreCase("infinity")) return Enchantment.ARROW_INFINITE;
		if (name.equalsIgnoreCase("punch")) return Enchantment.ARROW_KNOCKBACK;
		if (name.equalsIgnoreCase("sharpness")) return Enchantment.DAMAGE_ALL;
		if (name.equalsIgnoreCase("baneofarthropods") || name.equalsIgnoreCase("bane")) return Enchantment.DAMAGE_ARTHROPODS;
		if (name.equalsIgnoreCase("smite")) return Enchantment.DAMAGE_UNDEAD;
		if (name.equalsIgnoreCase("efficiency")) return Enchantment.DIG_SPEED;
		if (name.equalsIgnoreCase("unbreaking")) return Enchantment.DURABILITY;
		if (name.equalsIgnoreCase("fireaspect")) return Enchantment.FIRE_ASPECT;
		if (name.equalsIgnoreCase("knockback")) return Enchantment.KNOCKBACK;
		if (name.equalsIgnoreCase("fortune")) return Enchantment.LOOT_BONUS_BLOCKS;
		if (name.equalsIgnoreCase("looting")) return Enchantment.LOOT_BONUS_MOBS;
		if (name.equalsIgnoreCase("respiration")) return Enchantment.OXYGEN;
		if (name.equalsIgnoreCase("protection")) return Enchantment.PROTECTION_ENVIRONMENTAL;
		if (name.equalsIgnoreCase("blastprotection")) return Enchantment.PROTECTION_EXPLOSIONS;
		if (name.equalsIgnoreCase("featherfalling")) return Enchantment.PROTECTION_FALL;
		if (name.equalsIgnoreCase("fireprotection")) return Enchantment.PROTECTION_FIRE;
		if (name.equalsIgnoreCase("projectileprotection")) return Enchantment.PROTECTION_PROJECTILE;
		if (name.equalsIgnoreCase("silktouch")) return Enchantment.SILK_TOUCH;
		if (name.equalsIgnoreCase("thorns")) return Enchantment.THORNS;
		if (name.equalsIgnoreCase("aquaaffinity")) return Enchantment.WATER_WORKER;
		return null;
	}

	/**
	 * Returns an itemstack of a book
	 * @param title The title of the book
	 * @param author The author of the book
	 * @param text The text in the book
	 * @return The book itemstack
	 */
	static ItemStack getBook(String title, String author, List<String> text) {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta)book.getItemMeta();
		meta.setTitle(title);
		meta.setAuthor(author);
		meta.setPages(text);
		book.setItemMeta(meta);
		return book;
	}

	/**
	 * Returns the minecraft world time
	 * @param world The world to obtain the time of
	 * @return The time of the minecraft world
	 */
	public static String getTime(World world) {
		return (int)(Math.floor(world.getTime() / 1000.0)) + ":" + ((int) ((world.getTime() % 1000.0) / 1000.0 * 60) < 10 ? "0" + (int) ((world.getTime() % 1000.0) / 1000.0 * 60) : (int) ((world.getTime() % 1000.0) / 1000.0 * 60));
	}

	/**
	 * Returns a string with characters replaced with other specified characters ignoring case on replacement
	 * @param text The text
	 * @param search The search word to replace
	 * @param replacement The world to replace with
	 * @return The string after all instances of the search word, ignoring case, are replaced
	 */
	public static String replaceAllIgnoreCase(final String text, final String search, final String replacement){
		if(search.equals(replacement)) return text;
		final StringBuffer buffer = new StringBuffer(text);
		final String lowerSearch = search.toLowerCase(Locale.UK);
		int i = 0;
		int prev = 0;
		while((i = buffer.toString().toLowerCase(Locale.UK).indexOf(lowerSearch, prev)) > -1){
			buffer.replace(i, i+search.length(), replacement);
			prev = i + replacement.length();
		}
		return buffer.toString();
	}

	/**
	 * Returns a string number as roman numerals
	 * @param number The number to convert into roman numerals
	 * @return The string number as roman numerals
	 */
	private static String toRomanNumerals(String number) {
		if (number.equals("1")) return "I";
		if (number.equals("2")) return "II";
		if (number.equals("3")) return "III";
		if (number.equals("4")) return "IV";
		if (number.equals("5")) return "V";
		return null;
	}

	public static String toStrippedString(String rawText) {
		//
		// Color formatting
		//
		String text = rawText;
		if (text.contains("§")) {
			text = replaceAllIgnoreCase(text, "§a", "");
			text = replaceAllIgnoreCase(text, "§b", "");
			text = replaceAllIgnoreCase(text, "§c", "");
			text = replaceAllIgnoreCase(text, "§d", "");
			text = replaceAllIgnoreCase(text, "§e", "");
			text = replaceAllIgnoreCase(text, "§f", "");
			text = replaceAllIgnoreCase(text, "§k", "");
			text = replaceAllIgnoreCase(text, "§l", "");
			text = replaceAllIgnoreCase(text, "§m", "");
			text = replaceAllIgnoreCase(text, "§n", "");
			text = replaceAllIgnoreCase(text, "§o", "");
			text = replaceAllIgnoreCase(text, "§r", "");
			text = text.replaceAll("§0", "");
			text = text.replaceAll("§1", "");
			text = text.replaceAll("§2", "");
			text = text.replaceAll("§3", "");
			text = text.replaceAll("§4", "");
			text = text.replaceAll("§5", "");
			text = text.replaceAll("§6", "");
			text = text.replaceAll("§7", "");
			text = text.replaceAll("§8", "");
			text = text.replaceAll("§9", "");
		}
		text = text.replaceAll("<3", "");
		text = text.replaceAll(":\\)", "");
		text = text.replaceAll("\\(:", "");
		text = text.replaceAll(":\\(", "");
		text = text.replaceAll("\\):", "");
		if (text.contains("(") && text.contains(")")) {
			text = text.replaceAll("(male)", "");
			text = text.replaceAll("(female)", "");
			text = text.replaceAll("(music)", "");
			text = text.replaceAll("(check)", "");
			text = text.replaceAll("(x)", "");
			text = text.replaceAll("(c)", "");
			text = text.replaceAll("(r)", "");
			text = text.replaceAll("(wait)", "");
			text = text.replaceAll("(star)", "");
			text = text.replaceAll("(phone)", "");
			text = text.replaceAll("(yingyang)", "");
			text = text.replaceAll("(skull)", "");
			text = text.replaceAll("(radioactive)", "");
			text = text.replaceAll("(bio)", "");
			text = text.replaceAll("(airplane)", "");
			text = text.replaceAll("(mail)", "");
			text = text.replaceAll("(arrow)", "");
			text = text.replaceAll("(armystar)", "");
			text = text.replaceAll("(scissors)", "");
			text = text.replaceAll("(peace)", "");
			text = text.replaceAll("(shade)", "");
			text = text.replaceAll("(mediumshade)", "");
			text = text.replaceAll("(darkshade)", "");
			text = text.replaceAll("(box)", "");
			text = text.replaceAll("(cbox)", "");
			text = text.replaceAll("(xbox)", "");
			text = text.replaceAll("(triangle)", "");
			text = text.replaceAll("(square)", "");
			text = text.replaceAll("(circle)", "");
			text = text.replaceAll("-_*", "");
			text = text.replaceAll("(sun)", "");
			text = text.replaceAll("(sun2)", "");
			text = text.replaceAll("(moon)", "");
			text = text.replaceAll("(moon2)", "");
			text = text.replaceAll("(cloud)", "");
			text = text.replaceAll("(snowman)", "");
			text = text.replaceAll("(umbrella)", "");
			text = text.replaceAll("(flower)", "");
			text = text.replaceAll("(flower2)", "");
			text = text.replaceAll("(comet)", "");
			text = text.replaceAll("(zap)", "");
			text = text.replaceAll("(snow)", "");
		}
		return text;
	}
	
	/**
	 * Returns a formatted string
	 * @param text The text to format
	 * @return The formatted string
	 */
	public static String toFormattedString(String rawText) {
		//
		// Color formatting
		//
		String text = rawText;
		if (text.contains("&")) {
			text = replaceAllIgnoreCase(text, "&a", "§a");
			text = replaceAllIgnoreCase(text, "&b", "§b");
			text = replaceAllIgnoreCase(text, "&c", "§c");
			text = replaceAllIgnoreCase(text, "&d", "§d");
			text = replaceAllIgnoreCase(text, "&e", "§e");
			text = replaceAllIgnoreCase(text, "&f", "§f");
			text = replaceAllIgnoreCase(text, "&k", "§k");
			text = replaceAllIgnoreCase(text, "&l", "§l");
			text = replaceAllIgnoreCase(text, "&m", "§m");
			text = replaceAllIgnoreCase(text, "&n", "§n");
			text = replaceAllIgnoreCase(text, "&o", "§o");
			text = replaceAllIgnoreCase(text, "&r", "§r");
			text = text.replaceAll("&0", "§0");
			text = text.replaceAll("&1", "§1");
			text = text.replaceAll("&2", "§2");
			text = text.replaceAll("&3", "§3");
			text = text.replaceAll("&4", "§4");
			text = text.replaceAll("&5", "§5");
			text = text.replaceAll("&6", "§6");
			text = text.replaceAll("&7", "§7");
			text = text.replaceAll("&8", "§8");
			text = text.replaceAll("&9", "§9");
		}
		text = text.replaceAll("<3", "❤");
		text = text.replaceAll(":\\)", "☺");
		text = text.replaceAll("\\(:", "☺");
		text = text.replaceAll(":\\(", "☹");
		text = text.replaceAll("\\):", "☹");
		if (text.contains("(") && text.contains(")")) {
			text = text.replaceAll("\\(male\\)", "♂");
			text = text.replaceAll("\\(female\\)", "♀");
			text = text.replaceAll("\\(music\\)", "♪♫♫♪");
			text = text.replaceAll("\\(check\\)", "✔");
			text = text.replaceAll("\\(x\\)", "✖");
			text = text.replaceAll("\\(c\\)", "©");
			text = text.replaceAll("\\(r\\)", "®");
			text = text.replaceAll("\\(wait\\)", "⌛");
			text = text.replaceAll("\\(star\\)", "★");
			text = text.replaceAll("\\(phone\\)", "☎");
			text = text.replaceAll("\\(yingyang\\)", "☯");
			text = text.replaceAll("\\(skull\\)", "☠");
			text = text.replaceAll("\\(radioactive\\)", "☢");
			text = text.replaceAll("\\(bio\\)", "☣");
			text = text.replaceAll("\\(airplane\\)", "✈");
			text = text.replaceAll("\\(mail\\)", "✉");
			text = text.replaceAll("\\(arrow\\)", "➸");
			text = text.replaceAll("\\(armystar\\)", "✪");
			text = text.replaceAll("\\(scissors\\)", "✁");
			text = text.replaceAll("\\(peace\\)", "☮");
			text = text.replaceAll("\\(shade\\)", "░");
			text = text.replaceAll("\\(mediumshade\\)", "▒");
			text = text.replaceAll("\\(darkshade\\)", "▓");
			text = text.replaceAll("\\(box\\)", "☐");
			text = text.replaceAll("\\(cbox\\)", "☑");
			text = text.replaceAll("\\(xbox\\)", "☒");
			text = text.replaceAll("\\(triangle\\)", "▲");
			text = text.replaceAll("\\(square\\)", "■");
			text = text.replaceAll("\\(circle\\)", "○");
			text = text.replaceAll("\\(sun\\)", "☼");
			text = text.replaceAll("\\(sun2\\)", "☀");
			text = text.replaceAll("\\(moon\\)", "☾");
			text = text.replaceAll("\\(moon2\\)", "☽");
			text = text.replaceAll("\\(cloud\\)", "☁");
			text = text.replaceAll("\\(snowman\\)", "☃");
			text = text.replaceAll("\\(umbrella\\)", "☂");
			text = text.replaceAll("\\(flower\\)", "✿");
			text = text.replaceAll("\\(flower2\\)", "❀");
			text = text.replaceAll("\\(comet\\)", "☄");
			text = text.replaceAll("\\(zap\\)", "ϟ");
			text = text.replaceAll("\\(snow\\)", "❅");
		}
		return text;
	}

	/**
	 * Returns the weather state in the blocks biome as a string
	 * @param block The block to get the weather data from
	 * @return The weather state as a string at the block
	 */
	public static String getWeather(Block block) {
		if (block.getWorld().hasStorm() && block.getWorld().isThundering() && block.getBiome() != Biome.FROZEN_OCEAN && block.getBiome() != Biome.FROZEN_RIVER && block.getBiome() != Biome.ICE_MOUNTAINS && block.getBiome() != Biome.ICE_FLATS && block.getBiome() != Biome.TAIGA && block.getBiome() != Biome.TAIGA_HILLS) return "Lightning";
		if (block.getWorld().hasStorm() && (block.getBiome() == Biome.FROZEN_OCEAN || block.getBiome() == Biome.FROZEN_RIVER || block.getBiome() == Biome.ICE_MOUNTAINS || block.getBiome() == Biome.ICE_FLATS || block.getBiome() == Biome.TAIGA || block.getBiome() == Biome.TAIGA_HILLS)) return "Snow";
		if (block.getWorld().hasStorm()) return "Rain";
		return "Sunny";
	}

	/**
	 * Return if a player profile exists
	 * @param playerName The player name to execute the check with
	 * @return If the player's profile is existant
	 */
	private static boolean isProfileExistant(String playerName) {
		return SQL.getString(TableType.PlayerProfile, playerName, "player_name") != null;
	}
	
	public static Boolean isInSurvival(Entity entity) {
		return entity.getWorld().getName().equals("SurvivalLand") || entity.getWorld().getName().equals("SurvivalLandNether") || entity.getWorld().getName().equals("SurvivalLandTheEnd") ? true : false;
	}
	
	public static Boolean isInSurvival(World world) {
		return world.getName().equals("SurvivalLand") || world.getName().equals("SurvivalLandNether") || world.getName().equals("SurvivalLandTheEnd") ? true : false;
	}
	
	public static Boolean isInMinigame(Entity entity, MinigameType minigame) {
		if (minigame == MinigameType.TOWER_DEFENSE) {
			return entity.getWorld().getName().equals("Minigame");
		} else if (minigame == MinigameType.SKYBLOCK) {
			return entity.getWorld().getName().contains("SkyBlock/");
		} else {
			return false;
		}
	}
	
	public static Boolean isInMinigame(World world, MinigameType minigame) {
		if (minigame == MinigameType.TOWER_DEFENSE) {
			return world.getName().equals("Minigame");
		} else if (minigame == MinigameType.SKYBLOCK) {
			return world.getName().contains("SkyBlock/");
		} else {
			return false;
		}
	}
	
	public static Boolean isInPlotWorld(Entity entity) {
		return entity.getWorld().getName().equals("PlotLand");
	}
	
	public static Boolean isInPrivateWorld(Entity entity) {
		return entity.getWorld().getName().contains("Private worlds") ? true : false;
	}
	
	public static Boolean isInPrivateWorld(World world) {
		return world.getName().contains("Private worlds") ? true : false;
	}

	/**
	 * Protect a container (Chest, furnace & brewing stand ect...) in the survival world
	 * @param location The position of the container in the world
	 * @param player The player owner of the container
	 */
	public static void protectContainer(Location location, Player player) {
		try {
			SQL.insert(TableType.ContainerProtection, 
					"'" + location.getWorld().getName() + "','" + location.getBlockX() + "','" + 
							location.getBlockY() + "','" + location.getBlockZ() + "','" + player.getName() + "'");
		} catch (Exception exception) {
			logSevere("Failed to create container protection record");
			exception.printStackTrace();
		}
	}

	/**
	 * Unprotect a container (Chest, furnace & brewing stand ect...) in the survival world
	 * @param location The position of the container in the world
	 */
	public static void unprotectContainer(Location location) {
		SQL.deleteRowFromCriteria(TableType.ContainerProtection, "world='" + location.getWorld().getName() + 
				"' AND x='" + location.getBlockX() + "' AND y='" + location.getBlockY() + "' AND z='" + location.getBlockZ() + "'");
	}

	/**
	 * Return if the container (Chest, furnace & brewing stand ect...) is protected to the player
	 * @param location The position of the container in the world
	 * @param player The player to execute the check with
	 * @return If the container is protected to the player
	 */
	public static Boolean isContainerProtected(Location location, Player player) {
		if (SQL.isRowExistant(TableType.ContainerProtection, "world='" + location.getWorld().getName() + 
				"' AND x='" + location.getBlockX() + "' AND y='" + location.getBlockY() + "' AND z='" + location.getBlockZ() + "'") &&
				!SQL.getStringFromCriteria(TableType.ContainerProtection, "world='" + location.getWorld().getName() + 
						"' AND x='" + location.getBlockX() + "' AND y='" + location.getBlockY() + "' AND z='" + location.getBlockZ() + "'"
						, "player_name").equals(player.getName())) {
			return true;
		}
		return false;
	}

	/**
	 * Return if the container (Chest, furnace & brewing stand ect...) is protected
	 * @param location The position of the container in the world
	 * @return If the container is protected
	 */
	public static Boolean isContainerProtected(Location location) {
		if (SQL.isRowExistant(TableType.ContainerProtection, "world='" + location.getWorld().getName() + 
				"' AND x='" + location.getBlockX() + "' AND y='" + location.getBlockY() + "' AND z='" + location.getBlockZ() + "'")) {
			return true;
		}
		return false;
	}

	/**
	 * Return the value of a private world property
	 * @param worldName The name of the private world
	 * @param property The property to fetch the value of
	 * @return The value of the private world property
	 */
	public static String getPrivateWorldProperty(String worldName, String property) {
		return new Properties("plugins/EvilBook/Private worlds/" + worldName + "/Config.properties").getProperty(property);
	}

	/**
	 * Broadcasts a player message to other players
	 * Warning: This makes use of the mute functionality
	 * @param message The message to broadcast
	 */
	public static void broadcastPlayerMessage(String playerName, String message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			PlayerProfile profile = getProfile(player);
			if (!profile.isMuted(playerName)) {
				profile.getPlayer().sendMessage(message);
			}
		}
		/*
		 * Issue with profiles not unloading?
		for (PlayerProfile profile : playerProfiles.values()) {
			try {
				if (!profile.isMuted(playerName)) {
					profile.getPlayer().sendMessage(message);
				}
			} catch (Exception e) {
				logSevere("Exception sending chat message to " + profile.name);
				e.printStackTrace();
			}
		}
		*/
	}

	private static void incrementOwnerBalance(int increment) {
		if (getPlayer(EvilBook.config.getProperty("server_host")) != null) {
			getProfile(EvilBook.config.getProperty("server_host")).money += increment;
			getPlayer(EvilBook.config.getProperty("server_host")).sendMessage("§7You have recieved §a$" + increment + " §7from taxes");
		} else {
			SQL.setInteger(TableType.PlayerProfile, EvilBook.config.getProperty("server_host"), "money",
					SQL.getInteger(TableType.PlayerProfile, EvilBook.config.getProperty("server_host"), "money") + increment);
		}
	}
	
	private static String getUnlockedTitles(Player player) {
		String titles = "";
		if (getProfile(player).rank.isAdmin()) {
			titles += "Elf Mr Mrs Miss Lord Dr Prof Crafter Epic ";
		}
		if (getProfile(player).hasAchievement(Achievement.GLOBAL_COMMAND_DONATE)) titles += "Supporter ";
		if (getProfile(player).hasAchievement(Achievement.GLOBAL_COMMAND_HELMET)) titles += "Stylish ";
		if (getProfile(player).hasAchievement(Achievement.GLOBAL_EQUIP_BUTTERARMOUR)) titles += "Butter ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_KILL_BLAZE)) titles += "Blazed ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_KILL_MAGMACUBE)) titles += "Magma ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_KILL_PLAYER)) titles += "Psycho ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_MINE_DIAMOND)) titles += "Miner ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_MINE_COAL_IV)) titles += "Dirty ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_MINE_IRON_IV)) titles += "Hardnut ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_MINE_LAPIS_IV)) titles += "Ultramarine ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_MINE_GOLD_IV)) titles += "Golden ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_MINE_DIAMOND_IV)) titles += "Hardcore ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_MINE_REDSTONE_IV)) titles += "Electric ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_MINE_EMERALD_IV)) titles += "Iced ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_MINE_NETHERQUARTZ_IV)) titles += "Nether ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_KILL_PIG_II)) titles += "Bacon ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_KILL_ENDERMAN_II)) titles += "Ender ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_KILL_ZOMBIEPIG_II)) titles += "Zomble ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_KILL_CREEPER_II)) titles += "Creep ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_KILL_GHAST_II)) titles += "Ghastly ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_KILL_SILVERFISH_II)) titles += "Silver ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_KILL_SLIME_II)) titles += "Slimey ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_KILL_WITCH_II)) titles += "Witch ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_KILL_ZOMBIE_II)) titles += "Zombie ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_KILL_RARE_III)) titles += "Rare ";
		if (getProfile(player).hasAchievement(Achievement.SURVIVAL_KILL_RARE_IV)) titles += "Legendary ";
		if (getProfile(player).hasAchievement(Achievement.SKYBLOCK_CRAFT_BED)) titles += "Sandman ";
		if (getProfile(player).hasAchievement(Achievement.SKYBLOCK_CRAFT_JUKEBOX)) titles += "Juke ";
		return titles;
	}
	
	private static String getPlayerIP(String playerName) {
		try (Statement statement = SQL.connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT player_name, ip FROM " + SQL.database + "." + TableType.PlayerProfile.tableName + " WHERE player_name='" + playerName + "';")) {
				if (!rs.isBeforeFirst()) return null;
				while (rs.next()) {
					if (rs.getString("ip") != null) {
						return rs.getString("ip");
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns a player from the player name
	 * @param name The name of the player
	 * @return The player
	 */
	private static Player getPlayer(String name) {
		if (Bukkit.getServer().getPlayer(name) != null) return Bukkit.getServer().getPlayer(name);
		for (PlayerProfile profile : playerProfiles.values()) {
			if (profile.name.toLowerCase(Locale.UK).startsWith(name.toLowerCase(Locale.UK)) || (profile instanceof PlayerProfileAdmin && ((PlayerProfileAdmin)profile).nameAlias != null && ((PlayerProfileAdmin)profile).getStrippedNameAlias().toLowerCase(Locale.UK).startsWith(name.toLowerCase(Locale.UK)))) return Bukkit.getServer().getPlayer(profile.name);
		}
		for (PlayerProfile profile : playerProfiles.values()) {
			if (profile.name.toLowerCase(Locale.UK).contains(name.toLowerCase(Locale.UK)) || (profile instanceof PlayerProfileAdmin && ((PlayerProfileAdmin)profile).nameAlias != null && ((PlayerProfileAdmin)profile).getStrippedNameAlias().toLowerCase(Locale.UK).contains(name.toLowerCase(Locale.UK)))) return Bukkit.getServer().getPlayer(profile.name);
		}
		return null;
	}
	
	/** round n down to nearest multiple of m */
	private static long roundDown(long n, long m) {
	    return n >= 0 ? (n / m) * m : ((n - m + 1) / m) * m;
	}
	 
	/** round n up to nearest multiple of m */
	private static long roundUp(long n, long m) {
	    return n >= 0 ? ((n + m - 1) / m) * m : (n / m) * m;
	}

	/**
	 * @return The EvilEdit session
	 */
	public Session getEditSession() {
		return editSession;
	}

	/**
	 * Set the current EvilEdit session
	 * @param editSession The EvilEdit session to be assigned
	 */
	public void setEditSession(Session editSession) {
		this.editSession = editSession;
	}
}