package com.amentrix.evilbook.worldgen;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

public class SkylandFlowerPopulator extends BlockPopulator {

	private Random random;

	public SkylandFlowerPopulator(World world){
		this.random = new Random(world.getSeed());
	}

	@Override
	public void populate(World world, Random random, Chunk chunk){
		int x, y, z;
		Block block;
		Biome biome;
		for (x = 0; x < 16; ++x) {
			for (z = 0; z < 16; ++z) {
				for (y = 0; y < 128; y++) {
					if (y > 4){
						block = chunk.getBlock(x, y, z);
						biome = block.getBiome();
						if (block.getRelative(BlockFace.DOWN).getType() == Material.GRASS){
							if (biome == Biome.PLAINS){
								if (this.random.nextInt(100) < 7){
									block.setType((this.random.nextInt(100) < 75) ? Material.YELLOW_FLOWER : Material.RED_ROSE);
								}
							} else if (biome != Biome.DESERT && biome != Biome.TAIGA && biome != Biome.TAIGA_HILLS && biome != Biome.ICE_PLAINS && biome != Biome.ICE_MOUNTAINS && biome != Biome.FROZEN_RIVER && biome != Biome.FROZEN_OCEAN) {
								if (this.random.nextInt(100) < 2){
									block.setType((this.random.nextInt(100) < 75) ? Material.YELLOW_FLOWER : Material.RED_ROSE);
								}
							}
						}
					}
				}
			}
		}
	}
}