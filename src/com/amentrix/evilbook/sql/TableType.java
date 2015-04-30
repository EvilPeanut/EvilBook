package com.amentrix.evilbook.sql;

/**
 * TableType enumerator
 * @author Reece Aaron Lecrivain
 */
public enum TableType {
	PlayerProfile("`evilbook-playerprofiles`", "player_name", "player_name, rank, rank_prefix, money, name_title, name_alias, muted_players, warp_list, run_amplifier, walk_amplifier, fly_amplifier, jump_amplifier, achievement_list, last_login, total_logins, inventory_creative, inventory_survival, inventory_skyblock, ip, evilbook_version"),
	PlayerLocation("`evilbook-playerlocations`", "player_name", null),
	PlayerStatistics("`evilbook-playerstatistics`", "player_name", "player_name, mined_coal, mined_iron, mined_lapis, mined_gold, mined_diamond, mined_redstone, "
			+ "mined_emerald, mined_netherquartz, killed_pigs, killed_villagers, killed_cavespiders, killed_endermen, killed_spiders, killed_wolves, killed_zombiepigs, "
			+ "killed_blazes, killed_creepers, killed_ghasts, killed_magmacubes, killed_silverfish, killed_skeletons, killed_slimes, killed_witches, killed_zombies, "
			+ "killed_enderdragons, killed_withers, killed_players, killed_rares"),
	DynamicSign("`evilbook-dynamicsigns`", null, "world, x, y, z, line1, line2, line3, line4"),
	Emitter("`evilbook-emitters`", null, "world, x, y, z, effect, data, frequency"),
	Region("`evilbook-regions`", "region_name", "region_name, world, x1, y1, z1, x2, y2, z2, protected, player_name, welcome_message, leave_message, allowed_players, warp_name"),
	ContainerProtection("`evilbook-protectedcontainers`", null, "world, x, y, z, player_name"),
	Warps("`evilbook-warps`", "warp_name", "warp_name, location"),
	Statistics("`evilbook-statistics`", "date", "date, economy_growth, economy_trade, login_total, new_players, commands_executed, messages_sent, blocks_broken, blocks_placed"),
	Mail("`evilbook-mail`", null, "player_sender, player_recipient_UUID, message_text, date_sent"),
	CommandBlock("`evilbook-commandblock`", null, "player, world, x, y, z"),
	CommandStatistics("`evilbook-commandstatistics`", "command_name", "command_name, execution_count");
	
	public String tableName;
	String keyName;
	String fields;
	
	TableType(String tableName, String keyName, String fields) {
		this.tableName = tableName;
		this.keyName = keyName;
		this.fields = fields;
	}
}
