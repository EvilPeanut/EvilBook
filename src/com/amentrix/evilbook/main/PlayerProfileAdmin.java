package com.amentrix.evilbook.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.amentrix.evilbook.achievement.Achievement;
import com.amentrix.evilbook.eviledit.utils.Clipboard;
import com.amentrix.evilbook.nametag.NametagAPI;
import com.amentrix.evilbook.sql.StatementSet;
import com.amentrix.evilbook.sql.TableType;

/**
 * PlayerProfileAdmin instance
 * @author Reece Aaron Lecrivain
 */
public class PlayerProfileAdmin extends PlayerProfile {
	public String nameAlias, customRankPrefix = "§0[§6Custom§0]", customRankColor = "6";
	public Boolean isLogging = false;
	public Clipboard clipboard = new Clipboard();

	/**
	 * Construct a new admin PlayerProfile instance
	 * @param playerName The name of the player
	 */
	public PlayerProfileAdmin(final Player newPlayer) {
		try {
			this.name = newPlayer.getName();
			this.rank = Rank.valueOf(getProperty("rank"));
			if (this.rank.isCustomRank()) {
				String prefix = getProperty("rank_prefix");
				if (prefix != null) {
					this.customRankColor = prefix.substring(4, 5);
					this.customRankPrefix = prefix;
				}
			}
			this.money = Integer.parseInt(getProperty("money"));
			if (getProperty(TableType.PlayerLocation, "home_location") != null) {
				String[] location = getProperty(TableType.PlayerLocation, "home_location").split(">");
				this.homeLocation = new Location(Bukkit.getServer().getWorld(location[3]), Double.valueOf(location[0]), Double.valueOf(location[1]), Double.valueOf(location[2]));
			}
			if (getProperty("warp_list") != null) this.warps.addAll(Arrays.asList(getProperty("warp_list").toLowerCase().split(",")));
			if (getProperty("achievement_list") != null) for (String ach : getProperty("achievement_list").split(",")) this.achievements.add(Achievement.valueOf(ach));
			this.runAmplifier = Integer.parseInt(getProperty("run_amplifier"));
			this.walkAmplifier = Float.parseFloat(getProperty("walk_amplifier"));
			newPlayer.setWalkSpeed((float) this.walkAmplifier);
			this.flyAmplifier = Float.parseFloat(getProperty("fly_amplifier"));
			newPlayer.setFlySpeed((float) this.flyAmplifier);
			this.jumpAmplifier = Double.valueOf(getProperty("jump_amplifier"));
			this.nameTitle = getProperty("name_title");
			this.nameAlias = getProperty("name_alias");
			if (this.nameAlias != null) {
				if (this.nameTitle == null) {
					newPlayer.setDisplayName(this.nameAlias + "§f");
				} else {
					newPlayer.setDisplayName("§d" + this.nameTitle + " §f" + this.nameAlias + "§f");
				}
				updatePlayerListName();
			} else {
				if (this.nameTitle == null) {
					newPlayer.setDisplayName(this.name + "§f");
				} else {
					newPlayer.setDisplayName("§d" + this.nameTitle + " §f" + this.name + "§f");
				}
				updatePlayerListName();
			}
			if (getProperty("muted_players") != null) this.mutedPlayers.addAll(Arrays.asList(getProperty("muted_players").split(",")));
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (p.getName().equals(this.name)) continue;
				p.sendMessage("§9[§6" + Bukkit.getServer().getOnlinePlayers().size() + "/" + Bukkit.getServer().getMaxPlayers() + "§9] §6Everyone welcome " + newPlayer.getDisplayName() + "§6 back to the game!");
			}
			// Welcome message
			if (getMailCount() == 0) {
				newPlayer.sendMessage("§bWelcome to Amentrix");
			} else {
				newPlayer.sendMessage("§bWelcome to Amentrix §c✉" + getMailCount());
			}
			newPlayer.sendMessage("§3Type §a/survival §3to enter the survival world");
			newPlayer.sendMessage("§3Type §a/minigame §3to enter a minigame");
			newPlayer.sendMessage("§3Type §a/ranks §3for the list of ranks");
			newPlayer.sendMessage("§3Type §a/donate §3for instructions on how to donate");
			newPlayer.sendMessage("§3Type §a/help §3for help");
			// NametagEdit
			NametagAPI.updateNametagHard(this.name, "§" + this.rank.getColor(this), null);
			// Player profile statistics
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			setProperty("total_logins", Integer.toString(Integer.parseInt(getProperty("total_logins")) + 1));
			setProperty("last_login", sdf.format(date));
		} catch (Exception exception) {
			newPlayer.kickPlayer("§cA login error has occured and our team has been notified, sorry for the inconvenience");
			try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("EvilBook.log", true)))) {
				out.println("Failed to login " + newPlayer.getName());
				exception.printStackTrace(out);
			} catch (Exception logException) {
				logException.printStackTrace();
			}
		}
	}

	public String getStrippedNameAlias() {
		return ChatColor.stripColor(nameAlias);
	}
	
	/**
	 * Save the player profile
	 */
	@Override
	public void saveProfile() {
		StatementSet profileSaveAgent = new StatementSet();
		profileSaveAgent.setProperty(TableType.PlayerLocation, this.name, "home_location", this.homeLocation == null ? "NULL" : this.homeLocation.getX() + ">" + this.homeLocation.getY() + ">" + this.homeLocation.getZ() + ">" + this.homeLocation.getWorld().getName());
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "name_title", this.nameTitle == null ? "NULL" : this.nameTitle);
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "name_alias", this.nameAlias == null ? "NULL" : this.nameAlias);
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "muted_players", (this.mutedPlayers.size() != 0 ? StringUtils.join(this.mutedPlayers, ",") : "NULL"));
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "rank", this.rank.toString());
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "rank_prefix", this.customRankPrefix);
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "money", Integer.toString(this.money));
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "warp_list", (this.warps.size() != 0 ? StringUtils.join(this.warps, ",").replaceAll("'", "''") : "NULL"));
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "run_amplifier", Integer.toString(this.runAmplifier));
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "walk_amplifier", Double.toString(this.walkAmplifier));
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "fly_amplifier", Double.toString(this.flyAmplifier));
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "jump_amplifier", Double.toString(this.jumpAmplifier));
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "achievement_list", (this.achievements.size() != 0 ? StringUtils.join(this.achievements, ",") : "NULL"));
		profileSaveAgent.execute();
	}

	/**
	 * Set the title of the player
	 * @param title The title
	 */
	@Override
	public void setNameTitle(String title) {
		Player player = Bukkit.getServer().getPlayer(this.name);
		this.nameTitle = title;
		if (this.nameAlias == null) {
			if (this.nameTitle == null) {
				player.setDisplayName(this.name + "§f");
			} else {
				player.setDisplayName("§d" + this.nameTitle + " §f" + this.name + "§f");
			}
		} else {
			if (this.nameTitle == null) {
				player.setDisplayName(this.nameAlias + "§f");
			} else {
				player.setDisplayName("§d" + this.nameTitle + " §f" + this.nameAlias + "§f");
			}
		}
	}

	/**
	 * Set the name alias of the player
	 * @param alias The name alias
	 */
	public void setNameAlias(String alias) {
		Player player = Bukkit.getServer().getPlayer(this.name);
		if (alias == null) {
			this.nameAlias = null;
			if (this.nameTitle == null) {
				player.setDisplayName(this.name + "§f");
			} else {
				player.setDisplayName("§d" + this.nameTitle + " §f" + this.name + "§f");
			}
			updatePlayerListName();
		} else {
			this.nameAlias = alias;
			if (this.nameTitle == null) {
				player.setDisplayName(this.nameAlias + "§f");
			} else {
				player.setDisplayName("§d" + this.nameTitle + " §f" + this.nameAlias + "§f");
			}
			updatePlayerListName();
		}
	}

	/**
	 * Update the player's name in the player list
	 */
	@Override
	public void updatePlayerListName() {
		Player player = Bukkit.getServer().getPlayer(this.name);
		if (this.nameAlias != null) {
			if (this.isAway) {
				player.setPlayerListName("§7*§" + this.rank.getColor(this) + (this.nameAlias.length() > 11 ? this.nameAlias.substring(0, 11) : this.nameAlias));
			} else {
				player.setPlayerListName("§" + this.rank.getColor(this) + (this.nameAlias.length() > 14 ? this.nameAlias.substring(0, 14) : this.nameAlias));
			}
		} else {
			if (this.isAway) {
				player.setPlayerListName("§7*§" + this.rank.getColor(this) + (this.name.length() > 11 ? this.name.substring(0, 11) : this.name));
			} else {
				player.setPlayerListName("§" + this.rank.getColor(this) + (this.name.length() > 14 ? this.name.substring(0, 14) : this.name));
			}
		}
	}
}
