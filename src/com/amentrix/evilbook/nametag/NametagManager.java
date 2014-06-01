package com.amentrix.evilbook.nametag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

class NametagManager
{
	private static List<Integer> list = new ArrayList();

	private static HashMap<TeamInfo, List<String>> teams = new HashMap();

	private static void addToTeam(TeamInfo team, String player) {
		removeFromTeam(player);
		List list = teams.get(team);
		if (list != null) {
			list.add(player);
			Player p = Bukkit.getPlayerExact(player);
			if (p != null) {
				sendPacketsAddToTeam(team, p);
			} else {
				OfflinePlayer p2 = Bukkit.getOfflinePlayer(player);
				sendPacketsAddToTeam(team, p2);
			}
		}
	}

	private static void register(TeamInfo team) {
		teams.put(team, new ArrayList());
		sendPacketsAddTeam(team);
	}

	private static void removeTeam(TeamInfo team)
	{
		List list = teams.get(team);
		if (list != null) {
			for (String p : (String[])list.toArray(new String[list.size()])) {
				Player player = Bukkit.getPlayerExact(p);
				if (player != null) {
					sendPacketsRemoveFromTeam(team, player);
				} else {
					OfflinePlayer p2 = Bukkit.getOfflinePlayer(p);
					sendPacketsRemoveFromTeam(team, p2);
				}
			}
			sendPacketsRemoveTeam(team);
			teams.remove(team);
		}
	}

	private static TeamInfo removeFromTeam(String player)
	{
		for (TeamInfo team : teams.keySet().toArray(new TeamInfo[teams.size()])) {
			List list = teams.get(team);
			for (String p : (String[])list.toArray(new String[list.size()])) {
				if (p.equals(player)) {
					Player pl = Bukkit.getPlayerExact(player);
					if (pl != null) {
						sendPacketsRemoveFromTeam(team, pl);
					} else {
						OfflinePlayer p2 = Bukkit.getOfflinePlayer(p);
						sendPacketsRemoveFromTeam(team, p2);
					}
					list.remove(p);

					return team;
				}
			}
		}
		return null;
	}

	private static TeamInfo getTeam(String name)
	{
		for (TeamInfo team : teams.keySet().toArray(new TeamInfo[teams.size()])) {
			if (team.getName().equals(name)) {
				return team;
			}
		}
		return null;
	}

	private static TeamInfo[] getTeams() {
		TeamInfo[] list = new TeamInfo[teams.size()];
		int at = 0;
		for (TeamInfo team : teams.keySet().toArray(new TeamInfo[teams.size()])) {
			list[at] = team;
			at++;
		}
		return list;
	}

	private static String[] getTeamPlayers(TeamInfo team) {
		List list = teams.get(team);
		if (list != null)
		{
			return (String[])list.toArray(new String[list.size()]);
		}
		return new String[0];
	}

	static void load()
	{
		for (TeamInfo t : getTeams()) {
			int entry = -1;
			try {
				entry = Integer.parseInt(t.getName());
			}
			catch (Exception e) {
				//
			}
			if (entry != -1)
				list.add(Integer.valueOf(entry));
		}
	}

	static void update(String player, String prefix, String suffix)
	{
		if ((prefix == null) || (prefix.isEmpty())) {
			prefix = getPrefix(player);
		}

		if ((suffix == null) || (suffix.isEmpty())) {
			suffix = getSuffix(player);
		}

		TeamInfo t = get(prefix, suffix);

		addToTeam(t, player);
	}

	static void overlap(String player, String prefix, String suffix)
	{
		if (prefix == null) {
			prefix = "";
		}
		if (suffix == null) {
			suffix = "";
		}

		TeamInfo t = get(prefix, suffix);

		addToTeam(t, player);
	}

	static void clear(String player)
	{
		removeFromTeam(player);
	}

	static String getPrefix(String player)
	{
		for (TeamInfo team : getTeams()) {
			for (String p : getTeamPlayers(team))
			{
				if (p.equals(player)) {
					return team.getPrefix();
				}
			}
		}
		return "";
	}

	static String getSuffix(String player)
	{
		for (TeamInfo team : getTeams()) {
			for (String p : getTeamPlayers(team)) {
				if (p.equals(player)) {
					return team.getSuffix();
				}
			}
		}
		return "";
	}

	static String getFormattedName(String player)
	{
		return getPrefix(player) + player + getSuffix(player);
	}

	private static TeamInfo declareTeam(String name, String prefix, String suffix)
	{
		if (getTeam(name) != null) {
			TeamInfo team = getTeam(name);
			removeTeam(team);
		}

		TeamInfo team = new TeamInfo(name);

		team.setPrefix(prefix);
		team.setSuffix(suffix);

		register(team);

		return team;
	}

	private static TeamInfo get(String prefix, String suffix)
	{
		update();

		Integer[] arr$ = list.toArray(new Integer[list.size()]); int len$ = arr$.length; for (int i$ = 0; i$ < len$; i$++) { int t = arr$[i$].intValue();

		if (getTeam("NTE" + t) != null) {
			TeamInfo team = getTeam("NTE" + t);

			if ((team.getSuffix().equals(suffix)) && (team.getPrefix().equals(prefix)))
			{
				return team;
			}
		}
		}

		return declareTeam("NTE" + nextName(), prefix, suffix);
	}

