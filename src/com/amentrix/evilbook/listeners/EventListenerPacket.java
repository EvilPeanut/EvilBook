package com.amentrix.evilbook.listeners;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.Listener;

import com.amentrix.evilbook.achievement.Achievement;
import com.amentrix.evilbook.main.DynamicSignManager;
import com.amentrix.evilbook.main.EvilBook;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;

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
				BlockPosition blockLocation = packet.getBlockPositionModifier().read(0);
				Location signLocation = new Location(event.getPlayer().getWorld(), blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
				String[] lines = packet.getStringArrays().read(0);
				if (signLocation.getBlock().getState() instanceof Sign) {
					Sign sign = (Sign) signLocation.getBlock().getState();
					sign.setLine(0, lines[0]);
					sign.setLine(1, lines[1]);
					sign.setLine(2, lines[2]);
					sign.setLine(3, lines[3]);
					formatSignText(sign);
					if (DynamicSignManager.isDynamicSign(sign)) {
						DynamicSignManager.formatDynamicSign(sign);
						EvilBook.getProfile(event.getPlayer()).addAchievement(Achievement.GLOBAL_DYNAMIC_SIGN);
					}
					sign.update();
					EvilBook.lbConsumer.queueSignPlace(event.getPlayer().getName(), sign);
				}
			}
		});
	}
	
	static void formatSignText(Sign sign) {
		for (int i = 0; i < 4; i++) sign.setLine(i, EvilBook.toFormattedString(sign.getLine(i)));
	}
}