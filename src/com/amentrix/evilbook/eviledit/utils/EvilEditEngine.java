package com.amentrix.evilbook.eviledit.utils;

import org.bukkit.Location;
import com.amentrix.evilbook.statistics.GlobalStatistic;

/**
 * EvilEdit engine interface
 * Based on dhutils by desht
 * @author Reece Aaron Lecrivain
 */
public interface EvilEditEngine {
	public boolean setBlock(Location loc, int id, int data);
	public boolean setBlock(int x, int y, int z, int id, int data);

	public void notifyClients(GlobalStatistic statistic);

	public int getBlocksChanged();
}