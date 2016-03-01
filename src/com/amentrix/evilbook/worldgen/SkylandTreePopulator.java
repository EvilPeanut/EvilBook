package com.amentrix.evilbook.worldgen;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

/**
 * @author Reece Aaron Lecrivain
 */
class SkylandTreePopulator extends BlockPopulator {
	private Random random;

	/**
	 * Define a new tree populator
	 * @param world
	 */
	SkylandTreePopulator(World world){
		this.random = new Random(world.getSeed());
	}

	/**
	 * Populate the chunk
	 */
	@Override
	public void populate(World world, Random random, Chunk chunk) {
		int x, y, z;
		Block block, ground;
		Biome biome;

		int worldChunkX = chunk.getX() * 16;
		int worldChunkZ = chunk.getZ() * 16;

		for (x = 4; x < 13; ++x){
			for (z = 4; z < 13; ++z){
				for (y = 5; y < 128; y++){
					block = chunk.getBlock(x, y, x);
					ground = block.getRelative(BlockFace.DOWN);
					biome = world.getBiome(worldChunkX + x, worldChunkZ + z);

					if (ground.getType() == Material.GRASS){
						if (biome == Biome.PLAINS || biome == Biome.ICE_FLATS){
							if (this.random.nextInt(1000) < 5){
								world.generateTree(block.getLocation(), TreeType.TREE);
							}
						}else if (biome == Biome.TAIGA || biome == Biome.TAIGA_HILLS){
							if (this.random.nextInt(1000) < 70){
								world.generateTree(block.getLocation(), (this.random.nextInt(100) < 60) ? TreeType.REDWOOD : TreeType.TALL_REDWOOD);
							}
						}else if (biome == Biome.JUNGLE || biome == Biome.JUNGLE_HILLS){
							if (this.random.nextInt(1000) < 75){
								world.generateTree(block.getLocation(), TreeType.JUNGLE);
							}else if (this.random.nextInt(1000) < 300){
								world.generateTree(block.getLocation(), TreeType.JUNGLE_BUSH);
							}
						}else if (biome != Biome.DESERT && biome != Biome.DESERT_HILLS){
							if (this.random.nextInt(1000) < 75){
								if (this.random.nextInt(100) < 30){
									world.generateTree(block.getLocation(), TreeType.BIRCH);
								}else if (this.random.nextInt(100) < 55){
									world.generateTree(block.getLocation(), TreeType.TREE);
								}else{
									world.generateTree(block.getLocation(), TreeType.BIG_TREE);
								}
							}
						}
					}
				}
			}
		}
	}

}