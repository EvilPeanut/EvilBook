package com.amentrix.evilbook.minigame;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.amentrix.evilbook.main.EvilBook;

public class TowerDefenseMinigame {
	public MinigameDifficulty difficulty;
	public int money = 700;
	public int lives = 0;
	public int wave = 1;
	
	public TowerDefenseMinigame(Player hostPlayer, MinigameDifficulty difficulty, EvilBook plugin) {
		//
		// Joining message
		//
		hostPlayer.sendMessage("§bWelcome to the Tower Defense minigame");
		hostPlayer.sendMessage("§3Place towers and battle your way through the waves of mobs");
		
		//
		// Difficulty handling
		//	
		this.difficulty = difficulty;
		if (difficulty == MinigameDifficulty.EASY) {
			lives = 20;
		} else if (difficulty == MinigameDifficulty.NORMAL) {
			lives = 10;
		} else {
			lives = 5;
		}
		
		//
		// Clear host player's inventory
		//
		hostPlayer.getInventory().clear();
		
		//
		// Add the tower spawning items to the player inventory
		//
		ItemStack arrowTowerItem = new ItemStack(Material.ARROW);
		ItemMeta arrowTowerItemMeta = arrowTowerItem.getItemMeta();
		arrowTowerItemMeta.setDisplayName("Dart Tower");
		arrowTowerItemMeta.setLore(Arrays.asList("Damage: ?", "Range: ?", "Cost: $?"));
		arrowTowerItem.setItemMeta(arrowTowerItemMeta);
		hostPlayer.getInventory().addItem(arrowTowerItem);
		
		ItemStack mageTowerItem = new ItemStack(Material.BLAZE_POWDER);
		ItemMeta mageTowerItemMeta = arrowTowerItem.getItemMeta();
		mageTowerItemMeta.setDisplayName("Mage Tower");
		mageTowerItemMeta.setLore(Arrays.asList("Damage: ?", "Range: ?", "Cost: $?"));
		mageTowerItem.setItemMeta(mageTowerItemMeta);
		hostPlayer.getInventory().addItem(mageTowerItem);
		
		ItemStack bombTowerItem = new ItemStack(Material.FIREWORK_CHARGE);
		ItemMeta bombTowerItemMeta = arrowTowerItem.getItemMeta();
		bombTowerItemMeta.setDisplayName("Bomb Tower");
		bombTowerItemMeta.setLore(Arrays.asList("Damage: ?", "Range: ?", "Cost: $?"));
		bombTowerItem.setItemMeta(bombTowerItemMeta);
		hostPlayer.getInventory().addItem(bombTowerItem);
		
		ItemStack superDartTowerItem = new ItemStack(Material.BOW);
		ItemMeta superDartTowerItemMeta = arrowTowerItem.getItemMeta();
		superDartTowerItemMeta.setDisplayName("Super Dart Tower");
		superDartTowerItemMeta.setLore(Arrays.asList("Damage: ?", "Range: ?", "Cost: $?"));
		superDartTowerItem.setItemMeta(superDartTowerItemMeta);
		hostPlayer.getInventory().addItem(superDartTowerItem);
		
		ItemStack snowballTowerItem = new ItemStack(Material.SNOW_BALL);
		ItemMeta snowballTowerItemMeta = arrowTowerItem.getItemMeta();
		snowballTowerItemMeta.setDisplayName("Snowball Tower");
		snowballTowerItemMeta.setLore(Arrays.asList("Damage: ?", "Range: ?", "Cost: $?"));
		snowballTowerItem.setItemMeta(snowballTowerItemMeta);
		hostPlayer.getInventory().addItem(snowballTowerItem);
		
		ItemStack eggTowerItem = new ItemStack(Material.EGG);
		ItemMeta eggTowerItemMeta = arrowTowerItem.getItemMeta();
		eggTowerItemMeta.setDisplayName("Egg Tower");
		eggTowerItemMeta.setLore(Arrays.asList("Damage: ?", "Range: ?", "Cost: $?"));
		eggTowerItem.setItemMeta(eggTowerItemMeta);
		hostPlayer.getInventory().addItem(eggTowerItem);
		
		ItemStack witchHutTowerItem = new ItemStack(Material.POTION);
		ItemMeta witchHutTowerItemMeta = arrowTowerItem.getItemMeta();
		witchHutTowerItemMeta.setDisplayName("Witch Hut Tower");
		witchHutTowerItemMeta.setLore(Arrays.asList("Damage: ?", "Range: ?", "Cost: $?"));
		witchHutTowerItem.setItemMeta(witchHutTowerItemMeta);
		hostPlayer.getInventory().addItem(witchHutTowerItem);
		
		ItemStack butterTowerItem = new ItemStack(Material.GOLD_INGOT);
		ItemMeta butterTowerItemMeta = arrowTowerItem.getItemMeta();
		butterTowerItemMeta.setDisplayName("Butter Tower");
		butterTowerItemMeta.setLore(Arrays.asList("Damage: ?", "Range: ?", "Cost: $?"));
		butterTowerItem.setItemMeta(butterTowerItemMeta);
		hostPlayer.getInventory().addItem(butterTowerItem);
		
		//
		// Show the player their game status (Money, lives, wave)
		//
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		
		Team team = board.registerNewTeam("TowerDefense");
		team.addPlayer(hostPlayer);
		
		Objective objective = board.registerNewObjective(ChatColor.RED + "⚔ " + ChatColor.LIGHT_PURPLE + "Stats" + ChatColor.RED + " ⚔", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		Score moneyScore = objective.getScore(ChatColor.GREEN + "Money");
		moneyScore.setScore(money);
		
		Score livesScore = objective.getScore(ChatColor.GREEN + "Lives");
		livesScore.setScore(lives);
		
		Score waveScore = objective.getScore(ChatColor.GREEN + "Wave");
		waveScore.setScore(wave);
		
		hostPlayer.setScoreboard(board);
		
		//
		// Register event listeners
		//
		PluginManager pluginManager = plugin.getServer().getPluginManager();
		pluginManager.registerEvents(new TowerDefenseMinigameListener(), plugin);
	}
}
