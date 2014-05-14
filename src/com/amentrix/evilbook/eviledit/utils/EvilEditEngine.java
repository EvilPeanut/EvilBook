package com.amentrix.evilbook.eviledit.utils;

import org.bukkit.Location;
import com.amentrix.evilbook.statistics.Statistic;

public interface EvilEditEngine {
	public boolean setBlock(Location loc, int id, int data);
	public boolean setBlock(int x, int y, int z, int id, int data);

	public void notifyClients(Statistic statistic);

	public int getBlocksChanged();
}