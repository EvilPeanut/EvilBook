package com.amentrix.evilbook.achievement;

/**
 * Achievement enumerator
 * @author Reece Aaron Lecrivain
 */
public enum Achievement {
	// Achievements for mining in survival
	SURVIVAL_MINE_COAL("Coal Miner", "Mine coal in survival", '⚒', 5, null),
	SURVIVAL_MINE_COAL_II("Coal Miner II", "Mine 50 coal in survival", '⚒', 10, null),
	SURVIVAL_MINE_COAL_III("Coal Miner III", "Mine 100 coal in survival", '⚒', 20, null),
	SURVIVAL_MINE_COAL_IV("Coal Miner IV", "Mine 250 coal in survival", '⚒', 25, "Dirty title"),
	SURVIVAL_MINE_IRON("Iron Miner", "Mine iron in survival", '⚒', 5, null),
	SURVIVAL_MINE_IRON_II("Iron Miner II", "Mine 50 iron in survival", '⚒', 10, null),
	SURVIVAL_MINE_IRON_III("Iron Miner III", "Mine 100 iron in survival", '⚒', 20, null),
	SURVIVAL_MINE_IRON_IV("Iron Miner IV", "Mine 250 iron in survival", '⚒', 25, "Hardnut title"),
	SURVIVAL_MINE_LAPIS("Lapis Miner", "Mine lapis lazuli in survival", '⚒', 5, null),
	SURVIVAL_MINE_LAPIS_II("Lapis Miner II", "Mine 25 lapis lazuli in survival", '⚒', 10, null),
	SURVIVAL_MINE_LAPIS_III("Lapis Miner III", "Mine 50 lapis lazuli in survival", '⚒', 20, null),
	SURVIVAL_MINE_LAPIS_IV("Lapis Miner IV", "Mine 75 lapis lazuli in survival", '⚒', 25, "Ultramarine title"),
	SURVIVAL_MINE_GOLD("Gold Miner", "Mine gold in survival", '⚒', 10, null),
	SURVIVAL_MINE_GOLD_II("Gold Miner II", "Mine 25 gold in survival", '⚒', 20, null),
	SURVIVAL_MINE_GOLD_III("Gold Miner III", "Mine 50 gold in survival", '⚒', 25, null),
	SURVIVAL_MINE_GOLD_IV("Gold Miner IV", "Mine 75 gold in survival", '⚒', 50, "Golden title"),
	SURVIVAL_MINE_DIAMOND("Diamond Miner", "Mine diamond in survival", '⚒', 10, "Miner title"),
	SURVIVAL_MINE_DIAMOND_II("Diamond Miner II", "Mine 10 diamond in survival", '⚒', 20, null),
	SURVIVAL_MINE_DIAMOND_III("Diamond Miner III", "Mine 25 diamond in survival", '⚒', 25, null),
	SURVIVAL_MINE_DIAMOND_IV("Diamond Miner IV", "Mine 50 diamond in survival", '⚒', 50, "Hardcore title"),
	SURVIVAL_MINE_REDSTONE("Redstone Miner", "Mine redstone in survival", '⚒', 5, null),
	SURVIVAL_MINE_REDSTONE_II("Redstone Miner II", "Mine 25 redstone in survival", '⚒', 10, null),
	SURVIVAL_MINE_REDSTONE_III("Redstone Miner III", "Mine 50 redstone in survival", '⚒', 20, null),
	SURVIVAL_MINE_REDSTONE_IV("Redstone Miner IV", "Mine 100 redstone in survival", '⚒', 25, "Electric title"),
	SURVIVAL_MINE_EMERALD("Emerald Miner", "Mine emerald in survival", '⚒', 15, null),
	SURVIVAL_MINE_EMERALD_II("Emerald Miner II", "Mine 2 emerald in survival", '⚒', 30, null),
	SURVIVAL_MINE_EMERALD_III("Emerald Miner III", "Mine 5 emerald in survival", '⚒', 45, null),
	SURVIVAL_MINE_EMERALD_IV("Emerald Miner IV", "Mine 10 emerald in survival", '⚒', 50, "Iced title"),
	SURVIVAL_MINE_NETHERQUARTZ("Quartz Miner", "Mine nether quartz in survival", '⚒', 5, null),
	SURVIVAL_MINE_NETHERQUARTZ_II("Quartz Miner II", "Mine 50 nether quartz in survival", '⚒', 10, null),
	SURVIVAL_MINE_NETHERQUARTZ_III("Quartz Miner III", "Mine 100 nether quartz in survival", '⚒', 20, null),
	SURVIVAL_MINE_NETHERQUARTZ_IV("Quartz Miner IV", "Mine 250 nether quartz in survival", '⚒', 25, "Nether title"),
	// Achievements for killing mobs in survival
	SURVIVAL_KILL_PIG("Bringing Home The Bacon", "Kill a pig in survival", '⚔', 5, null),
	SURVIVAL_KILL_VILLAGER("Foe Homicide", "Kill a villager in survival", '⚔', 5, null),
	SURVIVAL_KILL_CAVESPIDER("Cave Spider Slayer", "Kill a cave spider in survival", '⚔', 10, null),
	SURVIVAL_KILL_ENDERMAN("Enderman Slayer", "Kill an enderman in survival", '⚔', 10, null),
	SURVIVAL_KILL_SPIDER("Spider Slayer", "Kill a spider in survival", '⚔', 5, null),
	SURVIVAL_KILL_WOLF("Man's Best Enemy", "Kill a wolf in survival", '⚔', 5, null),
	SURVIVAL_KILL_ZOMBIEPIG("Never Say Nether", "Kill a zombie pigman in survival", '⚔', 5, null),
	SURVIVAL_KILL_BLAZE("420 Blaze Is Dead", "Kill a blaze in survival", '⚔', 5, "Blazed title"),
	SURVIVAL_KILL_CREEPER("Boom Goes The Creeper!", "Kill a creeper in survival", '⚔', 5, null),
	SURVIVAL_KILL_GHAST("Ghastly Sight", "Kill a ghast in survival", '⚔', 10, null),
	SURVIVAL_KILL_MAGMACUBE("Magma Minion Slayer", "Kill a magma cube in survival", '⚔', 5, "Magma title"),
	SURVIVAL_KILL_SILVERFISH("Rodent Squasher", "Kill a silver fish in survival", '⚔', 5, null),
	SURVIVAL_KILL_SKELETON("Bone Rattler", "Kill a skeleton in survival", '⚔', 5, null),
	SURVIVAL_KILL_SLIME("Slime Slayer", "Kill a slime in survival", '⚔', 5, null),
	SURVIVAL_KILL_WITCH("The Witch Is Dead", "Kill a witch in survival", '⚔', 10, null),
	SURVIVAL_KILL_ZOMBIE("Deed Of The Undead", "Kill a zombie in survival", '⚔', 5, null),
	SURVIVAL_KILL_ENDERDRAGON("Dragon Slayer", "Kill an ender dragon in survival", '⚔', 15, null),
	SURVIVAL_KILL_WITHER("Wither Slayer", "Kill a wither in survival", '⚔', 15, null),
	SURVIVAL_KILL_PLAYER("Homicidal", "Kill another player in survival", '⚔', 5, "Psycho title"),
	SURVIVAL_KILL_PLAYER_II("Brutal", "Kill 10 players in survival", '⚔', 10, "Brutal title"),
	// Achievements for entering worlds
	GLOBAL_WORLD_FLATLAND("Infinite Intentions", "Enter the flat lands", '✈', 5, null),
	GLOBAL_WORLD_SKYLAND("Sky High", "Enter the sky lands", '✈', 5, null),
	GLOBAL_WORLD_SURVIVALLAND("Survival Of Fittest", "Enter the survival lands", '✈', 5, null),
	GLOBAL_WORLD_SURVIVALLANDNETHER("Nether Again", "Enter the survival nether", '✈', 5, null),
	GLOBAL_WORLD_SURVIVALLANDTHEEND("This Is The End", "Enter the survival end", '✈', 5, null),
	// Achievements for doing an action in survival/creative
	GLOBAL_DYNAMIC_SIGN("Dynamic Interests", "Create a dynamic sign", '✔', 5, null),
	GLOBAL_COMMAND_HELMET("New Headwear", "Use the /helmet command", '✔', 5, "Stylish title"),
	GLOBAL_COMMAND_DONATE("Server Supporter", "Use the /donate command", '✔', 5, "Supporter title"),
	GLOBAL_EQUIP_BUTTERARMOUR("Slippery Situation", "Equipt JacobClark's Butter Armour", '✔', 10, "Butter title");
	
	private String name, description, reward;
	private char icon;
	private int value;
	
	Achievement(String name, String description, char icon, int value, String reward) {
		this.name = name;
		this.description = description;
		this.icon = icon;
		this.value = value;
		this.reward = reward;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getReward() {
		return this.reward;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public char getIcon() {
		return this.icon;
	}
	
	public int getValue() {
		return this.value;
	}
}