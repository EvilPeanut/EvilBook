package com.amentrix.evilbook.nametag;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.entity.Player;

public class PacketPlayOut
{
	private Object packet;
	private static Method getHandle;
	private static Method sendPacket;
	private static Field playerConnection;
	private static String version = "";
	private static Class<?> packetType;

	PacketPlayOut(String name, String prefix, String suffix, Collection players, int paramInt) throws IllegalAccessException, InstantiationException, NoSuchFieldException
	{
		this.packet = packetType.newInstance();
		setField("a", name);
		setField("h", Integer.valueOf(paramInt));

		if ((paramInt == 0) || (paramInt == 2)) {
			setField("b", name);
			setField("c", prefix);
			setField("d", suffix);
			setField("g", players);
		}
		if (paramInt == 0)
			addAll(players);
	}

	PacketPlayOut(String name, Collection players, int paramInt) throws IllegalAccessException, InstantiationException, NoSuchFieldException
	{
		this.packet = packetType.newInstance();

		if ((players == null) || (players.isEmpty())) {
			players = new ArrayList();
		}

		setField("a", name);
		setField("h", Integer.valueOf(paramInt));
		addAll(players);
	}

	void sendToPlayer(Player bukkitPlayer) throws IllegalAccessException, InvocationTargetException
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

	private void addAll(Collection<?> col) throws NoSuchFieldException, IllegalAccessException {
		Field f = this.packet.getClass().getDeclaredField("g");
		f.setAccessible(true);
		((Collection)f.get(this.packet)).addAll(col);
	}

	static
	{
		try
		{
			version = org.bukkit.Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
			packetType = Class.forName("net.minecraft.server." + version + ".PacketPlayOutScoreboardTeam");

			Class typeCraftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
			Class typeNMSPlayer = Class.forName("net.minecraft.server." + version + ".EntityPlayer");
			Class typePlayerConnection = Class.forName("net.minecraft.server." + version + ".PlayerConnection");

			getHandle = typeCraftPlayer.getMethod("getHandle", new Class[0]);
			playerConnection = typeNMSPlayer.getField("playerConnection");
			sendPacket = typePlayerConnection.getMethod("sendPacket", new Class[] { Class.forName("net.minecraft.server." + version + ".Packet") });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}