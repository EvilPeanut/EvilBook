package com.amentrix.evilbook.statistics;

import com.amentrix.evilbook.achievement.Achievement;
import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;

/**
 * Player statistics framework
 * @author Reece Aaron Lecrivain
 */
public class PlayerStatistics {
	public static String getStatistic(String playerName, PlayerStatistic statistic) {
		String value = SQL.getProperty(TableType.PlayerStatistics, playerName, statistic.columnName);
		if (value == null) return "0";
		return value;
	}

	private static void setStatistic(String playerName, PlayerStatistic statistic, int value) {
		SQL.setProperty(TableType.PlayerStatistics, playerName, statistic.columnName, Integer.toString(value));
		switch (statistic) {
		case KILLED_RARES:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_RARE);
			if (value >= 2) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_RARE_II);
			if (value >= 5) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_RARE_III);
			if (value >= 10) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_RARE_IV);
			break;
		case KILLED_BLAZES: 
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_BLAZE);
			if (value >= 5) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_BLAZE_II);
			break;
		case KILLED_CAVESPIDERS:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_CAVESPIDER);
			if (value >= 10) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_CAVESPIDER_II);
			break;
		case KILLED_CREEPERS:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_CREEPER);
			if (value >= 10) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_CREEPER_II);
			break;
		case KILLED_ENDERDRAGONS:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_ENDERDRAGON);
			break;
		case KILLED_ENDERMEN:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_ENDERMAN);
			if (value >= 5) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_ENDERMAN_II);
			break;
		case KILLED_GHASTS:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_GHAST);
			if (value >= 5) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_GHAST_II);
			break;
		case KILLED_MAGMACUBES:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_MAGMACUBE);
			if (value >= 5) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_MAGMACUBE_II);
			break;
		case KILLED_PIGS:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_PIG);
			if (value >= 50) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_PIG_II);
			break;
		case KILLED_PLAYERS:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_PLAYER);
			if (value >= 10) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_PLAYER_II);
			break;
		case KILLED_SILVERFISH:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_SILVERFISH);
			if (value >= 10) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_SILVERFISH_II);
			break;
		case KILLED_SKELETONS:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_SKELETON);
			if (value >= 10) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_SKELETON_II);
			break;
		case KILLED_SLIMES:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_SLIME);
			if (value >= 10) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_SLIME_II);
			break;
		case KILLED_SPIDERS:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_SPIDER);
			if (value >= 10) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_SPIDER_II);
			break;
		case KILLED_VILLAGERS:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_VILLAGER);
			if (value >= 10) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_VILLAGER_II);
			break;
		case KILLED_WITCHES:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_WITCH);
			if (value >= 2) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_WITCH_II);
			break;
		case KILLED_WITHERS:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_WITHER);
			break;
		case KILLED_WOLVES:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_WOLF);
			if (value >= 5) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_WOLF_II);
			break;
		case KILLED_ZOMBIEPIGS:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_ZOMBIEPIG);
			if (value >= 10) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_ZOMBIEPIG_II);
			break;
		case KILLED_ZOMBIES:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_ZOMBIE);
			if (value >= 10) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_KILL_ZOMBIE_II);
			break;
		case MINED_COAL:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_COAL);
			if (value >= 50) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_COAL_II);
			if (value >= 100) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_COAL_III);
			if (value >= 250) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_COAL_IV);
			break;
		case MINED_DIAMOND:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_DIAMOND);
			if (value >= 10) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_DIAMOND_II);
			if (value >= 25) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_DIAMOND_III);
			if (value >= 50) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_DIAMOND_IV);
			break;
		case MINED_EMERALD:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_EMERALD);
			if (value >= 2) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_EMERALD_II);
			if (value >= 5) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_EMERALD_III);
			if (value >= 10) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_EMERALD_IV);
			break;
		case MINED_GOLD:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_GOLD);
			if (value >= 25) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_GOLD_II);
			if (value >= 50) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_GOLD_III);
			if (value >= 75) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_GOLD_IV);
			break;
		case MINED_IRON:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_IRON);
			if (value >= 50) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_IRON_II);
			if (value >= 100) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_IRON_III);
			if (value >= 250) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_IRON_IV);
			break;
		case MINED_LAPIS:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_LAPIS);
			if (value >= 25) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_LAPIS_II);
			if (value >= 50) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_LAPIS_III);
			if (value >= 75) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_LAPIS_IV);
			break;
		case MINED_NETHERQUARTZ:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_NETHERQUARTZ);
			if (value >= 50) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_NETHERQUARTZ_II);
			if (value >= 100) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_NETHERQUARTZ_III);
			if (value >= 250) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_NETHERQUARTZ_IV);
			break;
		case MINED_REDSTONE:
			EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_REDSTONE);
			if (value >= 25) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_REDSTONE_II);
			if (value >= 50) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_REDSTONE_III);
			if (value >= 100) EvilBook.getProfile(playerName).addAchievement(Achievement.SURVIVAL_MINE_REDSTONE_IV);
			break;
		default:
			break;
		}
	}
	
	public static void incrementStatistic(String playerName, PlayerStatistic statistic, int increment) {
		setStatistic(playerName, statistic, Integer.parseInt(getStatistic(playerName, statistic)) + increment);
	}
}
