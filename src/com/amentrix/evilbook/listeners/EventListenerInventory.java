package com.amentrix.evilbook.listeners;

import java.sql.PreparedStatement;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.material.SpawnEgg;

import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.sql.SQL;

/**
 * Inventory event listener
 * @author Reece Aaron Lecrivain
 */
public class EventListenerInventory implements Listener {
	public final static Inventory entitySpawnerMenu = Bukkit.createInventory(null, 27, "Assign creature to spawner");
	static {
		entitySpawnerMenu.setItem(0, new ItemStack(Material.MONSTER_EGG, 1, (short) 50));
		entitySpawnerMenu.setItem(1, new ItemStack(Material.MONSTER_EGG, 1, (short) 51));
		entitySpawnerMenu.setItem(2, new ItemStack(Material.MONSTER_EGG, 1, (short) 52));
		entitySpawnerMenu.setItem(3, new ItemStack(Material.MONSTER_EGG, 1, (short) 53));
		entitySpawnerMenu.setItem(4, new ItemStack(Material.MONSTER_EGG, 1, (short) 54));
		entitySpawnerMenu.setItem(5, new ItemStack(Material.MONSTER_EGG, 1, (short) 55));
		entitySpawnerMenu.setItem(6, new ItemStack(Material.MONSTER_EGG, 1, (short) 56));
		entitySpawnerMenu.setItem(7, new ItemStack(Material.MONSTER_EGG, 1, (short) 57));
		entitySpawnerMenu.setItem(8, new ItemStack(Material.MONSTER_EGG, 1, (short) 58));
		entitySpawnerMenu.setItem(9, new ItemStack(Material.MONSTER_EGG, 1, (short) 59));
		entitySpawnerMenu.setItem(10, new ItemStack(Material.MONSTER_EGG, 1, (short) 60));
		entitySpawnerMenu.setItem(11, new ItemStack(Material.MONSTER_EGG, 1, (short) 61));
		entitySpawnerMenu.setItem(12, new ItemStack(Material.MONSTER_EGG, 1, (short) 62));
		entitySpawnerMenu.setItem(13, new ItemStack(Material.MONSTER_EGG, 1, (short) 65));
		entitySpawnerMenu.setItem(14, new ItemStack(Material.MONSTER_EGG, 1, (short) 66));
		entitySpawnerMenu.setItem(15, new ItemStack(Material.MONSTER_EGG, 1, (short) 90));
		entitySpawnerMenu.setItem(16, new ItemStack(Material.MONSTER_EGG, 1, (short) 91));
		entitySpawnerMenu.setItem(17, new ItemStack(Material.MONSTER_EGG, 1, (short) 92));
		entitySpawnerMenu.setItem(18, new ItemStack(Material.MONSTER_EGG, 1, (short) 93));
		entitySpawnerMenu.setItem(19, new ItemStack(Material.MONSTER_EGG, 1, (short) 94));
		entitySpawnerMenu.setItem(20, new ItemStack(Material.MONSTER_EGG, 1, (short) 95));
		entitySpawnerMenu.setItem(21, new ItemStack(Material.MONSTER_EGG, 1, (short) 96));
		entitySpawnerMenu.setItem(22, new ItemStack(Material.MONSTER_EGG, 1, (short) 98));
		entitySpawnerMenu.setItem(23, new ItemStack(Material.MONSTER_EGG, 1, (short) 100));
	}
	
	/**
	 * Called when a player clicks a slot in an inventory
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getClickedInventory() != null) {
			// Prevent the player editing read only inventories
			if (event.getView().getTopInventory().getName().equals(entitySpawnerMenu.getName())) event.setCancelled(true);
			// Handle taking mail out of a player mailbox
			if (event.getClickedInventory().getTitle().equals("My inbox") && event.getCurrentItem().getType() == Material.WRITTEN_BOOK) {
				Player player = (Player)event.getWhoClicked();
				BookMeta book = (BookMeta) event.getCurrentItem().getItemMeta();
				try (PreparedStatement statement = SQL.connection.prepareStatement("DELETE FROM " + SQL.database + ".`evilbook-mail` WHERE player_recipient=? AND message_text=?")) {
					statement.setString(1, player.getName());
					statement.setString(2, book.getPage(1));
					statement.executeUpdate();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
			// Handle selecting a monster egg on the entity spawner menu
			if (event.getClickedInventory().getName().equals(entitySpawnerMenu.getName()) && event.getCurrentItem().getType() == Material.MONSTER_EGG) {
				Player player = (Player)event.getWhoClicked();
				BlockState block = EvilBook.getProfile(player).lastBlockInteraction.getBlock().getState();
				((CreatureSpawner)block).setSpawnedType(new SpawnEgg(event.getCurrentItem().getData().getData()).getSpawnedType());
				block.update();
				player.closeInventory();
			}
		}
	}
}
