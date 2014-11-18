package com.amentrix.evilbook.eviledit;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Furnace;
import org.bukkit.block.Jukebox;
import org.bukkit.block.NoteBlock;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import com.amentrix.evilbook.main.EvilBook;

/**
 * EvilEdit session instance
 * @author Reece Aaron Lecrivain
 */
public class Session implements CommandExecutor {
	private EvilBook plugin;
	
	public Session(EvilBook plugin) {
		this.plugin = plugin;
		Generation.init(plugin);
		Region.init(plugin);
	}
	
	/**
	 * Called when a command is executed
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (sender instanceof Player == false) return true;
		Player player = (Player)sender;
		if (command.getName().equalsIgnoreCase("move") || command.getName().equalsIgnoreCase("/move")) {	
			Region.moveArea(player, args);
		} else if (command.getName().equalsIgnoreCase("thaw")) {	
			Region.thawSnowArea(player);
		} else if (command.getName().equalsIgnoreCase("snow")) {	
			Region.overlaySnowArea(player);
		} else if (command.getName().equalsIgnoreCase("flip") || command.getName().equalsIgnoreCase("/flip")) {	
			Region.flipArea(player, args);
		} else if (command.getName().equalsIgnoreCase("undo") || command.getName().equalsIgnoreCase("/undo")) {	
			Region.undoEdit(player);
		} else if (command.getName().equalsIgnoreCase("fill") || command.getName().equalsIgnoreCase("/fill") || command.getName().equalsIgnoreCase("set") || command.getName().equalsIgnoreCase("/set")) {	
			Region.fillArea(this.plugin, player, args);
		} else if (command.getName().equalsIgnoreCase("rfill")) {	
			Region.randomFillArea(player, args);
		} else if (command.getName().equalsIgnoreCase("rreplace")) {	
			Region.randomReplaceArea(player, args);
		} else if (command.getName().equalsIgnoreCase("del")) {	
			Region.deleteArea(player, args);
		} else if (command.getName().equalsIgnoreCase("delete")) {	
			Region.deleteArea(player, args);
		} else if (command.getName().equalsIgnoreCase("rdel")) {	
			Region.randomDeleteArea(player, args);
		} else if (command.getName().equalsIgnoreCase("copy") || command.getName().equalsIgnoreCase("/copy")) {	
			Region.copy(player, args);
		} else if (command.getName().equalsIgnoreCase("cut") || command.getName().equalsIgnoreCase("/cut")) {	
			Region.cut(player, args);
		} else if (command.getName().equalsIgnoreCase("paste") || command.getName().equalsIgnoreCase("/paste")) {	
			Region.paste(player, args);
		} else if (command.getName().equalsIgnoreCase("drain") || command.getName().equalsIgnoreCase("/drain")) {	
			Region.drainArea(player, args);
		} else if (command.getName().equalsIgnoreCase("green") || command.getName().equalsIgnoreCase("/green")) {	
			Region.greenArea(player);
		} else if (command.getName().equalsIgnoreCase("setbiome") || command.getName().equalsIgnoreCase("/setbiome")) {	
			Region.setBiome(player, args);
		} else if (command.getName().equalsIgnoreCase("regen") || command.getName().equalsIgnoreCase("/regen")) {	
			Region.regenerateChunk(player);
		} else if (command.getName().equalsIgnoreCase("hollow") || command.getName().equalsIgnoreCase("/hollow")) {	
			Region.hollowArea(player, args);
		} else if (command.getName().equalsIgnoreCase("outline") || command.getName().equalsIgnoreCase("/outline")) {	
			Region.outlineArea(player, args);
		} else if (command.getName().equalsIgnoreCase("walls") || command.getName().equalsIgnoreCase("/walls")) {	
			Region.wallArea(player, args);
		} else if (command.getName().equalsIgnoreCase("overlay") || command.getName().equalsIgnoreCase("/overlay")) {	
			Region.overlayArea(player, args);
		} else if (command.getName().equalsIgnoreCase("replace") || command.getName().equalsIgnoreCase("/replace")) {	
			Region.replaceArea(player, args);
		} else if (command.getName().equalsIgnoreCase("count") || command.getName().equalsIgnoreCase("/count")) {	
			Region.count(player, args);
		} else if (command.getName().equalsIgnoreCase("size") || command.getName().equalsIgnoreCase("/size")) {	
			Region.size(player);
		} else if (command.getName().equalsIgnoreCase("desel") || command.getName().equalsIgnoreCase("/desel")) {	
			Region.deselectEditLocations(player);
		} else if (command.getName().equalsIgnoreCase("wand") || command.getName().equalsIgnoreCase("/wand")) {	
			Region.spawnEditWand(player);
		} else if (command.getName().equalsIgnoreCase("toggleeditwand")) {	
			Region.toggleEditWand(player);
		} else if (command.getName().equalsIgnoreCase("ceil")) {
			Movement.ascendPlayerToCeiling(player);
		} else if (command.getName().equalsIgnoreCase("descend")) {	
			Movement.descendPlayer(player);
		} else if (command.getName().equalsIgnoreCase("ascend")) {	
			Movement.ascendPlayer(player, true);
		} else if (command.getName().equalsIgnoreCase("thru")) {	
			Movement.passPlayerThroughOpposingWall(player);
		} else if (command.getName().equalsIgnoreCase("pumpkins")) {	
			Generation.createPumpkinForest(player);
		} else if (command.getName().equalsIgnoreCase("forest") || command.getName().equalsIgnoreCase("forestgen")) {	
			Generation.createForest(player, args);
		} else if (command.getName().equalsIgnoreCase("pyramid") || command.getName().equalsIgnoreCase("/pyramid")) {	
			Generation.createPyramid(player, args, false);
		} else if (command.getName().equalsIgnoreCase("hpyramid") || command.getName().equalsIgnoreCase("/hpyramid")) {	
			Generation.createPyramid(player, args, true);
		} else if (command.getName().equalsIgnoreCase("cylinder") || command.getName().equalsIgnoreCase("cyl") ||
				command.getName().equalsIgnoreCase("/cylinder") || command.getName().equalsIgnoreCase("/cyl")) {	
			Generation.createCylinder(player, args, false);
		} else if (command.getName().equalsIgnoreCase("hcylinder") || command.getName().equalsIgnoreCase("hcyl") ||
				command.getName().equalsIgnoreCase("/hcylinder") || command.getName().equalsIgnoreCase("/hcyl")) {	
			Generation.createCylinder(player, args, true);
		} else if (command.getName().equalsIgnoreCase("esphere") || command.getName().equalsIgnoreCase("/esphere")) {	
			Generation.createSphere(player, args, true, true);
		} else if (command.getName().equalsIgnoreCase("sphere") || command.getName().equalsIgnoreCase("/sphere")) {	
			Generation.createSphere(player, args, false, false);
		} else if (command.getName().equalsIgnoreCase("hsphere") || command.getName().equalsIgnoreCase("/hsphere")) {	
			Generation.createSphere(player, args, true, false);
		} else if (command.getName().equalsIgnoreCase("tree")) {
			Generation.createTree(player, args);
		}
		return true;
	}

	/**
	 * Square a length
	 * @param x The X length
	 * @param y The Y length
	 * @param z The Z length
	 * @return The squared length
	 */
	protected static final double lengthSq(double x, double y, double z) {
		return (x * x) + (y * y) + (z * z);
	}

