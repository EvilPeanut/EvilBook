package com.amentrix.evilbook.worldgen;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

public class PlotlandPopulator extends BlockPopulator {

	@Override
	public void populate(World world, Random rand, Chunk chunk) {
		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				Block block = chunk.getBlock(x, 30, z);
				int realX = block.getLocation().getBlockX();
				int realZ = block.getLocation().getBlockZ();
				// Road
				if (block.getType() == Material.AIR) {
					if ((((realX + 4) % 41 == 0) && (realZ % 3 != 0)) || (((realZ + 4) % 41 == 0) && (realX % 3 != 0))) {
						block.setType(Material.QUARTZ_BLOCK);
					} else {
						block.setTypeIdAndData(Material.STAINED_CLAY.getId(), (byte) 3, false);
					}
				}
				// Lamps
				if ((realX % 41 == 0 || (realX - 33) % 41 == 0) && (realZ % 41 == 0 || (realZ - 33) % 41 == 0)) {
					chunk.getBlock(x, 31, z).setType(Material.DOUBLE_STEP);
					chunk.getBlock(x, 32, z).setType(Material.FENCE);
					chunk.getBlock(x, 33, z).setType(Material.FENCE);
					chunk.getBlock(x, 34, z).setType(Material.GLOWSTONE);
				}
			}
		}
	}

}