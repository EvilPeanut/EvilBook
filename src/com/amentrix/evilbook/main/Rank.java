package com.amentrix.evilbook.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

/**
 * Rank enumerator
 * @author Reece Aaron Lecrivain
 */
public enum Rank {
	// Normal ranks
	BUILDER("Builder", "§0[§EBuilder§0]", "E", 0, null),
	ADVANCED_BUILDER("Advanced Builder", "§0[§5Adv.Builder§0]", "5", 0, "§aUnlocked usage of anvil block\nUnlocked usage of saplings\nUnlocked usage of sand block\nUnlocked usage of gravel block\nUnlocked usage of bone meal\nUnlocked survival world\nUnlocked flatlands world"),
	ARCHITECT("Architect", "§0[§DArchitect§0]", "D", 0, "§aUnlocked smoke emitter effect\nUnlocked flames emitter effect\nUnlocked hearts emitter effect\nUnlocked redstone fumes emitter effect\nUnlocked slime emitter effect\nUnlocked masic critical hit emitter effect\nUnlocked enchantment table emitter effect\nUnlocked skylands world\nUnlocked /chimney"),
	MODERATOR("Moderator", "§0[§9Moderator§0]", "9", 0, null),
	POLICE("Police", "§0[§3Police§0]", "3", 0, null),
	// Staff ranks
	STAFF_COPPER("Copper Staff", "§0[§ECopper Staff§0]", "E", 1000, null),
	STAFF_SILVER("Silver Staff", "§0[§7Silver Staff§0]", "7", 2000, null),
	STAFF_GOLD("Gold Staff", "§0[§6Gold Staff§0]", "6", 3000, null),
	STAFF_LAPIS("Lapis Staff", "§0[§1Lapis Staff§0]", "1", 4000, null),
	STAFF_DIAMOND("Diamond Staff", "§0[§BDiamond Staff§0]", "B", 5000, null),
	// Donator ranks
	ADMIN("Admin", "§0[§4Admin§0]", "4", 10000, null),
	COUNCILLOR("Councillor", "§0[§ACouncillor§0]", "A", 25000, null),
	ELITE("Elite", "§0[§CElite§0]", "C", 50000, null),
	INVESTOR("Investor", "§0[§6Investor§0]", "6", 75000, null),
	TYCOON("Tycoon", "§0[§6§OTycoon§0]", "6", Integer.MAX_VALUE, null),
	// Server Host rank
	SERVER_HOST("Server Host", "§0[§BServer Host§0]", "B", Integer.MAX_VALUE, null);
	
	private String prefix, color, name, unlocks;
	private Integer evilEditAreaLimit;
	
	public Boolean isHigher(Rank compareRank) { return this.compareTo(compareRank) > 0 ? true : false; }
	
	public Boolean isCustomRank() { return isHigher(Rank.ELITE); }
	
	/**
	 * @return If the player is an ADMIN
	 */
	public Boolean isAdmin() { return this.compareTo(Rank.ADMIN) >= 0 ? true : false; }
	
	/**
	 * @return The name of the rank
	 */
	public String getName(){ return this.name; }
	
	/**
	 * @return The prefix of the rank
	 */
	public String getPrefix(PlayerProfile player) { 
		if (this.isCustomRank()) return ((PlayerProfileAdmin)player).customRankPrefix;
		return this.prefix;
	}
	
	/**
	 * @return The color of the rank
	 */
	public String getColor(PlayerProfile player) {
		if (this.isCustomRank()) return ((PlayerProfileAdmin)player).customRankColor;
		return this.color;
	}
	
	/**
	 * @return The color of the rank, for non-player related use
	 */
	public String getColor() {
		return this.color;
	}
	
	/**
	 * @return The evil edit area limit of the rank
	 */
	public Integer getEvilEditAreaLimit(){ return this.evilEditAreaLimit; }
	
	/**
	 * Define a new rank
	 * @param name The name of the rank
	 * @param prefix The prefix of the rank
	 * @param color The color of the rank
	 * @param evilEditAreaLimit The evil edit area limit of the rank
	 */
	Rank (String name, String prefix, String color, Integer evilEditAreaLimit, String unlocks) {
		this.name = name;
		this.prefix = prefix;
		this.color = color;
		this.evilEditAreaLimit = evilEditAreaLimit;
		this.unlocks = unlocks;
	}
	
	/**
	 * Return the next rank on the rank ladder
	 * @return The next rank on the rank ladder
	 */
	public Rank getNextRank() {
		switch (this) {
		case BUILDER:
			return Rank.ADVANCED_BUILDER;
		case ADVANCED_BUILDER:
			return Rank.ARCHITECT;
		case ARCHITECT:
			return Rank.MODERATOR;
		case MODERATOR:
			return Rank.POLICE;
		case POLICE:
			return Rank.STAFF_COPPER;
		case STAFF_COPPER:
			return Rank.STAFF_SILVER;
		case STAFF_SILVER:
			return Rank.STAFF_GOLD;
		case STAFF_GOLD:
			return Rank.STAFF_LAPIS;
		case STAFF_LAPIS:
			return Rank.STAFF_DIAMOND;
		case STAFF_DIAMOND:
			return Rank.ADMIN;
		case ADMIN:
			return Rank.COUNCILLOR;
		case COUNCILLOR:
			return Rank.ELITE;
		case ELITE:
			return Rank.INVESTOR;
		case INVESTOR:
			return Rank.TYCOON;
		case TYCOON:
			return Rank.SERVER_HOST;
		case SERVER_HOST:
			return Rank.SERVER_HOST;
		default:
			break;
		}
		return this;
	}
	
	/**
	 * Return the previous rank on the rank ladder
	 * @return The previous rank on the rank ladder
	 */
	public Rank getPreviousRank() {
		switch (this) {
		case BUILDER:
			return Rank.BUILDER;
		case ADVANCED_BUILDER:
			return Rank.BUILDER;
		case ARCHITECT:
			return Rank.ADVANCED_BUILDER;
		case MODERATOR:
			return Rank.ARCHITECT;
		case POLICE:
			return Rank.MODERATOR;
		case STAFF_COPPER:
			return Rank.POLICE;
		case STAFF_SILVER:
			return Rank.STAFF_COPPER;
		case STAFF_GOLD:
			return Rank.STAFF_SILVER;
		case STAFF_LAPIS:
			return Rank.STAFF_GOLD;
		case STAFF_DIAMOND:
			return Rank.STAFF_LAPIS;
		case ADMIN:
			return Rank.STAFF_DIAMOND;
		case COUNCILLOR:
			return Rank.ADMIN;
		case ELITE:
			return Rank.COUNCILLOR;
		case INVESTOR:
			return Rank.ELITE;
		case TYCOON:
			return Rank.INVESTOR;
		case SERVER_HOST:
			return Rank.TYCOON;
		default:
			break;
		}
		return this;
	}
	
	public static boolean contains(String test) {
	    for (Rank rank : Rank.values()) {
	        if (rank.name().equals(test)) {
	            return true;
	        }
	    }
	    return false;
	}
}
