package com.amentrix.evilbook.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.Listener;

import com.amentrix.evilbook.main.DynamicSign;
import com.amentrix.evilbook.main.EvilBook;
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
							//TODO: Add but disallow command block to change blocks in protected regions player doesnt own
							//case "setblock":
							//if (evilbook.isInProtectedRegion(new Location(event.get), event.getPlayer())) {

							//} else {
							//return;
							//}
						case "summon":
							if (EvilBook.isInSurvival(event.getPlayer())) {
								event.getPlayer().sendMessage("§cThis command is blocked in survival");
								event.setCancelled(true);
							}
							return;
						case "testfor":
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
				Integer x = packet.getIntegers().read(0);
				Integer y = packet.getIntegers().read(1);
				Integer z = packet.getIntegers().read(2);
				if ((x == null) || (y == null) || (z == null)) return;
				Sign sign = (Sign)new Location(event.getPlayer().getWorld(), x.intValue(), y.intValue(), z.intValue()).getBlock().getState();
				for (int i = 0; i < 4; i++) sign.setLine(i, EvilBook.toFormattedString(lines[i]));
				if (sign.getLine(0).toLowerCase().contains("[time]") || sign.getLine(1).toLowerCase().contains("[time]") || sign.getLine(2).toLowerCase().contains("[time]") || sign.getLine(3).toLowerCase().contains("[time]") ||
						sign.getLine(0).toLowerCase().contains("[weather]") || sign.getLine(1).toLowerCase().contains("[weather]") || sign.getLine(2).toLowerCase().contains("[weather]") || sign.getLine(3).toLowerCase().contains("[weather]") ||
						sign.getLine(0).toLowerCase().contains("[online]") || sign.getLine(1).toLowerCase().contains("[online]") || sign.getLine(2).toLowerCase().contains("[online]") || sign.getLine(3).toLowerCase().contains("[online]")) {
					String[] text = new String[4];
					text[0] = EvilBook.replaceAllIgnoreCase(sign.getLine(0), "[Time]", "[time]");
					text[1] = EvilBook.replaceAllIgnoreCase(sign.getLine(1), "[Time]", "[time]");
					text[2] = EvilBook.replaceAllIgnoreCase(sign.getLine(2), "[Time]", "[time]");
					text[3] = EvilBook.replaceAllIgnoreCase(sign.getLine(3), "[Time]", "[time]");
					text[0] = EvilBook.replaceAllIgnoreCase(text[0], "[Weather]", "[weather]");
					text[1] = EvilBook.replaceAllIgnoreCase(text[1], "[Weather]", "[weather]");
					text[2] = EvilBook.replaceAllIgnoreCase(text[2], "[Weather]", "[weather]");
					text[3] = EvilBook.replaceAllIgnoreCase(text[3], "[Weather]", "[weather]");
					text[0] = EvilBook.replaceAllIgnoreCase(text[0], "[Online]", "[online]");
					text[1] = EvilBook.replaceAllIgnoreCase(text[1], "[Online]", "[online]");
					text[2] = EvilBook.replaceAllIgnoreCase(text[2], "[Online]", "[online]");
					text[3] = EvilBook.replaceAllIgnoreCase(text[3], "[Online]", "[online]");
					DynamicSign dynamicSign = new DynamicSign(sign.getBlock().getLocation(), text);
					EvilBook.dynamicSignList.add(dynamicSign);
					String time = EvilBook.getTime(sign.getBlock().getWorld());
					String weather = EvilBook.getWeather(sign.getBlock());
					sign.setLine(0, text[0].replace("[time]", time).replace("[weather]", weather).replace("[online]", Integer.toString(Bukkit.getServer().getOnlinePlayers().length)));
					sign.setLine(1, text[1].replace("[time]", time).replace("[weather]", weather).replace("[online]", Integer.toString(Bukkit.getServer().getOnlinePlayers().length)));
					sign.setLine(2, text[2].replace("[time]", time).replace("[weather]", weather).replace("[online]", Integer.toString(Bukkit.getServer().getOnlinePlayers().length)));
					sign.setLine(3, text[3].replace("[time]", time).replace("[weather]", weather).replace("[online]", Integer.toString(Bukkit.getServer().getOnlinePlayers().length)));
				}
				sign.update();
			}
		});
	}
}
