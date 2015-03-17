package com.amentrix.evilbook.statistics;

/**
 * Player statistic enumerator
 * @author Reece Aaron Lecrivain
 */
public enum PlayerStatistic {
	MINED_COAL("mined_coal"),
	MINED_IRON("mined_iron"),
	MINED_LAPIS("mined_lapis"),
	MINED_GOLD("mined_gold"),
	MINED_DIAMOND("mined_diamond"),
	MINED_REDSTONE("mined_redstone"),
	MINED_EMERALD("mined_emerald"),
	MINED_NETHERQUARTZ("mined_netherquartz"),
	KILLED_PIGS("killed_pigs"),
	KILLED_VILLAGERS("killed_villagers"),
	KILLED_CAVESPIDERS("killed_cavespiders"),
	KILLED_ENDERMEN("killed_endermen"),
	KILLED_SPIDERS("killed_spiders"),
	KILLED_WOLVES("killed_wolves"),
	KILLED_ZOMBIEPIGS("killed_zombiepigs"),
	KILLED_BLAZES("killed_blazes"),
	KILLED_CREEPERS("killed_creepers"),
	KILLED_GHASTS("killed_ghasts"),
	KILLED_MAGMACUBES("killed_magmacubes"),
	KILLED_SILVERFISH("killed_silverfish"),
	KILLED_SKELETONS("killed_skeletons"),
	KILLED_SLIMES("killed_slimes"),
	KILLED_WITCHES("killed_witches"),
	KILLED_ZOMBIES("killed_zombies"),
	KILLED_ENDERDRAGONS("killed_enderdragons"),
	KILLED_WITHERS("killed_withers"),
	KILLED_PLAYERS("killed_players"),
	KILLED_RARES("killed_rares");
	
	String columnName;
	
	PlayerStatistic(String columnName) {
		this.columnName = columnName;
	}
}
