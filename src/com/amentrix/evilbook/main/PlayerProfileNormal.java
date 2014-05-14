package com.amentrix.evilbook.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.amentrix.evilbook.nametag.NametagManager;
import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;
import com.amentrix.evilbook.statistics.Statistic;
import com.amentrix.evilbook.statistics.Statistics;

/**
 * PlayerProfileNormal instance
 * @author Reece Aaron Lecrivain
 */
public class PlayerProfileNormal extends PlayerProfile {
	/**
	 * Construct a new non-admin PlayerProfile instance
	 * @param playerName The name of the player
	 */
	public PlayerProfileNormal(Player newPlayer) {
		try {
			this.name = newPlayer.getName();
			if (getProperty("player_name") != null) {
				this.rank = Rank.valueOf(getProperty("rank"));
				this.money = Integer.parseInt(getProperty("money"));
				if (getProperty(TableType.PlayerLocation, "home_location") != null) {
					String[] location = getProperty(TableType.PlayerLocation, "home_location").split(">");
					this.homeLocation = new Location(Bukkit.getServer().getWorld(location[3]), Double.valueOf(location[0]), Double.valueOf(location[1]), Double.valueOf(location[2]));
				}
				if (getProperty("warp_list") != null) this.warps.addAll(Arrays.asList(getProperty("warp_list").toLowerCase().split(",")));
				if (getProperty("achievement_list") != null) for (String ach : getProperty("achievement_list").toLowerCase().split(",")) this.achievements.add(Achievement.getByName(ach));
				this.runAmplifier = Integer.parseInt(getProperty("run_amplifier"));
				this.walkAmplifier = Float.parseFloat(getProperty("walk_amplifier"));
				newPlayer.setWalkSpeed(this.walkAmplifier);
				this.flyAmplifier = Float.parseFloat(getProperty("fly_amplifier"));
				newPlayer.setFlySpeed(this.flyAmplifier);
				this.jumpAmplifier = Double.valueOf(getProperty("jump_amplifier"));
				newPlayer.setDisplayName("�f" + this.name);
				newPlayer.setPlayerListName("�" + this.rank.getColor(this) + (this.name.length() > 14 ? this.name.substring(0, 14) : this.name));
				if (getProperty("muted_players") != null) this.mutedPlayers.addAll(Arrays.asList(getProperty("muted_players").split(",")));
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (p.getName().equals(this.name)) continue;
					p.sendMessage("�9[�6" + Bukkit.getServer().getOnlinePlayers().length + "/" + Bukkit.getServer().getMaxPlayers() + "�9] �6Everyone welcome " + newPlayer.getDisplayName() + "�6 back to the game!");
				}
			} else {
				try {
					// Create new player profile
					this.rank = Rank.Builder;
					this.money = 0;
					SQL.insert(TableType.PlayerProfile, 
							"'" + this.name + "','" + this.rank.toString() + "',NULL,'" + this.money + "','" +
									"f',NULL,NULL,NULL,NULL,'4','0.1','0.1','0',NULL,'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "','1',NULL,NULL");
					// Create new player locations profile
					String fields = "player_name, home_location", data = "'" + this.name + "',NULL";
					for (World world : Bukkit.getServer().getWorlds()) {
						String worldName = world.getName();
						if (worldName.contains("Private worlds/")) worldName = worldName.split("Private worlds/")[1];
						fields += ", " + worldName;
						data += ",NULL";
					}
					SQL.insert(TableType.PlayerLocation, fields, data);
					// Statistics
					Statistics.incrementStatistic(Statistic.LoginNewPlayers, 1);
					//
					newPlayer.setDisplayName("�f" + this.name);
					newPlayer.setPlayerListName("�" + this.rank.getColor(this) + (this.name.length() > 14 ? this.name.substring(0, 14) : this.name));
					newPlayer.getInventory().addItem(EvilBook.getBook("Welcome to Amentrix", "Amentrix", Arrays.asList("�1Welcome to Amentrix\n\n�dRules\n�5�l1 �5Do not grief\n�5�l2 �5Do not advertise\n�5�l3 �5Do not spam\n\n�dWebsite\n�5www.amentrix.com\n\n�dHave fun!\n�7 - EvilPeanut")));
				} catch (Exception exception) {
					EvilBook.logSevere("Failed to create " + this.name + "'s player profile");
					exception.printStackTrace();
				}
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (p.getName().equals(this.name)) continue;
					p.sendMessage("�d" + SQL.getRowCount(TableType.PlayerProfile) + " �9players have joined the server");
					p.sendMessage("�9[�6" + Bukkit.getServer().getOnlinePlayers().length + "/" + Bukkit.getServer().getMaxPlayers() + "�9] �6Everyone welcome " + newPlayer.getDisplayName() + "�6 for the first time!");
				}
			}
			// Welcome message
			newPlayer.sendMessage("�bWelcome to Amentrix");
			newPlayer.sendMessage("�3Type �a/survival �3to enter the survival world");
			newPlayer.sendMessage("�3Type �a/minigame �3to enter a minigame");
			newPlayer.sendMessage("�3Type �a/admin �3to discover how to become an admin");
			newPlayer.sendMessage("�3Type �a/ranks �3for the list of ranks");
			newPlayer.sendMessage("�3Type �a/donate �3for instructions on how to donate");
			newPlayer.sendMessage("�3Type �a/help �3for help");
			// NametagEdit
			NametagManager.updateNametagHard(this.name, "�" + this.rank.getColor(this), null);
			// Player profile statistics
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			setProperty("total_logins", Integer.toString(Integer.parseInt(getProperty("total_logins")) + 1));
			setProperty("last_login", sdf.format(date));
		} catch (Exception exception) {
			newPlayer.kickPlayer("�cA login error has occured and our team has been notified, sorry for the inconvenience");
			try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("EvilBook.log", true)))) {
			    out.println("Failed to login " + newPlayer.getName());
			    exception.printStackTrace(out);
			} catch (Exception logException) {
				logException.printStackTrace();
			}
		}
	}

	/**
	 * Save the player profile
	 */
	@Override
	public void saveProfile() {
		setProperty(TableType.PlayerLocation, "home_location", this.homeLocation == null ? "NULL" : this.homeLocation.getX() + ">" + this.homeLocation.getY() + ">" + this.homeLocation.getZ() + ">" + this.homeLocation.getWorld().getName());
		setProperty("name_color", "f");
		setProperty("name_title", "NULL");
		setProperty("name_alias", "NULL");
		setProperty("muted_players", (this.mutedPlayers.size() != 0 ? StringUtils.join(this.mutedPlayers, ",") : "NULL"));
		setProperty("rank", this.rank.toString());
		setProperty("money", Integer.toString(this.money));
		setProperty("warp_list", (this.warps.size() != 0 ? StringUtils.join(this.warps, ",").replaceAll("'", "''") : "NULL"));
		setProperty("run_amplifier", Integer.toString(this.runAmplifier));
		setProperty("walk_amplifier", Float.toString(this.walkAmplifier));
		setProperty("fly_amplifier", Float.toString(this.flyAmplifier));
		setProperty("jump_amplifier", Double.toString(this.jumpAmplifier));
		setProperty("achievement_list", (this.achievements.size() != 0 ? StringUtils.join(this.achievements, ",") : "NULL"));
	}

	/**
	 * Update the player's name in the player list
	 */
	@Override
	public void updatePlayerListName() {
		Player player = Bukkit.getServer().getPlayer(this.name);
		if (this.isAway) {
			player.setPlayerListName("�7*�" + this.rank.getColor(this) + (this.name.length() > 11 ? this.name.substring(0, 11) : this.name));
		} else {
			player.setPlayerListName("�" + this.rank.getColor(this) + (this.name.length() > 14 ? this.name.substring(0, 14) : this.name));
		}
	}
}