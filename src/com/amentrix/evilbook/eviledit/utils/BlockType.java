package com.amentrix.evilbook.eviledit.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.amentrix.evilbook.eviledit.Session;
import com.amentrix.evilbook.main.EvilBook;

public class BlockType {
	private Material material;
	private Byte data = 0;
	
	public BlockType(String blockName) {
		material = EvilBook.getBlockMaterial(blockName);
	}
	
	public Boolean isValid(Player player) {
		if (material == null) {
			player.sendMessage("§7Please enter a valid block name or ID");
			return false;
		} else if (Session.isBlocked(material)) {
			player.sendMessage("§cThis block is banned in EvilEdit");
			return false;
		}
		return true;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public Byte getData() {
		return data;
	}
	
	public void setData(String blockData) {
		if (EvilBook.isByte(blockData)) {
			data = Byte.parseByte(blockData);
		} else {
			if (material == Material.WOOL || material == Material.CARPET || material == Material.STAINED_GLASS 
					|| material == Material.STAINED_GLASS_PANE || material == Material.STAINED_CLAY) {
				// Dont use 'white' as switch case as 0 is the default data
				switch (blockData.toLowerCase()) {
				case "orange":
					data = 1;
					break;
				case "magenta":
					data = 2;
					break;
				case "lightblue":
					data = 3;
					break;
				case "yellow":
					data = 4;
					break;
				case "lime":
					data = 5;
					break;
				case "pink":
					data = 6;
					break;
				case "gray":
					data = 7;
					break;
				case "grey":
					data = 7;
					break;
				case "lightgray":
					data = 8;
					break;
				case "lightgrey":
					data = 8;
					break;
				case "cyan":
					data = 9;
					break;
				case "purple":
					data = 10;
					break;
				case "blue":
					data = 11;
					break;
				case "brown":
					data = 12;
					break;
				case "green":
					data = 13;
					break;
				case "red":
					data = 14;
					break;
				case "black":
					data = 15;
					break;
				default:
					break;
				}
			} else if (material == Material.STEP || material == Material.DOUBLE_STEP) {
				// Dont use 'stone' as switch case as 0 is the default data
				switch (blockData.toLowerCase()) {
				case "sandstone":
					data = 1;
					break;
				case "wood":
					data = 2;
					break;
				case "wooden":
					data = 2;
					break;
				case "cobblestone":
					data = 3;
					break;
				case "cobble":
					data = 3;
					break;
				case "brick":
					data = 4;
					break;
				case "stonebrick":
					data = 5;
					break;
				case "netherbrick":
					data = 6;
					break;
				case "quartz":
					data = 7;
					break;
				default:
					break;
				}
			} else if (material == Material.WOOD || material == Material.WOOD_DOUBLE_STEP || material == Material.LOG ||
					material == Material.WOOD_STEP || material == Material.WOOD_STAIRS) {
				// Dont use 'oak' as switch case as 0 is the default data
				switch (blockData.toLowerCase()) {
				case "spruce":
					data = 1;
					break;
				case "birch":
					data = 2;
					break;
				case "jungle":
					data = 3;
					break;
				case "acacia":
					data = 4;
					break;
				case "darkoak":
					data = 5;
					break;
				default:
					break;
				}
			}
		}
	}
}
