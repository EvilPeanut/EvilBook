package com.amentrix.evilbook.eviledit;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.amentrix.evilbook.eviledit.utils.CraftEvilEditEngine;
import com.amentrix.evilbook.eviledit.utils.EditWandMode;
import com.amentrix.evilbook.eviledit.utils.EvilEditEngine;
import com.amentrix.evilbook.eviledit.utils.Selection;
import com.amentrix.evilbook.main.DynamicSign;
import com.amentrix.evilbook.main.Emitter;
import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.PlayerProfileAdmin;
import com.amentrix.evilbook.statistics.Statistic;

/**
 * EvilEdit region methods
 * @author Reece Aaron Lecrivain
 */
public class Region {
	private static EvilBook plugin;
	
	public static void init(EvilBook plugin) {
		Region.plugin = plugin;
	}
	
	public static void undoEdit(Player player) {
		if (((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getUndo().size() == 0) {
			player.sendMessage("§7You have no EvilEdit actions to undo");
		} else {
			EvilEditEngine engine = CraftEvilEditEngine.createEngineSilent(plugin, ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getUndo().get(0).getWorld(), player);
			for (BlockState block : ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getUndo()) {
				// Set the block material and data which includes direction
				engine.setBlock(block.getLocation(), block.getType().getId(), block.getData().getData());
				// Handle blocks with special states
				Session.setState(block, block.getLocation().getBlock().getState());
			}
			((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.clearUndo();
			// Undo dynamic sign removals
			for (DynamicSign dynamicSign : ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.undoDynamicSignList) {
				dynamicSign.create();
				EvilBook.dynamicSignList.add(dynamicSign);
			}
			((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.undoDynamicSignList = new ArrayList<>();
			// Undo emitter removals
			for (Emitter emitter : ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.undoEmitterList) {
				emitter.save();
				EvilBook.emitterList.add(emitter);
			}
			((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.undoEmitterList = new ArrayList<>();
			//
			engine.notifyClients(Statistic.BlocksPlaced);
			player.sendMessage("§7Undone " + engine.getBlocksChanged() + " block edit");
		}
	}
	
	public static void drainArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 0) {
			player.sendMessage("§5Incorrect command usage");
			player.sendMessage("§d/drain");
		} else if (selection.isValid()) {
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(plugin, selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
					{
						if (selection.getBlock(x, y, z).getType() == Material.WATER || selection.getBlock(x, y, z).getType() == Material.STATIONARY_WATER || selection.getBlock(x, y, z).getType() == Material.LAVA || selection.getBlock(x, y, z).getType() == Material.STATIONARY_LAVA) {
							engine.setBlock(x, y, z, Material.AIR.getId(), 0);
						}
					}
				}
			}
			engine.notifyClients(Statistic.BlocksBroken);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks drained");
		}
	}	

	public static void paste(Player player, String[] args) {
		if (EvilBook.isInSurvival(player)) {
			player.sendMessage("§7EvilEdit can't be used in survival");
		} else if (args.length != 0) {
			player.sendMessage("§5Incorrect command usage");
			player.sendMessage("§d/paste");
		} else if (((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopy().size() == 0) {
			player.sendMessage("§7Please copy an area of blocks before attempting paste");
		} else {
			int topBlockX = ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopy().get(0).getLocation().getBlockX();
			int bottomBlockX = ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopy().get(0).getLocation().getBlockX();
			int topBlockY = ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopy().get(0).getLocation().getBlockY();
			int bottomBlockY = ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopy().get(0).getLocation().getBlockY();
			int topBlockZ = ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopy().get(0).getLocation().getBlockZ();
			int bottomBlockZ = ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopy().get(0).getLocation().getBlockZ();
			for (BlockState block : ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopy()) {
				if (block.getLocation().getBlockX() > topBlockX) topBlockX = block.getLocation().getBlockX();
				if (block.getLocation().getBlockX() < bottomBlockX) bottomBlockX = block.getLocation().getBlockX();
				if (block.getLocation().getBlockY() > topBlockY) topBlockY = block.getLocation().getBlockY();
				if (block.getLocation().getBlockY() < bottomBlockY) bottomBlockY = block.getLocation().getBlockY();
				if (block.getLocation().getBlockZ() > topBlockZ) topBlockZ = block.getLocation().getBlockZ();
				if (block.getLocation().getBlockZ() < bottomBlockZ) bottomBlockZ = block.getLocation().getBlockZ();
			}
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(plugin, player.getWorld(), player);
			for (BlockState block : ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopy()) {
				Location loc = new Location(player.getWorld(), 
						player.getLocation().getBlockX() + (block.getLocation().getBlockX() - bottomBlockX), 
						player.getLocation().getBlockY() + (block.getLocation().getBlockY() - bottomBlockY), 
						player.getLocation().getBlockZ() + (block.getLocation().getBlockZ() - bottomBlockZ));
				// Set the block material and data which includes direction
				engine.setBlock(loc, block.getType().getId(), block.getData().getData());
				// Handle blocks with special states
				Session.setState(block, loc.getBlock().getState());
			}
			// Paste dynamic signs
			for (DynamicSign dynamicSign : ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.copyDynamicSignList) {
				DynamicSign newDynamicSign = dynamicSign;
				newDynamicSign.location = new Location(player.getWorld(), 
						player.getLocation().getBlockX() + (newDynamicSign.location.getBlockX() - bottomBlockX), 
						player.getLocation().getBlockY() + (newDynamicSign.location.getBlockY() - bottomBlockY), 
						player.getLocation().getBlockZ() + (newDynamicSign.location.getBlockZ() - bottomBlockZ));
				newDynamicSign.create();
				EvilBook.dynamicSignList.add(newDynamicSign);
			}
			((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.copyDynamicSignList = new ArrayList<>();
			// Paste emitters
			for (Emitter emitter : ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.copyEmitterList) {
				Emitter newEmitter = emitter;
				newEmitter.location = new Location(player.getWorld(), 
						player.getLocation().getBlockX() + (newEmitter.location.getBlockX() - bottomBlockX), 
						player.getLocation().getBlockY() + (newEmitter.location.getBlockY() - bottomBlockY), 
						player.getLocation().getBlockZ() + (newEmitter.location.getBlockZ() - bottomBlockZ));
				newEmitter.save();
				EvilBook.emitterList.add(newEmitter);
			}
			((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.copyEmitterList = new ArrayList<>();
			//
			engine.notifyClients(Statistic.BlocksPlaced);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks pasted");
		}
	}

	public static void copy(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 0) {
			player.sendMessage("§5Incorrect command usage");
			player.sendMessage("§d/copy");
		} else if (selection.isValid()) {
			((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.clearCopy();
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
					{
						// Append the block to the copy list
						((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.appendCopy(selection.getBlock(x, y, z).getState());
						// Dynamic signs
						for (DynamicSign dynamicSign : EvilBook.dynamicSignList) {
							if (dynamicSign.location.getWorld().getName().equals(selection.getWorld().getName()) && 
									dynamicSign.location.getBlockX() == x &&
									dynamicSign.location.getBlockY() == y &&
									dynamicSign.location.getBlockZ() == z) {
								((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.copyDynamicSignList.add(dynamicSign);
							}
						}
						// Emitters
						for (Emitter emitter : EvilBook.emitterList) {
							if (emitter.location.getWorld().getName().equals(selection.getWorld().getName()) && 
									emitter.location.getBlockX() == x &&
									emitter.location.getBlockY() == y &&
									emitter.location.getBlockZ() == z) {
								((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.copyEmitterList.add(emitter);
							}
						}
					}
				}
			}
			player.sendMessage("§7Selection of " + selection.getVolume() + " blocks copied");
		}
	}

	public static void randomDeleteArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length > 2) {
			player.sendMessage("§5Incorrect command usage");
			player.sendMessage("§d/rdel");
			player.sendMessage("§d/rdel [blockID / blockName]");
			player.sendMessage("§d/rdel [blockID / blockName] [blockData]");
		} else if (args.length == 2 && !EvilBook.isByte(args[1])) {
			player.sendMessage("§7Please enter a valid block data value");
		} else if (selection.isValid()) {
			Material deleteBlockMaterial = null;
			if (args.length >= 1) {
				deleteBlockMaterial = EvilBook.getBlockMaterial(args[0]);
				if (deleteBlockMaterial == null) {
					player.sendMessage("§7Please enter a valid block name or ID");
					return;
				}
			}
			Random randomizer = new Random();
			Byte blockData = args.length == 2 ? Byte.parseByte(args[1]) : 0;
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(plugin, selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
					{
						if (randomizer.nextBoolean() && (args.length == 0 || (args.length == 1 && selection.getBlock(x, y, z).getType() == deleteBlockMaterial) || (args.length == 2 && selection.getBlock(x, y, z).getType() == deleteBlockMaterial && selection.getBlock(x, y, z).getData() == blockData))) {
							engine.setBlock(x, y, z, Material.AIR.getId(), (byte) 0);
						}
					}
				}
			}
			engine.notifyClients(Statistic.BlocksBroken);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks randomly deleted");
		}
	}

	public static void deleteArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length > 2) {
			player.sendMessage("§5Incorrect command usage");
			player.sendMessage("§d/del");
			player.sendMessage("§d/del [blockID / blockName]");
			player.sendMessage("§d/del [blockID / blockName] [blockData]");
		} else if (args.length == 2 && !EvilBook.isByte(args[1])) {
			player.sendMessage("§7Please enter a valid block data value");
		} else if (selection.isValid()) {
			Material deleteBlockMaterial = null;
			if (args.length >= 1) {
				deleteBlockMaterial = EvilBook.getBlockMaterial(args[0]);
				if (deleteBlockMaterial == null) {
					player.sendMessage("§7Please enter a valid block name or ID");
					return;
				}
			}
			Byte blockData = args.length == 2 ? Byte.parseByte(args[1]) : 0;
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(plugin, selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getTopYBlock(); y >= selection.getBottomYBlock(); y--)
					{
						if (args.length == 0 || (args.length == 1 && selection.getBlock(x, y, z).getType() == deleteBlockMaterial) || (args.length == 2 && selection.getBlock(x, y, z).getType() == deleteBlockMaterial && selection.getBlock(x, y, z).getData() == blockData)) {
							engine.setBlock(x, y, z, Material.AIR.getId(), (byte) 0);
						}
					}
				}
			}
			engine.notifyClients(Statistic.BlocksBroken);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks deleted");
		}
	}

	public static void randomReplaceArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 2 && args.length != 4) {
			player.sendMessage("§5Incorrect command usage");
			player.sendMessage("§d/rreplace [blockID / blockName] [blockID / blockName]");
			player.sendMessage("§d/rreplace [blockID / blockName] [blockData] [blockID / blockName] [blockData]");
		} else if (args.length == 4 && (!EvilBook.isByte(args[1]) || !EvilBook.isByte(args[3]))) {
			player.sendMessage("§7Please enter valid block data values");
		} else if (selection.isValid()) {
			Material oldBlockMaterial = EvilBook.getBlockMaterial(args[0]);
			Byte oldBlockData = args.length == 4 ? Byte.parseByte(args[1]) : 0;
			Material newBlockMaterial = EvilBook.getBlockMaterial(args.length == 2 ? args[1] : args[2]);
			Byte newBlockData = args.length == 4 ? Byte.parseByte(args[3]) : 0;
			if (oldBlockMaterial == null || newBlockMaterial == null) {
				player.sendMessage("§7Please enter valid block names or IDs");
			} else if (Session.isBlocked(newBlockMaterial)) {
				player.sendMessage("§cThis block is banned in EvilEdit");
			} else {
				Random randomizer = new Random();
				EvilEditEngine engine = CraftEvilEditEngine.createEngine(plugin, selection.getWorld(), player);
				for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
				{
					for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
					{
						for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
						{
							if (randomizer.nextBoolean()) {
								if (selection.getBlock(x, y, z).getType() == oldBlockMaterial && (args.length == 2 || selection.getBlock(x, y, z).getData() == oldBlockData)) {
									engine.setBlock(x, y, z, newBlockMaterial.getId(), newBlockData);
								}
							}
						}
					}
				}
				engine.notifyClients(Statistic.BlocksPlaced);
				player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks randomly replaced");
			}
		}
	}

	public static void randomFillArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 1 && args.length != 2) {
			player.sendMessage("§5Incorrect command usage");
			player.sendMessage("§d/rfill [blockID / blockName]");
			player.sendMessage("§d/rfill [blockID / blockName] [blockData]");
		} else if (args.length == 2 && !EvilBook.isByte(args[1])) {
			player.sendMessage("§7Please enter a valid block data value");
		} else if (selection.isValid()) {
			Material blockMaterial = EvilBook.getBlockMaterial(args[0]);
			if (blockMaterial == null) {
				player.sendMessage("§7Please enter a valid block name or ID");
			} else if (Session.isBlocked(blockMaterial)) {
				player.sendMessage("§cThis block is banned in EvilEdit");
			} else {
				Random randomizer = new Random();
				Byte blockData = args.length == 2 ? Byte.parseByte(args[1]) : 0;
				EvilEditEngine engine = CraftEvilEditEngine.createEngine(plugin, selection.getWorld(), player);
				for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
				{
					for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
					{
						for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
						{
							if (randomizer.nextBoolean()) engine.setBlock(x, y, z, blockMaterial.getId(), blockData);
						}
					}
				}
				engine.notifyClients(Statistic.BlocksPlaced);
				player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks randomly filled");
			}
		}
	}
	
	public static void fillArea(EvilBook plugin, Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 1 && args.length != 2) {
			player.sendMessage("§5Incorrect command usage");
			player.sendMessage("§d/fill [blockID / blockName]");
			player.sendMessage("§d/fill [blockID / blockName] [blockData]");
		} else if (args.length == 2 && !EvilBook.isByte(args[1])) {
			player.sendMessage("§7Please enter a valid block data value");
		} else if (selection.isValid()) {
			Material blockMaterial = EvilBook.getBlockMaterial(args[0]);
			if (blockMaterial == null) {
				player.sendMessage("§7Please enter a valid block name or ID");
			} else if (Session.isBlocked(blockMaterial)) {
				player.sendMessage("§cThis block is banned in EvilEdit");
			} else {
				Byte blockData = args.length == 2 ? Byte.parseByte(args[1]) : 0;
				EvilEditEngine engine = CraftEvilEditEngine.createEngine(plugin, selection.getWorld(), player);
				for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
				{
					for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
					{
						for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
						{
							engine.setBlock(x, y, z, blockMaterial.getId(), blockData);
						}
					}
				}
				engine.notifyClients(Statistic.BlocksPlaced);
				player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks filled");
			}
		}
	}
	
	public static void setBiome(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 1) {
			player.sendMessage("§5Incorrect command usage");
			player.sendMessage("§d/setbiome [biome]");
		} else if (selection.isValid()) {
			Biome blockBiome = EvilBook.getBiome(args[0]);
			if (blockBiome == null) {
				player.sendMessage("§7A biome with this name doesn't exist");
			} else {
				((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.clearUndo();
				for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
				{
					for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
					{
						for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
						{
							((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.appendUndo(selection.getBlock(x, y, z).getState());
							selection.getWorld().setBiome(x, z, blockBiome);
						}
					}
				}
				for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x += 16)
				{
					for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z += 16)
					{
						selection.getWorld().refreshChunk(selection.getBlock(x, 0, z).getChunk().getX(), selection.getBlock(x, 0, z).getChunk().getZ());
					}
				}
				player.sendMessage("§7Selection of " + selection.getVolume() + " blocks set to " + args[0] + " biome");
			}
		}
	}
	
	public static void regenerateChunk(Player player) {
		if (EvilBook.isInSurvival(player)) {
			player.sendMessage("§7EvilEdit can't be used in survival");
		} else {
			BlockState[] logBlockStates = new BlockState[74273];
			((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.clearUndo();
			int i = 0;
			for (int x = 0; x <= 16; x++)
			{
				for (int z = 0; z <= 16; z++)
				{
					for (int y = 0; y <= 256; y++)
					{
						int realX = ((player.getLocation().getBlockX() / 16) * 16) + x;
						int realZ = ((player.getLocation().getBlockZ() / 16) * 16) + z;
						((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.appendUndo(player.getWorld().getBlockAt(realX, y, realZ).getState());
						logBlockStates[i++] = player.getWorld().getBlockAt(realX, y, realZ).getState();
					}
				}
			}
			if (player.getWorld().regenerateChunk(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ())) {
				player.sendMessage("§7Selection of 65536 blocks regenerated");
				i = 0;
				for (int x = 0; x <= 16; x++)
				{
					for (int z = 0; z <= 16; z++)
					{
						for (int y = 0; y <= 256; y++)
						{
							int realX = ((player.getLocation().getBlockX() / 16) * 16) + x;
							int realZ = ((player.getLocation().getBlockZ() / 16) * 16) + z;
							EvilBook.lbConsumer.queueBlockReplace(player.getName(), logBlockStates[i++], player.getWorld().getBlockAt(realX, y, realZ).getState());
						}
					}
				}
			} else {
				player.sendMessage("§7Failed to regenerate selection");
			}
		}
	}
	
	public static void hollowArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 0) {
			player.sendMessage("§5Incorrect command usage");
			player.sendMessage("§d/hollow");
		} else if (selection.isValid()) {
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(plugin, selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
					{
						if (x != selection.getBottomXBlock() && x != selection.getTopXBlock() && z != selection.getBottomZBlock() && z != selection.getTopZBlock() && y != selection.getBottomYBlock() && y != selection.getTopYBlock()) {
							engine.setBlock(x, y, z, Material.AIR.getId(), (byte) 0);
						}
					}
				}
			}
			engine.notifyClients(Statistic.BlocksBroken);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks hollowed");
		}
	}

	public static void outlineArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 1 && args.length != 2) {
			player.sendMessage("§5Incorrect command usage");
			player.sendMessage("§d/outline [blockID / blockName]");
			player.sendMessage("§d/outline [blockID / blockName] [blockData]");
		} else if (args.length == 2 && !EvilBook.isByte(args[1])) {
			player.sendMessage("§7Please enter a valid block data value");
		} else if (selection.isValid()) {
			Material blockMaterial = EvilBook.getBlockMaterial(args[0]);
			if (blockMaterial == null) {
				player.sendMessage("§7Please enter a valid block name or ID");
			} else if (Session.isBlocked(blockMaterial)) {
				player.sendMessage("§cThis block is banned in EvilEdit");
			} else {
				Byte blockData = args.length == 2 ? Byte.parseByte(args[1]) : 0;
				EvilEditEngine engine = CraftEvilEditEngine.createEngine(plugin, selection.getWorld(), player);
				for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
				{
					for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
					{
						for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
						{
							if (x == selection.getBottomXBlock() || x == selection.getTopXBlock() || z == selection.getBottomZBlock() || z == selection.getTopZBlock() || y == selection.getBottomYBlock() || y == selection.getTopYBlock()) {
								engine.setBlock(x, y, z, blockMaterial.getId(), blockData);
							}
						}
					}
				}
				engine.notifyClients(Statistic.BlocksPlaced);
				player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks outlined");
			}
		}
	}

	public static void wallArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 1 && args.length != 2) {
			player.sendMessage("§5Incorrect command usage");
			player.sendMessage("§d/walls [blockID / blockName]");
			player.sendMessage("§d/walls [blockID / blockName] [blockData]");
		} else if (args.length == 2 && !EvilBook.isByte(args[1])) {
			player.sendMessage("§7Please enter a valid block data value");
		} else if (selection.isValid()) {
			Material blockMaterial = EvilBook.getBlockMaterial(args[0]);
			if (blockMaterial == null) {
				player.sendMessage("§7Please enter a valid block name");
			} else if (Session.isBlocked(blockMaterial)) {
				player.sendMessage("§cThis block is banned in EvilEdit");
			} else {
				Byte blockData = args.length == 2 ? Byte.parseByte(args[1]) : 0;
				EvilEditEngine engine = CraftEvilEditEngine.createEngine(plugin, selection.getWorld(), player);
				for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
				{
					for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
					{
						for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
						{
							if (x == selection.getBottomXBlock() || x == selection.getTopXBlock() || z == selection.getBottomZBlock() || z == selection.getTopZBlock()) {
								engine.setBlock(x, y, z, blockMaterial.getId(), blockData);
							}
						}
					}
				}
				engine.notifyClients(Statistic.BlocksPlaced);
				player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks walled");
			}
		}
	}

	public static void overlayArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 1 && args.length != 2) {
			player.sendMessage("§5Incorrect command usage");
			player.sendMessage("§d/overlay [blockID / blockName]");
			player.sendMessage("§d/overlay [blockID / blockName] [blockData]");
		} else if (args.length == 2 && !EvilBook.isByte(args[1])) {
			player.sendMessage("§7Please enter a valid block data value");
		} else if (selection.isValid()) {
			Material blockMaterial = EvilBook.getBlockMaterial(args[0]);
			if (blockMaterial == null) {
				player.sendMessage("§7Please enter a valid block name");
			} else if (Session.isBlocked(blockMaterial)) {
				player.sendMessage("§cThis block is banned in EvilEdit");
			} else {
				Byte blockData = args.length == 2 ? Byte.parseByte(args[1]) : 0;
				EvilEditEngine engine = CraftEvilEditEngine.createEngine(plugin, selection.getWorld(), player);
				for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
				{
					for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
					{
						int highestY = player.getWorld().getHighestBlockYAt(x, z);
						engine.setBlock(x, highestY, z, blockMaterial.getId(), blockData);
					}
				}
				engine.notifyClients(Statistic.BlocksPlaced);
				player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks overlayed");
			}
		}
	}
	
