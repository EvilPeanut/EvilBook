package com.amentrix.evilbook.listeners;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.Listener;

import com.amentrix.evilbook.achievement.Achievement;
import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.utils.SignUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

/**
 * Packet event listener
 * @author Reece Aaron Lecrivain
 */
public class EventListenerPacket implements Listener {
	public static void registerCommandBlockPacketReceiver(final EvilBook evilbook)
	{
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(evilbook, new PacketType[] { PacketType.Play.Client.CUSTOM_PAYLOAD })
		{
			@Override
			public void onPacketReceiving(PacketEvent event)
			{
				PacketContainer packet = event.getPacket();
				if (packet.getStrings().size() >= 1 && packet.getStrings().read(0).equals("MC|AdvCdm")) {
					try {
						String cmd = EvilBook.getCommandBlockCommand(packet);
						switch (cmd) {
						case "say":
							return;
						case "broadcast":
							return;
						case "tellrawr":
							if (EvilBook.isInSurvival(event.getPlayer())) {
								event.getPlayer().sendMessage("§cThis command is blocked in survival");
								event.setCancelled(true);
							}
							return;
						case "setblock":
							if (EvilBook.isInSurvival(event.getPlayer())) {
								event.getPlayer().sendMessage("§cThis command is blocked in survival");
								event.setCancelled(true);
							}
							return;
						case "summon":
							if (EvilBook.isInSurvival(event.getPlayer())) {
								event.getPlayer().sendMessage("§cThis command is blocked in survival");
								event.setCancelled(true);
							}
							return;
						case "testfor":
							return;
						case "testforblock":
							return;
						case "scoreboard":
							return;
						case "effect":
							if (EvilBook.isInSurvival(event.getPlayer())) {
								event.getPlayer().sendMessage("§cThis command is blocked in survival");
								event.setCancelled(true);
							}
							return;
						case "me":
							return;
						case "clear":
							if (EvilBook.isInSurvival(event.getPlayer())) {
								event.getPlayer().sendMessage("§cThis command is blocked in survival");
								event.setCancelled(true);
							}
							return;
						case "tell":
							return;
						case "toggledownfall":
							if (EvilBook.isInSurvival(event.getPlayer())) {
								event.getPlayer().sendMessage("§cThis command is blocked in survival");
								event.setCancelled(true);
							}
							return;
						case "weather":
							if (EvilBook.isInSurvival(event.getPlayer())) {
								event.getPlayer().sendMessage("§cThis command is blocked in survival");
								event.setCancelled(true);
							}
							return;
						case "time":
							if (EvilBook.isInSurvival(event.getPlayer())) {
								event.getPlayer().sendMessage("§cThis command is blocked in survival");
								event.setCancelled(true);
							}
							return;
						default:
							break;
						}
						event.getPlayer().sendMessage("§cThis command block command is blocked");
						event.setCancelled(true);
					} catch (Exception e) {
						event.getPlayer().sendMessage("§cPlease enter a valid command block command");
						event.setCancelled(true);
					}
				}
			}
		});
	}

	public static void registerSignUpdatePacketReceiver(final EvilBook evilbook)
	{
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(evilbook, new PacketType[] { PacketType.Play.Client.UPDATE_SIGN })
		{
			@Override
			public void onPacketReceiving(PacketEvent event)
			{
				PacketContainer packet = event.getPacket();
				String[] lines = packet.getStringArrays().read(0);
				Location signLocation = new Location(event.getPlayer().getWorld(), packet.getIntegers().read(0), packet.getIntegers().read(1), packet.getIntegers().read(2));
				if (signLocation.getBlock().getState() instanceof Sign) {
					Sign sign = (Sign) signLocation.getBlock().getState();
					sign.setLine(0, lines[0]);
					sign.setLine(1, lines[1]);
					sign.setLine(2, lines[2]);
					sign.setLine(3, lines[3]);
					SignUtils.formatSignText(sign);
					if (SignUtils.isDynamicSign(sign)) {
						SignUtils.formatDynamicSign(sign);
						EvilBook.getProfile(event.getPlayer()).addAchievement(Achievement.GLOBAL_DYNAMIC_SIGN);
					}
					sign.update();
					EvilBook.lbConsumer.queueSignPlace(event.getPlayer().getName(), sign);
				}
			}
		});
	}
}