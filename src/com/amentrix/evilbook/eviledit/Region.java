package com.amentrix.evilbook.eviledit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.amentrix.evilbook.eviledit.utils.BlockType;
import com.amentrix.evilbook.eviledit.utils.CraftEvilEditEngine;
import com.amentrix.evilbook.eviledit.utils.EditWandMode;
import com.amentrix.evilbook.eviledit.utils.EvilEditEngine;
import com.amentrix.evilbook.eviledit.utils.Selection;
import com.amentrix.evilbook.main.ChatExtensions;
import com.amentrix.evilbook.main.DynamicSign;
import com.amentrix.evilbook.main.Emitter;
import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.PlayerProfileAdmin;
import com.amentrix.evilbook.main.Rank;
import com.amentrix.evilbook.minigame.MinigameType;
import com.amentrix.evilbook.reference.BiomeReference;
import com.amentrix.evilbook.reference.BlockReference;
import com.amentrix.evilbook.statistics.GlobalStatistic;

import net.minecraft.server.v1_8_R3.ChatClickable.EnumClickAction;

/**
 * EvilEdit region methods
 * @author Reece Aaron Lecrivain
 */
class Region {
	static void deforestArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 0) {
			ChatExtensions.sendCommandHelpMessage(player, "/deforest");
		} else if (selection.isValid()) {
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getTopYBlock(); y >= selection.getBottomYBlock(); y--)
					{
						Material type = selection.getBlock(x, y, z).getType();
						if (type == Material.LOG || type == Material.LOG_2 || type == Material.LEAVES || type == Material.LEAVES_2) {
							engine.setBlock(x, y, z, Material.AIR.getId(), (byte) 0);
						}
					}
				}
			}
			engine.notifyClients(GlobalStatistic.BlocksBroken);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks deforested");
		}
	}
	
	static void flipArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 1 || (!args[0].equalsIgnoreCase("x") && !args[0].equalsIgnoreCase("y") && !args[0].equalsIgnoreCase("z"))) {
			ChatExtensions.sendCommandHelpMessage(player, 
					Arrays.asList("/flip x",
							"/flip y",
							"/flip z"));
		} else if (selection.isValid()) {
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			List<BlockState> blockList = new ArrayList<>();
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
					{
						blockList.add(selection.getBlock(x, y, z).getState());
					}
				}
			}
			for (BlockState block : blockList) {
				if (args[0].equalsIgnoreCase("x")) engine.setBlock(selection.getBottomXBlock() + (selection.getTopXBlock() - block.getX()), block.getY(), block.getZ(), block.getTypeId(), block.getRawData());
				else if (args[0].equalsIgnoreCase("y")) engine.setBlock(block.getX(), selection.getBottomYBlock() + (selection.getTopYBlock() - block.getY()), block.getZ(), block.getTypeId(), block.getRawData());
				else engine.setBlock(block.getX(), block.getY(), selection.getBottomZBlock() + (selection.getTopZBlock() - block.getZ()), block.getTypeId(), block.getRawData());
			}
			engine.notifyClients(GlobalStatistic.BlocksPlaced);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks flipped on the " + args[0] + " axis");
		}
	}
	
	static void moveArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 2 || !EvilBook.isInteger(args[0]) || (!args[1].equalsIgnoreCase("x") && !args[1].equalsIgnoreCase("y") && !args[1].equalsIgnoreCase("z"))) {
			ChatExtensions.sendCommandHelpMessage(player, 
					Arrays.asList("/move [count] x",
							"/move [count] y",
							"/move [count] z"));
		} else if (selection.isValid()) {
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			List<BlockState> blockList = new ArrayList<>();
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
					{
						blockList.add(selection.getBlock(x, y, z).getState());
					}
				}
			}
			int count = Integer.parseInt(args[0]);
			List<Location> newLocationList = new ArrayList<>();
			for (BlockState block : blockList) {
				Boolean hasBeenChanged = false;
				for (Location loc : newLocationList) {
					if (loc.getBlockX() == block.getX() && loc.getBlockY() == block.getY() && loc.getBlockZ() == block.getZ()) hasBeenChanged = true;
				}
				if (!hasBeenChanged) engine.setBlock(block.getX(), block.getY(), block.getZ(), 0, 0);
				if (args[1].equalsIgnoreCase("x")) {
					engine.setBlock(block.getX() + count, block.getY(), block.getZ(), block.getTypeId(), block.getRawData());
					newLocationList.add(new Location(selection.getWorld(), block.getX() + count, block.getY(), block.getZ()));
				} else if (args[1].equalsIgnoreCase("y")) {
					engine.setBlock(block.getX(), block.getY() + count, block.getZ(), block.getTypeId(), block.getRawData());
					newLocationList.add(new Location(selection.getWorld(), block.getX(), block.getY() + count, block.getZ()));
				} else {
					engine.setBlock(block.getX(), block.getY(), block.getZ() + count, block.getTypeId(), block.getRawData());
					newLocationList.add(new Location(selection.getWorld(), block.getX(), block.getY(), block.getZ() + count));
				}
			}
			engine.notifyClients(GlobalStatistic.BlocksPlaced);
			if (args[1].equalsIgnoreCase("x")) {
				EvilBook.getProfile(player).actionLocationA.add(count, 0, 0);
				EvilBook.getProfile(player).actionLocationB.add(count, 0, 0);
			} else if (args[1].equalsIgnoreCase("y")) {
				EvilBook.getProfile(player).actionLocationA.add(0, count, 0);
				EvilBook.getProfile(player).actionLocationB.add(0, count, 0);
			} else {
				EvilBook.getProfile(player).actionLocationA.add(0, 0, count);
				EvilBook.getProfile(player).actionLocationB.add(0, 0, count);
			}
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks moved on the " + args[1] + " axis");
		}
	}
	
	static void undoEdit(Player player) {
		if (((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getUndo().size() == 0) {
			player.sendMessage("§7You have no EvilEdit actions to undo");
		} else {
			EvilEditEngine engine = CraftEvilEditEngine.createEngineSilent(((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getUndo().get(0).getWorld(), player);
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
				EvilBook.dynamicSignList.get(dynamicSign.location.getWorld()).add(dynamicSign);
			}
			((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.undoDynamicSignList = new ArrayList<>();
			// Undo emitter removals
			for (Emitter emitter : ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.undoEmitterList) {
				emitter.save();
				EvilBook.emitterList.add(emitter);
			}
			((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.undoEmitterList = new ArrayList<>();
			//
			engine.notifyClients(GlobalStatistic.BlocksPlaced);
			player.sendMessage("§7Undone " + engine.getBlocksChanged() + " block edit");
		}
	}
	
	static void drainArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 0) {
			ChatExtensions.sendCommandHelpMessage(player, "/drain");
		} else if (selection.isValid()) {
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
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
			engine.notifyClients(GlobalStatistic.BlocksBroken);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks drained");
		}
	}	
	
	static void greenArea(Player player) {
		Selection selection = new Selection(player);
		if (selection.isValid()) {
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
					{
						if (selection.getBlock(x, y, z).getType() == Material.DIRT) {
							engine.setBlock(x, y, z, Material.GRASS.getId(), 0);
						}
					}
				}
			}
			engine.notifyClients(GlobalStatistic.BlocksPlaced);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks greened");
		}
	}	

	static void paste(final Player player, String[] args) {
		if (EvilBook.isInSurvival(player) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7EvilEdit can't be used in survival");
		} else if (EvilBook.isInMinigame(player, MinigameType.SKYBLOCK) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7EvilEdit can't be used in skyblock survival");
		} else if (args.length != 0) {
			ChatExtensions.sendCommandHelpMessage(player, "/paste");
		} else if (((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopy().size() == 0) {
			player.sendMessage("§7Please copy an area of blocks before attempting paste");
		} else {
			if (((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopySize() <= 200000) {
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
				EvilEditEngine engine = CraftEvilEditEngine.createEngine(player.getWorld(), player);
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
					EvilBook.dynamicSignList.get(player.getWorld()).add(newDynamicSign);
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
				engine.notifyClients(GlobalStatistic.BlocksPlaced);
				player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks pasted");
			} else {
				player.sendMessage("§7Using EvilEdit angel, your edit should take " + (int)Math.floor((double)((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopySize() / 219520) + " seconds");
				
				int bottomBlockX = ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopy().get(0).getLocation().getBlockX();
				int bottomBlockY = ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopy().get(0).getLocation().getBlockY();
				int bottomBlockZ = ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopy().get(0).getLocation().getBlockZ();
				for (BlockState block : ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopy()) {
					if (block.getLocation().getBlockX() < bottomBlockX) bottomBlockX = block.getLocation().getBlockX();
					if (block.getLocation().getBlockY() < bottomBlockY) bottomBlockY = block.getLocation().getBlockY();
					if (block.getLocation().getBlockZ() < bottomBlockZ) bottomBlockZ = block.getLocation().getBlockZ();
				}
				final int bottomBlockX2 = bottomBlockX;
				final int bottomBlockZ2 = bottomBlockZ;
				final int bottomBlockY2 = bottomBlockY;
				final EvilEditEngine engine = CraftEvilEditEngine.createEngine(player.getWorld(), player);

				int count = 0;
				for (int index = 0; index <= ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopySize(); index += 21952)
				{
					final int i = index;
					count++;
					Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("EvilBook"), new Runnable() {
						@Override
						public void run() {
							int indexMax = Math.min(i + 21952, ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopySize());
							for (int subIndex = i; subIndex < indexMax; subIndex++) {
								BlockState block = ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.getCopy().get(subIndex);
								Location loc = new Location(player.getWorld(), 
										player.getLocation().getBlockX() + (block.getLocation().getBlockX() - bottomBlockX2), 
										player.getLocation().getBlockY() + (block.getLocation().getBlockY() - bottomBlockY2), 
										player.getLocation().getBlockZ() + (block.getLocation().getBlockZ() - bottomBlockZ2));
								// Set the block material and data which includes direction
								engine.setBlock(loc, block.getType().getId(), block.getData().getData());
								// Handle blocks with special states
								Session.setState(block, loc.getBlock().getState());
							}
						}
					}, count * 1L);
				}
				player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks pasted");
			}
		}
	}

	static void copy(final Player player, String[] args) {
		final Selection selection = new Selection(player);
		if (args.length != 0) {
			ChatExtensions.sendCommandHelpMessage(player, "/copy");
		} else if (selection.isValid()) {
			((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.clearCopy();
			if (selection.getVolume() <= 200000) {
				for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
				{
					for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
					{
						for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
						{
							// Append the block to the copy list
							((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.appendCopy(selection.getBlock(x, y, z).getState());
							// Dynamic signs
							for (DynamicSign dynamicSign : EvilBook.dynamicSignList.get(selection.getWorld())) {
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
			} else if (selection.getVolume() <= 2500000) {
				player.sendMessage("§7Using EvilEdit angel, your edit should take " + (int)Math.floor((double)selection.getVolume() / 219520) + " seconds");
				int count = 0;
				for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x += 28)
				{
					final int xf = x;
					for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z += 28)
					{
						final int zf = z;
						for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y += 28)
						{
							final int yf = y;
							count++;
							Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("EvilBook"), new Runnable() {
								@Override
								public void run() {
									int xmax = Math.min(xf + 28, selection.getTopXBlock());
									int zmax = Math.min(zf + 28, selection.getTopZBlock());
									int ymax = Math.min(yf + 28, selection.getTopYBlock());
									for (int x2 = xf; x2 <= xmax; x2++) {
										for (int z2 = zf; z2 <= zmax; z2++) {
											for (int y2 = yf; y2 <= ymax; y2++) {
												// Append the block to the copy list
												((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.appendCopy(selection.getBlock(x2, y2, z2).getState());
											}
										}
									}
								}
							}, count * 1L);
						}
					}
				}
			} else {
				player.sendMessage("§7Selection is too large to be copied");
			}
			player.sendMessage("§7Selection of " + selection.getVolume() + " blocks copied");
		}
	}
	
	static void cut(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 0) {
			ChatExtensions.sendCommandHelpMessage(player, "/cut");
		} else if (selection.isValid()) {
			((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.clearCopy();
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
					{
						// Append the block to the copy list
						((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.appendCopy(selection.getBlock(x, y, z).getState());
						// Dynamic signs
						for (DynamicSign dynamicSign : EvilBook.dynamicSignList.get(selection.getWorld())) {
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
						// Delete the old block
						engine.setBlock(x, y, z, Material.AIR.getId(), (byte) 0);
					}
				}
			}
			engine.notifyClients(GlobalStatistic.BlocksBroken);
			player.sendMessage("§7Selection of " + selection.getVolume() + " blocks cut");
		}
	}

	static void randomDeleteArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length > 2) {
			ChatExtensions.sendCommandHelpMessage(player, 
					Arrays.asList("/rdel",
							"/rdel [blockID / blockName]",
							"/rdel [blockID / blockName] [blockData]"));
		} else if (selection.isValid()) {
			BlockType actionBlock = null;
			if (args.length >= 1) {
				actionBlock = new BlockType(args[0]);
				if (args.length == 2) actionBlock.setData(args[1]);
				if (!actionBlock.isValid(player)) return;
			}
			Random randomizer = new Random();
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
					{
						if (randomizer.nextBoolean() && (args.length == 0 || (args.length == 1 && selection.getBlock(x, y, z).getType() == actionBlock.getMaterial()) || (args.length == 2 && selection.getBlock(x, y, z).getType() == actionBlock.getMaterial() && selection.getBlock(x, y, z).getData() == actionBlock.getData()))) {
							engine.setBlock(x, y, z, Material.AIR.getId(), (byte) 0);
						}
					}
				}
			}
			engine.notifyClients(GlobalStatistic.BlocksBroken);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks randomly deleted");
		}
	}

	static void deleteArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length > 2) {
			ChatExtensions.sendCommandHelpMessage(player, 
					Arrays.asList("/del",
							"/del [blockID / blockName]",
							"/del [blockID / blockName] [blockData]"));
		} else if (selection.isValid()) {
			BlockType actionBlock = null;
			if (args.length >= 1) {
				actionBlock = new BlockType(args[0]);
				if (args.length == 2) actionBlock.setData(args[1]);
				if (!actionBlock.isValid(player)) return;
			}
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getTopYBlock(); y >= selection.getBottomYBlock(); y--)
					{
						if (args.length == 0 || (args.length == 1 && selection.getBlock(x, y, z).getType() == actionBlock.getMaterial()) || (args.length == 2 && selection.getBlock(x, y, z).getType() == actionBlock.getMaterial() && selection.getBlock(x, y, z).getData() == actionBlock.getData())) {
							engine.setBlock(x, y, z, Material.AIR.getId(), (byte) 0);
						}
					}
				}
			}
			engine.notifyClients(GlobalStatistic.BlocksBroken);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks deleted");
		}
	}

	static void randomReplaceArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 2 && args.length != 4) {
			ChatExtensions.sendCommandHelpMessage(player, 
					Arrays.asList("/rreplace [blockID / blockName] [blockID / blockName]",
							"/rreplace [blockID / blockName] [blockData] [blockID / blockName] [blockData]"));
		} else if (selection.isValid()) {
			BlockType oldBlock = new BlockType(args[0]);
			if (args.length == 4) oldBlock.setData(args[1]);
			if (!oldBlock.isValid(player)) return;	
			BlockType newBlock = new BlockType(args.length == 2 ? args[1] : args[2]);
			if (args.length == 4) newBlock.setData(args[3]);
			if (!newBlock.isValid(player)) return;	
			Random randomizer = new Random();
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
					{
						if (randomizer.nextBoolean()) {
							if (selection.getBlock(x, y, z).getType() == oldBlock.getMaterial() && (args.length == 2 || selection.getBlock(x, y, z).getData() == oldBlock.getData())) {
								engine.setBlock(x, y, z, newBlock.getMaterial().getId(), newBlock.getData());
							}
						}
					}
				}
			}
			engine.notifyClients(GlobalStatistic.BlocksPlaced);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks randomly replaced");
		}
	}

	static void randomFillArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 1 && args.length != 2) {
			ChatExtensions.sendCommandHelpMessage(player, 
					Arrays.asList("/rfill [blockID / blockName]",
							"/rfill [blockID / blockName] [blockData]"));
		} else if (selection.isValid()) {
			BlockType actionBlock = null;
			if (args.length >= 1) {
				actionBlock = new BlockType(args[0]);
				if (args.length == 2) actionBlock.setData(args[1]);
				if (!actionBlock.isValid(player)) return;
			}
			Random randomizer = new Random();
			Byte blockData = args.length == 2 ? Byte.parseByte(args[1]) : 0;
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
					{
						if (randomizer.nextBoolean()) engine.setBlock(x, y, z, actionBlock.getMaterial().getId(), blockData);
					}
				}
			}
			engine.notifyClients(GlobalStatistic.BlocksPlaced);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks randomly filled");
		}
	}
	
	static void fillArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 1 && args.length != 2) {
			ChatExtensions.sendCommandHelpMessage(player, 
					Arrays.asList("/fill [blockID / blockName]",
							"/fill [blockID / blockName] [blockData]"));
		} else if (selection.isValid()) {
			BlockType actionBlock = null;
			if (args.length >= 1) {
				actionBlock = new BlockType(args[0]);
				if (args.length == 2) actionBlock.setData(args[1]);
				if (!actionBlock.isValid(player)) return;
			}
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
					{
						engine.setBlock(x, y, z, actionBlock.getMaterial().getId(), actionBlock.getData());
					}
				}
			}
			engine.notifyClients(GlobalStatistic.BlocksPlaced);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks filled");
		}
	}

	static void setBiome(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 1) {
			ChatExtensions.sendCommandHelpMessage(player, "/setbiome [biome]");
		} else if (selection.isValid()) {
			Biome blockBiome = BiomeReference.getBiome(args[0]);
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
	
	static void regenerateChunk(Player player) {
		if (EvilBook.isInSurvival(player) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7EvilEdit can't be used in survival");
		} else if (EvilBook.isInMinigame(player, MinigameType.SKYBLOCK) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7EvilEdit can't be used in skyblock survival");
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
	
	static void hollowArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 0) {
			ChatExtensions.sendCommandHelpMessage(player, "/hollow");
		} else if (selection.isValid()) {
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
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
			engine.notifyClients(GlobalStatistic.BlocksBroken);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks hollowed");
		}
	}

	static void outlineArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 1 && args.length != 2) {
			ChatExtensions.sendCommandHelpMessage(player, 
					Arrays.asList("/outline [blockID / blockName]",
							"/outline [blockID / blockName] [blockData]"));
		} else if (selection.isValid()) {
			BlockType actionBlock = null;
			if (args.length >= 1) {
				actionBlock = new BlockType(args[0]);
				if (args.length == 2) actionBlock.setData(args[1]);
				if (!actionBlock.isValid(player)) return;
			}
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
					{
						if (x == selection.getBottomXBlock() || x == selection.getTopXBlock() || z == selection.getBottomZBlock() || z == selection.getTopZBlock() || y == selection.getBottomYBlock() || y == selection.getTopYBlock()) {
							engine.setBlock(x, y, z, actionBlock.getMaterial().getId(), actionBlock.getData());
						}
					}
				}
			}
			engine.notifyClients(GlobalStatistic.BlocksPlaced);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks outlined");
		}
	}

	static void wallArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 1 && args.length != 2) {
			ChatExtensions.sendCommandHelpMessage(player, 
					Arrays.asList("/walls [blockID / blockName]",
							"/walls [blockID / blockName] [blockData]"));
		} else if (selection.isValid()) {
			BlockType actionBlock = null;
			if (args.length >= 1) {
				actionBlock = new BlockType(args[0]);
				if (args.length == 2) actionBlock.setData(args[1]);
				if (!actionBlock.isValid(player)) return;
			}
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
					{
						if (x == selection.getBottomXBlock() || x == selection.getTopXBlock() || z == selection.getBottomZBlock() || z == selection.getTopZBlock()) {
							engine.setBlock(x, y, z, actionBlock.getMaterial().getId(), actionBlock.getData());
						}
					}
				}
			}
			engine.notifyClients(GlobalStatistic.BlocksPlaced);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks walled");
		}
	}

	static void thawSnowArea(Player player) {
		Selection selection = new Selection(player);
		if (selection.isValid()) {
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
					{
						if (selection.getBlock(x, y, z).getType() == Material.SNOW) engine.setBlock(x, y, z, 0, 0);
					}
				}
			}
			engine.notifyClients(GlobalStatistic.BlocksBroken);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks thawed");
		}
	}
	
	static void overlaySnowArea(Player player) {
		Selection selection = new Selection(player);
		if (selection.isValid()) {
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					int highestY = player.getWorld().getHighestBlockYAt(x, z);
					if (highestY != 0) engine.setBlock(x, highestY, z, Material.SNOW.getId(), 0);
				}
			}
			engine.notifyClients(GlobalStatistic.BlocksPlaced);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks simulated snow cover");
		}
	}
	
	static void overlayArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 1 && args.length != 2) {
			ChatExtensions.sendCommandHelpMessage(player, 
					Arrays.asList("/overlay [blockID / blockName]",
							"/overlay [blockID / blockName] [blockData]"));
		} else if (selection.isValid()) {
			BlockType actionBlock = null;
			if (args.length >= 1) {
				actionBlock = new BlockType(args[0]);
				if (args.length == 2) actionBlock.setData(args[1]);
				if (!actionBlock.isValid(player)) return;
			}
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					int highestY = player.getWorld().getHighestBlockYAt(x, z);
					if (highestY != 0) engine.setBlock(x, highestY, z, actionBlock.getMaterial().getId(), actionBlock.getData());
				}
			}
			engine.notifyClients(GlobalStatistic.BlocksPlaced);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks overlayed");
		}
	}
	
	static void replaceArea(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 2 && args.length != 4) {
			ChatExtensions.sendCommandHelpMessage(player, 
					Arrays.asList("/replace [blockID / blockName] [blockID / blockName]",
							"/replace [blockID / blockName] [blockData] [blockID / blockName] [blockData]"));
		} else if (selection.isValid()) {
			BlockType oldBlock = new BlockType(args[0]);
			if (args.length == 4) oldBlock.setData(args[1]);
			if (!oldBlock.isValid(player)) return;	
			BlockType newBlock = new BlockType(args.length == 2 ? args[1] : args[2]);
			if (args.length == 4) newBlock.setData(args[3]);
			if (!newBlock.isValid(player)) return;
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
			{
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
				{
					for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
					{
						if (selection.getBlock(x, y, z).getType() == oldBlock.getMaterial() && (args.length == 2 || selection.getBlock(x, y, z).getData() == oldBlock.getData())) {
							engine.setBlock(x, y, z, newBlock.getMaterial().getId(), newBlock.getData());
						}
					}
				}
			}
			engine.notifyClients(GlobalStatistic.BlocksPlaced);
			player.sendMessage("§7Selection of " + engine.getBlocksChanged() + " blocks replaced");
		}
	}

	static void count(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (selection.isValid()) {
			if (args.length == 0 || args.length == 1 || args.length == 2) {
				int blockCount = 0;
				BlockType actionBlock = null;
				if (args.length >= 1) {
					actionBlock = new BlockType(args[0]);
					if (args.length == 2) actionBlock.setData(args[1]);
					if (!actionBlock.isValid(player)) return;
				}
				for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++)
				{
					for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++)
					{
						for (int y = selection.getBottomYBlock(); y <= selection.getTopYBlock(); y++)
						{
							if ((args.length == 0 && selection.getBlock(x, y, z).getType() != Material.AIR) || (args.length == 1 && selection.getBlock(x, y, z).getType() == actionBlock.getMaterial()) || (args.length == 2 && selection.getBlock(x, y, z).getType() == actionBlock.getMaterial() && selection.getBlock(x, y, z).getData() == actionBlock.getData())) blockCount++;
						}
					}
				}
				if (args.length == 0) {
					player.sendMessage("§7Selection contains " + blockCount + " blocks");
				} else {
					player.sendMessage("§7Selection contains " + blockCount + " " + BlockReference.getFriendlyName(actionBlock.getMaterial()) + " blocks");
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/count",
								"/count [blockID / blockName]",
								"/count [blockID / blockName] [blockData]"));
			}
		}
	}
	
	static void size(Player player) {
		Selection selection = new Selection(player);
		if (selection.isValid()) player.sendMessage("§7Selection size of " + (selection.getTopXBlock() + 1 - selection.getBottomXBlock()) + "x" + (selection.getTopYBlock() + 1 - selection.getBottomYBlock()) + "x" + (selection.getTopZBlock() + 1 - selection.getBottomZBlock()) + " blocks");
	}
	
	static void deselectEditLocations(Player player) {
		EvilBook.getProfile(player).actionLocationA = null;
		EvilBook.getProfile(player).actionLocationB = null;
		player.sendMessage("§7You have deselected your EvilEdit selection");
	}
	
	static void toggleEditWand(Player player) {
		EvilBook.getProfile(player).wandMode = EvilBook.getProfile(player).wandMode == EditWandMode.None ? EditWandMode.Selection : EditWandMode.None;
		player.sendMessage("§7Edit wand " + (EvilBook.getProfile(player).wandMode == EditWandMode.None ? "disabled" : "in selection mode"));
	}

	static void spawnEditWand(Player player) {
		if (EvilBook.isInSurvival(player) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7You can't spawn an EvilEdit wand in survival");
		} else if (EvilBook.isInMinigame(player, MinigameType.SKYBLOCK) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7You can't spawn an EvilEdit wand in skyblock survival");
		} else {
			player.getInventory().addItem(new ItemStack(Material.GOLD_SPADE));
			EvilBook.getProfile(player).wandMode = EditWandMode.Selection;
			player.sendMessage("§7You haved spawned an EvilEdit wand");
		}
	}
}
