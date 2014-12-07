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