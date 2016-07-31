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

import com.amentrix.evilbook.achievement.Achievement;
import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.SQLQuery;
import com.amentrix.evilbook.sql.StatementSet;
import com.amentrix.evilbook.sql.TableType;
import com.amentrix.evilbook.statistics.GlobalStatistic;

import net.minecraft.server.v1_10_R1.ChatClickable.EnumClickAction;

/**
 * PlayerProfileNormal instance
 * @author Reece Aaron Lecrivain
 */
public class PlayerProfileNormal extends PlayerProfile {
	/**
	 * Construct a new non-admin PlayerProfile instance
	 * @param playerName The name of the player
	 */
	public PlayerProfileNormal(EvilBook plugin, Player newPlayer, boolean showWelcome) {
		super(plugin);
		try {
			name = newPlayer.getName();
			if (getProperty("player_name") != null) {
				//
				// SQL query
				//
				SQLQuery query = new SQLQuery(TableType.PlayerProfile, "player_name", name);
				query.addField("rank");
				query.addField("money");
				query.addField("warp_list");
				query.addField("achievement_list");
				query.addField("run_amplifier");
				query.addField("walk_amplifier");
				query.addField("fly_amplifier");
				query.addField("jump_amplifier");
				query.addField("name_title");
				query.addField("muted_players");
				query.execute();
				//
				// Rank
				//
				rank = query.getRank("rank");
				//
				// Money
				//
				money = query.getInteger("money");
				//
				// Home location
				//
				//TODO: PlayerProfiles: Improve home_location retrieval
				if (getProperty(TableType.PlayerLocation, "home_location") != null) {
					String[] location = getProperty(TableType.PlayerLocation, "home_location").split(">");
					homeLocation = new Location(Bukkit.getServer().getWorld(location[3]), Double.valueOf(location[0]), Double.valueOf(location[1]), Double.valueOf(location[2]));
				}
				//
				// Warp list
				//
				//TODO: PlayerProfiles: Remove toLowerCase dependancy
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
				// Name title
				//
				nameTitle = query.getString("name_title");
				if (this.nameTitle == null) {
					newPlayer.setDisplayName(this.name + "§f");
				} else {
					newPlayer.setDisplayName("§d" + this.nameTitle + " §f" + this.name + "§f");
				}
				updatePlayerListName();
				//
				// Muted players list
				//
				if (query.getString("muted_players") != null) {
					mutedPlayers.addAll(Arrays.asList(query.getString("muted_players").split(",")));
				}
				//
				// Welcome message
				//
				if (showWelcome) {
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						if (p.getName().equals(this.name)) continue;
						p.sendMessage("§9[§6" + Bukkit.getServer().getOnlinePlayers().size() + "/" + Bukkit.getServer().getMaxPlayers() + "§9] §6Everyone welcome " + newPlayer.getDisplayName() + "§6 back to the game!");
					}
				}
			} else {
				//
				// Create new player profile
				//
				try {
					//
					// Rank
					//
					this.rank = Rank.BUILDER;
					//
					// Money
					//
					this.money = 100;
					//
					// Add profile data to SQL
					//
					SQL.insert(TableType.PlayerProfile, 
							"'" + this.name + "','" + newPlayer.getUniqueId().toString() + "','" + this.rank.toString() + "',NULL,'" + this.money + "'," +
									"NULL,NULL,NULL,NULL,'4','0.2','0.1','0',NULL,'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "','0',NULL,NULL,NULL,'" + newPlayer.getAddress().getAddress().getHostAddress() + "',NULL");
					//
					// Add location data to SQL
					//
					String fields = "player_name, home_location", data = "'" + this.name + "',NULL";
					for (World world : Bukkit.getServer().getWorlds()) {
						String worldName = world.getName();
						if (!worldName.contains("SkyBlock/")) {
							if (worldName.contains("Private worlds/")) worldName = worldName.split("Private worlds/")[1];
							fields += ", " + worldName;
							data += ",NULL";
						}
					}
					SQL.insert(TableType.PlayerLocation, fields, data);
					//
					// Statistics
					//
					GlobalStatistic.incrementStatistic(GlobalStatistic.LoginNewPlayers, 1);
					//
					// Player display and list name
					//
					newPlayer.setDisplayName("§f" + this.name);
					updatePlayerListName();
					//
					// Add welcome book to player inventory
					//
					newPlayer.getInventory().addItem(EvilBook.getBook("Welcome to " + EvilBook.config.getProperty("server_name"), EvilBook.config.getProperty("server_name"), Arrays.asList("§1Welcome to " + EvilBook.config.getProperty("server_name") + "\n\n§dRules\n§5§l1 §5Do not grief\n§5§l2 §5Do not advertise\n§5§l3 §5Do not spam\n\n§dWebsite\n§5www.amentrix.com\n\n§dHave fun!\n§7 - " + EvilBook.config.getProperty("server_host"))));
				} catch (Exception exception) {
					EvilBook.logSevere("Failed to create " + this.name + "'s player profile");
					exception.printStackTrace();
				}
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (p.getName().equals(this.name)) continue;
					p.sendMessage("§d" + SQL.getRowCount(TableType.PlayerProfile) + " §9players have joined the server");
					p.sendMessage("§9[§6" + Bukkit.getServer().getOnlinePlayers().size() + "/" + Bukkit.getServer().getMaxPlayers() + "§9] §6Everyone welcome " + newPlayer.getDisplayName() + "§6 for the first time!");
				}
			}
			//
			// Join message
			//
			if (showWelcome) {
				//
				// Welcome message
				//
				if (getMailCount() == 0) {
					newPlayer.sendMessage("⚔ §bWelcome to " + EvilBook.config.getProperty("server_name") + " §r⚔");
				} else {
					newPlayer.sendMessage("⚔ §bWelcome to " + EvilBook.config.getProperty("server_name") + " §r⚔ §c✉" + getMailCount());
				}
				ChatExtensions.sendClickableMessage(newPlayer, "  §3Type §a/survival §3to enter the survival world", EnumClickAction.SUGGEST_COMMAND, "/survival");
				ChatExtensions.sendClickableMessage(newPlayer, "  §3Type §a/minigame §3to enter a minigame", EnumClickAction.SUGGEST_COMMAND, "/minigame");
				ChatExtensions.sendClickableMessage(newPlayer, "  §3Type §a/admin §3to discover how to become an admin", EnumClickAction.SUGGEST_COMMAND, "/admin");
				ChatExtensions.sendClickableMessage(newPlayer, "  §3Type §a/ranks §3for the list of ranks", EnumClickAction.SUGGEST_COMMAND, "/ranks");
				ChatExtensions.sendClickableMessage(newPlayer, "  §3Type §a/donate §3for instructions on how to donate", EnumClickAction.SUGGEST_COMMAND, "/donate");
				ChatExtensions.sendClickableMessage(newPlayer, "  §3Type §a/help §3for help", EnumClickAction.SUGGEST_COMMAND, "/help");
			}
			//
			// NametagEdit
			//
			updateNametag("§" + this.rank.getColor(this), null);
			//
			// Statistics
			//
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			setInteger("total_logins", Integer.parseInt(getProperty("total_logins")) + 1);
			setString("last_login", sdf.format(date));
			setString("ip", getPlayer().getAddress().getAddress().getHostAddress());
			setString("evilbook_version", plugin.getDescription().getVersion());
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

	/**
	 * Save the player profile
	 */
	@Override
	public void saveProfile() {
		StatementSet profileSaveAgent = new StatementSet();
		profileSaveAgent.setProperty(TableType.PlayerLocation, name, "home_location", this.homeLocation == null ? "NULL" : this.homeLocation.getX() + ">" + this.homeLocation.getY() + ">" + this.homeLocation.getZ() + ">" + this.homeLocation.getWorld().getName());
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "name_title", this.nameTitle == null ? "NULL" : this.nameTitle);
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "name_alias", "NULL");
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "muted_players", (this.mutedPlayers.size() != 0 ? StringUtils.join(this.mutedPlayers, ",") : "NULL"));
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "rank", this.rank.toString());
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "money", Integer.toString(this.money));
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "warp_list", (this.warps.size() != 0 ? StringUtils.join(this.warps, ",").replaceAll("'", "''") : "NULL"));
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "run_amplifier", Integer.toString(this.runAmplifier));
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "walk_amplifier", Double.toString(this.walkAmplifier));
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "fly_amplifier", Double.toString(this.flyAmplifier));
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "jump_amplifier", Double.toString(this.jumpAmplifier));
		profileSaveAgent.setProperty(TableType.PlayerProfile, name, "achievement_list", (this.achievements.size() != 0 ? StringUtils.join(this.achievements, ",") : "NULL"));
		profileSaveAgent.execute();
	}

	/**
	 * Set the title of the player
	 * @param title The title
	 */
	@Override
	public void setNameTitle(String title) {
		Player player = getPlayer();
		this.nameTitle = title;
		if (this.nameTitle == null) {
			player.setDisplayName(this.name + "§f");
		} else {
			player.setDisplayName("§d" + this.nameTitle + " §f" + this.name + "§f");
		}
	}

	/**
	 * Update the player's name in the player list
	 */
	@Override
	public void updatePlayerListName() {
		Player player = getPlayer();
		if (this.isAway) {
			player.setPlayerListName("§7*§" + this.rank.getColor(this) + (this.name.length() > 11 ? this.name.substring(0, 11) : this.name));
		} else {
			player.setPlayerListName("§" + this.rank.getColor(this) + (this.name.length() > 14 ? this.name.substring(0, 14) : this.name));
		}
	}
}
