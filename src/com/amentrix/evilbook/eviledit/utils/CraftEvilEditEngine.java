package com.amentrix.evilbook.eviledit.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.PlayerProfileAdmin;
import com.amentrix.evilbook.statistics.GlobalStatistic;
import com.amentrix.evilbook.statistics.GlobalStatistics;

/**
 * EvilEdit engine instance
 * Based on dhutils by desht
 * @author Reece Aaron Lecrivain
 */
public class CraftEvilEditEngine implements EvilEditEngine {
	private final World world;

	private int minX = Integer.MAX_VALUE;
	private int minZ = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int maxZ = Integer.MIN_VALUE;
	private int blocksModified = 0;
	
	private Player player;
	
	private Boolean silent;
	
	private CraftEvilEditEngine(org.bukkit.World world, Player player, Boolean silent) {
		this.world = world;
		this.player = player;
		this.silent = silent;
	}

	@Override
	public int getBlocksChanged() {
		return this.blocksModified;
	}
	
	//TODO: Re-add support
	/*
	// Dynamic signs
	if (location.getBlock().getType() == Material.SIGN_POST || location.getBlock().getType() == Material.WALL_SIGN) {
		Iterator<DynamicSign> dynamicSigns = EvilBook.dynamicSignList.iterator();
		while (dynamicSigns.hasNext()) {
			DynamicSign dynamicSign = dynamicSigns.next();
			if (dynamicSign.location.getWorld() == location.getWorld() && dynamicSign.location.getBlockX() == location.getX() && dynamicSign.location.getBlockY() == location.getY() && dynamicSign.location.getBlockZ() == location.getZ()) {
				((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.undoDynamicSignList.add(dynamicSign);
				dynamicSign.delete();
				dynamicSigns.remove();
			}
		}
	}
	// Emitters								
	Location emitterLocation = location.getBlock().getRelative(BlockFace.UP).getLocation();
	SQL.deleteRowFromCriteria(TableType.Emitter, "world='" + emitterLocation.getWorld().getName() + 
			"' AND x='" + emitterLocation.getBlockX() + "' AND y='" + emitterLocation.getBlockY() + "' AND z='" + emitterLocation.getBlockZ() + "'");
	Iterator<Emitter> emit = EvilBook.emitterList.iterator();
	while (emit.hasNext()) {
		Emitter emitter = emit.next();
		if (emitter.location.getBlock().equals(emitterLocation.getBlock())) {
			((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.undoEmitterList.add(emitter);
			emit.remove();
		}
	}
	*/
	
	@Override
	public boolean setBlock(int x, int y, int z, int block, int data) {
		// Position validation
		if (y < 0 || y >= this.world.getMaxHeight()) return false;
		//
		BlockState oldBlockState = new Location(this.world, x, y, z).getBlock().getState();
		// Clipboard
		if (!this.silent) ((PlayerProfileAdmin)EvilBook.getProfile(this.player)).clipboard.appendUndo(oldBlockState);
		// Set the block to its new state
		this.minX = Math.min(this.minX, x);
		this.minZ = Math.min(this.minZ, z);
		this.maxX = Math.max(this.maxX, x);
		this.maxZ = Math.max(this.maxZ, z);
		this.blocksModified++;
		boolean res = this.world.getBlockAt(x, y, z).setTypeIdAndData(block, (byte) data, false);
		//int oldBlockId = this.world.getBlockTypeIdAt(x, y, z);
		//boolean res = this.nms.setBlockFast(this.world, x, y, z, block, (byte)data);
		//if (this.nms.getBlockLightBlocking(oldBlockId) != this.nms.getBlockLightBlocking(block) || this.nms.getBlockLightEmission(oldBlockId) != this.nms.getBlockLightEmission(block)) {
			//this.deferredBlocks.add(new DeferredBlock(x, y, z));
		//}
		// Logging
		EvilBook.lbConsumer.queueBlockReplace(this.player.getName(), oldBlockState, new Location(this.world, x, y, z).getBlock().getState());
		// Return if it was set
		return res;
	}
	
	@Override
	public boolean setBlock(Location loc, int block, int data) {
		return setBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), block, data);
	}

	@Override
	public void notifyClients(GlobalStatistic statistic) {
		// Statistics
		GlobalStatistics.incrementStatistic(statistic, this.blocksModified);
		// Do relighting
		//this.relightTask = Bukkit.getScheduler().runTaskTimer(this.plugin, this, 1L, 1L);
	}

	public static EvilEditEngine createEngine(org.bukkit.World world, Player player) {
		((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.clearUndo();
		return new CraftEvilEditEngine(world, player, false);
	}
	
	public static EvilEditEngine createEngineSilent(org.bukkit.World world, Player player) {
		return new CraftEvilEditEngine(world, player, true);
	}
}