package com.amentrix.evilbook.main;

/**
 * Rank enumerator
 * @author Reece Aaron Lecrivain
 */
public enum Rank {
	// Normal ranks
	Builder("Builder", "§0[§EBuilder§0]", "E", 0),
	AdvancedBuilder("Advanced Builder", "§0[§5Adv.Builder§0]", "5", 0),
	Architect("Architect", "§0[§DArchitect§0]", "D", 0),
	Moderator("Moderator", "§0[§9Moderator§0]", "9", 0),
	Police("Police", "§0[§3Police§0]", "3", 0),
	// Staff ranks
	CopperStaff("Copper Staff", "§0[§ECopperStaff§0]", "E", 1000),
	SilverStaff("Silver Staff", "§0[§7SilverStaff§0]", "7", 2000),
	GoldStaff("Gold Staff", "§0[§6GoldStaff§0]", "6", 3000),
	LapisStaff("Lapis Staff", "§0[§1LapisStaff§0]", "1", 4000),
	DiamondStaff("Diamond Staff", "§0[§BDiamondStaff§0]", "B", 5000),
	// Donator ranks
	Admin("Admin", "§0[§4Admin§0]", "4", 10000),
	Councillor("Councillor", "§0[§ACouncillor§0]", "A", 25000),
	Elite("Elite", "§0[§CElite§0]", "C", 50000),
	Investor("Investor", "§0[§6Investor§0]", "6", 75000),
	Tycoon("Tycoon", "§0[§6§OTycoon§0]", "6", Integer.MAX_VALUE),
	// Server Host rank
	ServerHost("Server Host", "§0[§BServerHost§0]", "B", Integer.MAX_VALUE);
	
	private String prefix, color, name;
	private Integer evilEditAreaLimit;
	
	public Boolean isHigher(Rank compareRank) { return this.compareTo(compareRank) > 0 ? true : false; }
	
	public Boolean isCustomRank() { return isHigher(Rank.Elite); }
	
	/**
	 * @return If the player is an admin
	 */
	public Boolean isAdmin() { return this.compareTo(Rank.Admin) >= 0 ? true : false; }
	
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
	Rank (String name, String prefix, String color, Integer evilEditAreaLimit) {
		this.name = name;
		this.prefix = prefix;
		this.color = color;
		this.evilEditAreaLimit = evilEditAreaLimit;
	}
	
	/**
	 * Return the next rank on the rank ladder
	 * @return The next rank on the rank ladder
	 */
	public Rank getNextRank() {
		switch (this) {
		case Builder:
			return Rank.AdvancedBuilder;
		case AdvancedBuilder:
			return Rank.Architect;
		case Architect:
			return Rank.Moderator;
		case Moderator:
			return Rank.Police;
		case Police:
			return Rank.CopperStaff;
		case CopperStaff:
			return Rank.SilverStaff;
		case SilverStaff:
			return Rank.GoldStaff;
		case GoldStaff:
			return Rank.LapisStaff;
		case LapisStaff:
			return Rank.DiamondStaff;
		case DiamondStaff:
			return Rank.Admin;
		case Admin:
			return Rank.Councillor;
		case Councillor:
			return Rank.Elite;
		case Elite:
			return Rank.Investor;
		case Investor:
			return Rank.Tycoon;
		case Tycoon:
			return Rank.ServerHost;
		case ServerHost:
			return Rank.ServerHost;
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
		case Builder:
			return Rank.Builder;
		case AdvancedBuilder:
			return Rank.Builder;
		case Architect:
			return Rank.AdvancedBuilder;
		case Moderator:
			return Rank.Architect;
		case Police:
			return Rank.Moderator;
		case CopperStaff:
			return Rank.Police;
		case SilverStaff:
			return Rank.CopperStaff;
		case GoldStaff:
			return Rank.SilverStaff;
		case LapisStaff:
			return Rank.GoldStaff;
		case DiamondStaff:
			return Rank.LapisStaff;
		case Admin:
			return Rank.DiamondStaff;
		case Councillor:
			return Rank.Admin;
		case Elite:
			return Rank.Councillor;
		case Investor:
			return Rank.Elite;
		case Tycoon:
			return Rank.Investor;
		case ServerHost:
			return Rank.Tycoon;
		default:
			break;
		}
		return this;
	}
}
