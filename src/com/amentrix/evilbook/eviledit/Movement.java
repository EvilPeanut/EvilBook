package com.amentrix.evilbook.eviledit;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.main.Rank;
import com.amentrix.evilbook.minigame.MinigameType;

/**
 * EvilEdit movement methods
 * @author Reece Aaron Lecrivain
 */
class Movement {
	static void passPlayerThroughOpposingWall(Player player) {
		if (EvilBook.isInSurvival(player) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage(ChatColor.GRAY + "You can't use this command in survival");
		} else if (EvilBook.isInMinigame(player, MinigameType.SKYBLOCK) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7EvilEdit can't be used in skyblock survival");
		} else {
			Location pos = player.getLocation(), oldLocation = player.getLocation();	
			
			Vector vector = new Vector();
			double rotX = player.getLocation().getYaw();
			double rotY = player.getLocation().getPitch();
			double xz = Math.cos(Math.toRadians(rotY));
			vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
			vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
			
			boolean hasHitSolid = player.getLocation().getBlock().getType().isSolid();
			
			for (int offset = 0; offset < 256; offset++) {
				if (!pos.add(vector).getBlock().getType().isSolid() && hasHitSolid) {
					player.teleport(pos);
					player.sendMessage(ChatColor.GRAY + "Passed through " + (int)Math.ceil(oldLocation.distance(pos)) + " blocks");
					return;
				} else if (pos.add(vector).getBlock().getType().isSolid()) {
					hasHitSolid = true;
				}
			}
			
			player.sendMessage(ChatColor.GRAY + "There are no valid obstacles to pass trough");
		}
	}

	static void ascendPlayerToCeiling(Player player) {
		if (EvilBook.isInSurvival(player) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage(ChatColor.GRAY + "You can't use this command in survival");
		} else if (EvilBook.isInMinigame(player, MinigameType.SKYBLOCK) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7EvilEdit can't be used in skyblock survival");
		} else {
			Location pos = player.getLocation();
			int x = pos.getBlockX();
			int initialY = Math.max(0, pos.getBlockY());
			int y = Math.max(0, pos.getBlockY() + 2);
			int z = pos.getBlockZ();
			World world = pos.getWorld();
			if (world.getBlockAt(x, y, z).getType() != Material.AIR) {
				player.sendMessage(ChatColor.GRAY + "There are no valid ceilings to ascend to");
				return;
			}
			while (y <= world.getMaxHeight()) {
				if (world.getBlockAt(x, y, z).getType() != Material.AIR) {
					int platformY = Math.max(initialY, y - 2);
					player.sendMessage(ChatColor.GRAY + "Ascended " + (platformY - player.getLocation().getBlockY()) + " blocks to ceiling");
					player.teleport(new Location(world, pos.getX(), platformY, pos.getZ()));
					return;
				}
				y++;
			}
		}
	}

	static void descendPlayer(Player player) {
		if (EvilBook.isInSurvival(player) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage(ChatColor.GRAY + "You can't use this command in survival");
		} else if (EvilBook.isInMinigame(player, MinigameType.SKYBLOCK) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7EvilEdit can't be used in skyblock survival");
		} else {
			int y = player.getLocation().getBlockY();
			boolean hasReachedSolid = false;
			while (hasReachedSolid == false || player.getWorld().getBlockAt(player.getLocation().getBlockX(), y, player.getLocation().getBlockZ()).getType() != Material.AIR || player.getWorld().getBlockAt(player.getLocation().getBlockX(), y - 1, player.getLocation().getBlockZ()).getType() == Material.AIR) {
				if (y == 0) {
					player.sendMessage(ChatColor.GRAY + "There are no valid blocks to descend to");
					return;
				}
				if (player.getWorld().getBlockAt(player.getLocation().getBlockX(), y, player.getLocation().getBlockZ()).getType() != Material.AIR) hasReachedSolid = true;
				y--;
			}
			player.sendMessage(ChatColor.GRAY + "Descended " + (player.getLocation().getBlockY() - y) + " blocks");
			Location freeLocation = player.getLocation();
			freeLocation.setY(y);
			player.teleport(freeLocation);
		}
	}

	static void ascendPlayer(Player player, Boolean displayMessages) {
		if (EvilBook.isInSurvival(player) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage(ChatColor.GRAY + "You can't use this command in survival");
		} else if (EvilBook.isInMinigame(player, MinigameType.SKYBLOCK) && !EvilBook.getProfile(player).rank.isHigher(Rank.TYCOON)) {
			player.sendMessage("§7EvilEdit can't be used in skyblock survival");
		} else {
			int y = player.getLocation().getBlockY();
			boolean hasReachedSolid = false;
			while (hasReachedSolid == false || player.getWorld().getBlockAt(player.getLocation().getBlockX(), y, player.getLocation().getBlockZ()).getType() != Material.AIR) {
				if (y == player.getWorld().getMaxHeight() + 2) {
					if (displayMessages) player.sendMessage(ChatColor.GRAY + "There are no valid blocks to ascend to");
					return;
				}
				if (player.getWorld().getBlockAt(player.getLocation().getBlockX(), y, player.getLocation().getBlockZ()).getType() != Material.AIR) hasReachedSolid = true;
				y++;
			}
			if (displayMessages) player.sendMessage(ChatColor.GRAY + "Ascended " + (y - player.getLocation().getBlockY()) + " blocks");
			Location freeLocation = player.getLocation();
			freeLocation.setY(y);
			player.teleport(freeLocation);
		}
	}
}
