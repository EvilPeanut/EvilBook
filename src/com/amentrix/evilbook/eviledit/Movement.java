package com.amentrix.evilbook.eviledit;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * EvilEdit movement methods
 * @author Reece Aaron Lecrivain
 */
public class Movement {
	public static void passPlayerThroughOpposingWall(Player player) {
		Location pos = player.getLocation();
		float yaw = pos.getYaw() / 90;
	    yaw = Math.round(yaw);
		int offset = 1;
		while (offset <= 32) {
		    if (yaw == -4 || yaw == 0 || yaw == 4) { // +z
				if (!pos.add(0, 0, offset / 2).getBlock().getType().isSolid()) {
					player.teleport(pos.add(0, 0, offset + 0.5));
					player.sendMessage(ChatColor.GRAY + "Passed through " + offset + " blocks");
					return;
				}
		    } else if (yaw == -1 || yaw == 3) { // +x
				if (!pos.add(offset / 2, 0, 0).getBlock().getType().isSolid()) {
					player.teleport(pos.add(offset + 0.5, 0, 0));
					player.sendMessage(ChatColor.GRAY + "Passed through " + offset + " blocks");
					return;
				}
		    } else if (yaw == -2 || yaw == 2) { // -z
				if (!pos.subtract(0, 0, offset / 2).getBlock().getType().isSolid()) {
					player.teleport(pos.subtract(0, 0, offset + 0.5));
					player.sendMessage(ChatColor.GRAY + "Passed through " + offset + " blocks");
					return;
				}
		    } else { // -x
				if (!pos.subtract(offset / 2, 0, 0).getBlock().getType().isSolid()) {
					player.teleport(pos.subtract(offset + 0.5, 0, 0));
					player.sendMessage(ChatColor.GRAY + "Passed through " + offset + " blocks");
					return;
				}
		    }
			offset++;
		}
		player.sendMessage(ChatColor.GRAY + "There are no valid obstacles to pass trough");
	}
	
	public static void ascendPlayerToCeiling(Player player) {
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

	public static void descendPlayer(Player player) {
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
	
	public static void ascendPlayer(Player player, Boolean displayMessages) {
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
