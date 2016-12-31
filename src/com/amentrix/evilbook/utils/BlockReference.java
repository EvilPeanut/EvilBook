package com.amentrix.evilbook.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;

import com.amentrix.evilbook.main.EvilBook;

/**
 * Block reference utility class
 * @author Reece Aaron Lecrivain
 */
public class BlockReference {
	private static final Map<Material, List<String>> blockList = new LinkedHashMap<>();
	
    static
    {
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
		blockList.put(Material.SLIME_BLOCK, Arrays.asList("Slime", "SlimeBlock"));
		blockList.put(Material.BARRIER, Arrays.asList("Barrier"));
		blockList.put(Material.IRON_TRAPDOOR, Arrays.asList("Iron Trapdoor", "IronTrapdoor", "IronHatch"));
		blockList.put(Material.PRISMARINE, Arrays.asList("Prismarine"));
		blockList.put(Material.SEA_LANTERN, Arrays.asList("Sea Lantern", "SeaLantern", "Lantern"));
		blockList.put(Material.HAY_BLOCK, Arrays.asList("Hay Block", "Hay", "HayBlock"));
		blockList.put(Material.CARPET, Arrays.asList("Carpet"));
		blockList.put(Material.HARD_CLAY, Arrays.asList("Hardened Clay", "HardClay", "HardenedClay"));
		blockList.put(Material.COAL_BLOCK, Arrays.asList("Block of Coal", "CoalBlock", "BlockofCoal"));
		blockList.put(Material.PACKED_ICE, Arrays.asList("Packed Ice", "PackedIce"));
		blockList.put(Material.DOUBLE_PLANT, Arrays.asList("Sunflower"));
		blockList.put(Material.STANDING_BANNER, Arrays.asList("Standing Banner"));
		blockList.put(Material.WALL_BANNER, Arrays.asList("Wall Banner"));
		blockList.put(Material.DAYLIGHT_DETECTOR_INVERTED, Arrays.asList("Inverted Daylight Sensor", "InvertedDaylightSensor", "InvertedLightSensor", "InvertedSolarPanel"));
		blockList.put(Material.RED_SANDSTONE, Arrays.asList("Red Sandstone", "RedSandstone"));
		blockList.put(Material.RED_SANDSTONE_STAIRS, Arrays.asList("Red Sandstone Stairs", "RedSandstoneStairs"));
		blockList.put(Material.DOUBLE_STONE_SLAB2, Arrays.asList("Double Red Sandstone Slab", "DoubleRedSandstoneSlab", "RedSandstoneDoubleSlab"));
		blockList.put(Material.STONE_SLAB2, Arrays.asList("Red Sandstone Slab", "RedSandstoneSlab"));
		blockList.put(Material.SPRUCE_FENCE_GATE, Arrays.asList("Spruce Fence Gate", "SpruceFenceGate", "SpruceGate"));
		blockList.put(Material.BIRCH_FENCE_GATE, Arrays.asList("Birch Fence Gate", "BirchFenceGate", "BirchGate"));
		blockList.put(Material.JUNGLE_FENCE_GATE, Arrays.asList("Jungle Fence Gate", "JungleFenceGate", "JungleGate"));
		blockList.put(Material.DARK_OAK_FENCE_GATE, Arrays.asList("Dark Oak Fence Gate", "DarkOakFenceGate", "DarkOakGate"));
		blockList.put(Material.ACACIA_FENCE_GATE, Arrays.asList("Acacia Fence Gate", "AcaciaFenceGate", "AcaciaGate"));
		blockList.put(Material.SPRUCE_FENCE, Arrays.asList("Spruce Fence", "SpruceFence"));
		blockList.put(Material.BIRCH_FENCE, Arrays.asList("Birch Fence", "BirchFence"));
		blockList.put(Material.JUNGLE_FENCE, Arrays.asList("Jungle Fence", "JungleFence"));
		blockList.put(Material.DARK_OAK_FENCE, Arrays.asList("Dark Oak Fence", "DarkOakFence"));
		blockList.put(Material.ACACIA_FENCE, Arrays.asList("Acacia Fence", "AcaciaFence"));
		blockList.put(Material.SPRUCE_DOOR, Arrays.asList("Spruce Door", "SpruceDoor"));
		blockList.put(Material.BIRCH_DOOR, Arrays.asList("Birch Door", "BirchDoor"));
		blockList.put(Material.JUNGLE_DOOR, Arrays.asList("Jungle Door", "JungleDoor"));
		blockList.put(Material.ACACIA_DOOR, Arrays.asList("Acacia Door", "AcaciaDoor"));
		blockList.put(Material.DARK_OAK_DOOR, Arrays.asList("Dark Oak Door", "DarkOakDoor"));
    }
    
	/**
	 * Return the block material from an argument
	 * @param block The argument containing the block ID or name
	 * @return The block material or null if it can't be found
	 */
	public static Material getBlockMaterial(String block) {
		int blockID = 0;
		for (Entry<Material, List<String>> entry : blockList.entrySet()) {
			if (EvilBook.isInteger(block)) {
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
    
	/**
	 * Return the human readable name of a material
	 * @param material The material
	 * @return The human readable material name
	 */
	public static String getFriendlyName(Material material) {
		return blockList.get(material) == null ? "Air" : blockList.get(material).get(0);
	}
}
