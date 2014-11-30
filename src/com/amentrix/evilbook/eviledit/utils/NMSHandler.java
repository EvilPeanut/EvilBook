package com.amentrix.evilbook.eviledit.utils;

import net.minecraft.server.v1_8_R1.Block;
import net.minecraft.server.v1_8_R1.Chunk;
import net.minecraft.server.v1_8_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_8_R1.EnumSkyBlock;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * EvilEdit NMSHandler
 * Based on dhutils by desht
 * @author Reece Aaron Lecrivain
 */
public class NMSHandler implements NMSAbstraction {
	@Override
	public boolean setBlockFast(World world, int x, int y, int z, int blockId, byte data) {
		net.minecraft.server.v1_8_R1.World w = ((CraftWorld) world).getHandle();
		Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
		//return chunk.a(x & 0x0f, y, z & 0x0f, Block.getById(blockId), data);
		return false;
	}

	@Override
	public void forceBlockLightLevel(World world, int x, int y, int z, int level) {
		net.minecraft.server.v1_8_R1.World w = ((CraftWorld) world).getHandle();
		//w.b(EnumSkyBlock.BLOCK, x, y, z, level);
	}

	@Override
	public int getBlockLightEmission(int blockId) {
		//return Block.getById(blockId).m();
		return 0;
	}

	@Override
	public int getBlockLightBlocking(int blockId) {
		//return Block.getById(blockId).k();
		return 0;
	}

	@Override
	public void queueChunkForUpdate(Player player, int cx, int cz) {
		((CraftPlayer) player).getHandle().chunkCoordIntPairQueue.add(new ChunkCoordIntPair(cx, cz));
	}

	@Override
	public void recalculateBlockLighting(World world, int x, int y, int z) {
		net.minecraft.server.v1_8_R1.World w = ((CraftWorld) world).getHandle();
		//w.t(x, y, z);
	}
}