	private static int nextName()
	{
		int at = 0;
		boolean cont = true;
		while (cont) {
			cont = false;
			Integer[] arr$ = list.toArray(new Integer[list.size()]); int len$ = arr$.length; for (int i$ = 0; i$ < len$; i$++) { int t = arr$[i$].intValue();
			if (t == at) {
				at++;
				cont = true;
			}
			}
		}

		list.add(Integer.valueOf(at));
		return at;
	}

	private static void update()
	{
		for (TeamInfo team : getTeams()) {
			int entry = -1;
			try {
				entry = Integer.parseInt(team.getName());
			}
			catch (Exception e) {
				//
			}
			if ((entry != -1) && 
					(getTeamPlayers(team).length == 0)) {
				removeTeam(team);
				list.remove(new Integer(entry));
			}
		}
	}

	static void sendTeamsToPlayer(Player p)
	{
		try
		{
			for (TeamInfo team : getTeams()) {
				PacketPlayOut mod = new PacketPlayOut(team.getName(), team.getPrefix(), team.getSuffix(), new ArrayList(), 0);

				mod.sendToPlayer(p);
				mod = new PacketPlayOut(team.getName(), Arrays.asList(getTeamPlayers(team)), 3);

				mod.sendToPlayer(p);
			}
		} catch (Exception e) {
			System.out.println("Failed to send packet for player (Packet209SetScoreboardTeam) : ");

			e.printStackTrace();
		}
	}

	private static void sendPacketsAddTeam(TeamInfo team)
	{
		try
		{
			for (Player p : Bukkit.getOnlinePlayers()) {
				PacketPlayOut mod = new PacketPlayOut(team.getName(), team.getPrefix(), team.getSuffix(), new ArrayList(), 0);

				mod.sendToPlayer(p);
			}
		} catch (Exception e) {
			System.out.println("Failed to send packet for player (Packet209SetScoreboardTeam) : ");

			e.printStackTrace();
		}
	}

	private static void sendPacketsRemoveTeam(TeamInfo team)
	{
		boolean cont = false;
		for (TeamInfo t : getTeams()) {
			if (t == team) {
				cont = true;
			}
		}
		if (!cont) {
			return;
		}

		try
		{
			for (Player p : Bukkit.getOnlinePlayers()) {
				PacketPlayOut mod = new PacketPlayOut(team.getName(), team.getPrefix(), team.getSuffix(), new ArrayList(), 1);

				mod.sendToPlayer(p);
			}
		} catch (Exception e) {
			System.out.println("Failed to send packet for player (Packet209SetScoreboardTeam) : ");

			e.printStackTrace();
		}
	}

	private static void sendPacketsAddToTeam(TeamInfo team, Player player)
	{
		boolean cont = false;
		for (TeamInfo t : getTeams()) {
			if (t == team) {
				cont = true;
			}
		}
		if (!cont) {
			return;
		}

		try
		{
			for (Player p : Bukkit.getOnlinePlayers()) {
				PacketPlayOut mod = new PacketPlayOut(team.getName(), Arrays.asList(new String[] { player.getName() }), 3);

				mod.sendToPlayer(p);
			}
		} catch (Exception e) {
			System.out.println("Failed to send packet for player (Packet209SetScoreboardTeam) : ");

			e.printStackTrace();
		}
	}

	private static void sendPacketsAddToTeam(TeamInfo team, OfflinePlayer player)
	{
		boolean cont = false;
		for (TeamInfo t : getTeams()) {
			if (t == team) {
				cont = true;
			}
		}
		if (!cont) {
			return;
		}

		try
		{
			for (Player p : Bukkit.getOnlinePlayers()) {
				PacketPlayOut mod = new PacketPlayOut(team.getName(), Arrays.asList(new String[] { player.getName() }), 3);

				mod.sendToPlayer(p);
			}
		} catch (Exception e) {
			System.out.println("Failed to send packet for player (Packet209SetScoreboardTeam) : ");

			e.printStackTrace();
		}
	}

	private static void sendPacketsRemoveFromTeam(TeamInfo team, Player player)
	{
		boolean cont = false;
		for (TeamInfo t : getTeams()) {
			if (t == team) {
				for (String p : getTeamPlayers(t)) {
					if (p.equals(player.getName())) {
						cont = true;
					}
				}
			}
		}
		if (!cont) {
			return;
		}

		try
		{
			for (Player p : Bukkit.getOnlinePlayers()) {
				PacketPlayOut mod = new PacketPlayOut(team.getName(), Arrays.asList(new String[] { player.getName() }), 4);

				mod.sendToPlayer(p);
			}
		} catch (Exception e) {
			System.out.println("Failed to send packet for player (Packet209SetScoreboardTeam) : ");

			e.printStackTrace();
		}
	}

	private static void sendPacketsRemoveFromTeam(TeamInfo team, OfflinePlayer player)
	{
		boolean cont = false;
		for (TeamInfo t : getTeams()) {
			if (t == team) {
				for (String p : getTeamPlayers(t)) {
					if (p.equals(player.getName())) {
						cont = true;
					}
				}
			}
		}
		if (!cont) {
			return;
		}

		try
		{
			for (Player p : Bukkit.getOnlinePlayers()) {
				PacketPlayOut mod = new PacketPlayOut(team.getName(), Arrays.asList(new String[] { player.getName() }), 4);

				mod.sendToPlayer(p);
			}
		} catch (Exception e) {
			System.out.println("Failed to send packet for player (Packet209SetScoreboardTeam) : ");

			e.printStackTrace();
		}
	}

	static void reset()
	{
		for (TeamInfo team : getTeams())
			removeTeam(team);
	}
}