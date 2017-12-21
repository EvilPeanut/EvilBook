package com.amentrix.evilbook.main;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.ChatClickable;
import net.minecraft.server.v1_12_R1.ChatMessage;
import net.minecraft.server.v1_12_R1.ChatModifier;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.ChatClickable.EnumClickAction;
import net.minecraft.server.v1_12_R1.ChatHoverable;
import net.minecraft.server.v1_12_R1.ChatHoverable.EnumHoverAction;

/**
 * Chat extensions
 * @author Reece Aaron Lecrivain
 */
public class ChatExtensions {
	public static void broadcastPlayerMessage(String playerName, TextComponent message) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			PlayerProfile profile = EvilBook.getProfile(player);
			if (!profile.isMuted(playerName)) {
				profile.getPlayer().spigot().sendMessage(message);
			}
		}
	}
	
	public static void sendClickableMessage(Player player, String message, EnumClickAction action, String data) {
        IChatBaseComponent base;
        base = new ChatMessage(message);
        ChatModifier modifier = new ChatModifier();
        modifier.setChatClickable(new ChatClickable(action, data));
        base.setChatModifier(modifier);
        MinecraftServer.getServer().getPlayerList().getPlayer(player.getName()).sendMessage(base);
	}
	
	public static void sendHoverableMessage(Player player, String message, EnumHoverAction action, String data) {
        IChatBaseComponent base;
        base = new ChatMessage(message);
        ChatModifier modifier = new ChatModifier();
        modifier.setChatHoverable(new ChatHoverable(action, new ChatMessage(data)));
        base.setChatModifier(modifier);
        MinecraftServer.getServer().getPlayerList().getPlayer(player.getName()).sendMessage(base);
	}

	public static void sendAdminRequiredMessage(Player player) {
		ChatExtensions.sendClickableMessage(player, ChatColor.LIGHT_PURPLE + "Please type " + ChatColor.GOLD + "/admin " + ChatColor.LIGHT_PURPLE + "to learn how to become admin", EnumClickAction.SUGGEST_COMMAND, "/admin");
	}
	
	public static void sendCommandHelpMessage(Player player, String usage) {
		player.sendMessage(ChatColor.DARK_PURPLE + "§oIncorrect command usage");
		sendClickableMessage(player, ChatColor.LIGHT_PURPLE + usage, EnumClickAction.SUGGEST_COMMAND, usage);
	}
	
	public static void sendCommandHelpMessage(Player player, List<String> usages) {
		player.sendMessage(ChatColor.DARK_PURPLE + "§oIncorrect command usage");
		for (String usage : usages) {
			sendClickableMessage(player, ChatColor.LIGHT_PURPLE + usage, EnumClickAction.SUGGEST_COMMAND, usage);
		}
	}
	
	static void sendCommandHelpMessage(CommandSender sender, String usage) {
		sender.sendMessage(ChatColor.DARK_PURPLE + "§oIncorrect command usage");
		if (sender instanceof Player) {
			sendClickableMessage((Player)sender, ChatColor.LIGHT_PURPLE + usage, EnumClickAction.SUGGEST_COMMAND, usage);
		} else {
			sender.sendMessage(ChatColor.LIGHT_PURPLE + usage);
		}
	}
	
	static void sendCommandHelpMessage(CommandSender sender, List<String> usages) {
		sender.sendMessage(ChatColor.DARK_PURPLE + "§oIncorrect command usage");
		for (String usage : usages) {
			if (sender instanceof Player) {
				sendClickableMessage((Player)sender, ChatColor.LIGHT_PURPLE + usage, EnumClickAction.SUGGEST_COMMAND, usage);
			} else {
				sender.sendMessage(ChatColor.LIGHT_PURPLE + usage);
			}
		}
	}
}
