package com.amentrix.evilbook.main;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.amentrix.evilbook.achievement.Achievement;
import com.amentrix.evilbook.eviledit.utils.EditWandMode;
import com.amentrix.evilbook.nametag.NametagManager;
import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;

/**
 * PlayerProfile parent instance
 * @author Reece Aaron Lecrivain
 */
public abstract class PlayerProfile {
	public String name, nameTitle, lastMessage, teleportantName, lastMsgPlayer;
	public List<String> warps = new ArrayList<>(), mutedPlayers = new ArrayList<>();
	public Boolean isAway = false, isInvisible = false, isDrunk = false;
	public List<Achievement> achievements = new ArrayList<>();
	public long lastMessageTime = 0, lastActionTime = 0;
	public double jumpAmplifier = 0, flyAmplifier = 0.1, walkAmplifier = 0.2;
	public Rank rank = Rank.BUILDER;
	public Entity disguise;
	public Location lastLocation, homeLocation, lastBlockInteraction;
	public int runAmplifier = 4, money = 0;
	public EditWandMode wandMode = EditWandMode.Selection;
	public Location actionLocationA, actionLocationB;
	public EvilBook plugin;
	
	public PlayerProfile(EvilBook plugin) {
		this.plugin = plugin;
	}
	
	public Player getPlayer() {
		return Bukkit.getServer().getPlayer(this.name);
	}
	
	public void viewMailInbox() {
		int mailCount = getMailCount();
		Inventory inboxMenu = Bukkit.createInventory(null, (int) (9 * (Math.ceil((double)mailCount / 9))) , mailCount == 0 ? "My inbox (empty)" : "My inbox");
		if (mailCount != 0) {
			try (Statement statement = SQL.connection.createStatement()) {
				try (ResultSet rs = statement.executeQuery("SELECT date_sent, player_sender, message_text FROM " + SQL.database + "." + TableType.Mail.tableName + " WHERE player_recipient_UUID='" + getPlayer().getUniqueId().toString() + "';")) {
					while (rs.next()) {
						inboxMenu.addItem(EvilBook.getBook(rs.getString("date_sent"), rs.getString("player_sender"), Arrays.asList(rs.getString("message_text"))));
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		getPlayer().openInventory(inboxMenu);
	}
	
	public void updateNametag(final String prefix, final String suffix)
	{
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
		{
			@Override
			public void run() {
				NametagManager.overlap(name, prefix, suffix);
			}
		});
	}
	
	public Boolean isCanEditWorld(World world) {
		if (EvilBook.isInSurvival(world) && !this.rank.isHigher(Rank.BUILDER)) {
			getPlayer().sendMessage("§7Survival lands require Creator rank to edit");
			return false;
		} else if (world.getName().equals("FlatLand") && !this.rank.isHigher(Rank.BUILDER)) {
			getPlayer().sendMessage("§7Flatlands require Creator rank to edit");
			return false;
		} else if (world.getName().equals("SkyLand") && !this.rank.isHigher(Rank.ARCHITECT)) {
			getPlayer().sendMessage("§7Skylands require Designer rank to edit");
			return false;
		}
		return true;
	}
	
	public int getMailCount() {
		try (Statement statement = SQL.connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM " + SQL.database + "." + TableType.Mail.tableName + " WHERE player_recipient_UUID='" + getPlayer().getUniqueId().toString() + "';")) {
				while (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}
	
	public int getAchievementScore() {
		int score = 0;
		for (Achievement ach : this.achievements) score += ach.getValue();
		return score;
	}
	
	/**
	 * Returns if the player has the specified achievement
	 * @param achievement The achievement to check for
	 * @return If the player has the achievement
	 */
	public Boolean hasAchievement(Achievement achievement) {
		return this.achievements.contains(achievement);
	}
	
	/**
	 * Reward an achievement to the player if they
	 * don't already have the achievement
	 * @param achievement The achievement to reward
	 */
	public void addAchievement(Achievement achievement) {
		if (!hasAchievement(achievement)) {
			this.achievements.add(achievement);
			if (achievement != Achievement.GLOBAL_COMMAND_DONATE) {
				getPlayer().sendMessage(ChatColor.GREEN + "" + achievement.getIcon() + ChatColor.BLUE + " You got the " + ChatColor.YELLOW + "[" + achievement.getName() + "] " + ChatColor.BLUE + "achievement " + ChatColor.GREEN + achievement.getIcon());
				if (achievement.getReward() != null) getPlayer().sendMessage(ChatColor.GRAY + "You have unlocked the " + achievement.getReward());
			}
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				if (!player.getName().equals(this.name)) player.sendMessage(ChatColor.GREEN + "" + achievement.getIcon() + ChatColor.BLUE + " " + this.name + " got the " + ChatColor.YELLOW + "[" + achievement.getName() + "] " + ChatColor.BLUE + "achievement " + ChatColor.GREEN + achievement.getIcon());
				player.playSound(getPlayer().getLocation(), Sound.FIREWORK_TWINKLE, 99.0F, 1.0F);
			}
		}
	}
	
	/**
	 * Returns if the specified player is muted
	 * @param playerName The player to test with
	 * @return If the player if muted
	 */
	public Boolean isMuted(String playerName) {
		return this.mutedPlayers.contains(playerName.toLowerCase(Locale.UK));
	}

	/**
	 * Set a player profile property
	 * @param property The property to set
	 * @param value The value of the property
	 */
	public void setProperty(String property, String value) {
		SQL.setProperty(TableType.PlayerProfile, this.name, property, value);
	}
	
	/**
	 * Get a player profile property
	 * @param property The property to get
	 * @return The value of the property
	 */
	public String getProperty(String property) {
		return SQL.getProperty(TableType.PlayerProfile, this.name, property);
	}
	
	/**
	 * Get a player profile property
	 * @param property The property to get
	 * @return The value of the property
	 */
	public String getProperty(TableType tableType, String property) {
		return SQL.getProperty(tableType, this.name, property);
	}

	/**
	 * Set the player's last position in the world
	 * Use when they teleport to a different world
	 * @param location Their location in the world
	 */
	public void setWorldLastPosition(Location location) {
		String worldName = location.getWorld().getName();
		if (!worldName.contains("SkyBlock/")) {
			if (worldName.contains("Private worlds/")) worldName = worldName.split("Private worlds/")[1];
			SQL.setProperty(TableType.PlayerLocation, this.name, worldName, location.getX() + ">" + location.getY() + ">" + location.getZ());
		}
	}

	/**
	 * Get the player's last position in the world
	 * @param world The world to get their last location in
	 */
	public Location getWorldLastPosition(String world) {
		String worldName = world;
		if (worldName.contains("Private worlds/")) worldName = worldName.split("Private worlds/")[1];
		String result = SQL.getProperty(TableType.PlayerLocation, this.name, worldName);
		if (result == null) return Bukkit.getServer().getWorld(world).getSpawnLocation();
		String[] location = result.split(">");
		return new Location(Bukkit.getServer().getWorld(world), Double.valueOf(location[0]), Double.valueOf(location[1]), Double.valueOf(location[2]));
	}
	
	/**
	 * Save the player profile
	 */
	public abstract void saveProfile();
	
	/**
	 * Update the player's name in the player list
	 */
	public abstract void updatePlayerListName();

	public abstract void setNameTitle(String title);
}
