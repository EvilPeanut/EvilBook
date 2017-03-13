package com.amentrix.evilbook.eviledit;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.entity.Player;

import com.amentrix.evilbook.eviledit.utils.BlockType;
import com.amentrix.evilbook.eviledit.utils.CraftEvilEditEngine;
import com.amentrix.evilbook.eviledit.utils.EvilEditEngine;
import com.amentrix.evilbook.eviledit.utils.Selection;
import com.amentrix.evilbook.eviledit.utils.TreeGenerationDelegate;
import com.amentrix.evilbook.main.ChatExtensions;
import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.Rank;
import com.amentrix.evilbook.minigame.MinigameType;
import com.amentrix.evilbook.statistics.GlobalStatistic;
import com.amentrix.evilbook.utils.BlockReference;
import com.amentrix.evilbook.utils.TreeReference;

/**
 * EvilEdit generation methods
 * @author Reece Aaron Lecrivain
 */
class Generation {
	static void createPumpkinForest(Player player) {
		Selection selection = new Selection(player);
		if (selection.isValid()) {
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(selection.getWorld(), player);
			Random positionRandomizer = new Random();
			for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x += 16) {
				for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z += 16) {
					for (int pumpkinNumber = 0; pumpkinNumber <= positionRandomizer.nextInt(9); pumpkinNumber++) {
						int pumpkinX = x + positionRandomizer.nextInt(8) - positionRandomizer.nextInt(8);
						int pumpkinZ = z + positionRandomizer.nextInt(8) - positionRandomizer.nextInt(8);
						int pumpkinY = selection.getWorld().getHighestBlockYAt(pumpkinX, pumpkinZ);
						if (selection.getWorld().getBlockAt(pumpkinX, pumpkinY, pumpkinZ).getType() == Material.AIR 
								&& selection.getWorld().getBlockAt(pumpkinX, pumpkinY - 1, pumpkinZ).getType() == Material.GRASS) {
							engine.setBlock(pumpkinX, pumpkinY, pumpkinZ, Material.PUMPKIN.getId(), (byte)positionRandomizer.nextInt(4));
						}
					}
				}
			}
			engine.notifyClients(GlobalStatistic.BLOCKS_PLACED);
			player.sendMessage("§7Created a " + engine.getBlocksChanged() + " pumpkin forest");
		}
	}

	static void createForest(Player player, String[] args) {
		Selection selection = new Selection(player);
		if (args.length != 1 && args.length != 2) {
			ChatExtensions.sendCommandHelpMessage(player, 
					Arrays.asList("/forest [treeType]",
							"/forest [treeType] [density]"));
		} else if (args.length == 2 && !EvilBook.isDouble(args[1])) {
			player.sendMessage("§7Please enter a valid forest density");
		} else if (selection.isValid()) {
			TreeType treeType = TreeReference.getTreeType(args[0]);
			if (treeType == null) {
				player.sendMessage("§7Please enter a valid tree type");
			} else {
				int trees = 0;
				double density = args.length == 1 ? 0.2 : Double.parseDouble(args[1]);
				TreeGenerationDelegate delegate = new TreeGenerationDelegate(player.getLocation(), player);
				for (int x = selection.getBottomXBlock(); x <= selection.getTopXBlock(); x++) {
					for (int z = selection.getBottomZBlock(); z <= selection.getTopZBlock(); z++) {
						if (Math.random() >= density) continue;
						for (int y = selection.getTopYBlock(); y >= selection.getBottomYBlock(); y--) {
							Material mat = selection.getBlock(x, y, z).getType();
							if (mat == Material.GRASS || mat == Material.DIRT) {
								if (player.getWorld().generateTree(selection.getBlock(x, y, z).getLocation(), treeType, delegate)) trees++;
								break;
							} else if (mat != Material.AIR) {
								break;
							}
						}
					}
				}
				delegate.notifyClients();
				player.sendMessage("§7Created a " + trees + " " + TreeReference.treeTypeList.get(treeType).get(0) + " tree forest");
			}
		}
	}
	
	static void createPyramid(Player player, String[] args, boolean hollow) {
		if (EvilBook.isInSurvival(player) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7EvilEdit can't be used in survival");
		} else if (EvilBook.isInMinigame(player, MinigameType.SKYBLOCK) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
				player.sendMessage("§7EvilEdit can't be used in skyblock survival");
		} else if (args.length != 2 && args.length != 3 && args.length != 5) {
			if (hollow) {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/hpyramid [blockID / blockName] [size]",
								"/hpyramid [blockID / blockName] [blockData] [size]"));
			} else {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/pyramid [blockID / blockName] [size]",
								"/pyramid [blockID / blockName] [blockData] [size]"));
			}
		} else if (!EvilBook.isInteger(args.length == 2 ? args[1] : args[2])) {
			player.sendMessage("§7Please enter a valid size");
		} else {
			BlockType actionBlock = new BlockType(args[0]);
			if (args.length == 3) actionBlock.setData(args[1]);
			if (!actionBlock.isValid(player)) return;
			int size = args.length == 2 ? Integer.parseInt(args[1]) : Integer.parseInt(args[2]);
			if (size > 50) {
				player.sendMessage("§7The maximum size limit is 50");
				return;
			}
			int height = size, blockId = actionBlock.getMaterial().getId();
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(player.getWorld(), player);
			for (int y = 0; y <= height; ++y) {
				size--;
				for (int x = 0; x <= size; ++x) {
					for (int z = 0; z <= size; ++z) {
						if ((!hollow && z <= size && x <= size) || z == size || x == size) {
							engine.setBlock(player.getLocation().add(x, y, z), blockId, actionBlock.getData());
							engine.setBlock(player.getLocation().add(-x, y, z), blockId, actionBlock.getData());
							engine.setBlock(player.getLocation().add(x, y, -z), blockId, actionBlock.getData());
							engine.setBlock(player.getLocation().add(-x, y, -z), blockId, actionBlock.getData());
						}
					}
				}
			}
			if (!hollow) Movement.ascendPlayer(player, false);
			engine.notifyClients(GlobalStatistic.BLOCKS_PLACED);
			player.sendMessage((hollow ? "§7Created a hollow " : "§7Created a ") + engine.getBlocksChanged() + " block pyramid made of " + BlockReference.getFriendlyName(actionBlock.getMaterial()));
		}
	}

	static void createSphere(Player player, String[] args, boolean hollow, boolean empty) {
		if (EvilBook.isInSurvival(player) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7EvilEdit can't be used in survival");
		} else if (EvilBook.isInMinigame(player, MinigameType.SKYBLOCK) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7EvilEdit can't be used in skyblock survival");
		} else if (args.length != 2 && args.length != 3 && args.length != 5) {
			if (hollow) {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/hsphere [blockID / blockName] [radius]",
								"/hsphere [blockID / blockName] [blockData] [radius]",
								"/hsphere [blockID / blockName] [blockData] [radiusX] [radiusY] [radiusZ]"));
			} else {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/sphere [blockID / blockName] [radius]",
								"/sphere [blockID / blockName] [blockData] [radius]",
								"/sphere [blockID / blockName] [blockData] [radiusX] [radiusY] [radiusZ]"));
			}
		} else if (args.length == 2 && !EvilBook.isInteger(args[1])) {
			player.sendMessage("§7Please enter a valid radius");
		} else if (args.length == 3 && !EvilBook.isInteger(args[2])) {
			player.sendMessage("§7Please enter a valid radius");
		} else if (args.length == 5 && (!EvilBook.isInteger(args[2]) || !EvilBook.isInteger(args[3]) || !EvilBook.isInteger(args[4]))) {
			player.sendMessage("§7Please enter a valid radius");
		} else {
			double radiusX;
			double radiusY;
			double radiusZ;
			if (args.length == 2) {
				radiusX = Integer.parseInt(args[1]) + 0.5;
				radiusY = Integer.parseInt(args[1]) + 0.5;
				radiusZ = Integer.parseInt(args[1]) + 0.5;
			} else if (args.length == 3) {
				radiusX = Integer.parseInt(args[2]) + 0.5;
				radiusY = Integer.parseInt(args[2]) + 0.5;
				radiusZ = Integer.parseInt(args[2]) + 0.5;
			} else {
				radiusX = Integer.parseInt(args[2]) + 0.5;
				radiusY = Integer.parseInt(args[3]) + 0.5;
				radiusZ = Integer.parseInt(args[4]) + 0.5;
			}
			if (radiusX > 50.5 || radiusY > 50.5 || radiusZ > 50.5) {
				player.sendMessage("§7The maximum radius limit is 50");
				return;
			}		 
			BlockType actionBlock = new BlockType(args[0]);
			if (args.length == 3) actionBlock.setData(args[1]);
			if (!actionBlock.isValid(player)) return;
			final double invRadiusX = 1 / radiusX;
			final double invRadiusY = 1 / radiusY;
			final double invRadiusZ = 1 / radiusZ;
			final int ceilRadiusX = (int) Math.ceil(radiusX);
			final int ceilRadiusY = (int) Math.ceil(radiusY);
			final int ceilRadiusZ = (int) Math.ceil(radiusZ);
			double nextXn = 0;
			EvilEditEngine engine = CraftEvilEditEngine.createEngine(player.getWorld(), player);
			forX: for (int x = 0; x <= ceilRadiusX; ++x) {
				final double xn = nextXn;
				nextXn = (x + 1) * invRadiusX;
				double nextYn = 0;
				forY: for (int y = 0; y <= ceilRadiusY; ++y) {
					final double yn = nextYn;
					nextYn = (y + 1) * invRadiusY;
					double nextZn = 0;
					forZ: for (int z = 0; z <= ceilRadiusZ; ++z) {
						final double zn = nextZn;
						nextZn = (z + 1) * invRadiusZ;
						double distanceSq = Session.lengthSq(xn, yn, zn);
						if (distanceSq > 1) {
							if (z == 0) {
								if (y == 0) {
									break forX;
								}
								break forY;
							}
							break forZ;
						}
						if (hollow) {
							if (Session.lengthSq(nextXn, yn, zn) <= 1 && Session.lengthSq(xn, nextYn, zn) <= 1 && Session.lengthSq(xn, yn, nextZn) <= 1)  {
								if (empty) {
									engine.setBlock(player.getLocation().add(x, y, z), Material.AIR.getId(), 0);
									engine.setBlock(player.getLocation().add(-x, y, z), Material.AIR.getId(), (byte) 0);
									engine.setBlock(player.getLocation().add(x, -y, z), Material.AIR.getId(), (byte) 0);
									engine.setBlock(player.getLocation().add(x, y, -z), Material.AIR.getId(), (byte) 0);
									engine.setBlock(player.getLocation().add(-x, -y, z), Material.AIR.getId(), (byte) 0);
									engine.setBlock(player.getLocation().add(x, -y, -z), Material.AIR.getId(), (byte) 0);
									engine.setBlock(player.getLocation().add(-x, y, -z), Material.AIR.getId(), (byte) 0);
									engine.setBlock(player.getLocation().add(-x, -y, -z), Material.AIR.getId(), (byte) 0);
								}
								continue;
							}
						}
						engine.setBlock(player.getLocation().add(x, y, z), actionBlock.getMaterial().getId(), actionBlock.getData());
						engine.setBlock(player.getLocation().add(-x, y, z), actionBlock.getMaterial().getId(), actionBlock.getData());
						engine.setBlock(player.getLocation().add(x, -y, z), actionBlock.getMaterial().getId(), actionBlock.getData());
						engine.setBlock(player.getLocation().add(x, y, -z), actionBlock.getMaterial().getId(), actionBlock.getData());
						engine.setBlock(player.getLocation().add(-x, -y, z), actionBlock.getMaterial().getId(), actionBlock.getData());
						engine.setBlock(player.getLocation().add(x, -y, -z), actionBlock.getMaterial().getId(), actionBlock.getData());
						engine.setBlock(player.getLocation().add(-x, y, -z), actionBlock.getMaterial().getId(), actionBlock.getData());
						engine.setBlock(player.getLocation().add(-x, -y, -z), actionBlock.getMaterial().getId(), actionBlock.getData());
					}
				}
			}
			if (!hollow) Movement.ascendPlayer(player, false);
			engine.notifyClients(GlobalStatistic.BLOCKS_PLACED);
			player.sendMessage((hollow ? empty ? "§7Created an empty hollow " : "§7Created a hollow " : "§7Created a ") + engine.getBlocksChanged() + " block sphere made of " + BlockReference.getFriendlyName(actionBlock.getMaterial()));
		}
	}

	static void createCylinder(Player player, String[] args, boolean hollow) {
		if (EvilBook.isInSurvival(player) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7EvilEdit can't be used in survival");
		} else if (EvilBook.isInMinigame(player, MinigameType.SKYBLOCK) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7EvilEdit can't be used in skyblock survival");
		} else if (args.length != 3 && args.length != 4 && args.length != 5) {
			if (hollow) {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/hcylinder [blockID / blockName] [radius] [height]",
								"/hcylinder [blockID / blockName] [blockData] [radius] [height]",
								"/hcylinder [blockID / blockName] [blockData] [radiusX] [radiusZ] [height]"));
			} else {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/cylinder [blockID / blockName] [radius] [height]",
								"/cylinder [blockID / blockName] [blockData] [radius] [height]",
								"/cylinder [blockID / blockName] [blockData] [radiusX] [radiusZ] [height]"));
			}
		} else if (args.length == 3 && (!EvilBook.isInteger(args[1]) || !EvilBook.isInteger(args[2]))) {
			player.sendMessage("§7Please enter a valid radius and height");
		} else if (args.length == 4 && (!EvilBook.isInteger(args[2]) || !EvilBook.isInteger(args[3]))) {
			player.sendMessage("§7Please enter a valid radius, height");
		} else if (args.length == 5 && (!EvilBook.isInteger(args[2]) || !EvilBook.isInteger(args[3]) || !EvilBook.isInteger(args[4]))) {
			player.sendMessage("§7Please enter valid radius, height");
		} else {
				double radiusX = args.length == 3 ? Integer.parseInt(args[1]) + 0.5 : Integer.parseInt(args[2]) + 0.5;
				double radiusZ = args.length == 3 ? Integer.parseInt(args[1]) + 0.5 : args.length == 4 ? Integer.parseInt(args[2]) + 0.5 : Integer.parseInt(args[3]) + 0.5;
				double height = args.length == 3 ? Integer.parseInt(args[2]) + 0.5 : args.length == 4 ? Integer.parseInt(args[3]) + 0.5 : Integer.parseInt(args[4]) + 0.5;
				if (radiusX > 25.5 || radiusZ > 25.5) {
					player.sendMessage("§7The maximum radius limit is 25");
					return;
				}		 
				final double invRadiusX = 1 / radiusX;
				final double invRadiusZ = 1 / radiusZ;
				final int ceilRadiusX = (int) Math.ceil(radiusX);
				final int ceilRadiusZ = (int) Math.ceil(radiusZ);
				double nextXn = 0;
				BlockType actionBlock = new BlockType(args[0]);
				if (args.length == 3) actionBlock.setData(args[1]);
				if (!actionBlock.isValid(player)) return;
				EvilEditEngine engine = CraftEvilEditEngine.createEngine(player.getWorld(), player);
				forX: for (int x = 0; x <= ceilRadiusX; ++x) {
					final double xn = nextXn;
					nextXn = (x + 1) * invRadiusX;
					double nextZn = 0;
					forZ: for (int z = 0; z <= ceilRadiusZ; ++z) {
						final double zn = nextZn;
						nextZn = (z + 1) * invRadiusZ;
						double distanceSq = Session.lengthSq(xn, zn);
						if (distanceSq > 1) {
							if (z == 0) {
								break forX;
							}
							break forZ;
						}
						if (hollow) {
							if (Session.lengthSq(nextXn, zn) <= 1 && Session.lengthSq(xn, nextZn) <= 1) {
								continue;
							}
						}
						for (int y = 0; y < height; ++y) {
							engine.setBlock(player.getLocation().add(x, y, z), actionBlock.getMaterial().getId(), actionBlock.getData());
							engine.setBlock(player.getLocation().add(-x, y, z), actionBlock.getMaterial().getId(), actionBlock.getData());
							engine.setBlock(player.getLocation().add(x, y, -z), actionBlock.getMaterial().getId(), actionBlock.getData());
							engine.setBlock(player.getLocation().add(-x, y, -z), actionBlock.getMaterial().getId(), actionBlock.getData());
						}
					}
				}
				if (!hollow) Movement.ascendPlayer(player, false);
				engine.notifyClients(GlobalStatistic.BLOCKS_PLACED);
				player.sendMessage((hollow ? "§7Created a hollow " : "§7Created a ") + engine.getBlocksChanged() + " block cylinder made of " + BlockReference.getFriendlyName(actionBlock.getMaterial()));
		}
	}
    
	static void createTree(Player player, String[] args) {
		if (EvilBook.isInSurvival(player) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7EvilEdit can't be used in survival");
		} else if (EvilBook.isInMinigame(player, MinigameType.SKYBLOCK) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7EvilEdit can't be used in skyblock survival");
		} else {
			if (args.length == 0) {
				TreeGenerationDelegate delegate = new TreeGenerationDelegate(player.getLocation(), player);
				player.getWorld().generateTree(player.getLocation(), TreeType.TREE, delegate);
				delegate.notifyClients();
				player.sendMessage("§7Created a tree");
			} else if (args.length == 1) {
				TreeType treeType = TreeReference.getTreeType(args[0]);
				if (treeType == null) {
					player.sendMessage("§7Please enter a valid tree type");
				} else {
					TreeGenerationDelegate delegate = new TreeGenerationDelegate(player.getLocation(), player);
					if (player.getWorld().generateTree(player.getLocation(), treeType, delegate)) {
						Movement.ascendPlayer(player, false);
						delegate.notifyClients();
						player.sendMessage("§7Created a " + TreeReference.treeTypeList.get(treeType).get(0) + " tree");
					} else {
						player.sendMessage("§7This is not a valid location for a " + TreeReference.treeTypeList.get(treeType).get(0) + " tree");
					}
				}
			} else {
				ChatExtensions.sendCommandHelpMessage(player, 
						Arrays.asList("/tree",
								"/tree [treeType]"));
			}
		}
	}
}
