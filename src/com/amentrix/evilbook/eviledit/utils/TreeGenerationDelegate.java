package com.amentrix.evilbook.eviledit.utils;

import org.bukkit.BlockChangeDelegate;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.PlayerProfileAdmin;
import com.amentrix.evilbook.statistics.GlobalStatistic;

/**
 * EvilEdit tree generation delegate
 * @author Reece Aaron Lecrivain
 */
public class TreeGenerationDelegate implements BlockChangeDelegate {
	private Location loc;
	private Player player;
	private EvilEditEngine engine;
	
	public TreeGenerationDelegate(Location loc, Player player, EvilBook plugin) {
		this.loc = loc;
		this.player = player;
		((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.clearUndo();
		engine = CraftEvilEditEngine.createEngine(plugin, player.getWorld(), player);
	}
	
	@Override
	public boolean setRawTypeIdAndData(int x, int y, int z, int id, int data) {
		BlockState oldBlockState = loc.getWorld().getBlockAt(x, y, z).getState();
		engine.setBlock(x, y, z, id, data);
		// Clipboard
		((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.appendUndo(oldBlockState);
		// Logging
		EvilBook.lbConsumer.queueBlockReplace(player.getName(), oldBlockState, new Location(loc.getWorld(), x, y, z).getBlock().getState());
		return true;
	}
	
	public void notifyClients() {
		engine.notifyClients(GlobalStatistic.BlocksPlaced);
	}
	
	@Override
	public int getHeight() {
		return loc.getWorld().getMaxHeight();
	}

	@Override
	public int getTypeId(int x, int y, int z) {
		return loc.getWorld().getBlockTypeIdAt(x, y, z);
	}

	@Override
	public boolean isEmpty(int x, int y, int z) {
		return getTypeId(x, y, z) == 0;
	}

	@Override
	public boolean setRawTypeId(int x, int y, int z, int id) {
		return setRawTypeIdAndData(x, y, z, id, 0);
	}

	@Override
	public boolean setTypeId(int x, int y, int z, int id) {
		return setRawTypeId(x, y, z, id);
	}

	@Override
	public boolean setTypeIdAndData(int x, int y, int z, int id, int data) {
		return setRawTypeIdAndData(x, y, z, id, data);
	}
}
