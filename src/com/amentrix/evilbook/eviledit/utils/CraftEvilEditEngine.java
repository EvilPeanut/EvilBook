package com.amentrix.evilbook.eviledit.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.PlayerProfileAdmin;
import com.amentrix.evilbook.statistics.GlobalStatistic;
import com.amentrix.evilbook.statistics.GlobalStatistics;

/**
 * EvilEdit engine instance
 * Based on dhutils by desht
 * @author Reece Aaron Lecrivain
 */
public class CraftEvilEditEngine implements EvilEditEngine, Runnable {
	private final Plugin plugin;
	private final World world;
	private final NMSAbstraction nms;

	private Queue<DeferredBlock> deferredBlocks = new ArrayDeque<>();
	private BukkitTask relightTask = null;

	private int minX = Integer.MAX_VALUE;
	private int minZ = Integer.MAX_VALUE;
	private int maxX = Integer.MIN_VALUE;
	private int maxZ = Integer.MIN_VALUE;
	private int blocksModified = 0;
	
	private Player player;
	
	private Boolean silent;
	
	public CraftEvilEditEngine(Plugin plugin, org.bukkit.World world, Player player, Boolean silent) {
		NMSHelper.init();
		this.plugin = plugin;
		this.world = world;
		this.player = player;
		this.silent = silent;
		this.nms = NMSHelper.getNMS();
		if (this.nms == null) {
			throw new IllegalStateException("NMS abstraction API is not available");
		}
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
		int oldBlockId = this.world.getBlockTypeIdAt(x, y, z);
		boolean res = this.nms.setBlockFast(this.world, x, y, z, block, (byte)data);
		if (this.nms.getBlockLightBlocking(oldBlockId) != this.nms.getBlockLightBlocking(block) || this.nms.getBlockLightEmission(oldBlockId) != this.nms.getBlockLightEmission(block)) {
			this.deferredBlocks.add(new DeferredBlock(x, y, z));
		}
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
		this.relightTask = Bukkit.getScheduler().runTaskTimer(this.plugin, this, 1L, 1L);
	}

	@Override
	public void run() {
		long now = System.nanoTime();
		int n = 1;
		while (this.deferredBlocks.peek() != null) {
			DeferredBlock db = this.deferredBlocks.poll();
			this.nms.recalculateBlockLighting(this.world, db.x, db.y, db.z);
			if (n++ % 1000 == 0) {
				// 2000000ns (2ms) is the max relight time per tick
				if (System.nanoTime() - now > 2000000) {
					break;
				}
			}
		}
		if (this.deferredBlocks.isEmpty()) {
			this.relightTask.cancel();
			this.relightTask = null;
			for (ChunkCoords cc : calculateChunks()) {
				this.world.refreshChunk(cc.x, cc.z);
			}
		}
	}

	public void setDeferredBufferSize(int size) {
		if (!this.deferredBlocks.isEmpty()) {
			throw new IllegalStateException("setDeferredBufferSize() called after block updates made");
		}
		this.deferredBlocks = new ArrayDeque<>(size);
	}

	private List<ChunkCoords> calculateChunks() {
		List<ChunkCoords> res = new ArrayList<>();
		if (this.blocksModified == 0) {
			return res;
		}
		int x1 = this.minX >> 4; int x2 = this.maxX >> 4;
		int z1 = this.minZ >> 4; int z2 = this.maxZ >> 4;
		for (int x = x1; x <= x2; x++) {
			for (int z = z1; z <= z2; z++) {
				res.add(new ChunkCoords(x, z));
			}
		}
		return res;
	}

	public static EvilEditEngine createEngine(Plugin plugin, org.bukkit.World world, Player player) {
		((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.clearUndo();
		return new CraftEvilEditEngine(plugin, world, player, false);
	}
	
	public static EvilEditEngine createEngineSilent(Plugin plugin, org.bukkit.World world, Player player) {
		return new CraftEvilEditEngine(plugin, world, player, true);
	}

	private class ChunkCoords {
		public final int x, z;
		public ChunkCoords(int x, int z) {
			this.x = x;
			this.z = z;
		}
	}

	private class DeferredBlock {
		public final int x, y, z;

		public DeferredBlock(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}