package com.amentrix.evilbook.nametag;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.entity.Player;

class PacketPlayOut
{
	Object packet;
	private static Method getHandle;
	private static Method sendPacket;
	private static Field playerConnection;
	private static Class<?> packetType;

	PacketPlayOut(String name, String prefix, String suffix, Collection players, int paramInt)
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

	PacketPlayOut(String name, Collection players, int paramInt)
			throws IllegalAccessException, InstantiationException, NoSuchFieldException
			{
		this.packet = packetType.newInstance();

		if ((paramInt != 3) && (paramInt != 4)) {
			throw new IllegalArgumentException("Method must be join or leave for player constructor");
		}

		if ((players == null) || (players.isEmpty())) {
			players = new ArrayList();
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

	private void addAll(Collection<?> col)
			throws NoSuchFieldException, IllegalAccessException
			{
		Field f = this.packet.getClass().getDeclaredField("e");
		f.setAccessible(true);
		((Collection)f.get(this.packet)).addAll(col);
			}

	private static String getCraftPlayerClasspath() {
		return "org.bukkit.craftbukkit." + PackageChecker.getVersion() + ".entity.CraftPlayer";
	}

	private static String getPlayerConnectionClasspath()
	{
		return "net.minecraft.server." + PackageChecker.getVersion() + ".PlayerConnection";
	}

	private static String getNMSPlayerClasspath()
	{
		return "net.minecraft.server." + PackageChecker.getVersion() + ".EntityPlayer";
	}

	private static String getPacketClasspath()
	{
		return "net.minecraft.server." + PackageChecker.getVersion() + ".Packet";
	}

	private static String getPacketTeamClasspath()
	{
		if ((Integer.valueOf(PackageChecker.getVersion().split("_")[1]).intValue() < 7) && (Integer.valueOf(PackageChecker.getVersion().toLowerCase().split("_")[0].replace("v", "")).intValue() == 1))
		{
			return "net.minecraft.server." + PackageChecker.getVersion() + ".Packet209SetScoreboardTeam";
		}

		return "net.minecraft.server." + PackageChecker.getVersion() + ".PacketPlayOutScoreboardTeam";
	}

	static
	{
		try
		{
			packetType = Class.forName(getPacketTeamClasspath());

			Class typeCraftPlayer = Class.forName(getCraftPlayerClasspath());
			Class typeNMSPlayer = Class.forName(getNMSPlayerClasspath());
			Class typePlayerConnection = Class.forName(getPlayerConnectionClasspath());

			getHandle = typeCraftPlayer.getMethod("getHandle", new Class[0]);
			playerConnection = typeNMSPlayer.getField("playerConnection");
			sendPacket = typePlayerConnection.getMethod("sendPacket", new Class[] { Class.forName(getPacketClasspath()) });
		}
		catch (Exception e) {
			System.out.println("Failed to setup reflection for Packet209Mod!");
			e.printStackTrace();
		}
	}
}