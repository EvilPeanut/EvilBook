package com.amentrix.evilbook.worldgen;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

class SkylandGrassPopulator extends BlockPopulator {
	private Random random;

	SkylandGrassPopulator(World world){
		this.random = new Random(world.getSeed());
	}

	@Override
	public void populate(World world, Random random, Chunk chunk){
		int x, y, z;
		Block block, ground;
		Biome biome;
		int worldChunkX = chunk.getX() * 16;
		int worldChunkZ = chunk.getZ() * 16;
		for (x = 0; x < 16; ++x) {
			for (z = 0; z < 16; ++z) {
				y = world.getHighestBlockYAt(worldChunkX + x, worldChunkZ + z);
				if (y > 5) {
					block = chunk.getBlock(x, y, z);
					ground = block.getRelative(BlockFace.DOWN);
					biome = block.getBiome();
					if (ground.getType() == Material.GRASS || ground.getType() == Material.SAND) {
						if (biome == Biome.PLAINS) {
							if (this.random.nextInt(100) < 35) {
								block.setType(Material.LONG_GRASS);
								block.setData((byte) 0x1);
							}
						} else if (biome == Biome.DESERT) {
							if (this.random.nextInt(100) < 3) {
								block.setType(Material.DEAD_BUSH);
							}
						} else if (biome == Biome.TAIGA || biome == Biome.TAIGA_HILLS) {
							if (this.random.nextInt(100) < 1) {
								block.setType(Material.LONG_GRASS);
								block.setData((byte) 0x2);
							}
						} else if (biome == Biome.TAIGA || biome == Biome.TAIGA_HILLS || biome == Biome.ICE_FLATS || biome == Biome.ICE_MOUNTAINS || biome == Biome.FROZEN_RIVER || biome == Biome.FROZEN_OCEAN) {
							if (this.random.nextInt(100) < 1) {
								block.setType(Material.LONG_GRASS);
								block.setData((byte) 0x1);
							}
						} else {
							if (this.random.nextInt(100) < 14) {
								block.setType(Material.LONG_GRASS);
								block.setData((byte) 0x1);
								block.setData((byte) ((this.random.nextInt(100) < 85) ? 0x1 : 0x2));
							}
						}
					}
				}
			}
		}
	}

}