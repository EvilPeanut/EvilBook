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
import com.amentrix.evilbook.statistics.Statistic;
import com.amentrix.evilbook.statistics.Statistics;

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
		if (nms == null) {
			throw new IllegalStateException("NMS abstraction API is not available");
		}
	}

	@Override
	public int getBlocksChanged() {
		return blocksModified;
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
		if (y < 0 || y >= world.getMaxHeight()) return false;
		//
		BlockState oldBlockState = new Location(world, x, y, z).getBlock().getState();
		// Clipboard
		if (!silent) ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.appendUndo(oldBlockState);
		// Set the block to its new state
		minX = Math.min(minX, x);
		minZ = Math.min(minZ, z);
		maxX = Math.max(maxX, x);
		maxZ = Math.max(maxZ, z);
		blocksModified++;
		int oldBlockId = world.getBlockTypeIdAt(x, y, z);
		boolean res = nms.setBlockFast(world, x, y, z, block, (byte)data);
		if (nms.getBlockLightBlocking(oldBlockId) != nms.getBlockLightBlocking(block) || nms.getBlockLightEmission(oldBlockId) != nms.getBlockLightEmission(block)) {
			deferredBlocks.add(new DeferredBlock(x, y, z));
		}
		// Logging
		EvilBook.lbConsumer.queueBlockReplace(player.getName(), oldBlockState, new Location(world, x, y, z).getBlock().getState());
		// Return if it was set
		return res;
	}
	
	@Override
	public boolean setBlock(Location loc, int block, int data) {
		// Position validation
		if (loc.getBlockY() < 0 || loc.getBlockY() >= world.getMaxHeight()) return false;
		//
		BlockState oldBlockState = loc.getBlock().getState();
		// Clipboard
		if (!silent) ((PlayerProfileAdmin)EvilBook.getProfile(player)).clipboard.appendUndo(oldBlockState);
		// Set the block to its new state
		minX = Math.min(minX, loc.getBlockX());
		minZ = Math.min(minZ, loc.getBlockZ());
		maxX = Math.max(maxX, loc.getBlockX());
		maxZ = Math.max(maxZ, loc.getBlockZ());
		blocksModified++;
		int oldBlockId = oldBlockState.getTypeId();
		boolean res = nms.setBlockFast(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), block, (byte)data);
		if (nms.getBlockLightBlocking(oldBlockId) != nms.getBlockLightBlocking(block) || nms.getBlockLightEmission(oldBlockId) != nms.getBlockLightEmission(block)) {
			deferredBlocks.add(new DeferredBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
		}
		// Logging
		EvilBook.lbConsumer.queueBlockReplace(player.getName(), oldBlockState, new Location(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getBlock().getState());
		// Return if it was set
		return res;
	}

	@Override
	public void notifyClients(Statistic statistic) {
		// Statistics
		Statistics.incrementStatistic(statistic, blocksModified);
		// Do relighting
		relightTask = Bukkit.getScheduler().runTaskTimer(plugin, this, 1L, 1L);
	}

	@Override
	public void run() {
		long now = System.nanoTime();
		int n = 1;
		while (deferredBlocks.peek() != null) {
			DeferredBlock db = deferredBlocks.poll();
			nms.recalculateBlockLighting(world, db.x, db.y, db.z);
			if (n++ % 1000 == 0) {
				// 2000000ns (2ms) is the max relight time per tick
				if (System.nanoTime() - now > 2000000) {
					break;
				}
			}
		}
		if (deferredBlocks.isEmpty()) {
			relightTask.cancel();
			relightTask = null;
			for (ChunkCoords cc : calculateChunks()) {
				world.refreshChunk(cc.x, cc.z);
			}
		}
	}

	public void setDeferredBufferSize(int size) {
		if (!deferredBlocks.isEmpty()) {
			throw new IllegalStateException("setDeferredBufferSize() called after block updates made");
		}
		deferredBlocks = new ArrayDeque<>(size);
	}

	private List<ChunkCoords> calculateChunks() {
		List<ChunkCoords> res = new ArrayList<>();
		if (blocksModified == 0) {
			return res;
		}
		int x1 = minX >> 4; int x2 = maxX >> 4;
		int z1 = minZ >> 4; int z2 = maxZ >> 4;
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