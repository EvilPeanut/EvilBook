package com.amentrix.evilbook.worldgen;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

/**
 * @author Reece Aaron Lecrivain
 */
public class SkylandPumpkinPopulator extends BlockPopulator {
	/**
	 * Populate the chunk
	 */
	@Override
	public void populate(World world, Random positionRandomizer, Chunk chunk) {
		if (positionRandomizer.nextInt(25) == 0) {
			int worldChunkX = chunk.getX() * 16;
			int worldChunkZ = chunk.getZ() * 16;
			for (int pumpkinNumber = 0; pumpkinNumber <= positionRandomizer.nextInt(9); ++pumpkinNumber) {
				int pumpkinX = worldChunkX + positionRandomizer.nextInt(8) - positionRandomizer.nextInt(8);
				int pumpkinZ = worldChunkZ + positionRandomizer.nextInt(8) - positionRandomizer.nextInt(8);
				int pumpkinY = world.getHighestBlockYAt(pumpkinX, pumpkinZ);
				if (world.getBlockAt(pumpkinX, pumpkinY, pumpkinZ).getType() == Material.AIR && world.getBlockAt(pumpkinX, pumpkinY - 1, pumpkinZ).getType() == Material.GRASS) {
					world.getBlockAt(pumpkinX, pumpkinY, pumpkinZ).setTypeIdAndData(86, (byte) positionRandomizer.nextInt(4), false);
				}
			}
		}
	}
}