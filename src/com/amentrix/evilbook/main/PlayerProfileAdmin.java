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
import com.amentrix.evilbook.sql.SQLQuery;
import com.amentrix.evilbook.sql.StatementSet;
import com.amentrix.evilbook.sql.TableType;

/**
 * PlayerProfileAdmin instance
 * @author Reece Aaron Lecrivain
 */
public class PlayerProfileAdmin extends PlayerProfile {
	String nameAlias, customRankPrefix = "§0[§6Custom§0]", customRankColor = "6";
	public Clipboard clipboard = new Clipboard();

	public PlayerProfileAdmin(EvilBook plugin, Player newPlayer, boolean showWelcome) {
		super(plugin);
		try {
			name = newPlayer.getName();
			UUID = newPlayer.getUniqueId().toString();
			//
			// SQL query
			//
			SQLQuery query = new SQLQuery(TableType.PlayerProfile, "player_name", name);
			query.addField("rank");
			query.addField("rank_prefix");
			query.addField("money");
			query.addField("warp_list");
			query.addField("achievement_list");
			query.addField("run_amplifier");
			query.addField("walk_amplifier");
			query.addField("fly_amplifier");
			query.addField("jump_amplifier");
			query.addField("name_title");
			query.addField("name_alias");
			query.addField("muted_players");
			query.addField("total_logins");
			query.execute();
			//
			// Rank
			//
			rank = query.getRank("rank");
			if (rank.isCustomRank()) {
				String prefix = query.getString("rank_prefix");
				if (prefix != null) {
					customRankColor = prefix.substring(4, 5);
					customRankPrefix = prefix;
				}
			}
			//
			// Money
			//
			money = query.getInteger("money");
			//
			// Home location
			//
			//TODO: Improve home_location retrieval
			if (getProperty(TableType.PlayerLocation, "home_location") != null) {
				String[] location = getProperty(TableType.PlayerLocation, "home_location").split(">");
				homeLocation = new Location(Bukkit.getServer().getWorld(location[3]), Double.valueOf(location[0]), Double.valueOf(location[1]), Double.valueOf(location[2]));
			}
			//
			// Warp list
			//
			//TODO: Remove toLowerCase dependancy
			if (query.getString("warp_list") != null) {
				warps.addAll(Arrays.asList(query.getString("warp_list").toLowerCase().split(",")));
			}
			//
			// Achievement list
			//
			if (query.getString("achievement_list") != null) {
				for (String achievement : query.getString("achievement_list").split(",")) {
					achievements.add(Achievement.valueOf(achievement));
				}
			}
			//
			// Amplifiers
			//
			runAmplifier = query.getInteger("run_amplifier");
			walkAmplifier = query.getFloat("walk_amplifier");
			newPlayer.setWalkSpeed(walkAmplifier);
			flyAmplifier = query.getFloat("fly_amplifier");
			newPlayer.setFlySpeed(flyAmplifier);
			jumpAmplifier = query.getFloat("jump_amplifier");
			//
			// Name title and alias
			//
			nameTitle = query.getString("name_title");
			nameAlias = query.getString("name_alias");
			if (nameAlias != null) {
				if (nameTitle == null) {
					newPlayer.setDisplayName(nameAlias + "§f");
				} else {
					newPlayer.setDisplayName("§d" + nameTitle + " §f" + nameAlias + "§f");
				}
			} else {
				if (nameTitle == null) {
					newPlayer.setDisplayName(name + "§f");
				} else {
					newPlayer.setDisplayName("§d" + nameTitle + " §f" + name + "§f");
				}
			}
			updatePlayerListName();
			//
			// Muted players list
			//
			if (query.getString("muted_players") != null) {
				mutedPlayers.addAll(Arrays.asList(query.getString("muted_players").split(",")));
			}
			//
			// Join messages
			//
			if (showWelcome) {
				//
				// Join message to other players
				//
				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					if (player.getName().equals(name)) continue;
					player.sendMessage("§9[§6" + plugin.getServer().getOnlinePlayers().size() + "/" + plugin.getServer().getMaxPlayers() + "§9] §6Everyone welcome " + newPlayer.getDisplayName() + "§6 back to the game!");
				}
				//
				// Welcome message
				//
				if (getMailCount() == 0) {
					newPlayer.sendMessage("⚔ §bWelcome to " + EvilBook.config.getProperty("server_name") + " §r⚔");
				} else {
					newPlayer.sendMessage("⚔ §bWelcome to " + EvilBook.config.getProperty("server_name") + " §r⚔ §c✉" + getMailCount());
				}
				newPlayer.sendMessage("  §3Type §a/survival §3to enter the survival world");
				newPlayer.sendMessage("  §3Type §a/minigame §3to enter a minigame");
				newPlayer.sendMessage("  §3Type §a/ranks §3for the list of ranks");
				newPlayer.sendMessage("  §3Type §a/donate §3for instructions on how to donate");
				newPlayer.sendMessage("  §3Type §a/help §3for help");
			}
			//
			// NametagEdit
			//
			updateNametag("§" + rank.getColor(this), null);
			//
			// Statistics
			//
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			setInteger("total_logins", query.getInteger("total_logins") + 1);
			setString("last_login", sdf.format(date));
			setString("ip", getPlayer().getAddress().getAddress().getHostAddress());
			setString("evilbook_version", plugin.getDescription().getVersion());
			//
			// SQL query close
			//
			query.close();
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

	@Override
	public void saveProfile() {
		StatementSet profileSaveAgent = new StatementSet();
		profileSaveAgent.setProperty(TableType.PlayerLocation, name, "home_location", homeLocation == null ? "NULL" : homeLocation.getX() + ">" + homeLocation.getY() + ">" + homeLocation.getZ() + ">" + homeLocation.getWorld().getName());
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "name_title", nameTitle == null ? "NULL" : nameTitle);
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "name_alias", nameAlias == null ? "NULL" : nameAlias);
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "muted_players", (mutedPlayers.size() != 0 ? StringUtils.join(mutedPlayers, ",") : "NULL"));
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "rank", rank.toString());
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "rank_prefix", customRankPrefix);
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "money", Integer.toString(money));
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "warp_list", (warps.size() != 0 ? StringUtils.join(warps, ",").replaceAll("'", "''") : "NULL"));
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "run_amplifier", Integer.toString(runAmplifier));
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "walk_amplifier", Double.toString(walkAmplifier));
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "fly_amplifier", Double.toString(flyAmplifier));
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "jump_amplifier", Double.toString(jumpAmplifier));
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "achievement_list", (achievements.size() != 0 ? StringUtils.join(achievements, ",") : "NULL"));
		profileSaveAgent.execute();
	}

	@Override
	public void setNameTitle(String title) {
		nameTitle = title;
		if (nameAlias == null) {
			if (nameTitle == null) {
				getPlayer().setDisplayName(name + "§f");
			} else {
				getPlayer().setDisplayName("§d" + nameTitle + " §f" + name + "§f");
			}
		} else {
			if (nameTitle == null) {
				getPlayer().setDisplayName(nameAlias + "§f");
			} else {
				getPlayer().setDisplayName("§d" + nameTitle + " §f" + nameAlias + "§f");
			}
		}
	}

	public void setNameAlias(String alias) {
		if (alias == null) {
			nameAlias = null;
			if (nameTitle == null) {
				getPlayer().setDisplayName(name + "§f");
			} else {
				getPlayer().setDisplayName("§d" + nameTitle + " §f" + name + "§f");
			}
		} else {
			nameAlias = alias;
			if (nameTitle == null) {
				getPlayer().setDisplayName(nameAlias + "§f");
			} else {
				getPlayer().setDisplayName("§d" + nameTitle + " §f" + nameAlias + "§f");
			}
		}
		updatePlayerListName();
	}

	@Override
	public void updatePlayerListName() {
		if (nameAlias != null) {
			if (isAway) {
				getPlayer().setPlayerListName("§7*§" + rank.getColor(this) + (nameAlias.length() > 11 ? nameAlias.substring(0, 11) : nameAlias));
			} else {
				getPlayer().setPlayerListName("§" + rank.getColor(this) + (nameAlias.length() > 14 ? nameAlias.substring(0, 14) : nameAlias));
			}
		} else {
			if (isAway) {
				getPlayer().setPlayerListName("§7*§" + rank.getColor(this) + (name.length() > 11 ? name.substring(0, 11) : name));
			} else {
				getPlayer().setPlayerListName("§" + rank.getColor(this) + (name.length() > 14 ? name.substring(0, 14) : name));
			}
		}
	}
}
