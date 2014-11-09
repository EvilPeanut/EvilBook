package com.amentrix.evilbook.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.TreeType;
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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
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
import com.amentrix.evilbook.invent.HatchTweak;
import com.amentrix.evilbook.listeners.EventListenerBlock;
import com.amentrix.evilbook.listeners.EventListenerEntity;
import com.amentrix.evilbook.listeners.EventListenerInventory;
import com.amentrix.evilbook.listeners.EventListenerPacket;
import com.amentrix.evilbook.listeners.EventListenerPlayer;
import com.amentrix.evilbook.listeners.EventListenerVehicle;
import com.amentrix.evilbook.map.Maps;
import com.amentrix.evilbook.nametag.NametagAPI;
import com.amentrix.evilbook.nametag.NametagEdit;
import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;
import com.amentrix.evilbook.statistics.GlobalStatistic;
import com.amentrix.evilbook.statistics.GlobalStatistics;
import com.amentrix.evilbook.statistics.PlayerStatistic;
import com.amentrix.evilbook.statistics.PlayerStatistics;
import com.amentrix.evilbook.worldgen.SkylandGenerator;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

import de.diddiz.LogBlock.Consumer;
import de.diddiz.LogBlock.LogBlock;

/**
 * EvilBook core class
 * @author Reece Aaron Lecrivain
 */
public class EvilBook extends JavaPlugin {
	// Reference lists
	public static Map<Material, List<String>> blockList = new LinkedHashMap<>();
	public static Map<Biome, List<String>> biomeList = new LinkedHashMap<>();
	public static Map<TreeType, List<String>> treeTypeList = new LinkedHashMap<>();
	//
	public static Map<String, PlayerProfile> playerProfiles = new HashMap<>();
	public static Map<String, Rank> commandBlacklist = new HashMap<>();
	public static List<DynamicSign> dynamicSignList = new ArrayList<>();
	public static List<String> paidWorldList = new ArrayList<>();
	public static List<Region> regionList = new ArrayList<>();
	public static List<Emitter> emitterList = new ArrayList<>();
	public static List<Location> inUseSurvivalWorkbenchesList = new ArrayList<>();
	public Session editSession = new Session(this);
	public Random random = new Random();
	// Log block API
	public static Consumer lbConsumer = null;
	// Dynmap API
	public static DynmapAPI dynmapAPI;
	public static MarkerAPI markerAPI;
	// Maps Module
	public Maps maps;

