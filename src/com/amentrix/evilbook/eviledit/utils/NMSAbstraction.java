package com.amentrix.evilbook.eviledit.utils;

import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * EvilEdit NMSAbstraction interface
 * Based on dhutils by desht
 * @author Reece Aaron Lecrivain
 */
public interface NMSAbstraction {
	public boolean setBlockFast(World world, int x, int y, int z, int blockId, byte data);
	
	public void forceBlockLightLevel(World world, int x, int y, int z, int level);
	public void recalculateBlockLighting(World world, int x, int y, int z);
	
	public int getBlockLightEmission(int blockId);
	public int getBlockLightBlocking(int blockId);
	
	public void queueChunkForUpdate(Player player, int cx, int cz);
}