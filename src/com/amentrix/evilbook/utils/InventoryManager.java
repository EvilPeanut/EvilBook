package com.amentrix.evilbook.utils;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.amentrix.evilbook.sql.SQL;
import com.amentrix.evilbook.sql.TableType;

public class InventoryManager {
	public static void get(Player player, String inventory) {
		player.getInventory().clear();
		player.getInventory().setHelmet(new ItemStack(Material.AIR));
		player.getInventory().setChestplate(new ItemStack(Material.AIR));
		player.getInventory().setLeggings(new ItemStack(Material.AIR));
		player.getInventory().setBoots(new ItemStack(Material.AIR));
				
		try {
			YamlConfiguration config = new YamlConfiguration();
			String rawInventory = SQL.getString(TableType.PlayerProfile, "inventory_" + inventory, player.getName());
			
			if (rawInventory != null) {
				config.loadFromString(rawInventory);
				for (int i = 0; i < player.getInventory().getSize(); i++) {
					if (config.get(Integer.toString(i)) != null) 
						player.getInventory().setItem(i, (ItemStack)config.get(Integer.toString(i)));
				}
				player.getInventory().setHelmet((ItemStack)config.get("head"));
				player.getInventory().setChestplate((ItemStack)config.get("chest"));
				player.getInventory().setLeggings((ItemStack)config.get("legs"));
				player.getInventory().setBoots((ItemStack)config.get("boots"));
				player.setHealth((double)config.get("health"));
				player.setFoodLevel((int)config.get("hunger"));
				player.setLevel((int)config.get("level"));
				player.setExp((float)config.getDouble("xp"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void set(Player player, String inventory) {
		YamlConfiguration config = new YamlConfiguration();
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack item = player.getInventory().getItem(i);
			if (item != null) config.set(Integer.toString(i), item);
		}
		config.set("head", player.getInventory().getHelmet());
		config.set("chest", player.getInventory().getChestplate());
		config.set("legs", player.getInventory().getLeggings());
		config.set("boots", player.getInventory().getBoots());
		config.set("health", player.getHealth());
		config.set("hunger", player.getFoodLevel());
		config.set("level", player.getLevel());
		config.set("xp", player.getExp());
		SQL.setValue(TableType.PlayerProfile, "inventory_" + inventory, player.getName(), config.saveToString().replaceAll("'", "''"));
	}
}