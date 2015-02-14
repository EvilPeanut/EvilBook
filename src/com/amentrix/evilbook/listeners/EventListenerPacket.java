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
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

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
				WrappedChatComponent[] lines = packet.getChatComponentArrays().read(0);
				BlockPosition blockLocation = packet.getBlockPositionModifier().read(0);
				Location signLocation = new Location(event.getPlayer().getWorld(), blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
				if (signLocation.getBlock().getState() instanceof Sign) {
					Sign sign = (Sign) signLocation.getBlock().getState();
					sign.setLine(0, lines[0].getJson().substring(1, lines[0].getJson().length() - 1));
					sign.setLine(1, lines[1].getJson().substring(1, lines[1].getJson().length() - 1));
					sign.setLine(2, lines[2].getJson().substring(1, lines[2].getJson().length() - 1));
					sign.setLine(3, lines[3].getJson().substring(1, lines[3].getJson().length() - 1));
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