	/**
	 * Called when the plugin is enabled
	 */
	@Override
	public void onEnable() {
		//
		// Register event listeners
		//
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new EventListenerBlock(), this);
		pluginManager.registerEvents(new EventListenerEntity(), this);
		pluginManager.registerEvents(new EventListenerInventory(), this);
		pluginManager.registerEvents(new EventListenerPlayer(), this);
		pluginManager.registerEvents(new EventListenerVehicle(), this);
		//
		// Register EvilBook Invent listeners
		//
		pluginManager.registerEvents(new HatchTweak(), this);
		//
		// Maps Module
		//
		maps = new Maps(this);
		getCommand("map").setExecutor(this.maps);
		//
		// Check mandatory files and folders exist
		//
		File check = new File("plugins/EvilBook");
		if (check.exists() == false && check.mkdir() == false) logSevere("Failed to create directory 'plugins/EvilBook'");
		check = new File("plugins/EvilBook/Private worlds");
		if (check.exists() == false && check.mkdir() == false) logSevere("Failed to create directory 'plugins/EvilBook/Private worlds'");
		//
		// World generators
		//
		WorldCreator flatLand = new WorldCreator("FlatLand");
		flatLand.type(WorldType.FLAT);
		getServer().createWorld(flatLand);
		getServer().createWorld(new WorldCreator("SurvivalLand"));
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
		NametagEdit.load(this);
		//
		// Connect EvilBook to MySQL
		//
		if (!SQL.connect(this)) {
			logSevere("Failed to load MySQL module");
			getServer().shutdown();
			return;
		}
		logInfo("Loaded MySQL module");
		//
		// Preform SQL checks
		//
		// Make sure appropriate players have operator status
		/*
		try (Statement statement = SQL.connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.PlayerProfile.tableName + ";")) {
				while (rs.next()) {
					if (Rank.valueOf(rs.getString("rank")).isHigher(Rank.Police)) {
						if (!getServer().getOfflinePlayer(rs.getString("player_name")).isOp()) {
							getServer().getOfflinePlayer(rs.getString("player_name")).setOp(true);
							logInfo("Auto-fixed incorrect op status for" + rs.getString("player_name"));
						}
					} else if (getServer().getOfflinePlayer(rs.getString("player_name")).isOp()) {
						getServer().getOfflinePlayer(rs.getString("player_name")).setOp(false);
						logInfo("Auto-fixed incorrect op status for" + rs.getString("player_name"));
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		*/
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
		try (Statement statement = SQL.connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.Region.tableName + ";")) {
				while (rs.next()) {
					if (getServer().getWorld(rs.getString("world")) != null) {
						regionList.add(new Region(rs));
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
		try (Statement statement = SQL.connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.DynamicSign.tableName + ";")) {
				while (rs.next()) {
					if (getServer().getWorld(rs.getString("world")) != null) {
						dynamicSignList.add(new DynamicSign(rs));
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
		// Load command blacklist
		// 
		commandBlacklist.put("/setblock", Rank.SERVER_HOST);
		commandBlacklist.put("/restart", Rank.SERVER_HOST);
		commandBlacklist.put("/stop", Rank.SERVER_HOST);
		commandBlacklist.put("/op", Rank.SERVER_HOST);
		commandBlacklist.put("/reload", Rank.SERVER_HOST);
		commandBlacklist.put("/makeadmin", Rank.SERVER_HOST);
		commandBlacklist.put("/setspawn", Rank.SERVER_HOST);
		commandBlacklist.put("/xp", Rank.SERVER_HOST);
		commandBlacklist.put("/gamerule", Rank.SERVER_HOST);
		commandBlacklist.put("/deop", Rank.SERVER_HOST);
		commandBlacklist.put("/demote", Rank.SERVER_HOST);
		commandBlacklist.put("/drug", Rank.SERVER_HOST);
		//
		commandBlacklist.put("/drwatson", Rank.ADMIN_STAFF);
		//
		commandBlacklist.put("/hyperhorse", Rank.TYCOON);
		//
		commandBlacklist.put("/setrank", Rank.INVESTOR);
		//
		commandBlacklist.put("/pardon", Rank.ELITE);
		commandBlacklist.put("/promote", Rank.ELITE);
		//
		commandBlacklist.put("/regen", Rank.COUNCILLOR);
		commandBlacklist.put("//regen", Rank.COUNCILLOR);
		commandBlacklist.put("/copy", Rank.COUNCILLOR);
		commandBlacklist.put("//copy", Rank.COUNCILLOR);
		commandBlacklist.put("/cut", Rank.COUNCILLOR);
		commandBlacklist.put("//cut", Rank.COUNCILLOR);
		commandBlacklist.put("/paste", Rank.COUNCILLOR);
		commandBlacklist.put("//paste", Rank.COUNCILLOR);
		//
		commandBlacklist.put("/thaw", Rank.ADMIN);
		commandBlacklist.put("/move", Rank.ADMIN);
		commandBlacklist.put("//move", Rank.ADMIN);
		commandBlacklist.put("/snow", Rank.ADMIN);
		commandBlacklist.put("/flip", Rank.ADMIN);
		commandBlacklist.put("//flip", Rank.ADMIN);
		commandBlacklist.put("/sphere", Rank.ADMIN);
		commandBlacklist.put("//sphere", Rank.ADMIN);
		commandBlacklist.put("/hsphere", Rank.ADMIN);
		commandBlacklist.put("//hsphere", Rank.ADMIN);
		commandBlacklist.put("/esphere", Rank.ADMIN);
		commandBlacklist.put("//esphere", Rank.ADMIN);
		commandBlacklist.put("/cspawn", Rank.ADMIN);
		commandBlacklist.put("/spawncreature", Rank.ADMIN);
		commandBlacklist.put("/mob", Rank.ADMIN);
		commandBlacklist.put("/spawnmob", Rank.ADMIN);
		commandBlacklist.put("/forestgen", Rank.ADMIN);
		commandBlacklist.put("/forest", Rank.ADMIN);
		commandBlacklist.put("/pyramid", Rank.ADMIN);
		commandBlacklist.put("//pyramid", Rank.ADMIN);
		commandBlacklist.put("/hpyramid", Rank.ADMIN);
		commandBlacklist.put("//hpyramid", Rank.ADMIN);
		commandBlacklist.put("/hcylinder", Rank.ADMIN);
		commandBlacklist.put("//hcylinder", Rank.ADMIN);
		commandBlacklist.put("/cylinder", Rank.ADMIN);
		commandBlacklist.put("//cylinder", Rank.ADMIN);
		commandBlacklist.put("/hcyl", Rank.ADMIN);
		commandBlacklist.put("//hcyl", Rank.ADMIN);
		commandBlacklist.put("/cyl", Rank.ADMIN);
		commandBlacklist.put("//cyl", Rank.ADMIN);
		commandBlacklist.put("/pumpkins", Rank.ADMIN);
		commandBlacklist.put("/setbiome", Rank.ADMIN);
		commandBlacklist.put("//setbiome", Rank.ADMIN);
		commandBlacklist.put("/count", Rank.ADMIN);
		commandBlacklist.put("//count", Rank.ADMIN);
		commandBlacklist.put("/size", Rank.ADMIN);
		commandBlacklist.put("//size", Rank.ADMIN);
		commandBlacklist.put("/desel", Rank.ADMIN);
		commandBlacklist.put("//desel", Rank.ADMIN);
		commandBlacklist.put("/wand", Rank.ADMIN);
		commandBlacklist.put("//wand", Rank.ADMIN);
		commandBlacklist.put("/drain", Rank.ADMIN);
		commandBlacklist.put("//drain", Rank.ADMIN);
		commandBlacklist.put("/green", Rank.ADMIN);
		commandBlacklist.put("//green", Rank.ADMIN);
		commandBlacklist.put("/overlay", Rank.ADMIN);
		commandBlacklist.put("//overlay", Rank.ADMIN);
		commandBlacklist.put("/walls", Rank.ADMIN);
		commandBlacklist.put("//walls", Rank.ADMIN);
		commandBlacklist.put("/outline", Rank.ADMIN);
		commandBlacklist.put("//outline", Rank.ADMIN);
		commandBlacklist.put("/hollow", Rank.ADMIN);	
		commandBlacklist.put("//hollow", Rank.ADMIN);	
		commandBlacklist.put("/undo", Rank.ADMIN);
		commandBlacklist.put("//undo", Rank.ADMIN);
		commandBlacklist.put("/fill", Rank.ADMIN);
		commandBlacklist.put("//fill", Rank.ADMIN);
		commandBlacklist.put("/set", Rank.ADMIN);
		commandBlacklist.put("//set", Rank.ADMIN);
		commandBlacklist.put("/replace", Rank.ADMIN);
		commandBlacklist.put("//replace", Rank.ADMIN);
		commandBlacklist.put("/rreplace", Rank.ADMIN);
		commandBlacklist.put("/rfill", Rank.ADMIN);
		commandBlacklist.put("/rdel", Rank.ADMIN);
		commandBlacklist.put("/rdelete", Rank.ADMIN);
		commandBlacklist.put("/del", Rank.ADMIN);
		commandBlacklist.put("/delete", Rank.ADMIN);
		commandBlacklist.put("/tree", Rank.ADMIN);
		commandBlacklist.put("/rename", Rank.ADMIN);
		commandBlacklist.put("/tool", Rank.ADMIN);
		commandBlacklist.put("/toggleeditwand", Rank.ADMIN);
		//
		commandBlacklist.put("/region", Rank.STAFF_DIAMOND);
		//
		commandBlacklist.put("/vanish", Rank.STAFF_LAPIS);
		commandBlacklist.put("/hide", Rank.STAFF_LAPIS);
		commandBlacklist.put("/unvanish", Rank.STAFF_LAPIS);
		commandBlacklist.put("/show", Rank.STAFF_LAPIS);
		//
		commandBlacklist.put("/tphere", Rank.STAFF_GOLD);
		commandBlacklist.put("/teleporthere", Rank.STAFF_GOLD);
		//
		commandBlacklist.put("/clean", Rank.STAFF_SILVER);
		commandBlacklist.put("/butcher", Rank.STAFF_SILVER);
		//
		commandBlacklist.put("/broadcast", Rank.STAFF_COPPER);
		commandBlacklist.put("/say", Rank.STAFF_COPPER);
		commandBlacklist.put("/dawn", Rank.STAFF_COPPER);
		commandBlacklist.put("/day", Rank.STAFF_COPPER);
		commandBlacklist.put("/dusk", Rank.STAFF_COPPER);
		commandBlacklist.put("/night", Rank.STAFF_COPPER);
		commandBlacklist.put("/storm", Rank.STAFF_COPPER);
		commandBlacklist.put("/rain", Rank.STAFF_COPPER);
		commandBlacklist.put("/sun", Rank.STAFF_COPPER);
		commandBlacklist.put("/time", Rank.STAFF_COPPER);
		//
		commandBlacklist.put("/sky", Rank.MODERATOR);
		//
		commandBlacklist.put("/chimney", Rank.ARCHITECT);
		//
		// Load Block List
		//
		blockList.put(Material.AIR, Arrays.asList("Air", "Void"));
		blockList.put(Material.STONE, Arrays.asList("Stone", "SmoothStone"));
		blockList.put(Material.GRASS, Arrays.asList("Grass"));
		blockList.put(Material.DIRT, Arrays.asList("Dirt", "Mud", "Ground"));
		blockList.put(Material.COBBLESTONE, Arrays.asList("Cobble Stone", "CobbleStone", "Cobble"));
		blockList.put(Material.WOOD, Arrays.asList("Planks", "WoodPlanks", "WoodenPlanks"));
		blockList.put(Material.SAPLING, Arrays.asList("Sapling", "TreeSapling"));
		blockList.put(Material.BEDROCK, Arrays.asList("BedRock", "Adminium"));
		blockList.put(Material.WATER, Arrays.asList("Water"));
		blockList.put(Material.STATIONARY_WATER, Arrays.asList("Stationary Water", "StationaryWater", "StaticWater", "StillWater"));
		blockList.put(Material.LAVA, Arrays.asList("Lava"));
		blockList.put(Material.STATIONARY_LAVA, Arrays.asList("Stationary Lava", "StationaryLava", "StaticLava", "StillLava"));
		blockList.put(Material.SAND, Arrays.asList("Sand"));
		blockList.put(Material.GRAVEL, Arrays.asList("Gravel"));
		blockList.put(Material.GOLD_ORE, Arrays.asList("Gold Ore", "GoldOre", "Gold"));
		blockList.put(Material.IRON_ORE, Arrays.asList("Iron Ore", "IronOre", "Iron"));
		blockList.put(Material.COAL_ORE, Arrays.asList("Coal Ore", "CoalOre", "Coal"));
		blockList.put(Material.LOG, Arrays.asList("Wood", "Log"));
		blockList.put(Material.LEAVES, Arrays.asList("Leaves"));
		blockList.put(Material.SPONGE, Arrays.asList("Sponge"));
		blockList.put(Material.GLASS, Arrays.asList("Glass", "GlassBlock"));
		blockList.put(Material.LAPIS_ORE, Arrays.asList("Lapis Ore", "LapisOre", "LapisLazuliOre"));
		blockList.put(Material.LAPIS_BLOCK, Arrays.asList("Lapis Block", "LapisBlock", "LapisLazuliBlock"));
		blockList.put(Material.DISPENSER, Arrays.asList("Dispenser", "ItemDispenser"));
		blockList.put(Material.SANDSTONE, Arrays.asList("Sandstone"));
		blockList.put(Material.NOTE_BLOCK, Arrays.asList("Note Block", "NoteBlock"));
		blockList.put(Material.BED_BLOCK, Arrays.asList("Bed"));
		blockList.put(Material.POWERED_RAIL, Arrays.asList("Powered Rail", "PoweredRail", "PowerRail"));
		blockList.put(Material.DETECTOR_RAIL, Arrays.asList("Detector Rail", "DetectorRail", "PressureRail", "PressurePlateRail"));
		blockList.put(Material.PISTON_STICKY_BASE, Arrays.asList("Sticky Piston", "StickyPiston"));
		blockList.put(Material.WEB, Arrays.asList("Cobweb", "Web"));
		blockList.put(Material.LONG_GRASS, Arrays.asList("Tall Grass", "TallGrass"));
		blockList.put(Material.DEAD_BUSH, Arrays.asList("Dead Bush", "DeadBush", "DeadSapling"));
		blockList.put(Material.PISTON_BASE, Arrays.asList("Piston"));
		blockList.put(Material.PISTON_EXTENSION, Arrays.asList("Piston Extension"));
		blockList.put(Material.WOOL, Arrays.asList("Wool"));
		blockList.put(Material.PISTON_MOVING_PIECE, Arrays.asList("Block Moved By Piston"));
		blockList.put(Material.YELLOW_FLOWER, Arrays.asList("Dandelion"));
		blockList.put(Material.RED_ROSE, Arrays.asList("Poppy"));
		blockList.put(Material.BROWN_MUSHROOM, Arrays.asList("Brown Mushroom", "BrownMushroom", "Mushroom"));
		blockList.put(Material.RED_MUSHROOM, Arrays.asList("Red Mushroom", "RedMushroom"));
		blockList.put(Material.GOLD_BLOCK, Arrays.asList("Gold Block", "GoldBlock"));
		blockList.put(Material.IRON_BLOCK, Arrays.asList("Iron Block", "IronBlock"));
		blockList.put(Material.DOUBLE_STEP, Arrays.asList("Double Slab", "DoubleSlab"));
		blockList.put(Material.STEP, Arrays.asList("Slab"));
		blockList.put(Material.BRICK, Arrays.asList("Brick", "Bricks"));
		blockList.put(Material.TNT, Arrays.asList("TNT", "Dynamite"));
		blockList.put(Material.BOOKSHELF, Arrays.asList("Bookshelf"));
		blockList.put(Material.MOSSY_COBBLESTONE, Arrays.asList("Moss Cobble Stone", "MossStone", "MossyStone", "MossCobble", "MossyCobble", "MossCobbleStone", "MossyCobbleStone"));
		blockList.put(Material.OBSIDIAN, Arrays.asList("Obsidian"));
		blockList.put(Material.TORCH, Arrays.asList("Torch"));
		blockList.put(Material.FIRE, Arrays.asList("Fire"));
		blockList.put(Material.MOB_SPAWNER, Arrays.asList("Spawner", "MonsterSpawner", "MobSpawner"));
		blockList.put(Material.WOOD_STAIRS, Arrays.asList("Oak Wood Stairs", "OakWoodStairs", "WoodStairs", "WoodenStairs"));
		blockList.put(Material.CHEST, Arrays.asList("Chest"));
		blockList.put(Material.REDSTONE_WIRE, Arrays.asList("Redstone Wire", "RedstoneWire", "Wire"));
		blockList.put(Material.DIAMOND_ORE, Arrays.asList("Diamond Ore", "DiamondOre"));
		blockList.put(Material.DIAMOND_BLOCK, Arrays.asList("Diamond Block", "DiamondBlock"));
		blockList.put(Material.WORKBENCH, Arrays.asList("Crafting table", "CraftingTable", "CraftingBench", "Workbench"));
		blockList.put(Material.CROPS, Arrays.asList("Wheat Seed", "WheatSeed", "WheatSeeds"));
		blockList.put(Material.SOIL, Arrays.asList("Farmland", "Soil"));
		blockList.put(Material.FURNACE, Arrays.asList("Furnace"));
		blockList.put(Material.BURNING_FURNACE, Arrays.asList("Burning Furnace", "BurningFurnace"));
		blockList.put(Material.SIGN_POST, Arrays.asList("Sign", "SignPost"));
		blockList.put(Material.WOODEN_DOOR, Arrays.asList("Door", "WoodenDoor", "WoodDoor"));
		blockList.put(Material.LADDER, Arrays.asList("Ladder"));
		blockList.put(Material.RAILS, Arrays.asList("Rail", "Track"));
		blockList.put(Material.COBBLESTONE_STAIRS, Arrays.asList("Cobble Stone Stairs", "CobbleStoneStairs", "CobbleStairs"));
		blockList.put(Material.WALL_SIGN, Arrays.asList("Wall Sign", "WallSign", "WallSignPost"));
		blockList.put(Material.LEVER, Arrays.asList("Lever", "Switch"));
		blockList.put(Material.STONE_PLATE, Arrays.asList("Stone Pressure Plate", "StonePressurePlate", "PressurePlate"));
		blockList.put(Material.IRON_DOOR_BLOCK, Arrays.asList("Iron Door", "IronDoor"));
		blockList.put(Material.WOOD_PLATE, Arrays.asList("Wooden Pressure Plate", "WoodenPressurePlate", "WoodPressurePlate"));
		blockList.put(Material.REDSTONE_ORE, Arrays.asList("Redstone Ore", "RedstoneOre"));
		blockList.put(Material.GLOWING_REDSTONE_ORE, Arrays.asList("Glowing Redstone Ore", "GlowingRedstone", "GlowingRedstoneOre"));
		blockList.put(Material.REDSTONE_TORCH_OFF, Arrays.asList("Off Redstone Torch", "OffRedstoneTorch"));
		blockList.put(Material.REDSTONE_TORCH_ON, Arrays.asList("Redstone Torch", "RedstoneTorch"));
		blockList.put(Material.STONE_BUTTON, Arrays.asList("Stone Button", "StoneButton"));
		blockList.put(Material.SNOW, Arrays.asList("Snow", "Snow Patch", "SnowPatch", "FlatSnow"));
		blockList.put(Material.ICE, Arrays.asList("Ice", "IceBlock"));
		blockList.put(Material.SNOW_BLOCK, Arrays.asList("Snow Block", "SnowBlock"));
		blockList.put(Material.CACTUS, Arrays.asList("Cactus"));
		blockList.put(Material.CLAY, Arrays.asList("Clay", "ClayBlock"));
		blockList.put(Material.SUGAR_CANE_BLOCK, Arrays.asList("Sugar Cane", "SugarCane", "Sugar", "Reed", "Cane"));
		blockList.put(Material.JUKEBOX, Arrays.asList("Jukebox"));
		blockList.put(Material.FENCE, Arrays.asList("Fence"));
		blockList.put(Material.PUMPKIN, Arrays.asList("Pumpkin"));
		blockList.put(Material.NETHERRACK, Arrays.asList("Netherrack"));
		blockList.put(Material.SOUL_SAND, Arrays.asList("Soul Sand", "SoulSand", "SinkingSand"));
		blockList.put(Material.GLOWSTONE, Arrays.asList("Glowstone"));
		blockList.put(Material.PORTAL, Arrays.asList("Portal", "NetherPortal"));
		blockList.put(Material.JACK_O_LANTERN, Arrays.asList("Jack O' Lantern", "JackOLantern", "Lantern", "PumpkinLantern"));
		blockList.put(Material.CAKE_BLOCK, Arrays.asList("Cake", "CakeBlock"));
		blockList.put(Material.DIODE_BLOCK_OFF, Arrays.asList("Redstone Repeater", "RedstoneRepeater", "OffRedstoneRepeater"));
		blockList.put(Material.DIODE_BLOCK_ON, Arrays.asList("On Redstone Repeater"));
		blockList.put(Material.STAINED_GLASS, Arrays.asList("Stained Glass", "StainedGlass"));
		blockList.put(Material.TRAP_DOOR, Arrays.asList("Trap Door", "TrapDoor"));
		blockList.put(Material.MONSTER_EGGS, Arrays.asList("Monster Egg", "MonsterEgg"));
		blockList.put(Material.SMOOTH_BRICK, Arrays.asList("Stone Bricks", "StoneBricks", "StoneBrick"));
		blockList.put(Material.HUGE_MUSHROOM_1, Arrays.asList("Huge Brown Mushroom", "HugeBrownMushroom", "HugeMushroom"));
		blockList.put(Material.HUGE_MUSHROOM_2, Arrays.asList("Huge Red Mushroom", "HugeRedMushroom"));
		blockList.put(Material.IRON_FENCE, Arrays.asList("Iron Bars", "IronBars", "Bars"));
		blockList.put(Material.THIN_GLASS, Arrays.asList("Glass Pane", "GlassPane", "ThinGlass"));
		blockList.put(Material.MELON_BLOCK, Arrays.asList("Melon"));
		blockList.put(Material.PUMPKIN_STEM, Arrays.asList("Pumpkin Stem", "PumpkinStem"));
		blockList.put(Material.MELON_STEM, Arrays.asList("Melon Stem", "MelonStem"));
		blockList.put(Material.VINE, Arrays.asList("Vines", "Vine"));
		blockList.put(Material.FENCE_GATE, Arrays.asList("Fence Gate", "FenceGate", "Gate"));
		blockList.put(Material.BRICK_STAIRS, Arrays.asList("Brick Stairs", "BrickStairs"));
		blockList.put(Material.SMOOTH_STAIRS, Arrays.asList("Stone Brick Stairs", "StoneBrickStairs"));
		blockList.put(Material.MYCEL, Arrays.asList("Mycelium"));
		blockList.put(Material.WATER_LILY, Arrays.asList("LilyPad"));
		blockList.put(Material.NETHER_BRICK, Arrays.asList("Nether Bricks", "NetherBricks", "NetherBrick"));
		blockList.put(Material.NETHER_FENCE, Arrays.asList("Nether Brick Fence", "NetherBrickFence"));
		blockList.put(Material.NETHER_BRICK_STAIRS, Arrays.asList("Nether Brick Stairs", "NetherBrickStairs"));
		blockList.put(Material.NETHER_WARTS, Arrays.asList("Nether Wart", "NetherWart"));
		blockList.put(Material.ENCHANTMENT_TABLE, Arrays.asList("Enchanting Table", "EnchantingTable", "EnchantmentTable"));
		blockList.put(Material.BREWING_STAND, Arrays.asList("Brewing stand", "BrewingStand"));
		blockList.put(Material.CAULDRON, Arrays.asList("Cauldron"));
		blockList.put(Material.ENDER_PORTAL, Arrays.asList("Ender Portal", "Ender Portal", "EndPortal"));
		blockList.put(Material.ENDER_PORTAL_FRAME, Arrays.asList("Ender Portal Frame", "EnderPortalFrame", "EndPortalFrame"));
		blockList.put(Material.ENDER_STONE, Arrays.asList("Ender Stone", "EnderStone", "EndStone"));
		blockList.put(Material.DRAGON_EGG, Arrays.asList("Dragon Egg", "DragonEgg"));
		blockList.put(Material.REDSTONE_LAMP_OFF, Arrays.asList("Redstone Lamp", "RedstoneLamp", "OffRedstoneLamp"));
		blockList.put(Material.REDSTONE_LAMP_ON, Arrays.asList("On Redstone Lamp"));
		blockList.put(Material.WOOD_DOUBLE_STEP, Arrays.asList("Wooden Double Slab", "WoodenDoubleSlab"));
		blockList.put(Material.WOOD_STEP, Arrays.asList("Wooden Slab", "WoodenSlab", "WoodenStep"));
		blockList.put(Material.COCOA, Arrays.asList("Cocoa Plant", "CocoaPlant"));
		blockList.put(Material.SANDSTONE_STAIRS, Arrays.asList("Sandstone Stairs", "SandstoneStairs"));
		blockList.put(Material.EMERALD_ORE, Arrays.asList("Emerald Ore", "EmeraldOre", "Emerald"));
		blockList.put(Material.ENDER_CHEST, Arrays.asList("Ender Chest", "EnderChest"));
		blockList.put(Material.TRIPWIRE_HOOK, Arrays.asList("Tripwire Hook", "TripwireHook"));
		blockList.put(Material.TRIPWIRE, Arrays.asList("Tripwire"));
		blockList.put(Material.EMERALD_BLOCK, Arrays.asList("Emerald Block", "EmeraldBlock"));
		blockList.put(Material.SPRUCE_WOOD_STAIRS, Arrays.asList("Spruce Wood Stairs", "SpruceWoodStairs"));
		blockList.put(Material.BIRCH_WOOD_STAIRS, Arrays.asList("Birch Wood Stairs", "BirchWoodStairs"));
		blockList.put(Material.JUNGLE_WOOD_STAIRS, Arrays.asList("Jungle Wood Stairs", "JungleWoodStairs"));
		blockList.put(Material.COMMAND, Arrays.asList("Command Block", "CommandBlock", "CommandBeacon"));
		blockList.put(Material.BEACON, Arrays.asList("Beacon"));
		blockList.put(Material.COBBLE_WALL, Arrays.asList("Cobble Stone Wall", "CobblestoneWall", "CobbleWall"));
		blockList.put(Material.FLOWER_POT, Arrays.asList("Flower Pot", "FlowerPot", "PlantPot", "Pot"));
		blockList.put(Material.CARROT, Arrays.asList("Carrots"));
		blockList.put(Material.POTATO, Arrays.asList("Potatoes", "Potatos"));
		blockList.put(Material.WOOD_BUTTON, Arrays.asList("Wood Button", "WoodButton", "WoodenButton"));
		blockList.put(Material.SKULL, Arrays.asList("Skull", "Head", "MobHead"));
		blockList.put(Material.ANVIL, Arrays.asList("Anvil"));
		blockList.put(Material.TRAPPED_CHEST, Arrays.asList("Trapped chest", "TrappedChest", "TrapChest", "ChestTrap", "RedstoneChest"));
		blockList.put(Material.GOLD_PLATE, Arrays.asList("Light Weighted Pressure Plate", "LightWeightedPressurePlate", "LightPressurePlate"));
		blockList.put(Material.IRON_PLATE, Arrays.asList("Heavy Weighted Pressure Plate", "HeavyWeightedPressurePlate", "HeavyPressurePlate"));
		blockList.put(Material.REDSTONE_COMPARATOR_OFF, Arrays.asList("Inactive Redstone Comparator", "InactiveRedstoneComparator", "RedstoneComparator", "Comparator"));
		blockList.put(Material.REDSTONE_COMPARATOR_ON, Arrays.asList("Active Redstone Comparator", "ActiveRedstoneComparator", "ActiveComparator"));
		blockList.put(Material.DAYLIGHT_DETECTOR, Arrays.asList("Daylight Sensor", "DaylightSensor", "LightSensor", "SolarPanel"));
		blockList.put(Material.REDSTONE_BLOCK, Arrays.asList("Block Of Redstone", "BlockOfRedstone", "RedstoneBlock"));
		blockList.put(Material.QUARTZ_ORE, Arrays.asList("Nether Quartz Ore", "NetherQuartzOre", "QuartzOre", "NetherOre"));
		blockList.put(Material.HOPPER, Arrays.asList("Hopper"));
		blockList.put(Material.QUARTZ_BLOCK, Arrays.asList("Block of Quartz", "BlockofQuartz", "QuartzBlock"));
		blockList.put(Material.QUARTZ_STAIRS, Arrays.asList("Quartz Stairs", "QuartzStairs"));
		blockList.put(Material.ACTIVATOR_RAIL, Arrays.asList("Activator Rail", "ActivatorRail"));
		blockList.put(Material.DROPPER, Arrays.asList("Dropper"));
		blockList.put(Material.STAINED_CLAY, Arrays.asList("Stained Clay", "StainedClay"));
		blockList.put(Material.STAINED_GLASS_PANE, Arrays.asList("Stained Glass Pane", "StainedPane", "StainedGlassPane"));
		blockList.put(Material.LEAVES_2, Arrays.asList("Acacia Leaves", "AcaciaLeaves"));
		blockList.put(Material.LOG_2, Arrays.asList("Acacia Wood", "AcaciaWood"));
		blockList.put(Material.ACACIA_STAIRS, Arrays.asList("Acacia Stairs", "AcaciaStairs"));
		blockList.put(Material.DARK_OAK_STAIRS, Arrays.asList("Dark Oak Stairs", "DarkOakStairs"));
		// UNUSED BLOCKS
		blockList.put(null, null);
		blockList.put(null, null);
		blockList.put(null, null);
		blockList.put(null, null);
		blockList.put(null, null);
		//
		blockList.put(Material.HAY_BLOCK, Arrays.asList("Hay Block", "Hay", "HayBlock"));
		blockList.put(Material.CARPET, Arrays.asList("Carpet"));
		blockList.put(Material.HARD_CLAY, Arrays.asList("Hardened Clay", "HardClay", "HardenedClay"));
		blockList.put(Material.COAL_BLOCK, Arrays.asList("Block of Coal", "CoalBlock", "BlockofCoal"));
		blockList.put(Material.PACKED_ICE, Arrays.asList("Packed Ice", "PackedIce"));
		blockList.put(Material.DOUBLE_PLANT, Arrays.asList("Sunflower"));
		//
		// Load Biome List
		//
		biomeList.put(Biome.OCEAN, Arrays.asList("Ocean", "Sea"));
		biomeList.put(Biome.PLAINS, Arrays.asList("Plains", "Plain"));
		biomeList.put(Biome.DESERT, Arrays.asList("Desert"));
		biomeList.put(Biome.FOREST, Arrays.asList("Forest"));
		biomeList.put(Biome.TAIGA, Arrays.asList("Taiga"));
		biomeList.put(Biome.SWAMPLAND, Arrays.asList("Swampland", "Swamp"));
		biomeList.put(Biome.RIVER, Arrays.asList("River"));
		biomeList.put(Biome.HELL, Arrays.asList("Hell"));
		biomeList.put(Biome.SKY, Arrays.asList("Sky"));
		biomeList.put(Biome.FROZEN_OCEAN, Arrays.asList("Frozen Ocean", "FrozenOcean", "FrozenSea"));
		biomeList.put(Biome.FROZEN_RIVER, Arrays.asList("Frozen River", "FrozenRiver"));
		biomeList.put(Biome.ICE_PLAINS, Arrays.asList("Ice Plains", "IcePlains", "IcePlain"));
		biomeList.put(Biome.ICE_MOUNTAINS, Arrays.asList("Ice Mountains", "IceMountains", "IceMountain"));
		biomeList.put(Biome.MUSHROOM_ISLAND, Arrays.asList("Mushroom Island", "MushroomIsland", "Mushroom"));
		biomeList.put(Biome.BEACH, Arrays.asList("Beach"));
		biomeList.put(Biome.JUNGLE, Arrays.asList("Jungle"));
		biomeList.put(Biome.SAVANNA, Arrays.asList("Savanna"));
		biomeList.put(Biome.MESA, Arrays.asList("MESA"));
		//
		// Load Tree Type List
		//
		treeTypeList.put(TreeType.ACACIA, Arrays.asList("Acacia"));
		treeTypeList.put(TreeType.BIG_TREE, Arrays.asList("Big Tree", "BigTree"));
		treeTypeList.put(TreeType.BIRCH, Arrays.asList("Birch"));
		treeTypeList.put(TreeType.BROWN_MUSHROOM, Arrays.asList("Brown Mushroom", "BrownMushroom"));
		treeTypeList.put(TreeType.DARK_OAK, Arrays.asList("Dark Oak", "DarkOak"));
		treeTypeList.put(TreeType.JUNGLE, Arrays.asList("Jungle"));
		treeTypeList.put(TreeType.JUNGLE_BUSH, Arrays.asList("Jungle Bush", "JungleBush"));
		treeTypeList.put(TreeType.MEGA_REDWOOD, Arrays.asList("Mega Redwood", "MegaRedwood"));
		treeTypeList.put(TreeType.RED_MUSHROOM, Arrays.asList("Red Mushroom", "RedMushroom"));
		treeTypeList.put(TreeType.REDWOOD, Arrays.asList("Redwood"));
		treeTypeList.put(TreeType.SMALL_JUNGLE, Arrays.asList("Small Jungle", "SmallJungle"));
		treeTypeList.put(TreeType.SWAMP, Arrays.asList("Swamp"));
		treeTypeList.put(TreeType.TALL_BIRCH, Arrays.asList("Tall Birch", "TallBirch"));
		treeTypeList.put(TreeType.TALL_REDWOOD, Arrays.asList("Tall Redwood", "TallRedwood"));
		treeTypeList.put(TreeType.TREE, Arrays.asList("Tree"));
		//
		// Register EvilEdit Commands
		//
		// Generation
		getCommand("pumpkins").setExecutor(this.editSession);
		getCommand("forest").setExecutor(this.editSession);
		getCommand("forestgen").setExecutor(this.editSession);
		getCommand("pyramid").setExecutor(this.editSession);
		getCommand("hpyramid").setExecutor(this.editSession);
		getCommand("cyl").setExecutor(this.editSession);
		getCommand("hcyl").setExecutor(this.editSession);
		getCommand("cylinder").setExecutor(this.editSession);
		getCommand("hcylinder").setExecutor(this.editSession);
		getCommand("esphere").setExecutor(this.editSession);
		getCommand("hsphere").setExecutor(this.editSession);
		getCommand("sphere").setExecutor(this.editSession);
		getCommand("tree").setExecutor(this.editSession);
		getCommand("/pyramid").setExecutor(this.editSession);
		getCommand("/hpyramid").setExecutor(this.editSession);
		getCommand("/cyl").setExecutor(this.editSession);
		getCommand("/hcyl").setExecutor(this.editSession);
		getCommand("/cylinder").setExecutor(this.editSession);
		getCommand("/hcylinder").setExecutor(this.editSession);
		getCommand("/esphere").setExecutor(this.editSession);
		getCommand("/hsphere").setExecutor(this.editSession);
		getCommand("/sphere").setExecutor(this.editSession);
		// Movement
		getCommand("thru").setExecutor(this.editSession);
		getCommand("ascend").setExecutor(this.editSession);
		getCommand("descend").setExecutor(this.editSession);
		getCommand("ceil").setExecutor(this.editSession);
		// Region
		getCommand("move").setExecutor(this.editSession);
		getCommand("/move").setExecutor(this.editSession);
		getCommand("snow").setExecutor(this.editSession);
		getCommand("thaw").setExecutor(this.editSession);
		getCommand("flip").setExecutor(this.editSession);
		getCommand("/flip").setExecutor(this.editSession);
		getCommand("undo").setExecutor(this.editSession);
		getCommand("/undo").setExecutor(this.editSession);
		getCommand("fill").setExecutor(this.editSession);
		getCommand("/fill").setExecutor(this.editSession);
		getCommand("set").setExecutor(this.editSession);
		getCommand("/set").setExecutor(this.editSession);
		getCommand("rfill").setExecutor(this.editSession);
		getCommand("rreplace").setExecutor(this.editSession);
		getCommand("del").setExecutor(this.editSession);
		getCommand("delete").setExecutor(this.editSession);
		getCommand("rdel").setExecutor(this.editSession);
		getCommand("copy").setExecutor(this.editSession);
		getCommand("cut").setExecutor(this.editSession);
		getCommand("paste").setExecutor(this.editSession);
		getCommand("drain").setExecutor(this.editSession);
		getCommand("green").setExecutor(this.editSession);
		getCommand("setbiome").setExecutor(this.editSession);
		getCommand("regen").setExecutor(this.editSession);
		getCommand("overlay").setExecutor(this.editSession);
		getCommand("walls").setExecutor(this.editSession);
		getCommand("outline").setExecutor(this.editSession);
		getCommand("hollow").setExecutor(this.editSession);
		getCommand("replace").setExecutor(this.editSession);
		getCommand("/copy").setExecutor(this.editSession);
		getCommand("/cut").setExecutor(this.editSession);
		getCommand("/paste").setExecutor(this.editSession);
		getCommand("/drain").setExecutor(this.editSession);
		getCommand("/green").setExecutor(this.editSession);
		getCommand("/setbiome").setExecutor(this.editSession);
		getCommand("/regen").setExecutor(this.editSession);
		getCommand("/overlay").setExecutor(this.editSession);
		getCommand("/walls").setExecutor(this.editSession);
		getCommand("/outline").setExecutor(this.editSession);
		getCommand("/hollow").setExecutor(this.editSession);
		getCommand("/replace").setExecutor(this.editSession);
		getCommand("count").setExecutor(this.editSession);
		getCommand("/count").setExecutor(this.editSession);
		getCommand("size").setExecutor(this.editSession);
		getCommand("/size").setExecutor(this.editSession);
		getCommand("desel").setExecutor(this.editSession);
		getCommand("/desel").setExecutor(this.editSession);
		getCommand("wand").setExecutor(this.editSession);
		getCommand("/wand").setExecutor(this.editSession);
		getCommand("toggleeditwand").setExecutor(this.editSession);		
		//
		// Log Block Integration
		//
		final Plugin plugin = pluginManager.getPlugin("EvilBook-Logging");
		if (plugin != null) {
			lbConsumer = ((LogBlock) plugin).getConsumer();
			logInfo("Loaded EvilBook-Logging module");
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
			dynmapAPI = ((DynmapAPI) dynmapPlugin);
			markerAPI = dynmapAPI.getMarkerAPI();
			logInfo("Loaded Dynmap module");
		} else {
			logSevere("Failed to load Dynmap module");
			getServer().shutdown();
			return;
		}
		//
		// Load Dynmap Markers
		//
		PlayerHomeMarkers.loadPlayerHomes();
		WarpMarkers.loadWarps();
		//
		// Register protocolLib listeners
		//
		EventListenerPacket.registerSignUpdatePacketReceiver(this);
		EventListenerPacket.registerCommandBlockPacketReceiver(this);
		//
		// Scheduler
		//
		Scheduler scheduler = new Scheduler(this);
		scheduler.tipsAutosave();
		scheduler.updateServices();
	}

	/**
	 * Called when the plugin is disabled
	 */
	@Override
	public void onDisable() {
		maps.saveMapIdList();
	}
	
	/**
	 * Called when a command is executed
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		// TODO: Add message to player showing unlocks on rankup
		// TODO: Add questing system, mobs that talk and give quests, sendBlockChange to show route of quest by a line of wool, mabey an adventure questing world?
		// TODO: Add ability to use EvilEdit in survival if the player has the needed blocks
		// TODO: Add rotate command to rotate an area of blocks / copied blocks for evil edit
		//
		// Command block command handling
		//
		if (sender instanceof BlockCommandSender) {
	        //BlockCommandSender cmdSender = (BlockCommandSender)sender;
	        //TODO: Add special command block commands 
	    }
		//
		// Statistics
		//
		GlobalStatistics.incrementStatistic(GlobalStatistic.CommandsExecuted, 1);
		//
		// Dr. Watson Command
		//
		if (command.getName().equalsIgnoreCase("drwatson")) {
			if (args.length == 0) {
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/drwatson sql");
				sender.sendMessage("§d/drwatson sqlclean");
				sender.sendMessage("§d/drwatson respring");
				sender.sendMessage("§d/drwatson memstat");
			} else if (args[0].equalsIgnoreCase("sql")) {
				//
				// Check `evilbook-dynamicsigns` table
				//
				sender.sendMessage("§7Dr. Watson scanning `evilbook-dynamicsigns`...");
				try (Statement statement = SQL.connection.createStatement()) {
					try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.DynamicSign.tableName + ";")) {
						while (rs.next()) {
							if (Bukkit.getWorld(rs.getString("world")) == null) {
								sender.sendMessage("§7--> World '" + rs.getString("world") + "' is not loaded");
							} else if (new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x"), rs.getInt("y"), rs.getInt("z")).getBlock().getState() instanceof Sign == false) {
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
					try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.Emitter.tableName + ";")) {
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
					try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.ContainerProtection.tableName + ";")) {
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
					try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.Region.tableName + ";")) {
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
					try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.Warps.tableName + ";")) {
						while (rs.next()) {
							if (Bukkit.getWorld(rs.getString("location").split(">")[0]) == null) {
								sender.sendMessage("§7--> World '" + rs.getString("location").split(">")[0] + "' is not loaded"); 
							}
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			} else if (args[0].equalsIgnoreCase("sqlclean")) {
				//
				// Clean `evilbook-dynamicsigns` table
				//
				sender.sendMessage("§7Dr. Watson cleaning `evilbook-dynamicsigns`...");
				try (Statement statement = SQL.connection.createStatement()) {
					try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + ".`evilbook-dynamicsigns`;")) {
						while (rs.next()) {
							if (Bukkit.getWorld(rs.getString("world")) == null) {
								sender.sendMessage("§7--> FIXED: World '" + rs.getString("world") + "' is not loaded"); 
								SQL.deleteRowFromCriteria(TableType.DynamicSign, "world='" + rs.getString("world") + "'");
							} else if (new Location(Bukkit.getWorld(rs.getString("world")), rs.getInt("x"), rs.getInt("y"), rs.getInt("z")).getBlock().getState() instanceof Sign == false) {
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
					try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.Emitter.tableName + ";")) {
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
					try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.ContainerProtection.tableName + ";")) {
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
					try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.Region.tableName + ";")) {
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
					try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.Warps.tableName + ";")) {
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
			} else if (args[0].equalsIgnoreCase("respring")) {
				//
				// Respring
				//
				broadcastPlayerMessage(sender.getName(), "§d[§5Server§d] Server respringing...");
				blockList.clear();
				biomeList.clear();
				treeTypeList.clear();
				commandBlacklist.clear();
				dynamicSignList.clear();
				paidWorldList.clear();
				regionList.clear();
				emitterList.clear();
				this.editSession = new Session(this);
				this.random = new Random();
				lbConsumer = null;
				HandlerList.unregisterAll();
				onEnable();
				broadcastPlayerMessage(sender.getName(), "§d[§5Server§d] Server resprung");
			} else if (args[0].equalsIgnoreCase("memstat")) {
				//
				// Memory statistics
				//
				sender.sendMessage("§dTotal memory §5" + Runtime.getRuntime().maxMemory() / 1048576 + "MB");
				int freePercentage = (int) Math.round((double)(Runtime.getRuntime().freeMemory()) / (double)(Runtime.getRuntime().maxMemory()) * 100);
				sender.sendMessage("§dUsed memory " + ((100 - freePercentage) > 90 ? "§c" : "§a") + ((Runtime.getRuntime().maxMemory() / 1048576) - (Runtime.getRuntime().freeMemory() / 1048576)) + "MB §e(" + (100 - freePercentage) + "%)");
				sender.sendMessage("§dFree memory " + (freePercentage > 10 ? "§a" : "§c") + Runtime.getRuntime().freeMemory() / 1048576 + "MB §e(" + freePercentage + "%)");
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " [message]");
			}
			return true;
		}
		//
		// Me Command
		//
		if (command.getName().equalsIgnoreCase("me")) {
			String me = "";
			for (String msg : args) me += " " + msg;
			broadcastPlayerMessage(sender.getName(), "* " + (sender instanceof Player ? ((Player)sender).getDisplayName() : sender.getName()) + me);
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
						sender.sendMessage("§5" + getServer().getOfflinePlayer(args[0]).getName() + "'s account balance is §d$" + SQL.getProperty(TableType.PlayerProfile, args[0], "money"));
					}
				} else {
					sender.sendMessage("§7You can't view a player's account balance who doesn't exist");
				}
			} else {
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " <player>");
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
							playerProfiles.put(getPlayer(args[0]).getName().toLowerCase(Locale.UK), new PlayerProfileAdmin(getPlayer(args[0])));
						}
						// Set nametag color
						NametagAPI.updateNametagHard(getPlayer(args[0]).getName(), "§" + getProfile(getPlayer(args[0])).rank.getColor((getProfile(getPlayer(args[0])))), null);
						//
						broadcastPlayerMessage(getPlayer(args[0]).getName(), "§c" + getPlayer(args[0]).getDisplayName() + " §dhas been promoted to Admin rank");
					} else {
						SQL.setProperty(TableType.PlayerProfile, args[0], "rank", "ADMIN");
						broadcastPlayerMessage(getServer().getOfflinePlayer(args[0]).getName(), "§c" + getServer().getOfflinePlayer(args[0]).getName() + " §dhas been promoted to Admin rank");
					}
					getServer().getOfflinePlayer(args[0]).setOp(true);
				} else {
					sender.sendMessage("§7You can't give admin rank to a player who doesn't exist");
				}
			} else {
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/makeadmin [player]");
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
						if (getProfile(args[0]).rank == Rank.POLICE) { 
							getServer().getOfflinePlayer(args[0]).setOp(false);
							// Player profile type conversion
							if (getProfile(args[0]) instanceof PlayerProfileAdmin) {
								getProfile(args[0]).saveProfile();
								playerProfiles.remove(getPlayer(args[0]).getName().toLowerCase(Locale.UK));
								playerProfiles.put(getPlayer(args[0]).getName().toLowerCase(Locale.UK), new PlayerProfileNormal(getPlayer(args[0])));
							}
							//
						}
						// Set nametag color
						NametagAPI.updateNametagHard(getPlayer(args[0]).getName(), "§" + getProfile(getPlayer(args[0])).rank.getColor((getProfile(getPlayer(args[0])))), null);
						//
						getProfile(args[0]).updatePlayerListName();
						broadcastPlayerMessage(getPlayer(args[0]).getName(), "§c" + getPlayer(args[0]).getDisplayName() + " §dhas been demoted to " + getProfile(args[0]).rank.getName() + " rank");
					} else {
						if (Rank.valueOf(SQL.getProperty(TableType.PlayerProfile, args[0], "rank")).getPreviousRank() == Rank.POLICE) getServer().getOfflinePlayer(args[0]).setOp(false);
						SQL.setProperty(TableType.PlayerProfile, args[0], "rank", Rank.valueOf(SQL.getProperty(TableType.PlayerProfile, args[0], "rank")).getPreviousRank().toString());
						broadcastPlayerMessage(getServer().getOfflinePlayer(args[0]).getName(), "§c" + getServer().getOfflinePlayer(args[0]).getName() + " §dhas been demoted to " + Rank.valueOf(SQL.getProperty(TableType.PlayerProfile, args[0], "rank")).getName() + " rank");
					}
				} else {
					sender.sendMessage("§7You can't demote a player who doesn't exist");
				}
			} else {
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/demote [player]");
			}
			return true;
		}
		//
		// Rules Command
		//
		if (command.getName().equalsIgnoreCase("rules")) {
			sender.sendMessage("§dAmentrix Server Rules");
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
				sender.sendMessage("  §0[§EBuilder§0] §7Join the server");
				sender.sendMessage("  §0[§5Adv.Builder§0] §7Impress an admin with a creation");
				sender.sendMessage("  §0[§DArchitect§0] §7Further impress an admin with creations");
				sender.sendMessage("  §0[§9Moderator§0] §7Show commitment and help the admins");
				sender.sendMessage("  §0[§3Police§0] §7Further help the admins");
				sender.sendMessage("  §7To purchase a rank please see /admin");
			} else if (args[0].equals("2")) {
				sender.sendMessage("§dRanks §5- §dPage 2 of 3 §5- §7/ranks <page>");
				sender.sendMessage("  §0[§ECopperStaff§0] §7Show loyalty and help players");
				sender.sendMessage("  §0[§7SilverStaff§0] §7Show loyalty and help players");
				sender.sendMessage("  §0[§6GoldStaff§0] §7Show loyalty and help players");
				sender.sendMessage("  §0[§1LapisStaff§0] §7Show loyalty and help players");
				sender.sendMessage("  §0[§BDiamondStaff§0] §7Show loyalty and help players");
				sender.sendMessage("  §7To purchase a rank please see /admin");
			} else if (args[0].equals("3")) {
				sender.sendMessage("§dRanks §5- §dPage 3 of 3 §5- §7/ranks <page>");
				sender.sendMessage("  §0[§4Admin§0] §7Purchased from the Amentrix website");
				sender.sendMessage("  §0[§ACouncillor§0] §7Purchased from the Amentrix website");
				sender.sendMessage("  §0[§CElite§0] §7Purchased from the Amentrix website");
				sender.sendMessage("  §0[§6Investor§0] §7Purchased from the Amentrix website");
				sender.sendMessage("  §0[§6§OTycoon§0] §7Purchased from the Amentrix website");
				sender.sendMessage("  §0[§B§OAdmin Staff§0] §7Elected admin staff team");
				sender.sendMessage("  §0[§BServer Host§0] §7EvilPeanut");
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
				if (isInSurvival(w)) continue;
				for (Entity entity : w.getEntities()) {
					if (entity.getType() == EntityType.PLAYER 
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
							sender.sendMessage("§7You can't promote a player to above moderator");
						} else if (sender instanceof Player && getProfile(args[0]).rank.getNextRank().isHigher(Rank.POLICE) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
							sender.sendMessage("§7You can't promote a player to above police");
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
									playerProfiles.put(getPlayer(args[0]).getName().toLowerCase(Locale.UK), new PlayerProfileAdmin(getPlayer(args[0])));
								}
								//
							}
							// Set nametag color
							NametagAPI.updateNametagHard(getPlayer(args[0]).getName(), "§" + getProfile(getPlayer(args[0])).rank.getColor((getProfile(getPlayer(args[0])))), null);
							//
							getProfile(args[0]).updatePlayerListName();
							broadcastPlayerMessage(getPlayer(args[0]).getName(), "§c" + getPlayer(args[0]).getDisplayName() + " §dhas been promoted to " + getProfile(args[0]).rank.getName() + " rank");
						}
					} else {
						if (sender instanceof Player && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
							sender.sendMessage("§7You can't promote offline players");
							return true;
						}
						if (Rank.valueOf(SQL.getProperty(TableType.PlayerProfile, args[0], "rank")).getNextRank().equals(Rank.STAFF_COPPER)) getServer().getOfflinePlayer(args[0]).setOp(true);
						SQL.setProperty(TableType.PlayerProfile, args[0], "rank", Rank.valueOf(SQL.getProperty(TableType.PlayerProfile, args[0], "rank")).getNextRank().toString());
						broadcastPlayerMessage(getServer().getOfflinePlayer(args[0]).getName(), "§c" + getServer().getOfflinePlayer(args[0]).getName() + " §dhas been promoted to " + Rank.valueOf(SQL.getProperty(TableType.PlayerProfile, args[0], "rank")).getName() + " rank");
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
								sender.sendMessage("§7You can't promote a player to above moderator");
							} else if (sender instanceof Player && rank.isHigher(Rank.POLICE) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
								sender.sendMessage("§7You can't promote a player to above police");
							} else {
								getProfile(args[0]).rank = rank;
								if (rank.isHigher(Rank.POLICE) && !getServer().getOfflinePlayer(args[0]).isOp()) {
									getServer().getOfflinePlayer(args[0]).setOp(true);
								} else if (rank.isHigher(Rank.STAFF_DIAMOND) && getProfile(args[0]) instanceof PlayerProfileNormal) {
									// Player profile type conversion
									getProfile(args[0]).saveProfile();
									playerProfiles.remove(getPlayer(args[0]).getName().toLowerCase(Locale.UK));
									playerProfiles.put(getPlayer(args[0]).getName().toLowerCase(Locale.UK), new PlayerProfileAdmin(getPlayer(args[0])));
									//
								}
								// Set nametag color
								NametagAPI.updateNametagHard(getPlayer(args[0]).getName(), "§" + getProfile(getPlayer(args[0])).rank.getColor((getProfile(getPlayer(args[0])))), null);
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
							SQL.setProperty(TableType.PlayerProfile, args[0], "rank", rank.name());
							broadcastPlayerMessage(getServer().getOfflinePlayer(args[0]).getName(), "§c" + getServer().getOfflinePlayer(args[0]).getName() + " §dhas been promoted to " + Rank.valueOf(SQL.getProperty(TableType.PlayerProfile, args[0], "rank")).getName() + " rank");
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/promote [player] <rank>");
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
		Player player = (Player)sender;
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/drug cocain");
				sender.sendMessage("§d/drug shrooms");
				sender.sendMessage("§d/drug alcohol");
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
					sender.sendMessage("§dEconomic growth = $" + GlobalStatistics.getStatistic(GlobalStatistic.EconomyGrowth) + " today §7$" + SQL.getColumnSum(TableType.Statistics, "economy_growth") + " total");
					sender.sendMessage("§dEconomic trade = $" + GlobalStatistics.getStatistic(GlobalStatistic.EconomyTrade) + " today §7$" + SQL.getColumnSum(TableType.Statistics, "economy_trade") + " total");
					sender.sendMessage("§dPlayer logins = " + GlobalStatistics.getStatistic(GlobalStatistic.LoginTotal) + " today §7" + SQL.getColumnSum(TableType.Statistics, "login_total") + " total");
					sender.sendMessage("§dNew players = " + GlobalStatistics.getStatistic(GlobalStatistic.LoginNewPlayers) + " today");
					sender.sendMessage("§dTotal unique players = " + SQL.getRowCount(TableType.PlayerProfile));
					sender.sendMessage("§dCommands executed = " + GlobalStatistics.getStatistic(GlobalStatistic.CommandsExecuted) + " today §7" + SQL.getColumnSum(TableType.Statistics, "commands_executed") + " total");
					sender.sendMessage("§dMessages sent = " + GlobalStatistics.getStatistic(GlobalStatistic.MessagesSent) + " today §7" + SQL.getColumnSum(TableType.Statistics, "messages_sent") + " total");
					sender.sendMessage("§dBlocks broken = " + GlobalStatistics.getStatistic(GlobalStatistic.BlocksBroken) + " today §7" + SQL.getColumnSum(TableType.Statistics, "blocks_broken") + " total");
					sender.sendMessage("§dBlocks placed = " + GlobalStatistics.getStatistic(GlobalStatistic.BlocksPlaced) + " today §7" + SQL.getColumnSum(TableType.Statistics, "blocks_placed") + " total");
				} else if (args[0].equalsIgnoreCase("player")) {
					sender.sendMessage("§5" + player.getName() + "'s General Statistics");
					sender.sendMessage("§dMoney = $" + SQL.getProperty(TableType.PlayerProfile, player.getName(), "money"));
					sender.sendMessage("§dTotal logins = " + SQL.getProperty(TableType.PlayerProfile, player.getName(), "total_logins"));
					sender.sendMessage("§dLast login = " + new SimpleDateFormat("dd-MM-yyyy").format(new Date(player.getLastPlayed())));
				} else if (args[0].equalsIgnoreCase("survival")) {
					List<String> text = new ArrayList();
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
							"§dPlayers = " + SQL.getColumnSum(TableType.PlayerStatistics, "killed_players") + "\n");
					player.getInventory().addItem(getBook("Server Survival Statistics", "Amentrix", text));
					sender.sendMessage("§7Please check your inventory for the survival statistics guide");
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("player")) {
					OfflinePlayer statPlayer = getServer().getOfflinePlayer(args[1]);
					if (statPlayer.hasPlayedBefore()) {
						sender.sendMessage("§5" + statPlayer.getName() + "'s General Statistics");
						sender.sendMessage("§dMoney = $" + SQL.getProperty(TableType.PlayerProfile, statPlayer.getName(), "money"));
						sender.sendMessage("§dTotal logins = " + SQL.getProperty(TableType.PlayerProfile, statPlayer.getName(), "total_logins"));
						sender.sendMessage("§dLast login = " + new SimpleDateFormat("dd-MM-yyyy").format(new Date(statPlayer.getLastPlayed())));
					} else {
						sender.sendMessage("§7Statistics for this player weren't found");
					}
				} else if (args[0].equalsIgnoreCase("survival")) {
					OfflinePlayer statPlayer = getServer().getOfflinePlayer(args[1]);
					if (statPlayer.hasPlayedBefore()) {
						List<String> text = new ArrayList();
						text.add("§5" + statPlayer.getName() + "'s Survival Statistics\n\n§7Page 1 - Mined ores\n§7Page 2 - Mob kills\n§7Page 3 - Mob kills");
						text.add("§5§oTotal ores mined\n\n§dCoal = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.MINED_COAL) + "\n" +
								"§dIron = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.MINED_IRON) + "\n" +
								"§dRedstone = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.MINED_REDSTONE) + "\n" +
								"§dLapis Lazuli = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.MINED_LAPIS) + "\n" +
								"§dQuartz = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.MINED_NETHERQUARTZ) + "\n" +
								"§dGold = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.MINED_GOLD) + "\n" +
								"§dDiamond = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.MINED_DIAMOND) + "\n" +
								"§dEmerald = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.MINED_EMERALD) + "\n");
						text.add("§5§oTotal mobs killed\n\n§dPigs = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_PIGS) + "\n" +
								"§dVillagers = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_VILLAGERS) + "\n" +
								"§dCave Spiders = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_CAVESPIDERS) + "\n" +
								"§dEndermen = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_ENDERMEN) + "\n" +
								"§dSpiders = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_SPIDERS) + "\n" +
								"§dWolves = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_WOLVES) + "\n" +
								"§dZombie Pigmen = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_ZOMBIEPIGS) + "\n" +
								"§dBlazes = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_BLAZES) + "\n" + 
								"§dCreepers = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_CREEPERS) + "\n" +
								"§dGhasts = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_GHASTS) + "\n" +
								"§dMagmacubes = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_MAGMACUBES) + "\n");
						text.add("§5§oTotal mobs killed\n\n§dSilverfish = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_SILVERFISH) + "\n" +
								"§dSkeletons = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_SKELETONS) + "\n" +
								"§dSlimes = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_SLIMES) + "\n" +
								"§dWitches = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_WITCHES) + "\n" +
								"§dZombies = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_ZOMBIES) + "\n" +
								"§dEnder Dragons = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_ENDERDRAGONS) + "\n" +
								"§dWithers = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_WITHERS) + "\n" +
								"§dPlayers = " + PlayerStatistics.getStatistic(statPlayer.getName(), PlayerStatistic.KILLED_PLAYERS) + "\n");
						player.getInventory().addItem(getBook(statPlayer.getName() + "'s Survival Statistics", "Amentrix", text));
						sender.sendMessage("§7Please check your inventory for the survival statistics guide");
					} else {
						sender.sendMessage("§7Statistics for this player weren't found");
					}
				}
			} else {
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " server");
				sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " player [player]");
				sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " survival <player>");
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
			} else if (args.length == 1) {
				//
				// Player is trying to set a title
				//
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
					String title = toFormattedString(args[0]);
					getProfile(player).setNameTitle(title);
					sender.sendMessage("§7You have changed your title to §d" + title);
				}
			} else {
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/title");
				sender.sendMessage("§d/title [title]");
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/mail inbox");
				sender.sendMessage("§d/mail send [player] [message]");
			} else if (args[0].equalsIgnoreCase("inbox")) {
				Inventory inboxMenu = Bukkit.createInventory(null, 27, "My inbox");
				try (Statement statement = SQL.connection.createStatement()) {
					try (ResultSet rs = statement.executeQuery("SELECT * FROM " + SQL.database + "." + TableType.Mail.tableName + " WHERE player_recipient='" + sender.getName() + "';")) {
						while (rs.next()) {
							inboxMenu.addItem(getBook(rs.getString("date_sent"), rs.getString("player_sender"), Arrays.asList(rs.getString("message_text"))));
						}
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				player.openInventory(inboxMenu);
			} else if (args[0].equalsIgnoreCase("send")) {
				if (args.length > 2) {
					if (isProfileExistant(args[1])) {
						StringBuilder message = new StringBuilder();
						for (int i = 2; i < args.length; i++) message.append(" " + args[i].replaceAll("'", "''"));
						SQL.insert(TableType.Mail, "'" + sender.getName() + "','" + getServer().getOfflinePlayer(args[1]).getName() + "','" + message + "','" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "'");
						sender.sendMessage("§7Message sent");
					} else {
						sender.sendMessage("§7The recipient player doesn't exist");
					}
				} else {
					sender.sendMessage("§5Incorrect command usage");
					sender.sendMessage("§d/mail send [player] [message]");
				}
			}
			return true;
		}
		//
		// Minigame Command
		//
		if (command.getName().equalsIgnoreCase("minigame")) {
			sender.sendMessage("§7The minigame creator is under development");
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
						sender.sendMessage("§7To " + getPlayer(args[0]).getDisplayName() + "§7:§f" + message);
						getPlayer(args[0]).sendMessage("§7From " + ((Player) sender).getDisplayName() + ":§f" + message);
						getProfile(player).lastMsgPlayer = getPlayer(args[0]).getName();
						getProfile(args[0]).lastMsgPlayer = sender.getName();
					} else {
						sender.sendMessage("§7You can't message a player who has muted you");
					}
				} else {
					sender.sendMessage("§7You can't message an offline player");
				}
			} else {
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/msg [player] [message]");
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
						sender.sendMessage("§7To " + getServer().getPlayer(getProfile(player).lastMsgPlayer).getDisplayName() + "§7:§f" + message);
						getServer().getPlayer(getProfile(player).lastMsgPlayer).sendMessage("§7From " + ((Player) sender).getDisplayName() + ":§f" + message);
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
			if (player.getName().equals("EvilPeanut")) {
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/tool none");
				sender.sendMessage("§d/tool selection");
				sender.sendMessage("§d/tool tree");
			}
			return true;
		}
		//
		// Effect Command
		//
		if (command.getName().equalsIgnoreCase("effect")) {
			if (args.length == 0) {
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/effect smoke [direction] [frequency]");
				sender.sendMessage("§d/effect flames [frequency]");
				sender.sendMessage("§d/effect potion [frequency]");
				for (EmitterEffect effectType : EmitterEffect.values()) if (effectType.isParticleEffect()) sender.sendMessage("§d/effect " + effectType.name().toLowerCase() + " [frequency] [amount]");
			} else if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("smoke")) {
					if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
						if (args.length == 1) {
							Emitter emitter = new Emitter(player.getLocation(), EmitterEffect.Smoke, 4, 2);
							emitterList.add(emitter);
							emitter.save();
							sender.sendMessage("§7Created smoke effect");
						} else if (args.length == 2) {
							if (isInteger(args[1])) {
								Emitter emitter = new Emitter(player.getLocation(), EmitterEffect.Smoke, Integer.parseInt(args[1]), 2);
								emitterList.add(emitter);
								emitter.save();
								sender.sendMessage("§7Created smoke effect");
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
						} else if (args.length == 3) {
							if (isInteger(args[1]) && isInteger(args[2]) && Integer.parseInt(args[2]) >= 0) {
								Emitter emitter = new Emitter(player.getLocation(), EmitterEffect.Smoke, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
								emitterList.add(emitter);
								emitter.save();
								sender.sendMessage("§7Created smoke effect");
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
							sender.sendMessage("§5Incorrect command usage");
							sender.sendMessage("§d/effect smoke");
							sender.sendMessage("§d/effect smoke [direction]");
							sender.sendMessage("§d/effect smoke [direction] [frequency]");
						}
					} else {
						sender.sendMessage("§7Please stand on a block before creating a smoke effect");
					}
				} else if (args[0].equalsIgnoreCase("flames")) {
					if (args.length == 1) {
						Emitter emitter = new Emitter(player.getLocation(), EmitterEffect.Flames, 4, 2);
						emitterList.add(emitter);
						emitter.save();
						sender.sendMessage("§7Created flames effect");
					} else if (args.length == 2) {
						if (isInteger(args[1])) {
							Emitter emitter = new Emitter(player.getLocation(), EmitterEffect.Flames, 4, Integer.parseInt(args[1]));
							emitterList.add(emitter);
							emitter.save();
							sender.sendMessage("§7Created flames effect");
						} else {
							sender.sendMessage("§5Please enter a valid frequency");
						}
					} else {
						sender.sendMessage("§5Incorrect command usage");
						sender.sendMessage("§d/effect flames");
						sender.sendMessage("§d/effect flames [frequency]");
					}
				} else if (args[0].equalsIgnoreCase("potion")) {
					if (args.length == 1) {
						Emitter emitter = new Emitter(player.getLocation(), EmitterEffect.Potion, 4, 2);
						emitterList.add(emitter);
						emitter.save();
						sender.sendMessage("§7Created potion effect");
					} else if (args.length == 2) {
						if (isInteger(args[1])) {
							Emitter emitter = new Emitter(player.getLocation(), EmitterEffect.Potion, 4, Integer.parseInt(args[1]));
							emitterList.add(emitter);
							emitter.save();
							sender.sendMessage("§7Created potion effect");
						} else {
							sender.sendMessage("§5Please enter a valid frequency");
						}
					} else {
						sender.sendMessage("§5Incorrect command usage");
						sender.sendMessage("§d/effect potion");
						sender.sendMessage("§d/effect potion [frequency]");
					}
				} else if (EmitterEffect.contains(args[0])) {
					EmitterEffect effect = EmitterEffect.parse(args[0]);
					if (getProfile(player).rank.isHigher(effect.minimumRank.getPreviousRank())) {
						if (args.length == 1) {
							Emitter emitter = new Emitter(player.getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5), effect, 1, 6);
							emitterList.add(emitter);
							emitter.save();
							sender.sendMessage("§7Created " + effect.name() + " effect");
						} else if (args.length == 2) {
							if (isInteger(args[1])) {
								Emitter emitter = new Emitter(player.getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5), effect, 1, Integer.parseInt(args[1]));
								emitterList.add(emitter);
								emitter.save();
								sender.sendMessage("§7Created " + effect.name() + " effect");
							} else {
								sender.sendMessage("§5Please enter a valid frequency");
							}
						} else if (args.length == 3) {
							if (isInteger(args[1]) && isInteger(args[2])) {
								Emitter emitter = new Emitter(player.getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5), effect, Integer.parseInt(args[2]), Integer.parseInt(args[1]));
								emitterList.add(emitter);
								emitter.save();
								sender.sendMessage("§7Created " + effect.name() + " effect");
							} else {
								sender.sendMessage("§5Please enter a valid frequency and amount");
							}
						} else {
							sender.sendMessage("§5Incorrect command usage");
							sender.sendMessage("§d/effect " + effect.name());
							sender.sendMessage("§d/effect " + effect.name() + " [frequency]");
							sender.sendMessage("§d/effect " + effect.name() + " [frequency] [amount]");
						}
					} else {
						sender.sendMessage("§5You have to be a higher rank");
						sender.sendMessage("§d" + effect.name() + " rank is required to create this effect");
					}
				} else {
					sender.sendMessage("§5Incorrect command usage");
					sender.sendMessage("§d/effect smoke [direction] [frequency]");
					sender.sendMessage("§d/effect flames [frequency]");
					sender.sendMessage("§d/effect potion [frequency]");
					for (EmitterEffect effectType : EmitterEffect.values()) if (effectType.isParticleEffect()) sender.sendMessage("§d/effect " + effectType.name().toLowerCase() + " [frequency] [amount]");
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
				} else if (args.length == 1) {
					if (isInteger(args[0])) {
						Emitter chimney = new Emitter(player.getLocation(), EmitterEffect.Smoke, Integer.parseInt(args[0]), 2);
						emitterList.add(chimney);
						chimney.save();
						sender.sendMessage("§7Created chimney");
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
					sender.sendMessage("§5Incorrect command usage");
					sender.sendMessage("§d/chimney");
					sender.sendMessage("§d/chimney [direction]");
					sender.sendMessage("§d/chimney [direction] [frequency]");
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/world [worldName]");
				sender.sendMessage("§d/world invite [worldName] [player]");
				sender.sendMessage("§d/world uninvite [worldName] [player]");
				sender.sendMessage("§d/world list");
				sender.sendMessage("§d/world create [worldName] [worldType] [worldOwner]");
			} else {
				if (args[0].equalsIgnoreCase("list")) {
					sender.sendMessage("§5Private worlds");
					sender.sendMessage(paidWorldList.size() == 0 ? "§dThere are no private worlds" : "§d" + paidWorldList.toString().split("\\[")[1].split("\\]")[0]);
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
						sender.sendMessage("§5Incorrect command usage");
						sender.sendMessage("§d/world invite [worldName] [player]");
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
						sender.sendMessage("§5Incorrect command usage");
						sender.sendMessage("§d/world invite [worldName] [player]");
					}
				} else if (args[0].equalsIgnoreCase("create") && args.length == 4) {
					if (player.getName().equals("EvilPeanut")) {
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
			List<String> text = new ArrayList();
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
			player.getInventory().addItem(getBook("Achievement Guide", "Amentrix", text));
			sender.sendMessage("§7Please check your inventory for the achievements guide");
			return true;
		}
		//
		// Donate Command
		//
		if (command.getName().equalsIgnoreCase("donate") || command.getName().equalsIgnoreCase("admin")) {
			sender.sendMessage("§5How to purchase a rank or donate to the server");
			sender.sendMessage("  §d§l1 §dGoto our website at §6http://minecraft.amentrix.com");
			sender.sendMessage("  §d§l2 §dClick the §6Shop §dbutton on the site");
			sender.sendMessage("  §d§l3 §dSelect the rank or item you wish to purchase");
			sender.sendMessage("  §d§l4 §dComplete the transaction and enjoy the rewards");
			sender.sendMessage("§7Purchased ranks allow the use of blocked items and content");
			sender.sendMessage("§7Each rank upgrade unlocks more content");
			getProfile(player).addAchievement(Achievement.GLOBAL_COMMAND_DONATE);
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
				sender.sendMessage("    §2Step 3 - Execute an /region command on the area");
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/slap <player>");
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/shock <player>");
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/rocket <player>");
			}
			return true;
		}
		//
		// Set Rank Command
		//
		if (command.getName().equalsIgnoreCase("setrank")) {
			if (args.length == 1) {
				if (args[0].toLowerCase(Locale.UK).contains("&k")) {
					sender.sendMessage("§7This rank's name is blocked");
					return true;
				}
				for (Rank rank : Rank.values()) {
					if (args[0].equalsIgnoreCase(rank.toString()) && !getProfile(player).rank.isHigher(rank.getPreviousRank())) {
						sender.sendMessage("§7This rank's name is blocked");
						return true;
					}
				}
				String prefix = args[0].startsWith("&") ? args[0] : "&6" + args[0];
				((PlayerProfileAdmin)getProfile(player)).customRankColor = prefix.substring(1, 2);
				((PlayerProfileAdmin)getProfile(player)).customRankPrefix = "§0[" + prefix.replaceAll("&", "§") + "§0]";
				NametagAPI.updateNametagHard(player.getName(), "§" + ((PlayerProfileAdmin)getProfile(player)).rank.getColor((getProfile(player))), null);
			} else {
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/setrank [rank]");
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
				if (isDouble(args[0]) && Double.valueOf(args[0]) <= 41) {
					getProfile(sender).jumpAmplifier = Double.valueOf(args[0]) / 4;
				} else {
					sender.sendMessage("§7Please enter a valid jump height");
				}
			} else {
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/jump [height]");
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
						double doubleSpeed = (double)speed / 100;
						getProfile(sender).walkAmplifier = doubleSpeed;
						player.setWalkSpeed((float) doubleSpeed);
						getProfile(sender).flyAmplifier = doubleSpeed;
						player.setFlySpeed((float) doubleSpeed);
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
							double doubleSpeed = (double)speed / 100;
							getProfile(sender).walkAmplifier = doubleSpeed;
							player.setWalkSpeed((float) doubleSpeed);
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
							double doubleSpeed = (double)speed / 100;
							getProfile(sender).flyAmplifier = doubleSpeed;
							player.setFlySpeed((float) doubleSpeed);
							sender.sendMessage("§7Set walk speed limit to " + args[1]);
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
							sender.sendMessage("§7Set walk speed limit to " + args[1]);
						} else {
							sender.sendMessage("§7Please enter a valid speed above 0 and below 100");
						}
					} else {
						sender.sendMessage("§7Please enter a valid numerical speed");
					}
				}
			} else {
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/speed");
				sender.sendMessage("§d/speed [speed]");
				sender.sendMessage("§d/speed walk [speed]");
				sender.sendMessage("§d/speed fly [speed]");
				sender.sendMessage("§d/speed run [speed]");
			}
			return true;
		}
		//
		// Disguise Command
		//
		if (command.getName().equalsIgnoreCase("disguise")) {
			if (isInSurvival(player) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
				sender.sendMessage("§7Mob disguise can't be used in survival");
			} else {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("kill")) { 
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
					sender.sendMessage("§5Incorrect command usage");
					sender.sendMessage("§d/disguise [mobName]");
					sender.sendMessage("§d/disguise [mobID]");
					sender.sendMessage("§d/disguise kill");
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/name [name]");
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/skull [owner]");
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " [message]");
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
				if (isInSurvival(player)) {
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
						sender.sendMessage("§7You are now wearing a " + getFriendlyName(itemHelmet.getType()) + " helmet");
					}
				} else {
					player.getInventory().setHelmet(player.getInventory().getItemInHand());
					if (player.getInventory().getItemInHand().getType().isBlock() == false) {
						sender.sendMessage("§7You are now wearing a custom helmet");
					} else {
						sender.sendMessage("§7You are now wearing a " + getFriendlyName(player.getInventory().getItemInHand().getType()) + " helmet");
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
			if (isInSurvival(player)) {
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
					for (Region region : regionList) {
						if (isInRegionXRange(region, player.getLocation()) && isInRegionYRange(region, player.getLocation()) && isInRegionZRange(region, player.getLocation())) {
							regionsFoundList.add(ChatColor.LIGHT_PURPLE + region.getRegionName() + " region owned by " + region.getOwner());
						}
					}
					if (regionsFoundList.size() != 0) {
						sender.sendMessage("§5You are in " + regionsFoundList.size() + (regionsFoundList.size() == 1 ? " region" : " regions"));
						for (String name : regionsFoundList) sender.sendMessage(name);
					} else {
						sender.sendMessage("§7Your not in any regions");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("create")) {
					if (args.length == 2) {
						if (getProfile(player).actionLocationA == null || getProfile(player).actionLocationB == null) {
							sender.sendMessage("§7Please select two region boundaries using the golden shovel tool");
						} else {
							if (SQL.isKeyExistant(TableType.Region, args[1].replaceAll("'", "''"))) {
								sender.sendMessage("§7A region with this name already exists");
								return true;
							}
							Region region = new Region(args[1],
									getProfile(player).actionLocationA,
									getProfile(player).actionLocationB,
									false,
									sender.getName(),
									null,
									null,
									null,
									null);
							region.saveRegion();
							regionList.add(region);
							sender.sendMessage("§7Region " + args[1] + " created");
						}
					} else {
						sender.sendMessage("§5Incorrect command usage");
						sender.sendMessage("§d/region add [regionName]");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("protect")) {
					if (args.length == 2) {
						if (SQL.isKeyExistant(TableType.Region, args[1].replaceAll("'", "''"))) {
							for (Region region : regionList) if (region.getRegionName().equalsIgnoreCase(args[1])) {
								if (!sender.getName().equals(region.getOwner()) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
									sender.sendMessage("§7You don't have ownership of this region");
									return true;
								}
								region.isProtected(true);	
								region.saveRegion();
								break;
							}
							sender.sendMessage("§7Region " + args[1] + " protected");
						} else {
							if (getProfile(player).actionLocationA == null || getProfile(player).actionLocationB == null) {
								sender.sendMessage("§7Please select two region boundaries using the golden shovel tool");
							} else {
								Region region = new Region(args[1],
										getProfile(player).actionLocationA,
										getProfile(player).actionLocationB,
										true,
										sender.getName(),
										null,
										null,
										null,
										null);
								region.saveRegion();
								regionList.add(region);
								sender.sendMessage("§7Region " + args[1] + " created and protected");
							}
						}
					} else {
						sender.sendMessage("§5Incorrect command usage");
						sender.sendMessage("§d/region protect [regionName]");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("remove")) {
					if (args.length == 2) {
						if (SQL.isKeyExistant(TableType.Region, args[1].replaceAll("'", "''"))) {
							for (Region region : regionList) {
								if (region.getRegionName().equalsIgnoreCase(args[1])) {
									if (!sender.getName().equals(region.getOwner()) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
										sender.sendMessage("§7You don't have ownership of this region");
										return true;
									}
									region.delete();
									regionList.remove(region);
									sender.sendMessage("§7Region " + args[1] + " removed");
									return true;
								}
							}
						} else {
							sender.sendMessage("§7No regions with this name exist");
							return true;
						}
					}
					sender.sendMessage("§5Incorrect command usage");
					sender.sendMessage("§d/region remove [regionName]");
					return true;
				} else if (args[0].equalsIgnoreCase("setwelcome")) {
					if (args.length > 2) {
						if (SQL.isKeyExistant(TableType.Region, args[1].replaceAll("'", "''"))) {
							StringBuilder message = new StringBuilder();
							for (int i = 2; i < args.length; i++) message.append(args[i] + " ");
							for (Region region : regionList) if (region.getRegionName().equalsIgnoreCase(args[1])) {
								if (!sender.getName().equals(region.getOwner()) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
									sender.sendMessage("§7You don't have ownership of this region");
									return true;
								}
								region.setWelcomeMessage(toFormattedString(message.toString().trim()));
								region.saveRegion();
								break;
							}
							sender.sendMessage("§7Region " + args[1] + " welcome message set");
						} else {
							sender.sendMessage("§7No regions with this name exist");
						}
					} else {
						sender.sendMessage("§5Incorrect command usage");
						sender.sendMessage("§d/region setWelcome [regionName] [welcomeMessage]");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("setleave")) {
					if (args.length > 2) {
						if (SQL.isKeyExistant(TableType.Region, args[1].replaceAll("'", "''"))) {
							StringBuilder message = new StringBuilder();
							for (int i = 2; i < args.length; i++) message.append(args[i] + " ");
							for (Region region : regionList) if (region.getRegionName().equalsIgnoreCase(args[1])) {
								if (!sender.getName().equals(region.getOwner()) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
									sender.sendMessage("§7You don't have ownership of this region");
									return true;
								}
								region.setLeaveMessage(toFormattedString(message.toString().trim()));	
								region.saveRegion();
								break;
							}
							sender.sendMessage("§7Region " + args[1] + " leave message set");
						} else {
							sender.sendMessage("§7No regions with this name exist");
						}
					} else {
						sender.sendMessage("§5Incorrect command usage");
						sender.sendMessage("§d/region setLeave [regionName] [leaveMessage]");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("allow")) {
					if (args.length == 3) {
						if (SQL.isKeyExistant(TableType.Region, args[1].replaceAll("'", "''"))) {
							for (Region region : regionList) if (region.getRegionName().equalsIgnoreCase(args[1])) {
								if (!sender.getName().equals(region.getOwner()) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
									sender.sendMessage("§7You don't have ownership of this region");
									return true;
								}
								if (isProfileExistant(args[2])) {
									OfflinePlayer regionPlayer = getServer().getOfflinePlayer(args[2]);
									region.addAllowedPlayer(regionPlayer.getName());
									region.saveRegion();
									sender.sendMessage("§7" + args[2] + "'s allowed permissions for region " + args[1]);
								} else {
									sender.sendMessage("§7This player doesn't exist");
								}
								break;
							}
						} else {
							sender.sendMessage("§7No regions with this name exist");
						}
					} else {
						sender.sendMessage("§5Incorrect command usage");
						sender.sendMessage("§d/region allow [regionName] [playerName]");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("deny")) {
					if (args.length == 3) {
						if (SQL.isKeyExistant(TableType.Region, args[1].replaceAll("'", "''"))) {
							for (Region region : regionList) {
								if (region.getRegionName().equalsIgnoreCase(args[1])) {
									if (!sender.getName().equals(region.getOwner()) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
										sender.sendMessage("§7You don't have ownership of this region");
										return true;
									}
									if (isProfileExistant(args[2])) {
										OfflinePlayer regionPlayer = getServer().getOfflinePlayer(args[2]);
										region.removeAllowedPlayer(regionPlayer.getName());
										region.saveRegion();
										sender.sendMessage("§7" + args[2] + "'s permissions removed for region " + args[1]);
										break;
									}
									sender.sendMessage("§7This player doesn't exist");
									break;
								}
							}
						} else {
							sender.sendMessage("§7No regions with this name exist");
						}
					} else {
						sender.sendMessage("§5Incorrect command usage");
						sender.sendMessage("§d/region deny [regionName] [playerName]");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp")) {
					if (args.length == 2) {
						for (Region region : regionList) if (region.getRegionName().equalsIgnoreCase(args[1])) {
							player.teleport(region.getLocationA());
							break;
						}
					} else {
						sender.sendMessage("§d/region tp [regionName]");
					}
					return true;
				} else if (args[0].equalsIgnoreCase("setWarp")) {
					if (args.length == 3) {
						if (SQL.isKeyExistant(TableType.Region, args[1].replaceAll("'", "''"))) {
							for (Region region : regionList) {
								if (region.getRegionName().equalsIgnoreCase(args[1])) {
									if (!sender.getName().equals(region.getOwner()) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
										sender.sendMessage("§7You don't have ownership of this region");
										return true;
									} else if (SQL.isKeyExistant(TableType.Warps, args[2].toLowerCase(Locale.UK))) {
										region.setWarp(args[2].toLowerCase(Locale.UK));	
										region.saveRegion();
										sender.sendMessage("§7Region " + args[1] + " warp set");
										break;
									} else {
										sender.sendMessage("§7No warps with this name exist");
										break;
									}
								}
							}
						} else {
							sender.sendMessage("§7No regions with this name exist");
						}
					} else {
						sender.sendMessage("§5Incorrect command usage");
						sender.sendMessage("§d/region setWarp [regionName] [warpName]");
					}
					return true;
				}
			}
			sender.sendMessage("§5Incorrect command usage");
			sender.sendMessage("§d/region scan");
			sender.sendMessage("§d/region create [regionName]");
			sender.sendMessage("§d/region protect [regionName]");
			sender.sendMessage("§d/region remove [regionName]");
			sender.sendMessage("§d/region setWelcome [regionName] [welcomeMessage]");
			sender.sendMessage("§d/region setLeave [regionName] [leaveMessage]");
			sender.sendMessage("§d/region tp [regionName]");
			sender.sendMessage("§d/region setWarp [regionName] [warpName]");
			sender.sendMessage("§d/region allow [regionName] [playerName]");
			sender.sendMessage("§d/region deny [regionName] [playerName]");
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
					if (getProfile(p).rank.isHigher(Rank.POLICE)) {
						p.sendMessage(sender.getName() + " requires assistance: " + message.trim());
						adminOnline = true;
					}
				}
				if (!adminOnline) {
					sender.sendMessage("§7No staff are online to recieve your request");
				}
			} else {
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " [message]");
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
								GlobalStatistics.incrementStatistic(GlobalStatistic.EconomyTrade, Integer.parseInt(args[1]));
							} else {
								String money = SQL.getProperty(TableType.PlayerProfile, args[0], "money");
								money = Integer.toString(Integer.parseInt(money) + Integer.parseInt(args[1]));
								getProfile(sender).money -= Integer.parseInt(args[1]);
								sender.sendMessage("§7You have paid " + getServer().getOfflinePlayer(args[0]).getName() + " §c$" + args[1]);
								GlobalStatistics.incrementStatistic(GlobalStatistic.EconomyTrade, Integer.parseInt(args[1]));
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " [player] [amount]");
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
					if (isInSurvival(player) == false || getProfile(sender).rank.isHigher(Rank.TYCOON)) {
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
						if (isInSurvival(getPlayer(args[1])) == false) {
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " [mode]");
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " [name]");
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " [player]");
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/unmute [player]");
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
				if (SQL.getProperty(TableType.PlayerLocation, args[0], "home_location") != null) {
					String[] location = SQL.getProperty(TableType.PlayerLocation, args[0], "home_location").split(">");
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
			if (isInSurvival(player) == false || getProfile(sender).rank.isHigher(Rank.TYCOON)) {
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
					sender.sendMessage("§5Incorrect command usage");
					sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " [mob] <amount>");
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
				String playerWarps = SQL.getProperty(TableType.PlayerProfile, args[1], "warp_list");
				if (playerWarps != null) {
					List<String> warps = Arrays.asList(playerWarps.split(","));
					sender.sendMessage("§5" + args[1] + "'s warps");
					sender.sendMessage("§d" + warps.toString().substring(1).substring(0, warps.toString().length() - 2));
				} else {
					sender.sendMessage("§7" + args[1] + " doesn't own any warps");
				}
			} else {
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/warp [warpName]");
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
						for (Region region : regionList) {
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " [warpName]");
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
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " [warpName]");
			}
			return true;
		}
		//
		// Enchant Command
		//
		if (command.getName().equalsIgnoreCase("enchant")) {
			if (isInSurvival(player) == false || getProfile(sender).rank.isHigher(Rank.TYCOON)) {
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
						} catch (RuntimeException exception) {
							throw exception;
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
					sender.sendMessage("§5Incorrect command usage");
					sender.sendMessage("§d/enchant [enchantmentID] [enchantmentLevel]");
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
			if (isInSurvival(player) == false || getProfile(sender).rank.isHigher(Rank.ELITE)) {
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
			if (isInSurvival(player) == false || getProfile(sender).rank.isHigher(Rank.ELITE)) {
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
			if (isInSurvival(player) == false || getProfile(sender).rank.isHigher(Rank.ELITE)) {
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
			if (!isInSurvival(player) || getProfile(sender).rank.isHigher(Rank.ELITE)) {
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
					sender.sendMessage("§5Incorrect command usage");
					sender.sendMessage("§d/time [time]");
					sender.sendMessage("§d/time dawn");
					sender.sendMessage("§d/time day");
					sender.sendMessage("§d/time dusk");
					sender.sendMessage("§d/time night");
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
			sender.sendMessage("§7The world spawn has been moved");
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
			if (!isInSurvival(player) || getProfile(sender).rank.isHigher(Rank.TYCOON)) {
				int entities = player.getWorld().getLivingEntities().size();
				for (LivingEntity entity : player.getWorld().getLivingEntities()) if (entity.getType() == EntityType.PLAYER || (entity.getType() == EntityType.WOLF && ((Tameable)entity).isTamed())) entities--; else entity.remove();
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
				broadcastPlayerMessage(sender.getName(), player.getDisplayName() + " hugs " + args[0]);
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
				broadcastPlayerMessage(sender.getName(), player.getDisplayName() + " kisses " + args[0]);
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
			if (isInSurvival(player) == false || getProfile(sender).rank.isHigher(Rank.ELITE)) {
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
			if (isInSurvival(player) == false || getProfile(sender).rank.isHigher(Rank.ELITE)) {
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
			if (isInSurvival(player) == false || getProfile(sender).rank.isHigher(Rank.ELITE)) {
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
			if (isInSurvival(player) == false || getProfile(sender).rank.isHigher(Rank.ELITE)) {
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
		if (command.getName().equalsIgnoreCase("accept")) {
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
		if (command.getName().equalsIgnoreCase("tp") || command.getName().equalsIgnoreCase("teleport")) {
			if (args.length == 1) {
				if (getPlayer(args[0]) != null) {
					if (!getProfile(sender).rank.isHigher(getProfile(getPlayer(args[0])).rank.getPreviousRank())) {
						getProfile(getPlayer(args[0])).teleportantName = sender.getName();
						getPlayer(args[0]).sendMessage("§d" + player.getDisplayName() + " §dwishes to teleport to you");
						getPlayer(args[0]).sendMessage("§aTo accept the request type /accept");
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
					player.teleport(destination);
					sender.sendMessage("§aTeleported to co-ordinates");
					sender.sendMessage("§dX: " + args[0]);
					sender.sendMessage("§dY: " + args[1]);
					sender.sendMessage("§dZ: " + args[2]);
				} else {
					sender.sendMessage("§5Incorrect command usage");
					sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " [x] [y] [z]");
					sender.sendMessage("§7The X, Y and Z values must be numbers");
				}
			} else {
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " [player]");
				sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " [x] [y] [z]");
			}
			return true;
		}
		//
		// Teleport Player Here Command
		//
		if (command.getName().equalsIgnoreCase("tphere") || command.getName().equalsIgnoreCase("teleporthere")) {
			if (args.length == 1) {
				if (getPlayer(args[0]) != null) {
					if (!isInSurvival(player) || getProfile(sender).rank.isHigher(Rank.ELITE)) {
						getPlayer(args[0]).teleport(player);
					} else {
						sender.sendMessage("§7You can't teleport a player who is in survival");
					}
				} else {
					sender.sendMessage("§7You can't teleport an offline player");
				}
			} else {
				sender.sendMessage("§5Incorrect command usage");
				sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " [player]");
			}
			return true;
		}
		//
		// Give Command
		//
		if (command.getName().equalsIgnoreCase("item") || command.getName().equalsIgnoreCase("give")) {
			if (isInSurvival(player) && !getProfile(sender).rank.isHigher(Rank.TYCOON)) {
				sender.sendMessage("§7Spawning of items is blocked in survival");
			} else {
				if (args.length == 1) {
					Material material = getBlockMaterial(args[0]);
					if (material == null) {
						player.sendMessage("§7Please enter a valid name or ID");
					} else {
						player.getInventory().addItem(new ItemStack(material));
					}
				} else if (args.length == 2) {
					Material material = getBlockMaterial(args[0]);
					if (material == null || !isInteger(args[1])) {
						player.sendMessage("§7Please enter a valid name or ID and amount");
					} else {
						player.getInventory().addItem(new ItemStack(material, Integer.parseInt(args[1])));
					}
				} else if (args.length == 3) {
					Material material = getBlockMaterial(args[0]);
					if (material == null || !isInteger(args[1]) || !isByte(args[2])) {
						player.sendMessage("§7Please enter a valid name or ID, amount and data");
					} else {
						player.getInventory().addItem(new ItemStack(material, Integer.parseInt(args[1]), Byte.parseByte(args[2])));
					}
				} else {
					sender.sendMessage("§5Incorrect command usage");
					sender.sendMessage("§d/" + command.getName().toLowerCase(Locale.UK) + " [ID / name] <amount> <data>");
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
	 * Check if an entity is in the survival world
	 * @param entity The entity execute the check with
	 * @return If the entity is in an survival world or not
	 */
	public static Boolean isInSurvival(Entity entity) {
		return entity.getWorld().getName().equals("SurvivalLand") || entity.getWorld().getName().equals("SurvivalLandNether") || entity.getWorld().getName().equals("SurvivalLandTheEnd") ? true : false;
	}

	/**
	 * Check if a world is a survival world
	 * @param worldName The world name to execute the check with
	 * @return If the world is a survival world or not
	 */
	public static Boolean isInSurvival(World world) {
		return world.getName().equals("SurvivalLand") || world.getName().equals("SurvivalLandNether") || world.getName().equals("SurvivalLandTheEnd") ? true : false;
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
	 * Check if a string can be casted to a float
	 * @param string The string to execute the cast check with
	 * @return If the string can be casted to a float
	 */
	public static Boolean isFloat(String string) {
		try {Float.parseFloat(string); return true;} catch (Exception exception) {return false;}
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
	 * Sends a message alert to moderator and above ranks
	 * @param alert The message
	 */
	public static void alert(String alert) {
		for (PlayerProfile profile : playerProfiles.values()) {
			if (profile.rank.isHigher(Rank.ARCHITECT)) Bukkit.getServer().getPlayer(profile.name).sendMessage("§7§O" + alert);
		}
	}

	/**
	 * Returns a player from the player name
	 * @param name The name of the player
	 * @return The player
	 */
	public static Player getPlayer(String name) {
		if (Bukkit.getServer().getPlayer(name) != null) return Bukkit.getServer().getPlayer(name);
		for (PlayerProfile profile : playerProfiles.values()) {
			if (profile.name.toLowerCase(Locale.UK).startsWith(name.toLowerCase(Locale.UK)) || (profile instanceof PlayerProfileAdmin && ((PlayerProfileAdmin)profile).nameAlias != null && ((PlayerProfileAdmin)profile).getStrippedNameAlias().toLowerCase(Locale.UK).startsWith(name.toLowerCase(Locale.UK)))) return Bukkit.getServer().getPlayer(profile.name);
		}
		return null;
	}

	/**
	 * Returns if a location is in a region
	 * @param region The region to execute the check with
	 * @param loc The location to execute the check with
	 * @return If the location is inside the region
	 */
	public static Boolean isInRegion(Region region, Location loc) {
		if (region.getLocationA().getWorld().getName().equals(loc.getWorld().getName()) == false) return false;
		if (isInRegionXRange(region, loc) && isInRegionYRange(region, loc) && isInRegionZRange(region, loc)) return true;
		return false;
	}

	/**
	 * Returns if the region is protected or not for the player
	 * @param player The player to execute the check with
	 * @param loc The location to execute the check with
	 * @return If the location is in a protected region which the player doesn't have rights to
	 */
	public static Boolean isInProtectedRegion(Location loc, Player player) {
		if (getProfile(player).rank.isAdmin()) return false;
		for (Region region : regionList) {
			if (region.isProtected() == false) continue;
			if (region.getOwner().equals(player.getName()) || region.getAllowedPlayers().contains(player.getName())) continue;
			if (region.getLocationA().getWorld().getName().equals(loc.getWorld().getName()) == false) continue;
			if (isInRegionXRange(region, loc)) {
				if (isInRegionYRange(region, loc)) {
					if (isInRegionZRange(region, loc)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns if the player is in the region's X range or not
	 * @param region The region to execute the check with
	 * @param location The location to execute the check with
	 * @return If the location is in the region's X range
	 */
	static Boolean isInRegionXRange(Region region, Location location) {
		if (region.getLocationA().getBlockX() <= region.getLocationB().getBlockX()) {
			if (location.getBlockX() >= region.getLocationA().getBlockX() && location.getBlockX() <= region.getLocationB().getBlockX()) return true;
		} else if (region.getLocationA().getBlockX() >= region.getLocationB().getBlockX()) {
			if (location.getBlockX() <= region.getLocationA().getBlockX() && location.getBlockX() >= region.getLocationB().getBlockX()) return true;
		}
		return false;
	}

	/**
	 * Returns if the player is in the region's Y range or not
	 * @param region The region to execute the check with
	 * @param location The location to execute the check with
	 * @return If the location is in the region's Y range
	 */
	static Boolean isInRegionYRange(Region region, Location playerLocation) {
		if (region.getLocationA().getBlockY() <= region.getLocationB().getBlockY()) {
			if (playerLocation.getBlockY() >= region.getLocationA().getBlockY() && playerLocation.getBlockY() <= region.getLocationB().getBlockY()) return true;
		} else if (region.getLocationA().getBlockY() >= region.getLocationB().getBlockY()) {
			if (playerLocation.getBlockY() <= region.getLocationA().getBlockY() && playerLocation.getBlockY() >= region.getLocationB().getBlockY()) return true;
		}
		return false;
	}

	/**
	 * Returns if the player is in the region's Z range or not
	 * @param region The region to execute the check with
	 * @param location The location to execute the check with
	 * @return If the location is in the region's Z range
	 */
	static Boolean isInRegionZRange(Region region, Location playerLocation) {
		if (region.getLocationA().getBlockZ() <= region.getLocationB().getBlockZ()) {
			if (playerLocation.getBlockZ() >= region.getLocationA().getBlockZ() && playerLocation.getBlockZ() <= region.getLocationB().getBlockZ()) return true;
		} else if (region.getLocationA().getBlockZ() >= region.getLocationB().getBlockZ()) {
			if (playerLocation.getBlockZ() <= region.getLocationA().getBlockZ() && playerLocation.getBlockZ() >= region.getLocationB().getBlockZ()) return true;
		}
		return false;
	}

	/**
	 * Return the entity type from the entity type name
	 * @param name The name of the entity type
	 * @return The entity type
	 */
	public static EntityType getEntity(String name) {
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
	public static Enchantment getEnchantment(String name) {
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
	 * Returns a colorized string
	 * @param string The string to colorize
	 * @return The colorized string
	 */
	public static String colorizeString(String string) {
		String name = "";
		int color = 0;
		for (char c : string.toCharArray()) {
			if (color == 0) name += "§a" + c;
			if (color == 1) name += "§b" + c;
			if (color == 2) name += "§c" + c;
			if (color == 3) name += "§d" + c;
			if (color == 4) {
				name += "§e" + c;
				color = 0;
			} else {
				color++;
			}
		}
		return name;
	}

	/**
	 * Returns an itemstack of a book
	 * @param title The title of the book
	 * @param author The author of the book
	 * @param text The text in the book
	 * @return The book itemstack
	 */
	public static ItemStack getBook(String title, String author, List<String> text) {
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
	public static String toRomanNumerals(String number) {
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
			text = text.replaceAll("-_*", "ツ");
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
		if (block.getWorld().hasStorm() && block.getWorld().isThundering() && block.getBiome() != Biome.FROZEN_OCEAN && block.getBiome() != Biome.FROZEN_RIVER && block.getBiome() != Biome.ICE_MOUNTAINS && block.getBiome() != Biome.ICE_PLAINS && block.getBiome() != Biome.TAIGA && block.getBiome() != Biome.TAIGA_HILLS) return "Lightning";
		if (block.getWorld().hasStorm() && (block.getBiome() == Biome.FROZEN_OCEAN || block.getBiome() == Biome.FROZEN_RIVER || block.getBiome() == Biome.ICE_MOUNTAINS || block.getBiome() == Biome.ICE_PLAINS || block.getBiome() == Biome.TAIGA || block.getBiome() == Biome.TAIGA_HILLS)) return "Snow";
		if (block.getWorld().hasStorm()) return "Rain";
		return "Sunny";
	}

	/**
	 * Update the online player web statistic
	 * @param onlinePlayers The number of online players
	 */
	public static void updateWebPlayerStatistics(int onlinePlayers) {
		try (BufferedWriter out = new BufferedWriter(new FileWriter("C:/Program Files (x86)/Apache Software Foundation/Apache2.2/htdocs/playerStats.htm"))) {
			out.write("<html><body style='background:transparent;overflow:hidden'><p style='color:#31AFF5;text-align:center;font-family:Calibri'>" + onlinePlayers + " Players online<p></body></html>");
		} catch (RuntimeException exception) {
			throw exception;
		} catch (Exception exception) {
			logWarning("Failed to update player count web statistics");
		}
	}

	/**
	 * Return if a player profile exists
	 * @param playerName The player name to execute the check with
	 * @return If the player's profile is existant
	 */
	public static boolean isProfileExistant(String playerName) {
		return SQL.getProperty(TableType.PlayerProfile, playerName, "player_name") != null;
	}

	/**
	 * Gets the players creative inventory and equips it
	 * @param player The player
	 */
	public static void getCreativeInventory(Player player) {	
		String inventory = SQL.getProperty(TableType.PlayerProfile, player.getName(), "inventory_creative");
		if (inventory == null) {
			player.getInventory().clear();
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
			// Load inventory contents
			player.getInventory().clear();
			// Save inventory contents
			for (int i = 0; i < player.getInventory().getSize(); i++) {
				if (config.get(Integer.toString(i)) != null) 
					player.getInventory().setItem(i, (ItemStack)config.get(Integer.toString(i)));
			}
			// Load armour
			player.getInventory().setHelmet((ItemStack)config.get("head"));
			player.getInventory().setChestplate((ItemStack)config.get("chest"));
			player.getInventory().setLeggings((ItemStack)config.get("legs"));
			player.getInventory().setBoots((ItemStack)config.get("boots"));
			// Load health value
			player.setHealth((double)config.get("health"));
			// Load hunger value
			player.setFoodLevel((int)config.get("hunger"));
			// Load level and xp
			player.setLevel((int)config.get("level"));
			player.setExp((float)config.getDouble("xp"));
			//
		}
	}

	/**
	 * Get the players survival inventory and equip it
	 * @param player The player
	 */
	public static void getSurvivalInventory(Player player) {
		String inventory = SQL.getProperty(TableType.PlayerProfile, player.getName(), "inventory_survival");
		if (inventory == null) {
			player.getInventory().clear();
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
			// Load inventory contents
			player.getInventory().clear();
			// Save inventory contents
			for (int i = 0; i < player.getInventory().getSize(); i++) {
				if (config.get(Integer.toString(i)) != null) 
					player.getInventory().setItem(i, (ItemStack)config.get(Integer.toString(i)));
			}
			// Load armour
			player.getInventory().setHelmet((ItemStack)config.get("head"));
			player.getInventory().setChestplate((ItemStack)config.get("chest"));
			player.getInventory().setLeggings((ItemStack)config.get("legs"));
			player.getInventory().setBoots((ItemStack)config.get("boots"));
			// Load health value
			player.setHealth((double)config.get("health"));
			// Load hunger value
			player.setFoodLevel((int)config.get("hunger"));
			// Load level and xp
			player.setLevel((int)config.get("level"));
			player.setExp((float)config.getDouble("xp"));
			//
		}
	}

	/**
	 * Set the players creative inventory
	 * @param player The player
	 */
	public static void setCreativeInventory(Player player) {
		YamlConfiguration config = new YamlConfiguration();
		// Save inventory contents
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack item = player.getInventory().getItem(i);
			if (item != null) config.set(Integer.toString(i), item);
		}
		// Save armour
		config.set("head", player.getInventory().getHelmet());
		config.set("chest", player.getInventory().getChestplate());
		config.set("legs", player.getInventory().getLeggings());
		config.set("boots", player.getInventory().getBoots());
		// Save health value
		config.set("health", player.getHealth());
		// Save hunger value
		config.set("hunger", player.getFoodLevel());
		// Save level and xp
		config.set("level", player.getLevel());
		config.set("xp", player.getExp());
		//
		SQL.setProperty(TableType.PlayerProfile, player.getName(), "inventory_creative", config.saveToString().replaceAll("'", "''"));
	}

	/**
	 * Set the players survival inventory
	 * @param player The player
	 */
	public static void setSurvivalInventory(Player player) {
		YamlConfiguration config = new YamlConfiguration();
		// Save inventory contents
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack item = player.getInventory().getItem(i);
			if (item != null) config.set(Integer.toString(i), item);
		}
		// Save armour
		config.set("head", player.getInventory().getHelmet());
		config.set("chest", player.getInventory().getChestplate());
		config.set("legs", player.getInventory().getLeggings());
		config.set("boots", player.getInventory().getBoots());
		// Save health value
		config.set("health", player.getHealth());
		// Save hunger value
		config.set("hunger", player.getFoodLevel());
		// Save level and xp
		config.set("level", player.getLevel());
		config.set("xp", player.getExp());
		//
		SQL.setProperty(TableType.PlayerProfile, player.getName(), "inventory_survival", config.saveToString().replaceAll("'", "''"));
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
				!SQL.getPropertyFromCriteria(TableType.ContainerProtection, "world='" + location.getWorld().getName() + 
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
	 * Return the block material from an argument
	 * @param block The argument containing the block ID or name
	 * @return The block material or null if it can't be found
	 */
	public static Material getBlockMaterial(String block) {
		int blockID = 0;
		for (Entry<Material, List<String>> entry : blockList.entrySet()) {
			if (entry.getKey() == null) {
				blockID += 5;
				continue;
			}
			if (isInteger(block)) {
				if (Integer.parseInt(block) == blockID) return entry.getKey();
			} else {
				if (entry.getValue() == null) continue;
				for (String subItem : entry.getValue()) {
					if (subItem != null && block.equalsIgnoreCase(subItem)) {
						return entry.getKey();
					}
				}
			}
			blockID++;
		}
		return null;
	}

	public static Biome getBiome(String biome) {
		for (Entry<Biome, List<String>> entry : biomeList.entrySet()) {
			if (entry.getValue() == null) continue;
			for (String subItem : entry.getValue()) {
				if (subItem != null && biome.equalsIgnoreCase(subItem)) {
					return entry.getKey();
				}
			}
		}
		return null;
	}

	public static TreeType getTreeType(String treeType) {
		for (Entry<TreeType, List<String>> entry : treeTypeList.entrySet()) {
			if (entry.getValue() == null) continue;
			for (String subItem : entry.getValue()) {
				if (subItem != null && treeType.equalsIgnoreCase(subItem)) {
					return entry.getKey();
				}
			}
		}
		return null;
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
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (!getProfile(player).isMuted(playerName)) {
				player.sendMessage(message);
			}
		}
	}

	public static void sendParticlesPacket(Location loc, String effectName, float effectSpeed, int amount)
	{
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.WORLD_PARTICLES);
		packet.getStrings().write(0, effectName);
		packet.getFloat().write(0, (float)loc.getX());
		packet.getFloat().write(1, (float)loc.getY());
		packet.getFloat().write(2, (float)loc.getZ());
		packet.getFloat().write(3, (float) 0);
		packet.getFloat().write(4, (float) 0);
		packet.getFloat().write(5, (float) 0);
		packet.getFloat().write(6, effectSpeed);
		packet.getIntegers().write(0, amount);
		try {
			for (Player player : Bukkit.getServer().getOnlinePlayers()) if (loc.getWorld() == player.getWorld() && loc.distance(player.getLocation()) <= 16) ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static String getCommandBlockCommand(PacketContainer packet)
	{
		try {
			byte[] Content = packet.getByteArrays().read(0);
			boolean found = false;
			String rec = "";
			for (byte f : Content) {
				if ((f == 47) && (!found))
				{
					found = true;
					rec = rec + (char)f;
				}
				else
				{
					if ((f == 0) && (found)) {
						break;
					}
					if (found) {
						rec = rec + (char)f;
					}
				}
			}
			return rec.substring(1).split(" ")[0];
		} catch (Exception e) {
			return null;
		}
	}

	public static String getCommandBlockCommandParameters(PacketContainer packet)
	{
		byte[] Content = packet.getByteArrays().read(0);
		boolean found = false;
		String rec = "";
		for (byte f : Content) {
			if ((f == 47) && (!found))
			{
				found = true;
				rec = rec + (char)f;
			}
			else
			{
				if ((f == 0) && (found)) {
					break;
				}
				if (found) {
					rec = rec + (char)f;
				}
			}
		}
		return rec.contains(" ") ? rec.substring(1).split(" ")[1] : "";
	}

	public static String getFriendlyName(Material material) {
		return blockList.get(material) == null ? "Air" : blockList.get(material).get(0);
	}

	public static void incrementOwnerBalance(int increment) {
		if (getPlayer("EvilPeanut") != null) {
			getProfile("EvilPeanut").money += increment;
			getPlayer("EvilPeanut").sendMessage("§7You have recieved §a$" + increment + " §7from taxes");
		} else {
			String money = SQL.getProperty(TableType.PlayerProfile, "EvilPeanut", "money");
			money = Integer.toString(Integer.parseInt(money) + increment);
			SQL.setProperty(TableType.PlayerProfile, "EvilPeanut", "money", money);
		}
	}
	
	public String getUnlockedTitles(Player player) {
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
		return titles;
	}
}
