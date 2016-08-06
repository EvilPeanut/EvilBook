package com.amentrix.evilbook.eviledit.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.amentrix.evilbook.eviledit.Session;
import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.utils.BlockReference;

public class BlockType {
	private Material material;
	private Byte data = 0;
	
	public BlockType(String blockName) {
		this.material = BlockReference.getBlockMaterial(blockName);
	}
	
	public Boolean isValid(Player player) {
		if (this.material == null) {
			player.sendMessage("§7Please enter a valid block name or ID");
			return false;
		} else if (Session.isBlocked(this.material)) {
			player.sendMessage("§cThis block is banned in EvilEdit");
			return false;
		}
		return true;
	}
	
	public Material getMaterial() {
		return this.material;
	}
	
	public Byte getData() {
		return this.data;
	}
	
	//TODO: EvilEdit: Update for 1.8
	public void setData(String blockData) {
		if (EvilBook.isByte(blockData)) {
			this.data = Byte.parseByte(blockData);
		} else {
			if (this.material == Material.WOOL || this.material == Material.CARPET || this.material == Material.STAINED_GLASS 
					|| this.material == Material.STAINED_GLASS_PANE || this.material == Material.STAINED_CLAY) {
				// Dont use 'white' as switch case as 0 is the default data
				switch (blockData.toLowerCase()) {
				case "orange":
					this.data = 1;
					break;
				case "magenta":
					this.data = 2;
					break;
				case "lightblue":
					this.data = 3;
					break;
				case "yellow":
					this.data = 4;
					break;
				case "lime":
					this.data = 5;
					break;
				case "pink":
					this.data = 6;
					break;
				case "gray":
					this.data = 7;
					break;
				case "grey":
					this.data = 7;
					break;
				case "lightgray":
					this.data = 8;
					break;
				case "lightgrey":
					this.data = 8;
					break;
				case "cyan":
					this.data = 9;
					break;
				case "purple":
					this.data = 10;
					break;
				case "blue":
					this.data = 11;
					break;
				case "brown":
					this.data = 12;
					break;
				case "green":
					this.data = 13;
					break;
				case "red":
					this.data = 14;
					break;
				case "black":
					this.data = 15;
					break;
				default:
					break;
				}
			} else if (this.material == Material.STEP || this.material == Material.DOUBLE_STEP) {
				// Dont use 'stone' as switch case as 0 is the default data
				switch (blockData.toLowerCase()) {
				case "sandstone":
					this.data = 1;
					break;
				case "wood":
					this.data = 2;
					break;
				case "wooden":
					this.data = 2;
					break;
				case "cobblestone":
					this.data = 3;
					break;
				case "cobble":
					this.data = 3;
					break;
				case "brick":
					this.data = 4;
					break;
				case "stonebrick":
					this.data = 5;
					break;
				case "netherbrick":
					this.data = 6;
					break;
				case "quartz":
					this.data = 7;
					break;
				default:
					break;
				}
			} else if (this.material == Material.WOOD || this.material == Material.WOOD_DOUBLE_STEP || this.material == Material.LOG ||
					this.material == Material.WOOD_STEP || this.material == Material.WOOD_STAIRS) {
				// Dont use 'oak' as switch case as 0 is the default data
				switch (blockData.toLowerCase()) {
				case "spruce":
					this.data = 1;
					break;
				case "birch":
					this.data = 2;
					break;
				case "jungle":
					this.data = 3;
					break;
				case "acacia":
					this.data = 4;
					break;
				case "darkoak":
					this.data = 5;
					break;
				default:
					break;
				}
			}
		}
	}
}
