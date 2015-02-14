package com.amentrix.evilbook.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class PlotlandGenerator extends ChunkGenerator {

	@Override
	public List<BlockPopulator> getDefaultPopulators(World world){
		ArrayList<BlockPopulator> populators = new ArrayList<>();
		populators.add(new PlotlandPopulator());
		return populators;
	}
	
	@Override
	public boolean canSpawn(World world, int x, int z) {
		return false;	
	}
	
	@Override
	public byte[] generate(World world, Random rand, int chunkx, int chunkz) {
		byte[] blocks = new byte[65536];

		long realX = chunkx * 16;
		long realZ = chunkz * 16;

		if (realX < 0) realX = 41 + (realX % 41);
		if (realZ < 0) realZ = 41 + (realZ % 41);

		for (int y = 0; y < 31; ++y) {
			for (int x = 0; x < 16; ++x) {
				for (int z = 0; z < 16; ++z) {
					if (y != 30) {
						blocks[coordsToInt(x, y, z)] = y == 0 ? (byte) Material.BEDROCK.getId() : (byte) Material.DIRT.getId();
					} else {
						long relx = (realX + x) % 41;
						long relz = (realZ + z) % 41;

						if (relx < 34 && relz < 34) {
							blocks[coordsToInt(x, 30, z)] = (byte) Material.GRASS.getId();
						} 

						// Slabs
						if (relx == 0 && relz < 34) {
							blocks[coordsToInt(x, 31, z)] = (byte) 44;
						} else if (relx == 33 && relz < 34) {
							blocks[coordsToInt(x, 31, z)] = (byte) 44;
						} else if (relx < 34 && relz == 0) {
							blocks[coordsToInt(x, 31, z)] = (byte) 44;
						} else if (relx < 34 && relz == 33) {
							blocks[coordsToInt(x, 31, z)] = (byte) 44;
						}
					}
				}
			}
		}
		return blocks;
	}

	private static int coordsToInt(int x, int y, int z) {
		return (x * 16 + z) * 256 + y;
	}
}