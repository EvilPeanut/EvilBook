package com.amentrix.evilbook.nametag;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Player;

/**
 * PacketPlayOut instance
 * Based on NameTagEdit by wacossusca34
 * @author Reece Aaron Lecrivain
 */
class PacketPlayOut
{
	Object packet;
	private static Method getHandle;
	private static Method sendPacket;
	private static Field playerConnection;
	private static Class<?> packetType;

	PacketPlayOut(String name, String prefix, String suffix, Collection<?> players, int paramInt)
			throws IllegalAccessException, InstantiationException, NoSuchFieldException
			{
		this.packet = packetType.newInstance();
		setField("a", name);
		setField("f", Integer.valueOf(paramInt));

		if ((paramInt == 0) || (paramInt == 2)) {
			setField("b", name);
			setField("c", prefix);
			setField("d", suffix);
			setField("g", Integer.valueOf(1));
		}
		if (paramInt == 0)
			addAll(players);
			}

	PacketPlayOut(String name, Collection<?> players, int paramInt)
			throws IllegalAccessException, InstantiationException, NoSuchFieldException
			{
		this.packet = packetType.newInstance();

		if ((paramInt != 3) && (paramInt != 4)) {
			throw new IllegalArgumentException("Method must be join or leave for player constructor");
		}

		if ((players == null) || (players.isEmpty())) {
			players = new ArrayList<>();
		}

		setField("a", name);
		setField("f", Integer.valueOf(paramInt));
		addAll(players);
			}

	void sendToPlayer(Player bukkitPlayer)
			throws IllegalAccessException, InvocationTargetException
			{
		Object player = getHandle.invoke(bukkitPlayer, new Object[0]);

		Object connection = playerConnection.get(player);

		sendPacket.invoke(connection, new Object[] { this.packet });
			}

	private void setField(String field, Object value) throws NoSuchFieldException, IllegalAccessException
	{
		Field f = this.packet.getClass().getDeclaredField(field);
		f.setAccessible(true);
		f.set(this.packet, value);
	}

	private void addAll(Collection col) throws NoSuchFieldException, IllegalAccessException
	{
		Field f = this.packet.getClass().getDeclaredField("e");
		f.setAccessible(true);
		((Collection)f.get(this.packet)).addAll(col);
	}

	static
	{
		try
		{
			packetType = net.minecraft.server.v1_7_R3.PacketPlayOutScoreboardTeam.class;

			Class<?> typeCraftPlayer = org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer.class;
			Class<?> typeNMSPlayer = net.minecraft.server.v1_7_R3.EntityPlayer.class;
			Class<?> typePlayerConnection = net.minecraft.server.v1_7_R3.PlayerConnection.class;

			getHandle = typeCraftPlayer.getMethod("getHandle", new Class[0]);
			playerConnection = typeNMSPlayer.getField("playerConnection");
			sendPacket = typePlayerConnection.getMethod("sendPacket", new Class[] { net.minecraft.server.v1_7_R3.Packet.class });
		}
		catch (Exception e) {
			System.out.println("Failed to setup reflection for Packet209Mod!");
			e.printStackTrace();
		}
	}
}