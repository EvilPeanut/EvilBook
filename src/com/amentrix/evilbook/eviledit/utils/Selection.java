package com.amentrix.evilbook.eviledit.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.Rank;

/**
 * EvilEdit selection instance
 * @author Reece Aaron Lecrivain
 */
public class Selection {
	private Player player;
	private Location locationA, locationB;
	
	public Selection(Player player) {
		this.player = player;
		this.locationA = EvilBook.getProfile(player).actionLocationA;
		this.locationB = EvilBook.getProfile(player).actionLocationB;
	}
	
	//
	// Get the selection bounds
	//
	public int getTopXBlock() {return this.locationA.getBlockX() < this.locationB.getBlockX() ? this.locationB.getBlockX() : this.locationA.getBlockX();}
	public int getBottomXBlock() {return this.locationA.getBlockX() > this.locationB.getBlockX() ? this.locationB.getBlockX() : this.locationA.getBlockX();}
	public int getTopYBlock() {return Math.min(this.locationA.getBlockY() < this.locationB.getBlockY() ? this.locationB.getBlockY() : this.locationA.getBlockY(), this.locationA.getWorld().getMaxHeight());}
	public int getBottomYBlock() {return Math.max(this.locationA.getBlockY() > this.locationB.getBlockY() ? this.locationB.getBlockY() : this.locationA.getBlockY(), 0);}
	public int getTopZBlock() {return this.locationA.getBlockZ() < this.locationB.getBlockZ() ? this.locationB.getBlockZ() : this.locationA.getBlockZ();}
	public int getBottomZBlock() {return this.locationA.getBlockZ() > this.locationB.getBlockZ() ? this.locationB.getBlockZ() : this.locationA.getBlockZ();}
	
	/**
	 * Return the number of blocks in an area
	 * @return Number of blocks in the area
	 */
	public int getVolume() {
		return (((getTopXBlock() - getBottomXBlock()) + 1) * ((getTopYBlock() - getBottomYBlock()) + 1) * ((getTopZBlock() - getBottomZBlock()) + 1));
	}

	/**
	 * Return if the selection is valid
	 * @return If the selection is valid
	 */
	public Boolean isValid() {
		if (this.locationA == null) {
			this.player.sendMessage("§7Please select the first selection location");
			return false;
		} else if (this.locationB == null) {
			this.player.sendMessage("§7Please select the second selection location");
			return false;
		} else if (this.locationA.getWorld() != this.locationB.getWorld()) {
			this.player.sendMessage("§7Please select two locations in the same world");
			return false;
		} else if (getVolume() > EvilBook.getProfile(this.player).rank.getEvilEditAreaLimit()) {
			this.player.sendMessage("§7You can only set a selections biome of up to an area of " + EvilBook.getProfile(this.player).rank.getEvilEditAreaLimit() + " blocks");
			if (!EvilBook.getProfile(this.player).rank.isHigher(Rank.Elite)) this.player.sendMessage("§7Rank-up to lift this limit");
			return false;
		}
		return true;
	}
	
	/**
	 * Get a block from co-ordinates in the selection world
	 * @param x The X co-ordinate of the block
	 * @param y The Y co-ordinate of the block
	 * @param z The Z co-ordinate of the block
	 * @return The block
	 */
	public Block getBlock(int x, int y, int z) {
		return this.locationA.getWorld().getBlockAt(x, y, z);
	}
	
	/**
	 * Get the selection world
	 * @return The selection world
	 */
	public World getWorld() {
		return this.locationA.getWorld();
	}
}