	public static void replaceArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 2 && args.length != 4) {
			player.sendMessage("§5Incorrect command usage");
			player.sendMessage("§d/replace [blockID / blockName] [blockID / blockName]");
			player.sendMessage("§d/replace [blockID / blockName] [blockData] [blockID / blockName] [blockData]");
		} else if (args.length == 4 && (!EvilBook.isByte(args[1]) || !EvilBook.isByte(args[3]))) {
			player.sendMessage("§7Please enter valid block data values");
		} else if (selection.isValid()) {
			Material oldBlockMaterial = EvilBook.getBlockMaterial(args[0]);
			Byte oldBlockData = args.length == 4 ? Byte.parseByte(args[1]) : 0;
			Material newBlockMaterial = EvilBook.getBlockMaterial(args.length == 2 ? args[1] : args[2]);
			Byte newBlockData = args.length == 4 ? Byte.parseByte(args[3]) : 0;
			if (oldBlockMaterial == null || newBlockMaterial == null) {
				player.sendMessage("§7Please enter valid block names or IDs");
			} else if (Session.isBlocked(newBlockMaterial)) {
				player.sendMessage("§cThis block is banned in EvilEdit");
			} else {
				EvilEditEngine engine = CraftEvilEditEngine.createEngine(plugin, selection.getWorld(), player);
				for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
				{
					for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
					{
						for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
						{
							if (selection.getBlock(x, y, z).getType() == oldBlockMaterial && (args.length == 2 || selection.getBlock(x, y, z).getData() == oldBlockData)) {
								engine.setBlock(x, y, z, newBlockMaterial.getId(), newBlockData);
							}
						}
					}
				}
				engine.notifyClients(Statistic.BlocksPlaced);
				player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks replaced");
			}
		}
	}
	
	public static void count(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (selection.isValid()) {
			if (args.length == 0 || args.length == 1 || args.length == 2) {
				int blockCount = 0;
				Material blockMaterial = null;
				if (args.length != 0) {
					blockMaterial = EvilBook.getBlockMaterial(args[0]);
					if (blockMaterial == null) {
						player.sendMessage("§7Please enter a valid block name or ID");
						return;
					}
				}
				Byte blockData = 0;
				if (args.length == 2) {
					if (!EvilBook.isByte(args[1])) {
						player.sendMessage("§7Please enter a valid block data value");
						return;
					}
					blockData = Byte.valueOf(args[1]);
				}
				for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
				{
					for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
					{
						for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
						{
							if ((args.length == 0 && selection.getBlock(x, y, z).getType() != Material.AIR) || (args.length == 1 && selection.getBlock(x, y, z).getType() == blockMaterial) || (args.length == 2 && selection.getBlock(x, y, z).getType() == blockMaterial && selection.getBlock(x, y, z).getData() == blockData)) blockCount++;
						}
					}
				}
				if (args.length == 0) {
					player.sendMessage("§7Selection contains " + blockCount + " blocks");
				} else {
					player.sendMessage("§7Selection contains " + blockCount + " " + EvilBook.getFriendlyName(blockMaterial) + " blocks");
				}
			} else {
				player.sendMessage("§5Incorrect command usage");
				player.sendMessage("§d/count");
				player.sendMessage("§d/count [blockID / blockName]");
				player.sendMessage("§d/count [blockID / blockName] [blockData]");
			}
		}
	}
	
	public static void size(Player player) {
		Selection selection = new Selection(player);
		if (selection.isValid()) player.sendMessage("§7Selection size of " + (selection.getTopXBlock() + 1 - selection.getBottomXBlock()) + "x" + (selection.getTopYBlock() + 1 - selection.getBottomYBlock()) + "x" + (selection.getTopZBlock() + 1 - selection.getBottomZBlock()) + " blocks");
	}
	
	public static void deselectEditLocations(Player player) {
		EvilBook.getProfile(player).actionLocationA = null;
		EvilBook.getProfile(player).actionLocationB = null;
		player.sendMessage("§7You have deselected your EvilEdit selection");
	}
	
	public static void toggleEditWand(Player player) {
		EvilBook.getProfile(player).wandMode = EvilBook.getProfile(player).wandMode == EditWandMode.None ? EditWandMode.Selection : EditWandMode.None;
		player.sendMessage("§7Edit wand " + (EvilBook.getProfile(player).wandMode == EditWandMode.None ? "disabled" : "in selection mode"));
	}

	public static void spawnEditWand(Player player) {
		if (EvilBook.isInSurvival(player)) {
			player.sendMessage("§7You can't spawn an EvilEdit wand in survival");
		} else {
			player.getInventory().addItem(new ItemStack(Material.GOLD_SPADE));
			EvilBook.getProfile(player).wandMode = EditWandMode.Selection;
			player.sendMessage("§7You haved spawned an EvilEdit wand");
		}
	}
}
