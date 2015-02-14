package com.amentrix.evilbook.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.amentrix.evilbook.achievement.Achievement;
import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.StatementSet;
import com.amentrix.evilbook.sql.TableType;
import com.amentrix.evilbook.statistics.GlobalStatistic;
import com.amentrix.evilbook.statistics.GlobalStatistics;

/**
 * PlayerProfileNormal instance
 * @author Reece Aaron Lecrivain
 */
public class PlayerProfileNormal extends PlayerProfile {
	/**
	 * Construct a new non-admin PlayerProfile instance
	 * @param playerName The name of the player
	 */
	public PlayerProfileNormal(EvilBook plugin, Player newPlayer) {
		super(plugin);
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
				if (getProperty("achievement_list") != null) for (String ach : getProperty("achievement_list").split(",")) this.achievements.add(Achievement.valueOf(ach));
				this.runAmplifier = Integer.parseInt(getProperty("run_amplifier"));
				this.walkAmplifier = Float.parseFloat(getProperty("walk_amplifier"));
				newPlayer.setWalkSpeed((float) this.walkAmplifier);
				this.flyAmplifier = Float.parseFloat(getProperty("fly_amplifier"));
				newPlayer.setFlySpeed((float) this.flyAmplifier);
				this.jumpAmplifier = Double.valueOf(getProperty("jump_amplifier"));
				this.nameTitle = getProperty("name_title");
				if (this.nameTitle == null) {
					newPlayer.setDisplayName(this.name + "§f");
				} else {
					newPlayer.setDisplayName("§d" + this.nameTitle + " §f" + this.name + "§f");
				}
				updatePlayerListName();
				newPlayer.setPlayerListName("§" + this.rank.getColor(this) + (this.name.length() > 14 ? this.name.substring(0, 14) : this.name));
				if (getProperty("muted_players") != null) this.mutedPlayers.addAll(Arrays.asList(getProperty("muted_players").split(",")));
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					if (p.getName().equals(this.name)) continue;
					p.sendMessage("§9[§6" + Bukkit.getServer().getOnlinePlayers().size() + "/" + Bukkit.getServer().getMaxPlayers() + "§9] §6Everyone welcome " + newPlayer.getDisplayName() + "§6 back to the game!");
				}
			} else {
				try {
					// Create new player profile
					this.rank = Rank.BUILDER;
					this.money = 100;
					SQL.insert(TableType.PlayerProfile, 
							"'" + this.name + "','" + this.rank.toString() + "',NULL,'" + this.money + "'," +
									"NULL,NULL,NULL,NULL,'4','0.2','0.1','0',NULL,'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "','1',NULL,NULL,'" + newPlayer.getAddress().getAddress().getHostAddress() + "',NULL");
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
					GlobalStatistics.incrementStatistic(GlobalStatistic.LoginNewPlayers, 1);
					//
					newPlayer.setDisplayName("§f" + this.name);
					newPlayer.setPlayerListName("§" + this.rank.getColor(this) + (this.name.length() > 14 ? this.name.substring(0, 14) : this.name));
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
			// Welcome message
			if (getMailCount() == 0) {
				newPlayer.sendMessage("⚔ §bWelcome to " + EvilBook.config.getProperty("server_name") + " §r⚔");
			} else {
				newPlayer.sendMessage("⚔ §bWelcome to " + EvilBook.config.getProperty("server_name") + " §r⚔ §c✉" + getMailCount());
			}
			newPlayer.sendMessage("  §3Type §a/survival §3to enter the survival world");
			newPlayer.sendMessage("  §3Type §a/minigame §3to enter a minigame");
			newPlayer.sendMessage("  §3Type §a/admin §3to discover how to become an admin");
			newPlayer.sendMessage("  §3Type §a/ranks §3for the list of ranks");
			newPlayer.sendMessage("  §3Type §a/donate §3for instructions on how to donate");
			newPlayer.sendMessage("  §3Type §a/help §3for help");
			// NametagEdit
			updateNametag("§" + this.rank.getColor(this), null);
			// Player profile statistics
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			setProperty("total_logins", Integer.toString(Integer.parseInt(getProperty("total_logins")) + 1));
			setProperty("last_login", sdf.format(date));
			// Supply a changelog book if the current evilbook version is different to when last logged in
			String version = plugin.getDescription().getVersion();
			if (getProperty("evilbook_version") == null || !getProperty("evilbook_version").equals(version)) {
				if (rank == Rank.ADVANCED_BUILDER) {
					newPlayer.sendMessage("§cYou have not been demoted your rank name has changed");
				} else if (rank == Rank.ARCHITECT) {
					newPlayer.sendMessage("§cYou have not been demoted your rank name has changed");
				} else if (rank == Rank.MODERATOR) {
					newPlayer.sendMessage("§cYou have not been demoted your rank name has changed");
				} else if (rank == Rank.POLICE) {
					newPlayer.sendMessage("§cYou have not been demoted your rank name has changed");
				}
				List<String> text = new ArrayList<>();
				text.add("§5Welcome to EvilBook " + version + "\n\n§7Alot has changed since EvilBook 6, please read this book to get "
						+ "a brief understanding of what has been added and changed");
				text.add("§5§oRank Changes\n\n§dAdv.Builder is now named Creator\n\nArchitect is now named Designer\n\n"
						+ "Moderator is now named Architect\n\nPolice is now named Engineer");
				text.add("§5§oRank Changes\n\n§dAll staff rank names have had their prefix removed and are now distinguishable "
						+ "by the rank color shown\n\n§7Please note that your rank name may have changed"
						+ "but you have not lost any abilities or commands");
				text.add("§5§oCommand Additions\n\n§d/feedback has been added to allow players to send feedback on the server\n\n/inbox has been "
						+ "added as an alias for /mail inbox");
				text.add("§5§oCommand Additions\n\n§d/region removewarp has been added to allow players to remove the region's warp\n\n/region select has been "
						+ "added to allow players to select the region area for use with EvilEdit");
				text.add("§5§oCommand Additions\n\n§d/region inherit has been added to allow players to inherit a region's properties from another"
						+ "\n\n/deforest has been added to EvilEdit to remove all trees and leaves in a selected area");
				text.add("§5§oRedstone\n\n§dThe ammeter tool has been added which allows a player to right click redstone wire to read its current");
				text.add("§5§oWorlds\n\n§dThe plot world has been added and can be accessed using /plotland\n\n§7No other worlds have been reset or altered in the process");
				text.add("§5§oMinigames\n\n§dThe Tower Defense minigame has been added where a player has to strategically place towers to fight waves of monsters "
						+ "\n\n§7It can be played using /minigame towerDefense");
				text.add("§5§oMail\n\n§dThe mail inbox now dynamically sizes depending on how much mail the user has and shows a message "
						+ "when they have no mail");
				text.add("§5§oBlocks\n\n§dThe mob spawner block can now be used by Creator ranks and higher, it is no longer admin only");
				text.add("§5§oEvilEdit\n\n§dA new experimental engine has been added to EvilEdit which now supports data values");
				text.add("§5§oAchievements\n\n§dThe 'A Rare Kill' achievement has been added including three other progressive achievements eg. 'A Rare Kill II'"
						+ "\n\nTwo new titles have been added 'Rare' and 'Ledgendary'");
				text.add("§5§oFixes\n\n§dCountless fixes have been added however the most notible is that sign editing has been fixed");
				text.add("§5§oMiscellaneous\n\n§dCountless optimizations have been added including many plugin message changes\n\n§7This changelog book was "
						+ "provided to give a brief overview of changes in this version however there are many more improvements and additions not noted");
				newPlayer.getInventory().addItem(EvilBook.getBook("EvilBook 7 Guide", EvilBook.config.getProperty("server_name"), text));
				setProperty("evilbook_version", version);
			}
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
		profileSaveAgent.setProperty(TableType.PlayerLocation, this.name, "home_location", this.homeLocation == null ? "NULL" : this.homeLocation.getX() + ">" + this.homeLocation.getY() + ">" + this.homeLocation.getZ() + ">" + this.homeLocation.getWorld().getName());
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "name_title", this.nameTitle == null ? "NULL" : this.nameTitle);
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "name_alias", "NULL");
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "muted_players", (this.mutedPlayers.size() != 0 ? StringUtils.join(this.mutedPlayers, ",") : "NULL"));
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "rank", this.rank.toString());
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "money", Integer.toString(this.money));
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "warp_list", (this.warps.size() != 0 ? StringUtils.join(this.warps, ",").replaceAll("'", "''") : "NULL"));
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "run_amplifier", Integer.toString(this.runAmplifier));
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "walk_amplifier", Double.toString(this.walkAmplifier));
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "fly_amplifier", Double.toString(this.flyAmplifier));
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "jump_amplifier", Double.toString(this.jumpAmplifier));
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "achievement_list", (this.achievements.size() != 0 ? StringUtils.join(this.achievements, ",") : "NULL"));
		profileSaveAgent.setProperty(TableType.PlayerProfile, this.name, "ip", getPlayer().getAddress().getAddress().getHostAddress());
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
