package com.amentrix.evilbook.statistics;

/**
 * Global statistic enumerator
 * @author Reece Aaron Lecrivain
 */
public enum GlobalStatistic {
	EconomyGrowth("economy_growth"),
	EconomyTrade("economy_trade"),
	LoginTotal("login_total"),
	LoginNewPlayers("new_players"),
	CommandsExecuted("commands_executed"),
	MessagesSent("messages_sent"),
	BlocksBroken("blocks_broken"),
	BlocksPlaced("blocks_placed");
	
	public String columnName;
	
	GlobalStatistic(String columnName) {
		this.columnName = columnName;
	}
}