	/**
	 * Square a length
	 * @param x The X length
	 * @param z The Z length
	 * @return The squared length
	 */
	protected static final double lengthSq(double x, double z) {
		return (x * x) + (z * z);
	}

	/**
	 * Returns if a material is blocked in EvilEdit
	 * @param material The material to return if blocked
	 * @return If the material is blocked
	 */
	public static Boolean isBlocked(Material material) {
		switch (material) {
		case BED_BLOCK:
			return true;
		case PISTON_EXTENSION:
			return true;
		case PISTON_MOVING_PIECE:
			return true;
		case WALL_SIGN:
			return true;
		case SIGN_POST:
			return true;
		case IRON_DOOR_BLOCK:
			return true;
		default:
			return false;
		}
	}

	public static void setState(BlockState oldBlock, BlockState newBlock) {
		try {
			if (oldBlock instanceof BrewingStand && newBlock instanceof BrewingStand) {
				BrewingStand state = (BrewingStand)oldBlock;
				BrewingStand stateReal = (BrewingStand)newBlock;
				stateReal.setBrewingTime(state.getBrewingTime());
				stateReal.update();
			} else if (oldBlock instanceof CommandBlock && newBlock instanceof CommandBlock) {
				CommandBlock state = (CommandBlock)oldBlock;
				CommandBlock stateReal = (CommandBlock)newBlock;
				stateReal.setCommand(state.getCommand());
				stateReal.update();
			} else if (oldBlock instanceof CreatureSpawner && newBlock instanceof CreatureSpawner) {
				CreatureSpawner state = (CreatureSpawner)oldBlock;
				CreatureSpawner stateReal = (CreatureSpawner)newBlock;
				stateReal.setSpawnedType(state.getSpawnedType());
				stateReal.setDelay(state.getDelay());
				stateReal.update();
			} else if (oldBlock instanceof Furnace && newBlock instanceof Furnace) {
				Furnace state = (Furnace)oldBlock;
				Furnace stateReal = (Furnace)newBlock;
				stateReal.setBurnTime(state.getBurnTime());
				stateReal.setCookTime(state.getCookTime());
				stateReal.update();
			} else if (oldBlock instanceof Jukebox && newBlock instanceof Jukebox) {
				Jukebox state = (Jukebox)oldBlock;
				Jukebox stateReal = (Jukebox)newBlock;
				stateReal.setPlaying(state.getPlaying());
				stateReal.update();
			} else if (oldBlock instanceof NoteBlock && newBlock instanceof NoteBlock) {
				NoteBlock state = (NoteBlock)oldBlock;
				NoteBlock stateReal = (NoteBlock)newBlock;
				stateReal.setNote(state.getNote());
				stateReal.update();
			} else if (oldBlock instanceof Sign && newBlock instanceof Sign) {
				Sign state = (Sign)oldBlock;
				Sign stateReal = (Sign)newBlock;
				stateReal.setLine(0, state.getLines()[0]);
				stateReal.setLine(1, state.getLines()[1]);
				stateReal.setLine(2, state.getLines()[2]);
				stateReal.setLine(3, state.getLines()[3]);
				stateReal.update();
			} else if (oldBlock instanceof Skull && newBlock instanceof Skull) {
				Skull state = (Skull)oldBlock;
				Skull stateReal = (Skull)newBlock;
				stateReal.setSkullType(state.getSkullType());
				stateReal.setRotation(state.getRotation());
				stateReal.setOwner(state.getOwner());
				stateReal.update();
			}
			// If the oldBlock is a container handle contents
			if (oldBlock instanceof InventoryHolder && newBlock instanceof InventoryHolder) {
				InventoryHolder inventory = (InventoryHolder)oldBlock;
				InventoryHolder inventoryReal = (InventoryHolder)newBlock;
				inventoryReal.getInventory().setContents(inventory.getInventory().getContents());
			}
		} catch (RuntimeException e) {
			EvilBook.logWarning("Failed to set EvilEdit block state");
		} catch (Exception e) {
			EvilBook.logWarning("Failed to set EvilEdit block state");
		}
	}
}
